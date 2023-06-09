create table conf_entity(
id [bigint] NOT NULL IDENTITY(1, 1),
ent_name varchar(300) NOT NULL,
source_format varchar(300) NOT NULL,
dsiplay_format varchar(300) NOT NULL,
type varchar(10) NOT NULL,
CONSTRAINT entity_id PRIMARY KEY (id )
);