
DROP TABLE IF EXISTS djs.xxl_job_qrtz_simple_triggers;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_cron_triggers;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_simprop_triggers;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_blob_triggers;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_calendars;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_paused_trigger_grps;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_fired_triggers;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_scheduler_state;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_locks;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_triggers;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_job_details;

DROP TABLE IF EXISTS djs.xxl_job_qrtz_trigger_group;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_trigger_info;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_trigger_log;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_trigger_logglue;
DROP TABLE IF EXISTS djs.xxl_job_qrtz_trigger_registry;

-- ----------------------------
-- Table structure for xxl_job_qrtz_job_details
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_job_details (
  SCHED_NAME varchar(120) NOT NULL,
  JOB_NAME varchar(200) NOT NULL,
  JOB_GROUP varchar(200) NOT NULL,
  DESCRIPTION varchar(250) ,
  JOB_CLASS_NAME varchar(250) NOT NULL,
  IS_DURABLE BOOL NOT NULL,
  IS_NONCONCURRENT BOOL NOT NULL,
  IS_UPDATE_DATA BOOL NOT NULL,
  REQUESTS_RECOVERY BOOL NOT NULL,
  JOB_DATA bytea,
  PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);


-- ----------------------------
-- Table structure for xxl_job_qrtz_triggers
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_triggers (
  SCHED_NAME varchar(120) NOT NULL,
  TRIGGER_NAME varchar(200) NOT NULL,
  TRIGGER_GROUP varchar(200) NOT NULL,
  JOB_NAME varchar(200) NOT NULL,
  JOB_GROUP varchar(200) NOT NULL,
  DESCRIPTION varchar(250) ,
  NEXT_FIRE_TIME bigint ,
  PREV_FIRE_TIME bigint ,
  PRIORITY int ,
  TRIGGER_STATE varchar(16) NOT NULL,
  TRIGGER_TYPE varchar(8) NOT NULL,
  START_TIME bigint NOT NULL,
  END_TIME bigint NULL,
  CALENDAR_NAME varchar(200) NULL,
  MISFIRE_INSTR smallint NULL,
  JOB_DATA bytea NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP) 
  REFERENCES djs.xxl_job_qrtz_job_details(SCHED_NAME,JOB_NAME,JOB_GROUP)
);


-- ----------------------------
-- Table structure for xxl_job_qrtz_simple_triggers
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_simple_triggers (
  SCHED_NAME varchar(120) NOT NULL,
  TRIGGER_NAME varchar(200) NOT NULL,
  TRIGGER_GROUP varchar(200) NOT NULL,
  REPEAT_COUNT bigint NOT NULL,
  REPEAT_INTERVAL bigint NOT NULL,
  TIMES_TRIGGERED bigint NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
  REFERENCES djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);


-- ----------------------------
-- Table structure for xxl_job_qrtz_cron_triggers
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_cron_triggers (
  SCHED_NAME varchar(120) NOT NULL,
  TRIGGER_NAME varchar(200) NOT NULL,
  TRIGGER_GROUP varchar(200) NOT NULL,
  CRON_EXPRESSION varchar(200) NOT NULL,
  TIME_ZONE_ID varchar(80) ,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
  REFERENCES djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);



-- ----------------------------
-- Table structure for xxl_job_qrtz_simprop_triggers
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_simprop_triggers (
  SCHED_NAME varchar(120) NOT NULL,
  TRIGGER_NAME varchar(200) NOT NULL,
  TRIGGER_GROUP varchar(200) NOT NULL,
  STR_PROP_1 varchar(512) NULL,
  STR_PROP_2 varchar(512) NULL,
  STR_PROP_3 varchar(512) NULL,
  INT_PROP_1 int NULL,
  INT_PROP_2 int NULL,
  LONG_PROP_1 bigint NULL,
  LONG_PROP_2 bigint NULL,
  DEC_PROP_1 numeric(13,4) NULL,
  DEC_PROP_2 numeric(13,4) NULL,
  BOOL_PROP_1 BOOL NULL,
  BOOL_PROP_2 BOOL NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
  REFERENCES djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);



