-- Материалы
INSERT INTO materials (name, description) VALUES
                                              ('Золото 585', 'Желтое золото 585 пробы'),
                                              ('Серебро 925', 'Серебро 925 пробы'),
                                              ('Платина 950', 'Платина 950 пробы'),
                                              ('Бриллиант', 'Ограненный алмаз'),
                                              ('Изумруд', 'Натуральный изумруд'),
                                              ('Сапфир', 'Натуральный синий сапфир'),
                                              ('Рубин', 'Натуральный рубин'),
                                              ('Жемчуг', 'Культивированный жемчуг');

-- Пользователи (включая админа)
INSERT INTO users (username, email, password_hash, role, enabled, email_verified, created_at, updated_at) VALUES
                                                                                                              ('anna_ivanova', 'anna.ivanova@example.com', 'bcrypt_hash_stub_1', 'CLIENT', true, false, NOW(), NOW()),
                                                                                                              ('ivan_petrov', 'ivan.petrov@example.com', 'bcrypt_hash_stub_2', 'CLIENT', true, false, NOW(), NOW()),
                                                                                                              ('maria_sidorova', 'maria.sidorova@example.com', 'bcrypt_hash_stub_3', 'CLIENT', true, false, NOW(), NOW()),
                                                                                                              ('sergey_kuznetsov', 'sergey.kuznetsov@example.com', 'bcrypt_hash_stub_4', 'CLIENT', true, false, NOW(), NOW()),
                                                                                                              ('elena_smirnova', 'elena.smirnova@example.com', 'bcrypt_hash_stub_5', 'CLIENT', true, false, NOW(), NOW()),
                                                                                                              ('admin', 'admin@jewelry.local', '$2y$10$gjOv5biyeKwx9HzbaG6sSefp9E9bds5zZd3yYTgW7Zpgy6ezzSVUq', 'ADMIN', true, true, NOW(), NOW());

-- Клиенты
INSERT INTO clients (user_id, first_name, last_name, patronymic, phone, is_permanent) VALUES
                                                                                          (1, 'Анна', 'Иванова', 'Петровна', '+79991234567', true),
                                                                                          (2, 'Иван', 'Петров', 'Сергеевич', '+79992345678', false),
                                                                                          (3, 'Мария', 'Сидорова', 'Алексеевна', '+79993456789', true),
                                                                                          (4, 'Сергей', 'Кузнецов', NULL, '+79994567890', false),
                                                                                          (5, 'Елена', 'Смирнова', 'Владимировна', '+79995678901', true);

-- Изделия
INSERT INTO products (name, description, sku, weight, price, type, in_stock, is_available) VALUES
                                                                                               ('Золотое кольцо с бриллиантом', 'Элегантное кольцо из желтого золота с бриллиантом 0.5 карат', 'RING-GD-001', 5.250, 45000.00, 'КОЛЬЦО', 10, true),
                                                                                               ('Серебряные серьги', 'Серьги-пусеты из серебра 925 пробы', 'EARR-SV-001', 3.120, 8500.00, 'СЕРЬГИ', 25, true),
                                                                                               ('Платиновое обручальное кольцо', 'Классическое обручальное кольцо из платины', 'RING-PT-001', 4.800, 65000.00, 'КОЛЬЦО', 5, true),
                                                                                               ('Колье с сапфиром', 'Колье из белого золота с сапфиром в центре', 'NECK-GD-001', 12.500, 120000.00, 'КОЛЬЕ', 3, true),
                                                                                               ('Браслет из красного золота', 'Женский браслет из красного золота 585 пробы', 'BRAC-GD-001', 15.750, 78000.00, 'БРАСЛЕТ', 8, true);

-- Связь изделий с материалами
INSERT INTO product_materials (product_id, material_id, quantity) VALUES
                                                                      (1, 1, 5.000), (1, 4, 0.500),
                                                                      (2, 2, 3.000),
                                                                      (3, 3, 4.800),
                                                                      (4, 1, 10.000), (4, 6, 2.500),
                                                                      (5, 1, 15.000);

-- Заказы
INSERT INTO orders (client_id, status, total_amount, discount_amount, final_amount, notes) VALUES
                                                                                               (1, 'PENDING', 45000.00, 0.00, 45000.00, 'Первый тестовый заказ'),
                                                                                               (1, 'CONFIRMED', 77000.00, 2000.00, 75000.00, 'Заказ с двумя позициями и скидкой'),
                                                                                               (1, 'CANCELLED', 89000.00, 0.00, 89000.00, 'Отменённый заказ на цепочку');

-- Позиции заказов
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES
                                                                                      (1, 1, 1, 45000.00, 45000.00),
                                                                                      (2, 1, 1, 45000.00, 45000.00),
                                                                                      (2, 2, 1, 32000.00, 32000.00),
                                                                                      (3, 3, 1, 89000.00, 89000.00);