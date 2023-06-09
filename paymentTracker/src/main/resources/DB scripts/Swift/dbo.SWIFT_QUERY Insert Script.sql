USE [Swift]
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rmesg', N'"select dateadd(second, -47, M.mesg_crea_date_time) as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''), ''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))
union all
select top 20 M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_REL_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''),''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))"

', N'RMESG  ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rtextfield', N'"select field_code, replace(value, ''//ACCC'', ''//ACSC'') value from dbo.rTextField with (nolock) where aid = ~aid~
and text_s_umidl = ~s_umidl~
and text_s_umidh = ~s_umidh~
and field_code in(50,59,79)"


', N'rTextField ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rintv', N'"select i.intv_inty_name, i.intv_mpfn_name, i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) inner join dbo.rAppe a with (nolock) 
on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidl = a.appe_s_umidl and i.intv_inst_num = a.appe_inst_num
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc, i.intv_seq_nbr desc"


', N'rintv ,
rAppe ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'wdnack-result', N'"select top 1 appe_date_time
from wdNackResult
where aid = ~aid~ and appe_s_umidl = ~s_umidl~ and appe_s_umidh = ~s_umidh~
order by appe_date_time desc "


', N'wdNackResult')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rintv-detection', N'"select substring(v.intv_merged_text, CHARINDEX(''Detection report ticket: '', v.intv_merged_text, 1)+24, CHARINDEX(char(13), v.intv_merged_text, 1) - 24) as detectionid 
from dbo.rintv v with (nolock)
where aid = ~aid~ and  intv_s_umidh = ~s_umidh~  and intv_s_umidl = ~s_umidl~ 
and v.intv_mpfn_name = ''OFCS_Detect'' and v.intv_inty_name = ''Toolkit intervention type''
and CHARINDEX(''Detection report ticket: '', v.intv_merged_text) > 0"



', N'rintv')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rmesg-trn-ref-only', N'"select M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) where ( MESG_TRN_REF  = ''~ReferenceNum~'')
and MESG_TRN_REF not in (''NON-REF'', ''NON REF'', ''NONREF'',''NOTPROVIDED'')  and mesg_crea_date_time > dateadd(day, -~RetrieveTill~ ,sysdatetime()) 
and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')"


', N'RMESG')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rtextfield', N'"select field_code, replace(value, ''//ACCC'', ''//ACSC'') value from dbo.rTextField with (nolock) where aid = ~aid~
and text_s_umidl = ~s_umidl~
and text_s_umidh = ~s_umidh~
and field_code in(50,59,79)"


', N'rTextField')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rintv', N'"select replace(i.intv_inty_name, ''Instance updateRouted in IPLA'',''Instance completed'') intv_inty_name, replace(i.intv_mpfn_name, ''IPLA_GPI_TS_MT103'', ''_SI_to_SWIFT'') intv_mpfn_name, 
i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) inner join dbo.rAppe a with (nolock) 
on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidl = a.appe_s_umidl and i.intv_inst_num = a.appe_inst_num
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc, i.intv_seq_nbr desc"


', N'rintv,
rAppe')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'wdnack-result', N'"select top 1 appe_date_time
from dbo.wdNackResult with (nolock)
where aid = ~aid~ and appe_s_umidl = ~s_umidl~ and appe_s_umidh = ~s_umidh~
order by appe_date_time desc "


', N'wdNackResult')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rintv-detection', N'"select substring(v.intv_merged_text, CHARINDEX(''Detection report ticket: '', v.intv_merged_text, 1)+24, CHARINDEX(char(13), v.intv_merged_text, 1) - 24) as detectionid 
from dbo.rintv v with (nolock)
where aid = ~aid~ and  intv_s_umidh = ~s_umidh~  and intv_s_umidl = ~s_umidl~ 
and v.intv_mpfn_name = ''OFCS_Detect'' and v.intv_inty_name = ''Toolkit intervention type''
and CHARINDEX(''Detection report ticket: '', v.intv_merged_text) > 0"



