package com.mashreq.paymentTracker.serviceImpl;

import java.io.StringReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.SnappDetailedReportInput;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service("snappDetails")
public class SnappReportServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {

	private static final Logger log = LoggerFactory.getLogger(SnappReportServiceImpl.class);
	private static final String FILENAME = "SnappReportServiceImpl";

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	LinkReportService linkReportService;

	@Autowired
	ReportOutputExecutor reportOutputExecutor;

	@Override
	public ReportInput populateBaseInputContext(ReportContext reportContext) {
		SnappDetailedReportInput snappDetailedReportInput = new SnappDetailedReportInput();
		ReportInstanceDTO reportInstance = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> promptsList = reportInstance.getPromptsList();
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(promptsList,
				MashreqFederatedReportConstants.REFERENCENUMPROMPTS);
		if (null != referenceNumPrompt) {
			snappDetailedReportInput.setReferenceNumPrompt(referenceNumPrompt);
		}
		return snappDetailedReportInput;
	}

	@Override
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportOutput> outputList = new ArrayList<ReportOutput>();
		List<ReportOutput> snappReportOutputList = new ArrayList<ReportOutput>();
		Report report = new Report();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		if (null != reportInstanceDTO) {
			report = reportConfigurationService.fetchReportByName(reportInstanceDTO.getReportName());
		}
		List<Components> componentList = componentsDAO.findAllByreportId(report.getId());
		if (componentList.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + report.getId());
		} else {
			Components componentObj = componentList.get(0);
			ReportComponentDTO reportComponent = populateReportComponent(componentObj);
			SnappDetailedReportInput snappDetailedReportInput = (SnappDetailedReportInput) reportInput;
			snappDetailedReportInput.setComponent(reportComponent);
			Set<ReportComponentDetailDTO> reportComponentDetailsList = reportComponent.getReportComponentDetails();
			for (ReportComponentDetailDTO reportComponetDetail : reportComponentDetailsList) {
				FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
				List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
				context.setQueryId(reportComponetDetail.getId());
				context.setQueryKey(reportComponetDetail.getQueryKey());
				context.setQueryString(reportComponetDetail.getQuery());
				promptsList.add(snappDetailedReportInput.getReferenceNumPrompt());
				context.setPrompts(promptsList);
				context.setExecutionId(reportContext.getExecutionId());
				snappReportOutputList = queryExecutorService.executeQuery(reportComponetDetail, context);
				if (!snappReportOutputList.isEmpty()) {
					PaymentInvestigationReportOutput paymentInvestigationReportOutput = new PaymentInvestigationReportOutput();
					paymentInvestigationReportOutput = populateReportOutputForSingleRecord(snappReportOutputList,
							MashreqFederatedReportConstants.SNAPP_MWLOG_DETAIL_KEY);
					List<Object> rowData = new ArrayList<Object>();
					rowData.add(paymentInvestigationReportOutput.getSourceRefNum());
					rowData.add(paymentInvestigationReportOutput.getDebitAccount());
					rowData.add(paymentInvestigationReportOutput.getBeneficaryAccount());
					rowData.add(paymentInvestigationReportOutput.getCurrency());
					rowData.add(paymentInvestigationReportOutput.getAmount());
					ReportOutput defaultOutput = new ReportOutput();
					defaultOutput.setComponentDetailId(paymentInvestigationReportOutput.getComponentDetailId());
					defaultOutput.setRowData(rowData);
					outputList.add(defaultOutput);
				}
			}
			if (!outputList.isEmpty()) {
				List<Map<String, Object>> rowDataMapList = reportOutputExecutor.populateRowData(outputList, report);
				List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = reportOutputExecutor
						.populateColumnDef(report);
				responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
				responseData.setData(rowDataMapList);
			}
			log.info(FILENAME + "[SnappReportServiceImpl processReport Response] -->" + responseData.toString());
		}
		return responseData;
	}

	private PaymentInvestigationReportOutput populateReportOutputForSingleRecord(
			List<ReportOutput> snappReportOutputList, String componentDetailKey) {

		PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
		if (!snappReportOutputList.isEmpty()) {
			ReportOutput defaultOutput = snappReportOutputList.get(0);
			List<Object> requestRow = defaultOutput.getRowData();
			reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
			if (MashreqFederatedReportConstants.SNAPP_MWLOG.equalsIgnoreCase(componentDetailKey)) {
				Timestamp landingTime = UtilityClass.getTimeStampRepresentation(requestRow.get(0));
				String sourceRefNum = UtilityClass.getStringRepresentation(requestRow.get(1));
				String activity = UtilityClass.getStringRepresentation(requestRow.get(2));
				String workstage = UtilityClass.getStringRepresentation(requestRow.get(3));
				String completedBy = UtilityClass.getStringRepresentation(requestRow.get(4));
				String messageContent = UtilityClass.getStringRepresentation(requestRow.get(5));

				SimpleDateFormat sdf = new SimpleDateFormat(MashreqFederatedReportConstants.VALUE_DATE_FORMAT_KEY);
				String valueDate = sdf.format(new Date(landingTime.getTime()));

				String activityStatus = MashreqFederatedReportConstants.PENDING_ACTIVITY_STATUS;
				Timestamp completionTime = landingTime;
				if (snappReportOutputList.size() > 1) {
					List<Object> responseRow = snappReportOutputList.get(1).getRowData();
					completionTime = UtilityClass.getTimeStampRepresentation(responseRow.get(0));
					activityStatus = MashreqFederatedReportConstants.COMPLETED_ACTIVITY_STATUS;
				}
				reportOutput.setLandingTime(landingTime);
				reportOutput.setActivity(activity);
				reportOutput.setCompletionTime(completionTime);
				reportOutput.setActivityStatus(activityStatus);
				reportOutput.setSourceRefNum(sourceRefNum);
				reportOutput.setWorkstage(workstage);
				reportOutput.setCompletedBy(completedBy);
				reportOutput.setValueDate(valueDate);
				reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_SNAPP);
				parseXMLMessage(messageContent, reportOutput);
			} else if (MashreqFederatedReportConstants.SNAPP_MWLOG_DETAIL_KEY.equalsIgnoreCase(componentDetailKey)) {
				String sourceRefNum = UtilityClass.getStringRepresentation(requestRow.get(0));
				reportOutput.setSourceRefNum(sourceRefNum);
				String messageContent = UtilityClass.getStringRepresentation(requestRow.get(1));
				parseXMLMessage(messageContent, reportOutput);
			}
		}
		return reportOutput;

	}

	private void parseXMLMessage(String messageContent, PaymentInvestigationReportOutput reportOutput) {

		// clean the content
		messageContent = messageContent.replace("<SOAP-ENV:Header/>", "");
		Document xml = convertStringToDocument(messageContent);
		if (xml != null) {
			System.out.println(messageContent);
			Element debitLeg = xml.getElementById("DebitLeg");
			Element creditLeg = xml.getElementById("CreditLeg");
			String amount = null;
			String currency = null;
			String beneficaryAccount = null;
			String debitAccount = null;
			if (creditLeg != null) {
				Element creditCurrency = xml.getElementById("Currency");
				if (creditCurrency != null) {
					currency = creditCurrency.getTextContent();
				}
				Element amountElement = xml.getElementById("Amount");
				if (amountElement != null) {
					amount = amountElement.getTextContent();
				}
				Element accountNumElement = xml.getElementById("AccountNo");
				if (accountNumElement != null) {
					beneficaryAccount = accountNumElement.getTextContent();
				}
				populateBeneficaryAndRecieverInfo(creditLeg, reportOutput);
			}
			if (debitLeg != null) {
				Element accountNumElement = xml.getElementById("AccountNo");
				if (accountNumElement != null) {
					debitAccount = accountNumElement.getTextContent();
				}
				if (currency == null) {
					Element debitCurrency = xml.getElementById("Currency");
					if (debitCurrency != null) {
						currency = debitCurrency.getTextContent();
					}
				}
			}
			if (null != amount) {
				reportOutput.setAmount(amount);
			}
			if (null != currency) {
				reportOutput.setCurrency(currency);
			}
			if (null != beneficaryAccount) {
				reportOutput.setBeneficaryAccount(beneficaryAccount);
			}
			if (null != debitAccount) {
				reportOutput.setDebitAccount(debitAccount);
			}
		} else {
			log.error("unable to Parse");
		}

	}

	private void populateBeneficaryAndRecieverInfo(Element creditLeg, PaymentInvestigationReportOutput reportOutput) {

		List<String> beneficaryDetails = new ArrayList<String>();
		List<String> receiverInfo = new ArrayList<String>();
		/*
		 * Element BenNameElement = creditLeg.get("BenName"); Element BenAddr1 =
		 * findElement("BenAddr1", creditLeg); Element BenAddr2 =
		 * findElement("BenAddr2", creditLeg); Element BenAddr3 =
		 * findElement("BenAddr3", creditLeg); addToList(BenNameElement,
		 * beneficaryDetails); addToList(BenAddr1, beneficaryDetails);
		 * addToList(BenAddr2, beneficaryDetails); addToList(BenAddr3,
		 * beneficaryDetails);
		 * 
		 * Element receiverBicCodeElement = findElement("AWInstBICCode", creditLeg);
		 * Element receiverNameElement = findElement("AWInstName", creditLeg); Element
		 * receiver1Element = findElement("AWInstAddr1", creditLeg); Element
		 * receiver2Element = findElement("AWInstAddr2", creditLeg); Element
		 * receiver3Element = findElement("AWInstAddr3", creditLeg);
		 * addToList(receiverBicCodeElement, receiverInfo);
		 * addToList(receiverNameElement, receiverInfo); addToList(receiver1Element,
		 * receiverInfo); addToList(receiver2Element, receiverInfo);
		 * addToList(receiver3Element, receiverInfo);
		 */ if (null != beneficaryDetails) {
			String beneficaryInfo = beneficaryDetails.stream().collect(Collectors.joining(","));
			reportOutput.setBeneficaryDetail(beneficaryInfo);
		}

		if (null != receiverInfo) {
			String receiver = receiverInfo.stream().collect(Collectors.joining(","));
			reportOutput.setReceiver(receiver);
		}
	}

	private void addToList(Element valueElement, List<String> values) {
		String value = null;
		if (valueElement != null) {
			value = valueElement.getTextContent();
		}
		if (null != value) {
			values.add(value);
		}
	}

	private Document convertStringToDocument(String messageContent) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(messageContent)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private ReportComponentDTO populateReportComponent(Components component) {
		ReportComponentDTO reportComponentDTO = new ReportComponentDTO();
		reportComponentDTO.setActive(CheckType.getCheckType(component.getActive()));
		reportComponentDTO.setComponentKey(component.getComponentKey());
		reportComponentDTO.setComponentName(component.getComponentName());
		reportComponentDTO.setId(component.getId());
		reportComponentDTO.setReportComponentDetails(populateComponentDetails(component.getComponentDetailsList()));
		return reportComponentDTO;
	}

	private Set<ReportComponentDetailDTO> populateComponentDetails(List<ComponentDetails> componentDetailsList) {
		Set<ReportComponentDetailDTO> componentDetailDTO = new HashSet<ReportComponentDetailDTO>();
		componentDetailsList.stream().forEach(componentDetails -> {
			ReportComponentDetailDTO reportComponentDetailDTO = new ReportComponentDetailDTO();
			reportComponentDetailDTO.setId(componentDetails.getId());
			reportComponentDetailDTO.setQuery(componentDetails.getQuery());
			reportComponentDetailDTO.setQueryKey(componentDetails.getQueryKey());
			reportComponentDetailDTO.setReportComponentId(componentDetails.getComponents().getId());
			componentDetailDTO.add(reportComponentDetailDTO);
		});
		return componentDetailDTO;
	}

	private FederatedReportPromptDTO getMatchedInstancePrompt(List<ReportPromptsInstanceDTO> reportPromptsList,
			String promptKey) {
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		Optional<ReportPromptsInstanceDTO> promptsOptional = reportPromptsList.stream()
				.filter(prompts -> prompts.getPrompt().getKey().equalsIgnoreCase(promptKey)).findAny();
		ReportPromptsInstanceDTO reportInstancePrompt = promptsOptional.get();
		if (null != reportInstancePrompt) {
			List<String> promptsList = new ArrayList<String>();
			if (null != reportInstancePrompt && null != reportInstancePrompt.getPrompt().getPromptValue()) {
				promptsList.add(reportInstancePrompt.getPrompt().getPromptValue());
			}
			if (null != reportInstancePrompt && !reportInstancePrompt.getPrompt().getValue().isEmpty())
				;
			{
				promptsList.addAll(reportInstancePrompt.getPrompt().getValue());
			}
			String promptValue = promptsList.stream().collect(Collectors.joining(","));
			federatedReportPromptDTO.setPromptKey(reportInstancePrompt.getPrompt().getKey());
			federatedReportPromptDTO.setPromptValue(promptValue);
		}

		return federatedReportPromptDTO;
	}

}