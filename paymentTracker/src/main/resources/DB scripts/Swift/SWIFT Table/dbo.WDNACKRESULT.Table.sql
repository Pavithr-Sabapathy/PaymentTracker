USE [Swift]
GO
/****** Object:  Table [dbo].[WDNACKRESULT]    Script Date: 6/1/2023 12:55:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[WDNACKRESULT](
	[sysid] [varchar](255) NULL,
	[aid] [varchar](255) NULL,
	[appe_s_umidl] [varchar](255) NULL,
	[appe_s_umidh] [varchar](255) NULL,
	[appe_inst_num] [varchar](255) NULL,
	[appe_date_time] [datetime] NULL,
	[appe_seq_nbr] [varchar](255) NULL,
	[insert_time] [datetime] NULL,
	[X_CREA_DATE_TIME_MESG] [varchar](255) NULL
) ON [PRIMARY]
GO
