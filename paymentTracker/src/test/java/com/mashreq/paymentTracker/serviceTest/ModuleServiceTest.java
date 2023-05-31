package com.mashreq.paymentTracker.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ApplicationModule;
import com.mashreq.paymentTracker.repository.ModuleRepository;
import com.mashreq.paymentTracker.serviceImpl.ModuleServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {
	@InjectMocks
	ModuleServiceImpl mockModuleService;
	
	@Mock
	private ModuleRepository mockModuleRepository;
	 
	@Test
	public void testFetchModule() {
		//set
		ApplicationModule moduleMockObject = new ApplicationModule();
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");
		
		List<ApplicationModule> metricsMockList = Arrays.asList(moduleMockObject);

		when(mockModuleRepository.findAll()).thenReturn(metricsMockList);

		// test
		List<ApplicationModule> metricsMockResponse = mockModuleService.fetchAllModule();

		assertEquals(1, metricsMockResponse.size());
		//verify
		verify(mockModuleRepository, times(1)).findAll();
	}
		
	@Test
	public void testSaveModule() {
		//set
		ApplicationModule moduleMockObject = new ApplicationModule();
		moduleMockObject.setModuleName("module1");
		moduleMockObject.setDisplayName("sampleModule");
		moduleMockObject.setModuleDescription("ModuleDesc");
		moduleMockObject.setActive("active");
		moduleMockObject.setValid("valid");
		//method call
		mockModuleService.saveModule(moduleMockObject);
		
		//verify
		 ArgumentCaptor<ApplicationModule> argumentCaptor = ArgumentCaptor.forClass(ApplicationModule.class);
	        verify(mockModuleRepository).save(argumentCaptor.capture());
	        ApplicationModule savedModule = argumentCaptor.getValue();
	        assertEquals(moduleMockObject.getModuleName(), savedModule.getModuleName());
	        assertEquals("sampleModule", savedModule.getDisplayName());
	        assertEquals("ModuleDesc", savedModule.getModuleDescription());
	        assertEquals("active", savedModule.getActive());
	        assertEquals("valid", savedModule.getValid());
	}
	@Test
	public void testDeleteById(){
		long moduleId = 1L;

		when(mockModuleRepository.existsById(moduleId)).thenReturn(true);
		doNothing().when(mockModuleRepository).deleteById(moduleId);

		mockModuleService.deleteModule(moduleId);;

		verify(mockModuleRepository).existsById(1L);
		verify(mockModuleRepository).deleteById(1L);
	}
	@Test
	void testdeleteModuleByIdNotExists() throws ResourceNotFoundException {
		long moduleId = 1L;

		when(mockModuleRepository.existsById(moduleId)).thenReturn(false);
		mockModuleRepository.deleteById(moduleId);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> mockModuleService.deleteModule(moduleId),
				"Expected ModuleService.deleteModuleById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains(ApplicationConstants.MODULE_DOES_NOT_EXISTS));
	}
	
}
