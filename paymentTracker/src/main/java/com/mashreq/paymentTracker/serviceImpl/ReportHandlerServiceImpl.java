package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.EntityDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDataDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseMetaDTO;
import com.mashreq.paymentTracker.dto.ReportExecutionDTO;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.model.DataEntity;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.model.ReportData;
import com.mashreq.paymentTracker.model.ReportExecution;
import com.mashreq.paymentTracker.model.ReportInstance;
import com.mashreq.paymentTracker.model.ReportInstancePrompt;
import com.mashreq.paymentTracker.model.Roles;
import com.mashreq.paymentTracker.model.Users;
import com.mashreq.paymentTracker.repository.ReportDataRepository;
import com.mashreq.paymentTracker.repository.ReportExecutionRepoistory;
import com.mashreq.paymentTracker.repository.ReportInstanceRepository;
import com.mashreq.paymentTracker.service.EdmsProcessService;
import com.mashreq.paymentTracker.service.FlexFederatedReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportHandlerService;
import com.mashreq.paymentTracker.service.SwiftDetailedReportService;
import com.mashreq.paymentTracker.service.UAEFTSReportService;
import com.mashreq.paymentTracker.type.EntityType;
import com.mashreq.paymentTracker.type.ExecutionStatusType;
import com.mashreq.paymentTracker.utility.DateTimeUtil;

@Component
public class ReportHandlerServiceImpl implements ReportHandlerService {

	private static final Logger log = LoggerFactory.getLogger(ReportHandlerServiceImpl.class);

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	FlexFederatedReportService flexFederatedReportService;

	@Autowired
	SwiftDetailedReportService swiftDetailedReportService;

	@Autowired
	ReportInstanceRepository reportInstanceRepo;

	@Autowired
	ReportExecutionRepoistory reportExecutionRepo;

	@Autowired
	ReportDataRepository reportDataRepo;

	@Autowired
	EdmsProcessService edmsProcessService;

	@Autowired
	UAEFTSReportService UAEFTSReportService;
	
	@Override
	public ReportExecuteResponseData executeReport(String reportName, ReportExecutionRequest reportExecutionRequest)
			throws ReportException, JsonProcessingException {

		ReportExecuteResponseData reportExecuteResponseData = new ReportExecuteResponseData();
		ReportContext reportContext = new ReportContext();
		ReportDataDTO reportDataDTO = new ReportDataDTO();
		ReportExecuteResponseMetaDTO reportExecutionMetaDTO = new ReportExecuteResponseMetaDTO();
		/**
		 * populate report instance to store in reportInstance and reportPromptInstance
		 * table
		 **/
		Date startTime = new Date();
		ReportInstanceDTO reportInstanceDTO = populateReportInstance(reportExecutionRequest, reportName);
		ReportInstance reportInstance = createReportInstance(reportInstanceDTO);
		reportInstance = reportInstanceRepo.save(reportInstance);
		if (null != reportInstance.getId()) {
			reportInstanceDTO.setId(reportInstance.getId());
		}
		if (null != reportInstanceDTO) {
			/** populate report context to use **/
			reportContext = populateReportContext(reportInstanceDTO);
			if (null != reportExecutionRequest.getLinkReference()) {
				reportContext.setLinkReference(reportExecutionRequest.getLinkReference());
			}
			if (null != reportExecutionRequest.getLinkReference()) {
				reportContext.setLinkReference(reportExecutionRequest.getLinkReference());
			}
			reportContext.setLinkedReport(reportExecutionRequest.getLinkExecution());
			/*** populate report execution to store in report execution table **/
			ReportExecutionDTO reportExecutionDTO = populateReportExecution(reportContext);
			ReportExecution reportExecution = createReportExecution(reportExecutionDTO);
			reportContext.setExecutionId(reportExecution.getId());
			if (reportName.equals(MashreqFederatedReportConstants.FLEX_REPORT_NAME)) {
				reportExecuteResponseData = flexFederatedReportService.processFlexReport(reportInstanceDTO,
						reportContext);

			} else if (reportName.equals(MashreqFederatedReportConstants.SWIFT_REPORT_NAME)) {
				reportExecuteResponseData = swiftDetailedReportService.processSwiftDetailReport(reportInstanceDTO,
						reportContext);
			} else if (reportName.equals(MashreqFederatedReportConstants.EDMS_REPORT_NAME)) {
				reportExecuteResponseData = edmsProcessService.ProcessEdmsCommonReport(reportInstanceDTO,
						reportContext);
			} else if(reportName.equals(MashreqFederatedReportConstants.UAEFTS_REPORT_NAME)) {
				reportExecuteResponseData = UAEFTSReportService.processUAEFTSReport(reportInstanceDTO, reportContext);
			}
				
		}

		ObjectMapper mapper = new ObjectMapper();
		String jsonData = mapper.writeValueAsString(reportExecuteResponseData);
		reportDataDTO.setReportExecutionId(reportContext.getExecutionId());
		reportDataDTO.setReportData(jsonData);
		ReportData reportData = populateReportData(reportDataDTO);
		reportDataRepo.save(reportData);

		Date endTime = new Date();
		reportExecutionMetaDTO = populateReportExecutionResponseMeta(startTime, endTime, reportName);
		reportExecuteResponseData.setMeta(reportExecutionMetaDTO);
		updateReportExecutionTimeByExecutionId(reportExecutionMetaDTO.getExecutionTime(),
				reportContext.getExecutionId());
		return reportExecuteResponseData;
	}

