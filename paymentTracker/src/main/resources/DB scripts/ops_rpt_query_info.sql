 CREATE TABLE ops_rpt_query_info(
id [bigint] NOT NULL IDENTITY(1, 1), execution_id bigint NOT NULL, data_source_name character varying(255) NOT NULL, 
query_key character varying(55), executed_query text,query_execution_time bigint,failure_cause text,
start_time datetime2(6),end_time datetime2(6),data_found character(1) DEFAULT 'N',
CONSTRAINT ops_rpt_query_info_pkey PRIMARY KEY (id ),CONSTRAINT ops_rpt_query_info_execution_id_fkey FOREIGN KEY (execution_id)
REFERENCES ops_rpt_exec (id) ON UPDATE NO ACTION ON DELETE NO ACTION)