', N'rintv ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rmesg-trn-ref-only', N'"with mesg as (
select top 50 * from dbo.RMESG M with (nolock)
where mesg_crea_date_time >= dateadd(day, -60, getdate()) and mesg_crea_date_time <= getdate()
and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
and  MESG_TRN_REF  = ''~ReferenceNum~''
)
select M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from MESG M 
where (MESG_TRN_REF <> ''NON-REF'' and MESG_TRN_REF <> ''NON REF'' and MESG_TRN_REF <> ''NONREF'' and MESG_TRN_REF <> ''NOTPROVIDED'')
and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')"



', N'RMESG')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rtextfield', N'"select t.field_code, t.field_option , case when field_code in (50, 51, 52, 53, 54, 55, 56, 57, 59, 70, 72, 75,  76, 77, 79) then replace( t.value, char(13)+char(10), ''###'') else t.value end
from rtextfield t with (nolock)
where t.aid = ~aid~
and t.text_s_umidl = ~s_umidl~
and t.text_s_umidh = ~s_umidh~"


', N'rtextfield ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-stx-message', N'"select top 1 ''Swift Input : '' , description as ''Swift Input''
from dbo.stxmessage with (nolock)
where type = ''~MessageType~''
order by version_idx desc"



', N'stxmessage ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rcorr', N'select corr_x1, corr_institution_name,  corr_city_name, (corr_ctry_name + '' '' + corr_ctry_code) from  dbo.rcorr c with (nolock) where corr_x1 = ''~CorrBank~'' 
', N'rcorr')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rmesg', N'"select dateadd(second, -47, M.mesg_crea_date_time) as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''),''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))
union all
select top 20 M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_REL_TRN_REF = 
  (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''),''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))"


', N'RMESG  ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'SwiftRmesgTest', N'rmesgTestkey', N'select top 20 M.mesg_crea_date_time as ''Landing Time'', M.mesg_crea_date_time as''CompletionTime'', ''Completed'' as ''Activity Status'', M.MESG_TRN_REF as ''Source Ref No'', M.x_fin_ccy as ''CCY'', M.x_fin_amount as ''Amount'', M.mesg_fin_value_date as ''Value Date'', M.x_receiver_X1 as ''Receiver Bank'', ''Gateway'' as ''Workstage'', ''System'' as ''completedBy'', M.aid, M.mesg_s_umidl, M.mesg_s_umidh, mesg_type, mesg_sub_format, MESG_REL_TRN_REF, mesg_sender_x1 from dbo.RMESG M with (nolock) where mesg_crea_date_time >= dateadd(day, -45, getdate()) and mesg_crea_date_time <= getdate() and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950) and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'') and MESG_REL_TRN_REF = (select replace(replace(replace(replace(''033DBFC221253120'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''), ''NONREF'', ''INVAL''),''NONE'', ''INVAL''))


', N'RMESG ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rmesg', N'"select aid, mesg_s_umidh, mesg_s_umidl, 
(case when mesg_type = ''103'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''103'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
else ''Copy Sent/Received to SWIFT'' end ) as ''Header'', 
(case mesg_network_priority when ''PRI_NORMAL  '' then ''NORMAL'' when ''PRI_URGENT  '' then ''URGENT'' else mesg_network_priority end) as ''Priority/Delivery '' , 
mesg_trn_ref as ''Message Input Reference'' ,
x_receiver_X1 as ''Receiver'',
mesg_sender_x1 as ''Sender''
from dbo.rmesg with (nolock)
where mesg_trn_ref = ''~ReferenceNum~''
and mesg_sub_format = ''~MessageSubFormat~''"



', N'rmesg ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rintv', N'"select i.intv_inty_name, i.intv_mpfn_name, i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) left join dbo.rAppe a with (nolock) on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidh = a.appe_s_umidh
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc ,intv_seq_nbr desc"


', N'rintv ,
rAppe ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-stx-entry-field-view', N'"select code, entry_option, expansion,version_idx 
from stxEntryFieldView s with (nolock)
where s.type = ''~MessageType~''
and code in(~MessageCodes~) 
order by code,version_idx desc"


