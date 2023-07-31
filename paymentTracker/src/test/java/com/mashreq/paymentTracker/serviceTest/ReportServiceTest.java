package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.Metrics;
import com.mashreq.paymentTracker.model.Prompts;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.serviceImpl.ReportConfigurationServiceImpl;


@SpringBootTest
@AutoConfigureMockMvc
public class ReportServiceTest {

	@InjectMocks
	ReportConfigurationServiceImpl reportConfigurationService;

	@Mock
	ReportDAO mockreportConfigurationRepo;
	
	@Mock
    private ModelMapper modelMapper;

	@Test
    public void testSaveReport() {
		ReportDTORequest mockReportDTORequest = new ReportDTORequest();
		mockReportDTORequest.setActive("y");
		mockReportDTORequest.setDisplayName("Reference Number");
		mockReportDTORequest.setReportCategory("Reference");
		mockReportDTORequest.setReportDescription("Search");
		mockReportDTORequest.setReportName("Refernce_No");
		mockReportDTORequest.setValid("N");
		mockReportDTORequest.setModuleId(1L);
		
		Report mockReports = new Report();
		mockReports.setActive("y");
		mockReports.setDisplayName("Reference Number");
		mockReports.setId(1L);
		mockReports.setReportCategory("Reference");
		mockReports.setReportDescription("Search");
		mockReports.setReportName("Refernce_No");
		mockReports.setValid("N");
		
        ReportDTO expectedReportDTO = new ReportDTO();
        expectedReportDTO.setId(1L); 

        when(modelMapper.map(mockReports, ReportDTO.class)).thenReturn(expectedReportDTO);

        when(mockreportConfigurationRepo.saveReport(any(Report.class))).thenReturn(mockReports);

        ReportDTO actualReportDTO = reportConfigurationService.saveReport(mockReportDTORequest);

        assertNotNull(actualReportDTO);
        assertEquals(expectedReportDTO.getId(), actualReportDTO.getId());

        verify(mockreportConfigurationRepo, times(1)).saveReport(any(Report.class));

        verify(modelMapper, times(1)).map(mockReports, ReportDTO.class);
    }
	
	@Test
	public void fetchReportByName() throws Exception{
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

		doNothing().when(mockreportConfigurationRepo).deleteReport(reportId);

		reportConfigurationService.deleteReportById(reportId);

		verify(mockreportConfigurationRepo).deleteReport(1L);
	}

	@Test
    void fetchReportsByModule() throws Exception {
        String moduleName = "Module1";
        List<Report> reportList = new ArrayList<>();
        reportList.add(new Report("Report1"));
        reportList.add(new Report("Report2"));

        when(mockreportConfigurationRepo.findReportByModule(moduleName)).thenReturn(reportList);

        List<ReportDTO> result = reportConfigurationService.fetchReportsByModule(moduleName);

        assertEquals(2, result.size());

        verify(mockreportConfigurationRepo, times(1)).findReportByModule(moduleName);
        
    }

  
   @Test
   public void testUpdateReportById() {
	   long reportId = 1L;
	   ReportDTORequest reportUpdateRequest = new ReportDTORequest();
	   reportUpdateRequest.setReportName("report name");

	   	Report existingReport = new Report();
	   		existingReport.setId(reportId);
	   		existingReport.setReportName("report name");
    
	   	Report updatedReport = new Report();
	   		updatedReport.setId(reportId);

	   	ReportDTO expectedReportDTO = new ReportDTO(1L, "reportName1", "displayName1","reportDescription1","reportCategory1",
    			"active1","valid1", 1L,"connectorKey1");

	   	when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(existingReport);
	   	when(modelMapper.map(reportUpdateRequest, Report.class)).thenReturn(updatedReport);
	   	when(mockreportConfigurationRepo.updateReport(updatedReport)).thenReturn(updatedReport);
	   	when(modelMapper.map(updatedReport, ReportDTO.class)).thenReturn(expectedReportDTO);

	   	ReportDTO actualReportDTO = reportConfigurationService.updateReportById(reportUpdateRequest, reportId);

	   		assertNotNull(actualReportDTO);
	   		assertEquals(reportId, actualReportDTO.getId());
    
	   	verify(mockreportConfigurationRepo, times(1)).getReportById(reportId);
}



