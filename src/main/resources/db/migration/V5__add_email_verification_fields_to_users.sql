-- Добавляем поля для подтверждения email
ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN verification_token VARCHAR(255);
ALTER TABLE users ADD COLUMN verification_token_expires_at TIMESTAMP;

-- Индекс для поиска по токену
CREATE INDEX idx_users_verification_token ON users(verification_token);