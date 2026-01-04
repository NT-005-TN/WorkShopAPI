-- Функция для обновления поля updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Функция для логирования изменений в audit_log
CREATE OR REPLACE FUNCTION log_audit_event()
RETURNS TRIGGER AS $$
DECLARE
action_type VARCHAR(20);
    old_data JSON;
    new_data JSON;
BEGIN
    IF TG_OP = 'INSERT' THEN
        action_type := 'CREATE';
        old_data := NULL;
        new_data := row_to_json(NEW);
    ELSIF TG_OP = 'UPDATE' THEN
        action_type := 'UPDATE';
        old_data := row_to_json(OLD);
        new_data := row_to_json(NEW);
    ELSIF TG_OP = 'DELETE' THEN
        action_type := 'DELETE';
        old_data := row_to_json(OLD);
        new_data := NULL;
END IF;

INSERT INTO audit_log (user_id, action, table_name, record_id, old_values, new_values)
VALUES (
           CASE
               WHEN TG_OP = 'DELETE' THEN OLD.user_id
               ELSE NEW.user_id
               END,
           action_type,
           TG_TABLE_NAME,
           CASE
               WHEN TG_OP = 'DELETE' THEN OLD.id
               ELSE NEW.id
               END,
           old_data,
           new_data
       );

RETURN CASE WHEN TG_OP = 'DELETE' THEN OLD ELSE NEW END;
END;
$$ language 'plpgsql';

-- Функция для проверки email
CREATE OR REPLACE FUNCTION validate_email_format()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.email !~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        RAISE EXCEPTION 'Некорректный формат email: %', NEW.email;
END IF;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Функция для автоматического обновления stock при создании заказа
CREATE OR REPLACE FUNCTION update_product_stock()
RETURNS TRIGGER AS $$
BEGIN
    -- Уменьшаем количество товара на складе
UPDATE products
SET in_stock = in_stock - NEW.quantity,
    updated_at = CURRENT_TIMESTAMP
WHERE id = NEW.product_id;

-- Проверяем, не стало ли количество отрицательным
IF (SELECT in_stock FROM products WHERE id = NEW.product_id) < 0 THEN
        RAISE EXCEPTION 'Недостаточно товара на складе для продукта ID: %', NEW.product_id;
END IF;

RETURN NEW;
END;
$$ language 'plpgsql';

-- Функция для возврата товара на склад при удалении позиции заказа
CREATE OR REPLACE FUNCTION restore_product_stock()
RETURNS TRIGGER AS $$
BEGIN
    -- Возвращаем товар на склад
UPDATE products
SET in_stock = in_stock + OLD.quantity,
    updated_at = CURRENT_TIMESTAMP
WHERE id = OLD.product_id;

RETURN OLD;
END;
$$ language 'plpgsql';

-- Функция для расчета итоговой суммы заказа
CREATE OR REPLACE FUNCTION calculate_order_total()
RETURNS TRIGGER AS $$
DECLARE
order_total DECIMAL(12,2);
    order_discount DECIMAL(12,2);
BEGIN
    -- Рассчитываем общую сумму заказа на основе позиций
SELECT COALESCE(SUM(total_price), 0)
INTO order_total
FROM order_items
WHERE order_id = NEW.id;

-- Получаем скидку (если есть)
SELECT COALESCE(discount_amount, 0)
INTO order_discount
FROM orders
WHERE id = NEW.id;

-- Обновляем сумму заказа
NEW.total_amount := order_total;
    NEW.final_amount := order_total - order_discount;

RETURN NEW;
END;
$$ language 'plpgsql';

-- Функция для проверки ролей пользователей
CREATE OR REPLACE FUNCTION check_user_role_consistency()
RETURNS TRIGGER AS $$
BEGIN
    -- Если пользователь добавлен в clients, проверяем что его роль CLIENT
    IF EXISTS (SELECT 1 FROM clients WHERE user_id = NEW.user_id) AND NEW.role != 'CLIENT' THEN
        RAISE EXCEPTION 'Пользователь с ID % находится в таблице clients, но его роль не CLIENT', NEW.user_id;
END IF;

    -- Если пользователь добавлен в employees, проверяем что его роль SELLER или ADMIN
    IF EXISTS (SELECT 1 FROM employees WHERE user_id = NEW.user_id)
       AND NEW.role NOT IN ('SELLER', 'ADMIN') THEN
        RAISE EXCEPTION 'Пользователь с ID % находится в таблице employees, но его роль не SELLER или ADMIN', NEW.user_id;
END IF;

RETURN NEW;
END;
$$ language 'plpgsql';

-- ТРИГГЕРЫ для таблицы users (более 5 полей → 2 триггера)
CREATE TRIGGER trg_users_update_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_users_validate_email
    BEFORE INSERT OR UPDATE ON users
                         FOR EACH ROW
                         EXECUTE FUNCTION validate_email_format();

-- ТРИГГЕРЫ для таблицы clients (более 5 полей → 2 триггера)
CREATE TRIGGER trg_clients_update_updated_at
    BEFORE UPDATE ON clients
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_clients_audit
    AFTER INSERT OR UPDATE OR DELETE ON clients
    FOR EACH ROW
    EXECUTE FUNCTION log_audit_event();

-- ТРИГГЕРЫ для таблицы employees (более 5 полей → 2 триггера)
CREATE TRIGGER trg_employees_update_updated_at
    BEFORE UPDATE ON employees
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_employees_audit
    AFTER INSERT OR UPDATE OR DELETE ON employees
    FOR EACH ROW
    EXECUTE FUNCTION log_audit_event();

-- ТРИГГЕРЫ для таблицы products (более 5 полей → 2 триггера)
CREATE TRIGGER trg_products_update_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_products_check_availability
    BEFORE INSERT OR UPDATE ON products
                         FOR EACH ROW
                         EXECUTE FUNCTION check_product_availability();

-- Дополнительная функция для продуктов
CREATE OR REPLACE FUNCTION check_product_availability()
RETURNS TRIGGER AS $$
BEGIN
    -- Автоматически устанавливаем is_available на основе количества на складе
    NEW.is_available := (NEW.in_stock > 0);

    -- Проверяем, что цена не отрицательная
    IF NEW.price < 0 THEN
        RAISE EXCEPTION 'Цена не может быть отрицательной';
END IF;

    -- Проверяем, что вес положительный
    IF NEW.weight <= 0 THEN
        RAISE EXCEPTION 'Вес должен быть больше 0';
END IF;

RETURN NEW;
END;
$$ language 'plpgsql';

-- ТРИГГЕРЫ для таблицы orders (более 5 полей → 2 триггера)
CREATE TRIGGER trg_orders_update_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_orders_calculate_total
    BEFORE INSERT OR UPDATE ON orders
                         FOR EACH ROW
                         EXECUTE FUNCTION calculate_order_total();

-- ТРИГГЕРЫ для таблицы order_items (более 5 полей → 2 триггера)
CREATE TRIGGER trg_order_items_update_stock
    AFTER INSERT ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_product_stock();

CREATE TRIGGER trg_order_items_restore_stock
    AFTER DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION restore_product_stock();

-- Триггер для автоматической проверки ролей
CREATE TRIGGER trg_users_check_role_consistency
    AFTER INSERT OR UPDATE ON users
                        FOR EACH ROW
                        EXECUTE FUNCTION check_user_role_consistency();

-- Триггер для материалов
CREATE TRIGGER trg_materials_update_updated_at
    BEFORE UPDATE ON materials
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Триггер для product_materials
CREATE TRIGGER trg_product_materials_update_updated_at
    BEFORE UPDATE ON product_materials
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();