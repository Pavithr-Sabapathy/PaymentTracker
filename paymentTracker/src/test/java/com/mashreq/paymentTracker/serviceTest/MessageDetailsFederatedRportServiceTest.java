package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyLong;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.SwiftDetailedReportService;
import com.mashreq.paymentTracker.serviceImpl.MessageDetailsFederatedReportServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;
import com.mashreq.paymentTracker.dto.MessageDetailsFederatedReportInput;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.ReportComponentDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.utility.CheckType;
import java.util.HashSet;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class MessageDetailsFederatedRportServiceTest {

	@MockBean
	SwiftDetailedReportService swiftDetailedReportService;
	
	
	
	@MockBean
	ReportConfigurationService reportConfigurationService;
	
	@MockBean
	CannedReportService cannedReportService;
	
	 @MockBean
	 private ComponentsDAO componentsRepository;
	
	@MockBean
	LinkReportService linkReportService;
	
	@MockBean
	QueryExecutorService queryExecutorService;
	
	@Autowired
	MessageDetailsFederatedReportServiceImpl messageDetailsFederatedReportServiceImpl;
	
	@Test
	void populateBaseInputContext() {
		
		MessageDetailsFederatedReportInput messageDetailsFederatedReportInput=new MessageDetailsFederatedReportInput();
		FederatedReportPromptDTO federatedReportPromptDTO=new FederatedReportPromptDTO();
		federatedReportPromptDTO.setPromptKey("MessageSubFormatPrompt");
		federatedReportPromptDTO.setPromptKey("MessageThroughPrompt");
		federatedReportPromptDTO.setPromptKey("MessageTypePrompt");
		federatedReportPromptDTO.setPromptKey("ReferenceNumPrompt");
		List<String> value = new ArrayList<String>();
		value.add("sample");
		federatedReportPromptDTO.setValueList(value);
		messageDetailsFederatedReportInput.setMessageSubFormatPrompt(federatedReportPromptDTO);
		messageDetailsFederatedReportInput.setMessageThroughPrompt(federatedReportPromptDTO);
		messageDetailsFederatedReportInput.setMessageTypePrompt(federatedReportPromptDTO);
		messageDetailsFederatedReportInput.setReferenceNumPrompt(federatedReportPromptDTO);
		
		List<ReportPromptsInstanceDTO>  promptsList = new ArrayList<ReportPromptsInstanceDTO>();
		ReportPromptsInstanceDTO reportPrompt = new ReportPromptsInstanceDTO();
		PromptInstance promptsInstance = new PromptInstance();
		promptsInstance.setKey("MessageSubFormatPrompt");
		promptsInstance.setKey("MessageThroughPrompt");
		promptsInstance.setKey("MessageTypePrompt");
		promptsInstance.setKey("ReferenceNumPrompt");
		List<String> valueList = new ArrayList<String>();
		valueList.add("");
		valueList.add("");
		valueList.add("");
		valueList.add("019010125320");
		
		promptsInstance.setValue(valueList);
		reportPrompt.setPrompt(promptsInstance);
		promptsList.add(reportPrompt);
		

		ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
		reportInstanceDTO
				.setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
		reportInstanceDTO.setId(1L);
		reportInstanceDTO.setModuleId(1L);
		reportInstanceDTO.setPromptsList(promptsList);
		reportInstanceDTO.setReportId(1L);

		ReportContext reportContext = new ReportContext();
		reportContext.setCountry(CountryType.UAE);
		reportContext.setExecutionId(1L);
		reportContext.setLinkInstanceId(1L);
		reportContext.setLinkReference("Link Reference");
		reportContext.setLinkedReport(true);
		reportContext.setModuleId(1L);
		reportContext.setReportId(1L);
		reportContext.setReportInstance(reportInstanceDTO);
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

		ReportInput actualProcessFlexReportResult = messageDetailsFederatedReportServiceImpl.populateBaseInputContext(reportContext);

		assertNotNull(actualProcessFlexReportResult);
				
	}
	
	@Test
	void processMessageDetailReport() {
		
        CannedReport cannedReport = new CannedReport();
        cannedReport.setActive(CheckType.NO);
        cannedReport.setAppId(1L);
        cannedReport.setCannedReportComponents(new HashSet<>());
        cannedReport.setCannedReportMetrics(new HashSet<>());
        cannedReport.setCannedReportPrompts(new HashSet<>());
        cannedReport.setDeleted(CheckType.NO);
        cannedReport.setDisplayName("Display Name");
        cannedReport.setId(1L);
        cannedReport.setName("Name");
        cannedReport.setValid(CheckType.NO);
        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);
        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(null);
        when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(new Report());

        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
        reportInstanceDTO
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        ReportExecuteResponseData actualProcessSwiftDetailReportResult = messageDetailsFederatedReportServiceImpl
                .processReport(null, reportContext);
        assertNull(actualProcessSwiftDetailReportResult.getColumnDefs());
        assertNull(actualProcessSwiftDetailReportResult.getData());
        verify(cannedReportService).populateCannedReportInstance(Mockito.<Report>any());
        verify(componentsRepository).findAllByreportId(anyLong());
        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
	}
	
	@Test
	void processMessageDetailReport2() {
		CannedReport cannedReport = new CannedReport();
        cannedReport.setActive(CheckType.NO);
        cannedReport.setAppId(1L);
        cannedReport.setCannedReportComponents(new HashSet<>());
        cannedReport.setCannedReportMetrics(new HashSet<>());
        cannedReport.setCannedReportPrompts(new HashSet<>());
        cannedReport.setDeleted(CheckType.NO);
        cannedReport.setDisplayName("Display Name");
        cannedReport.setId(1L);
        cannedReport.setName("Name");
        cannedReport.setValid(CheckType.NO);
        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);
        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(null);
        when(reportConfigurationService.fetchReportByName(Mockito.<String>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
        reportInstanceDTO
                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
        assertThrows(ResourceNotFoundException.class,
                () -> messageDetailsFederatedReportServiceImpl.processReport(null, reportContext));
        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
	}
	@Test
	void processMessageDetailReport3() {
		 CannedReport cannedReport = new CannedReport();
	        cannedReport.setActive(CheckType.NO);
	        cannedReport.setAppId(1L);
	        cannedReport.setCannedReportComponents(new HashSet<>());
	        cannedReport.setCannedReportMetrics(new HashSet<>());
	        cannedReport.setCannedReportPrompts(new HashSet<>());
	        cannedReport.setDeleted(CheckType.NO);
	        cannedReport.setDisplayName("Display Name");
	        cannedReport.setId(1L);
	        cannedReport.setName("Name");
	        cannedReport.setValid(CheckType.NO);
	        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);

	        ArrayList<Components> componentsList = new ArrayList<>();
	        componentsList.add(new Components());
	        List<Components> ofResult = new ArrayList<Components>();
	        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(ofResult);
	        when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(new Report());

	        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
	        reportInstanceDTO
	                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
	        ReportExecuteResponseData actualProcessSwiftDetailReportResult = messageDetailsFederatedReportServiceImpl
	                .processReport(null, reportContext);
	        assertNull(actualProcessSwiftDetailReportResult.getColumnDefs());
	        assertNull(actualProcessSwiftDetailReportResult.getData());
	        verify(cannedReportService).populateCannedReportInstance(Mockito.<Report>any());
	        verify(componentsRepository).findAllByreportId(anyLong());
	        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
		
	}
	
	@Test
	void processMessageDetailReport4() {
		 CannedReport cannedReport = new CannedReport();
	        cannedReport.setActive(CheckType.NO);
	        cannedReport.setAppId(1L);
	        cannedReport.setCannedReportComponents(new HashSet<>());
	        cannedReport.setCannedReportMetrics(new HashSet<>());
	        cannedReport.setCannedReportPrompts(new HashSet<>());
	        cannedReport.setDeleted(CheckType.NO);
	        cannedReport.setDisplayName("Display Name");
	        cannedReport.setId(1L);
	        cannedReport.setName("Name");
	        cannedReport.setValid(CheckType.NO);
	        when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);
	        when(componentsRepository.findAllByreportId(anyLong())).thenReturn(null);
	        when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(new Report());

	        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
	        reportInstanceDTO
	                .setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
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
	        assertThrows(ResourceNotFoundException.class,
	                () -> messageDetailsFederatedReportServiceImpl.processReport(null, reportContext));
	        verify(cannedReportService).populateCannedReportInstance(Mockito.<Report>any());
	        verify(componentsRepository).findAllByreportId(anyLong());
	        verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
	}
	
	
	

}
