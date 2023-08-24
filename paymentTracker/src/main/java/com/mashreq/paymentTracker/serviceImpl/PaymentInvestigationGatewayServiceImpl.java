package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.GatewayDataContext;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.exception.ExceptionCodes;
import com.mashreq.paymentTracker.exception.ReportConnectorException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.service.PaymentInvestigationGatewayService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.type.EDMSProcessType;
import com.mashreq.paymentTracker.utility.CheckType;

@Service
public class PaymentInvestigationGatewayServiceImpl implements PaymentInvestigationGatewayService {

	private static final Logger log = LoggerFactory.getLogger(PaymentInvestigationGatewayServiceImpl.class);
	private static final String FILENAME = "PaymentInvestigationGatewayServiceImpl";

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	FlexReportConnector flexReportConnector;

	@Autowired
	UAEFTSReportConnector uaeftsReportConnector;

	@Autowired
	SwiftReportConnector swiftReportConnector;

	@Autowired
	SafeWatchReportConnector safeWatchReportConnector;

	@Autowired
	FircosoftReportConnector fircosoftReportConnector;

	@Override
	public void processGateway(PaymentInvestigationReportInput paymentInvestigationReportInput,
			List<Components> componentList, ReportContext reportContext,
			List<PaymentInvestigationReportOutput> reportOutputList) throws Exception {
		GatewayDataContext gatewayDataContext = new GatewayDataContext();
		paymentInvestigationReportInput.setGatewayDataContext(gatewayDataContext);
		processComponent(paymentInvestigationReportInput, componentList, reportContext,
				MashreqFederatedReportConstants.COMPONENT_SWIFT_KEY, reportOutputList);
		if (!gatewayDataContext.isGatewayDataFound()) {
			processComponent(paymentInvestigationReportInput, componentList, reportContext,
					MashreqFederatedReportConstants.COMPONENT_UAEFTS_KEY, reportOutputList);
			// if record found from swift
			if (gatewayDataContext.isSwiftDataFound()) {
				try {
					processComponent(paymentInvestigationReportInput, componentList, reportContext,
							MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY, reportOutputList);
				} catch (ReportConnectorException exception) {
					populateFailedSystemsData(paymentInvestigationReportInput, exception,
							MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY);
				}
			}
			if (gatewayDataContext.isGatewayDataFound()) {
				// handle the fircosoft system.right now the condition is if compliance
				// information is already there, we will not process that message.
				try {
					processComponent(paymentInvestigationReportInput, componentList, reportContext,
							MashreqFederatedReportConstants.COMPONENT_FIRCOSOFT_KEY, reportOutputList);
				} catch (ReportConnectorException exception) {
					populateFailedSystemsData(paymentInvestigationReportInput, exception,
							MashreqFederatedReportConstants.COMPONENT_FIRCOSOFT_KEY);
				}

			}
		}
	}

