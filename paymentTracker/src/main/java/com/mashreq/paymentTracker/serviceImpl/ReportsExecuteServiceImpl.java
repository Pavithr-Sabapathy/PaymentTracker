package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportsExecuteService;

@Component
public class ReportsExecuteServiceImpl implements ReportsExecuteService {

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsRepository componentRepository;

	@Override
	public void executeReport(String reportName, ReportProcessingRequest reportProcessingRequest)
			throws ReportException {
		Reports reportObject = new Reports();
		try {
			reportObject = reportConfigurationService.fetchReportByName(reportName);
		} catch (Exception exception) {
			throw new ReportException("Report instance creation failed for the report" + reportName);
		}
		ReportInstanceDTO reportInstanceDTO = populateReportPromptsInstance(reportProcessingRequest, reportObject);
		/** get the component info based on report id **/
		Optional<Components> componentsOptional = componentRepository.findById(reportObject.getId());
		Components componentObject;
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportObject.getId());
		} else {
			componentObject = componentsOptional.get();
			List<ComponentDetails> componentDetailsList = componentObject.getComponentDetailsList();
			if (componentDetailsList.isEmpty()) {
				throw new ResourceNotFoundException(
						ApplicationConstants.COMPONENT_DETAILS_DOES_NOT_EXISTS + componentObject.getId());
			} else {
				List<ReportPromptsInstanceDTO> promptsMappedList = reportInstanceDTO.getPromptsList();
				Map<String, List<String>> promptsRequestMapping = promptsMappedList.stream().collect(Collectors
						.toMap(ReportPromptsInstanceDTO::getKey, ReportPromptsInstanceDTO::getPromptsValueList));

				String sourceQueryValue = promptsRequestMapping.entrySet().stream()
						.filter(promptsRequest -> ApplicationConstants.ACCOUNTINGSOURCEPROMPTS
								.contains(promptsRequest.getKey()))
						.map(map -> map.getValue().toString()).collect(Collectors.joining());
				List<ComponentDetails> componentSourceDetails = componentDetailsList.stream()
						.filter(componentDetail -> componentDetail.getQuery().equals(sourceQueryValue))
						.collect(Collectors.toList());
				ComponentDetails componentDetails = componentSourceDetails.get(0);
				populateQueryKeyString(componentDetails, reportInstanceDTO.getPromptsList());
			}
		}

	}

	private void populateQueryKeyString(ComponentDetails componentObject, List<ReportPromptsInstanceDTO> promptsList) {
		Connection connection = null;
		String queryString = componentObject.getQuery();
		try {
			PreparedStatement executePreparedStatementquery = connection.prepareStatement(queryString);
		//	populatePreparedStatementWithPromptValue(executePreparedStatementquery, promptsList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ReportInstanceDTO populateReportPromptsInstance(ReportProcessingRequest reportProcessingRequest,
			Reports reportObject) {
		ReportInstanceDTO reportInstanceObj = new ReportInstanceDTO();

		if (null != reportObject) {
			reportInstanceObj.setUserId(reportProcessingRequest.getUserId());
			reportInstanceObj.setUserName(reportProcessingRequest.getUserName());
			reportInstanceObj.setModuleId(reportObject.getModuleId());
			reportInstanceObj.setReportId(reportObject.getId());
			reportInstanceObj.setReportName(reportObject.getReportName());
			reportInstanceObj.setCreationDate(new Date());
			List<ReportPromptsInstanceDTO> reportPromptsInstanceList = populatePromptsInstance(reportProcessingRequest,
					reportObject);
			reportInstanceObj.setPromptsList(reportPromptsInstanceList);
		}
		return reportInstanceObj;
	}

	private List<ReportPromptsInstanceDTO> populatePromptsInstance(ReportProcessingRequest reportProcessingRequest,
			Reports reportObject) {
		List<ReportPromptsInstanceDTO> reportPromptsInstanceDTOList = new ArrayList<ReportPromptsInstanceDTO>();
		List<Prompts> promptsList = reportObject.getPromptList();
		if (promptsList.isEmpty()) {
			throw new ResourceNotFoundException(
					ApplicationConstants.PROMPTS_DOES_NOT_EXISTS + reportObject.getReportName());
		} else {
			List<PromptsProcessingRequest> promptsExcecutionRequest = reportProcessingRequest
					.getPrompts();
			reportPromptsInstanceDTOList = populatePromptsValue(promptsExcecutionRequest, promptsList);
		}
		return reportPromptsInstanceDTOList;
	}

	private List<ReportPromptsInstanceDTO> populatePromptsValue(List<PromptsProcessingRequest> promptsExcecutionRequest,
			List<Prompts> promptsList) {
		Map<String, PromptsProcessingRequest> promptsRequestMapping = new HashMap<String, PromptsProcessingRequest>();

		List<ReportPromptsInstanceDTO> reportPromptsInstanceList = new ArrayList<ReportPromptsInstanceDTO>();

		promptsRequestMapping = promptsExcecutionRequest.stream()
				.collect(Collectors.toMap(PromptsProcessingRequest::getKey, Function.identity()));

		promptsRequestMapping.forEach((promptKey, promptValue) -> {
			ReportPromptsInstanceDTO reportPromptsIntanceDTO = new ReportPromptsInstanceDTO();
			reportPromptsIntanceDTO.setPromptsValueList(promptValue.getPromptsValueList());
			reportPromptsIntanceDTO.setPromptValue(promptValue.getPromptValue());
			reportPromptsInstanceList.add(reportPromptsIntanceDTO);
		});
		return reportPromptsInstanceList;
	}
};
