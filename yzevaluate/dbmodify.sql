﻿ALTER TABLE evaluate_records
  ADD COLUMN locked boolean;
COMMENT ON COLUMN evaluate_records.locked IS '锁定状态';

--附件是与指标评价ID关联的，并非与模型中的指标ID关联
ALTER TABLE evaluate_appendix
  ADD COLUMN remarkid character varying(32);
COMMENT ON COLUMN evaluate_appendix.remarkid IS '指标评价ID(remark和custom表的id)
';
--学校自评和发展规划附件上传
ALTER TABLE evaluate_records
  ADD COLUMN selfevaurl character varying(100);
ALTER TABLE evaluate_records
  ADD COLUMN planurl character varying(100);
COMMENT ON COLUMN evaluate_records.selfevaurl IS '自评报告路径';
COMMENT ON COLUMN evaluate_records.planurl IS '3年发展规划路径';

--评估记录的权重改为100分
UPDATE evaluate_records SET weights=100

--发展性指标表加自评理由字段
ALTER TABLE evaluate_custom
  ADD COLUMN remark_s character varying(200);
COMMENT ON COLUMN evaluate_custom.remark_s IS '自评理由';

--发展性指标表加督评理由字段
ALTER TABLE evaluate_custom
  ADD COLUMN remark_p character varying(200);
COMMENT ON COLUMN evaluate_custom.remark_p IS '督评理由';
--自评理由和督评理由字段改长
ALTER TABLE evaluate_remark
   ALTER COLUMN remark_s TYPE character varying(200);
ALTER TABLE evaluate_remark
   ALTER COLUMN remark_p TYPE character varying(200);
--发展性指标权重改为小数点1位
ALTER TABLE evaluate_custom
   ALTER COLUMN weights TYPE numeric(3,1);
COMMENT ON COLUMN evaluate_custom.weights IS '分值';


-- View: monitor_index_view

-- DROP VIEW monitor_index_view;

CREATE OR REPLACE VIEW monitor_index_view AS
 SELECT monitor_index.location,
    monitor_index.name,
    monitor_index.id,
    monitor_index.weights,
    monitor_index.catalogid,
    monitor_index.unittype,
    monitor_index.detail,
    monitor_index.department,
    sys_unit.name AS deptname,
    monitor_index.selfeva,
    monitor_index.masterrole,
    sys_role.name AS masterrolename
   FROM monitor_index
     JOIN sys_unit ON monitor_index.department::text = sys_unit.id::text
     LEFT JOIN sys_role ON monitor_index.masterrole::text = sys_role.id::text
  ORDER BY monitor_index.location;

ALTER TABLE monitor_index_view
  OWNER TO postgres;


