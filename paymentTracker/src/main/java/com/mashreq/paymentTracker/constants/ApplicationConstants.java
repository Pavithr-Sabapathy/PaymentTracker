package com.mashreq.paymentTracker.constants;

public class ApplicationConstants {
	/** Datasource Configuration constants */
	public static final String DATA_SOURCE_CREATION_MSG = "DataSource Configuration Created Successfully";
	public static final String DATA_SOURCE_DELETION_MSG = "DataSource Configuration deleted successfully for this Id";
	public static final String DATA_SOURCE_CONFIG_DOES_NOT_EXISTS = "DataSource Configuration not exist with this id :";
	public static final String DATA_SOURCE_UPDATE_MSG = "DataSource Configuration Updated Successfully";

	/** Reports constants */
	public static final String REPORT_CREATION_MSG = "Report Created Successfully";
	public static final String REPORT_DELETION_MSG = "Report Configuration deleted for this Id";
	public static final String REPORT_DOES_NOT_EXISTS = "Report Configuration not exist with this id :";
	public static final String REPORT_UPDATE_MSG = "Report Configuration Updated Successfully";

	/** Prompts constants */
	public static final String PROMPTS_CREATION_MSG = "Prompts Created Successfully";
	public static final String PROMPTS_DELETION_MSG = "Prompts deleted for this Id";
	public static final String PROMPTS_DOES_NOT_EXISTS = "Prompts not exist with this id :";
	public static final String PROMPTS_UPDATE_MSG = "Prompts Updated Successfully";

	/** Metrics constants */
	public static final String METRICS_CREATION_MSG = "Metrics Created Successfully";
	public static final String METRICS_DELETION_MSG = "Metrics deleted for this Id";
	public static final String METRICS_DOES_NOT_EXISTS = "Metrics not exist with this id :";
	public static final String METRICS_UPDATE_MSG = "Metrics Updated Successfully";

	/** Components and componentDetails Constants **/
	public static final String COMPONENT_CREATION_MSG = "Component Created Successfully";
	public static final String COMPONENT_DELETION_MSG = "Component deleted for this Id";
	public static final String COMPONENT_DOES_NOT_EXISTS = "Component not exist with this id :";
	public static final String COMPONENT_DETAILS_CREATION_MSG = "Component Details Created Successfully";
	public static final String COMPONENT_DETAILS_DELETION_MSG = "Component Details deleted for this Id";
	public static final String COMPONENT_DETAILS_DOES_NOT_EXISTS = "Component Details not exist with this id :";

	/** Module constants **/
	public static final String MODULE_CREATION_MSG = "Module Created Successfully";
	public static final String MODULE_DELETION_MSG = "Module Deleted Successfully";
	public static final String MODULE_DOES_NOT_EXISTS = "Module not exist with this id ";

	/** Linked Report and Linked Report Details constants */

	public static final String LINK_REPORT_CREATION_MSG = "Linked Report Created Successfully";
	public static final String LINK_REPORT_DELETION_MSG = "Linked Report Deleted Successfully";
	public static final String LINK_REPORT_DOES_NOT_EXISTS = "Linked Report not exist with this id";
	public static final String LINK_MAPPING_REPORT_CREATION_MSG = "Link Mapping Created Successfully";
	public static final String LINK_MAPPING_REPORT_DELETION_MSG = "Linked Mapping Deleted Successfully";
	public static final String LINK_MAPPING_DOES_NOT_EXISTS = "Link Mapping not exist with this id";

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
	public static final String S_UMIDH_PROMPT_KEY = "s-umidh";
	public static final String S_UMIDL_PROMPT_KEY = "s-umidl";
	public static final String SWIFT_DETAILED_REPORT_TYPE_PROMPT_KEY = "swift-detailed-type";
	public static final String MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY = "reference-num";
	public static final String MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY = "message-type";
	public static final String MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY = "message-sub-format";

	public static final String MESSAGE_DETAILS_SWIFT_MSG_RMESG = "msg-rmesg";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RINTV = "msg-rintv";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RTEXTFIELD = "msg-rtextfield";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_STX_MESSAGE = "msg-stx-message";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_RCORR = "msg-rcorr";
	public static final String MESSAGE_DETAILS_SWIFT_MSG_STX_ENTRY_FIELD_VIEW = "msg-stx-entry-field-view";
	public static final String MESSAGE_DETAILS_CORR_BANK_PROMPT_KEY = "corr-bank";

	public static final String MESSAGE_INPUT_SUB_FORMAT = "INPUT";
	public static final String MESSAGE_OUTPUT_SUB_FORMAT = "OUTPUT";

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

	public static final String THREE_HASH_NOTATION = "###";
	public static final String BREAK_TAG = "<br/>";
	public static final String PAYMENT_STATUS_CODE = "79";
	public static final String GPI_ENABLED_TRCH_CODE = "TRCKCHZ";

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

	public static final String MESSAGE_DETAILS_MESSAGE_CODES_PROMPT_KEY = "message-codes";
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

}
