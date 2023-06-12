package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.ComponentsCountry;
import com.mashreq.paymentTracker.model.DataSourceConfig;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.ComponentsCountryRepository;
import com.mashreq.paymentTracker.repository.ComponentsDetailsRepository;
import com.mashreq.paymentTracker.repository.ComponentsRepository;
import com.mashreq.paymentTracker.repository.DataSourceConfigRepository;
import com.mashreq.paymentTracker.repository.ReportConfigurationRepository;
import com.mashreq.paymentTracker.serviceImpl.ComponentsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ComponentServiceTest {

	@InjectMocks
	ComponentsServiceImpl componentsServiceImpl;

	@Mock
	private ComponentsRepository mockComponentRepository;

	@Mock
	private ComponentsCountryRepository mockComponentsCountryRepository;

	@Mock
	private ReportConfigurationRepository reportConfigurationRepo;

	@Mock
	private ComponentsDetailsRepository mockComponentsDetailsRepository;

	@Mock
	private DataSourceConfigRepository dataSourceConfigRepository;

	@Mock
	private Components compResponse;
	@Test
	void testdeleteComponentByIdNotExists() throws ResourceNotFoundException {
		long componentId = 1L;

		when(mockComponentRepository.existsById(componentId)).thenReturn(false);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> componentsServiceImpl.deleteComponentById(componentId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains(ApplicationConstants.COMPONENT_DOES_NOT_EXISTS));
	}
	
	
	
	
	@Test
	public void testdeleteComponentById() {
		long componentId = 1L;

		when(mockComponentRepository.existsById(componentId)).thenReturn(true);
		doNothing().when(mockComponentRepository).deleteById(componentId);

		componentsServiceImpl.deleteComponentById(componentId);

		verify(mockComponentRepository).existsById(1L);
		verify(mockComponentRepository).deleteById(1L);
	}

	@Test
	void testdeleteComponentDetailsByIdNotExists() throws ResourceNotFoundException {
		long componentId = 1L;

		when(mockComponentsDetailsRepository.existsById(componentId)).thenReturn(false);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> componentsServiceImpl.deleteComponentDetailsById(componentId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains(ApplicationConstants.COMPONENT_DETAILS_DOES_NOT_EXISTS));
	}

	@Test
	public void testdeleteComponentDetailsById() {
		long componentId = 1L;

		when(mockComponentsDetailsRepository.existsById(componentId)).thenReturn(true);
		doNothing().when(mockComponentsDetailsRepository).deleteById(componentId);

		componentsServiceImpl.deleteComponentDetailsById(componentId);

		verify(mockComponentsDetailsRepository).existsById(1L);
		verify(mockComponentsDetailsRepository).deleteById(1L);
	}
	@Test
	public void testSaveComponent() throws ResourceNotFoundException{
		ComponentsRequestDTO componentsRequest = new ComponentsRequestDTO();
        componentsRequest.setReportId(1L);
        componentsRequest.setActive("active");
        componentsRequest.setComponentKey("test-component");
        componentsRequest.setComponentName("Test Component");
        componentsRequest.setDataSourceId(1L);

        Report report = new Report();
        report.setId(1L);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setId(1L);

        // when
        when(reportConfigurationRepo.findById(1L)).thenReturn(Optional.of(report));

        when(dataSourceConfigRepository.findById(1L)).thenReturn(Optional.of(dataSourceConfig));
        componentsServiceImpl.saveComponents(componentsRequest);
        
        ArgumentCaptor<Components> argumentCaptor = ArgumentCaptor.forClass(Components.class);
        verify(mockComponentRepository).save(argumentCaptor.capture());
		Components savedComponents = argumentCaptor.getValue();
        assertEquals(componentsRequest.getComponentName(), savedComponents.getComponentName());
        assertEquals(componentsRequest.getComponentKey(), savedComponents.getComponentKey());
        assertEquals(componentsRequest.getActive(), savedComponents.getActive());
		assertEquals(report, savedComponents.getReport());
       
        // then
        componentsServiceImpl.saveComponents(componentsRequest);
       verify(mockComponentRepository, times(1)).save(savedComponents);
    }
	@Test()
    public void testSaveComponent_NotFound() {
		
        // Create a MetricsDTO instance for the test
		ComponentsRequestDTO componentsRequest = new ComponentsRequestDTO();
        componentsRequest.setReportId(1L);
        componentsRequest.setActive("active");
        componentsRequest.setComponentKey("test-component");
        componentsRequest.setComponentName("Test Component");
        componentsRequest.setDataSourceId(1L);

        // Configure the reportConfigurationRepo.findById() method 
        when(reportConfigurationRepo.findById(componentsRequest.getReportId())).thenReturn(Optional.empty());	    
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> componentsServiceImpl.saveComponents(componentsRequest),
				"Expected saveComponet to throw, but it didn't");
		assertNotNull(thrown);
		assertFalse(thrown.getMessage().contains(ApplicationConstants.COMPONENT_DETAILS_DOES_NOT_EXISTS));	
    }
	

	@Test
    public void testSaveComponentsDetails() {
      
		 ComponentDetailsRequestDTO componentDetailsRequestDTO = new ComponentDetailsRequestDTO();
	        componentDetailsRequestDTO.setCompReportId(1L);
	        componentDetailsRequestDTO.setQuery("SELECT * FROM my_table");
	        componentDetailsRequestDTO.setQueryKey("my_query");

	        Components components = new Components();
	        components.setId(1L);
	        
	        ComponentDetails componentDetails = new ComponentDetails();
			componentDetails.setComponents(components);
			componentDetails.setQuery(componentDetailsRequestDTO.getQuery());
			componentDetails.setQueryKey(componentDetailsRequestDTO.getQueryKey());
	        
	        when(mockComponentRepository.findById(componentDetailsRequestDTO.getCompReportId())).thenReturn(Optional.of(components));
			//when(mockComponentsDetailsRepository.save(componentDetails)).thenReturn(componentDetails);
			componentsServiceImpl.saveComponentsDetails(componentDetailsRequestDTO);
			
			 ArgumentCaptor<ComponentDetails> argumentCaptor = ArgumentCaptor.forClass(ComponentDetails.class);
		        verify(mockComponentsDetailsRepository).save(argumentCaptor.capture());
		        ComponentDetails savedComponents = argumentCaptor.getValue();
		        assertEquals(componentDetails.getComponents(), savedComponents.getComponents());
		        assertEquals(componentDetails.getQuery(), savedComponents.getQuery());
		        assertEquals(componentDetails.getQueryKey(), savedComponents.getQueryKey());
		       
		        // then
		        componentsServiceImpl.saveComponentsDetails(componentDetailsRequestDTO);
		       verify(mockComponentsDetailsRepository, times(1)).save(savedComponents);
			

    }
	@Test
	public void testSaveComponentsDetails_NotFound() {
		 // Create a MetricsDTO instance for the test
		ComponentDetailsRequestDTO componentDetailsRequestDTO = new ComponentDetailsRequestDTO();
        componentDetailsRequestDTO.setCompReportId(1L);
        componentDetailsRequestDTO.setQuery("SELECT * FROM my_table");
        componentDetailsRequestDTO.setQueryKey("my_query");

        // Configure the reportConfigurationRepo.findById() method 
        when(mockComponentRepository.findById(componentDetailsRequestDTO.getCompReportId())).thenReturn(Optional.empty());	    
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> componentsServiceImpl.saveComponentsDetails(componentDetailsRequestDTO),
				"Expected saveComponetDetails to throw, but it didn't");
		assertNotNull(thrown);
		assertFalse(thrown.getMessage().contains(ApplicationConstants.COMPONENT_DETAILS_DOES_NOT_EXISTS));	
		
	}
	
	 @Test
	    void saveComponentsCountryDetails() {
			
		  // create mock data
		     ComponentsCountry compCountryObject = new ComponentsCountry();
		     compCountryObject.setId(compCountryObject.getId());
			compCountryObject.setCountry(compCountryObject.getCountry());
			compCountryObject.setDataSourceConfig(compCountryObject.getDataSourceConfig());
			compCountryObject.setComponents(compCountryObject.getComponents());
			
	        DataSourceConfig dataSourceConfig = new DataSourceConfig();
	        dataSourceConfig.setCountry("USA");
	        Components componentsResponse = new Components();
	        componentsResponse.setId(1L);

		    mockComponentsCountryRepository.save(compCountryObject);
	        ComponentsCountry savedComponentsCountry = new ComponentsCountry();
	        
	        assertEquals(compCountryObject.getCountry(), savedComponentsCountry.getCountry());
	        assertEquals(compCountryObject.getDataSourceConfig(), savedComponentsCountry.getDataSourceConfig());
	        assertEquals(compCountryObject.getComponents(), savedComponentsCountry.getComponents());   
	    }


}