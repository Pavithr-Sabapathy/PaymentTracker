create table conf_report(
  [id] [bigint] NOT NULL IDENTITY(1, 1),
  [report_name] VARCHAR(500) NOT NULL,
  [display_name] VARCHAR(500) NOT NULL,
  [rep_description] VARCHAR(500),
  [category] VARCHAR(100) NOT NULL,
  [active] VARCHAR(1) NOT NULL,
  [valid] VARCHAR(1) NOT NULL,
  [module_id] [bigint] NOT NULL,
CONSTRAINT conf_report_module_id_fkey FOREIGN KEY (module_id)
REFERENCES conf_module(id),
  CONSTRAINT [pk_report_id] PRIMARY KEY ([id]));