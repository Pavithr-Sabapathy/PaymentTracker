package com.mashreq.paymentTracker.serviceImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.CannedReportComponent;
import com.mashreq.paymentTracker.dto.CannedReportComponentDetail;
import com.mashreq.paymentTracker.dto.CannedReportInstanceComponent;
import com.mashreq.paymentTracker.dto.CannedReportMetric;
import com.mashreq.paymentTracker.dto.CannedReportPrompt;
import com.mashreq.paymentTracker.dto.FederatedReportOutput;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.FlexAccountingDetailedFederatedReportInput;
import com.mashreq.paymentTracker.dto.FlexReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.exception.DataAccessException;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.ComponentsCountry;
import com.mashreq.paymentTracker.model.DataSourceConfig;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsCountryRepository;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.service.FlexFederatedReportService;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.SourceConnectionUtil;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class FlexFederatedReportServiceImpl implements FlexFederatedReportService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	private ComponentsRepository componentRepository;

	@Autowired
	LinkReportService linkReportService;
	
	@Autowired
	ComponentsCountryRepository componentsCountryRepository;

	private static final Logger log = LoggerFactory.getLogger(FlexFederatedReportServiceImpl.class);
	private static final String FILENAME = "FlexFederatedReportServiceImpl";

	public ReportExecuteResponseData processFlexReport(String reportName, ReportContext reportContext,
			ReportExecutionRequest reportExecutionRequest) {
		FlexAccountingDetailedFederatedReportInput flexAccountingDetailedFederatedReportInput = new FlexAccountingDetailedFederatedReportInput();
		List<FlexReportExecuteResponseData> flexReportExecuteResponseList = new ArrayList<FlexReportExecuteResponseData>();
		ReportExecuteResponseData flexReportExecuteResponseData = new ReportExecuteResponseData();
		Report report = reportConfigurationService.fetchReportByName(reportName);
		CannedReport cannedReport = populateCannedReportInstance(report);
		Optional<List<Components>> componentsOptional = componentRepository.findAllByreportId(cannedReport.getId());
		if (componentsOptional.isEmpty()) {
			throw new ResourceNotFoundException(ApplicationConstants.REPORT_DOES_NOT_EXISTS + cannedReport.getId());
		} else {
			List<Components> componentList = componentsOptional.get();
			Set<CannedReportComponent> cannedReportComponentSet = populateCannedReportComponent(componentList);
			cannedReport.setCannedReportComponents(cannedReportComponentSet);
			if (!componentList.isEmpty()) {
				for (Components component : componentList) {
					if (component.getActive().equalsIgnoreCase("Y")) {
						CannedReportInstanceComponent componentReportInstance = modelMapper.map(component,
								CannedReportInstanceComponent.class);
						FlexAccountingDetailedFederatedReportInput FlexAccountingDetailedFederatedReportInput = new FlexAccountingDetailedFederatedReportInput();
						FlexAccountingDetailedFederatedReportInput.setComponent(componentReportInstance);
						flexAccountingDetailedFederatedReportInput = populateBaseInputContext(
								cannedReport.getCannedReportPrompts(), reportExecutionRequest.getPrompts());
						List<FlexReportExecuteResponseData> flexReportExecuteResponse = executeReport(
								flexAccountingDetailedFederatedReportInput, component);
						if (!flexReportExecuteResponse.isEmpty()) {
							flexReportExecuteResponseList.addAll(flexReportExecuteResponse);
						}
					}
				}
				List<Map<String, Object>> rowDataMapList = populateRowData(flexReportExecuteResponseList, report);
				List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = populateColumnDef(
						flexReportExecuteResponseList, report);
				flexReportExecuteResponseData.setColumnDefs(reportExecuteResponseCloumnDefList);
				flexReportExecuteResponseData.setData(rowDataMapList);
			}

		}
		return flexReportExecuteResponseData;
	}

	private List<Map<String, Object>> populateRowData(List<FlexReportExecuteResponseData> flexReportList,
			Report reportObject) {
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

	private List<ReportExecuteResponseColumnDefDTO> populateColumnDef(
			List<FlexReportExecuteResponseData> flexReportList, Report reportObject) {
		List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = new ArrayList<ReportExecuteResponseColumnDefDTO>();
		try {
			List<Metrics> metricsList = reportObject.getMetricsList();
			metricsList.stream().forEach(metrics -> {
				ReportExecuteResponseColumnDefDTO reportExecuteResponseCloumnDef = new ReportExecuteResponseColumnDefDTO();
				reportExecuteResponseCloumnDef.setField(metrics.getDisplayName());
				reportExecuteResponseCloumnDefList.add(reportExecuteResponseCloumnDef);
			});
			List<String> metricsWithLinkList = prepareLinkReportInfo(reportObject);
			reportExecuteResponseCloumnDefList.stream().forEach(colummnDef ->{
				if(metricsWithLinkList.contains(colummnDef.getField())) {
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
			metricsWithLinks.add(linkedreportResponseDTO.getSourceMetrics().getDisplayName());
		});
		return metricsWithLinks;
	}

	private List<FlexReportExecuteResponseData> executeReport(
			FlexAccountingDetailedFederatedReportInput flexAccountingDetailedFederatedReportInput,
			Components component) {
		List<FlexReportExecuteResponseData> flexReportExecuteResponse = new ArrayList<FlexReportExecuteResponseData>();
		ComponentDetails matchedComponentDetail = null;
		if (null != component) {
			List<ComponentDetails> componentDetails = component.getComponentDetailsList();
			for (ComponentDetails componentDetail : componentDetails) {
				if (componentDetail.getQueryKey().toLowerCase().contains(flexAccountingDetailedFederatedReportInput
						.getAccountingSourcePrompt().getPromptValue().toLowerCase())) {
					matchedComponentDetail = componentDetail;
					break;
				}
			}
			if (matchedComponentDetail != null) {
				List<FederatedReportPromptDTO> prompts = new ArrayList<FederatedReportPromptDTO>();
				prompts.add(flexAccountingDetailedFederatedReportInput.getReferenceNumPrompt());
				prompts.add(flexAccountingDetailedFederatedReportInput.getDebitAccountPrompt());
				flexReportExecuteResponse = processComponentDetail(matchedComponentDetail, prompts);

			}
		}
		return flexReportExecuteResponse;
	}

	private List<FlexReportExecuteResponseData> processComponentDetail(ComponentDetails componentDetails,
			List<FederatedReportPromptDTO> promptDTO) {
		
		Long reportComponentId = componentDetails.getComponents().getId();
		List<ComponentsCountry> componentsCountrieList = processComponentCountry(reportComponentId); 
		DataSourceConfig dataSource = componentsCountrieList.get(0).getDataSourceConfig();
		List<FlexReportExecuteResponseData> flexReportDefaultOutputList = new ArrayList<FlexReportExecuteResponseData>();
		String queryString = componentDetails.getQuery();
		List<FederatedReportOutput> federatedReportOutputList = new ArrayList<FederatedReportOutput>();
		for (FederatedReportPromptDTO prompts : promptDTO) {
			if (null != prompts.getPromptKey() && queryString.indexOf(prompts.getPromptKey()) > 0) {
				if (null != prompts.getPromptValue()) {
					queryString = queryString.replace(MashreqFederatedReportConstants.TILDE + prompts.getPromptKey() + MashreqFederatedReportConstants.TILDE, prompts.getPromptValue());
				}
			}
		}

		try {
			Class.forName(MashreqFederatedReportConstants.DRIVER_CLASS_NAME);
			Connection connection;
			connection = SourceConnectionUtil.getConnection(dataSource.getDataSourceName()); 
//			connection = DriverManager.getConnection(MashreqFederatedReportConstants.FLEX_DATABASE_URL,
//					MashreqFederatedReportConstants.DATABASE_USERNAME, MashreqFederatedReportConstants.DATABASE_PASSWORD);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryString);

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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (DataAccessException e) {
			e.printStackTrace();
		} 
		return flexReportDefaultOutputList;
	}

	private List<ComponentsCountry> processComponentCountry(Long reportComponentId) {
		if (null != reportComponentId) {
			Optional<List<ComponentsCountry>> componentsCountryOptional = componentsCountryRepository
					.findAllBycomponentsId(reportComponentId);
			if (componentsCountryOptional.isEmpty()) {
				throw new ResourceNotFoundException(
						ApplicationConstants.COMPONENT_COUNTRY_DOES_NOT_EXISTS + reportComponentId);
			} else {
				List<ComponentsCountry> componentsCountryList = componentsCountryOptional.get();

				if (!componentsCountryList.isEmpty()) {
					List<ComponentsCountry> filterdcomponentsCountryList = new ArrayList<ComponentsCountry>();
					componentsCountryList.stream().forEach(componentsCountry -> {
						if (componentsCountry.getDataSourceConfig().getDataSourceSchemaName()
								.equalsIgnoreCase(MashreqFederatedReportConstants.DS_FLEX)) {
							filterdcomponentsCountryList.add(componentsCountry);
						}
					});
					return filterdcomponentsCountryList;
				}
			}
		}
		return null;
	}	

	private FlexAccountingDetailedFederatedReportInput populateBaseInputContext(
			Set<CannedReportPrompt> cannedReportPrompt, List<PromptsProcessingRequest> uiPromptsRequestList) {
		FlexAccountingDetailedFederatedReportInput flexAccountingDetailedFederatedReportInput = new FlexAccountingDetailedFederatedReportInput();
		List<String> promptKeyList = cannedReportPrompt.stream().map(CannedReportPrompt::getPromptKey)
				.collect(Collectors.toList());
		Map<String, PromptsProcessingRequest> promptsRequestMapping = uiPromptsRequestList.stream()
				.collect(Collectors.toMap(PromptsProcessingRequest::getKey, Function.identity()));
		promptsRequestMapping.forEach((promptKey, promptValue) -> {
			if (UtilityClass.ignoreCaseContains(promptKeyList, promptKey)) {

				FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
				federatedReportPromptDTO.setPromptKey(promptKey);
				federatedReportPromptDTO.setPromptValue(promptValue.getValue().get(0).trim());
				if (promptKey.equalsIgnoreCase(MashreqFederatedReportConstants.REFERENCENUMPROMPTS)) {
					flexAccountingDetailedFederatedReportInput.setReferenceNumPrompt(federatedReportPromptDTO);
				} else if (promptKey.equalsIgnoreCase(MashreqFederatedReportConstants.RELATEDACCOUNTPROMPTS)) {
					flexAccountingDetailedFederatedReportInput.setDebitAccountPrompt(federatedReportPromptDTO);
				} else if (promptKey.equalsIgnoreCase(MashreqFederatedReportConstants.ACCOUNTINGSOURCEPROMPTS)) {
					flexAccountingDetailedFederatedReportInput.setAccountingSourcePrompt(federatedReportPromptDTO);
				}
			}
		});
		return flexAccountingDetailedFederatedReportInput;
	}

	private Set<CannedReportComponent> populateCannedReportComponent(List<Components> componentList) {
		Set<CannedReportComponent> cannedReportComponentSet = new HashSet<CannedReportComponent>();
		if (!componentList.isEmpty()) {
			componentList.stream().forEach(component -> {
				CannedReportComponent cannedReportComponent = new CannedReportComponent();
				cannedReportComponent.setId(component.getId());
				cannedReportComponent.setComponentKey(component.getComponentKey());
				cannedReportComponent.setComponentName(component.getComponentName());
				cannedReportComponent.setActive(CheckType.getCheckType(component.getActive()));
				Set<CannedReportComponentDetail> cannedReportComponentDetailsSet = populateCannedReportComponentDetails(
						component);
				cannedReportComponent.setCannedReportComponentDetails(cannedReportComponentDetailsSet);
				cannedReportComponentSet.add(cannedReportComponent);
			});
		}
		return cannedReportComponentSet;
	}

	private Set<CannedReportComponentDetail> populateCannedReportComponentDetails(Components component) {
		Set<CannedReportComponentDetail> cannedReportComponentDetailsSet = new HashSet<CannedReportComponentDetail>();
		if (!component.getComponentDetailsList().isEmpty()) {
			List<ComponentDetails> componentDetailsList = component.getComponentDetailsList();
			componentDetailsList.stream().forEach(componentDetails -> {
				CannedReportComponentDetail cannedReportComponentDetail = new CannedReportComponentDetail();
				cannedReportComponentDetail.setId(componentDetails.getId());
				cannedReportComponentDetail.setQuery(componentDetails.getQuery());
				cannedReportComponentDetail.setQueryKey(componentDetails.getQueryKey());
				cannedReportComponentDetailsSet.add(cannedReportComponentDetail);
			});
		}
		return cannedReportComponentDetailsSet;
	}

	private CannedReport populateCannedReportInstance(Report report) {
		CannedReport cannedReport = new CannedReport();
		if (null != report) {
			cannedReport.setId(report.getId());
			cannedReport.setName(report.getReportName());
			cannedReport.setDisplayName(report.getDisplayName());
			cannedReport.setValid(CheckType.getCheckType(report.getValid()));
			Set<CannedReportPrompt> cannedReportPromptSet = populateReportPrompts(report);
			Set<CannedReportMetric> cannedReportMetricSet = populateReportMetric(report);
			cannedReport.setCannedReportPrompts(cannedReportPromptSet);
			cannedReport.setCannedReportMetrics(cannedReportMetricSet);
		}
		return cannedReport;
	}

	private Set<CannedReportMetric> populateReportMetric(Report report) {
		Set<CannedReportMetric> cannedReportMetricSet = new HashSet<CannedReportMetric>();
		List<Metrics> metricList = report.getMetricsList();
		metricList.forEach(metric -> {
			CannedReportMetric cannedReportMetric = new CannedReportMetric();
			cannedReportMetric.setId(metric.getId());
			cannedReportMetric.setLabel(metric.getDisplayName());
			cannedReportMetric.setOrder(metric.getMetricsOrder().intValue());
			cannedReportMetric.setDisplayable(CheckType.getCheckType(metric.getDisplay()));
			cannedReportMetric.setMetricKey(metric.getDisplayName());
			cannedReportMetricSet.add(cannedReportMetric);
		});
		return cannedReportMetricSet;
	}

	private Set<CannedReportPrompt> populateReportPrompts(Report report) {
		Set<CannedReportPrompt> cannedReportPromptSet = new HashSet<CannedReportPrompt>();
		List<Prompts> promptsList = report.getPromptList();
		promptsList.forEach(prompt -> {
			CannedReportPrompt cannedReportPrompts = new CannedReportPrompt();
			cannedReportPrompts.setId(prompt.getId());
			cannedReportPrompts.setOrder(prompt.getPromptOrder().intValue());
			cannedReportPrompts.setPromptKey(prompt.getPromptKey());
			cannedReportPrompts.setPromptKeyDisplay(prompt.getDisplayName());
			cannedReportPrompts.setRequired(CheckType.getCheckType(prompt.getPromptRequired()));
			cannedReportPromptSet.add(cannedReportPrompts);
		});
		return cannedReportPromptSet;
	}
}
