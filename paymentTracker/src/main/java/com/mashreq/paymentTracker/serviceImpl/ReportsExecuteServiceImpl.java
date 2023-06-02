package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.FlexReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.LinkedReportRequestDTO;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseMetaDTO;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportsExecuteService;

@Component
public class ReportsExecuteServiceImpl implements ReportsExecuteService {

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsRepository componentRepository;

	@Autowired
	LinkReportService linkReportService;

	private static final Logger log = LoggerFactory.getLogger(ReportsExecuteServiceImpl.class);
	private static final String FILENAME = "ReportsExecuteServiceImpl";

	@Override
	public FlexReportExecuteResponseData executeReport(String reportName,
			ReportProcessingRequest reportProcessingRequest) throws ReportException {
		Reports reportObject = new Reports();
		FlexReportExecuteResponseData flexReportExecuteResponseData = new FlexReportExecuteResponseData();
		ReportExecuteResponseMetaDTO reportExecutionMetaDTO = new ReportExecuteResponseMetaDTO();
		try {
			Date startTime = new Date();
			reportObject = reportConfigurationService.fetchReportByName(reportName);
			ReportInstanceDTO reportInstanceDTO = populateReportPromptsInstance(reportProcessingRequest, reportObject);
			/** get the component info based on report id **/
			Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(reportObject.getId());
			 List<Components> componentObject = new ArrayList<Components>();
			if (componentsOptional.isEmpty()) {
				throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + reportObject.getId());
			} else {
				componentObject = componentsOptional.get();
				Components component = componentObject.get(0);
				List<ComponentDetails> componentDetailsList = component.getComponentDetailsList();
				if (componentDetailsList.isEmpty()) {
					throw new ResourceNotFoundException(
							ApplicationConstants.COMPONENT_DETAILS_DOES_NOT_EXISTS + component.getId());
				} else {
					List<ReportPromptsInstanceDTO> promptsMappedList = reportInstanceDTO.getPromptsList();

					ReportPromptsInstanceDTO promptsAccountingFilter = promptsMappedList.stream().filter(
							prompts -> prompts.getKey().equalsIgnoreCase(ApplicationConstants.ACCOUNTINGSOURCEPROMPTS))
							.findFirst().orElse(null);
					if (null != promptsAccountingFilter && null != promptsAccountingFilter.getKey()) {
						List<ComponentDetails> componentSourceDetails = componentDetailsList.stream()
								.filter(componentDetail -> componentDetail.getQueryKey()
										.equalsIgnoreCase(promptsAccountingFilter.getPromptsValueList().get(0)))
								.collect(Collectors.toList());
						ComponentDetails componentDetails = componentSourceDetails.get(0);
						List<FlexReportExecuteResponseData> flexReportList = populateDynamicQuery(componentDetails,
								reportInstanceDTO.getPromptsList());
						List<Map<String, Object>> rowDataMapList = populateRowData(flexReportList, reportObject);
						List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = populateColumnDef(
								flexReportList, reportObject);
						flexReportExecuteResponseData.setColumnDefs(reportExecuteResponseCloumnDefList);
						flexReportExecuteResponseData.setData(rowDataMapList);
					}
					Date endTime = new Date();
					reportExecutionMetaDTO.setStartTime(startTime.toString());
					reportExecutionMetaDTO.setEndTime(endTime.toString());
					reportExecutionMetaDTO.setReportId(reportName);
					flexReportExecuteResponseData.setMeta(reportExecutionMetaDTO);
				}
			}
		} catch (Exception exception) {
			throw new ReportException("Report instance creation failed for the report" + reportName);
		}

