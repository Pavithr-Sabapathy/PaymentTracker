package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashreq.paymentTracker.dto.APIResponse;
import com.mashreq.paymentTracker.dto.PromptsProcessingRequest;
import com.mashreq.paymentTracker.dto.ReportContext;
import com.mashreq.paymentTracker.dto.ReportDataDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseColumnDefDTO;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportExecuteResponseMetaDTO;
import com.mashreq.paymentTracker.dto.ReportExecutionRequest;
import com.mashreq.paymentTracker.dto.ReportInstanceDTO;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.model.ReportInstance;
import com.mashreq.paymentTracker.repository.ReportDataRepository;
import com.mashreq.paymentTracker.repository.ReportExecutionRepoistory;
import com.mashreq.paymentTracker.repository.ReportInstanceRepository;
import com.mashreq.paymentTracker.service.FlexFederatedReportService;
import com.mashreq.paymentTracker.service.ReportConfigurationService;
import com.mashreq.paymentTracker.service.SwiftDetailedReportService;
import com.mashreq.paymentTracker.serviceImpl.ReportHandlerServiceImpl;

@ContextConfiguration(classes = { ReportHandlerServiceImpl.class })
@ExtendWith(SpringExtension.class)
class ReportHandlerServiceTest {

	@MockBean
	private FlexFederatedReportService flexFederatedReportService;

	@MockBean
	private ReportConfigurationService reportConfigurationService;

	@MockBean
	private ReportDataRepository reportDataRepository;

	@MockBean
	private ReportExecutionRepoistory reportExecutionRepoistory;

	@Autowired
	private ReportHandlerServiceImpl reportHandlerServiceImpl;

	@MockBean
	private ReportInstanceRepository reportInstanceRepository;

	@Mock
	private ModelMapper modelMapper;

	@MockBean
	private SwiftDetailedReportService swiftDetailedReportService;

	@Test
	void testExecuteReportTestCase1() throws JsonProcessingException, ReportException {

		ReportExecutionRequest reportExecutionRequest = mock(ReportExecutionRequest.class);

		doNothing().when(reportExecutionRequest).setCreateDate(Mockito.<Date>any());
		doNothing().when(reportExecutionRequest).setIsMapLinked(Mockito.<Boolean>any());
		doNothing().when(reportExecutionRequest).setLinkExecution(Mockito.<Boolean>any());
		doNothing().when(reportExecutionRequest).setLinkInstanceId(anyLong());
		doNothing().when(reportExecutionRequest).setLinkReference(Mockito.<String>any());
		doNothing().when(reportExecutionRequest).setPrompts(Mockito.<List<PromptsProcessingRequest>>any());
		doNothing().when(reportExecutionRequest).setRole(Mockito.<String>any());
		doNothing().when(reportExecutionRequest).setUserId(anyLong());
		doNothing().when(reportExecutionRequest).setUserName(Mockito.<String>any());

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

		ReportInstance reportInstance = mock(ReportInstance.class);
		doNothing().when(reportInstance).setId(anyLong());
		doNothing().when(reportInstance).setCreateDate(Mockito.<Date>any());
		doNothing().when(reportInstance).setReportDesc(anyString());
		doNothing().when(reportInstance).setReportName(anyString());

		reportInstance.setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
		reportInstance.setReportDesc("Report Description");
		reportInstance.setReportName("Report Name");
		reportInstance.setId(1L);

		Mockito.when(reportInstanceRepository.save(reportInstance)).thenReturn(reportInstance);

		reportInstance = reportInstanceRepository.save(reportInstance);
		
		ReportInstanceDTO reportInstanceDTO = mock(ReportInstanceDTO.class);
		doNothing().when(reportInstanceDTO).setUserName(anyString());
		doNothing().when(reportInstanceDTO).setId(anyLong());
		doNothing().when(reportInstanceDTO).setRoleName(anyString());
		doNothing().when(reportInstanceDTO).setRoleId(anyLong());
		doNothing().when(reportInstanceDTO).setModuleId(anyLong());
		doNothing().when(reportInstanceDTO).setCreationDate(Mockito.<Date>any());

		reportInstanceDTO.setUserName("Test Name");
		reportInstanceDTO.setId(1L);
		reportInstanceDTO.setRoleName("Admin");
		reportInstanceDTO.setRoleId(1L);
		reportInstanceDTO.setModuleId(1L);
		reportInstanceDTO
				.setCreationDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
		;

		ReportDataDTO reportDataDTO = mock(ReportDataDTO.class);

		reportDataDTO.setId(1L);
		reportDataDTO.setReportData("Data");
		reportDataDTO.setReportExecutionId(1L);

		ReportContext reportContext = mock(ReportContext.class);

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

		ReportExecuteResponseData reportExecuteResponseData = reportHandlerServiceImpl.executeReport("Report Name",
				reportExecutionRequest);
		
		assertEquals("Report Name", reportExecuteResponseData.getMeta().getReportId());
		assertNotNull(reportExecuteResponseData);

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
		APIResponse actualPopulateSuccessAPIResponeResult = reportHandlerServiceImpl
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

		ReportExecuteResponseData flexList = mock(ReportExecuteResponseData.class);

		doNothing().when(flexList).setColumnDefs(Mockito.<List<ReportExecuteResponseColumnDefDTO>>any());
		doNothing().when(flexList).setData(Mockito.<List<Map<String, Object>>>any());
		doNothing().when(flexList).setMeta(Mockito.<ReportExecuteResponseMetaDTO>any());

		flexList.setColumnDefs(new ArrayList<>());
		flexList.setData(new ArrayList<>());
		flexList.setMeta(meta);

		APIResponse actualPopulateSuccessAPIResponeResult = reportHandlerServiceImpl
				.populateSuccessAPIRespone(flexList);

		assertNotNull(actualPopulateSuccessAPIResponeResult);
		assertEquals("Report Execution Success", actualPopulateSuccessAPIResponeResult.getMessage());

		verify(flexList).setColumnDefs(Mockito.<List<ReportExecuteResponseColumnDefDTO>>any());
		verify(flexList).setData(Mockito.<List<Map<String, Object>>>any());
		verify(flexList).setMeta(Mockito.<ReportExecuteResponseMetaDTO>any());
	}
}
