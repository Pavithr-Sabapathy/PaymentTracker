package com.mashreq.paymentTracker.serviceImpl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jdom.Element;
import org.xml.sax.InputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jdom.Document;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.FlexDetailedReportInput;
import com.mashreq.paymentTracker.dto.MOLDetailedFederatedReportInput;
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
import com.mashreq.paymentTracker.utility.UtilityClass;

@Service
public class MOLReportConnector extends ReportConnector {

	@Autowired
	QueryExecutorService queryExecutorService;

	private static final Logger log = LoggerFactory.getLogger(MOLReportConnector.class);
	private static final String FILENAME = "MOLReportConnector";

	@Override
	public List<? extends ReportOutput> processReportComponent(ReportInput reportInput, ReportContext reportContext) {
		if (reportInput instanceof FlexDetailedReportInput) {
			MOLDetailedFederatedReportInput molDetailedFederatedReportInput = (MOLDetailedFederatedReportInput) reportInput;
			return molDetailedReport(molDetailedFederatedReportInput, reportContext);
		} else if (reportInput instanceof PaymentInvestigationReportInput paymentInvestigationReportInput) {

		}

		return null;
	}

	private List<? extends ReportOutput> molDetailedReport(
			MOLDetailedFederatedReportInput molDetailedFederatedReportInput, ReportContext reportContext) {
		List<ReportDefaultOutput> outputList = new ArrayList<ReportDefaultOutput>();
		ReportComponentDTO componentObj = molDetailedFederatedReportInput.getComponent();
		if (null != componentObj) {
			Set<ReportComponentDetailDTO> componentDetailList = componentObj.getReportComponentDetails();
			if (!componentDetailList.isEmpty()) {
				componentDetailList.stream().forEach(reportComponetDetail -> {

					ReportComponentDetailContext context = new ReportComponentDetailContext();
					List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
					context.setQueryId(reportComponetDetail.getId());
					context.setQueryKey(reportComponetDetail.getQueryKey());
					context.setQueryString(reportComponetDetail.getQuery());
					promptsList.add(molDetailedFederatedReportInput.getReferenceNumPrompt());
					context.setPrompts(promptsList);
					context.setExecutionId(reportContext.getExecutionId());
					List<ReportDefaultOutput> molReportOutputList = queryExecutorService
							.executeQuery(reportComponetDetail, context);
					if (!molReportOutputList.isEmpty()) {
						PaymentInvestigationReportOutput paymentInvestigationReportOutput = new PaymentInvestigationReportOutput();
						paymentInvestigationReportOutput = populateReportOutputForSingleRecord(molReportOutputList,
								MashreqFederatedReportConstants.MOL_AUTH_DATA_DETAIL_KEY);

						List<Object> rowData = new ArrayList<Object>();
						rowData.add(paymentInvestigationReportOutput.getSourceRefNum());
						rowData.add(paymentInvestigationReportOutput.getDebitAccount());
						rowData.add(paymentInvestigationReportOutput.getBeneficaryAccount());
						rowData.add(paymentInvestigationReportOutput.getCurrency());
						rowData.add(paymentInvestigationReportOutput.getAmount());
						ReportDefaultOutput defaultOutput = new ReportDefaultOutput();
						defaultOutput.setComponentDetailId(paymentInvestigationReportOutput.getComponentDetailId());
						defaultOutput.setRowData(rowData);
						outputList.add(defaultOutput);
					}

				});

			}
		}

		return null;
	}

	private PaymentInvestigationReportOutput populateReportOutputForSingleRecord(
			List<ReportDefaultOutput> molReportOutputList, String componentDetailKey) {
		PaymentInvestigationReportOutput reportOutput = new PaymentInvestigationReportOutput();
		if (!molReportOutputList.isEmpty()) {
			ReportDefaultOutput defaultOutput = molReportOutputList.get(0);
			List<Object> rowData = defaultOutput.getRowData();
			reportOutput.setComponentDetailId(defaultOutput.getComponentDetailId());
			if (MashreqFederatedReportConstants.MOL_AUTH_DATA_KEY.equalsIgnoreCase(componentDetailKey)) {
				{
					reportOutput.setLandingTime(UtilityClass.getTimeStampRepresentation(rowData.get(0)));
					reportOutput.setActivity(UtilityClass.getStringRepresentation(rowData.get(1)));
					reportOutput.setCompletionTime(UtilityClass.getTimeStampRepresentation(rowData.get(2)));
					reportOutput.setActivityStatus(UtilityClass.getStringRepresentation(rowData.get(3)));
					reportOutput.setSourceRefNum(UtilityClass.getStringRepresentation(rowData.get(4)));
					reportOutput.setDebitAccount(UtilityClass.getStringRepresentation(rowData.get(5)));
					reportOutput.setWorkstage(UtilityClass.getStringRepresentation(rowData.get(6)));
					reportOutput.setCompletedBy(UtilityClass.getStringRepresentation(rowData.get(7)));
					reportOutput.setSource(MashreqFederatedReportConstants.SOURCE_SYSTEM_MOL);
					reportOutput.setCustomerId(UtilityClass.getStringRepresentation(rowData.get(9)));
					// parseXMLMessage(message, reportOutput);
				}
			} else if (MashreqFederatedReportConstants.MOL_AUTH_DATA_DETAIL_KEY.equalsIgnoreCase(componentDetailKey)) {
				reportOutput.setDebitAccount(UtilityClass.getStringRepresentation(rowData.get(0)));
				// parseXMLMessage(message, reportOutput);

			}

		}
		return reportOutput;
	}

