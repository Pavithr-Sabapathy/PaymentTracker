package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dto.AdvanceSearchReportOutput;
import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.LinkedReportResponseDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.service.CannedReportService;
import com.mashreq.paymentTracker.service.LinkReportService;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.ReportOutput;
import com.mashreq.paymentTracker.serviceImpl.MatrixPaymentReportServiceImpl;
import com.mashreq.paymentTracker.type.CountryType;

@SpringBootTest
@AutoConfigureMockMvc
public class MatrixPaymentReportServiceTest {

	@Autowired
	MatrixPaymentReportServiceImpl matrixPaymentReportServiceImpl;

	@MockBean
	QueryExecutorService queryExecutorService;

	@MockBean
	private LinkReportService linkReportService;

	@MockBean
	CannedReportService cannedReportService;

	@MockBean
	private ReportConfigurationService reportConfigurationService;

	@MockBean
	private ComponentsDAO componentsDAO;

	@Test
	void testProcessMatrixPaymentReport() {

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

		ArrayList<Components> componentsList = new ArrayList<>();
		componentsList.add(new Components());
		List<Components> ofResult = new ArrayList<Components>();

		ArrayList<LinkedReportResponseDTO> linkedReportResponseDTOList = new ArrayList<>();
		linkedReportResponseDTOList.add(new LinkedReportResponseDTO(1L, "Link Name", "Link Description", "Report Name",
				0, 0, "Linked Report Name", "Source Metric Name", 0, "Active", null, null, 0, 0, null, 0));

		when(reportConfigurationService.fetchReportByName(Mockito.<String>any())).thenReturn(new Report());
		when(linkReportService.fetchLinkedReportByReportId(anyLong())).thenReturn(linkedReportResponseDTOList);
		when(componentsDAO.findAllByreportId(anyLong())).thenReturn(ofResult);

		List<? extends ReportOutput> actualProcessMatrixReportResult = matrixPaymentReportServiceImpl
				.processMatrixPaymentReport(null, reportContext);

		assertNotNull(actualProcessMatrixReportResult);

		assertEquals("Report Name",
				((ReportExecuteResponseData) actualProcessMatrixReportResult).getMeta().getReportId());

		verify(componentsDAO).findAllByreportId(anyLong());
		verify(linkReportService).fetchLinkedReportByReportId(anyLong());
		verify(reportConfigurationService).fetchReportByName(Mockito.<String>any());
	}

}
