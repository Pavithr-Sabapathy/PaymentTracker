package com.mashreq.paymentTracker.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.GatewayDataContext;
import com.mashreq.paymentTracker.dto.PaymentInvestigationReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.service.PaymentInvestigationGatewayService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportConnector;
import com.mashreq.paymentTracker.utility.CheckType;

public class PaymentInvestigationGatewayServiceImpl implements PaymentInvestigationGatewayService {

	private static final Logger log = LoggerFactory.getLogger(PaymentInvestigationGatewayServiceImpl.class);
	private static final String FILENAME = "PaymentInvestigationGatewayServiceImpl";

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	FlexReportConnector flexReportConnector;

	@Autowired
	SwiftReportConnector swiftReportConnector;

	@Autowired
	SafeWatchReportConnector safeWatchReportConnector;

	@Override
	public void processGateway(PaymentInvestigationReportInput paymentInvestigationReportInput,
			List<Components> componentList, ReportContext reportContext) {
		GatewayDataContext gatewayDataContext = new GatewayDataContext();
		paymentInvestigationReportInput.setGatewayDataContext(gatewayDataContext);
		try {
			processComponent(paymentInvestigationReportInput, componentList, reportContext,
					MashreqFederatedReportConstants.COMPONENT_SWIFT_KEY);
			// if record found from swift
			if (gatewayDataContext.isSwiftDataFound()) {
				try {
					processComponent(paymentInvestigationReportInput, componentList, reportContext,
							MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY);
				} catch (Exception e) {

				}
			}
			if (gatewayDataContext.isGatewayDataFound()) {
				// handle the fircosoft system.right now the condition is if compliance
				// information is already there, we will not process that message.
				try {
					processComponent(paymentInvestigationReportInput, componentList, reportContext,
							MashreqFederatedReportConstants.COMPONENT_FIRCOSOFT_KEY);
				} catch (Exception exception) {
				}

			}
		} catch (Exception exception) {

		}
	}

	public void processComponent(PaymentInvestigationReportInput paymentInvestigationReportInput,
			List<Components> componentList, ReportContext reportContext, String componentKey) {

		Components matchedComponentsObj = getMatchedComponent(componentList, componentKey);
		ReportConnector reportConnector = getMatchedReportService(componentKey);
		if (matchedComponentsObj != null && reportConnector != null) {
			ReportComponentDTO matchedComponentsDTO = populateReportComponent(matchedComponentsObj);
			paymentInvestigationReportInput.setComponent(matchedComponentsDTO);
			reportConnector.processReportComponent(paymentInvestigationReportInput, reportContext);
		} else {
			log.debug("Component Missing/Matched Connector Missing for key " + componentKey);
		}
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
		} else if (connectorKey.equalsIgnoreCase(MashreqFederatedReportConstants.COMPONENT_SAFE_WATCH_KEY)) {
			reportConnector = safeWatchReportConnector;
		}
		return reportConnector;

	}

	private Components getMatchedComponent(List<Components> componentList, String componentKey) {
		Components components = componentList.stream()
				.filter(component -> component.getComponentKey().equalsIgnoreCase(componentKey)).findFirst()
				.orElse(null);
		return components;
	}

}