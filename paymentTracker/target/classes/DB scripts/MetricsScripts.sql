 CREATE TABLE [conf_metric](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[display_name] [varchar](500) NOT NULL,
	[metrics_order] bigint NOT NULL,
	[report_id] bigint NOT NULL,
	[display] VARCHAR(1) NOT NULL,
	[ent_id] bigint NULL,
	CONSTRAINT [pk_metrics_id] PRIMARY KEY ([id]),
    CONSTRAINT conf_metric_report_id_fkey FOREIGN KEY(report_id) REFERENCES conf_report(id));