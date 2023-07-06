CREATE TABLE conf_rpt_comp_det
(
   [id] [bigint] IDENTITY
   (
      1,
      1
   )
   NOT NULL,
   [query] [nvarchar] (2000) NOT NULL,
   [query_key] [nvarchar] (500) NOT NULL,
   report_comp_id bigint NOT NULL,
   CONSTRAINT pk_conf_rpt_com_det_id PRIMARY KEY (id),
   CONSTRAINT fk_report_comp_id FOREIGN KEY (report_comp_id) REFERENCES conf_rpt_comp (id) ON UPDATE NO ACTION ON DELETE NO ACTION
);