	private void updateReportExecutionTimeByExecutionId(Long executionTime, Long executionId) {
		reportExecutionRepo.updateExecutionTimeByExecutionId(executionTime, executionId);
	}

	private ReportExecuteResponseMetaDTO populateReportExecutionResponseMeta(Date startTime, Date endTime,
			String reportName) {
		ReportExecuteResponseMetaDTO meta = new ReportExecuteResponseMetaDTO();
		meta.setStartTime(DateTimeUtil.getFormattedDate(startTime));
		meta.setEndTime(DateTimeUtil.getFormattedDate(endTime));
		meta.setExecutionTime(endTime.getTime() - startTime.getTime());
		meta.setReportId(reportName);
		return meta;
	}

	private ReportData populateReportData(ReportDataDTO reportDataDTO) {
		ReportData reportData = new ReportData();
		if (null != reportDataDTO.getReportExecutionId()) {
			ReportExecution reportExecution = new ReportExecution();
			reportExecution.setId(reportDataDTO.getReportExecutionId());
			reportData.setReportExecution(reportExecution);
		}
		if (null != reportDataDTO.getReportData()) {
			reportData.setReportData(reportDataDTO.getReportData());
		}
		return reportData;
	}

	private ReportExecution createReportExecution(ReportExecutionDTO reportExecutionDTO) {

		log.info("inserting report excution with values " + reportExecutionDTO.toString());
		ReportExecution reportExecution = new ReportExecution();
		if (null != reportExecutionDTO.getId())
			reportExecution.setId(reportExecutionDTO.getId());
		if (null != reportExecutionDTO.getReportId()) {
			Report report = new Report();
			report.setId(reportExecutionDTO.getReportId());
			reportExecution.setReport(report);
		}
		if (null != reportExecutionDTO.getReportInstanceId()) {
			ReportInstance reportInstance = new ReportInstance();
			reportInstance.setId(reportExecutionDTO.getReportInstanceId());
			reportExecution.setReportInstance(reportInstance);
		}
		if (null != reportExecutionDTO.getModuleId()) {
			ApplicationModule module = new ApplicationModule();
			module.setId(reportExecutionDTO.getModuleId());
			reportExecution.setModule(module);
		}
		if (null != reportExecutionDTO.getUserId()) {
			Users user = new Users();
			user.setId(reportExecutionDTO.getUserId());
			user.setFirstName(reportExecutionDTO.getUserName());
			user.setFullName(reportExecutionDTO.getUserName());
			reportExecution.setUser(user);
		}
		if (null != reportExecutionDTO.getRoleId()) {
			Roles roles = new Roles();
			roles.setId(reportExecutionDTO.getRoleId());
			roles.setRoleName(reportExecutionDTO.getRoleName());
			reportExecution.setRole(roles);
		}
		if (null != reportExecutionDTO.getStatus())
			reportExecution.setExecutionStatus(reportExecutionDTO.getStatus().getValue());

		if (null != reportExecutionDTO.getLinkExecution() && reportExecutionDTO.getLinkExecution() == true)
			reportExecution.setLinkExecution("T");
		else
			reportExecution.setLinkExecution("F");
		if (null != reportExecutionDTO.getStartDate())
			reportExecution.setStartDate(reportExecutionDTO.getStartDate());
		if (null != reportExecutionDTO.getEndDate())
			reportExecution.setEndDate(reportExecutionDTO.getEndDate());
		if (null != reportExecutionDTO.getFailureCase())
			reportExecution.setFailureCause(reportExecutionDTO.getFailureCase());
		if (null != reportExecutionDTO.getUserName())
			reportExecution.setUserName(reportExecutionDTO.getUserName());
		if (null != reportExecutionDTO.getRoleName())
			reportExecution.setRoleName(reportExecutionDTO.getRoleName());
		ReportExecution reportExecutionResponse = reportExecutionRepo.save(reportExecution);
		reportExecution.setId(reportExecutionResponse.getId());
		return reportExecution;

	}

