CREATE TABLE conf_users

(  id [bigint] NOT NULL IDENTITY(1, 1),username character varying(100) NOT NULL, password character varying(255) NOT NULL,
   first_name character varying(55) NOT NULL, last_name character varying(55) NOT NULL,
  full_name character varying(255) DEFAULT NULL, email_id character varying(255) DEFAULT NULL,
  status character varying(5) NOT NULL DEFAULT 'D', last_login_date datetime2(6),
 date_created datetime2(6), date_modified datetime2(6),
  read_only character(1) DEFAULT 'N',CONSTRAINT pk_u_id PRIMARY KEY (id ),CONSTRAINT uk_un UNIQUE (username ))