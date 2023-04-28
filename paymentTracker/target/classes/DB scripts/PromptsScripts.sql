CREATE TABLE [conf_prompt](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[pr_key] varchar(100) NOT NULL,
	[display_name] [varchar](500) NOT NULL,
	[pr_order] bigint NOT NULL,
	[pr_required] varchar(1) NOT NULL,
	[report_id] bigint NOT NULL,
	[ent_id] bigint NULL,
	CONSTRAINT [pk_prompt_id] PRIMARY KEY ([id]),
 constraint conf_prompt_report_id_fkey FOREIGN KEY(report_id) REFERENCES conf_report(id));