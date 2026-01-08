-- Изменяем тип record_id с INTEGER на BIGINT
ALTER TABLE audit_log
ALTER COLUMN record_id TYPE BIGINT USING record_id::BIGINT;