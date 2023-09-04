package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mashreq.paymentTracker.dao.ReportDataDAO;
import com.mashreq.paymentTracker.dao.ReportExecutionDAO;
import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDataDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseMetaDTO;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.model.ReportInstance;
import com.mashreq.paymentTracker.repository.ReportInstanceRepository;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.SwiftDetailedReportService;
import com.mashreq.paymentTracker.serviceImpl.ReportHandlerServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportHandlerServiceTest {
	
	@MockBean
	private ReportConfigurationService reportConfigurationService;

	@MockBean
	private ReportDataDAO reportDataRepository;

	@MockBean
	private ReportExecutionDAO reportExecutionRepoistory;

	@Autowired
	private ReportHandlerServiceImpl reportHandlerService;

	@MockBean
	private ReportInstanceRepository reportInstanceRepository;

	@Mock
	private ModelMapper modelMapper;

	@MockBean
	private SwiftDetailedReportService swiftDetailedReportService;
	
@Test
public void testExecuteReportTestCase1() {
	String reportName ="Report Name";
	ReportExecutionRequest reportExecutionRequest=new ReportExecutionRequest();
	reportExecutionRequest
	.setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
	reportExecutionRequest.setIsMapLinked(true);
	reportExecutionRequest.setLinkExecution(true);
	reportExecutionRequest.setLinkInstanceId(1L);
	reportExecutionRequest.setLinkReference("Link Reference");
	reportExecutionRequest.setPrompts(new ArrayList<>());
	reportExecutionRequest.setRole("Role");
	reportExecutionRequest.setUserId(1L);
	reportExecutionRequest.setUserName("janedoe");
	
	ReportInstance reportInstance = new ReportInstance();
	reportInstance.setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
	reportInstance.setReportDesc("Report Description");
	reportInstance.setReportName("Report Name");
	reportInstance.setId(1L);
	reportInstance = reportInstanceRepository.save(reportInstance);
	
	ReportInstanceDTO reportInstanceDTO =new ReportInstanceDTO();
	reportInstanceDTO.setUserName("Test Name");
	reportInstanceDTO.setId(1L);
	reportInstanceDTO.setRoleName("Admin");
	reportInstanceDTO.setRoleId(1L);
	reportInstanceDTO.setModuleId(1L);
	reportInstanceDTO
			.setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
	ReportDataDTO reportDataDTO =new ReportDataDTO();
	reportDataDTO.setId(1L);
	reportDataDTO.setReportData("Data");
	reportDataDTO.setReportExecutionId(1L);
	
	ReportContext reportContext =new ReportContext();
	reportContext.setExecutionId(1L);
	reportContext.setLinkedReport(true);
	reportContext.setLinkInstanceId(1L);
	reportContext.setLinkReference("reference");
	reportContext.setModuleId(1L);
	reportContext.setReportId(1L);
	reportContext.setReportInstance(reportInstanceDTO);
	reportContext.setReportName("Report Name");
	reportContext.setRoleId(1L);
	reportContext.setRoleName("Admin");
	reportContext.setUserId(1L);
	reportContext.setModuleId(1L);
	
	when(modelMapper.map(reportInstanceDTO, ReportContext.class)).thenReturn(reportContext);
	
	when(modelMapper.map(reportExecutionRequest, ReportInstanceDTO.class)).thenReturn(reportInstanceDTO);

	
	ReportExecuteResponseData response = reportHandlerService.executeReport(reportName,
			reportExecutionRequest);
	assertNotNull(response);


	
}

@Test
void testPopulateSuccessAPIResponeTestCase1() {
	ReportExecuteResponseMetaDTO meta = new ReportExecuteResponseMetaDTO();
	meta.setEndTime("End Time");
	meta.setExecutionTime(1L);
	meta.setReportId("42");
	meta.setStartTime("Start Time");
	meta.setTotalExists(true);

	ReportExecuteResponseData flexList = new ReportExecuteResponseData();
	flexList.setColumnDefs(new ArrayList<>());
	flexList.setData(new ArrayList<>());
	flexList.setMeta(meta);
	APIResponse actualPopulateSuccessAPIResponeResult = reportHandlerService
			.populateSuccessAPIRespone(flexList);
	assertSame(flexList, actualPopulateSuccessAPIResponeResult.getData());
	assertTrue(actualPopulateSuccessAPIResponeResult.isStatus());
	assertEquals("Report Execution Success", actualPopulateSuccessAPIResponeResult.getMessage());
}

@Test
void testPopulateSuccessAPIResponeTestCase2() {

	ReportExecuteResponseMetaDTO meta = new ReportExecuteResponseMetaDTO();
	meta.setEndTime("End Time");
	meta.setExecutionTime(1L);
	meta.setReportId("42");
	meta.setStartTime("Start Time");
	meta.setTotalExists(true);

	ReportExecuteResponseData flexList = new  ReportExecuteResponseData();


	flexList.setColumnDefs(new ArrayList<>());
	flexList.setData(new ArrayList<>());
	flexList.setMeta(meta);

	APIResponse actualPopulateSuccessAPIResponeResult = reportHandlerService
			.populateSuccessAPIRespone(flexList);

	assertNotNull(actualPopulateSuccessAPIResponeResult);
	assertEquals("Report Execution Success", actualPopulateSuccessAPIResponeResult.getMessage());

	verify(flexList).setColumnDefs(Mockito.<List<ReportExecuteResponseColumnDefDTO>>any());
	verify(flexList).setData(Mockito.<List<Map<String, Object>>>any());
	verify(flexList).setMeta(Mockito.<ReportExecuteResponseMetaDTO>any());
}
	
	

}
