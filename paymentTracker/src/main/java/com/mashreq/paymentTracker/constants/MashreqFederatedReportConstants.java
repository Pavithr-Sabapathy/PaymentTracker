package com.mashreq.paymentTracker.constants;

import java.util.ArrayList;
import java.util.List;

public class MashreqFederatedReportConstants {
	public static final List<String> INCOMING_PAYMENT_CODES_LIST = new ArrayList<String>();
	public static final List<String> OUTGOING_PAYMENT_CODES_LIST = new ArrayList<String>();
	static {
		INCOMING_PAYMENT_CODES_LIST.add("O 103");
		INCOMING_PAYMENT_CODES_LIST.add("O 102");
		INCOMING_PAYMENT_CODES_LIST.add("O 202");
		INCOMING_PAYMENT_CODES_LIST.add("O 203");
		INCOMING_PAYMENT_CODES_LIST.add("O 200");

		OUTGOING_PAYMENT_CODES_LIST.add("I 103");
		OUTGOING_PAYMENT_CODES_LIST.add("I 102");
		OUTGOING_PAYMENT_CODES_LIST.add("I 202");
		OUTGOING_PAYMENT_CODES_LIST.add("I 203");
		OUTGOING_PAYMENT_CODES_LIST.add("I 200");

	}

	/*** Report Name **/
	public static final String FLEX_REPORT_NAME = "flexPostingDetails";
	public static final String SWIFT_REPORT_NAME = "swiftDetails";
	public static final String EDMS_REPORT_NAME = "edms";
	public static final String UAEFTS_REPORT_NAME = "UAEFTSDetails";
	public static final String ADVANCE_SEARCH_REPORT_NAME = "advanceSearch";
	public static final String SNAPP_DETAILS = "SnappDetails";
	/** Metrics and prompts constant **/
	public static final String METRIC = "M";
	public static final String PROMPT = "P";
	public static final String ACCOUNTINGSOURCEPROMPTS = "AccountingSource";
	public static final String REFERENCENUMPROMPTS = "ReferenceNum";
	public static final String RELATEDACCOUNTPROMPTS = "RelatedAccount";
	public static final String MESSAGETYPEPROMPTS = "mesgType";
	/** Prepared Statement Constants **/
	public static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=PaymentTracker;encrypt=true;trustServerCertificate=true";
	public static final String FLEX_DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=Flex;encrypt=true;trustServerCertificate=true";
	public static final String SWIFT_DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=Swift;encrypt=true;trustServerCertificate=true";
	public static final String DATABASE_USERNAME = "TestLogin";
	public static final String DATABASE_PASSWORD = "sample@12345";

	/** Swift detail prompts constants **/
	public static final String AID_PROMPT_KEY = "aid";
	public static final String S_UMIDH_PROMPT_KEY = "s_umidh";
	public static final String S_UMIDL_PROMPT_KEY = "s_umidl";
	public static final String SWIFT_DETAILED_REPORT_TYPE_PROMPT_KEY = "SWIFTDetailedType";
	public static final String MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY = "ReferenceNum";
	public static final String MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY = "MessageType";
	public static final String MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY = "MessageSubFormat";
	public static final String MESSAGE_DETAILS_CORR_BANK_PROMPT_KEY = "CorrBank";
	public static final String MESSAGE_DETAILS_MESSAGE_THROUGH_PROMPT_KEY = "MessageThrough";

	public static final String DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RMESG = "RMESG";
	public static final String DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RINTV = "RINTV";
	public static final String MESSAGE_THROUGH_SWIFT = "SWIFT";
	public static final String MESSAGE_THROUGH_UAEFTS = "UAEFTS";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RMESG = "msg-rmesg";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RINTV = "msg-rintv";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RTEXTFIELD = "msg-rtextfield";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_STX_MESSAGE = "msg-stx-message";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RCORR = "msg-rcorr";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_STX_ENTRY_FIELD_VIEW = "msg-stx-entry-field-view";

