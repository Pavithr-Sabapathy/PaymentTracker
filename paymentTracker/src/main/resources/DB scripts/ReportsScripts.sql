create table conf_report
(
   id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,
   report_name VARCHAR2 (500)
   not null,
   display_name VARCHAR2 (500) not null,
   rep_description VARCHAR2 (500),
   category1 VARCHAR2 (100) not null,
   active VARCHAR2 (1) not null,
   valid VARCHAR2 (1) not null,
   connector_key varchar (500),
   module_id NUMBER (10) not null,
   CONSTRAINT conf_report_module_id_fkey FOREIGN KEY (module_id) REFERENCES conf_module (id),
   CONSTRAINT pk_report_id PRIMARY KEY (id)
);