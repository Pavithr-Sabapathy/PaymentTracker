package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
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
}