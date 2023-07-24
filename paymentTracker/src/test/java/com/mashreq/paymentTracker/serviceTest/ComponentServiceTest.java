package com.mashreq.paymentTracker.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dao.ComponentDetailsDAO;
import com.mashreq.paymentTracker.dao.ComponentsCountryDAO;
import com.mashreq.paymentTracker.dao.ComponentsDAO;
import com.mashreq.paymentTracker.dao.ReportDAO;
import com.mashreq.paymentTracker.dto.ComponentDetailsRequestDTO;
import com.mashreq.paymentTracker.dto.ComponentsRequestDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentDetails;
import com.mashreq.paymentTracker.model.Components;
import com.mashreq.paymentTracker.model.ComponentsCountry;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.model.Report;
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.serviceImpl.ComponentsServiceImpl;

@ExtendWith(SpringExtension.class)
public class ComponentServiceTest {

	@InjectMocks
	ComponentsServiceImpl componentsServiceImpl;

	@Mock
	private Components compResponse;

	@Mock
	private ComponentsDAO componentsDAO;

	@Mock
	private ComponentsCountryDAO componentsCountryDAO;

	@Mock
	private ComponentDetailsDAO componentDetailsDAO;

	@Mock
	DataSourceRepository dataSourceRepo;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	ReportDAO reportDAO;

	@Test
	public void testdeleteComponentById() {
		long componentId = 1L;

		doNothing().when(componentsDAO).deleteById(componentId);

		componentsServiceImpl.deleteComponentById(componentId);

		verify(componentsDAO).deleteById(1L);
	}

	@Test
	public void testdeleteComponentDetailsById() {
		long componentId = 1L;

		doNothing().when(componentDetailsDAO).deleteById(componentId);
		componentsServiceImpl.deleteComponentDetailsById(componentId);
		verify(componentDetailsDAO).deleteById(1L);
	}

	@Test
	public void testSaveComponent() throws ResourceNotFoundException {
		ComponentsRequestDTO componentsRequest = new ComponentsRequestDTO();
		componentsRequest.setReportId(1L);
		componentsRequest.setActive("active");
		componentsRequest.setComponentKey("test-component");
		componentsRequest.setComponentName("Test Component");
		componentsRequest.setDataSourceId(1L);

		Report report = new Report();
		report.setId(1L);

		DataSource dataSourceConfig = new DataSource();
		dataSourceConfig.setId(1L);

		// when
		when(reportDAO.getReportById(1L)).thenReturn(report);

		when(dataSourceRepo.getDataSourceById(1L)).thenReturn(dataSourceConfig);
		
		componentsServiceImpl.saveComponents(componentsRequest);

		ArgumentCaptor<Components> argumentCaptor = ArgumentCaptor.forClass(Components.class);
		verify(componentsDAO).save(argumentCaptor.capture());
		Components savedComponents = argumentCaptor.getValue();
		assertEquals(componentsRequest.getComponentName(), savedComponents.getComponentName());
		assertEquals(componentsRequest.getComponentKey(), savedComponents.getComponentKey());
		assertEquals(componentsRequest.getActive(), savedComponents.getActive());
		assertEquals(report, savedComponents.getReport());

		// then
		componentsServiceImpl.saveComponents(componentsRequest);
		verify(componentsDAO, times(1)).save(savedComponents);
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
		when(reportDAO.getReportById(componentsRequest.getReportId())).thenReturn(null);
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

		when(componentsDAO.findById(componentDetailsRequestDTO.getCompReportId())).thenReturn(components);
		// when(mockComponentsDetailsRepository.save(componentDetails)).thenReturn(componentDetails);
		componentsServiceImpl.saveComponentsDetails(componentDetailsRequestDTO);

		ArgumentCaptor<ComponentDetails> argumentCaptor = ArgumentCaptor.forClass(ComponentDetails.class);
		verify(componentDetailsDAO).save(argumentCaptor.capture());
		ComponentDetails savedComponents = argumentCaptor.getValue();
		assertEquals(componentDetails.getComponents(), savedComponents.getComponents());
		assertEquals(componentDetails.getQuery(), savedComponents.getQuery());
		assertEquals(componentDetails.getQueryKey(), savedComponents.getQueryKey());

		// then
		componentsServiceImpl.saveComponentsDetails(componentDetailsRequestDTO);
		verify(componentDetailsDAO, times(1)).save(savedComponents);

	}

	@Test
	public void testSaveComponentsDetails_NotFound() {
		// Create a MetricsDTO instance for the test
		ComponentDetailsRequestDTO componentDetailsRequestDTO = new ComponentDetailsRequestDTO();
		componentDetailsRequestDTO.setCompReportId(1L);
		componentDetailsRequestDTO.setQuery("SELECT * FROM my_table");
		componentDetailsRequestDTO.setQueryKey("my_query");

		// Configure the reportConfigurationRepo.findById() method
		when(componentsDAO.findById(componentDetailsRequestDTO.getCompReportId())).thenReturn(null);
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> componentsServiceImpl.saveComponentsDetails(componentDetailsRequestDTO),
				"Component not exist with this id :");
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

		DataSource dataSourceConfig = new DataSource();
		dataSourceConfig.setCountry("USA");
		Components componentsResponse = new Components();
		componentsResponse.setId(1L);

		componentsCountryDAO.save(compCountryObject);
		ComponentsCountry savedComponentsCountry = new ComponentsCountry();

		assertEquals(compCountryObject.getCountry(), savedComponentsCountry.getCountry());
		assertEquals(compCountryObject.getDataSourceConfig(), savedComponentsCountry.getDataSourceConfig());
		assertEquals(compCountryObject.getComponents(), savedComponentsCountry.getComponents());
	}

}