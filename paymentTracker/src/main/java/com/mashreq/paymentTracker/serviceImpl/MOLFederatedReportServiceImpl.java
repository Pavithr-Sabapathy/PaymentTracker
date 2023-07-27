package com.mashreq.paymentTracker.serviceImpl;

import java.io.StringReader;
import java.sql.Date;
import java.sql.SQLException;
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
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.MOLDetailedFederatedReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportControllerService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;



@Service("molDetails")
public class MOLFederatedReportServiceImpl extends ReportControllerServiceImpl implements ReportControllerService {
	
	private static final Logger log = LoggerFactory.getLogger(MOLFederatedReportServiceImpl.class);
	private static final String FILENAME = "MOLFederatedReportServiceImpl";
	

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsDAO componentsDAO;

	@Autowired
	LinkReportService linkReportService;

	@Autowired
	QueryExecutorService queryExecutorService;

	@Autowired
	CannedReportService cannedReportService;
	
	@Autowired
	ReportOutputExecutor reportOutputExecutor;


	

	@Override
	public ReportInput populateBaseInputContext(ReportContext reportContext) {
		MOLDetailedFederatedReportInput molDetailedFederatedReportInput = new MOLDetailedFederatedReportInput();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		List<ReportPromptsInstanceDTO> reportPromptsList = reportInstanceDTO.getPromptsList();
		FederatedReportPromptDTO referenceNumPrompt = getMatchedInstancePrompt(reportPromptsList,
				MashreqFederatedReportConstants.REFERENCENUMPROMPTS);
		if (null != referenceNumPrompt) {
			molDetailedFederatedReportInput.setReferenceNumPrompt(referenceNumPrompt);
		}

		return molDetailedFederatedReportInput;
	}

