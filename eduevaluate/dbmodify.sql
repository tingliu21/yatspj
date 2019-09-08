--修改时间：2019.8.30
--为县行政区添加特殊地区、重点帮扶县特征字段
ALTER TABLE sys_unit
  ADD COLUMN keynote boolean;
ALTER TABLE sys_unit
  ADD COLUMN develop character(1);
COMMENT ON COLUMN sys_unit.develop IS '0代表欠发达地区，1代表发展中地区，2代表发达地区';
COMMENT ON COLUMN sys_unit.keynote IS '重点帮扶县';

--修改时间：2019.9.7
--总分不排名，按等级来；监测值字段长度不够
ALTER TABLE evaluate_records
  ADD COLUMN level character(2);
COMMENT ON COLUMN evaluate_records.level IS '分等级';

ALTER TABLE evaluate_index
   ALTER COLUMN value TYPE numeric(12,4);
COMMENT ON COLUMN evaluate_index.value IS '评估值';