	public List<? extends ReportOutput> processComponent(
			PaymentInvestigationReportInput paymentInvestigationReportInput, List<Components> componentList,
			ReportContext reportContext, String componentKey, List<PaymentInvestigationReportOutput> reportOutputList)
			throws ReportConnectorException {
		List<? extends ReportOutput> reportOutput = new ArrayList<ReportOutput>();
		Components matchedComponentsObj = getMatchedComponent(componentList, componentKey);
		ReportConnector reportConnector = getMatchedReportService(componentKey);
		if (matchedComponentsObj != null && reportConnector != null) {
			ReportComponentDTO matchedComponentsDTO = populateReportComponent(matchedComponentsObj);
			paymentInvestigationReportInput.setComponent(matchedComponentsDTO);
			reportOutput = reportConnector.processReportComponent(paymentInvestigationReportInput, reportContext);
		} else {
			log.debug("Component Missing/Matched Connector Missing for key " + componentKey);
		}
		return reportOutput;
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

	private ReportConnector getMatchedReportService(String connectorKey) {
		ReportConnector reportConnector = null;
		if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_FLEX_KEY)) {
			reportConnector = flexReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_SWIFT_KEY)) {
			reportConnector = swiftReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_UAEFTS_KEY)) {
			reportConnector = uaeftsReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY)) {
			reportConnector = safeWatchReportConnector;
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_FIRCOSOFT_KEY)) {
			reportConnector = fircosoftReportConnector;
		}
		return reportConnector;

	}

	private Components getMatchedComponent(List<Components> componentList, String componentKey) {
		Components components = componentList.stream()
				.filter(component -> component.getComponentKey().equalsIgnoreCase(componentKey)).findFirst()
				.orElse(null);
		return components;
	}

	@Override
	public void processChannels(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList)throws Exception {
		processMatrixSystem(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
		if (!paymentInvestigationReportInput.isChannelDataFound()) {
			paymentInvestigationReportInput.setEdmsProcessType(EDMSProcessType.FTO);
			processEDMS(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
			if (!paymentInvestigationReportInput.isChannelDataFound()) {
				processSnapp(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
				if (!paymentInvestigationReportInput.isChannelDataFound()) {
					processMOL(paymentInvestigationReportInput, reportContext, componentList, reportOutputList);
				}
			}
		}
	}

	private void processMOL(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) throws Exception {
		try {
			List<? extends ReportOutput> molComponentData = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_MOL_KEY, reportOutputList);
			molComponentData.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!molComponentData.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
			}
		} catch (ReportConnectorException exception) {
			populateFailedSystemsData(paymentInvestigationReportInput, exception,
					MashreqFederatedReportConstants.SOURCE_SYSTEM_MOL);

		}

	}

	private void processSnapp(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) throws Exception {
		try {
			List<? extends ReportOutput> snappComponentData = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_SNAPP_KEY,
					reportOutputList);
			snappComponentData.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!snappComponentData.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
			}
		}catch (ReportConnectorException exception) {
			populateFailedSystemsData(paymentInvestigationReportInput, exception,
					MashreqFederatedReportConstants.SOURCE_SYSTEM_SNAPP);


		}

	}

	private void processEDMS(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) throws Exception {
		try {
			List<? extends ReportOutput> edmsOutputList = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_EMDS_KEY, reportOutputList);
			edmsOutputList.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!edmsOutputList.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
			}
		} catch (ReportConnectorException exception) {
			populateFailedSystemsData(paymentInvestigationReportInput, exception,
					MashreqFederatedReportConstants.SOURCE_SYSTEM_EDMS);
		}
	}

	private void processMatrixSystem(PaymentInvestigationReportInput paymentInvestigationReportInput,
			ReportContext reportContext, List<Components> componentList,
			List<PaymentInvestigationReportOutput> reportOutputList) throws Exception {
		try {
			List<? extends ReportOutput> matrixPaymentOutputList = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_MATRIX_PAYMENT_KEY,
					reportOutputList);
			matrixPaymentOutputList.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!matrixPaymentOutputList.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
				paymentInvestigationReportInput.getMatrixReportContext().setMatrixDataFound(true);
			}
		} catch (ReportConnectorException exception) {
			populateFailedSystemsData(paymentInvestigationReportInput, exception,
					MashreqFederatedReportConstants.SOURCE_SYSTEM_MATRIX_PAYMENT);

		}

		try {
			List<? extends ReportOutput> matrixPortalOutputList = processComponent(paymentInvestigationReportInput,
					componentList, reportContext, MashreqFederatedReportConstants.COMPONENT_MATRIX_PORTAL_KEY,
					reportOutputList);
			matrixPortalOutputList.stream().forEach(output -> {
				PaymentInvestigationReportOutput piReportOutput = (PaymentInvestigationReportOutput) output;
				reportOutputList.add(piReportOutput);
			});
			if (!matrixPortalOutputList.isEmpty()) {
				paymentInvestigationReportInput.setChannelDataFound(true);
				paymentInvestigationReportInput.getMatrixReportContext().setMatrixDataFound(true);
			}
		} catch (ReportConnectorException exception) {
			populateFailedSystemsData(paymentInvestigationReportInput, exception,
					MashreqFederatedReportConstants.SOURCE_SYSTEM_MATRIX_PORTAL);
		}
	}

	public void populateFailedSystemsData(PaymentInvestigationReportInput reportInputContext,
			ReportConnectorException exception, String sourceSystem) throws Exception {

		PaymentInvestigationReportOutput output = new PaymentInvestigationReportOutput();
		String activity = null;
		if (ExceptionCodes.REPORT_EXECUTION_QUERY_EXECUTION_FAILURE == exception.getCode()) {
			activity = MashreqFederatedReportConstants.SYSTEM_NOT_RESPONDED_MESSAGE;
			if (MashreqFederatedReportConstants.APPLY_COLOR_NOTATION) {
				activity = "<font color=" + MashreqFederatedReportConstants.SYSTEM_NOT_RESPONDED_COLOR + ">" + activity
						+ "</font>";
				sourceSystem = "<font color=" + MashreqFederatedReportConstants.SYSTEM_NOT_RESPONDED_COLOR + ">"
						+ sourceSystem + "</font>";
			}

		} else if (ExceptionCodes.REPORT_EXECUTION_INPUT_VALIDATION_FAILED == exception.getCode()) {
			activity = MashreqFederatedReportConstants.INSUFFICIENT_INPUT_MESSAGE;
			if (MashreqFederatedReportConstants.APPLY_COLOR_NOTATION) {
				activity = "<font color=" + MashreqFederatedReportConstants.INSUFFICIENT_INPUT_COLOR + ">" + activity
						+ "</font>";
				sourceSystem = "<font color=" + MashreqFederatedReportConstants.INSUFFICIENT_INPUT_COLOR + ">"
						+ sourceSystem + "</font>";
			}
		} else {
			throw new Exception();
		}
		output.setActivity(activity);
		output.setSource(sourceSystem);
		reportInputContext.getFailedSystemOutputs().add(output);
	}

}