package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.AdvanceSearchReportService;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.EdmsProcessService;
import com.mashreq.paymentTracker.service.FlexFederatedReportService;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.MatrixPaymentReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.UAEFTSReportService;

@Component
public class AdvanceSearchReportServiceImpl implements AdvanceSearchReportService {

	private static final Logger log = LoggerFactory.getLogger(AdvanceSearchReportServiceImpl.class);
	private static final String FILENAME = "AdvanceSearchReportServiceImpl";

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	CannedReportService cannedReportService;

	@Autowired
	private ComponentsRepository componentRepository;

	@Autowired
	FlexFederatedReportService flexFederatedReportService;

	@Autowired
	MatrixPaymentReportService matrixPaymentReportService;

	@Autowired
	EdmsProcessService edmsProcessService;

	@Autowired
	UAEFTSReportService UAEFTSReportService;

	@Autowired
	LinkReportService linkReportService;
	
	public ReportExecuteResponseData processAdvanceSearchReport(ReportInstanceDTO reportInstanceDTO,
			ReportContext reportContext) {

		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = null;
		List<Components> activeComponentList = new ArrayList<Components>();
		
		/** fetch the report details based on report name **/
		Report report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		CannedReport cannedReport = cannedReportService.populateCannedReportInstance(report);
		Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(cannedReport.getId());
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + cannedReport.getId());
		} else {
			List<Components> componentList = componentsOptional.get();
			if (!componentList.isEmpty()) {
				activeComponentList = componentList.stream().filter(component -> component.getActive().equals("Y"))
						.collect(Collectors.toList());
			}
		}
		if (!activeComponentList.isEmpty()) {
			AdvanceSearchReportInput advanceSearchReportInput = populateBaseInputContext(
					reportInstanceDTO.getPromptsList());
			List<AdvanceSearchReportOutput> advanceSearchReportOutputList = processDetailedReport(
					advanceSearchReportInput, activeComponentList, reportContext);
			advanceSearchReportOutputList = rearrangeAndFillReportData(advanceSearchReportOutputList,
					advanceSearchReportInput);
			List<FederatedReportOutput> federatedReportOutputList = populateDataToObjectForm(
					advanceSearchReportOutputList, report);
			if (!federatedReportOutputList.isEmpty()) {
				List<Map<String, Object>> rowDataMapList = populateRowData(federatedReportOutputList, report);
				reportExecuteResponseCloumnDefList = populateColumnDef(report);
				responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
				responseData.setData(rowDataMapList);
			}

		}
		return responseData;
	}

	private List<ReportExecuteResponseColumnDefDTO> populateColumnDef(Report reportObject) {
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = new ArrayList<ReportExecuteResponseColumnDefDTO>();
		try {
			List<Metrics> metricsList = reportObject.getMetricsList();
			metricsList.stream().forEach(metrics -> {
				ReportExecuteResponseColumnDefDTO reportExecuteResponseCloumnDef = new ReportExecuteResponseColumnDefDTO();
				reportExecuteResponseCloumnDef.setField(metrics.getDisplayName());
				reportExecuteResponseCloumnDefList.add(reportExecuteResponseCloumnDef);
			});
			List<String> metricsWithLinkList = prepareLinkReportInfo(reportObject);
			reportExecuteResponseCloumnDefList.stream().forEach(colummnDef -> {
				if (metricsWithLinkList.contains(colummnDef.getField())) {
					colummnDef.setLinkExists(Boolean.TRUE);
				}
			});

		} catch (JpaSystemException exception) {
			log.error(FILENAME + " [Exception Occured] " + exception.getMessage());
		} catch (ResourceNotFoundException exception) {
			log.error(FILENAME + " [Exception Occured] " + exception.getMessage());
		}
		return reportExecuteResponseCloumnDefList;
	}

	private List<String> prepareLinkReportInfo(Report reportObject) {
		List<String> metricsWithLinks = new ArrayList<String>();
		List<LinkedReportResponseDTO> linkedreportResponseDTOList = linkReportService
				.fetchLinkedReportByReportId(reportObject.getId());
		linkedreportResponseDTOList.stream().forEach(linkedreportResponseDTO -> {
			metricsWithLinks.add(linkedreportResponseDTO.getSourceMetricName());
		});
		return metricsWithLinks;
	}
	
	private List<Map<String, Object>> populateRowData(List<FederatedReportOutput> flexReportExecuteResponseList,
			Report report) {
		List<Map<String, Object>> rowDataList = new ArrayList<Map<String, Object>>();
		List<Metrics> reportMetricsList = report.getMetricsList();
		List<String> metricsDisplayNameList = reportMetricsList.stream().map(Metrics::getDisplayName)
				.collect(Collectors.toList());
		Map<String, Object> rowMap = new HashMap<String, Object>();
		flexReportExecuteResponseList.stream().forEach(flexReport -> {
			List<Object> dataList = flexReport.getRowData();

			Iterator<Object> ik = dataList.iterator();
			Iterator<String> iv = metricsDisplayNameList.iterator();

			while (ik.hasNext() && iv.hasNext()) {
				rowMap.put(iv.next(), ik.next());
			}

			rowDataList.add(rowMap);

		});
		return rowDataList;
	}

 
	private List<FederatedReportOutput> populateDataToObjectForm(
			List<AdvanceSearchReportOutput> advanceSearchReportOutputList, Report report) {
		List<FederatedReportOutput> data = new ArrayList<FederatedReportOutput>();
		for (AdvanceSearchReportOutput output : advanceSearchReportOutputList) {
			FederatedReportOutput defaultOutput = new FederatedReportOutput();
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
				public void run() {
					List<AdvanceSearchReportOutput> advanceSearchFlexReportOutList = flexFederatedReportService
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
				public void run() {
					List<AdvanceSearchReportOutput> advanceSearchEdmsReportOutList = edmsProcessService
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
					List<AdvanceSearchReportOutput> advanceSearchUAEFTSReportOutList = UAEFTSReportService
							.processUAEFTSReport(advanceSearchReportInput, componentList, reportContext);
					advanceSearchReportFinalOutputList.addAll(advanceSearchUAEFTSReportOutList);
				}
			}
		}
		return advanceSearchReportFinalOutputList;
	}

	private AdvanceSearchReportInput populateBaseInputContext(List<ReportPromptsInstanceDTO> promptsList) {
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