USE [Swift]
GO
/****** Object:  Table [dbo].[STXMESSAGE]    Script Date: 6/1/2023 12:55:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[STXMESSAGE](
	[idx] [float] NULL,
	[type] [float] NULL,
	[version_idx] [float] NULL,
	[description] [nvarchar](255) NULL
) ON [PRIMARY]
GO
