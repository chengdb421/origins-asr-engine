--
-- 初始化SQL脚本
-- 
create table if not EXISTS asr_task(
  id integer primary key auto_increment,
  uuid varchar(64) not null,
  created_at datetime not null,
  state varchar(32),
  result_text longtext,
  error_message longtext,
  request_id varchar(255),
  raw_media_url longtext not null,
  temporary_media_url longtext,
  running_at datetime,
  completed_at datetime,
  sequence int not null default 0,
  model_name varchar(255) not null
);