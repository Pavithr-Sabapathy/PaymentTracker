USE [Swift]
GO
/****** Object:  Table [dbo].[RCORR]    Script Date: 6/1/2023 12:55:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RCORR](
	[corr_type] [nvarchar](255) NULL,
	[corr_X1] [nvarchar](255) NULL,
	[corr_X2] [nvarchar](255) NULL,
	[corr_X3] [nvarchar](255) NULL,
	[corr_X4] [nvarchar](255) NULL,
	[corr_nature] [nvarchar](255) NULL,
	[corr_BIC_can_be_updated] [float] NULL,
	[corr_inheritance] [float] NULL,
	[corr_language] [nvarchar](255) NULL,
	[corr_information] [nvarchar](255) NULL,
	[corr_institution_name] [nvarchar](255) NULL,
	[corr_branch_info] [nvarchar](255) NULL,
	[corr_location] [nvarchar](255) NULL,
	[corr_city_name] [nvarchar](255) NULL,
	[corr_physical_address] [nvarchar](255) NULL,
	[corr_ctry_code] [nvarchar](255) NULL,
	[corr_ctry_name] [nvarchar](255) NULL,
	[corr_subtype] [nvarchar](255) NULL,
	[corr_pob_number] [nvarchar](255) NULL,
	[corr_pob_location] [nvarchar](255) NULL,
	[corr_pob_ctry_code] [nvarchar](255) NULL,
	[corr_pob_ctry_name] [nvarchar](255) NULL,
	[corr_status] [nvarchar](255) NULL,
	[corr_crea_oper_nickname] [nvarchar](255) NULL,
	[corr_crea_date_time] [datetime] NULL,
	[corr_mod_oper_nickname] [nvarchar](255) NULL,
	[corr_mod_date_time] [datetime] NULL,
	[corr_token] [float] NULL,
	[corr_data_last] [nvarchar](255) NULL
) ON [PRIMARY]
GO
