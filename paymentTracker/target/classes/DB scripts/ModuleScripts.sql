  create table conf_module(
  [id] [bigint] NOT NULL IDENTITY(1, 1),
	mod_name VARCHAR(200) not null,
	display_name VARCHAR(500) not null,
	mod_description VARCHAR(1000) not null,
	active VARCHAR(1) not null,
	valid VARCHAR(1) null
	 CONSTRAINT [pk_conf_module_id] PRIMARY KEY ([id])
	)