		return flexReportExecuteResponseData;
	}

	private List<ReportExecuteResponseColumnDefDTO> populateColumnDef(
			List<FlexReportExecuteResponseData> flexReportList, Reports reportObject) {
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = new ArrayList<ReportExecuteResponseColumnDefDTO>();
		ReportExecuteResponseColumnDefDTO reportExecuteResponseCloumnDef = new ReportExecuteResponseColumnDefDTO();
		List<Metrics> metricsList = reportObject.getMetricsList();
		metricsList.stream().forEach(metrics -> {
			reportExecuteResponseCloumnDef.setField(metrics.getDisplayName());
			reportExecuteResponseCloumnDefList.add(reportExecuteResponseCloumnDef);
		});
		try {
			LinkedReportRequestDTO linkedReportRequestDTO = linkReportService
					.fetchLinkedReportByReportId(reportObject.getId());

			if (null != linkedReportRequestDTO) {
				Long sourceMetricValue = linkedReportRequestDTO.getSourceMetricId();
				reportExecuteResponseCloumnDef.setLinkExists(null != sourceMetricValue ? true : false);
			}
		} catch (JpaSystemException exception) {
			log.error(FILENAME + " [Exception Occured] " + exception.getMessage());
		}
		return reportExecuteResponseCloumnDefList;
	}

	private List<Map<String, Object>> populateRowData(List<FlexReportExecuteResponseData> flexReportList,
			Reports reportObject) {
		List<Map<String, Object>> rowDataList = new ArrayList<Map<String, Object>>();
		List<Metrics> reportMetricsList = reportObject.getMetricsList();
		List<String> metricsDisplayNameList = reportMetricsList.stream().map(Metrics::getDisplayName)
				.collect(Collectors.toList());
		Map<String, Object> rowMap = new HashMap<String, Object>();
		flexReportList.stream().forEach(flexReport -> {
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

	private List<FlexReportExecuteResponseData> populateDynamicQuery(ComponentDetails componentDetails,
			List<ReportPromptsInstanceDTO> promptsList) {
		String queryString = componentDetails.getQuery().replaceAll("~", "");
		StringBuilder queryBuilder = new StringBuilder();
		List<String> promptsValueList = new ArrayList<String>();
		List<FlexReportExecuteResponseData> flexReportDefaultOutputList = new ArrayList<FlexReportExecuteResponseData>();
		promptsList.forEach(prompts -> {
			// check whether the prompt key present in the query and not null
			if (null != prompts.getKey() && queryString.indexOf(prompts.getKey()) > 0) {
				if (null != prompts.getPromptsValueList()) {
					promptsValueList.addAll(prompts.getPromptsValueList());
				}
				if (null != prompts.getPromptValue()) {
					promptsValueList.add(prompts.getPromptValue());
				}
				String promptsValue = String.join(",", promptsValueList);
				queryBuilder.append(queryString.replace(prompts.getKey(), promptsValue));
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
					FlexReportExecuteResponseData flexReportOutput = new FlexReportExecuteResponseData();
					List<Object> rowData = new ArrayList<Object>();
					for (int index = 1; index < columnCount; index++) {
						Object colValue = resultSet.getObject(index);
						rowData.add(colValue);
					}
					flexReportOutput.setRowData(rowData);
					flexReportDefaultOutputList.add(flexReportOutput);
				}
			}
			connection.close();
			return flexReportDefaultOutputList;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flexReportDefaultOutputList;
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
			List<PromptsProcessingRequest> promptsExcecutionRequest = reportProcessingRequest.getPrompts();
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
			reportPromptsIntanceDTO.setKey(promptKey);
			reportPromptsIntanceDTO.setPromptsValueList(promptValue.getPromptsValueList());
			reportPromptsIntanceDTO.setPromptValue(promptValue.getPromptValue());
			reportPromptsInstanceList.add(reportPromptsIntanceDTO);
		});
		return reportPromptsInstanceList;
	}

	@Override
	public APIResponse populateSuccessAPIRespone(FlexReportExecuteResponseData flexList) {
		APIResponse reportExecutionApiResponse = new APIResponse();
		reportExecutionApiResponse.setData(flexList);
		reportExecutionApiResponse.setMessage("Report Execution Success");
		reportExecutionApiResponse.setStatus(Boolean.TRUE);
		return reportExecutionApiResponse;
	}
};