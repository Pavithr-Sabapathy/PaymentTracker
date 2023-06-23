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

	/***Report Name**/
	public static final String FLEX_REPORT_NAME="flexPostingDetails";
	public static final String SWIFT_REPORT_NAME="swiftDetails";
	public static final String EDMS_REPORT_NAME="";
	
	/** Metrics and prompts constant **/
	public static final String METRIC = "M";
	public static final String PROMPT = "P";
	public static final String ACCOUNTINGSOURCEPROMPTS = "AccountingSource";
	public static final String REFERENCENUMPROMPTS = "ReferenceNum";
	public static final String RELATEDACCOUNTPROMPTS = "RelatedAccount";

	/** Prepared Statement Constants **/
	public static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=PaymentTracker;encrypt=true;trustServerCertificate=true";
	public static final String FLEX_DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=Flex;encrypt=true;trustServerCertificate=true";
	public static final String SWIFT_DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=Swift;encrypt=true;trustServerCertificate=true";
	public static final String DATABASE_USERNAME = "TestLogin";
	public static final String DATABASE_PASSWORD = "Sample";

	/** Swift detail prompts constants **/
	public static final String AID_PROMPT_KEY = "aid";
	public static final String S_UMIDH_PROMPT_KEY = "s_umidh";
	public static final String S_UMIDL_PROMPT_KEY = "s_umidl";
	public static final String SWIFT_DETAILED_REPORT_TYPE_PROMPT_KEY = "SWIFTDetailedType";
	public static final String MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY = "ReferenceNum";
	public static final String MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY = "MessageType";
	public static final String MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY = "MessageSubFormat";
	public static final String MESSAGE_DETAILS_CORR_BANK_PROMPT_KEY = "CorrBank";

	public static final String DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RMESG = "RMESG";
	public static final String DETAILS_MESSAGE_TYPE_PROMPT_VALUE_RINTV = "RINTV";

	public static final String MESSAGE_DETAILS_SWIFT_MSG_RMESG = "msg-rmesg";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RINTV = "msg-rintv";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RTEXTFIELD = "msg-rtextfield";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_STX_MESSAGE = "msg-stx-message";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RCORR = "msg-rcorr";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_STX_ENTRY_FIELD_VIEW = "msg-stx-entry-field-view";

	public static final String ADVANCE_SEARCH_FROM_DATE_PROMPT_KEY = "~FromDate~";
	public static final String ADVANCE_SEARCH_TO_DATE_PROMPT_KEY = "~ToDate~";
	public static final String ADVANCE_SEARCH_ACCOUNT_NUMBER_PROMPT_KEY = "~AccountNumber~";
	public static final String ADVANCE_SEARCH_CURRENCY_PROMPT_KEY = "~Currency~";
	public static final String ADVANCE_SEARCH_AMOUNT_BETWEEN_PROMPT_KEY = "~AmountFrom~";
	public static final String ADVANCE_SEARCH_AMOUNT_TO_PROMPT_KEY = "~AmountTo~";
	public static final String ADVANCE_SEARCH_TRANSACTION_STATUS_PROMPT_KEY = "~TransactionStatus~";
	public static final String ADVANCE_SEARCH_TRANSACTION_REF_NUM_PROMPT_KEY = "~TransactionReferenceNo~";
	public static final String ADVANCE_SEARCH_REPORT_TRANSACTION_REJECT_STATUS = "Rejected";
	public static final String ADVANCE_SEARCH_REPORT_TRANSACTION_CREDIT_CONFIRMED_STATUS = "Credit Confirmed";
	public static final String ADVANCE_SEARCH_MESSAGE_THROUGH_UAEFTS = "UAEFTS";
	public static final String ADVANCE_SEARCH_INITATION_SOURCE_FLEX = "FLEX";
	public static final String ADVANCE_SEARCH_INITATION_SOURCE_MATRIX = "MATRIX";
	public static final String ADVANCE_SEARCH_REPORT_TRANSACTION_STATUS_PROMPT_DEFAULT_VALUE = "All";
	public static final String MESSAGE_INPUT_SUB_FORMAT = "INPUT";
	public static final String MESSAGE_OUTPUT_SUB_FORMAT = "OUTPUT";

	public static final String ADVANCE_SEARCH_FLEX_COMPONENT_KEY = "flex";
	public static final String ADVANCE_SEARCH_EDMS_COMPONENT_KEY = "edms";
	public static final String ADVANCE_SEARCH_MATRIX_COMPONENT_KEY = "matrix";
	public static final String ADVANCE_SEARCH_UAEFTS_COMPONENT_KEY = "uaefts";
	public static final String ADVANCE_SEARCH_UAEFTS_CCN_KEY = "uaefts-ccn";

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
	public static final String PAYMENT_STATUS_CODE = "79";
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

}