CREATE TABLE ops_report_inst_prompt(
  id [bigint] NOT NULL IDENTITY(1, 1),report_id bigint NOT NULL,prompt_id bigint NOT NULL,
  prompt_key character varying(500) NOT NULL, prompt_value text DEFAULT NULL,
  entity_id bigint NOT NULL, report_inst_id bigint NOT NULL,CONSTRAINT ops_report_inst_prompt_pkey PRIMARY KEY (id ),
  CONSTRAINT ops_report_inst_prompt_entity_id_fkey FOREIGN KEY (entity_id) REFERENCES conf_entity (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ops_report_inst_prompt_prompt_id_fkey FOREIGN KEY (prompt_id) REFERENCES conf_prompt (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ops_report_inst_prompt_report_id_fkey FOREIGN KEY (report_id ) REFERENCES conf_report (id) ON UPDATE NO ACTION ON DELETE NO ACTION);