create table conf_rpt_comp(
id  [bigint] NOT NULL IDENTITY(1, 1),
report_id bigint,
component_name VARCHAR(500) NOT NULL,
component_key VARCHAR(500) NOT NULL,
active VARCHAR(1) NOT NULL,
 CONSTRAINT [pk_rpt_comp_id] PRIMARY KEY ([id]),
CONSTRAINT conf_rpt_comp_report_id_fkey FOREIGN KEY(report_id)
REFERENCES conf_report(id));