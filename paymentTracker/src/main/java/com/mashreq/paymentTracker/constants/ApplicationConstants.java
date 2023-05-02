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

	/**Metrics and prompts constant**/
	public static final String METRIC = "M";
	public static final String PROMPT = "P";
	public static final String ACCOUNTINGSOURCEPROMPTS= "AccountingSource";
	public static final String REFERENCENUMPROMPTS= "ReferenceNum";
	public static final String RELATEDACCOUNTPROMPTS= "RelatedAccount";
	
	/**	Prepared Statement Constants	**/
	public static final String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=PaymentTracker;encrypt=true;trustServerCertificate=true";
	public static final String DATABASE_USERNAME = "TestLogin";
	public static final String DATABASE_PASSWORD = "Sample";
}

