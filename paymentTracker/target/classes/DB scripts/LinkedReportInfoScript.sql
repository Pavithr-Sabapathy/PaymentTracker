
CREATE TABLE conf_linked_report_info ( 
 [id] [bigint] NOT NULL IDENTITY(1, 1),
 report_id bigint NOT NULL,
 linked_report_id bigint NOT NULL,
 source_metric_id bigint NOT NULL,
 active character varying(1) NOT NULL,
 CONSTRAINT conf_linked_report_info_pkey PRIMARY KEY (id ), 
 CONSTRAINT conf_linked_report_info_linked_report_id_fkey FOREIGN KEY (linked_report_id) REFERENCES conf_report (id)   ON UPDATE NO ACTION ON DELETE NO ACTION,
 CONSTRAINT conf_linked_report_info_report_id_fkey FOREIGN KEY (report_id) REFERENCES conf_report (id) ON UPDATE NO ACTION ON DELETE NO ACTION,
 CONSTRAINT conf_linked_report_info_source_metric_id_fkey FOREIGN KEY (source_metric_id) REFERENCES conf_metric (id) ON UPDATE NO ACTION ON DELETE NO ACTION);