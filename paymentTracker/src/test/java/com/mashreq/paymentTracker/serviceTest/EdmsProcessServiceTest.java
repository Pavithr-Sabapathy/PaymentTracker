package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
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
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportDefaultInput;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.serviceImpl.EdmsProcessServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;

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
	void testProcessReport() {

		List<ReportOutput> mockOutputList = new ArrayList<ReportOutput>();
		ReportOutput reportOutput = new ReportOutput();
		reportOutput.setComponentDetailId(1L);
		reportOutput.setRowData(null);
		mockOutputList.add(reportOutput);

		FederatedReportDefaultInput mockFederatedReportDefaultInput = new FederatedReportDefaultInput();

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

		when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(report);
		when(componentsDAO.findAllByreportId(anyLong())).thenReturn(mockComponentsList);
		when(queryExecutorService.executeQuery(any(ReportComponentDetailDTO.class),
				any(FederatedReportComponentDetailContext.class))).thenReturn(mockOutputList);
		ReportExecuteResponseData actualProcessFlexReportResult = edmsProcessServiceImpl
				.processReport(mockFederatedReportDefaultInput, reportContext);
		assertNotNull(actualProcessFlexReportResult);
	}

	@Test
	void testProcessEdmsReport() {
		AdvanceSearchReportInput advanceSearchReportInput = mock(AdvanceSearchReportInput.class);
		ReportContext reportContext = new ReportContext();
		reportContext.setCountry(CountryType.UAE);
		reportContext.setExecutionId(1L);
		reportContext.setLinkInstanceId(1L);
		reportContext.setLinkReference("Link Reference");
		reportContext.setLinkedReport(true);
		reportContext.setModuleId(1L);
		reportContext.setReportId(1L);
		reportContext.setReportInstance(null);
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
		mockComponents.setComponentKey("edms");
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
		List<ReportOutput> reportOutputList = new ArrayList<ReportOutput>();
		ReportOutput mockReportOutput = new ReportOutput();
		mockReportOutput.setComponentDetailId(1L);
		reportOutputList.add(mockReportOutput);
		/*
		 * when(queryExecutorService.executeQuery(any(ReportComponentDetailDTO.class),
		 * any(FederatedReportComponentDetailContext.class))).thenReturn(
		 * reportOutputList );
		 */
		List<AdvanceSearchReportOutput> resultOutputList = edmsProcessServiceImpl
				.processEdmsReport(advanceSearchReportInput, mockComponentsList, reportContext);
	}
}