CREATE DATABASE PaymentTracker;

CREATE TABLE [conf_data_source] (
  [id] [bigint] NOT NULL IDENTITY(1, 1),
	[ds_name] [varchar] (500) NOT NULL,
	[ds_description] [varchar](2000) NULL,
	[ds_provider] [bigint]  NOT NULL,
	[ds_username] [varchar](100) NOT NULL,
	[ds_password] [nvarchar](255) NOT NULL,
	[password_encrypted] [nvarchar](255) NOT NULL,
	[server_ip] [nvarchar](200) NOT NULL,
	[ds_port] [bigint] NOT NULL,
	[ds_schema_name] [nvarchar](250) NOT NULL,
	[ds_owner] [nvarchar](250) NOT NULL,
	[active] [varchar](1) NOT NULL,
    CONSTRAINT [pk_ds_id] PRIMARY KEY ([id])
);


Alter table conf_data_source add  ds_country varchar(255) null;