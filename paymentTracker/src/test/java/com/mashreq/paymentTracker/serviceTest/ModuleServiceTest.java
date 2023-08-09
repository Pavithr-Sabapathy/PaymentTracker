package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.ModuleDAO;
import com.mashreq.paymentTracker.dto.ModuleDTO;
import com.mashreq.paymentTracker.dto.ModuleResponseDTO;
import com.mashreq.paymentTracker.dto.ReportDTO;
import com.mashreq.paymentTracker.dto.ReportDTORequest;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.serviceImpl.ModuleServiceImpl;
import com.mashreq.paymentTracker.serviceImpl.ReportConfigurationServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class ModuleServiceTest {
	@InjectMocks
	ModuleServiceImpl mockModuleService;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private ModuleDAO mockModuleRepository;

	@Mock
	ReportConfigurationServiceImpl mockReportServiceImpl;

	
	@Test
	public void testFetchAllModule() {
		List<ApplicationModule> mockModuleList = Arrays.asList(
	        	new ApplicationModule(1L, "Module 1", "dis_name1", "modDesc1", "active1", "valid1", "error1", "warning1"),
	        	new ApplicationModule(1L, "Module 2", "dis_name2", "modDesc2", "active2", "valid2", "error2", "warning2")

	        );

	        List<ModuleResponseDTO> mockResponseList = Arrays.asList(
	            new ModuleResponseDTO(),
	            new ModuleResponseDTO()
	        );

	        when(mockModuleRepository.findAll()).thenReturn(mockModuleList);

	        when(modelMapper.map(mockModuleList, new TypeToken<List<ModuleResponseDTO>>() {}.getType()))
	            .thenReturn(mockResponseList);

	        List<ModuleResponseDTO> result = mockModuleService.fetchAllModule();

	        verify(mockModuleRepository, times(1)).findAll();

	        verify(modelMapper, times(1)).map(mockModuleList, new TypeToken<List<ModuleResponseDTO>>() {}.getType());

	        assertEquals(mockResponseList, result);
	}

	
	@Test
    public void testSaveModule() {
      
        ModuleDTO moduleRequest = new ModuleDTO();
        moduleRequest.setName("Test Module");

        ApplicationModule applicationModule = new ApplicationModule();
        applicationModule.setModuleName("Test Module");
        applicationModule.setError(ApplicationConstants.REPORT_ERROR_DESCRIPTION);
        applicationModule.setWarning(ApplicationConstants.REPORT_WARNING_DESCRIPTION);
        
        List<ReportDTO> reportDtoList = new ArrayList<>();
        ReportDTO report1 = new ReportDTO();
        report1.setActive("Y");
        reportDtoList.add(report1);
        
        ModuleResponseDTO moduleRespDto = new ModuleResponseDTO();
        moduleRespDto.setModuleName("module name");

        when(mockReportServiceImpl.fetchReportsByModule(moduleRequest.getName())).thenReturn(reportDtoList);
        when(mockModuleRepository.save(any(ApplicationModule.class))).thenReturn(applicationModule);
        
        when(modelMapper.map(moduleRequest, ApplicationModule.class)).thenReturn(applicationModule);
        when(modelMapper.map(applicationModule, ModuleResponseDTO.class)).thenReturn(moduleRespDto);

        ModuleResponseDTO moduleResponseDTO = mockModuleService.saveModule(moduleRequest);
        
        assertNotNull(moduleResponseDTO);
        assertEquals(moduleRespDto, moduleResponseDTO);

        verify(mockModuleRepository, times(1)).save(any(ApplicationModule.class));
    }
	@Test
    public void testFetchModuleByName() {
        String moduleName = "moduleName";

        List<ApplicationModule> mockModuleList = Arrays.asList(
	        	new ApplicationModule(1L, "Module 1", "dis_name1", "modDesc1", "active1", "valid1", "error1", "warning1"),
	        	new ApplicationModule(1L, "Module 2", "dis_name2", "modDesc2", "active2", "valid2", "error2", "warning2")
	        );

	        List<ModuleResponseDTO> mockResponseList = Arrays.asList(
	            new ModuleResponseDTO(),
	            new ModuleResponseDTO()
	        );
      
        when(mockModuleRepository.findByModuleName(moduleName)).thenReturn(mockModuleList);

        when(modelMapper.map(mockModuleList, new TypeToken<List<ModuleResponseDTO>>() {}.getType()))
            .thenReturn(mockResponseList);

        List<ModuleResponseDTO> actualResponse = mockModuleService.fetchModuleByName(moduleName);

        verify(mockModuleRepository, times(1)).findByModuleName(moduleName);
        verify(modelMapper, times(1)).map(mockModuleList, new TypeToken<List<ModuleResponseDTO>>() {}.getType());

        assertNotNull(actualResponse);
        assertEquals(mockResponseList, actualResponse);
    }
	@Test
	public void testFetchModuleByNameNotExists() throws Exception {
		String moduleName = "module name";
		
		when(mockModuleRepository.findByModuleName(moduleName)).thenReturn(null);
		
		assertThrows(ResourceNotFoundException.class,
	                () -> mockModuleService.fetchModuleByName(moduleName));
		
	     verify(mockModuleRepository, times(1)).findByModuleName(moduleName);

	}
	@Test
    public void testUpdateModule() {
        Long moduleId = 1L;
        ModuleDTO moduleRequest = new ModuleDTO();
        moduleRequest.setName("Test Module");
        
        ApplicationModule existingModule = new ApplicationModule();
        existingModule.setModuleName("Test Module");

        List<ReportDTO> reportList = new ArrayList<>();
        ReportDTO report1 = new ReportDTO();
        report1.setActive("Y");
        reportList.add(report1);
    
        ApplicationModule updatedModule = new ApplicationModule();
        updatedModule.setModuleName("module name");
        
        ModuleResponseDTO moduleRespDto = new ModuleResponseDTO();
        moduleRespDto.setModuleName("module name");
        
        when(mockModuleRepository.findById(moduleId)).thenReturn(existingModule);

        when(mockReportServiceImpl.fetchReportsByModule(existingModule.getModuleName())).thenReturn(reportList);

        when(modelMapper.map(moduleRequest, ApplicationModule.class)).thenReturn(updatedModule);
        when(modelMapper.map(moduleRespDto, ApplicationModule.class)).thenReturn(updatedModule);

        when(mockModuleRepository.update(updatedModule)).thenReturn(updatedModule);

        mockModuleService.updateModule(moduleRequest, moduleId);
        
        verify(mockModuleRepository, times(1)).findById(moduleId);
        verify(mockModuleRepository, times(1)).update(updatedModule);
     
        assertEquals("module name", updatedModule.getModuleName());
     
    }
    @Test
    public void testUpdateModuleNotExist() {
        Long nonExistentModuleId = 100L;
        ModuleDTO moduleRequest = new ModuleDTO();
        
        when(mockModuleRepository.findById(nonExistentModuleId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> mockModuleService.updateModule(moduleRequest, nonExistentModuleId));

        verify(mockModuleRepository, times(1)).findById(nonExistentModuleId);
    }
	@Test
	public void testDeleteById() {
		long moduleId = 1L;

		doNothing().when(mockModuleRepository).deleteById(moduleId);

		mockModuleService.deleteModule(moduleId);

		verify(mockModuleRepository).deleteById(1L);
	}

}