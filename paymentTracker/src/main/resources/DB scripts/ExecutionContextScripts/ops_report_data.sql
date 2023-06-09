
CREATE TABLE ops_report_data(

  id [bigint] NOT NULL IDENTITY(1, 1),rpt_exec_id bigint NOT NULL,report_data text,
  CONSTRAINT pk_rd_id PRIMARY KEY (id ),  CONSTRAINT ops_report_data_rpt_exec_id_fkey FOREIGN KEY (rpt_exec_id)
	      REFERENCES ops_rpt_exec (id) ON UPDATE NO ACTION ON DELETE NO ACTION);