	private ReportExecutionDTO populateReportExecution(ReportContext reportContext) {
		ReportExecutionDTO execution = new ReportExecutionDTO();
		execution.setReportId(reportContext.getReportId());
		execution.setLinkExecution(reportContext.getLinkedReport());
		execution.setModuleId(reportContext.getModuleId());
		execution.setReportInstanceId(reportContext.getReportInstance().getId());
		execution.setRoleId(reportContext.getRoleId());
		execution.setStartDate(new Date());
		execution.setStatus(ExecutionStatusType.INPROGRESS);
		execution.setUserId(reportContext.getUserId());
		execution.setUserName(reportContext.getUserName());
		execution.setRoleName(reportContext.getRoleName());
		execution.setEndDate(new Date());
		return execution;
	}

	private ReportContext populateReportContext(ReportInstanceDTO reportInstanceDTO) {
		ReportContext context = new ReportContext();
		context.setModuleId(reportInstanceDTO.getModuleId());
		context.setReportId(reportInstanceDTO.getReportId());
		context.setReportInstance(reportInstanceDTO);
		context.setReportName(reportInstanceDTO.getReportName());
		context.setRoleId(reportInstanceDTO.getRoleId());
		context.setUserId(reportInstanceDTO.getUserId());
		context.setUserName(reportInstanceDTO.getUserName());
		context.setRoleName(reportInstanceDTO.getRoleName());
		return context;
	}

	private ReportInstance createReportInstance(ReportInstanceDTO reportInstanceDTO) {
		ReportInstance reportInstance = new ReportInstance();
		if (null != reportInstanceDTO) {
			if (null != reportInstanceDTO.getReportId()) {
				Report report = new Report();
				report.setId(reportInstanceDTO.getReportId());
				reportInstance.setReport(report);
			}
			if (null != reportInstanceDTO.getRoleId()) {
				Roles role = new Roles();
				role.setId(reportInstanceDTO.getRoleId());
				reportInstance.setRole(role);
			}
			if (null != reportInstanceDTO.getModuleId()) {
				ApplicationModule module = new ApplicationModule();
				module.setId(reportInstanceDTO.getModuleId());
				reportInstance.setModule(module);
			}
			if (null != reportInstanceDTO.getUserId()) {
				Users user = new Users();
				user.setId(reportInstanceDTO.getUserId());
				reportInstance.setUser(user);
			}

			if (null != reportInstanceDTO.getReportName())
				reportInstance.setReportName(reportInstanceDTO.getReportName());
			if (null != reportInstanceDTO.getCreationDate())
				reportInstance.setCreateDate(reportInstanceDTO.getCreationDate());
			if (null != reportInstanceDTO.getPromptsList())
				reportInstance.setReportInstancePrompts(createReportInstancePrompts(reportInstanceDTO.getPromptsList(),
						reportInstanceDTO, reportInstance));
		}
		return reportInstance;
	}

