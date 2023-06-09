USE [Swift]
GO
/****** Object:  Table [dbo].[STXENTRYFIELDVIEW]    Script Date: 6/1/2023 12:55:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[STXENTRYFIELDVIEW](
	[idx] [float] NULL,
	[type_idx] [float] NULL,
	[field_cnt] [float] NULL,
	[code] [float] NULL,
	[code_id] [float] NULL,
	[loop_id] [float] NULL,
	[sequence_id] [float] NULL,
	[entry_option] [nvarchar](255) NULL,
	[entry_alternate] [nvarchar](255) NULL,
	[entry_alternate_choice] [nvarchar](255) NULL,
	[entry_id] [nvarchar](255) NULL,
	[tag] [nvarchar](255) NULL,
	[patt_id] [nvarchar](255) NULL,
	[expansion] [nvarchar](255) NULL,
	[is_optional] [float] NULL,
	[type] [float] NULL,
	[version_idx] [float] NULL
) ON [PRIMARY]
GO
