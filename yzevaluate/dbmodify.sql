ALTER TABLE evaluate_records
  ADD COLUMN locked boolean;
COMMENT ON COLUMN evaluate_records.locked IS '锁定状态';