	private List<ReportInstancePrompt> createReportInstancePrompts(List<ReportPromptsInstanceDTO> promptsList,
			ReportInstanceDTO reportInstanceDTO, ReportInstance reportInstance) {

		List<ReportInstancePrompt> reportInstancePromptSet = new ArrayList<>();
		if (!promptsList.isEmpty())
			promptsList.forEach(instancePrompt -> {
				log.info("Inserting Report Instance Prompt:" + instancePrompt.toString());
				ReportInstancePrompt reportInstancePrompt = new ReportInstancePrompt();
				reportInstancePrompt.setReportInstance(reportInstance);
				if (null != instancePrompt.getPrompt()) {
					PromptInstance promptInstance = instancePrompt.getPrompt();
					if (promptInstance != null) {
						Prompts prompt = new Prompts();
						prompt.setId(promptInstance.getId());
						reportInstancePrompt.setPrompt(prompt);

						if (null != instancePrompt.getReportId()) {
							Report report = new Report();
							report.setId(instancePrompt.getReportId());
							reportInstancePrompt.setReport(report);
						}

						if (null != promptInstance.getKey())
							reportInstancePrompt.setPromptKey(promptInstance.getKey());
						if (null != promptInstance.getPromptValue())
							reportInstancePrompt.setPromptValue(promptInstance.getPromptValue());
					}
					if (null != promptInstance.getEntityId()) {
						DataEntity dataEntity = new DataEntity();
						dataEntity.setId(promptInstance.getEntityId());
						reportInstancePrompt.setEntity(dataEntity);
					}
				}
				reportInstancePromptSet.add(reportInstancePrompt);
			});
		return reportInstancePromptSet;

	}

	private ReportInstanceDTO populateReportInstance(ReportExecutionRequest reportExecutionRequest, String reportName) {

		ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
		Report report = reportConfigurationService.fetchReportByName(reportName);
		try {
			if (null != report) {
				reportInstanceDTO.setReportId(report.getId());
				reportInstanceDTO.setReportName(report.getReportName());
				reportInstanceDTO.setRoleId(0L); // TODO fetch the role.
				reportInstanceDTO.setRoleName(reportExecutionRequest.getRole());
				reportInstanceDTO.setUserId(reportExecutionRequest.getUserId());
				reportInstanceDTO.setUserName(reportExecutionRequest.getUserName());
				reportInstanceDTO.setModuleId(report.getModuleId());
				reportInstanceDTO.setCreationDate(new Date());
				List<ReportPromptsInstanceDTO> reportPromptsInstanceDTOList = populateInstancePrompts(
						reportExecutionRequest, report);
				reportInstanceDTO.setPromptsList(reportPromptsInstanceDTOList);
			}
		} catch (ReportException e) {
			e.printStackTrace();
		}
		return reportInstanceDTO;
	}

	private List<ReportPromptsInstanceDTO> populateInstancePrompts(ReportExecutionRequest reportExecutionRequest,
			Report report) throws ReportException {
		List<ReportPromptsInstanceDTO> reportPromptsInstanceDTOList = new ArrayList<ReportPromptsInstanceDTO>();
		List<PromptsProcessingRequest> uiPrompts = reportExecutionRequest.getPrompts();
		List<Prompts> reportPromptsList = report.getPromptList();
		List<PromptInstance> promptInstanceList = populatePromptInstanceByPrompts(reportPromptsList, uiPrompts);
		if (!promptInstanceList.isEmpty()) {
			promptInstanceList.stream().forEach(promptsInstance -> {
				ReportPromptsInstanceDTO instancePrompt = new ReportPromptsInstanceDTO();
				promptsInstance.setId(promptsInstance.getId());
				promptsInstance.setKey(promptsInstance.getKey());
				promptsInstance.setName(promptsInstance.getName());
				promptsInstance.setOrder(promptsInstance.getOrder());
				promptsInstance.setRequired(promptsInstance.getRequired());
				promptsInstance.setEntityId(promptsInstance.getEntityId());
				promptsInstance.setPromptValue(promptsInstance.getPromptValue());
				promptsInstance.setValue(promptsInstance.getValue());
				instancePrompt.setPrompt(promptsInstance);
				instancePrompt.setReportId(report.getId());
				reportPromptsInstanceDTOList.add(instancePrompt);
			});
		}
		return reportPromptsInstanceDTOList;

	}

