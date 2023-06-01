package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.CannedReportInstance;
import com.mashreq.paymentTracker.dto.CannedReportInstanceComponent;
import com.mashreq.paymentTracker.dto.FederatedReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.dto.SWIFTDetailedFederatedReportDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.utility.CheckType;

public class SwiftDetailedReportServiceImpl {
	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsRepository componentRepository;

	public void processSwiftDetailReport(String reportName, ReportProcessingRequest reportProcessingRequest) {
		Reports reportObject = reportConfigurationService.fetchReportByName(reportName);
		Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(reportObject.getId());
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportObject.getId());
		} else {
			List<Components> componentsList = componentsOptional.get();
			for (Components component : componentsList) {
				if (component.getActive().equals(CheckType.YES.toString())) {
					SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO = populateBaseInputContext(
							reportObject.getPromptList(), reportProcessingRequest.getPrompts());
					// TODO
					// swiftDetailedReportDTO.setComponent(component);
					processSwiftDetailedReport(swiftDetailedReportDTO, component);
				}

			}
		}
	}

	private Object processSwiftDetailedReport(SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO,
			Components component) {
		FederatedReportPromptDTO detailedType = swiftDetailedReportDTO.getDetailedType();
		switch (detailedType.getPromptValue()) {
		case "RMESG":
		case "RINTV":
			return populateRMesgDetailData(swiftDetailedReportDTO, component);

		}
		return null;
	}

	private Object populateRMesgDetailData(SWIFTDetailedFederatedReportDTO swiftDetailedReportDTO,
			Components component) {
		MessageDetailsFederatedReportInput reportInput = new MessageDetailsFederatedReportInput();
		reportInput.setMessageSubFormatPrompt(swiftDetailedReportDTO.getMessageSubFormatPrompt());
		reportInput.setMessageTypePrompt(swiftDetailedReportDTO.getMessageTypePrompt());
		reportInput.setReferenceNumPrompt(swiftDetailedReportDTO.getReferenceNumPrompt());
		return processMessageDetailsReport(reportInput, component);
	}

	private Object processMessageDetailsReport(MessageDetailsFederatedReportInput messagingDetailsInput,
			Components component) {
		List<ComponentDetails> componentDetailsList = component.getComponentDetailsList();
		if (!componentDetailsList.isEmpty()) {
			String rmesgQuery = ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RMESG;
			processMessageDetailsComponentDetail(component, componentDetailsList, messagingDetailsInput, rmesgQuery);

		}
		return null;
	}

	private void processMessageDetailsComponentDetail(Components component, List<ComponentDetails> componentDetailsList,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey) {
		if (!componentDetailsList.isEmpty()) {
			ComponentDetails componentDetails = getMatchedComponentDetails(componentDetailsList, componentDetailKey);
			if (ApplicationConstants.MESSAGE_DETAILS_SWIFT_MSG_RMESG.equalsIgnoreCase(componentDetailKey)) {
				processMessageDetailsRMesgQuery(component, componentDetails, messagingDetailsInput,
						componentDetailKey);
			}

		}
	}

	private ComponentDetails getMatchedComponentDetails(List<ComponentDetails> componentDetailsList, String componentDetailKey) {

		ComponentDetails componentDetailsObject = componentDetailsList.stream().filter(
				component -> componentDetailKey.equalsIgnoreCase(component.getQueryKey())).findAny().orElse(null);
		return componentDetailsObject;
	}

	private void processMessageDetailsRMesgQuery(Components component, ComponentDetails componentDetails,
			MessageDetailsFederatedReportInput messagingDetailsInput, String componentDetailKey) {
		  List<FederatedReportPromptDTO> promptsList = new ArrayList<FederatedReportPromptDTO>();
		  promptsList.add(messagingDetailsInput.getReferenceNumPrompt());
		  promptsList.add(messagingDetailsInput.getMessageSubFormatPrompt());
		  processComponentDetail(component,componentDetails,promptsList);
	}

	private List<FederatedReportOutput> processComponentDetail(Components component, ComponentDetails componentDetails,
			List<FederatedReportPromptDTO> promptsList) {
		// TODO Auto-generated method stub
		// Ramalashmi - Common for logic... Establish connection replace the promtps value and execute the query
		String query = componentDetails.getQuery();
		StringBuilder queryBuilder = new StringBuilder();
		List<FederatedReportOutput> federatedReportOutputList = new ArrayList<FederatedReportOutput>();
		promptsList.forEach(prompts -> {
			if (null != prompts.getPromptKey() && query.indexOf(prompts.getPromptKey()) > 0) {
				if (null != prompts.getPromptValue()) {
					String promptsValue = prompts.getPromptValue();
					queryBuilder.append(query.replace(prompts.getPromptKey(), promptsValue));
				}
			}

		});

		try {
			Class.forName(ApplicationConstants.DRIVER_CLASS_NAME);
			Connection connection;
			connection = DriverManager.getConnection(ApplicationConstants.FLEX_DATABASE_URL,
					ApplicationConstants.DATABASE_USERNAME, ApplicationConstants.DATABASE_PASSWORD);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
			if (resultSet != null) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (resultSet.next()) {
					FederatedReportOutput federatedReportOutput = new FederatedReportOutput();
					List<Object> rowData = new ArrayList<Object>();
					List<String> columnLabelList = new ArrayList<String>();
					for (int index = 1; index < columnCount; index++) {
						Object colValue = resultSet.getObject(index);
						rowData.add(colValue);
						String columnLabel = metaData.getColumnLabel(index);
						columnLabelList.add(columnLabel);
					}
					federatedReportOutput.setRowData(rowData);
					federatedReportOutput.setColumnLabels(columnLabelList);
					federatedReportOutputList.add(federatedReportOutput);
				}
			}
			connection.close();
			return federatedReportOutputList;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return federatedReportOutputList;
	}

	private SWIFTDetailedFederatedReportDTO populateBaseInputContext(List<Prompts> reportInstancePromptsList,
			List<PromptsProcessingRequest> uiPromptsRequest) {
		SWIFTDetailedFederatedReportDTO SWIFTDetailedFederatedReportDTO = new SWIFTDetailedFederatedReportDTO();
		Map<String, PromptsProcessingRequest> promptsRequestMapping = uiPromptsRequest.stream()
				.collect(Collectors.toMap(PromptsProcessingRequest::getKey, Function.identity()));
		List<FederatedReportPromptDTO> federatedReportPromptDTOList = new ArrayList<FederatedReportPromptDTO>();
		promptsRequestMapping.forEach((promptKey, promptValue) -> {
			FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
			federatedReportPromptDTO.setPromptKey(promptKey);
			federatedReportPromptDTO.setPromptValue(promptValue.getPromptValue());
			federatedReportPromptDTOList.add(federatedReportPromptDTO);
		});

		federatedReportPromptDTOList.stream().forEach(fedratedReportPrompt -> {
			if (fedratedReportPrompt.getPromptKey().equalsIgnoreCase(ApplicationConstants.AID_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setAidPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey().equalsIgnoreCase(ApplicationConstants.S_UMIDH_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setUmidhPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey().equalsIgnoreCase(ApplicationConstants.S_UMIDL_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setUmidlPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.SWIFT_DETAILED_REPORT_TYPE_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setDetailedType(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_DETAILS_REFERENCE_NUM_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setReferenceNumPrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_DETAILS_MESSAGE_TYPE_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setMessageTypePrompt(fedratedReportPrompt);
			} else if (fedratedReportPrompt.getPromptKey()
					.equalsIgnoreCase(ApplicationConstants.MESSAGE_DETAILS_MESSAGE_SUB_FORMAT_PROMPT_KEY)) {
				SWIFTDetailedFederatedReportDTO.setMessageSubFormatPrompt(fedratedReportPrompt);
			}
		});
		return SWIFTDetailedFederatedReportDTO;
	}

}