CREATE TABLE conf_security_roles
(id [bigint] NOT NULL IDENTITY(1, 1),name character varying(50) NOT NULL,
  description character varying(100),status character(1) NOT NULL DEFAULT 'D',
  date_created datetime2(6),date_modified datetime2(6),internal character(1) DEFAULT 'N'
  ,read_only character(1) DEFAULT 'N',CONSTRAINT pk_sr_id PRIMARY KEY (id ))