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

import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Reports;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.serviceImpl.ReportConfigurationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

	@InjectMocks
	ReportConfigurationServiceImpl reportConfigurationService;

	@Mock
	ReportConfigurationRepository mockreportConfigurationRepo;

	@Test
	public void testSaveReports() {
		Reports mockReportsResponse = new Reports();
		
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		
		when(mockreportConfigurationRepo.save(mockReportsResponse)).thenReturn(mockReportsResponse);
		Reports reports = reportConfigurationService.saveReportConfiguration(any(ReportDTORequest.class));
		assertEquals(reports.getDisplayName(), "Reference Number");
		verify(mockreportConfigurationRepo, times(1)).save(mockReportsResponse);
	}

	@Test
	public void testfetchAllReports() {
		List<Reports> mockReportsList = new ArrayList<Reports>();
	
		Reports mockReportsResponse = new Reports();
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
		List<Reports> reportsResponse = reportConfigurationService.fetchAllReports();

		assertEquals(1, reportsResponse.size());
		verify(mockreportConfigurationRepo, times(1)).findAll();
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
		Reports mockReportsResponse = new Reports();
		mockReportsResponse.setActive("y");
		mockReportsResponse.setDisplayName("Reference Number");
		mockReportsResponse.setId(1L);
		mockReportsResponse.setReportCategory("Reference");
		mockReportsResponse.setReportDescription("Search");
		mockReportsResponse.setReportName("Refernce_No");
		mockReportsResponse.setValid("N");
		
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.of(mockReportsResponse));
		reportConfigurationService.updateReportById(any(ReportDTORequest.class), reportId);
		verify(mockreportConfigurationRepo, times(1)).findById(reportId);
	}

	@Test
	public void testupdateReportByIdNotExists() throws ResourceNotFoundException {
		long reportId = 1L;
		Reports mockReportsResponse = null;
		when(mockreportConfigurationRepo.findById(reportId)).thenReturn(Optional.ofNullable(mockReportsResponse));
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> reportConfigurationService.updateReportById(any(ReportDTORequest.class), reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
	}

}