	public static final String MESSAGE_DETAILS_UAEFTS_COMPONENT_KEY = "uaefts";
	public static final String MESSAGE_DETAILS_SWIFT_COMPONENT_KEY = "swift";
	public static final String PROCESSING_SYSTEM_MESSAGE = "Processing System : ";

	public static final String SWIFT_RMESG = "rmesg";
	public static final String SWIFT_TRN_REF_MESG = "rmesg-trn-ref-only";
	public static final String SWIFT_RTEXTFIELD = "rtextfield";
	public static final String SWIFTINTVDETECTION = "rintv-detection";
	public static final String SWIFTINTV = "rintv";

	public static final String UAEFTS_AML_FTSMSGS_KEY = "uaefts-aml-ftsmsgs";
	public static final String UAEFTS_MTQA_KEY = "uaefts-mtqa";
	public static final String UAEFTS_MANUALMSGS_DETAILS_KEY = "manualmsgs-details";
	public static final String UAEFTS_MT202_KEY = "uaefts-mt202";
	public static final String UAEFTS_MT202_INPUTMSGS_DETAILS_KEY = "uaefts-mt202-inputmsgs-details";
	public static final String UAEFTS_MTINPUTMSGS_DETAILS_KEY = "uaefts-mtinputmsgs-details";
	public static final String UAEFTS_AML_MTQA_KEY = "uaefts-aml-mtqa";
	public static final String UAEFTS_FTSMSGS_KEY = "uaefts-ftsmsgs";
	public static final String UAEFTS_INCOMING_MTFN_KEY = "uaefts-incoming-mtfn";
	public static final String UAEFTS_AML_MTINPUTMSGS_DETAILS_KEY = "uaefts-aml-mtinputmsgs-details";
	public static final String UAEFTS_AML_INCOMING_MTFN_KEY = "uaefts-aml-incoming-mtfn";
	public static final String UAEFTS_INCOMING_CCN_KEY = "uaefts-incoming-ccn";
	public static final String UAEFTS_FORMAT_ACTION_REPAIR_STATUS = "R";

	public static final String UAEFTS_OUTGOING_MTFN_KEY = "uaefts-outgoing-mtfn";

	public static final String ADVANCE_SEARCH_FROM_DATE_PROMPT_KEY = "FromDate";
	public static final String ADVANCE_SEARCH_TO_DATE_PROMPT_KEY = "ToDate";
	public static final String ADVANCE_SEARCH_ACCOUNT_NUMBER_PROMPT_KEY = "AccountNumber";
	public static final String ADVANCE_SEARCH_CURRENCY_PROMPT_KEY = "Currency";
	public static final String ADVANCE_SEARCH_AMOUNT_BETWEEN_PROMPT_KEY = "AmountFrom";
	public static final String ADVANCE_SEARCH_AMOUNT_TO_PROMPT_KEY = "AmountTo";
	public static final String ADVANCE_SEARCH_TRANSACTION_STATUS_PROMPT_KEY = "TransactionStatus";
	public static final String ADVANCE_SEARCH_TRANSACTION_REF_NUM_PROMPT_KEY = "TransactionReferenceNo";
	public static final String ADVANCE_SEARCH_REPORT_TRANSACTION_REJECT_STATUS = "Rejected";
	public static final String ADVANCE_SEARCH_REPORT_TRANSACTION_CREDIT_CONFIRMED_STATUS = "Credit Confirmed";
	public static final String ADVANCE_SEARCH_MESSAGE_THROUGH_UAEFTS = "UAEFTS";
	public static final String ADVANCE_SEARCH_INITATION_SOURCE_FLEX = "FLEX";
	public static final String ADVANCE_SEARCH_INITATION_SOURCE_MATRIX = "MATRIX";
	public static final String ADVANCE_SEARCH_REPORT_TRANSACTION_STATUS_PROMPT_DEFAULT_VALUE = "All";
	public static final String MESSAGE_INPUT_SUB_FORMAT = "INPUT";
	public static final String MESSAGE_OUTPUT_SUB_FORMAT = "OUTPUT";
	public static final String MESSAGE_INPUT_SUB_FORMAT_INITIAL = "I";
	public static final String MESSAGE_OUTPUT_SUB_FORMAT_INITIAL = "O";