	@Override
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) throws SQLException {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		List<ReportOutput> molReportExecuteResponseList = new ArrayList<ReportOutput>();
		List<ReportOutput> outputList = new ArrayList<ReportOutput>();
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
			MOLDetailedFederatedReportInput molDetailedFederatedReportInput = (MOLDetailedFederatedReportInput) reportInput;
			molDetailedFederatedReportInput.setComponent(reportComponent);
			Set<ReportComponentDetailDTO> reportComponentDetailsList = reportComponent.getReportComponentDetails();
			for (ReportComponentDetailDTO reportComponetDetail : reportComponentDetailsList) {
				FederatedReportComponentDetailContext context = new FederatedReportComponentDetailContext();
				List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
				context.setQueryId(reportComponetDetail.getId());
				context.setQueryKey(reportComponetDetail.getQueryKey());
				context.setQueryString(reportComponetDetail.getQuery());
				promptsList.add(molDetailedFederatedReportInput.getReferenceNumPrompt());
				context.setPrompts(promptsList);
				context.setExecutionId(reportContext.getExecutionId());
				molReportExecuteResponseList = queryExecutorService.executeQuery(reportComponetDetail, context);
				if (!molReportExecuteResponseList.isEmpty()) {
					PaymentInvestigationReportOutput paymentInvestigationReportOutput = new PaymentInvestigationReportOutput();
					paymentInvestigationReportOutput = populateReportOutputForSingleRecord(molReportExecuteResponseList,
							MashreqFederatedReportConstants.MOL_AUTH_DATA_DETAIL_KEY);
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
			List<ReportOutput> molReportOutputList, String componentDetailKey) throws SQLException {
		PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
		if (!molReportOutputList.isEmpty()) {
			ReportOutput defaultReportOutput = molReportOutputList.get(0);
			List<Object> requestRow = defaultReportOutput.getRowData();
			reportOutput.setComponentDetailId(defaultReportOutput.getComponentDetailId());
			if (MashreqFederatedReportConstants.MOL_AUTH_DATA.equalsIgnoreCase(componentDetailKey)) {
				Timestamp landingTime = UtilityClass.getTimeStampRepresentation(requestRow.get(0));
				String Activity = UtilityClass.getStringRepresentation(requestRow.get(1));
				Timestamp CompletionTime = UtilityClass.getTimeStampRepresentation(requestRow.get(2));
			    String ActivityStatus = UtilityClass.getStringRepresentation(requestRow.get(3));
			    String SourceRefNum = UtilityClass.getStringRepresentation(requestRow.get(4));
			    String DebitAccount = UtilityClass.getStringRepresentation(requestRow.get(5));
			    String Workstage = UtilityClass.getStringRepresentation(requestRow.get(6));
			    String CompletedBy = UtilityClass.getStringRepresentation(requestRow.get(7));
			    SimpleDateFormat sdf = new SimpleDateFormat(MashreqFederatedReportConstants.VALUE_DATE_FORMAT_KEY);
				String valueDate = sdf.format(new Date(landingTime.getTime()));

				String activityStatus = MashreqFederatedReportConstants.PENDING_ACTIVITY_STATUS;
				Timestamp completionTime = landingTime;
				
				  String message = UtilityClass.getStringRepresentation(requestRow.get(8));
				   parseXMLMessage(message, reportOutput);
				  String CustomerId=UtilityClass.getStringRepresentation(requestRow.get(9));
				
			}else if(MashreqFederatedReportConstants.MOL_AUTH_DATA.endsWith(componentDetailKey)) {
				String DebitAccount=UtilityClass.getStringRepresentation(requestRow.get(0));
				 String message = UtilityClass.getStringRepresentation(requestRow.get(1));
				   parseXMLMessage(message, reportOutput);
				 }
			}
			
		      return reportOutput;
		   }
	
	
	
	private void parseXMLMessage (String message, PaymentInvestigationReportOutput reportOutput) {
	      //clean message
	      message = message.trim();
	      int lastIndex = message.indexOf("</GenericPaymentRequestDTO>");
	      if (lastIndex != -1) {
	         message = message.substring(0, message.indexOf("</GenericPaymentRequestDTO>") + 27);
	      }
	      Document xml = convertStringToDocument(message);
	      if (xml != null) {
	    	  Element rootElement = xml.getRootElement();
	         populateAmountAndCurrency(rootElement, reportOutput);
	         populateBeneficaryAndReceiver(rootElement, reportOutput);
	      } else {
	         log.error("Unable to create XML");
	      }
	   }


   

	private void populateAmountAndCurrency(Element rootElement,
						PaymentInvestigationReportOutput reportOutput) {
	    Element txnAmount =UtilityClass.findElement("txnamount", rootElement);
	      if (txnAmount != null) {
	         String txnAmountValue = txnAmount.getTextTrim();
	         if (!txnAmountValue.isEmpty()) {
	            reportOutput.setAmount(txnAmountValue);
	         }
	      }
	      Element txnCurrency = UtilityClass.findElement("txncurrency", rootElement);
	      if (txnCurrency != null) {
	         String txnCurrencyValue = txnCurrency.getTextTrim();
	         if (!txnCurrencyValue.isEmpty()) {
	            reportOutput.setCurrency(txnCurrencyValue);
	         }
	      }
					
				}

	private Document convertStringToDocument(String message) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = (Document) builder.parse(new InputSource(new StringReader(message)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void populateBeneficaryAndReceiver (Element rootElement,
			 PaymentInvestigationReportOutput reportOutput) {
	      List<Element> udfElements = new ArrayList<Element>();
	 
	     /* findElements("udfdto", rootElement, udfElements);
	      if (ExecueCoreUtil.isCollectionNotEmpty(udfElements)) {
	         String fldSortCode = null;
	         String fldBankSwiftCode = null;
	         String fldBeneBankCity = null;
	         String fldiBan = null;
	         String fldBeneName = null;
	         String fldValueDate = null;
	         for (Element element : udfElements) {
	            Element udfName = findElement("udfName", element);
	            Element udfValue = findElement("udfValue", element);
	            if (udfName != null && udfValue != null) {
	               if (udfName.getTextTrim().equalsIgnoreCase("fldsortcode")) {
	                  fldSortCode = udfValue.getTextTrim();
	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldBankSwiftCode")) {
	                  fldBankSwiftCode = udfValue.getTextTrim();
	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldBeneBankCity")) {
	                  fldBeneBankCity = udfValue.getTextTrim();
	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldiBan")) {
	                  fldiBan = udfValue.getTextTrim();
	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldBeneName")) {
	                  fldBeneName = udfValue.getTextTrim();
	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldValueDate")) {
	                  fldValueDate = udfValue.getTextTrim();
	               }
	            }
	            if (fldSortCode != null && fldBankSwiftCode != null && fldBeneBankCity != null && fldiBan != null
	                     && fldBeneName != null && fldValueDate != null) {
	               break;
	            }
	         }
	         if (null!=fldiBan) {
	            reportOutput.setBeneficaryAccount(fldiBan);
	         }
	         if (null!=fldValueDate) {
	            reportOutput.setValueDate(fldValueDate);
	         }
	         if (null!=fldBeneName)) {
	            reportOutput.setBeneficaryDetail(fldBeneName);
	         }
	         List<String> receiverInfo = new ArrayList<String>();
	         if (null!=fldSortCode)) {
	            receiverInfo.add(fldSortCode);
	         }
	         if (null!=fldBankSwiftCode) {
	            receiverInfo.add(fldBankSwiftCode);
	         }
	         if (null!=fldBeneBankCity) {
	            receiverInfo.add(fldBeneBankCity);
	         }
	         if (null!=(receiverInfo) {
	        	 String receiver = receiverInfo.stream().collect(Collectors.joining(","));
	            reportOutput.setReceiver(receiver);
	         }

	      }
	   }
	}*/

	
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
