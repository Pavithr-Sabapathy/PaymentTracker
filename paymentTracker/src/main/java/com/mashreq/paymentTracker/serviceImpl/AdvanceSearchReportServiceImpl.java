package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.MatrixPaymentReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.service.UAEFTSReportService;

@Service("advanceSearch")
public class AdvanceSearchReportServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {

	private static final Logger log = LoggerFactory.getLogger(AdvanceSearchReportServiceImpl.class);
	private static final String FILENAME = "AdvanceSearchReportServiceImpl";

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	CannedReportService cannedReportService;

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	MatrixPaymentReportService matrixPaymentReportService;

	@Autowired
	ReportOutputExecutor reportOutputExecutor;
	
	UAEFTSReportService Uaefts;

	@Override
	public ReportInput populateBaseInputContext(ReportContext reportContext) {
		ReportInstanceDTO reportInstance = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> promptsList = new ArrayList<ReportPromptsInstanceDTO>();
		if (null != reportInstance) {
			promptsList = reportInstance.getPromptsList();
		}
		System.out.println(promptsList.toString());
		FederatedReportPromptDTO fromDatePrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_FROM_DATE_PROMPT_KEY);
		FederatedReportPromptDTO toDatePrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_TO_DATE_PROMPT_KEY);
		FederatedReportPromptDTO accountNumPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_ACCOUNT_NUMBER_PROMPT_KEY);
		FederatedReportPromptDTO currencyPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_CURRENCY_PROMPT_KEY);
		FederatedReportPromptDTO amountBetweenPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_AMOUNT_BETWEEN_PROMPT_KEY);
		FederatedReportPromptDTO amountToPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_AMOUNT_TO_PROMPT_KEY);
		FederatedReportPromptDTO transactionStatusPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_TRANSACTION_STATUS_PROMPT_KEY);
		FederatedReportPromptDTO transactionRefNumPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.ADVANCE_SEARCH_TRANSACTION_REF_NUM_PROMPT_KEY);
		AdvanceSearchReportInput advanceSearchReportInput = new AdvanceSearchReportInput();
		if (fromDatePrompt != null) {
			advanceSearchReportInput.setFromDatePrompt(fromDatePrompt);
		}
		if (toDatePrompt != null) {
			advanceSearchReportInput.setToDatePrompt(toDatePrompt);
		}
		if (accountNumPrompt != null) {
			advanceSearchReportInput.setAccountNumPrompt(accountNumPrompt);
		}
		if (currencyPrompt != null) {
			advanceSearchReportInput.setCurrencyPrompt(currencyPrompt);
		}
		if (amountBetweenPrompt != null) {
			advanceSearchReportInput.setAmountBetweenPrompt(amountBetweenPrompt);
		}
		if (amountToPrompt != null) {
			advanceSearchReportInput.setAmountToPrompt(amountToPrompt);
		}
		if (transactionStatusPrompt != null) {
			advanceSearchReportInput.setTransactionStatus(transactionStatusPrompt);
		}
		if (transactionRefNumPrompt != null) {
			advanceSearchReportInput.setTransactionRefNum(transactionRefNumPrompt);
		}
		return advanceSearchReportInput;
	}

	@Override
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {

		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = null;
		List<Components> activeComponentList = new ArrayList<Components>();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		/** fetch the report details based on report name **/
		Report report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		CannedReport cannedReport = cannedReportService.populateCannedReportInstance(report);
		List<Components> componentList = componentsDAO.findAllByreportId(cannedReport.getId());
		if (componentList.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + cannedReport.getId());
		} else {
			activeComponentList = componentList.stream().filter(component -> component.getActive().equals("Y"))
					.collect(Collectors.toList());
		}
		if (!activeComponentList.isEmpty()) {
			AdvanceSearchReportInput advanceSearchReportInput = (AdvanceSearchReportInput) reportInput;
			List<AdvanceSearchReportOutput> advanceSearchReportOutputList = processDetailedReport(
					advanceSearchReportInput, activeComponentList, reportContext);
			advanceSearchReportOutputList = rearrangeAndFillReportData(advanceSearchReportOutputList,
					advanceSearchReportInput);
			List<ReportDefaultOutput> federatedReportOutputList = populateDataToObjectForm(advanceSearchReportOutputList,
					report);
			if (!federatedReportOutputList.isEmpty()) {
				List<Map<String, Object>> rowDataMapList = reportOutputExecutor
						.populateRowData(federatedReportOutputList, report);
				reportExecuteResponseCloumnDefList = reportOutputExecutor.populateColumnDef(report);
				responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
				responseData.setData(rowDataMapList);
			}
			log.info(FILENAME + "[processAdvanceSearchReport Response] -->" + responseData.toString());
		}
		return responseData;
	}

	private List<ReportDefaultOutput> populateDataToObjectForm(List<AdvanceSearchReportOutput> advanceSearchReportOutputList,
			Report report) {
		List<ReportDefaultOutput> data = new ArrayList<ReportDefaultOutput>();
		for (AdvanceSearchReportOutput output : advanceSearchReportOutputList) {
			ReportDefaultOutput defaultOutput = new ReportDefaultOutput();
			List<Object> rowData = new ArrayList<Object>();
			rowData.add(output.getTransactionReference());
			rowData.add(output.getBeneficiaryDetails());
			rowData.add(output.getValueDate());
			rowData.add(output.getCurrency());
			rowData.add(output.getAmount());
			rowData.add(output.getStatus());
			rowData.add(output.getMessageType());
			rowData.add(output.getInitationSource());
			rowData.add(output.getMessageThrough());
			rowData.add(output.getAccountNum());
			rowData.add(output.getRelatedAccount());
			rowData.add(output.getInstrumentCode());
			rowData.add(output.getExternalRefNum());
			rowData.add(output.getCoreReferenceNum());
			rowData.add(output.getTransactionDate());
			rowData.add(output.getProcessName());
			rowData.add(output.getActivityName());
			rowData.add(output.getMessageSubFormat());
			defaultOutput.setRowData(rowData);
			data.add(defaultOutput);
		}
		return data;
	}

	private List<AdvanceSearchReportOutput> rearrangeAndFillReportData(
			List<AdvanceSearchReportOutput> advanceSearchReportOutputList,
			AdvanceSearchReportInput advanceSearchReportInput) {

		List<AdvanceSearchReportOutput> rearrangedReportOutput = new ArrayList<AdvanceSearchReportOutput>();
		if (!advanceSearchReportOutputList.isEmpty()) {
			// filter the data based on transaction status prompt
			FederatedReportPromptDTO transactionStatusPrompt = advanceSearchReportInput.getTransactionStatus();
			String transactionStatus = transactionStatusPrompt.getPromptValue();
			if (null != transactionStatus && (!transactionStatus.equalsIgnoreCase(
					MashreqFederatedReportConstants.ADVANCE_SEARCH_REPORT_TRANSACTION_STATUS_PROMPT_DEFAULT_VALUE))) {
				for (AdvanceSearchReportOutput advanceSearchFederatedReportOutput : advanceSearchReportOutputList) {
					if (transactionStatus.equalsIgnoreCase(advanceSearchFederatedReportOutput.getStatus())) {
						rearrangedReportOutput.add(advanceSearchFederatedReportOutput);
					}
				}
			} else {
				rearrangedReportOutput.addAll(advanceSearchReportOutputList);
			}
		}
		// add any failed system rows at the end.
		if (!advanceSearchReportInput.getFailedSystemOutputs().isEmpty()) {
			rearrangedReportOutput.addAll(advanceSearchReportInput.getFailedSystemOutputs());
		}
		return rearrangedReportOutput;

	}

	private List<AdvanceSearchReportOutput> processDetailedReport(AdvanceSearchReportInput advanceSearchReportInput,
			List<Components> componentList, ReportContext reportContext) {
		List<AdvanceSearchReportOutput> advanceSearchReportFinalOutputList = new ArrayList<AdvanceSearchReportOutput>();

		if (!componentList.isEmpty()) {

			Long roleId = reportContext.getRoleId();

			// --TODO Check role implementation

//			  SecurityRoles role = getUserManagementService().getRoleById(roleId); String
//			  roleName = role.getName(); boolean isMatrixCustomerRole = false; if
//			  (roleName.equalsIgnoreCase(IMashreqFederatedReportConstants.
//			  CUSTOMER_MATRIX_REPORTING_ROLE)) { 
//			  boolean isMatrixCustomerRole = false;}

			boolean isMatrixCustomerRole = false;

			// -TODO Implement all these Parallel
			// run all three parallely

			Thread flexProcessor = new Thread() {
				FlexDetailedReportServiceImpl flexReportServiceImpl = new FlexDetailedReportServiceImpl();

				public void run() {
					List<AdvanceSearchReportOutput> advanceSearchFlexReportOutList = flexReportServiceImpl
							.processFlexDetailReport(advanceSearchReportInput, componentList, reportContext);
					advanceSearchReportFinalOutputList.addAll(advanceSearchFlexReportOutList);

				}

			};

			Thread matrixProcessor = new Thread() {
				public void run() {
					List<AdvanceSearchReportOutput> advanceSearchMatrixReportOutList = matrixPaymentReportService
							.processMatrixPaymentReport(advanceSearchReportInput, componentList, reportContext);
					advanceSearchReportFinalOutputList.addAll(advanceSearchMatrixReportOutList);

				}
			};

			Thread edmsProcessor = new Thread() {
				EdmsProcessServiceImpl edmsProcessServiceImpl = new EdmsProcessServiceImpl();

				public void run() {
					List<AdvanceSearchReportOutput> advanceSearchEdmsReportOutList = edmsProcessServiceImpl
							.processEdmsReport(advanceSearchReportInput, componentList, reportContext);
					advanceSearchReportFinalOutputList.addAll(advanceSearchEdmsReportOutList);
				}

			};

			// run all three parallely

			flexProcessor.start();
			if (!isMatrixCustomerRole) {
				matrixProcessor.start();
			}
			edmsProcessor.start();

			// wait for all three

			try {
				flexProcessor.join();
				if (!isMatrixCustomerRole) {
					matrixProcessor.join();
				}
				edmsProcessor.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Map<String, AdvanceSearchReportOutput> flexMatrixBasedUaeftsTransactionsList = new HashMap<String, AdvanceSearchReportOutput>();
			// break up the data into two parts uaefts and others so that we can use
			// uaefts-ccn tables to find credit-confirmed status
			if (!advanceSearchReportFinalOutputList.isEmpty()) {
				for (AdvanceSearchReportOutput transOutput : advanceSearchReportFinalOutputList) {
					if ((MashreqFederatedReportConstants.ADVANCE_SEARCH_INITATION_SOURCE_FLEX
							.equalsIgnoreCase(transOutput.getInitationSource())
							|| (MashreqFederatedReportConstants.ADVANCE_SEARCH_INITATION_SOURCE_MATRIX
									.equalsIgnoreCase(transOutput.getInitationSource())))) {
						if (MashreqFederatedReportConstants.ADVANCE_SEARCH_MESSAGE_THROUGH_UAEFTS
								.equalsIgnoreCase(transOutput.getMessageThrough())) {
							flexMatrixBasedUaeftsTransactionsList.put(transOutput.getTransactionReference(),
									transOutput);
						}
					}
				}
				// process the uaefts-ccn to update the status
				if (!flexMatrixBasedUaeftsTransactionsList.isEmpty()) {
					advanceSearchReportInput
							.setFlexMatrixBasedUaeftsTransactions(flexMatrixBasedUaeftsTransactionsList);

					List<AdvanceSearchReportOutput> advanceSearchUAEFTSReportOutList = Uaefts
							.processAdvanceSearchReport(advanceSearchReportInput, componentList, reportContext);
					advanceSearchReportFinalOutputList.addAll(advanceSearchUAEFTSReportOutList);

				}
			}
		}
		return advanceSearchReportFinalOutputList;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> promptsList,
			String advanceSearchFromDatePromptKey) {
		// TODO Auto-generated method stub

		FederatedReportPromptDTO promptInfo = new FederatedReportPromptDTO();
		ReportPromptsInstanceDTO matchedPrompt = null;
		for (ReportPromptsInstanceDTO prompt : promptsList) {
			if (prompt.getPrompt().getKey().equalsIgnoreCase(advanceSearchFromDatePromptKey)) {
				matchedPrompt = prompt;
				break;
			}
		}
		if (matchedPrompt != null) {
			promptInfo.setPromptKey(matchedPrompt.getPrompt().getKey());
			promptInfo.setPromptValue(matchedPrompt.getPrompt().getPromptValue());
			promptInfo.setValueList(matchedPrompt.getPrompt().getValue());
		}
		return promptInfo;
	}

}