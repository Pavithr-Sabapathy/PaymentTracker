CREATE TABLE ops_report_inst(

id [bigint] NOT NULL IDENTITY(1, 1),report_id bigint NOT NULL,user_id bigint NOT NULL,
report_name character varying(55) NOT NULL,report_description VARCHAR(255),
module_id bigint NOT NULL DEFAULT (0),role_id bigint NOT NULL DEFAULT (0),
create_date datetime2(6), CONSTRAINT ops_report_inst_pkey PRIMARY KEY (id ),
CONSTRAINT ops_report_inst_module_id_fkey FOREIGN KEY (module_id) REFERENCES conf_module (id)  ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT ops_report_inst_report_id_fkey FOREIGN KEY (report_id)  REFERENCES conf_report (id)    ON UPDATE NO ACTION ON DELETE NO ACTION,
--CONSTRAINT ops_report_inst_role_id_fkey FOREIGN KEY (role_id) REFERENCES conf_security_roles (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
--CONSTRAINT ops_report_inst_user_id_fkey FOREIGN KEY (user_id) REFERENCES conf_users (id) ON UPDATE NO ACTION ON DELETE NO ACTION
);