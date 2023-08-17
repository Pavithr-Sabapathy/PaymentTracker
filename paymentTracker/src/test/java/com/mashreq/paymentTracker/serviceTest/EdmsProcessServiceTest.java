package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportDefaultInput;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.serviceImpl.CannedReportServiceImpl;
import com.mashreq.paymentTracker.serviceImpl.EdmsProcessServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;
import com.mashreq.paymentTracker.utility.CheckType;

@SpringBootTest
@AutoConfigureMockMvc
public class EdmsProcessServiceTest {

	@MockBean
	private QueryExecutorService queryExecutorService;

	@MockBean
	ReportConfigurationService reportConfigurationService;

	@MockBean
	private ComponentsDAO componentsDAO;

	@MockBean
	ReportOutputExecutor reportOutputExecutor;

	@Autowired
	EdmsProcessServiceImpl edmsProcessServiceImpl;

	@MockBean
	CannedReportServiceImpl cannedReportService;
	
	@MockBean
	private LinkReportService linkReportService;

	@Test
	void testPopulateBaseInputContext() {
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

		List<ReportPromptsInstanceDTO> mockPromptsList = new ArrayList<ReportPromptsInstanceDTO>();
		ReportPromptsInstanceDTO mockReportPromptsInstanceDTO = new ReportPromptsInstanceDTO();
		ReportPromptsInstanceDTO mockReportPromptsInstanceDTO1 = new ReportPromptsInstanceDTO();

		PromptInstance mockPromptInstance = new PromptInstance();
		PromptInstance mockPromptInstance1 = new PromptInstance();
		List<String> valueList = new ArrayList<String>();
		valueList.add("12324324");

		mockPromptInstance.setKey("ReferenceNum");
		mockPromptInstance.setValue(valueList);
		mockPromptInstance.setPromptValue("12324324");

		mockPromptInstance1.setKey("mesgType");
		mockPromptInstance1.setValue(valueList);
		mockPromptInstance1.setPromptValue("12324324");

		mockReportPromptsInstanceDTO.setId(1L);
		mockReportPromptsInstanceDTO.setReportId(2L);
		mockReportPromptsInstanceDTO.setReportInstanceId(reportInstance.getId());
		mockReportPromptsInstanceDTO.setPrompt(mockPromptInstance);

		mockReportPromptsInstanceDTO1.setReportInstanceId(reportInstance.getId());
		mockReportPromptsInstanceDTO1.setPrompt(mockPromptInstance1);

		mockPromptsList.add(mockReportPromptsInstanceDTO);
		mockPromptsList.add(mockReportPromptsInstanceDTO1);
		reportInstance.setPromptsList(mockPromptsList);

		ReportInput response = edmsProcessServiceImpl.populateBaseInputContext(reportContext);
		assertNotNull(response);
	}
	
	@Test
    public void testProcessReport() throws Exception {
        ReportInput reportInput = new FederatedReportDefaultInput();
        ReportContext reportContext = new ReportContext();
        ReportInstanceDTO reportInstanceDTO = new ReportInstanceDTO();
        reportInstanceDTO.setReportName("TestReport");
        reportContext.setReportInstance(reportInstanceDTO);

        Report report = new Report();
        report.setId(1L);

        CannedReport cannedReport = new CannedReport();
        cannedReport.setId(1L);

        Components mockComponents = new Components();
		mockComponents.setId(1L);
		mockComponents.setActive("Y");
		mockComponents.setComponentKey("uaefts");
		mockComponents.setComponentName("AdvanceSearch");
		mockComponents.setId(1L);
		mockComponents.setReport(report);;

        List<Components> componentList = new ArrayList<>();
        componentList.add(mockComponents);

        List<ReportDefaultOutput> outputList = new ArrayList<>();

        when(reportConfigurationService.fetchReportByName("TestReport")).thenReturn(report);
        when(cannedReportService.populateCannedReportInstance(report)).thenReturn(cannedReport);
        when(componentsDAO.findAllByreportId(1L)).thenReturn(componentList);
        when(reportOutputExecutor.populateRowData(outputList, report)).thenReturn(new ArrayList<>());
        when(reportOutputExecutor.populateColumnDef(report)).thenReturn(new ArrayList<>());

        ReportExecuteResponseData responseData = edmsProcessServiceImpl.processReport(reportInput, reportContext);

        assertNotNull(responseData);

        verify(reportConfigurationService, times(1)).fetchReportByName("TestReport");
        verify(cannedReportService, times(1)).populateCannedReportInstance(report);
        verify(componentsDAO, times(1)).findAllByreportId(1L);
        verify(reportOutputExecutor, times(1)).populateRowData(outputList, report);
        verify(reportOutputExecutor, times(1)).populateColumnDef(report);
    }
}