	public static final String ADVANCE_SEARCH_FLEX_COMPONENT_KEY = "flex";
	public static final String ADVANCE_SEARCH_EDMS_COMPONENT_KEY = "edms";
	public static final String ADVANCE_SEARCH_MATRIX_COMPONENT_KEY = "matrix";
	public static final String ADVANCE_SEARCH_UAEFTS_COMPONENT_KEY = "uaefts";
	public static final String ADVANCE_SEARCH_UAEFTS_CCN_KEY = "uaefts-ccn";

	public static final String SNAPP_MWLOG_DETAIL_KEY = "mwlog-detail";
	public static final String MOL_AUTH_DATA_DETAIL_KEY = "auth-data-detail";
	public static final String SNAPP_MWLOG = "mwlog";
	public static final String MOL_AUTH_DATA = "authdata";
	public static final String GPI_ENABLED_IPALA_CODE = "IPLAGPIA";

	public static final String COMPLETED_ACTIVITY_STATUS = "Completed";
	public static final String RINTV_MESG_LIVE = "Pending";
	public static final String RINTV_MESG_ACK = "ACK";
	public static final String RINTV_MESG_NACK = "NACK";
	public static final String RINTV_MPFN_SI_TO_SWIFT = "_SI_TO_SWIFT";
	public static final String RINTV_NAME_INSTANCE_COMPLETED = "Instance completed";
	public static final String RAPPE_NETWORK_DELIVERY_STATUS_ACKED = "DLV_ACKED";
	public static final String RINTV_NAME_INSTANCE_CREATED = "Instance created";
	public static final String RINTV_NAME_INSTANCE_ROUTED = "Instance routed";
	public static final String RINTV_NAME_AUTHORIZATION_NOT_PRESENT = "Authorisation not present";
	public static final String RAPPE_NETWORK_DELIVERY_STATUS_NACKED = "DLV_NACKED";

	public static final String TILDE = "~";
	public static final String COMMA = ",";
	public static final String THREE_HASH_NOTATION = "###";
	public static final String BREAK_TAG = "<br/>";
	public static final String GPI_ENABLED_TRCH_CODE = "TRCKCHZ";

	public static final String FIELD_DESCRIPTION = "Field Description";
	public static final String FIELD_VALUE = "Field Value";

	public static final String PAYMENT_STATUS_DEFAULT = "NO STATUS";
	public static final String PAYMENT_STATUS_COMPLETED = "Completed";
	public static final String PAYMENT_STATUS_COMPLETED_CODE = "ACSC";
	public static final String PAYMENT_STATUS_RETURNED = "Returned";
	public static final String PAYMENT_STATUS_RETURNED_CODE = "RJCT";
	public static final String PAYMENT_STATUS_FORWARDED_GPI_BANK = "Forwarded to gpiBank";
	public static final String PAYMENT_STATUS_FORWARDED_GPI_BANK_CODE = "ACSP/000";
	public static final String PAYMENT_STATUS_FORWARDED_NON_GPI_BANK = "Forwarded to NON-gpiBank";
	public static final String PAYMENT_STATUS_FORWARDED_NON_GPI_BANK_CODE = "ACSP/001";
	public static final String PAYMENT_STATUS_IN_PROGRESS = "In-progress";
	public static final String PAYMENT_STATUS_IN_PROGRESS_CODE = "ACSP/002";
	public static final String PAYMENT_STATUS_AWAITING_DOCUMENTS = "Awaiting Documents";
	public static final String PAYMENT_STATUS_AWAITING_DOCUMENTS_CODE = "ACSP/003";
	public static final String PAYMENT_STATUS_AWAITING_FUNDS = "Awaiting Funds";
	public static final String PAYMENT_STATUS_AWAITING_FUNDS_CODE = "ACSP/004";
	public static final String PAYMENT_STATUS_FORWARDED_NEXT_GPI = "Payment forward to next gpi Correspondent";
	public static final String PAYMENT_STATUS_FORWARDED_NEXT_GPI_CODE = "ACSP/G000";
	public static final String PAYMENT_STATUS_FORWARDED_NEXT_NON_GPI = "Payment forward to next Nongpi Correspondent";
	public static final String PAYMENT_STATUS_FORWARDED_NEXT_NON_GPI_CODE = "ACSP/G001";
	public static final String PAYMENT_STATUS_PAYMENT_IN_PROGRESS = "Payment In progress";
	public static final String PAYMENT_STATUS_PAYMENT_IN_PROGRESS_CODE = "ACSP/G002";
	public static final String PAYMENT_STATUS_PENDING_DOCUMENTS = "Payment pending documents";
	public static final String PAYMENT_STATUS_PENDING_DOCUMENTS_CODE = "ACSP/G003";
	public static final String PAYMENT_STATUS_AWAITING_CREDIT_COVER = "Payment awaiting credit cover";
	public static final String PAYMENT_STATUS_AWAITING_CREDIT_COVER_CODE = "ACSP/G004";