	private List<PromptInstance> populatePromptInstanceByPrompts(List<Prompts> promptsList,
			List<PromptsProcessingRequest> uiPromptsList) {
		List<PromptInstance> PromptInstanceList = new ArrayList<PromptInstance>();
		if (promptsList != null && promptsList.size() > 0)
			promptsList.forEach(prompt -> {
				Optional<PromptsProcessingRequest> uipromptsOptional = uiPromptsList.stream()
						.filter(uiPrompts -> uiPrompts.getKey().equalsIgnoreCase(prompt.getPromptKey())).findAny();
				PromptsProcessingRequest uiPrompt = uipromptsOptional.get();
				PromptInstance PromptInstanceObject = new PromptInstance();
				if (null != prompt.getId())
					PromptInstanceObject.setId(prompt.getId());
				if (null != prompt.getEntity() && null != prompt.getEntity().getId())
					PromptInstanceObject.setEntityId(prompt.getEntity().getId());
				if (null != prompt.getReport() && null != prompt.getReport().getId())
					PromptInstanceObject.setReportId(prompt.getReport().getId());
				if (null != prompt.getDisplayName())
					PromptInstanceObject.setName(prompt.getDisplayName());
				if (null != prompt.getPromptKey())
					PromptInstanceObject.setKey(prompt.getPromptKey());
				if (null != prompt.getPromptOrder())
					PromptInstanceObject.setOrder(prompt.getPromptOrder().toString());
				if (null != prompt.getPromptRequired())
					PromptInstanceObject.setRequired(
							(prompt.getPromptRequired().equalsIgnoreCase("y") ? Boolean.TRUE : Boolean.FALSE));
				if (null != prompt.getEntity()) {
					EntityDTO entityDTO = populateEntityDTOFromDataEntity(prompt.getEntity());
					PromptInstanceObject.setEntity(entityDTO);
					log.info("prompts values" + PromptInstanceObject.toString());
				}
				PromptInstanceObject.setPromptValue(uiPrompt.getPromptValue());
				PromptInstanceObject.setValue(uiPrompt.getValue());
				PromptInstanceList.add(PromptInstanceObject);
			});
		return PromptInstanceList;

	}

	private EntityDTO populateEntityDTOFromDataEntity(DataEntity entity) {
		EntityDTO entityVO = new EntityDTO();
		if (null != entity.getId())
			entityVO.setId(entity.getId());
		if (null != entity.getDisplayFormat())
			entityVO.setDisplayFormat(entity.getDisplayFormat());
		if (null != entity.getEntityName())
			entityVO.setEntityName(entity.getEntityName());
		if (null != entity.getEntityType())
			entityVO.setEntityType(EntityType.valueOf(entity.getEntityType()));
		if (null != entity.getSourceFormat())
			entityVO.setSourceFormat(entity.getSourceFormat());
		return entityVO;
	}

	@Override
	public APIResponse populateSuccessAPIRespone(ReportExecuteResponseData flexList) {
		APIResponse reportExecutionApiResponse = new APIResponse();
		reportExecutionApiResponse.setData(flexList);
		reportExecutionApiResponse.setMessage(ApplicationConstants.REPORT_EXECUTION_MSG);
		reportExecutionApiResponse.setStatus(Boolean.TRUE);
		return reportExecutionApiResponse;
	}

}