', N'stxEntryFieldView ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'rintv', N'"select (select char(13) + convert(varchar(max), i.intv_date_time, 13) +
               ''; INTV MergedText: '' + cast(i.intv_merged_text as varchar(max))
          FROM dbo.rintv i WITH(nolock)
         INNER JOIN dbo .rmesg m WITH(nolock)
            ON m.aid = i.aid
           AND m.mesg_s_umidl = i.intv_s_umidl
           AND m.mesg_s_umidh = i.intv_s_umidh
         WHERE m.aid = ~aid~ AND m.mesg_s_umidl = ~s_umidl~ AND m.mesg_s_umidh = ~s_umidh~
           FOR xml path(''''), type),
       
       (SELECT Char(13) + Cast(field_code AS VARCHAR) +
               COALESCE(field_option, '''') + '' : '' + value
          FROM dbo.rtextfield t WITH(nolock)
         INNER JOIN dbo .rmesg m WITH(nolock)
            ON m.aid = t.aid
           AND m.mesg_s_umidl = t.text_s_umidl
           AND m.mesg_s_umidh = t.text_s_umidh
         WHERE m.aid = ~aid~ AND m.mesg_s_umidl = ~s_umidl~ AND m.mesg_s_umidh = ~s_umidh~
           FOR xml path(''''), type)	"

', NULL)
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rmesg', N'"select aid, mesg_s_umidh, mesg_s_umidl, 
(case when mesg_type = ''103'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''103'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
else ''Copy Sent/Received to SWIFT'' end ) as ''Header'', 
(case mesg_network_priority when ''PRI_NORMAL  '' then ''NORMAL'' when ''PRI_URGENT  '' then ''URGENT'' else mesg_network_priority end) as ''Priority/Delivery '' , 
mesg_trn_ref as ''Message Input Reference'' ,
x_receiver_X1 as ''Receiver'',
mesg_sender_x1 as ''Sender''
from ~Owner~.rmesg with (nolock)
where mesg_trn_ref = ''~ReferenceNum~''
and mesg_sub_format = ''~MessageSubFormat~''"



', N'rmesg ,
rintv ,
rtextfield ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rintv', N'"select i.intv_inty_name, i.intv_mpfn_name, i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) left join dbo.rAppe a with (nolock) on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidh = a.appe_s_umidh
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc ,intv_seq_nbr desc"


', N'rintv ,
rAppe ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rtextfield', N'"select t.field_code, t.field_option , case when field_code in (50, 51, 52, 53, 54, 55, 56, 57, 59, 70, 72, 75,  76, 77, 79) then replace( t.value, char(13)+char(10), ''###'') else t.value end
from rtextfield t with (nolock)
where t.aid = ~aid~
and t.text_s_umidl = ~s_umidl~
and t.text_s_umidh = ~s_umidh~"


', N'rtextfield ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-stx-message', N'"select top 1 ''Swift Input : '' , description as ''Swift Input''
from dbo.stxmessage with (nolock)
where type = ''~MessageType~''
order by version_idx desc"


', N'stxmessage')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rcorr', N'"select corr_x1, corr_institution_name,  corr_city_name, (corr_ctry_name + '' '' + corr_ctry_code) from  dbo
.rcorr c with (nolock) where corr_x1 = ''~CorrBank~'' "



', N'rcorr')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-stx-entry-field-view', N'"select code, entry_option, expansion,version_idx 
from stxEntryFieldView s with (nolock)
where s.type = ''~MessageType~''
and code in(~MessageCodes~) 
order by code,version_idx desc"


', N'stxEntryFieldView ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rmesg', N'"select dateadd(second, -47, M.mesg_crea_date_time) as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''), ''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))
union all
select top 20 M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_REL_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''),''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))"

', N'RMESG  ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rmesg', N'"select dateadd(second, -47, M.mesg_crea_date_time) as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''), ''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))
union all
select top 20 M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_REL_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''),''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))"

', N'RMESG  ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rtextfield', N'"select field_code, replace(value, ''//ACCC'', ''//ACSC'') value from dbo.rTextField with (nolock) where aid = ~aid~
and text_s_umidl = ~s_umidl~
and text_s_umidh = ~s_umidh~
and field_code in(50,59,79)"


', N'rTextField ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rintv', N'"select i.intv_inty_name, i.intv_mpfn_name, i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) inner join dbo.rAppe a with (nolock) 
on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidl = a.appe_s_umidl and i.intv_inst_num = a.appe_inst_num
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc, i.intv_seq_nbr desc"


', N'rintv ,
rAppe ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'wdnack-result', N'"select top 1 appe_date_time
from wdNackResult
where aid = ~aid~ and appe_s_umidl = ~s_umidl~ and appe_s_umidh = ~s_umidh~
order by appe_date_time desc "


', N'wdNackResult')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rintv-detection', N'"select substring(v.intv_merged_text, CHARINDEX(''Detection report ticket: '', v.intv_merged_text, 1)+24, CHARINDEX(char(13), v.intv_merged_text, 1) - 24) as detectionid 
from dbo.rintv v with (nolock)
where aid = ~aid~ and  intv_s_umidh = ~s_umidh~  and intv_s_umidl = ~s_umidl~ 
and v.intv_mpfn_name = ''OFCS_Detect'' and v.intv_inty_name = ''Toolkit intervention type''
and CHARINDEX(''Detection report ticket: '', v.intv_merged_text) > 0"



', N'rintv')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation ', N'rmesg-trn-ref-only', N'"select M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) where ( MESG_TRN_REF  = ''~ReferenceNum~'')
and MESG_TRN_REF not in (''NON-REF'', ''NON REF'', ''NONREF'',''NOTPROVIDED'')  and mesg_crea_date_time > dateadd(day, -~RetrieveTill~ ,sysdatetime()) 
and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')"


', N'RMESG')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rtextfield', N'"select field_code, replace(value, ''//ACCC'', ''//ACSC'') value from dbo.rTextField with (nolock) where aid = ~aid~
and text_s_umidl = ~s_umidl~
and text_s_umidh = ~s_umidh~
and field_code in(50,59,79)"


', N'rTextField')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rintv', N'"select replace(i.intv_inty_name, ''Instance updateRouted in IPLA'',''Instance completed'') intv_inty_name, replace(i.intv_mpfn_name, ''IPLA_GPI_TS_MT103'', ''_SI_to_SWIFT'') intv_mpfn_name, 
i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) inner join dbo.rAppe a with (nolock) 
on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidl = a.appe_s_umidl and i.intv_inst_num = a.appe_inst_num
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc, i.intv_seq_nbr desc"


', N'rintv,
rAppe')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'wdnack-result', N'"select top 1 appe_date_time
from dbo.wdNackResult with (nolock)
where aid = ~aid~ and appe_s_umidl = ~s_umidl~ and appe_s_umidh = ~s_umidh~
order by appe_date_time desc "


', N'wdNackResult')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rintv-detection', N'"select substring(v.intv_merged_text, CHARINDEX(''Detection report ticket: '', v.intv_merged_text, 1)+24, CHARINDEX(char(13), v.intv_merged_text, 1) - 24) as detectionid 
from dbo.rintv v with (nolock)
where aid = ~aid~ and  intv_s_umidh = ~s_umidh~  and intv_s_umidl = ~s_umidl~ 
and v.intv_mpfn_name = ''OFCS_Detect'' and v.intv_inty_name = ''Toolkit intervention type''
and CHARINDEX(''Detection report ticket: '', v.intv_merged_text) > 0"



', N'rintv ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rmesg-trn-ref-only', N'"with mesg as (
select top 50 * from dbo.RMESG M with (nolock)
where mesg_crea_date_time >= dateadd(day, -60, getdate()) and mesg_crea_date_time <= getdate()
and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
and  MESG_TRN_REF  = ''~ReferenceNum~''
)
select M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from MESG M 
where (MESG_TRN_REF <> ''NON-REF'' and MESG_TRN_REF <> ''NON REF'' and MESG_TRN_REF <> ''NONREF'' and MESG_TRN_REF <> ''NOTPROVIDED'')
and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')"



', N'RMESG')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rtextfield', N'"select t.field_code, t.field_option , case when field_code in (50, 51, 52, 53, 54, 55, 56, 57, 59, 70, 72, 75,  76, 77, 79) then replace( t.value, char(13)+char(10), ''###'') else t.value end
from rtextfield t with (nolock)
where t.aid = ~aid~
and t.text_s_umidl = ~s_umidl~
and t.text_s_umidh = ~s_umidh~"


', N'rtextfield ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-stx-message', N'"select top 1 ''Swift Input : '' , description as ''Swift Input''
from dbo.stxmessage with (nolock)
where type = ''~MessageType~''
order by version_idx desc"



', N'stxmessage ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rcorr', N'select corr_x1, corr_institution_name,  corr_city_name, (corr_ctry_name + '' '' + corr_ctry_code) from  dbo.rcorr c with (nolock) where corr_x1 = ''~CorrBank~'' 
', N'rcorr')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Tracker ', N'rmesg', N'"select dateadd(second, -47, M.mesg_crea_date_time) as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_TRN_REF = (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''),''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))
union all
select top 20 M.mesg_crea_date_time as ''Landing Time'',
M.mesg_crea_date_time as''CompletionTime'',
''Completed'' as ''Activity Status'',
M.MESG_TRN_REF as ''Source Ref No'',
M.x_fin_ccy  as ''CCY'',
M.x_fin_amount as ''Amount'',
M.mesg_fin_value_date  as ''Value Date'',
M.x_receiver_X1 as ''Receiver'',
''Gateway'' as ''Workstage'',
''System'' as ''completedBy'',
M.aid,
M.mesg_s_umidl,
M.mesg_s_umidh,
mesg_type,
mesg_sub_format,
MESG_REL_TRN_REF,
mesg_sender_x1
from dbo.RMESG  M with (nolock) 
  where mesg_crea_date_time >= dateadd(day, -~RetrieveTill~, getdate()) and mesg_crea_date_time <= getdate()
  and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950)
  and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'')
  and MESG_REL_TRN_REF = 
  (select replace(replace(replace(replace(replace(''~ReferenceNum~'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''),''NONREF'', ''INVAL''),''NONE'', ''INVAL''),''NOTPROVIDED'', ''INVAL''))"


', N'RMESG  ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'SwiftRmesgTest', N'rmesgTestkey', N'select top 20 M.mesg_crea_date_time as ''Landing Time'', M.mesg_crea_date_time as''CompletionTime'', ''Completed'' as ''Activity Status'', M.MESG_TRN_REF as ''Source Ref No'', M.x_fin_ccy as ''CCY'', M.x_fin_amount as ''Amount'', M.mesg_fin_value_date as ''Value Date'', M.x_receiver_X1 as ''Receiver Bank'', ''Gateway'' as ''Workstage'', ''System'' as ''completedBy'', M.aid, M.mesg_s_umidl, M.mesg_s_umidh, mesg_type, mesg_sub_format, MESG_REL_TRN_REF, mesg_sender_x1 from dbo.RMESG M with (nolock) where mesg_crea_date_time >= dateadd(day, -45, getdate()) and mesg_crea_date_time <= getdate() and mesg_type in (101, 102, 103, 191, 192, 195, 196, 199, 202, 291, 292, 295, 296, 299, 910, 940, 950) and (mesg_sender_X1 <> ''TRCKCHZZVAL'' and x_receiver_X1 <> ''TRCKCHZZXXX'') and MESG_REL_TRN_REF = (select replace(replace(replace(replace(''033DBFC221253120'', ''NON-REF'',''INVAL''), ''NON REF'', ''INVAL''), ''NONREF'', ''INVAL''),''NONE'', ''INVAL''))


', N'RMESG ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rmesg', N'"select aid, mesg_s_umidh, mesg_s_umidl, 
(case when mesg_type = ''103'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''103'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
else ''Copy Sent/Received to SWIFT'' end ) as ''Header'', 
(case mesg_network_priority when ''PRI_NORMAL  '' then ''NORMAL'' when ''PRI_URGENT  '' then ''URGENT'' else mesg_network_priority end) as ''Priority/Delivery '' , 
mesg_trn_ref as ''Message Input Reference'' ,
x_receiver_X1 as ''Receiver'',
mesg_sender_x1 as ''Sender''
from dbo.rmesg with (nolock)
where mesg_trn_ref = ''~ReferenceNum~''
and mesg_sub_format = ''~MessageSubFormat~''"



', N'rmesg ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-rintv', N'"select i.intv_inty_name, i.intv_mpfn_name, i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) left join dbo.rAppe a with (nolock) on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidh = a.appe_s_umidh
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc ,intv_seq_nbr desc"


', N'rintv ,
rAppe ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Message Details', N'msg-stx-entry-field-view', N'"select code, entry_option, expansion,version_idx 
from stxEntryFieldView s with (nolock)
where s.type = ''~MessageType~''
and code in(~MessageCodes~) 
order by code,version_idx desc"


', N'stxEntryFieldView ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'rintv', N'"select (select char(13) + convert(varchar(max), i.intv_date_time, 13) +
               ''; INTV MergedText: '' + cast(i.intv_merged_text as varchar(max))
          FROM dbo.rintv i WITH(nolock)
         INNER JOIN dbo .rmesg m WITH(nolock)
            ON m.aid = i.aid
           AND m.mesg_s_umidl = i.intv_s_umidl
           AND m.mesg_s_umidh = i.intv_s_umidh
         WHERE m.aid = ~aid~ AND m.mesg_s_umidl = ~s_umidl~ AND m.mesg_s_umidh = ~s_umidh~
           FOR xml path(''''), type),
       
       (SELECT Char(13) + Cast(field_code AS VARCHAR) +
               COALESCE(field_option, '''') + '' : '' + value
          FROM dbo.rtextfield t WITH(nolock)
         INNER JOIN dbo .rmesg m WITH(nolock)
            ON m.aid = t.aid
           AND m.mesg_s_umidl = t.text_s_umidl
           AND m.mesg_s_umidh = t.text_s_umidh
         WHERE m.aid = ~aid~ AND m.mesg_s_umidl = ~s_umidl~ AND m.mesg_s_umidh = ~s_umidh~
           FOR xml path(''''), type)	"

', NULL)
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rmesg', N'"select aid, mesg_s_umidh, mesg_s_umidl, 
(case when mesg_type = ''103'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''103'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' 
when mesg_type = ''202'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' 
else ''Copy Sent/Received to SWIFT'' end ) as ''Header'', 
(case mesg_network_priority when ''PRI_NORMAL  '' then ''NORMAL'' when ''PRI_URGENT  '' then ''URGENT'' else mesg_network_priority end) as ''Priority/Delivery '' , 
mesg_trn_ref as ''Message Input Reference'' ,
x_receiver_X1 as ''Receiver'',
mesg_sender_x1 as ''Sender''
from ~Owner~.rmesg with (nolock)
where mesg_trn_ref = ''~ReferenceNum~''
and mesg_sub_format = ''~MessageSubFormat~''"



', N'rmesg ,
rintv ,
rtextfield ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rtextfield', N'"select t.field_code, t.field_option , case when field_code in (50, 51, 52, 53, 54, 55, 56, 57, 59, 70, 72, 75,  76, 77, 79) then replace( t.value, char(13)+char(10), ''###'') else t.value end
from rtextfield t with (nolock)
where t.aid = ~aid~
and t.text_s_umidl = ~s_umidl~
and t.text_s_umidh = ~s_umidh~"


', N'rtextfield ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-stx-message', N'"select top 1 ''Swift Input : '' , description as ''Swift Input''
from dbo.stxmessage with (nolock)
where type = ''~MessageType~''
order by version_idx desc"


', N'stxmessage')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rcorr', N'"select corr_x1, corr_institution_name,  corr_city_name, (corr_ctry_name + '' '' + corr_ctry_code) from  dbo
.rcorr c with (nolock) where corr_x1 = ''~CorrBank~'' "



', N'rcorr')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-stx-entry-field-view', N'"select code, entry_option, expansion,version_idx 
from stxEntryFieldView s with (nolock)
where s.type = ''~MessageType~''
and code in(~MessageCodes~) 
order by code,version_idx desc"


', N'stxEntryFieldView ')
GO
INSERT [dbo].[SWIFT_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Swift Details', N'msg-rintv', N'"select i.intv_inty_name, i.intv_mpfn_name, i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason 
from dbo.rintv i with (nolock) left join dbo.rAppe a with (nolock) on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidh = a.appe_s_umidh
where i.aid = ~aid~
and intv_s_umidl = ~s_umidl~
and intv_s_umidh = ~s_umidh~
order by i.intv_date_time desc ,intv_seq_nbr desc"


', N'rintv ,
rAppe ')
GO
