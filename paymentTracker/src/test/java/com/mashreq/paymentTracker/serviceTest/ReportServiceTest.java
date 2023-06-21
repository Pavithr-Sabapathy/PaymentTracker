package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.serviceImpl.ReportConfigurationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

	@InjectMocks
	ReportConfigurationServiceImpl reportConfigurationService;

	@Mock
	ReportConfigurationRepository mockreportConfigurationRepo;
	
	@Mock
    private ModelMapper modelMapper;

	@Test
	public void testSaveReports() throws Exception {
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		
		ReportDTORequest mockReportDTORequest = new ReportDTORequest();
		mockReportDTORequest.setActive("y");
		mockReportDTORequest.setDisplayName("Reference Number");
		mockReportDTORequest.setReportCategory("Reference");
		mockReportDTORequest.setReportDescription("Search");
		mockReportDTORequest.setReportName("Refernce_No");
		mockReportDTORequest.setValid("N");
		mockReportDTORequest.setModuleName("sample");
		//Report reportsResponse = modelMapper.map(mockReportDTORequest, Report.class);
		
		when(modelMapper.map(mockReportDTORequest, Report.class)).thenReturn(mockReportsResponse);
		when(mockreportConfigurationRepo.save(mockReportsResponse)).thenReturn(mockReportsResponse);
		
		Report Report = reportConfigurationService.saveReport(mockReportDTORequest);
		assertEquals(Report.getDisplayName(), "Reference Number");
		verify(mockreportConfigurationRepo, times(1)).save(mockReportsResponse);
	}

	@Test
	public void testfetchAllReports() {
		List<Report> mockReportsList = new ArrayList<Report>();
	
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");

		mockReportsList.add(mockReportsResponse);

		when(mockreportConfigurationRepo.findAll()).thenReturn(mockReportsList);

		// test
		List<Report> reportsResponse = reportConfigurationService.fetchAllReports();

		assertEquals(1, reportsResponse.size());
		verify(mockreportConfigurationRepo, times(1)).findAll();
	}
	@Test
	public void fetchReportByName() {
		String name = "report name";
		
		Report mockReportsResponse = new Report();
		mockReportsResponse.setReportName("Report_Name");
	
		when(mockreportConfigurationRepo.findByReportName(name)).thenReturn(mockReportsResponse);

		reportConfigurationService.fetchReportByName(name);

		verify(mockreportConfigurationRepo).findByReportName(name);

	}

	@Test
	public void testdeleteReportById() {
		long reportId = 1L;

		when(mockreportConfigurationRepo.existsById(reportId)).thenReturn(true);
		doNothing().when(mockreportConfigurationRepo).deleteById(reportId);

		reportConfigurationService.deleteReportById(reportId);

		verify(mockreportConfigurationRepo).existsById(1L);
		verify(mockreportConfigurationRepo).deleteById(1L);
	}

	@Test
	void testdeleteReportByIdNotExists() throws ResourceNotFoundException {
		long reportId = 1L;

		when(mockreportConfigurationRepo.existsById(reportId)).thenReturn(false);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> reportConfigurationService.deleteReportById(reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
	}

	@Test
	public void testupdateReportById() {
		long reportId = 1L;
		Report mockReportsResponse = new Report();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Old DisplayName");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.of(mockReportsResponse));
		
		ReportDTORequest reportDtoRequest = new ReportDTORequest();
		when(modelMapper.map(reportDtoRequest, Report.class)).thenReturn(mockReportsResponse);
		
		//reportConfigurationService.updateReportById(any(ReportDTORequest.class), reportId);
		reportConfigurationService.updateReportById(reportDtoRequest, reportId);
		verify(mockreportConfigurationRepo, times(1)).findById(reportId);
		verify(mockreportConfigurationRepo, times(1)).save(mockReportsResponse);
	}

	@Test
	public void testupdateReportByIdNotExists() throws ResourceNotFoundException {
		long reportId = 1L;
		Report mockReportsResponse = null;
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.ofNullable(mockReportsResponse));
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> reportConfigurationService.updateReportById(any(ReportDTORequest.class), reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
	}
}