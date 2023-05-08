package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.DataSourceConfig;
import com.mashreq.paymentTracker.repository.DataSourceConfigRepository;
import com.mashreq.paymentTracker.serviceImpl.DataSourceConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
public class DataSourceConfigServiceTest {

	@InjectMocks
	DataSourceConfigServiceImpl dataSourceConfigService;

	@Mock
	DataSourceConfigRepository mockdataSourceConfigRepository;

	@Test
	public void testSaveDataSourceConfig() throws Exception {
		DataSourceConfig mockdataSourceConfigValue = new DataSourceConfig(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");
		when(mockdataSourceConfigRepository.save(mockdataSourceConfigValue)).thenReturn(mockdataSourceConfigValue);
		DataSourceConfig dataSourceConfiguration = dataSourceConfigService
				.saveDataSourceConfiguration(mockdataSourceConfigValue);
		assertEquals(dataSourceConfiguration.getDataSourceName(), "sample");
		verify(mockdataSourceConfigRepository, times(1)).save(mockdataSourceConfigValue);
	}

	@Test
	public void testGetDataSourceConfigById() {
		long dataSourceId = 1L;
		DataSourceConfig mockdataSourceConfigValue = new DataSourceConfig(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");
		when(mockdataSourceConfigRepository.findById(dataSourceId)).thenReturn(Optional.of(mockdataSourceConfigValue));
		DataSourceConfig dataSourceConfiguration = dataSourceConfigService.getDataSourceConfigById(dataSourceId);
		assertEquals(dataSourceConfiguration.getDataSourceName(), "sample");
		verify(mockdataSourceConfigRepository, times(1)).findById(dataSourceId);
	}

	@Test
	public void testGetDataSourceConfigByIdNotExists() throws ResourceNotFoundException {
		long dataSourceId = 1L;
		DataSourceConfig mockdataSourceConfigValue = null;
		when(mockdataSourceConfigRepository.findById(dataSourceId))
				.thenReturn(Optional.ofNullable(mockdataSourceConfigValue));

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> dataSourceConfigService.getDataSourceConfigById(dataSourceId),
				"Expected dataSourceConfigService.getDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("DataSource Configuration not exist with this id :1"));

	}

	@Test
	public void testUpdateDataSourceConfigById() {
		long dataSourceId = 1L;
		DataSourceConfig mockdataSourceConfigValue = new DataSourceConfig(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");;
		when(mockdataSourceConfigRepository.findById(dataSourceId)).thenReturn(Optional.of(mockdataSourceConfigValue));
		dataSourceConfigService.updateDataSourceConfigById(mockdataSourceConfigValue);
		verify(mockdataSourceConfigRepository, times(1)).findById(dataSourceId);
	}

	@Test
	public void testUpdateDataSourceConfigByIdNotExists() throws ResourceNotFoundException {
		long dataSourceId = 1L;
		//DataSourceConfig mockdataSourceConfigValue = null;
		DataSourceConfig mockdataSourceConfigValue = new DataSourceConfig(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");
		
		
		when(mockdataSourceConfigRepository.findById(dataSourceId)).thenReturn(Optional.empty());
				//.thenReturn(Optional.ofNullable(mockdataSourceConfigValue));
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> dataSourceConfigService.updateDataSourceConfigById(mockdataSourceConfigValue),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("DataSource Configuration not exist with this id :1"));
	}

	@Test
	public void testAllDataSourceConfig() {
		List<DataSourceConfig> mockDataSourceConfigList = new ArrayList<DataSourceConfig>();
		DataSourceConfig dataSourceConfigValue1 = new DataSourceConfig(1L, "Oracle", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y","UAE");
		DataSourceConfig dataSourceConfigValue2 = new DataSourceConfig(1L, "FLEX", "FLEX_UAT", BigInteger.ZERO, "Oracle",
				"ReadOnly", "6789", "@3$45", BigInteger.ZERO, "101.20.12.43", "RS", "y","UAE");
		DataSourceConfig dataSourceConfigValue3 = new DataSourceConfig(1L, "Oracle", "null", BigInteger.ZERO, "Oracle",
				"ReadOnly", "34567", "@!()*$5", BigInteger.ZERO, "123.13.34.56", "sample", "y","UAE");

		mockDataSourceConfigList.add(dataSourceConfigValue1);
		mockDataSourceConfigList.add(dataSourceConfigValue2);
		mockDataSourceConfigList.add(dataSourceConfigValue3);

		when(mockdataSourceConfigRepository.findAll()).thenReturn(mockDataSourceConfigList);

		// test
		List<DataSourceConfig> dataSourceConfig = dataSourceConfigService.allDataSourceConfig();

		assertEquals(3, dataSourceConfig.size());
		verify(mockdataSourceConfigRepository, times(1)).findAll();
	}

	@Test
	public void testDeleteDataSourceConfigById() {
		long dataSourceId = 1L;

		when(mockdataSourceConfigRepository.existsById(dataSourceId)).thenReturn(true);
		doNothing().when(mockdataSourceConfigRepository).deleteById(dataSourceId);

		dataSourceConfigService.deleteDataSourceConfigById(dataSourceId);

		verify(mockdataSourceConfigRepository).existsById(1L);
		verify(mockdataSourceConfigRepository).deleteById(1L);
	}

	@Test
	void testDeleteDataSourceConfigByIdNotExists() throws ResourceNotFoundException {
		long dataSourceId = 1L;
		when(mockdataSourceConfigRepository.existsById(dataSourceId)).thenReturn(false);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> dataSourceConfigService.deleteDataSourceConfigById(dataSourceId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("DataSource Configuration not exist with this id :1"));
	}
}