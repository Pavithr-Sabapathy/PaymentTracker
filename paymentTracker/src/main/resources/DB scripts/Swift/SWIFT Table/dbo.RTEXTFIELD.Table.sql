USE [Swift]
GO
/****** Object:  Table [dbo].[RTEXTFIELD]    Script Date: 6/1/2023 12:55:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RTEXTFIELD](
	[aid] [float] NULL,
	[text_s_umidl] [float] NULL,
	[text_s_umidh] [float] NULL,
	[field_cnt] [float] NULL,
	[field_code] [float] NULL,
	[field_code_id] [float] NULL,
	[field_option] [nvarchar](255) NULL,
	[value] [nvarchar](max) NULL,
	[value_memo] [nvarchar](255) NULL,
	[sequence_id] [float] NULL,
	[group_idx] [float] NULL,
	[X_CREA_DATE_TIME_MESG] [datetime] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
