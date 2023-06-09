USE [Swift]
GO
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Advance serach ', N'edms', N'" select distinct f.wi_name  as ""Transaction Reference"",
(f.BENE_ACCOUNT_NO ||'' ''||
f.FLX_BEN_FULL_NAME || '' ''|| f.REMITTANCE_INFO || '' ''|| f.SENDER_REC_INFO) as ""Beneficiary Details"", 
TO_CHAR(nvl(f.VALUE_DATE, f.introduction_date_time), ''dd-MM-yyyy'')as ""Value Date"", 
nvl(decode(f.currency, ''--Select--'', '''', f.currency), ''AED'') as ""Currency"",
nvl(f.amount, 0) as ""Amount"", 
''In Progress'' AS ""Transaction Status"", 
''NA'' as ""Message Type"", 
''EDMS'' AS ""Initiation Source"", 
(case when f.CURRENCY = ''AED'' then ''UAEFTS'' else ''SWIFT'' end)  as ""Message Through"",
f.appl_account_no as ""Account No"", 
''NA'' as ""Related Account"", 
nvl((f.flx_ben_swift_code||'', ''),'''')||nvl(f.flx_ben_bank_name,'''') as ""Instrument Code"",
nvl(f.core_ref , '''') as ""External Ref No"",
TRANSACTION_ID as ""Core Ref No"",
q.introductiondatetime as ""Transaction Date"",
q.processname as ""Process Name"", 
q.activityname as ""Activity Name"",
(case  when DECISION = ''Reject'' then ''Rejected''  when DECISION = ''Decline'' then ''Rejected'' when DECISION = ''Discard'' then ''Rejected'' else ''NA'' end) AS ""Reject Status""
from mashreqibps.QUEUEVIEWTRK q, mashreqibps.mrq_cpc_fto_exttable f
where q.processinstanceid = f.wi_name and q.processname = ''CPC_FTO'' and nvl(f.appl_account_no, ''x'')<>''x''
and q.introductiondatetime between to_date(''~FromDate~'', ''DD/MM/YYYY'') and (to_date(''~ToDate~'', ''DD/MM/YYYY'')+1)
and f.appl_account_no = ''~AccountNumber~''
and f.transaction_id is null
and instr(f.wi_name, decode(''~TransactionReferenceNo~'', ''NULL'', f.wi_name, ''~TransactionReferenceNo~'')) > 0
and nvl(decode(f.currency, ''--Select--'', ''AED'', f.currency), ''AED'') = decode(upper(trim(''~Currency~'')), ''NULL'', nvl(decode(f.currency, ''--Select--'', ''AED'', f.currency), ''AED''), upper(trim(''~Currency~'')))
and cast(nvl(f.amount, 0) as decimal) between coalesce(~AmountFrom~, cast(nvl(f.amount, 0) as decimal)) and coalesce(~AmountTo~, cast(nvl(f.amount, 0) as decimal))"

', N'QUEUEVIEWTRK ,mrq_cpc_fto_exttable')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation', N'rid-tat', N'"SELECT     tat.entry_date_time AS ""Landing Time"", ( 
            CASE tat.activity_name
                       WHEN N''Introduction'' THEN N''New case raised''
                       WHEN N''Branch Processing'' THEN N''Referred to Branch''
                       WHEN N''OUTSTANDING'' THEN N''Marked to awaiting Queue''
                       WHEN N''Closer'' THEN N''Marked for closure''
                       WHEN N''Branch_Checker'' THEN N''Branch Checker Authorization''
                       WHEN N''Business_Compliance'' THEN N''Compliance Ops Referral'' 
                       WHEN N''RCU'' THEN N''Call Back - Referral''
                       WHEN N''CBU_BRANCH'' THEN N''Call Back - Branch Referral'' 
                       WHEN N''Checker'' THEN N''Checker Processing'' 
                       WHEN N''Data Entry'' THEN N''Work Item Entry'' 
                       WHEN N''Data Verification'' THEN N''Work Item Entry'' 
                       WHEN N''Image_QC'' THEN N''Branch Processing'' 
                       WHEN N''ImageQC'' THEN N''Branch Processing'' 
                       WHEN N''Processing_Verify'' THEN N''Branch Processing'' 
                       WHEN N''Refferal Queue'' THEN N''Referred for discrepancy'' 
                       WHEN N''Team Leader'' THEN N''Second Checker Authorization'' 
                       WHEN N''TL_Manager_Approval'' THEN N''Manager Approval''
                       WHEN N''Logging'' THEN N''Case Logged''
                       WHEN N''Action_Correspnc'' THEN N''Action reply to correspondent bank''
                       ELSE tat.activity_name
            END) ||'' |Type:''||nvl(rid.problem_type, '''') AS ""Activity"", 
            tat.exit_date_time AS ""Completion Time"", ( 
            CASE 
                       WHEN tat.exit_date_time IS NULL THEN ''Pending'' 
                       ELSE ''Completed'' 
            END )       AS ""Activity Status"", 
            rid.wi_name AS ""EDMS ref no"", 
            rid.currency, 
            rid.amount, 
            tat.activity_name AS ""WorkStage"", 
            tat.processed_by  AS ""Completed By"", 
            tat.pickuptime    AS ""pickup Time"" 
 FROM  mashreqibps.mrq_cpc_rid_exttable rid, mashreqibps.USR_0_TAT_REPORT_VIEW2 tat 
 WHERE rid.wi_name = tat.process_instance_id 
 AND ( (''INQ''||substr(rid.wi_name, 16, 6)= ''~ReferenceNum~'') OR
      (rid.processing_info =''~ReferenceNum~'') OR (rid.transaction_ref =''~ReferenceNum~'') or (rid.ref_wi_id =''~ReferenceNum~''))
 AND tat.activity_name NOT IN (''Archive'', ''Auto Upload Advice'', ''Branch'', ''Duplicate'', ''Log_cash_Mgmt'', ''OCR_Extraction'', 
 ''OCR_Registration'', ''Original Tracking'', ''Other_Sys_Checker'', ''Rescan'', ''WNS Queue'')"


', N'mrq_cpc_rid_exttable,
USR_0_TAT_REPORT_VIEW2')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'EDMS_FTC', N'EDMS-ftc-query-TT', N'"SELECT
	wi_name as ""Detail Concept"",
	Ref_No as ""Detail Concept"",
	contract_ref_num as ""Detail Concept"",
	cif_no as ""Source Ref No"",
	
	UPPER(currworkstepname) currworkstepname,
	UPPER(document_type) document_type,
	UPPER(approval_status) approval_status,
	UPPER(maker_decision) maker_decision,
	UPPER(checker_decision) checker_decision,
	(case
		when UPPER(document_type) = ''NEW LC'' then ''LCISSREQ''
		when UPPER(document_type) = ''LC AMENDMENT'' then ''LCAMEREQ''
		when UPPER(document_type) = ''NEW LG'' then ''BGISSREQ''
		when UPPER(document_type) = ''LG AMENDMENT'' then ''BGAMEREQ''
		when UPPER(document_type) = ''OPEN ACC'' then ''OPADPAYTRL''
		when UPPER(document_type) = ''IMP-TR SETT'' then ''SETLTRQ''
		when UPPER(document_type) = ''EXPORT CONFIRMATION LC'' then ''DDB012''
		when UPPER(document_type) = ''EXPORT TRANSFER LC'' then ''DDB013''
		when UPPER(document_type) = ''AOP'' then ''DDB014''
	end) as SubProduct,
	(case
		when contract_ref_num ='''' then 1
		when contract_ref_num is null then 1
		else rank() over (PARTITION BY document_type,contract_ref_num order by  intro_date desc)
	end) as ContractRank
FROM
	mashreqibps.mrq_ftc_doc_exttable
where
	UPPER(host) = ''UAE'' and
	UPPER(document_type) IN (''OPEN ACC'',''IMP-TR SETT'') and
	trunc(intro_date) between to_date(''20/09/22'', ''DD/MM/YYYY'') and to_date(''30/09/22'', ''DD/MM/YYYY'') and
	cif_no IN (''013847081'') and  ( contract_ref_num = ''032ICAP222660009'' )"

', N'mrq_ftc_doc_exttable')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'EDMS RID Details', N'edms-rid', N'"SELECT WI_NAME,
       INQ_REC_DATE,
       CURRENCY,
       SUB_PROBLEM_TYPE,
       TRANSACTION_REF,
       PROCESSING_UNIT,
       CUSTOMER_INQUIRY_SOURCE,
       TYPE_OF_INQUIRY_NEW,
       PROCESSING_INFO,
       USER_STATUS,
       REFERRED_TO,
       PROBLEM_TYPE,
       MESSAGE_TYPE,
       DUPLICATE,
       SOURCE_OF_APP,
       BUSINESS_GROUP
  FROM mashreqibps.mrq_cpc_rid_exttable
 WHERE ( (wi_name = ''~ReferenceNum~'') OR (''INQ''||substr(wi_name, 16, 6)= ''~ReferenceNum~'') OR (processing_info =''~ReferenceNum~'') OR (transaction_ref =''~ReferenceNum~'') OR (ref_wi_id =''~ReferenceNum~'') )"


', N'mrq_cpc_rid_exttable')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation', N'fto-tat', N'"select decode(tat.entry_date_time,
              null,
              decode(fto.currworkstepname,
              ''Data Entry'',fto.data_entry_entry_datetime,
              ''Checker'',fto.checker_entry_datetime,
              ''EDD Checker'',fto.edd_checker_entry_time,
              ''EDD Maker'',fto.edd_maker_entry_time,
              ''EDD_Compliance'',fto.edd_compliance_entry_date,
              ''ImageQC'',fto.imageqc_entry_datetime,
              ''Processing_Verify'',fto.process_verify_entry_datetime,
              ''Refferal Queue'',fto.refferal_queue_entry_datetime,
              ''Supervisor_Branch'',fto.super_branch_entry_datetime,
              ''Supervisor_CreditOps'',fto.super_creditops_entry_datetime,
              ''Duplicate'',fto.duplicate_entry_datetime,
              ''Business_Compliance'',fto.business_compliance_entry),
              tat.entry_date_time) as ""Landing Time"",
       (case nvl(tat.activity_name, N'''')
         when N''Branch_Checker'' then N''Branch Checker Authorization''
         when N''Business_Compliance'' then N''Compliance Ops Referral''
         when N''CBU'' then N''Call Back - Referral''
         when N''CBU_BRANCH'' then N''Call Back - Branch Referral''
         when N''Central_Compliance'' then N''HO Compliance Review''
         when N''Checker'' then N''Checker Processing''
         when N''Credit Ops'' then N''Credit Refferal Maker''
         when N''Credit_Ops_Checker'' then N''Credit Referral Checker''
         when N''CSU'' then N''Customer Service Maker''
         when N''CSU_Checker'' then N''Customer Service Checker''
         when N''Data Entry'' then N''Data Entry''
         when N''Data Verification'' then N''Data Verification''
         when N''DataEntry'' then N''Data Entry''
         when N''Discarded Items'' then N''Work Item Rejected''
         when N''Discrepancy_CallBack'' then N''Referred for Discrepancy''
         when N''Image_QC'' then N''Scanned Image QC''
         when N''ImageQC'' then N''Scanned Image QC''
         when N''OCR_Extraction'' then N''OCR Extraction''
         when N''OCR_Registration'' then N''OCR Registration''
         when N''Processing_Verify'' then N''Maker Processing''
         when N''Refferal Queue'' then N''Referred for discrepancy''
         when N''Supervisor_CreditOps'' then N''Credit Refferal Second Checker''
         when N''Supervisor_Branch'' then N''Branch Refferal Second Checker''
         when N''Team Leader'' then N''Second Checker Authorization''
         when N''EDD Maker'' then N''EDD Maker''
         when N''EDD Checker'' then N''EDD Checker''
         when N''EDD_Compliance'' then N''EDD Compliance''
         when N''Refferal Queue'' then N''Refferal Queue''
         when N''Duplicate'' then N''Duplicate Queue''
         when N''Business_Compliance'' then N''Business Compliance Queue''
         else q.ACTIVITYNAME
       end) || '''' || (case
         when fto.preworkstepname = tat.activity_name then
          decode(fto.decision,
          ''Reject'','' - Reject'',
          ''Rejected'','' - Reject'',
          ''Decline'','' - Decline'', '''')
         else '''' end) as ""Activity"",
       tat.exit_date_time as ""Completion Time"",
       (case when tat.exit_date_time is null then ''Pending'' else ''Completed'' end) as ""Activity Status"",
       fto.wi_name as ""EDMS ref no"",
       decode(fto.currency, ''--Select--'', '''', fto.currency) as currency,
       fto.amount,
       to_char(fto.value_date, ''dd-MM-yyyy'') AS ""Value Date"",
       fto.appl_account_no as ""Debit A/C"",
       (nvl(fto.flx_ben_swift_code || '', '', '''') ||
       nvl(fto.flx_ben_bank_name || '', '', '''') ||
       nvl(fto.intermediary_inst, '''')) as ""Receiver"",
       fto.bene_account_no as ""Beneficiary A/C"",
       nvl(fto.flx_ben_country || '', '', '''') ||
       nvl(fto.flx_ben_city || '', '', '''') ||
       nvl(fto.flx_ben_full_name || '', '', '''') ||
       nvl(fto.remittance_info || '', '', '''') || nvl(fto.sender_rec_info, '''') as ""Ben Details"",
       decode(tat.activity_name, null, q.ACTIVITYNAME, tat.activity_name) as ""WorkStage"",
       tat.processed_by as ""Completed By"",
       null as ""pickup Time"", /*tat.pickuptime*/
       (case
         when fto.currworkstepname in
              (''Business_Compliance'', /*''Checker'', ''Data Entry'', ''EDD Checker'', ''EDD Maker'', ''EDD_Compliance'', ''ImageQC'', ''Processing_Verify'', ''Duplicate'',*/
               ''Refferal Queue'', ''Supervisor_Branch'', ''Supervisor_CreditOps'') then ''Y''
 	 when fto.currworkstepname = ''Data Entry'' and fto.data_entry_entry_datetime is not null then ''Y''
 	 when fto.currworkstepname = ''Checker'' and fto.checker_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''EDD Checker'' and fto.edd_checker_entry_time is not null then ''Y''
	 when fto.currworkstepname = ''EDD Maker'' and fto.edd_maker_entry_time is not null then ''Y''
	 when fto.currworkstepname = ''EDD_Compliance'' and fto.edd_compliance_entry_date is not null then ''Y''
	 when fto.currworkstepname = ''ImageQC'' and fto.imageqc_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Processing_Verify'' and fto.process_verify_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Refferal Queue'' and fto.refferal_queue_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Supervisor_Branch'' and fto.super_branch_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Supervisor_CreditOps'' and fto.super_creditops_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Duplicate'' and fto.duplicate_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Business_Compliance'' and fto.business_compliance_entry is not null then ''Y''
         when fto.refferal_queue_entry_datetime is not null then  ''Y''
         when fto.CREDIT_OPS_ENTRY_DATETIME is not null then ''Y''
         when fto.call_back_required = ''Y'' then ''Y''
         when fto.call_back_required = ''Yes'' then ''Y''
         when fto.call_back_required = ''No'' then ''N''
         when fto.call_back_required = ''N'' then ''N''
         when fto.call_back_required = ''ByPass'' then ''Y''
         when fto.call_back_required in null then ''N''
         when fto.call_back_required in null then ''N''
         else ''Y'' end) as ""Governance Check""
  from mashreqibps.mrq_cpc_fto_exttable fto
  LEFT JOIN mashreqibps.USR_0_TAT_REPORT_VIEW2 tat on tat.process_instance_id = fto.wi_name
   and tat.activity_name not in (''Archive'',
                                 ''Auto Upload Advice'',
                                 ''Branch'', /*''Duplicate'',*/
                                 ''Log_cash_Mgmt'',
                                 ''OCR_Extraction'',
                                 ''OCR_Registration'',
                                 ''Original Tracking'',
                                 ''Other_Sys_Checker'',
                                 ''Rescan'',
                                 ''WNS Queue'')
 inner join mashreqibps.QUEUEVIEWTRK q on fto.wi_name = q.processinstanceid
 where fto.wi_name =  ''~ReferenceNum~''
/*and q.introductiondatetime > sysdate - (~RetrieveTill~+1)
order by tat.entry_date_time asc*/"

', N'mrq_cpc_fto_exttable,
mrq_cpc_fto_exttable,
QUEUEVIEWTRK')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Investigation', N'edd-referral', N'"with inprogress as
(SELECT (CASE 
           WHEN fto.currworkstepname=''Business_Compliance'' THEN fto.BUSINESS_COMPLIANCE_ENTRY
           WHEN fto.currworkstepname=''Checker'' THEN fto.CHECKER_ENTRY_DATETIME
           when fto.currworkstepname=''Data Entry'' then fto.DATA_ENTRY_ENTRY_DATETIME
           when fto.currworkstepname=''EDD Checker'' then fto.EDD_CHECKER_ENTRY_TIME
           when fto.currworkstepname=''EDD Maker'' then fto.EDD_MAKER_ENTRY_TIME
           when fto.currworkstepname=''EDD_Compliance'' then fto.EDD_COMPLIANCE_ENTRY_DATE
           when fto.currworkstepname=''ImageQC'' then fto.IMAGEQC_ENTRY_DATETIME
           when fto.currworkstepname=''Processing_Verify'' then fto.PROCESS_VERIFY_ENTRY_DATETIME
           when fto.currworkstepname=''Refferal Queue'' then fto.REFFERAL_QUEUE_ENTRY_DATETIME
           when fto.currworkstepname=''Supervisor_Branch'' then fto.SUPER_BRANCH_ENTRY_DATETIME
           when fto.currworkstepname=''Supervisor_CreditOps'' then fto.SUPER_CREDITOPS_ENTRY_DATETIME
           when fto.currworkstepname=''Duplicate'' then fto.DUPLICATE_ENTRY_DATETIME
         END) AS Landing_Time,
         fto.currworkstepname as activity,
         null AS Completion_time,
         ''Pending'' AS Activity_Status,
         fto.wi_name AS Reference_No,
         decode(fto.currency, ''--Select--'', '''', fto.currency) as currency,
         fto.amount,
         To_char(fto.value_date, ''dd-MM-yyyy'') AS Value_Date,
         fto.appl_account_no AS Debit_ac,
         (Nvl(fto.flx_ben_swift_code || '', '', '''') || Nvl(fto.flx_ben_bank_name || '', '', '''') || Nvl(fto.intermediary_inst, '''')) AS Receiver,
          fto.bene_account_no  AS Beneficiary_ac,
         (Nvl(fto.flx_ben_country || '', '', '''')  || Nvl(fto.flx_ben_city || '', '', '''') || Nvl(fto.flx_ben_full_name || '', '', '''') ||
         Nvl(fto.remittance_info || '', '', '''') || Nvl(fto.sender_rec_info, '''')) AS Ben_Details,
         fto.currworkstepname AS WorkStage,
         '''' AS Completed_By,
         (CASE 
           WHEN fto.currworkstepname=''Business_Compliance'' THEN fto.BUSINESS_COMPLIANCE_EXIT
           WHEN fto.currworkstepname=''Checker'' THEN fto.CHECKER_EXIT_DATETIME
           when fto.currworkstepname=''Data Entry'' then fto.DATA_ENTRY_EXIT_DATETIME
           when fto.currworkstepname=''EDD Checker'' then fto.EDD_CHECKER_EXIT_TIME
           when fto.currworkstepname=''EDD Maker'' then fto.EDD_MAKER_EXIT_TIME
           when fto.currworkstepname=''EDD_Compliance'' then fto.EDD_COMPLIANCE_EXIT_DATE
           when fto.currworkstepname=''ImageQC'' then fto.IMAGEQC_EXIT_DATETIME
           when fto.currworkstepname=''Processing_Verify'' then fto.PROCESS_VERIFY_EXIT_DATETIME
           when fto.currworkstepname=''Refferal Queue'' then fto.REFFERAL_QUEUE_EXIT_DATETIME
           when fto.currworkstepname=''Supervisor_Branch'' then fto.SUPERV_BRANCH_EXIT_DATETIME
           when fto.currworkstepname=''Supervisor_CreditOps'' then fto.SUPER_CREDITOPS_EXIT_DATETIME
           when fto.currworkstepname=''Duplicate'' then fto.DUPLICATE_EXIT_DATETIME  END) as exit_time
    FROM mashreqibps.mrq_cpc_fto_exttable fto
   WHERE (fto.wi_name = ''~ReferenceNum~'' or fto.gov_contract_ref_no = ''~ReferenceNum~'') )
select landing_time    as ""Landing Time"",
       CAST(activity AS VARCHAR2(1000))        as ""Activity"",
       completion_time as ""Completion Time"",
       activity_status as ""Activity Status"",
       CAST(reference_no AS VARCHAR2(1000))    as ""EDMS ref no"",
       currency,
       amount,
       value_date      AS ""Value Date"",
       CAST( debit_ac AS VARCHAR2(1000))       as ""Debit A/C"",
       receiver        as ""Receiver"",
      CAST( beneficiary_ac AS VARCHAR2(1000))  as ""Beneficiary A/C"",
       ben_details     as ""Ben Details"",
       CAST(workstage AS VARCHAR2(1000))       as ""WorkStage"",
       completed_by    as ""Completed By""
  from inprogress
where ((exit_time is null) or (landing_time > exit_time))
   and activity in (''Business_Compliance'', ''Checker'', ''Data Entry'', ''EDD Checker'', ''EDD Maker'', ''EDD_Compliance'', ''ImageQC'', ''Processing_Verify'', ''Duplicate'',
                    ''Refferal Queue'',  ''Supervisor_Branch'', ''Supervisor_CreditOps'')
   and (select count(1) from mashreqibps.USR_0_TAT_REPORT_VIEW2 tat where tat.process_instance_id = inprogress.Reference_No and tat.activity_name <> inprogress.activity) >= 1
union all
SELECT (CASE
         WHEN parent_activity_name = ''CBU'' THEN fto.cbu_entry_datetime
         WHEN parent_activity_name = ''Credit_Ops'' THEN fto.credit_ops_entry_datetime
         WHEN parent_activity_name = ''Credit Ops'' THEN fto.credit_ops_entry_datetime
       END) AS ""Landing Time"",
       (CASE
          WHEN parent_activity_name = ''CBU'' THEN ''Call Back''
         WHEN parent_activity_name = ''Credit_Ops'' THEN ''Credit Referral''
         WHEN parent_activity_name = ''Credit Ops'' THEN ''Credit Referral''
       END) AS ""Activity"",
       (CASE
         WHEN parent_activity_name = ''CBU'' AND fto.cbu_exit_datetime is NULL THEN sysdate
         WHEN parent_activity_name = ''CBU'' AND fto.cbu_exit_datetime is NOT NULL THEN fto.cbu_exit_datetime
         WHEN parent_activity_name = ''Credit_Ops'' and fto.credit_ops_exit_datetime is null then  sysdate
         WHEN parent_activity_name = ''Credit_Ops'' and fto.credit_ops_exit_datetime is not null THEN fto.credit_ops_exit_datetime
         WHEN parent_activity_name = ''Credit Ops'' and fto.credit_ops_exit_datetime is null then sysdate
         WHEN parent_activity_name = ''Credit Ops'' and fto.credit_ops_exit_datetime is not null THEN fto.credit_ops_exit_datetime
       END) AS ""Completion_time"",
       CAST( nvl(to_char(ref.decision), ''Pending'') AS VARCHAR2(1000)) AS ""Activity Status"",
       CAST(fto.wi_name AS VARCHAR2(1000)) AS ""Reference No"",
       decode(fto.currency, ''--Select--'', '''', fto.currency) as currency,
       fto.amount,
       To_char(fto.value_date, ''dd-MM-yyyy'') AS ""Value Date"",
       CAST(fto.appl_account_no AS VARCHAR2(1000)) AS ""Debit A/C"",
       (Nvl(fto.flx_ben_swift_code || '', '', '''') || Nvl(fto.flx_ben_bank_name || '', '', '''') || Nvl(fto.intermediary_inst, '''')) AS ""Receiver"",
       CAST(fto.bene_account_no AS VARCHAR2(1000)) AS ""Beneficiary A/C"",
       (Nvl(fto.flx_ben_country || '', '', '''')  || Nvl(fto.flx_ben_city || '', '', '''') || Nvl(fto.flx_ben_full_name || '', '', '''') ||
       Nvl(fto.remittance_info || '', '', '''') || Nvl(fto.sender_rec_info, '''')) AS ""Ben Details"",
       CAST(to_char(ref.process_name) AS VARCHAR2(1000)) AS ""WorkStage"",
       to_char(ref.cbu_user) AS ""Completed By""
  FROM mashreqibps.mrq_cpc_fto_exttable fto
 inner join mashreqibps.mrq_referral_exttable ref
    ON fto.wi_name = ref.parent_wi_number
 WHERE (gov_contract_ref_no = ''~ReferenceNum~'' or fto.wi_name = ''~ReferenceNum~'') and
 ref.parent_activity_name in (''CBU'', ''Credit_Ops'', ''Credit Ops'')
ORDER  BY 1"


', N'mrq_cpc_fto_exttable,
mrq_referral_exttable,
USR_0_TAT_REPORT_VIEW2, 
mrq_cpc_fto_exttable ')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Trecker', N'fto-tat', N'"select decode(tat.entry_date_time,
              null,
              decode(fto.currworkstepname,
              ''Data Entry'',fto.data_entry_entry_datetime,
              ''Checker'',fto.checker_entry_datetime,
              ''EDD Checker'',fto.edd_checker_entry_time,
              ''EDD Maker'',fto.edd_maker_entry_time,
              ''EDD_Compliance'',fto.edd_compliance_entry_date,
              ''ImageQC'',fto.imageqc_entry_datetime,
              ''Processing_Verify'',fto.process_verify_entry_datetime,
              ''Refferal Queue'',fto.refferal_queue_entry_datetime,
              ''Supervisor_Branch'',fto.super_branch_entry_datetime,
              ''Supervisor_CreditOps'',fto.super_creditops_entry_datetime,
              ''Duplicate'',fto.duplicate_entry_datetime,
              ''Business_Compliance'',fto.business_compliance_entry),
              tat.entry_date_time) as ""Landing Time"",
       (case nvl(tat.activity_name, N'''')
         when N''Branch_Checker'' then N''Branch Checker Authorization''
         when N''Business_Compliance'' then N''Compliance Ops Referral''
         when N''CBU'' then N''Call Back - Referral''
         when N''CBU_BRANCH'' then N''Call Back - Branch Referral''
         when N''Central_Compliance'' then N''HO Compliance Review''
         when N''Checker'' then N''Checker Processing''
         when N''Credit Ops'' then N''Credit Refferal Maker''
         when N''Credit_Ops_Checker'' then N''Credit Referral Checker''
         when N''CSU'' then N''Customer Service Maker''
         when N''CSU_Checker'' then N''Customer Service Checker''
         when N''Data Entry'' then N''Data Entry''
         when N''Data Verification'' then N''Data Verification''
         when N''DataEntry'' then N''Data Entry''
         when N''Discarded Items'' then N''Work Item Rejected''
         when N''Discrepancy_CallBack'' then N''Referred for Discrepancy''
         when N''Image_QC'' then N''Scanned Image QC''
         when N''ImageQC'' then N''Scanned Image QC''
         when N''OCR_Extraction'' then N''OCR Extraction''
         when N''OCR_Registration'' then N''OCR Registration''
         when N''Processing_Verify'' then N''Maker Processing''
         when N''Refferal Queue'' then N''Referred for discrepancy''
         when N''Supervisor_CreditOps'' then N''Credit Refferal Second Checker''
         when N''Supervisor_Branch'' then N''Branch Refferal Second Checker''
         when N''Team Leader'' then N''Second Checker Authorization''
         when N''EDD Maker'' then N''EDD Maker''
         when N''EDD Checker'' then N''EDD Checker''
         when N''EDD_Compliance'' then N''EDD Compliance''
         when N''Refferal Queue'' then N''Refferal Queue''
         when N''Duplicate'' then N''Duplicate Queue''
         when N''Business_Compliance'' then N''Business Compliance Queue''
         else q.ACTIVITYNAME
       end) || '''' || (case
         when fto.preworkstepname = tat.activity_name then
          decode(fto.decision,
          ''Reject'','' - Reject'',
          ''Rejected'','' - Reject'',
          ''Decline'','' - Decline'', '''')
         else '''' end) as ""Activity"",
       tat.exit_date_time as ""Completion Time"",
       (case when tat.exit_date_time is null then ''Pending'' else ''Completed'' end) as ""Activity Status"",
       fto.wi_name as ""EDMS ref no"",
       decode(fto.currency, ''--Select--'', '''', fto.currency) as currency,
       fto.amount,
       to_char(fto.value_date, ''dd-MM-yyyy'') AS ""Value Date"",
       fto.appl_account_no as ""Debit A/C"",
       (nvl(fto.flx_ben_swift_code || '', '', '''') ||
       nvl(fto.flx_ben_bank_name || '', '', '''') ||
       nvl(fto.intermediary_inst, '''')) as ""Receiver"",
       fto.bene_account_no as ""Beneficiary A/C"",
       nvl(fto.flx_ben_country || '', '', '''') ||
       nvl(fto.flx_ben_city || '', '', '''') ||
       nvl(fto.flx_ben_full_name || '', '', '''') ||
       nvl(fto.remittance_info || '', '', '''') || nvl(fto.sender_rec_info, '''') as ""Ben Details"",
       decode(tat.activity_name, null, q.ACTIVITYNAME, tat.activity_name) as ""WorkStage"",
       tat.processed_by as ""Completed By"",
       null as ""pickup Time"", /*tat.pickuptime*/
       (case
         when fto.currworkstepname in
              (''Business_Compliance'', /*''Checker'', ''Data Entry'', ''EDD Checker'', ''EDD Maker'', ''EDD_Compliance'', ''ImageQC'', ''Processing_Verify'', ''Duplicate'',*/
               ''Refferal Queue'', ''Supervisor_Branch'', ''Supervisor_CreditOps'') then ''Y''
 	 when fto.currworkstepname = ''Data Entry'' and fto.data_entry_entry_datetime is not null then ''Y''
 	 when fto.currworkstepname = ''Checker'' and fto.checker_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''EDD Checker'' and fto.edd_checker_entry_time is not null then ''Y''
	 when fto.currworkstepname = ''EDD Maker'' and fto.edd_maker_entry_time is not null then ''Y''
	 when fto.currworkstepname = ''EDD_Compliance'' and fto.edd_compliance_entry_date is not null then ''Y''
	 when fto.currworkstepname = ''ImageQC'' and fto.imageqc_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Processing_Verify'' and fto.process_verify_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Refferal Queue'' and fto.refferal_queue_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Supervisor_Branch'' and fto.super_branch_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Supervisor_CreditOps'' and fto.super_creditops_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Duplicate'' and fto.duplicate_entry_datetime is not null then ''Y''
	 when fto.currworkstepname = ''Business_Compliance'' and fto.business_compliance_entry is not null then ''Y''
         when fto.refferal_queue_entry_datetime is not null then  ''Y''
         when fto.CREDIT_OPS_ENTRY_DATETIME is not null then ''Y''
         when fto.call_back_required = ''Y'' then ''Y''
         when fto.call_back_required = ''Yes'' then ''Y''
         when fto.call_back_required = ''No'' then ''N''
         when fto.call_back_required = ''N'' then ''N''
         when fto.call_back_required = ''ByPass'' then ''Y''
         when fto.call_back_required in null then ''N''
         when fto.call_back_required in null then ''N''
         else ''Y'' end) as ""Governance Check""
  from ~Owner~.mrq_cpc_fto_exttable fto
  LEFT JOIN ~Owner~.USR_0_TAT_REPORT_VIEW2 tat on tat.process_instance_id = fto.wi_name
   and tat.activity_name not in (''Archive'',
                                 ''Auto Upload Advice'',
                                 ''Branch'', /*''Duplicate'',*/
                                 ''Log_cash_Mgmt'',
                                 ''OCR_Extraction'',
                                 ''OCR_Registration'',
                                 ''Original Tracking'',
                                 ''Other_Sys_Checker'',
                                 ''Rescan'',
                                 ''WNS Queue'')
 inner join ~Owner~.QUEUEVIEWTRK q on fto.wi_name = q.processinstanceid
 where fto.wi_name =  ''~ReferenceNum~''
/*and q.introductiondatetime > sysdate - (~RetrieveTill~+1)
order by tat.entry_date_time asc*/"


', N'mrq_cpc_fto_exttable,
USR_0_TAT_REPORT_VIEW2,
QUEUEVIEWTRK ')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Trecker', N'rid-tat', N'"SELECT     tat.entry_date_time AS ""Landing Time"", ( 
            CASE tat.activity_name
                       WHEN N''Introduction'' THEN N''New case raised''
                       WHEN N''Branch Processing'' THEN N''Referred to Branch''
                       WHEN N''OUTSTANDING'' THEN N''Marked to awaiting Queue''
                       WHEN N''Closer'' THEN N''Marked for closure''
                       WHEN N''Branch_Checker'' THEN N''Branch Checker Authorization''
                       WHEN N''Business_Compliance'' THEN N''Compliance Ops Referral'' 
                       WHEN N''RCU'' THEN N''Call Back - Referral''
                       WHEN N''CBU_BRANCH'' THEN N''Call Back - Branch Referral'' 
                       WHEN N''Checker'' THEN N''Checker Processing'' 
                       WHEN N''Data Entry'' THEN N''Work Item Entry'' 
                       WHEN N''Data Verification'' THEN N''Work Item Entry'' 
                       WHEN N''Image_QC'' THEN N''Branch Processing'' 
                       WHEN N''ImageQC'' THEN N''Branch Processing'' 
                       WHEN N''Processing_Verify'' THEN N''Branch Processing'' 
                       WHEN N''Refferal Queue'' THEN N''Referred for discrepancy'' 
                       WHEN N''Team Leader'' THEN N''Second Checker Authorization'' 
                       WHEN N''TL_Manager_Approval'' THEN N''Manager Approval''
                       WHEN N''Logging'' THEN N''Case Logged''
                       WHEN N''Action_Correspnc'' THEN N''Action reply to correspondent bank''
                       ELSE tat.activity_name
            END) ||'' |Type:''||nvl(rid.problem_type, '''') AS ""Activity"", 
            tat.exit_date_time AS ""Completion Time"", ( 
            CASE 
                       WHEN tat.exit_date_time IS NULL THEN ''Pending'' 
                       ELSE ''Completed'' 
            END )       AS ""Activity Status"", 
            rid.wi_name AS ""EDMS ref no"", 
            rid.currency, 
            rid.amount, 
            tat.activity_name AS ""WorkStage"", 
            tat.processed_by  AS ""Completed By"", 
            tat.pickuptime    AS ""pickup Time"" 
 FROM  mashreqibps.mrq_cpc_rid_exttable rid, mashreqibps.USR_0_TAT_REPORT_VIEW2 tat 
 WHERE rid.wi_name = tat.process_instance_id 
 AND ( (''INQ''||substr(rid.wi_name, 16, 6)= ''~ReferenceNum~'') OR
      (rid.processing_info =''~ReferenceNum~'') OR (rid.transaction_ref =''~ReferenceNum~'') or (rid.ref_wi_id =''~ReferenceNum~''))
 AND tat.activity_name NOT IN (''Archive'', ''Auto Upload Advice'', ''Branch'', ''Duplicate'', ''Log_cash_Mgmt'', ''OCR_Extraction'', 
 ''OCR_Registration'', ''Original Tracking'', ''Other_Sys_Checker'', ''Rescan'', ''WNS Queue'')"


', N'mrq_cpc_rid_exttable,
USR_0_TAT_REPORT_VIEW2 ')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'EDMS FTO Details', N'edms-fto', N'"select 
ACCOUNT_TITLE,
       AED_EQUIVALENT_AMOUNT,
       cast(APPLICATION_RECEIVED_TIME as timestamp) as ""APPLICATION_RECEIVED_TIME "",
       BENE_ACCOUNT_NO,
       BENEFICIARY_FULL_NAME,
       decode(CALL_BACK_DONE, ''--Select--'', ''NA'', CALL_BACK_DONE) as CALL_BACK_DONE,
       decode(CHECKER_EXCHANGE_RATE, ''--Select--'', '''', CHECKER_EXCHANGE_RATE) AS CHECKER_EXCHANGE_RATE,
       CHINESE_ACC_FLAG,
       CIF_ID,
       CIF_NO,
       COMMENTS,
       CORE_REF,
       COVER_ACC,
       CSU_CHECKER_COMMENTS,
       CURRENCY_DEADLINE,
       CUSTOMER_RELATIONSHIP_TYPE,
       CUSTOMER_SEGMENT,
       DEAL_DATE,
       decode(DEAL_NODEAL, ''--Select--'', '''', DEAL_NODEAL) as DEAL_NODEAL,
       DEALS_DETAIL,
       DEBIT_AC_CURRENCY,
       DEBIT_AMOUNT,
       DEC_CHECKER,
       DEC_DATA_ENTRY,
       decode(DEC_PROCESSING, ''--Select--'', '''', DEC_PROCESSING) as DEC_PROCESSING,
       DECISION,
       DRAWING_CODE,
       DUPLICATE,
       EXCHANGE_RATE,
       FLX_BANK_ADDRESS1,
       FLX_BANK_ADDRESS2,
       FLX_BANK_ADDRESS3,
       FLX_BEN_BANK_NAME,
       FLX_BEN_SWIFT_CODE,
       decode(FLX_CHARGES, ''--Select--'', '''', FLX_CHARGES) as FLX_CHARGES,
       FLX_DEAL_REFERENCE,
       decode(FLX_TRAC_COD, ''--Select--'', '''', FLX_TRAC_COD) as FLX_TRAC_COD,
       HIDDEN_CREDIT_AMOUNT,
       decode(HIDDEN_CREDIT_CURRENCY, ''--Select--'', '''', HIDDEN_CREDIT_CURRENCY) as HIDDEN_CREDIT_CURRENCY,
       HIDDEN_DEBIT_NUMBER,
       HOST,
       INTERMEDIARY_INST,
       INTRODUCTION_DATE_TIME,
       JOB_REC_ID,
       LOB1,
       MAIL_FLAG,
       PREWORKSTEPNAME,
       PROCESS,
       decode(PROCESSOR_EXCHANGE_RATE, ''--Select--'', '''', PROCESSOR_EXCHANGE_RATE) as PROCESSOR_EXCHANGE_RATE,
       QAMOUNT,
       REMITTANCE_INFO,
       SEGMENT,
       SENDER_REC_INFO,
       SOURCE_OF_APPL,
       TRANSACTION_ID,
       TYPE_OF_PAYMENT,
       VALIDATION_RESULT,
       VALUE_DATE,
       VVIP,
       WI_NAME,
       -- new fields
       decode(CURRENCY, ''--Select--'', '''', CURRENCY) as CREDIT_CURRENCY,
       AMOUNT as CREDIT_AMOUNT,
       DEBIT_AC_CURRENCY as DEBIT_CURRENCY,
       DEBIT_AMOUNT as DEBIT_AMOUNT,       
       APPLICATION_DATE,
       APPL_ACCOUNT_NO,
       APPL_BRANCH_CODE,
       CURRWORKSTEPNAME,
       FLX_BEN_FULL_NAME,
       -- EDD coreref contract
       GOV_CONTRACT_REF_NO
       -- new fields       
  FROM ~Owner~.mrq_cpc_fto_exttable
WHERE wi_name =  ''~ReferenceNum~''"


', N'mrq_cpc_fto_exttable')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'Payment Trecker', N'edd-referral', N'"/*with inprogress as
 (SELECT (CASE fto.currworkstepname
           WHEN ''Business_Compliance'' THEN fto.BUSINESS_COMPLIANCE_ENTRY
           WHEN ''Checker'' THEN fto.CHECKER_ENTRY_DATETIME
           when ''Data Entry'' then fto.DATA_ENTRY_ENTRY_DATETIME
           when ''EDD Checker'' then fto.EDD_CHECKER_ENTRY_TIME
           when ''EDD Maker'' then fto.EDD_MAKER_ENTRY_TIME
           when ''EDD_Compliance'' then fto.EDD_COMPLIANCE_ENTRY_DATE
           when ''ImageQC'' then fto.IMAGEQC_ENTRY_DATETIME
           when ''Processing_Verify'' then fto.PROCESS_VERIFY_ENTRY_DATETIME
           when ''Refferal Queue'' then fto.REFFERAL_QUEUE_ENTRY_DATETIME
           when ''Supervisor_Branch'' then fto.SUPER_BRANCH_ENTRY_DATETIME
           when ''Supervisor_CreditOps'' then fto.SUPER_CREDITOPS_ENTRY_DATETIME
           when ''Duplicate'' then fto.DUPLICATE_ENTRY_DATETIME
         END) AS Landing_Time,
         fto.currworkstepname as activity,
         null AS Completion_time,
         ''Pending'' AS Activity_Status,
         fto.wi_name AS Reference_No,
         decode(fto.currency, ''--Select--'', '''', fto.currency) as currency,
         fto.amount,
         To_char(fto.value_date, ''dd-MM-yyyy'') AS Value_Date,
         fto.appl_account_no AS Debit_ac,
         (Nvl(fto.flx_ben_swift_code || '', '', '''') || Nvl(fto.flx_ben_bank_name || '', '', '''') || Nvl(fto.intermediary_inst, '''')) AS Receiver,
         fto.bene_account_no AS Beneficiary_ac,
         (Nvl(fto.flx_ben_country || '', '', '''')  || Nvl(fto.flx_ben_city || '', '', '''') || Nvl(fto.flx_ben_full_name || '', '', '''') ||
         Nvl(fto.remittance_info || '', '', '''') || Nvl(fto.sender_rec_info, '''')) AS Ben_Details,
         fto.currworkstepname AS WorkStage,
         '''' AS Completed_By,
         (CASE fto.currworkstepname
           WHEN ''Business_Compliance'' THEN fto.BUSINESS_COMPLIANCE_EXIT
           WHEN ''Checker'' THEN fto.CHECKER_EXIT_DATETIME
           when ''Data Entry'' then fto.DATA_ENTRY_EXIT_DATETIME
           when ''EDD Checker'' then fto.EDD_CHECKER_EXIT_TIME
           when ''EDD Maker'' then fto.EDD_MAKER_EXIT_TIME
           when ''EDD_Compliance'' then fto.EDD_COMPLIANCE_EXIT_DATE
           when ''ImageQC'' then fto.IMAGEQC_EXIT_DATETIME
           when ''Processing_Verify'' then fto.PROCESS_VERIFY_EXIT_DATETIME
           when ''Refferal Queue'' then fto.REFFERAL_QUEUE_EXIT_DATETIME
           when ''Supervisor_Branch'' then fto.SUPERV_BRANCH_EXIT_DATETIME
           when ''Supervisor_CreditOps'' then fto.SUPER_CREDITOPS_EXIT_DATETIME
           when ''Duplicate'' then fto.DUPLICATE_EXIT_DATETIME  END) as exit_time
    FROM mashreqibps.mrq_cpc_fto_exttable fto
   WHERE (fto.wi_name = ''~ReferenceNum~'' or fto.gov_contract_ref_no = ''~ReferenceNum~'') )
select landing_time    as ""Landing Time"",
       activity        as ""Activity"",
       completion_time as ""Completion Time"",
       activity_status as ""Activity Status"",
       reference_no    as ""EDMS ref no"",
       currency,
       amount,
       value_date      AS ""Value Date"",
       debit_ac        as ""Debit A/C"",
       receiver        as ""Receiver"",
       beneficiary_ac  as ""Beneficiary A/C"",
       ben_details     as ""Ben Details"",
       workstage       as ""WorkStage"",
       completed_by    as ""Completed By""
  from inprogress
 where ((exit_time is null) or (landing_time > exit_time))
   and activity in (''Business_Compliance'', ''Checker'', ''Data Entry'', ''EDD Checker'', ''EDD Maker'', ''EDD_Compliance'', ''ImageQC'', ''Processing_Verify'', ''Duplicate'',
                    ''Refferal Queue'',  ''Supervisor_Branch'', ''Supervisor_CreditOps'')
   and (select count(1) from mashreqibps.USR_0_TAT_REPORT_VIEW tat where tat.process_instance_id = inprogress.Reference_No and tat.activity_name <> inprogress.activity) >= 1
union all*/
SELECT (CASE
         WHEN parent_activity_name = ''CBU'' THEN fto.cbu_entry_datetime
         WHEN parent_activity_name = ''Credit_Ops'' THEN fto.credit_ops_entry_datetime
         WHEN parent_activity_name = ''Credit Ops'' THEN fto.credit_ops_entry_datetime
       END) AS ""Landing Time"",
       (CASE
          WHEN parent_activity_name = ''CBU'' THEN ''Call Back''
         WHEN parent_activity_name = ''Credit_Ops'' THEN ''Credit Referral''
         WHEN parent_activity_name = ''Credit Ops'' THEN ''Credit Referral''
       END) AS ""Activity"",
       (CASE
         WHEN parent_activity_name = ''CBU'' AND fto.cbu_exit_datetime is NULL THEN sysdate
         WHEN parent_activity_name = ''CBU'' AND fto.cbu_exit_datetime is NOT NULL THEN fto.cbu_exit_datetime
         WHEN parent_activity_name = ''Credit_Ops'' and fto.credit_ops_exit_datetime is null then  sysdate
         WHEN parent_activity_name = ''Credit_Ops'' and fto.credit_ops_exit_datetime is not null THEN fto.credit_ops_exit_datetime
         WHEN parent_activity_name = ''Credit Ops'' and fto.credit_ops_exit_datetime is null then sysdate
         WHEN parent_activity_name = ''Credit Ops'' and fto.credit_ops_exit_datetime is not null THEN fto.credit_ops_exit_datetime
       END) AS ""Completion_time"",
       nvl(to_char(ref.decision), ''Pending'') AS ""Activity Status"",
       fto.wi_name AS ""Reference No"",
       decode(fto.currency, ''--Select--'', '''', fto.currency) as currency,
       fto.amount,
       To_char(fto.value_date, ''dd-MM-yyyy'') AS ""Value Date"",
       fto.appl_account_no AS ""Debit A/C"",
       (Nvl(fto.flx_ben_swift_code || '', '', '''') || Nvl(fto.flx_ben_bank_name || '', '', '''') || Nvl(fto.intermediary_inst, '''')) AS ""Receiver"",
       fto.bene_account_no AS ""Beneficiary A/C"",
       (Nvl(fto.flx_ben_country || '', '', '''')  || Nvl(fto.flx_ben_city || '', '', '''') || Nvl(fto.flx_ben_full_name || '', '', '''') ||
       Nvl(fto.remittance_info || '', '', '''') || Nvl(fto.sender_rec_info, '''')) AS ""Ben Details"",
       to_char(ref.process_name) AS ""WorkStage"",
       to_char(ref.cbu_user) AS ""Completed By""
  FROM mashreqibps.mrq_cpc_fto_exttable fto
 inner join mashreqibps.mrq_referral_exttable ref
    ON fto.wi_name = ref.parent_wi_number
 WHERE (gov_contract_ref_no = ''~ReferenceNum~'' or fto.wi_name = ''~ReferenceNum~'') and
 ref.parent_activity_name in (''CBU'', ''Credit_Ops'', ''Credit Ops'')
ORDER  BY 1"


', N'mrq_referral_exttable, 
mrq_cpc_fto_exttable ,
USR_0_TAT_REPORT_VIEW ,
mrq_cpc_fto_exttable ')
INSERT [dbo].[EDMS_QUERY] ([REPORT_NAME], [QUERY_KEY], [QUERY], [TABLE_NAME]) VALUES (N'EDMSEDDDetails', N'edms-edd-details', N'"select b.wi_name,
       b.gov_contract_ref_no,
       b.transaction_id,
       b.edd_maker_entry_time,
       b.edd_maker_exit_time,
       b.edd_checker_entry_time,
       b.edd_checker_exit_time,
       b.edd_referral_entry_time,
       b.edd_referral_exit_time,
       b.currworkstepname,
       b.pf_customer_segment,
       b.pf_decision as Checker_Decision,
       nvl2(b.appl_account_no, b.appl_account_no, b.gov_ben_account_no) as appl_account_no,
       Nvl2(B.Account_Title, B.Account_Title, b.Gov_Ben_Name) As Account_Title,
       Nvl2(B.Amount, B.Amount, Gov_Transaction_Amount) As Amount,
       Nvl2(B.Cif_Id, B.Cif_Id, Gov_Cif) As Cif_Id,
       B.Bene_Account_No As ""Bene Acc No(Scanned)"",
       B.Gov_Ben_Account_No As ""Bene Acc No(MBOL)"",
       B.Flx_Ben_Full_Name,
       B.Gov_Ben_Name,
       B.Swift_Code,
       B.Flx_Ben_Swift_Code,
       B.Gov_Payment_Details,
       nvl2(b.transfer_type, b.transfer_type, b.type_of_payment) As Transfer_Type,
       B.Edd_Chk_Cmnts EDD_Checker_Comments,
       b.workstep_comments EDD_WStps_Comments,
       b.transaction_type,
       b.gov_question1 EDD_GOVCheck_Comments,
       b.gov_answer1 EDD_GOVCheck_Response, 
       b.pf_customer_segment,
       decode(b.rbg_reason, ''--Select--'', '''', b.rbg_reason) rbg_reason,
       b.rbg_reason_array,
       b.document_upload_source,
       b.document_upload_status,
       b.doc_name, b.doc_added    
  from mashreqibps.mrq_cpc_fto_exttable b
 where (b.pf_flag = ''S'' Or B.Gov_Edms_Flag = ''S'')
  and b.edd_maker_entry_time >= sysdate-90
   and (b.gov_contract_ref_no = ''~ReferenceNum~'' or b.transaction_id = ''~ReferenceNum~'' or instr(b.wi_name, ''~ReferenceNum~'')>0 )"


', N'mrq_cpc_fto_exttable')
GO