	@Test
	public void testupdateReportByIdNotExists() throws ResourceNotFoundException {
		long reportId = 1L;
		when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(null);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> reportConfigurationService.updateReportById(any(ReportDTORequest.class), reportId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertNotNull(thrown.getMessage().contains("Report Configuration not exist with this id :1"));
	}
	
	@Test
    public void testFetchReportsByModule() {
        String moduleName = "TestModule";
        List<Prompts> promptList = new ArrayList<>();
        List<Metrics> metricsList = new ArrayList<>();
        
        List<Report> reportList = new ArrayList<>();
        reportList.add(new Report(1L, "reportName1", "displayName1","reportDescription1","reportCategory1","active1","valid1",promptList,metricsList));
        reportList.add(new Report(2L, "reportName2", "displayName2","reportDescription2","reportCategory2","active2","valid2",promptList,metricsList));
        
        List<ReportDTO> expectedReportDTOList = new ArrayList<>();
        expectedReportDTOList.add(new ReportDTO(1L, "reportName1", "displayName1","reportDescription1","reportCategory1",
    			"active1","valid1", 1L,"connectorKey1"));
        expectedReportDTOList.add(new ReportDTO(2L, "reportName2", "displayName2","reportDescription2","reportCategory2",
    			"active2","valid2", 2L,"connectorKey2"));
        
        when(mockreportConfigurationRepo.findReportByModule(moduleName)).thenReturn(reportList);

        when(modelMapper.map(reportList, ReportDTO.class)).thenReturn(expectedReportDTOList.get(0));
        when(modelMapper.map(reportList, ReportDTO.class)).thenReturn(expectedReportDTOList.get(1));

        List<ReportDTO> actualReportDTOList = reportConfigurationService.fetchReportsByModule(moduleName);

        assertNotNull(actualReportDTOList);
        assertFalse(actualReportDTOList.isEmpty());
        assertEquals(expectedReportDTOList.size(), actualReportDTOList.size());
    }
	@Test
	public void testFetchReportsByModuleIdNotExists() throws ResourceNotFoundException {
		 Long moduleId = 100L;

	     when(mockreportConfigurationRepo.findByModuleId(moduleId)).thenReturn(new ArrayList<>());

	     assertThrows(ResourceNotFoundException.class,
	                () -> reportConfigurationService.fetchReportsByModuleId(moduleId));

	     verify(mockreportConfigurationRepo, times(1)).findByModuleId(moduleId);
	}
	
	@Test
    public void testFetchReportByIdExist() {
        Long reportId = 1L;
        Report reportObject = new Report();
        reportObject.setId(reportId);
        reportObject.setReportName("Test Report");

        ReportDTO expectedReportDTO = new ReportDTO();
        expectedReportDTO.setId(reportId);
        expectedReportDTO.setReportName("Test Report");

        when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(reportObject);

        when(modelMapper.map(reportObject, ReportDTO.class)).thenReturn(expectedReportDTO);

        ReportDTO actualReportDTO = reportConfigurationService.fetchReportById(reportId);

        assertNotNull(actualReportDTO);
        assertEquals(expectedReportDTO.getId(), actualReportDTO.getId());
        assertEquals(expectedReportDTO.getReportName(), actualReportDTO.getReportName());
    }
	@Test
    public void testFetchReportByIdNotExist() {
        Long reportId = 1L;

        when(mockreportConfigurationRepo.getReportById(reportId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
        	reportConfigurationService.fetchReportById(reportId);
        });
    }
}