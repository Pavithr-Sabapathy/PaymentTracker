package com.mashreq.paymentTracker.serviceTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.FlexDetailedReportInput;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.serviceImpl.MessageDetailsFederatedReportServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;

@SpringBootTest
@AutoConfigureMockMvc
public class MessageDetailsFederatedRportServiceTest {
	@Autowired
	MessageDetailsFederatedReportServiceImpl MessageDetailsFederatedReportService;
	
	@MockBean
	ReportConfigurationService reportConfigurationService;
	
	@MockBean
	private ComponentsDAO componentsDAO;
	
	@MockBean
	private LinkReportService linkReportService;
	
	@Mock
	private ModelMapper modelMapper;

@Test
public void testPopulateBaseInputContext()
{
	
	List<ReportPromptsInstanceDTO> promptsList = new ArrayList<ReportPromptsInstanceDTO>();
	PromptInstance promptsInstance = new PromptInstance();
	promptsInstance.setKey("MessageThrough");
	List<String> valueList = new ArrayList<String>();
	valueList.add("s23324");
	promptsInstance.setValue(valueList);

	ReportPromptsInstanceDTO prompts = new ReportPromptsInstanceDTO();
	prompts.setPrompt(promptsInstance);
	promptsList.add(prompts);

	promptsInstance.setKey("ReferenceNum");
	List<String> valueList1 = new ArrayList<String>();
	valueList1.add("s23324");
	promptsInstance.setValue(valueList1);
	prompts.setPrompt(promptsInstance);
	promptsList.add(prompts);

	
	promptsInstance.setKey("MessageType");
	List<String> valueList4 = new ArrayList<String>();
	valueList4.add("s23324");
	promptsInstance.setValue(valueList4);
	prompts.setPrompt(promptsInstance);
	promptsList.add(prompts);

	promptsInstance.setKey("MessageSubFormat");
	List<String> valueList5 = new ArrayList<String>();
	valueList5.add("s23324");
	promptsInstance.setValue(valueList5);
	prompts.setPrompt(promptsInstance);
	promptsList.add(prompts);

	ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
	reportInstanceDTO
			.setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
	reportInstanceDTO.setId(1L);
	reportInstanceDTO.setModuleId(1L);
	reportInstanceDTO.setPromptsList(promptsList);
	reportInstanceDTO.setReportId(1L);
	reportInstanceDTO.setReportInstanceComponents(new HashSet<>());
	reportInstanceDTO.setReportInstanceMetrics(new HashSet<>());
	reportInstanceDTO.setReportInstancePrompts(new HashSet<>());
	reportInstanceDTO.setReportName("Report Name");
	reportInstanceDTO.setRoleId(1L);
	reportInstanceDTO.setRoleName("Role Name");
	reportInstanceDTO.setUserId(1L);
	reportInstanceDTO.setUserName("janedoe");

	ReportInstanceDTO reportInstance = new ReportInstanceDTO();
	reportInstance
			.setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
	reportInstance.setId(1L);
	reportInstance.setModuleId(1L);
	reportInstance.setPromptsList(new ArrayList<>());
	reportInstance.setReportId(1L);
	reportInstance.setReportInstanceComponents(new HashSet<>());
	reportInstance.setReportInstanceMetrics(new HashSet<>());
	reportInstance.setReportInstancePrompts(new HashSet<>());
	reportInstance.setReportName("Report Name");
	reportInstance.setRoleId(1L);
	reportInstance.setRoleName("Role Name");
	reportInstance.setUserId(1L);
	reportInstance.setUserName("janedoe");

	ReportContext reportContext = new ReportContext();
	reportContext.setCountry(CountryType.UAE);
	reportContext.setExecutionId(1L);
	reportContext.setLinkInstanceId(1L);
	reportContext.setLinkReference("Link Reference");
	reportContext.setLinkedReport(true);
	reportContext.setModuleId(1L);
	reportContext.setReportId(1L);
	reportContext.setReportInstance(reportInstance);
	reportContext.setReportName("Report Name");
	reportContext.setRoleId(1L);
	reportContext.setRoleName("Role Name");
	reportContext.setUserId(1L);
	reportContext.setUserName("janedoe");

	when(modelMapper.map(reportContext, ReportInstanceDTO.class)).thenReturn(reportInstance);
	
	ReportInput response = MessageDetailsFederatedReportService.populateBaseInputContext(reportContext);
	assertNotNull(response);
}


@Test
void testProcessMessageReport() {
	ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
	reportInstanceDTO.setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
	reportInstanceDTO.setId(1L);
	reportInstanceDTO.setModuleId(1L);
	reportInstanceDTO.setPromptsList(new ArrayList<>());
	reportInstanceDTO.setReportId(1L);
	reportInstanceDTO.setReportInstanceComponents(new HashSet<>());
	reportInstanceDTO.setReportInstanceMetrics(new HashSet<>());
	reportInstanceDTO.setReportInstancePrompts(new HashSet<>());
	reportInstanceDTO.setReportName("Report Name");
	reportInstanceDTO.setRoleId(1L);
	reportInstanceDTO.setRoleName("Role Name");
	reportInstanceDTO.setUserId(1L);
	reportInstanceDTO.setUserName("janedoe");

	ReportInstanceDTO reportInstance = new ReportInstanceDTO();
	reportInstance
			.setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
	reportInstance.setId(1L);
	reportInstance.setModuleId(1L);
	reportInstance.setPromptsList(new ArrayList<>());
	reportInstance.setReportId(1L);
	reportInstance.setReportInstanceComponents(new HashSet<>());
	reportInstance.setReportInstanceMetrics(new HashSet<>());
	reportInstance.setReportInstancePrompts(new HashSet<>());
	reportInstance.setReportName("Report Name");
	reportInstance.setRoleId(1L);
	reportInstance.setRoleName("Role Name");
	reportInstance.setUserId(1L);
	reportInstance.setUserName("janedoe");

	ReportContext reportContext = new ReportContext();
	reportContext.setCountry(CountryType.UAE);
	reportContext.setExecutionId(1L);
	reportContext.setLinkInstanceId(1L);
	reportContext.setLinkReference("Link Reference");
	reportContext.setLinkedReport(true);
	reportContext.setModuleId(1L);
	reportContext.setReportId(1L);
	reportContext.setReportInstance(reportInstance);
	reportContext.setReportName("Report Name");
	reportContext.setRoleId(1L);
	reportContext.setRoleName("Role Name");
	reportContext.setUserId(1L);
	reportContext.setUserName("janedoe");

	Report report = new Report();
	report.setId(1L);

	Components mockComponents = new Components();
	mockComponents.setId(1L);
	mockComponents.setActive("Y");
	mockComponents.setComponentKey("uaefts");
	mockComponents.setComponentName("AdvanceSearch");
	mockComponents.setId(1L);
	mockComponents.setReport(report);

	ComponentDetails mockComponentDetails = new ComponentDetails();
	mockComponentDetails.setComponents(mockComponents);
	mockComponentDetails.setId(1L);
	mockComponentDetails.setQuery("Select * from conf_rpt_Comp");
	mockComponentDetails.setQueryKey("uaefts-ccn");
	List<ComponentDetails> mockComponentDetailsList = new ArrayList<ComponentDetails>();
	mockComponentDetailsList.add(mockComponentDetails);

	mockComponents.setComponentDetailsList(mockComponentDetailsList);
	List<Components> mockComponentsList = new ArrayList<Components>();
	mockComponentsList.add(mockComponents);

	ArrayList<LinkedReportResponseDTO> linkedReportResponseDTOList = new ArrayList<>();
	linkedReportResponseDTOList.add(new LinkedReportResponseDTO(1L, "Link Name", "Link Description", "Report Name",
			0, 0, "Linked Report Name", "Source Metric Name", 0, "Active", null, null, 0, 0, null, 0));

	MessageDetailsFederatedReportInput mockMessageDetailedReportInput = new MessageDetailsFederatedReportInput();
	FederatedReportPromptDTO mockFederatedReportPromptDTO = new FederatedReportPromptDTO();
	mockFederatedReportPromptDTO.setPromptKey("SWIFT");
	mockFederatedReportPromptDTO.setPromptValue("SWIFT");
	mockMessageDetailedReportInput.setReferenceNumPrompt(mockFederatedReportPromptDTO);
	//mockFlexDetailedReportInput.setAccountingSourcePrompt(mockFederatedReportPromptDTO);

	when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(report);
	when(linkReportService.fetchLinkedReportByReportId(anyLong())).thenReturn(linkedReportResponseDTOList);
	when(componentsDAO.findAllByreportId(anyLong())).thenReturn(mockComponentsList);

	ReportExecuteResponseData actualProcessFlexReportResult = MessageDetailsFederatedReportService
			.processReport(mockMessageDetailedReportInput, reportContext);

	assertNotNull(actualProcessFlexReportResult);

	verify(componentsDAO).findAllByreportId(anyLong());
	verify(linkReportService).fetchLinkedReportByReportId(anyLong());
	verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
	
}




}