	public static final String MESSAGE_DETAILS_MESSAGE_CODES_PROMPT_KEY = "MessageCodes";
	public static final String COLON = " : ";
	public static final String MESSAGE_CODE_32A = "32A";
	public static final String MESSAGE_CODE_33B = "33B";
	public static final String DESCRIPTION_LABEL = "Message Description";
	public static final String DELIVERY_STATUS_LABEL = "Network Delivery Status";
	public static final String PRIORITY_LABEL = "Priority/Delivery";
	public static final String INPUT_REFERENCE_LABEL = "Message Input Reference";
	public static final String SWIFT_INPUT_LABEL = "Swift Input";
	public static final String SENDER_LABEL = "Sender";
	public static final String RECEIVER_LABEL = "Receiver";
	public static final String DATE_LABEL = "Date";
	public static final String CURRENCY_LABEL = "Currency";
	public static final String AMOUNT_LABEL = "Amount";
	public static final String SEMI_COLON = " ; ";

	// Data Source names
	public static final String DS_EDMS = "EDMS";
	public static final String DS_FLEX = "Flex";
	public static final String DS_SWIFT = "SWIFT";
	public static final String YES = "Y";
	public static final String NO = "N";

	public static final String EDMS_EDD_DETAILED_REP_COMP = "EDMSEDD DR";
	public static final String BPM_EDD_DETAILED_REP_COMP = "BPMEDD DR";

	public static final String NO_REPORTS = "No Report Found";
	public static final String PENDING_ACTIVITY_STATUS = "Pending";

	public static final String SOURCE_SYSTEM_SNAPP = "SNAPP";
	public static final String SOURCE_SYSTEM_MOL = "MOL";
	public static final String DATE_FORMATS_KEY = "federated-reports.static-values.mashreq.date-formats.format";
	public static final String VALUE_DATE_FORMAT_KEY = "dd-MMM-yyyy";
	public static final String RETRIEVE_TILL_PROMPT_DEFAULT_VALUE = "60";

	public static final String COMPONENT_FLEX_KEY = "flex";
	public static final String COMPONENT_SWIFT_KEY = "swift";
	public static final String COMPONENT_UAEFTS_KEY = "uaefts";
	public static final String COMPONENT_MATRIX_PAYMENT_KEY = "matrix-payment";
	public static final String COMPONENT_MATRIX_PORTAL_KEY = "matrix-portal";
	public static final String COMPONENT_EMDS_KEY = "edms";
	public static final String COMPONENT_SNAPP_KEY = "snapp";