	private void parseXMLMessage(String message, PaymentInvestigationReportOutput reportOutput) {
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

	private void populateBeneficaryAndReceiver(Element rootElement, PaymentInvestigationReportOutput reportOutput) {
		List<Element> udfElements = new ArrayList<Element>();
//		 findElements("udfdto", rootElement, udfElements);
//	      if (udfElements!=null) {
//	         String fldSortCode = null;
//	         String fldBankSwiftCode = null;
//	         String fldBeneBankCity = null;
//	         String fldiBan = null;
//	         String fldBeneName = null;
//	         String fldValueDate = null;
//	         for (Element element : udfElements) {
//	            Element udfName = findElement("udfName", element);
//	            Element udfValue = findElement("udfValue", element);
//	            if (udfName != null && udfValue != null) {
//	               if (udfName.getTextTrim().equalsIgnoreCase("fldsortcode")) {
//	                  fldSortCode = udfValue.getTextTrim();
//	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldBankSwiftCode")) {
//	                  fldBankSwiftCode = udfValue.getTextTrim();
//	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldBeneBankCity")) {
//	                  fldBeneBankCity = udfValue.getTextTrim();
//	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldiBan")) {
//	                  fldiBan = udfValue.getTextTrim();
//	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldBeneName")) {
//	                  fldBeneName = udfValue.getTextTrim();
//	               } else if (udfName.getTextTrim().equalsIgnoreCase("fldValueDate")) {
//	                  fldValueDate = udfValue.getTextTrim();
//	               }
//	            }
//	            if (fldSortCode != null && fldBankSwiftCode != null && fldBeneBankCity != null && fldiBan != null
//	                     && fldBeneName != null && fldValueDate != null) {
//	               break;
//	            }
//	         }
//	         if (fldiBan!=null) {
//	            reportOutput.setBeneficaryAccount(fldiBan);
//	         }
//	         if (fldValueDate!=null) {
//	            reportOutput.setValueDate(fldValueDate);
//	         }
//	         if (fldBeneName!=null) {
//	            reportOutput.setBeneficaryDetail(fldBeneName);
//	         }
//	         List<String> receiverInfo = new ArrayList<String>();
//	         if (fldSortCode!=null) {
//	            receiverInfo.add(fldSortCode);
//	         }
//	         if (fldBankSwiftCode!=null) {
//	            receiverInfo.add(fldBankSwiftCode);
//	         }
//	         if (fldBeneBankCity!=null) {
//	            receiverInfo.add(fldBeneBankCity);
//	         }
//	         if (receiverInfo!=null) {
//	            String receiver = ExecueCoreUtil.joinCollection(receiverInfo, ",");
//	            reportOutput.setReceiver(receiver);
//	         }

	}

	private void populateAmountAndCurrency(Element rootElement, PaymentInvestigationReportOutput reportOutput) {
		Element txnAmount = UtilityClass.findElement("txnamount", rootElement);
		if (txnAmount != null) {
			String txnAmountValue = txnAmount.getTextTrim();
			if (txnAmountValue != null) {
				reportOutput.setAmount(txnAmountValue);
			}
		}
		Element txnCurrency = UtilityClass.findElement("txncurrency", rootElement);
		if (txnCurrency != null) {
			String txnCurrencyValue = txnCurrency.getTextTrim();
			if (txnCurrencyValue != null) {
				reportOutput.setCurrency(txnCurrencyValue);
			}
		}

	}

	private Document convertStringToDocument(String messageContent) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = (Document) builder.parse(new InputSource(new StringReader(messageContent)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
