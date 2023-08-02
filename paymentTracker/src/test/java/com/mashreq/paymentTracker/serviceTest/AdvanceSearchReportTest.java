package com.mashreq.paymentTracker.serviceTest;

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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportInput;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.CannedReport;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.PromptInstance;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.MatrixPaymentReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportInput;
import com.mashreq.paymentTracker.service.ReportOutputExecutor;
import com.mashreq.paymentTracker.service.UAEFTSReportService;
import com.mashreq.paymentTracker.serviceImpl.AdvanceSearchReportServiceImpl;
import com.mashreq.paymentTracker.serviceImpl.FlexDetailedReportServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;
import com.mashreq.paymentTracker.utility.CheckType;

@SpringBootTest
@AutoConfigureMockMvc
class AdvanceSearchReportTest {
	@MockBean
	ReportConfigurationService reportConfigurationService;

	@MockBean
	CannedReportService cannedReportService;

	@MockBean
	private ComponentsDAO componentsDAO;

	@MockBean
	MatrixPaymentReportService matrixPaymentReportService;

	@MockBean
	ReportOutputExecutor reportOutputExecutor;

	@MockBean
	UAEFTSReportService Uaefts;

	@Autowired
	AdvanceSearchReportServiceImpl advanceSearchReportServiceImpl;

	@MockBean
	FlexDetailedReportServiceImpl flexReportServiceImpl;

	@Test
	void testPopulateBaseInput() {
		List<ReportPromptsInstanceDTO> promptsList = new ArrayList<ReportPromptsInstanceDTO>();
		PromptInstance promptsInstance = new PromptInstance();
		promptsInstance.setKey("AccountNumber");
		List<String> valueList = new ArrayList<String>();
		valueList.add("019010125320");
		promptsInstance.setValue(valueList);

		ReportPromptsInstanceDTO prompts = new ReportPromptsInstanceDTO();
		prompts.setPrompt(promptsInstance);
		promptsList.add(prompts);

		promptsInstance.setKey("TransactionStatus");
		List<String> valueList1 = new ArrayList<String>();
		valueList1.add("019010125320");
		promptsInstance.setValue(valueList1);
		prompts.setPrompt(promptsInstance);
		promptsList.add(prompts);

		promptsInstance.setKey("Currency");
		List<String> valueList2 = new ArrayList<String>();
		valueList2.add("019010125320");
		valueList2.add("019010125320");
		promptsInstance.setValue(valueList2);
		prompts.setPrompt(promptsInstance);
		promptsList.add(prompts);

		promptsInstance.setKey("AmountFrom");
		List<String> valueList3 = new ArrayList<String>();
		valueList3.add("019010125320");
		valueList3.add("019010125320");
		promptsInstance.setValue(valueList3);
		prompts.setPrompt(promptsInstance);
		promptsList.add(prompts);

		promptsInstance.setKey("AmountTo");
		List<String> valueList4 = new ArrayList<String>();
		valueList4.add("019010125320");
		valueList4.add("019010125320");
		promptsInstance.setValue(valueList4);
		prompts.setPrompt(promptsInstance);
		promptsList.add(prompts);

		promptsInstance.setKey("TransactionReferenceNo");
		List<String> valueList5 = new ArrayList<String>();
		valueList5.add("019010125320");
		valueList5.add("019010125320");
		promptsInstance.setValue(valueList5);
		prompts.setPrompt(promptsInstance);
		promptsList.add(prompts);

		promptsInstance.setKey("FromDate");
		List<String> valueList6 = new ArrayList<String>();
		valueList6.add("019010125320");
		valueList6.add("019010125320");
		promptsInstance.setValue(valueList6);
		prompts.setPrompt(promptsInstance);
		promptsList.add(prompts);

		promptsInstance.setKey("ToDate");
		List<String> valueList7 = new ArrayList<String>();
		valueList7.add("019010125320");
		valueList7.add("019010125320");
		promptsInstance.setValue(valueList7);
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

		Report report = new Report();
		report.setId(1L);

		ReportInput response = advanceSearchReportServiceImpl.populateBaseInputContext(reportContext);
		assertNotNull(response);
	}

	@Test
	void TestProcessReport() {
		List<AdvanceSearchReportOutput> mockOutputList = new ArrayList<AdvanceSearchReportOutput>();
		AdvanceSearchReportInput advanceSearchReportInput = new AdvanceSearchReportInput();
		FederatedReportPromptDTO accountNumPrompt = new FederatedReportPromptDTO();
		accountNumPrompt.setPromptKey("AccountNumber");
		List<String> valueList = new ArrayList<String>();
		valueList.add("019010125320");
		accountNumPrompt.setValueList(valueList);
		advanceSearchReportInput.setAccountNumPrompt(accountNumPrompt);

		Report report = new Report();
		report.setId(1L);

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

		AdvanceSearchReportOutput mockAdvanceSearchReportOutput = new AdvanceSearchReportOutput();
		mockAdvanceSearchReportOutput.setAccountNum("123324324");
		mockAdvanceSearchReportOutput.setActivityName("sample Test case");
		mockAdvanceSearchReportOutput.setAmount("10000");
		mockAdvanceSearchReportOutput.setTransactionDate(null);
		mockAdvanceSearchReportOutput.setInitationSource("FLEX");
		mockAdvanceSearchReportOutput.setMessageThrough("UAEFTS");
		mockOutputList.add(mockAdvanceSearchReportOutput);

		when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(report);
		when(cannedReportService.populateCannedReportInstance(Mockito.<Report>any())).thenReturn(cannedReport);
		when(componentsDAO.findAllByreportId(anyLong())).thenReturn(mockComponentsList);
		/*
		 * when(matrixPaymentReportService.processMatrixPaymentReport(
		 * advanceSearchReportInput, mockComponentsList,
		 * reportContext)).thenReturn(mockOutputList);
		 * when(Uaefts.processAdvanceSearchReport(advanceSearchReportInput,
		 * mockComponentsList, reportContext)) .thenReturn(mockOutputList);
		 */
		ReportExecuteResponseData actualProcessSwiftDetailReportResult = advanceSearchReportServiceImpl
				.processReport(advanceSearchReportInput, reportContext);
		assertNotNull(actualProcessSwiftDetailReportResult);
		verify(cannedReportService).populateCannedReportInstance(Mockito.<Report>any());
		verify(componentsDAO).findAllByreportId(anyLong());
		verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
	}
}