insert into conf_Report values  ('swiftDetails','Swift Details','Swift Details','report','y','y',2)

insert into conf_prompt values('aid','aid',1,'y',10009,NULL)

insert into conf_prompt values('s_umidl','s_umidl',2,'y',10009,NULL)

insert into conf_prompt values('s_umidh','s_umidh',3,'y',10009,NULL)

insert into conf_prompt values('SWIFTDetailedType','SWIFTDetailedType',4,'y',10009,NULL)

insert into conf_prompt values('MessageType','MessageType',5,'y',10009,NULL)

insert into conf_prompt values('MessageSubFormat','MessageSubFormat',6,'y',10009,NULL)

insert into conf_prompt values('ReferenceNum','ReferenceNum',7,'y',10009,NULL)

insert into conf_metric values('Field Description',1,10009,'y',NULL)

insert into conf_metric values('Field Value',2,10009,'y',NULL)

insert into conf_metric values('INTV Details',3,10009,'y',NULL)

insert into conf_rpt_comp values (10009,'SWIFT DR','Default','y')

insert into conf_rpt_comp_det values ('select (select char(13) + convert(varchar(max), i.intv_date_time, 13) +''; INTV MergedText: '' + cast(i.intv_merged_text as varchar(max)) FROM rintv i WITH(nolock) INNER JOIN rmesg m WITH(nolock) ON m.aid = i.aid AND m.mesg_s_umidl = i.intv_s_umidl AND m.mesg_s_umidh = i.intv_s_umidh WHERE m.aid = ~aid~ AND m.mesg_s_umidl = ~s_umidl~ AND m.mesg_s_umidh = ~s_umidh~ FOR xml path(''), type),(SELECT Char(13) + Cast(field_code AS VARCHAR) +COALESCE(field_option, '') + '': '' + value FROM rtextfield t WITH(nolock) INNER JOIN rmesg m WITH(nolock) ON m.aid = t.aid AND m.mesg_s_umidl = t.text_s_umidl AND m.mesg_s_umidh = t.text_s_umidh WHERE m.aid = ~aid~ AND m.mesg_s_umidl = ~s_umidl~ AND m.mesg_s_umidh = ~s_umidh~ FOR xml path(''), type)','rintv',3)

insert into conf_rpt_comp_det values('select aid, mesg_s_umidh, mesg_s_umidl, (case when mesg_type = ''103'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' when mesg_type = ''103'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' when mesg_type = ''202'' and mesg_sub_format = ''INPUT'' then ''Notification (Transmission) of Original sent to SWIFT'' when mesg_type = ''202'' and mesg_sub_format = ''OUTPUT'' then ''Notification (Transmission) of Original received from SWIFT'' else ''Copy Sent/Received to SWIFT'' end ) as ''Header'', (case mesg_network_priority when ''PRI_NORMAL  '' then ''NORMAL'' when ''PRI_URGENT  '' then ''URGENT'' else mesg_network_priority end) as ''Priority/Delivery '' , mesg_trn_ref as ''Message Input Reference'' , x_receiver_X1 as ''Receiver'', mesg_sender_x1 as ''Sender'' from rmesg with (nolock) where mesg_trn_ref = ''~ReferenceNum~'' and mesg_sub_format = ''~MessageSubFormat~''','msg-rmesg',3)

insert into conf_rpt_comp_det values('select i.intv_inty_name, i.intv_mpfn_name, i.intv_date_time, a.appe_network_delivery_status, a.appe_nak_reason from rintv i with (nolock) left join rAppe a with (nolock) on i.aid = a.aid and i.intv_s_umidh = a.appe_s_umidh and i.intv_s_umidh = a.appe_s_umidh where i.aid = ~aid~ and intv_s_umidl = ~s_umidl~ and intv_s_umidh = ~s_umidh~ order by i.intv_date_time desc ,intv_seq_nbr desc','msg-rintv',3)

insert into conf_rpt_comp_det values('select t.field_code, t.field_option , case when field_code in (50, 51, 52, 53, 54, 55, 56, 57, 59, 70, 72, 75,  76, 77, 79) then replace( t.value, char(13)+char(10), ''###'') else t.value end from rtextfield t with (nolock) where t.aid = ~aid~ and t.text_s_umidl = ~s_umidl~ and t.text_s_umidh = ~s_umidh~','msg-rtextfield',3)

insert into conf_rpt_comp_det values('select top 1 ''Swift Input : '' , description as ''Swift Input'' from stxmessage with (nolock) where type = ''~MessageType~'' order by version_idx desc','msg-stx-message',3)

insert into conf_rpt_comp_det values('select corr_x1, corr_institution_name,  corr_city_name, (corr_ctry_name + ' ' + corr_ctry_code) from  rcorr c with (nolock) where corr_x1 = '~CorrBank~'' ,'msg-rcorr',3)

insert into conf_rpt_comp_det values('select code, entry_option, expansion,version_idx from stxEntryFieldView s with (nolock) where s.type = ''~MessageType~'' and code in(~MessageCodes~) order by code,version_idx desc','msg-stx-entry-field-view',3)