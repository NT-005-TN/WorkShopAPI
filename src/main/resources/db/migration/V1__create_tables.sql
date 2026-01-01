-- Таблица клиентов (аналог Client из MySQL)
CREATE TABLE IF NOT EXISTS clients (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    patronymic VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    is_permanent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE clients IS 'Клиенты ювелирной мастерской';

-- Таблица материалов (аналог Material из MySQL)
CREATE TABLE IF NOT EXISTS materials (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE materials IS 'Материалы для изготовления ювелирных изделий';

-- Таблица изделий (аналог Product из MySQL)
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    weight DECIMAL(8,3) NOT NULL CHECK (weight > 0),
    price DECIMAL(12,2) NOT NULL CHECK (price >= 0),
    type VARCHAR(20) NOT NULL,
    in_stock INTEGER DEFAULT 0 CHECK (in_stock >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE products IS 'Ювелирные изделия';

-- Таблица связи материалов и изделий (аналог MaterialProduct из MySQL)
CREATE TABLE IF NOT EXISTS product_materials (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    material_id INTEGER NOT NULL REFERENCES materials(id) ON DELETE CASCADE,
    quantity DECIMAL(8,3) NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, material_id)
);

COMMENT ON TABLE product_materials IS 'Связь изделий с материалами';

-- Таблица заказов (аналог Order из MySQL)
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL REFERENCES clients(id),
    status VARCHAR(20) DEFAULT 'PENDING',
    total_amount DECIMAL(12,2) CHECK (total_amount >= 0),
    discount_amount DECIMAL(12,2) DEFAULT 0 CHECK (discount_amount >= 0),
    final_amount DECIMAL(12,2) CHECK (final_amount >= 0),
    notes TEXT,
    order_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (final_amount = total_amount - discount_amount)
);

COMMENT ON TABLE orders IS 'Заказы клиентов';

-- Таблица позиций заказа
CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(12,2) NOT NULL CHECK (total_price >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (total_price = unit_price * quantity)
);

COMMENT ON TABLE order_items IS 'Позиции в заказе';