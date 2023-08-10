package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.FlexDetailedReportInput;
import com.mashreq.paymentTracker.dto.FlexReportContext;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.type.PaymentType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class FlexReportConnector extends ReportConnector {

	private static final Logger log = LoggerFactory.getLogger(FlexReportConnector.class);
	private static final String FILENAME = "FlexReportConnector";

	@Autowired
	QueryExecutorService queryExecutorService;

	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof FlexDetailedReportInput flexDetailedReport) {
			return executeFlexReport(flexDetailedReport, reportContext);
		} else if (reportInput instanceof AdvanceSearchReportInput flexAdvanceSearchReportInput) {
			return processFlexAdvanceSearchReport(flexAdvanceSearchReportInput, reportContext);
		} else if (reportInput instanceof PaymentInvestigationReportInput piReportInput) {
			return processPaymentInvestigationReportInput(piReportInput, reportContext);
		}
		return null;
	}

	private List<? extends ReportOutput> processPaymentInvestigationReportInput(
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext) {
		List<PaymentInvestigationReportOutput> outputList = new ArrayList<PaymentInvestigationReportOutput>();
		ReportComponentDTO componentObj = piReportInput.getComponent();
		FlexReportContext flexContext = new FlexReportContext();
		if (componentObj != null) {
			Set<ReportComponentDetailDTO> componentDetailList = componentObj.getReportComponentDetails();
			if (!componentDetailList.isEmpty()) {
				// matrix flow
				if (piReportInput.isProcessOnlyFlexAccountingBasedOnDebitAccount()) {
					if (piReportInput.getMatrixReportContext() != null && null != piReportInput.getCoreReferenceNum()) {
						List<PaymentInvestigationReportOutput> flexActbData = null;
						String componentDetailKey = MashreqFederatedReportConstants.FLEX_ACTB_DAILY_LOG_RELATED_ACC_KEY;
						flexActbData = processComponentDetail(componentObj, piReportInput, componentDetailKey,
								reportContext, flexContext);
						// if we get a record from actb_daily_log we will not go to actb_history table
						if (!flexActbData.isEmpty()) {
							componentDetailKey = MashreqFederatedReportConstants.FLEX_ACTB_HISTORY_RELATED_ACC_KEY;
							flexActbData = processComponentDetail(componentObj, piReportInput, componentDetailKey,
									reportContext, flexContext);
						}
						if (!flexActbData.isEmpty()) {
							outputList.addAll(flexActbData);
						}
					}
				} else if (piReportInput.getMolRecord() != null) {
					String componentDetailKey = MashreqFederatedReportConstants.FLEX_STTM_CUSTOMER_KEY;
					piReportInput.setUserReferenceNum(piReportInput.getMolRecord().getCustomerId());
					processComponentDetail(componentObj, piReportInput, componentDetailKey, reportContext,
							flexContext);
				} else {
					processCoreContractData(componentObj, piReportInput, reportContext, flexContext, outputList);
				}

			}
		}
		return null;
	}

	private void processCoreContractData(ReportComponentDTO componentObj, PaymentInvestigationReportInput piReportInput,
			ReportContext reportContext, FlexReportContext flexContext,
			List<PaymentInvestigationReportOutput> outputList) {

		// process message in table, if generated reference is populated only then need
		// to go to contract table.
		// process contract tables if inward message not found.

		// TODO - VG- process contract tables irrespective of data found or not in msg
		// in
		processMsgInTables(componentObj, piReportInput, reportContext, flexContext, outputList);
		processCoreContractTables(componentObj, piReportInput, reportContext, flexContext, outputList);

		// populate the data flags to main context & update the time values in flex data
		PaymentType paymentType = PaymentType.OUTWARD;
		if (flexContext.getMessageInData() != null && flexContext.getMessageOutData() != null) {
			paymentType = PaymentType.INWARD_RESULTING_INTO_OUTWARD;
		} else if (flexContext.getMessageInData() != null) {
			paymentType = PaymentType.INWARD;
		}
		boolean coreRecordFound = false;
		if (flexContext.getMessageInData() != null || flexContext.getFttbContract() != null) {
			coreRecordFound = true;
		}
		piReportInput.setPaymentType(paymentType);
		piReportInput.setCoreRecordFound(coreRecordFound);

		PaymentInvestigationReportOutput contractedInitiatedData = flexContext.getContractedInitiatedData();
		PaymentInvestigationReportOutput contractedBookedData = flexContext.getContractedBookedData();
		PaymentInvestigationReportOutput contractedLiquidatedData = flexContext.getContractedLiquidatedData();
		PaymentInvestigationReportOutput accountingData = flexContext.getAccountingData();
		PaymentInvestigationReportOutput messageInData = flexContext.getMessageInData();
		// find the max checker date(completion date) from init and liqu and book
		// modify the data for dates
		Date maxCheckerDate = null;
		if (contractedLiquidatedData != null) {
			maxCheckerDate = contractedLiquidatedData.getCompletionTime();
		}
		if (contractedInitiatedData != null) {
			if (contractedInitiatedData.getCompletionTime() != null) {
				if (maxCheckerDate != null) {
					if (maxCheckerDate.getTime() < contractedInitiatedData.getCompletionTime().getTime()) {
						maxCheckerDate = contractedInitiatedData.getCompletionTime();
					}
				} else {
					maxCheckerDate = contractedInitiatedData.getCompletionTime();
				}
			}
		}

		if (contractedBookedData != null) {
			if (contractedBookedData.getCompletionTime() != null) {
				if (maxCheckerDate != null) {
					if (maxCheckerDate.getTime() < contractedBookedData.getCompletionTime().getTime()) {
						maxCheckerDate = contractedBookedData.getCompletionTime();
					}
				} else {
					maxCheckerDate = contractedBookedData.getCompletionTime();
				}
			}
		}
		// populate the max checker date if that is not null. that too only if
		// completion time of that row is not null, else update with sysdate
		if (contractedInitiatedData != null) {
			if (contractedInitiatedData.getCompletionTime() == null) {
				contractedInitiatedData.setCompletionTime(new Date());
			} else if (maxCheckerDate != null) {
				contractedInitiatedData.setCompletionTime(maxCheckerDate);
			}
		}

		if (contractedBookedData != null) {
			if (contractedBookedData.getCompletionTime() == null) {
				contractedBookedData.setCompletionTime(new Date());
			} else if (maxCheckerDate != null) {
				contractedBookedData.setCompletionTime(maxCheckerDate);
			}
		}

		if (contractedLiquidatedData != null) {
			if (contractedLiquidatedData.getCompletionTime() == null) {
				contractedLiquidatedData.setCompletionTime(new Date());
			} else if (maxCheckerDate != null) {
				contractedLiquidatedData.setCompletionTime(maxCheckerDate);
			}
		}

		if (contractedInitiatedData != null && messageInData != null) {
			messageInData.setCompletionTime(contractedInitiatedData.getLandingTime());
		}
		// modify the activity name
		if (paymentType == PaymentType.INWARD && accountingData != null) {
			accountingData.setActivity(MashreqFederatedReportConstants.FLEX_ACCOUNTING_CREDITED_STATUS);
		}

	}

	private void processCoreContractTables(ReportComponentDTO componentObj,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext, FlexReportContext flexContext,
			List<PaymentInvestigationReportOutput> outputList) {

		processFTTBContractTables(componentObj, piReportInput, reportContext, flexContext, outputList);
		if (flexContext.getFttbContract() != null) {
			processCstbContractEventTables(componentObj, piReportInput, reportContext, flexContext, outputList);
			processAccountingTables(componentObj, piReportInput, reportContext, flexContext, outputList);
			processMsgOutTables(componentObj, piReportInput, reportContext, flexContext, outputList);
		}

	}

	private List<PaymentInvestigationReportOutput> processAccountingTables(ReportComponentDTO componentObj,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext, FlexReportContext flexContext,
			List<PaymentInvestigationReportOutput> outputList) {

		List<PaymentInvestigationReportOutput> accountingEntries = new ArrayList<PaymentInvestigationReportOutput>();
		String componentDetailKey = MashreqFederatedReportConstants.FLEX_ACTB_DAILY_LOG_KEY;
		accountingEntries = processComponentDetail(componentObj, piReportInput, componentDetailKey, reportContext,
				flexContext);
		if (accountingEntries.isEmpty()) {
			componentDetailKey = MashreqFederatedReportConstants.FLEX_ACTB_HISTORY_KEY;
			accountingEntries = processComponentDetail(componentObj, piReportInput, componentDetailKey, reportContext,
					flexContext);
		}
		// add it to the final list
		if (!accountingEntries.isEmpty()) {
			outputList.addAll(accountingEntries);
		}
		return accountingEntries;

	}

	private List<PaymentInvestigationReportOutput> processMsgOutTables(ReportComponentDTO componentObj,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext, FlexReportContext flexContext,
			List<PaymentInvestigationReportOutput> outputList) {

		String componentDetailKey = MashreqFederatedReportConstants.FLEX_DAILY_MSG_OUT_KEY;
		List<PaymentInvestigationReportOutput> piOutputList = processComponentDetail(componentObj, piReportInput,
				componentDetailKey, reportContext, flexContext);
		if (!piOutputList.isEmpty()) {
			outputList.addAll(piOutputList);
		}
		return outputList;
	}

	private List<PaymentInvestigationReportOutput> processCstbContractEventTables(ReportComponentDTO componentObj,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext, FlexReportContext flexContext,
			List<PaymentInvestigationReportOutput> outputList) {

		String componentDetailKey = MashreqFederatedReportConstants.FLEX_CSTB_CONTRACT_EVENT_KEY;
		List<PaymentInvestigationReportOutput> outputData = processComponentDetail(componentObj, piReportInput,
				componentDetailKey, reportContext, flexContext);
		if (!outputData.isEmpty()) {
			outputList.addAll(outputData);
		}
		return outputData;
	}

	private List<PaymentInvestigationReportOutput> processFTTBContractTables(ReportComponentDTO componentObj,
			PaymentInvestigationReportInput piReportInput, ReportContext reportContext, FlexReportContext flexContext,
			List<PaymentInvestigationReportOutput> outputList) {

		boolean coreRecordFoundUsingCoreRef = false;
		boolean coreRecordFoundUsingSourceRef = false;

		String componentDetailKey = MashreqFederatedReportConstants.FLEX_FTTB_CONTRACT_CORE_REF_KEY;
		List<PaymentInvestigationReportOutput> outputData = processComponentDetail(componentObj, piReportInput,
				componentDetailKey, reportContext, flexContext);
		if (outputData.isEmpty()) {
			componentDetailKey = MashreqFederatedReportConstants.FLEX_FTTB_CONTRACT_SOURCE_REF_KEY;
			outputData = processComponentDetail(componentObj, piReportInput, componentDetailKey, reportContext,
					flexContext);
			if (!outputData.isEmpty()) {
				coreRecordFoundUsingSourceRef = true;
			}
		} else {
			coreRecordFoundUsingCoreRef = true;
		}
		piReportInput.setCoreRecordFoundUsingCoreRef(coreRecordFoundUsingCoreRef);
		piReportInput.setCoreRecordFoundUsingSourceRef(coreRecordFoundUsingSourceRef);
		return outputData;

	}

	private void processMsgInTables(ReportComponentDTO componentObj, PaymentInvestigationReportInput piReportInput,
			ReportContext reportContext, FlexReportContext flexContext,
			List<PaymentInvestigationReportOutput> outputList) {

		// get data from inward table always
		String componentDetailKey = MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_SOURCE_REF_KEY;
		List<PaymentInvestigationReportOutput> piOutputList = processComponentDetail(componentObj, piReportInput,
				componentDetailKey, reportContext, flexContext);
		if (!piOutputList.isEmpty()) {
			componentDetailKey = MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_CORE_REF_KEY;
			piOutputList = processComponentDetail(componentObj, piReportInput, componentDetailKey, reportContext,
					flexContext);
			if (piOutputList.isEmpty()) {
				componentDetailKey = MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_HISTORY_SOURCE_REF_KEY;
				piOutputList = processComponentDetail(componentObj, piReportInput, componentDetailKey, reportContext,
						flexContext);
				if (piOutputList.isEmpty()) {
					componentDetailKey = MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_HISTORY_CORE_REF_KEY;
					piOutputList = processComponentDetail(componentObj, piReportInput, componentDetailKey,
							reportContext, flexContext);
				}
			}
		}
		if (!piOutputList.isEmpty()) {
			outputList.addAll(piOutputList);
		}

	}

	private List<PaymentInvestigationReportOutput> processComponentDetail(ReportComponentDTO componentObj,
			PaymentInvestigationReportInput piReportInput, String componentDetailKey, ReportContext reportContext,
			FlexReportContext flexContext) {

		List<PaymentInvestigationReportOutput> reportOutput = new ArrayList<PaymentInvestigationReportOutput>();
		Set<ReportComponentDetailDTO> componentDetailList = componentObj.getReportComponentDetails();
		ReportComponentDetailDTO componentDetail = getMatchedComponentDetail(componentDetailList, componentDetailKey);
		if (componentDetail != null) {
			ReportComponentDetailContext context = populateReportComponentDetailContext(componentDetail, piReportInput,
					reportContext);
			List<ReportDefaultOutput> outputlist = queryExecutorService.executeQuery(componentDetail, context);
			if (componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_ACTB_DAILY_LOG_RELATED_ACC_KEY)
					|| componentDetailKey
							.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_ACTB_HISTORY_RELATED_ACC_KEY)
					|| componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_ACTB_DAILY_LOG_KEY)
					|| componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_ACTB_HISTORY_KEY)) {
				reportOutput = populateFlexActbDailyLog(outputlist, flexContext);
			} else if (componentDetailKey
					.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_FTTB_CONTRACT_CORE_REF_KEY)
					|| componentDetailKey
							.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_FTTB_CONTRACT_SOURCE_REF_KEY)) {
				reportOutput = populateFlexFttbContract(outputlist, flexContext, piReportInput);
			} else if (componentDetailKey
					.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_CSTB_CONTRACT_EVENT_KEY)) {
				reportOutput = populateFlexCstbContract(outputlist, flexContext);
			} else if (componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_DAILY_MSG_OUT_KEY)) {
				reportOutput = populateFlexDailyMsgOut(outputlist, flexContext);
			} else if (componentDetailKey.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_STTM_CUSTOMER_KEY)) {
				updateMolRecord(outputlist, piReportInput.getMolRecord());
			} else if (componentDetailKey
					.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_CORE_REF_KEY)
					|| componentDetailKey
							.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_SOURCE_REF_KEY)
					|| componentDetailKey
							.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_HISTORY_SOURCE_REF_KEY)
					|| componentDetailKey
							.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_DAILY_MSG_IN_HISTORY_CORE_REF_KEY)) {
				reportOutput = populateFlexDailyMsgIn(outputlist, flexContext, piReportInput);
			}
		} else {
			log.debug("Component Detail missing for " + componentDetailKey);
		}
		return reportOutput;
	}

	private List<PaymentInvestigationReportOutput> populateFlexDailyMsgIn(List<ReportDefaultOutput> outputList,
			FlexReportContext flexContext, PaymentInvestigationReportInput piReportInput) {

		List<PaymentInvestigationReportOutput> reportOutputList = new ArrayList<PaymentInvestigationReportOutput>();
		if (!outputList.isEmpty()) {
			ReportDefaultOutput reportDefaultOutput = outputList.get(0);
			List<Object> componentDataRow = reportDefaultOutput.getRowData();
			PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
			reportOutput.setComponentDetailId(reportDefaultOutput.getComponentDetailId());
			reportOutput.setActivity(UtilityClass.getStringRepresentation(componentDataRow.get(1)));
			reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(componentDataRow.get(2)));
			reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(componentDataRow.get(0)));
			reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(componentDataRow.get(0)));
			reportOutput.setWorkstage(UtilityClass.getStringRepresentation(componentDataRow.get(3)));
			reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(componentDataRow.get(4)));
			reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(componentDataRow.get(6)));
			reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_FLEX);
			piReportInput.setSourceReferenceNum(UtilityClass.getStringRepresentation(componentDataRow.get(6)));
			PaymentInvestigationReportOutput fttbContract = flexContext.getFttbContract();
			if (fttbContract != null) {
				reportOutput.setAmount(fttbContract.getAmount());
				reportOutput.setBeneficaryAccount(fttbContract.getBeneficaryAccount());
				reportOutput.setBeneficaryDetail(fttbContract.getBeneficaryDetail());
				reportOutput.setCurrency(fttbContract.getCurrency());
				reportOutput.setDebitAccount(fttbContract.getDebitAccount());
				reportOutput.setValueDate(fttbContract.getValueDate());
				reportOutput.setReceiver(fttbContract.getReceiver());
			}
			flexContext.setMessageInData(reportOutput);
			reportOutputList.add(reportOutput);
		}
		return reportOutputList;

	}

	private void updateMolRecord(List<ReportDefaultOutput> outputlist, PaymentInvestigationReportOutput molRecord) {

		if (!outputlist.isEmpty()) {
			ReportDefaultOutput reportDefaultOutput = outputlist.get(0);
			List<Object> rowData = reportDefaultOutput.getRowData();
			String molCustomerType = UtilityClass.getStringRepresentation(rowData.get(0));
			if (molCustomerType != null) {
				molRecord.setSource(molCustomerType);
			}
		}

	}

	private List<PaymentInvestigationReportOutput> populateFlexDailyMsgOut(List<ReportDefaultOutput> outputList,
			FlexReportContext flexContext) {
		List<PaymentInvestigationReportOutput> reportOutputList = new ArrayList<PaymentInvestigationReportOutput>();
		if (!outputList.isEmpty()) {
			ReportDefaultOutput reportDefaultOutput = outputList.get(0);
			List<Object> componentDataRow = reportDefaultOutput.getRowData();
			PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
			reportOutput.setComponentDetailId(reportDefaultOutput.getComponentDetailId());
			reportOutput.setActivity(UtilityClass.getStringRepresentation(componentDataRow.get(1)));
			reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(componentDataRow.get(3)));
			reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(componentDataRow.get(0)));
			reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(componentDataRow.get(2)));
			reportOutput.setWorkstage(UtilityClass.getStringRepresentation(componentDataRow.get(4)));
			reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(componentDataRow.get(5)));
			reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_FLEX);
			PaymentInvestigationReportOutput fttbContract = flexContext.getFttbContract();
			reportOutput.setAmount(fttbContract.getAmount());
			reportOutput.setBeneficaryAccount(fttbContract.getBeneficaryAccount());
			reportOutput.setBeneficaryDetail(fttbContract.getBeneficaryDetail());
			reportOutput.setCurrency(fttbContract.getCurrency());
			reportOutput.setDebitAccount(fttbContract.getDebitAccount());
			reportOutput.setValueDate(fttbContract.getValueDate());
			reportOutput.setReceiver(fttbContract.getReceiver());
			reportOutput.setSourceRefNum(fttbContract.getSourceRefNum());
			reportOutputList.add(reportOutput);
			flexContext.setMessageOutData(reportOutput);
		}
		return reportOutputList;
	}

	private List<PaymentInvestigationReportOutput> populateFlexCstbContract(List<ReportDefaultOutput> outputlist,
			FlexReportContext flexContext) {

		List<PaymentInvestigationReportOutput> reportOutputList = new ArrayList<PaymentInvestigationReportOutput>();
		if (!outputlist.isEmpty()) {
			for (ReportDefaultOutput defaultOutput : outputlist) {
				List<Object> componentDataRow = defaultOutput.getRowData();
				PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
				reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
				reportOutput.setActivity(UtilityClass.getStringRepresentation(componentDataRow.get(0)));
				reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(componentDataRow.get(1)));
				reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(componentDataRow.get(2)));
				reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(componentDataRow.get(3)));
				reportOutput.setWorkstage(UtilityClass.getStringRepresentation(componentDataRow.get(4)));
				reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(componentDataRow.get(5)));
				String eventCode = UtilityClass.getStringRepresentation(componentDataRow.get(6));
				reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_FLEX);
				PaymentInvestigationReportOutput fttbContract = flexContext.getFttbContract();
				reportOutput.setAmount(fttbContract.getAmount());
				reportOutput.setBeneficaryAccount(fttbContract.getBeneficaryAccount());
				reportOutput.setBeneficaryDetail(fttbContract.getBeneficaryDetail());
				reportOutput.setCurrency(fttbContract.getCurrency());
				reportOutput.setDebitAccount(fttbContract.getDebitAccount());
				reportOutput.setValueDate(fttbContract.getValueDate());
				reportOutput.setReceiver(fttbContract.getReceiver());
				reportOutput.setSourceRefNum(fttbContract.getSourceRefNum());
				if (eventCode.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_INITATIED_STATUS)) {
					reportOutput.setComponentDetailId(fttbContract.getComponentDetailId());
					flexContext.setContractedInitiatedData(reportOutput);
					reportOutputList.add(reportOutput);
				} else if (eventCode.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_BOOKED_STATUS)) {
					reportOutput.setComponentDetailId(fttbContract.getComponentDetailId());
					flexContext.setContractedBookedData(reportOutput);
					reportOutputList.add(reportOutput);
				} else if (eventCode.equalsIgnoreCase(MashreqFederatedReportConstants.FLEX_LIQUIDATED_STATUS)) {
					flexContext.setContractedLiquidatedData(reportOutput);
					reportOutputList.add(reportOutput);
				}
			}
		}
		return reportOutputList;

	}

	private List<PaymentInvestigationReportOutput> populateFlexFttbContract(List<ReportDefaultOutput> outputlist,
			FlexReportContext flexContext, PaymentInvestigationReportInput piReportInput) {

		List<PaymentInvestigationReportOutput> outputList = new ArrayList<PaymentInvestigationReportOutput>();
		if (!outputlist.isEmpty()) {
			ReportDefaultOutput federatedReportDefaultOutput = outputlist.get(0);
			List<Object> componentDataRow = federatedReportDefaultOutput.getRowData();
			PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
			reportOutput.setComponentDetailId(federatedReportDefaultOutput.getComponentDetailId());
			reportOutput.setCurrency(UtilityClass.getStringRepresentation(componentDataRow.get(0)));
			reportOutput.setAmount(UtilityClass.getStringRepresentation(componentDataRow.get(1)));
			reportOutput.setValueDate(UtilityClass.getStringRepresentation(componentDataRow.get(2)));
			reportOutput.setDebitAccount(UtilityClass.getStringRepresentation(componentDataRow.get(3)));
			reportOutput.setReceiver(UtilityClass.getStringRepresentation(componentDataRow.get(4)));
			reportOutput.setBeneficaryAccount(UtilityClass.getStringRepresentation(componentDataRow.get(5)));
			reportOutput.setBeneficaryDetail(UtilityClass.getStringRepresentation(componentDataRow.get(6)));
			reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(componentDataRow.get(7)));
			String sourceRefNum = UtilityClass.getStringRepresentation(componentDataRow.get(8));
			String sourceType = UtilityClass.getStringRepresentation(componentDataRow.get(9));
			String govCheck = UtilityClass.getStringRepresentation(componentDataRow.get(10));
			piReportInput.setSourceReferenceNum(sourceRefNum);
			piReportInput.setCoreReferenceNum(reportOutput.getSourceRefNum());
			piReportInput.setSourceType(sourceType);
			piReportInput.setGovCheck(govCheck);
			piReportInput.setGovCheckReference(reportOutput.getSourceRefNum());
			flexContext.setFttbContract(reportOutput);
			outputList.add(reportOutput);
		}
		return outputList;

	}

	private List<PaymentInvestigationReportOutput> populateFlexActbDailyLog(List<ReportDefaultOutput> outputlist,
			FlexReportContext flexContext) {

		List<PaymentInvestigationReportOutput> output = new ArrayList<PaymentInvestigationReportOutput>();
		if (!outputlist.isEmpty()) {
			Timestamp minTime = UtilityClass.getTimeStampRepresentation(outputlist.get(0).getRowData().get(0));
			Timestamp maxTime = UtilityClass.getTimeStampRepresentation(outputlist.get(0).getRowData().get(2));
			String debitAccount = null;
			PaymentInvestigationReportOutput consolidatedReportOutput = null;
			for (ReportDefaultOutput defaultOutput : outputlist) {
				List<Object> rowData = defaultOutput.getRowData();
				PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
				reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
				Timestamp landingTime = UtilityClass.getTimeStampRepresentation(rowData.get(0));
				reportOutput.setLandingTime(landingTime);
				reportOutput.setActivity(UtilityClass.getStringRepresentation(rowData.get(1)));
				Timestamp completionTime = UtilityClass.getTimeStampRepresentation(rowData.get(2));
				reportOutput.setCompletionTime(completionTime);
				reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(3)));
				reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(rowData.get(4)));
				reportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(5)));
				reportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(6)));
				reportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(7)));
				reportOutput.setWorkstage(UtilityClass.getStringRepresentation(rowData.get(12)));
				reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(rowData.get(13)));

				String accountingSource = UtilityClass.getStringRepresentation(rowData.get(14));
				reportOutput.setAccountingSource(accountingSource);
				reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_FLEX);

				String accountNum = UtilityClass.getStringRepresentation(rowData.get(8));
				String debitCreditFlag = UtilityClass.getStringRepresentation(rowData.get(9));
				String ibFlag = UtilityClass.getStringRepresentation(rowData.get(10));
				String trnCode = UtilityClass.getStringRepresentation(rowData.get(11));
				if (minTime.getTime() > landingTime.getTime()) {
					minTime = landingTime;
				}
				if (maxTime.getTime() < completionTime.getTime()) {
					maxTime = completionTime;
				}
				if ((ibFlag == null || ibFlag.equalsIgnoreCase("N"))
						&& (MashreqFederatedReportConstants.FLEX_TRN_CODES_LIST.contains(trnCode))) {
					if (debitCreditFlag.equalsIgnoreCase(MashreqFederatedReportConstants.DEBIT_FLAG_VALUE)) {
						debitAccount = accountNum;
					} else {
						reportOutput.setBeneficaryAccount(accountNum);
						consolidatedReportOutput = reportOutput;
					}
				}
			}
			if (consolidatedReportOutput != null) {
				consolidatedReportOutput.setLandingTime(minTime);
				consolidatedReportOutput.setCompletionTime(maxTime);
				consolidatedReportOutput.setDebitAccount(debitAccount);
				output.add(consolidatedReportOutput);
				flexContext.setAccountingData(consolidatedReportOutput);
			}
		}
		return output;

	}

	private ReportComponentDetailDTO getMatchedComponentDetail(Set<ReportComponentDetailDTO> componentDetailList,
			String componentDetailKey) {
		return componentDetailList.stream()
				.filter(componentDetail -> componentDetail.getQueryKey().equalsIgnoreCase(componentDetailKey)).findAny()
				.orElse(null);
	}

	private List<ReportDefaultOutput> executeFlexReport(
			FlexDetailedReportInput flexAccountingDetailedFederatedReportInput, ReportContext reportContext) {
		ReportComponentDetailDTO matchedComponentDetail = new ReportComponentDetailDTO();
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		ReportComponentDTO component = flexAccountingDetailedFederatedReportInput.getComponent();
		if (null != component) {
			Set<ReportComponentDetailDTO> componentDetailsSet = component.getReportComponentDetails();
			for (ReportComponentDetailDTO componentDetail : componentDetailsSet) {
				if (componentDetail.getQueryKey().toLowerCase().contains(flexAccountingDetailedFederatedReportInput
						.getAccountingSourcePrompt().getPromptValue().toLowerCase())) {
					matchedComponentDetail = componentDetail;
					matchedComponentDetail.setReportComponent(component);
					break;
				}
			}
			if (matchedComponentDetail != null) {

				ReportComponentDetailContext context = new ReportComponentDetailContext();
				List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
				context.setQueryId(matchedComponentDetail.getId());
				context.setQueryKey(matchedComponentDetail.getQueryKey());
				context.setQueryString(matchedComponentDetail.getQuery());
				promptsList.add(flexAccountingDetailedFederatedReportInput.getReferenceNumPrompt());
				promptsList.add(flexAccountingDetailedFederatedReportInput.getAccountingSourcePrompt());
				promptsList.add(flexAccountingDetailedFederatedReportInput.getDebitAccountPrompt());
				context.setPrompts(promptsList);
				context.setExecutionId(reportContext.getExecutionId());

				flexReportExecuteResponse = queryExecutorService.executeQuery(matchedComponentDetail, context);

			}
		}
		return flexReportExecuteResponse;
	}

	private List<? extends ReportOutput> processFlexAdvanceSearchReport(
			AdvanceSearchReportInput advanceSearchReportInput, ReportContext reportContext) {
		List<ReportDefaultOutput> flexReportExecuteResponse = new ArrayList<ReportDefaultOutput>();
		List<AdvanceSearchReportOutput> advanceSearchReportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		ReportComponentDTO reportComponent = advanceSearchReportInput.getFlexComponent();
		Set<ReportComponentDetailDTO> componentDetailsSet = reportComponent.getReportComponentDetails();
		if (!componentDetailsSet.isEmpty()) {
			ReportComponentDetailDTO componentDetail = componentDetailsSet.iterator().next(); // take first as it
																								// gonna be single
																								// set
			if (null != componentDetail) {
				ReportComponentDetailContext context = new ReportComponentDetailContext();
				List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
				promptsList = populatePromptsForAdvanceSearch(advanceSearchReportInput);
				context.setQueryId(componentDetail.getId());
				context.setQueryKey(componentDetail.getQueryKey());
				context.setQueryString(componentDetail.getQuery());
				context.setExecutionId(reportContext.getExecutionId());
				context.setPrompts(promptsList);
				flexReportExecuteResponse = queryExecutorService.executeQuery(componentDetail, context);
				if (!flexReportExecuteResponse.isEmpty()) {
					advanceSearchReportOutputList = populateDataForAdvanceSearch(flexReportExecuteResponse,
							advanceSearchReportInput);
				}
			}

		}
		return advanceSearchReportOutputList;
	}

	private List<AdvanceSearchReportOutput> populateDataForAdvanceSearch(
			List<ReportDefaultOutput> flexReportExecuteResponse, AdvanceSearchReportInput advanceSearchReportInput) {
		List<AdvanceSearchReportOutput> reportOutputList = new ArrayList<AdvanceSearchReportOutput>();
		if (!flexReportExecuteResponse.isEmpty()) {
			for (ReportDefaultOutput federatedReportOutput : flexReportExecuteResponse) {
				AdvanceSearchReportOutput advanceSearchReportOutput = new AdvanceSearchReportOutput();
				List<Object> rowData = federatedReportOutput.getRowData();
				advanceSearchReportOutput.setTransactionReference(UtilityClass.getStringRepresentation(rowData.get(0)));
				advanceSearchReportOutput.setBeneficiaryDetails(UtilityClass.getStringRepresentation(rowData.get(1)));
				advanceSearchReportOutput.setValueDate(UtilityClass.getStringRepresentation(rowData.get(2)));
				advanceSearchReportOutput.setCurrency(UtilityClass.getStringRepresentation(rowData.get(3)));
				advanceSearchReportOutput.setAmount(UtilityClass.getStringRepresentation(rowData.get(4)));
				advanceSearchReportOutput.setStatus(UtilityClass.getStringRepresentation(rowData.get(5)));
				advanceSearchReportOutput.setMessageType(UtilityClass.getStringRepresentation(rowData.get(6)));
				advanceSearchReportOutput.setInitationSource(UtilityClass.getStringRepresentation(rowData.get(7)));
				advanceSearchReportOutput.setMessageThrough(UtilityClass.getStringRepresentation(rowData.get(8)));
				advanceSearchReportOutput.setAccountNum(UtilityClass.getStringRepresentation(rowData.get(9)));
				advanceSearchReportOutput.setRelatedAccount(UtilityClass.getStringRepresentation(rowData.get(10)));
				advanceSearchReportOutput.setInstrumentCode(UtilityClass.getStringRepresentation(rowData.get(11)));
				advanceSearchReportOutput.setExternalRefNum(UtilityClass.getStringRepresentation(rowData.get(12)));
				advanceSearchReportOutput.setCoreReferenceNum(UtilityClass.getStringRepresentation(rowData.get(13)));
				advanceSearchReportOutput.setTransactionDate(UtilityClass.getTimeStampRepresentation(rowData.get(14)));
				advanceSearchReportOutput.setProcessName(UtilityClass.getStringRepresentation(rowData.get(15)));
				advanceSearchReportOutput.setActivityName(UtilityClass.getStringRepresentation(rowData.get(16)));
				String rejectStatus = UtilityClass.getStringRepresentation(rowData.get(17));
				if (rejectStatus.equalsIgnoreCase(
						MashreqFederatedReportConstants.ADVANCE_SEARCH_REPORT_TRANSACTION_REJECT_STATUS)) {
					advanceSearchReportOutput.setStatus(rejectStatus);
				}
				reportOutputList.add(advanceSearchReportOutput);
			}
		}
		return reportOutputList;
	}

	private List<FederatedReportPromptDTO> populatePromptsForAdvanceSearch(
			AdvanceSearchReportInput advanceSearchReportInput) {
		List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
		prompts.add(advanceSearchReportInput.getAccountNumPrompt());
		prompts.add(advanceSearchReportInput.getAmountBetweenPrompt());
		prompts.add(advanceSearchReportInput.getAmountToPrompt());
		prompts.add(advanceSearchReportInput.getCurrencyPrompt());
		prompts.add(advanceSearchReportInput.getFromDatePrompt());
		prompts.add(advanceSearchReportInput.getToDatePrompt());
		prompts.add(advanceSearchReportInput.getTransactionRefNum());
		return prompts;

	}

}