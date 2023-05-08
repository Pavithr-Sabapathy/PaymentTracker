CREATE TABLE conf_linked_report_info_det (
 [id] [bigint] NOT NULL IDENTITY(1, 1),
 link_rep_id bigint NOT NULL,
 link_rpt_prompt_id bigint NOT NULL,
 mapped_id bigint NOT NULL,

 mapping_type character varying(1) NOT NULL,
 CONSTRAINT conf_linked_report_info_det_pkey PRIMARY KEY (id ), 
 CONSTRAINT conf_linked_report_info_det_link_rep_id_fkey FOREIGN KEY (link_rep_id) REFERENCES conf_linked_report_info (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
 CONSTRAINT conf_linked_report_info_det_link_rpt_prompt_id_fkey FOREIGN KEY (link_rpt_prompt_id) REFERENCES conf_prompt (id) ON UPDATE NO ACTION ON DELETE NO ACTION);

