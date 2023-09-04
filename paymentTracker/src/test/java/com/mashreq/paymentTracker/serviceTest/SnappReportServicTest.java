package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.SnappDetailedReportInput;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.serviceImpl.SnappReportConnector;
import com.mashreq.paymentTracker.serviceImpl.SnappReportServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;

@SpringBootTest
@AutoConfigureMockMvc
public class SnappReportServicTest {

	@MockBean
	private QueryExecutorService queryExecutorService;

	@MockBean
	ReportConfigurationService reportConfigurationService;

	@MockBean
	private ComponentsDAO componentsDAO;

	@MockBean
	ReportOutputExecutor reportOutputExecutor;

	@Autowired
	SnappReportServiceImpl SnappReportServiceImpl;

	@MockBean
	private LinkReportService linkReportService;

	@MockBean
	SnappReportConnector snappReportConnector;
	@Test
	void populateBaseInputContext() {

		SnappDetailedReportInput snappDetailedReportInput = new SnappDetailedReportInput();
		FederatedReportPromptDTO referenceNumPrompt = new FederatedReportPromptDTO();
		referenceNumPrompt.setPromptKey("ReferenceNum");
		List<String> value = new ArrayList<String>();
		value.add("sample");
		referenceNumPrompt.setValueList(value);
		snappDetailedReportInput.setReferenceNumPrompt(referenceNumPrompt);

		List<ReportPromptsInstanceDTO> promptsList = new ArrayList<ReportPromptsInstanceDTO>();
		ReportPromptsInstanceDTO reportPrompt = new ReportPromptsInstanceDTO();
		PromptInstance promptsInstance = new PromptInstance();
		promptsInstance.setKey("ReferenceNum");
		List<String> valueList = new ArrayList<String>();
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

		ReportInput actualProcessFlexReportResult = SnappReportServiceImpl.populateBaseInputContext(reportContext);

		assertNotNull(actualProcessFlexReportResult);
	}

	@Test
    public void testProcessReport() throws Exception {
		SnappDetailedReportInput reportInput = new SnappDetailedReportInput();
		FederatedReportPromptDTO ReferenceNum = new FederatedReportPromptDTO();
		ReferenceNum.setPromptKey("ReferenceNum");
		List<String> valueList = new ArrayList<String>();
		valueList.add("019010125320");
		ReferenceNum.setValueList(valueList);
		reportInput.setReferenceNumPrompt(ReferenceNum);
		
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
		reportInstanceDTO.setReportName("TestReport");
		reportInstanceDTO.setRoleId(1L);
		reportInstanceDTO.setRoleName("TestReport");
		reportInstanceDTO.setUserId(1L);
		reportInstanceDTO.setUserName("janedoe");

		ReportContext reportContext = new ReportContext();
		reportContext.setCountry(CountryType.UAE);
		reportContext.setExecutionId(1L);
		reportContext.setLinkInstanceId(1L);
		reportContext.setLinkReference("Link Reference");
		reportContext.setLinkedReport(true);
		reportContext.setModuleId(1L);
		reportContext.setReportId(1L);
		reportContext.setReportInstance(reportInstanceDTO);
		reportContext.setReportName("TestReport");
		reportContext.setRoleId(1L);
		reportContext.setRoleName("TestReport");
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

        ReportOutput reportOutput = new ReportDefaultOutput();

        List<Components> componentList = new ArrayList<>();
        componentList.add(mockComponents);

        List<? extends ReportOutput> outputList = new ArrayList<>();
       // outputList.addAll( reportOutput);

        List<ReportDefaultOutput> snappReportOutputList = new ArrayList<>();
        snappReportOutputList.add((ReportDefaultOutput) reportOutput);

        when(reportConfigurationService.fetchReportByName("TestReport")).thenReturn(report);
        when(componentsDAO.findAllByreportId(1L)).thenReturn(componentList);
        when(snappReportConnector.processReportComponent(reportInput, reportContext)).thenReturn(new ArrayList<>());
        when(reportOutputExecutor.populateRowData(snappReportOutputList, report)).thenReturn(new ArrayList<>());
        when(reportOutputExecutor.populateColumnDef(report)).thenReturn(new ArrayList<>());

        ReportExecuteResponseData responseData = SnappReportServiceImpl.processReport(reportInput, reportContext);

        assertNotNull(responseData);
             
       /* verify(reportConfigurationService, times(1)).fetchReportByName("TestReport");
        verify(componentsDAO, times(1)).findAllByreportId(1L);
        verify(snappReportConnector, times(1)).processReportComponent(reportInput, reportContext);
        verify(reportOutputExecutor, times(1)).populateRowData(snappReportOutputList, report);
        verify(reportOutputExecutor, times(1)).populateColumnDef(report);*/
    }
}