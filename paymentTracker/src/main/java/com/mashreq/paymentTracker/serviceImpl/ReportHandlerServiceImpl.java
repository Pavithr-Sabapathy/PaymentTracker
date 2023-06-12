package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.EntityDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecutionDTO;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.model.DataEntity;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.model.ReportExecution;
import com.mashreq.paymentTracker.model.ReportInstance;
import com.mashreq.paymentTracker.model.ReportInstancePrompt;
import com.mashreq.paymentTracker.model.Roles;
import com.mashreq.paymentTracker.model.Users;
import com.mashreq.paymentTracker.repository.ReportExecutionRepoistory;
import com.mashreq.paymentTracker.repository.ReportInstanceRepository;
import com.mashreq.paymentTracker.service.FlexFederatedReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportHandlerService;
import com.mashreq.paymentTracker.type.EntityType;
import com.mashreq.paymentTracker.type.ExecutionStatusType;

@Component
public class ReportHandlerServiceImpl implements ReportHandlerService {

	private static final Logger log = LoggerFactory.getLogger(ReportHandlerServiceImpl.class);

	@Autowired
	ReportConfigurationService reportConfigurationService;

	@Autowired
	FlexFederatedReportService flexFederatedReportService;

	@Autowired
	ReportInstanceRepository reportInstanceRepo;

	@Autowired
	ReportExecutionRepoistory reportExecutionRepo;

	@Override
	public ReportExecuteResponseData executeReport(String reportName, ReportExecutionRequest reportExecutionRequest)
			throws ReportException {
		ReportExecuteResponseData reportExecuteResponseData = new ReportExecuteResponseData();
		ReportInstanceDTO reportInstanceDTO = populateReportInstance(reportExecutionRequest, reportName);
		ReportInstance reportInstance = createReportInstance(reportInstanceDTO);
		reportInstanceRepo.save(reportInstance);
		if (null != reportInstanceDTO) {
			ReportContext reportContext = populateReportContext(reportInstanceDTO);
			if (null != reportExecutionRequest.getLinkReference()) {
				reportContext.setLinkReference(reportExecutionRequest.getLinkReference());
			}
			if (null != reportExecutionRequest.getLinkReference()) {
				reportContext.setLinkReference(reportExecutionRequest.getLinkReference());
			}
			reportContext.setLinkedReport(reportExecutionRequest.getLinkExecution());
			ReportExecutionDTO reportExecutionDTO = populateReportExecution(reportContext);
			ReportExecution reportExecution = createReportExecution(reportExecutionDTO);
			reportContext.setExecutionId(reportExecution.getId());
			reportExecuteResponseData = flexFederatedReportService.processFlexReport(reportName, reportContext, reportExecutionRequest);
		}
		return reportExecuteResponseData;
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
		if (null != reportExecution.getStartDate())
			reportExecution.setStartDate(reportExecutionDTO.getStartDate());
		if (null != reportExecution.getEndDate())
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
		execution.setReportInstanceId(reportContext.getReportInstance().getReportId());
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
		List<Prompts> reportPromptsList = report.getPromptList();
		List<PromptInstance> PromptInstanceList = populatePromptInstanceByPrompts(reportPromptsList);

		List<ReportPromptsInstanceDTO> instancePromptList = new ArrayList<ReportPromptsInstanceDTO>();

		if (null != PromptInstanceList) {
			List<PromptsProcessingRequest> promptsExecutionRequest = reportExecutionRequest.getPrompts();
			promptsExecutionRequest.stream()
					.forEach(promptsRequest -> PromptInstanceList.stream().forEach(promptsInstanceDTO -> {
						if (promptsInstanceDTO.getKey().equalsIgnoreCase(promptsRequest.getKey())) {
							PromptInstance promptsInstance = new PromptInstance();
							ReportPromptsInstanceDTO instancePrompt = new ReportPromptsInstanceDTO();
							promptsInstance.setId(promptsInstanceDTO.getId());
							promptsInstance.setKey(promptsInstanceDTO.getKey());
							promptsInstance.setName(promptsInstanceDTO.getName());
							promptsInstance.setOrder(promptsInstanceDTO.getOrder());
							promptsInstance.setRequired(promptsInstanceDTO.getRequired());
							promptsInstance.setEntityId(promptsInstanceDTO.getEntityId());
							promptsInstance.setPromptValue(promptsRequest.getPromptValue());
							promptsInstance.setValue(promptsRequest.getValue());
							instancePrompt.setPrompt(promptsInstance);
							instancePrompt.setReportId(report.getId());
							instancePromptList.add(instancePrompt);
						}
					}));
		} else {
			log.error("error while retrieving prompt for report : " + report.getReportName());
			throw new ReportException(ApplicationConstants.REPORT_INSTANCE_CREATION_FAILED_MSG + report.getReportName());
		}
		return instancePromptList;

	}

	private List<PromptInstance> populatePromptInstanceByPrompts(List<Prompts> promptsList) {
		List<PromptInstance> PromptInstanceList = new ArrayList<PromptInstance>();
		if (promptsList != null && promptsList.size() > 0)
			promptsList.forEach(prompt -> {
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