CREATE TABLE djs.xxl_job_qrtz_blob_triggers (
  SCHED_NAME varchar(120) NOT NULL,
  TRIGGER_NAME varchar(200) NOT NULL,
  TRIGGER_GROUP varchar(200) NOT NULL,
  BLOB_DATA bytea NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
  REFERENCES djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

-- ----------------------------
-- Table structure for xxl_job_qrtz_calendars
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_calendars (
  SCHED_NAME varchar(120) NOT NULL,
  CALENDAR_NAME varchar(200) NOT NULL,
  CALENDAR bytea NOT NULL,
  PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);


-- ----------------------------
-- Table structure for xxl_job_qrtz_paused_trigger_grps
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_paused_trigger_grps (
  SCHED_NAME varchar(120) NOT NULL,
  TRIGGER_GROUP varchar(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

-- ----------------------------
-- Table structure for xxl_job_qrtz_fired_triggers
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_fired_triggers (
  SCHED_NAME varchar(120) NOT NULL,
  ENTRY_ID varchar(95) NOT NULL,
  TRIGGER_NAME varchar(200) NOT NULL,
  TRIGGER_GROUP varchar(200) NOT NULL,
  INSTANCE_NAME varchar(200) NOT NULL,
  FIRED_TIME bigint NOT NULL,
  SCHED_TIME bigint NOT NULL,
  PRIORITY int NOT NULL,
  STATE varchar(16) NOT NULL,
  JOB_NAME varchar(200) NULL,
  JOB_GROUP varchar(200) NULL,
  IS_NONCONCURRENT BOOL NULL,
  REQUESTS_RECOVERY BOOL NULL,
  PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);



-- ----------------------------
-- Table structure for xxl_job_qrtz_scheduler_state
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_scheduler_state (
  SCHED_NAME varchar(120) NOT NULL,
  INSTANCE_NAME varchar(200) NOT NULL,
  LAST_CHECKIN_TIME bigint NOT NULL,
  CHECKIN_INTERVAL bigint NOT NULL,
  PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);


-- ----------------------------
-- Table structure for xxl_job_qrtz_locks
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_locks (
  SCHED_NAME varchar(120) NOT NULL,
  LOCK_NAME varchar(40) NOT NULL,
  PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);




-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_group
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_trigger_group (
  id serial NOT NULL,
  app_name varchar(64) NOT NULL,
  title varchar(128),
  "order" smallint NOT NULL DEFAULT '0',
  address_type smallint NOT NULL DEFAULT '0',
  address_list varchar(512),
  old_address_list varchar(512),
  PRIMARY KEY (id)
);
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_group.app_name IS '执行器AppName';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_group.title IS '执行器名称';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_group."order" IS '排序';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_group.address_type IS '执行器地址类型：0=自动注册、1=手动录入';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_group.address_list IS '执行器地址列表，多地址逗号分隔';

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_info
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_trigger_info (
  id serial NOT NULL,
  job_group int NOT NULL,
  job_cron varchar(128) NOT NULL,
  job_desc varchar(255) NOT NULL,
  add_time timestamp ,
  update_time timestamp ,
  author varchar(64),
  alarm_email varchar(255),
  executor_route_strategy varchar(50),
  executor_handler varchar(255),
  executor_param varchar(512),
  executor_block_strategy varchar(50),
  executor_fail_strategy varchar(50), 
  glue_type varchar(50) NOT NULL,
  glue_source text,
  glue_remark varchar(128),
  glue_updatetime timestamp,
  child_jobid varchar(255),
  execute_timeout int NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.job_group IS '执行器主键ID';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.job_cron IS '任务执行CRON';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.author IS '作者';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.alarm_email IS '报警邮件';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.executor_route_strategy IS '执行器路由策略';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.executor_param IS '执行器任务参数';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.executor_block_strategy IS '阻塞处理策略';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.executor_fail_strategy IS '失败处理策略';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.execute_timeout IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.glue_type IS 'GLUE类型';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.glue_remark IS 'GLUE备注';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.glue_updatetime IS 'GLUE更新时间';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_info.child_jobid IS '子任务ID，多个逗号分隔';

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_log
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_trigger_log (
  id serial NOT NULL,
  job_group int NOT NULL,
  job_id int NOT NULL,
  glue_type varchar(50),
  executor_address varchar(255),
  executor_handler varchar(255),
  executor_param varchar(512),
  trigger_time timestamp,
  trigger_code int NOT NULL,
  trigger_msg varchar(2048),
  handle_time timestamp,
  handle_code int NOT NULL,
  handle_msg varchar(2048),
  PRIMARY KEY (id)
);


COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.job_group IS '执行器主键ID';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.job_id IS '任务，主键ID';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.glue_type IS 'GLUE类型';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.executor_address IS '执行器地址，本次执行的地址';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.executor_handler IS '执行器任务handler';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.executor_param IS '执行器任务参数';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.trigger_time IS '调度-时间';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.trigger_code IS '调度-结果';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.trigger_msg IS '调度-日志';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.handle_time IS '执行-时间';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.handle_code IS '执行-状态';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_log.handle_msg IS '执行-日志';

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_logglue
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_trigger_logglue (
  id serial NOT NULL,
  job_id int NOT NULL,
  glue_type varchar(50),
  glue_source text,
  glue_remark varchar(128) NOT NULL,
  add_time timestamp NULL ,
  update_time timestamp NULL,
  PRIMARY KEY (id)
);
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_logglue.job_id IS '任务，主键ID';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_logglue.glue_type IS 'GLUE类型';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_logglue.glue_source IS 'GLUE源代码';
COMMENT ON COLUMN djs.xxl_job_qrtz_trigger_logglue.glue_remark IS 'GLUE备注';

-- ----------------------------
-- Table structure for xxl_job_qrtz_trigger_registry
-- ----------------------------
CREATE TABLE djs.xxl_job_qrtz_trigger_registry (
  id serial NOT NULL,
  registry_group varchar(255) NOT NULL,
  registry_key varchar(255) NOT NULL,
  registry_value varchar(255) NOT NULL,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);


create index idx_qrtz_j_req_recovery on djs.xxl_job_qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on djs.xxl_job_qrtz_job_details(SCHED_NAME,JOB_GROUP);

create index idx_qrtz_t_j on djs.xxl_job_qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_t_jg on djs.xxl_job_qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_c on djs.xxl_job_qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qrtz_t_g on djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_qrtz_t_state on djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qrtz_t_n_state on djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_next_fire_time on djs.xxl_job_qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st on djs.xxl_job_qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on djs.xxl_job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on djs.xxl_job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on djs.xxl_job_qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index idx_qrtz_ft_trig_inst_name on djs.xxl_job_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on djs.xxl_job_qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on djs.xxl_job_qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_ft_jg on djs.xxl_job_qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_ft_t_g on djs.xxl_job_qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_tg on djs.xxl_job_qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);

create index I_trigger_time ON djs.xxl_job_qrtz_trigger_log (trigger_time);


--修改表中主键自增长开始值从100开始
ALTER SEQUENCE "djs"."xxl_job_qrtz_trigger_group_id_seq" RESTART WITH 100;

--修改表中主键自增长开始值从100开始
ALTER SEQUENCE "djs"."xxl_job_qrtz_trigger_info_id_seq" RESTART WITH 100;