	public static final String PAYMENT_TRACKER_REFERENCE_NUM_PROMPT_KEY = "ReferenceNum";
	public static final String PAYMENT_TRACKER_COUNTRY_CODE_PROMPT_KEY = "CountryCode";
	public static final String PAYMENT_TRACKER_RETRIEVE_TILL_PROMPT_KEY = "RetrieveTill";
	public static final String EDMS_REF_PREFIX = "CPC-00";
	public static final String EDMS_REF_SUFFIX = "-FTO";

	public static final String CUSTOMER_REPORTING_ROLE = "MASHREQ_CUSTOMER_ROLE";
	public static final String CUSTOMER_MATRIX_REPORTING_ROLE = "MASHREQ_MATRIX_CUSTOMER_ROLE";

	public static final String FLEX_SOURCE_SYSTEM = "FLEXCUBE";
	public static final String SWIFT_SOURCE_SYSTEM = "SWIFT";

	public static final String DEBIT_ACCOUNT_SWIFT_CODE = "50";
	public static final String BENEFICARY_ACCOUNT_SWIFT_CODE = "59";
	public static final String PAYMENT_STATUS_CODE = "79";
	public static final String RINTV_MESG_REJECTED_LOCALLY = null;
	public static final String RAPPE_NETWORK_DELIVERY_REJECTED_LOCALLY = null;
	public static final String RINTV_MPFN_MPC = null;
	public static final String RINTV_MPFN_MPM = null;
	public static final String RINTV_MPFN_NONE = null;
	public static final String GATEWAY_PAYMENT_SCREENING_ACTIVITY = null;
	public static final String GATEWAY_PAYMENT_INWARD_NETWORK_ACTIVITY = null;
	public static final String GATEWAY_MESSAGE_OUTGOING_ACTIVITY = null;
	public static final String GATEWAY_MESSAGE_SCREENING_PROCESSED_ACTIVITY = null;
	public static final String SAFEWATCH_DEFAULT_COMPLETEDBY = null;
	public static final String INCOMING_PAYMENT_STATUS_MESSAGE_TYPE = null;
	public static final String OUTGOING_PAYMENT_STATUS_MESSAGE_TYPE = null;
	public static final String GATEWAY_PAYMENT_OUTWARD_NETWORK_ACTIVITY = null;
	public static final String GATEWAY_MESSAGE_INCOMING_ACTIVITY = null;
	public static final String Swift_NACK_RESULT = "swift-wdnack-result";
	public static final String GPI_EXTERNAL_TRCH_STATUS_ACTIVITY = null;
	public static final String GPI_EXTERNAL_IPALA_STATUS_ACTIVITY = null;
	public static final String NACK_ACTIVITY = null;

	public static final String CPC_COMPLIANCE_WORKSTAGE = "CPC Compliance";
	public static final String HO_COMPLIANCE_WORKSTAGE = "HO Compliance";
	public static final String HO_COMPLIANCE_CHECKER_WORKSTAGE = "HO Compliance Checker";
	public static final String CPC_COMPLIANCE_CHECKER_WORKSTAGE = "CPC Checker";

	public static final String COMPLIANCE_DONT_KNOW_STATUS = "Under Review with Compliance";
	public static final String SOURCE_SYSTEM_UAEFTS = "uaefts";

	public static final String GATEWAY_PAYMENT_SCREENING_PROCESSED_ACTIVITY = "Payment screening processed";

	public static final String UAEFTS_AML_MANUALMSGS_DETAILS_KEY = "uaefts-aml-manualmsgs-details";
	public static final String UAEFTS_AML_MT202_KEY = "uaefts-aml-mt202";
	public static final String UAEFTS_AML_MT202_INPUTMSGS_DETAILS_KEY = "uaefts-aml-mt202-inputmsgs-details";

	public static final String CBUNIQUE_FILE_ID_PROMPT_KEY = "cb-unique-file-id";
	public static final String MSG_ID_PROMPT_KEY = "msg-id";

	public static final List<String> COMPLIANCE_DONT_KNOW_STATUS_LIST = new java.util.ArrayList<String>();
}