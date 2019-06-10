2018-12-25
1、专家指标考核时按照初始化评估时指定的专家顺序，专家1只考评专家1负责的指标，联络员可考评所有专家指标

2019-01-06
1、专家身份未定，由评估现场商量指定，因此，同一组专家可以考评所有专家指标
2、添加学校评估锁定功能


=========================================
以下为鄞州幼儿园系统添加
2019-6-10
--monitor_catalog表修改
COMMENT ON COLUMN monitor_catalog.qualify
  IS '是否达标性指标(弃用)';
COMMENT ON COLUMN monitor_catalog.type
  IS '指标类型，用来区分1级指标的具体哪一个(弃用)';
ALTER TABLE monitor_catalog
  ADD COLUMN level integer;
COMMENT ON COLUMN monitor_catalog.qualify IS '是否达标性指标(弃用)';
COMMENT ON COLUMN monitor_catalog.type IS '指标类型，用来区分1级指标的具体哪一个(弃用)';
COMMENT ON COLUMN monitor_catalog.level IS '指标等级(新增)';

