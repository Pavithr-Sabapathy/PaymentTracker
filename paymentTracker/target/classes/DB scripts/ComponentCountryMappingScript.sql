CREATE TABLE conf_comp_country
(
  [id] [bigint] NOT NULL IDENTITY(1, 1),
  rept_comp_id bigint NOT NULL,
  country character varying(200) NOT NULL,
  data_source_id bigint NOT NULL,
  CONSTRAINT conf_comp_country_pkey PRIMARY KEY (id ),
  CONSTRAINT conf_source_query_data_source_id_fkey FOREIGN KEY (data_source_id)
      REFERENCES conf_data_source (id) 
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT conf_source_query_rept_comp_id_fkey FOREIGN KEY (rept_comp_id)
      REFERENCES conf_rpt_comp
     )