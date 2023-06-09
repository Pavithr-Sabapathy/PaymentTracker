CREATE TABLE ops_rpt_exec(

id  [bigint] NOT NULL IDENTITY(1, 1), report_id bigint NOT NULL,report_inst_id bigint NOT NULL, module_id bigint NOT NULL DEFAULT (0),user_id bigint NOT NULL, user_name VARCHAR(500) NOT NULL,
role_id bigint NOT NULL,role_name VARCHAR(500) NOT NULL,execution_status VARCHAR(2) DEFAULT 'N',start_date datetime2(6),end_date datetime2(6),
failure_cause text,link_execution VARCHAR(1) DEFAULT 'N',execution_time bigint,CONSTRAINT ops_rpt_exec_pkey PRIMARY KEY (id ),
CONSTRAINT ops_rpt_exec_module_id_fkey FOREIGN KEY (module_id) REFERENCES conf_module (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT ops_rpt_exec_report_id_fkey FOREIGN KEY (report_id) REFERENCES conf_report (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
--CONSTRAINT ops_rpt_exec_role_id_fkey FOREIGN KEY (role_id) REFERENCES conf_security_roles (id)  ON UPDATE NO ACTION ON DELETE NO ACTION,
--CONSTRAINT ops_rpt_exec_user_id_fkey FOREIGN KEY (user_id) REFERENCES conf_users (id)  ON UPDATE NO ACTION ON DELETE NO ACTION
);