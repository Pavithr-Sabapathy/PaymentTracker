USE [Swift]
GO
/****** Object:  Table [dbo].[RINTV]    Script Date: 6/1/2023 12:55:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RINTV](
	[aid] [float] NULL,
	[intv_s_umidl] [float] NULL,
	[intv_s_umidh] [float] NULL,
	[intv_inst_num] [float] NULL,
	[intv_date_time] [datetime] NULL,
	[intv_seq_nbr] [float] NULL,
	[intv_inty_num] [float] NULL,
	[intv_inty_name] [nvarchar](255) NULL,
	[intv_inty_category] [nvarchar](255) NULL,
	[intv_oper_nickname] [nvarchar](255) NULL,
	[intv_appl_serv_name] [nvarchar](255) NULL,
	[intv_mpfn_name] [nvarchar](255) NULL,
	[intv_appe_date_time] [datetime] NULL,
	[intv_appe_seq_nbr] [float] NULL,
	[intv_length] [float] NULL,
	[intv_token] [float] NULL,
	[intv_text] [nvarchar](255) NULL,
	[intv_merged_text] [nvarchar](255) NULL,
	[INTV_SIGNATURE_WEIGHT] [nvarchar](255) NULL,
	[X_DS] [nvarchar](255) NULL,
	[X_CREA_DATE_TIME_MESG] [datetime] NULL
) ON [PRIMARY]
GO
