package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.MOLDetailedFederatedReportInput;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.SnappDetailedReportInput;
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
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.utility.CheckType;

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

	@Autowired
	MOLReportConnector molReportConnector;

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
	public ReportExecuteResponseData processReport(ReportInput reportInput, ReportContext reportContext) {
		ReportExecuteResponseData responseData = new ReportExecuteResponseData();
		Report report = new Report();
		ReportInstanceDTO reportInstanceDTO = reportContext.getReportInstance();
		List<ReportDefaultOutput> snappReportOutputList = new ArrayList<ReportDefaultOutput>();
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

			List<? extends ReportOutput> outputList = molReportConnector.processReportComponent(reportInput,
					reportContext);

			if (!outputList.isEmpty()) {

				for (ReportOutput reportOutput : outputList) {
					ReportDefaultOutput output = (ReportDefaultOutput) reportOutput;
					snappReportOutputList.add(output);
				}
				List<Map<String, Object>> rowDataMapList = reportOutputExecutor.populateRowData(snappReportOutputList,
						report);
				List<ReportExecuteResponseColumnDefDTO> reportExecuteResponseCloumnDefList = reportOutputExecutor
						.populateColumnDef(report);
				responseData.setColumnDefs(reportExecuteResponseCloumnDefList);
				responseData.setData(rowDataMapList);
			}
			log.info(FILENAME + "[MOLFederatedReportServiceImpl processReport Response] -->" + responseData.toString());
		}
		return responseData;
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
		ReportPromptsInstanceDTO reportInstancePrompt = new ReportPromptsInstanceDTO();
		FederatedReportPromptDTO federatedReportPromptDTO = new FederatedReportPromptDTO();
		Optional<ReportPromptsInstanceDTO> promptsOptional = reportPromptsList.stream()
				.filter(prompts -> prompts.getPrompt().getKey().equalsIgnoreCase(promptKey)).findAny();
		if (promptsOptional.isPresent()) {
			reportInstancePrompt = promptsOptional.get();
		}
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
