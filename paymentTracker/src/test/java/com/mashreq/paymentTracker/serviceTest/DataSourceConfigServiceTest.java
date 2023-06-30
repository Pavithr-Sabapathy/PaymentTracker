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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mashreq.paymentTracker.dto.DataSourceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.serviceImpl.DataSourceConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
public class DataSourceConfigServiceTest {

	@InjectMocks
	DataSourceConfigServiceImpl dataSourceConfigService;

	@Mock
	DataSourceRepository mockdataSourceConfigRepository;

	@Test
	public void testSaveDataSourceConfig() throws Exception {
		DataSourceDTO mockDataSourceDTO = new DataSourceDTO("sample", "oracle", 1L, "Oracle", "ReadOnly", "12345",
				"@!@#234", 1L, "123.13.34.56", "PT", "y");
		DataSource mockdataSourceConfigValue = new DataSource(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");
		when(mockdataSourceConfigRepository.save(any(DataSource.class))).thenReturn(mockdataSourceConfigValue);
		DataSource dataSourceConfiguration = dataSourceConfigService.saveDataSourceConfiguration(mockDataSourceDTO);
		assertEquals(dataSourceConfiguration.getName(), "sample");
		verify(mockdataSourceConfigRepository, times(1)).save(mockdataSourceConfigValue);
	}

	@Test
	public void testGetDataSourceConfigById() {
		long dataSourceId = 1L;
		DataSource mockdataSourceConfigValue = new DataSource(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");
		when(mockdataSourceConfigRepository.findById(dataSourceId)).thenReturn(Optional.of(mockdataSourceConfigValue));
		DataSourceDTO dataSourceConfiguration = dataSourceConfigService.getDataSourceConfigById(dataSourceId);
		assertEquals(dataSourceConfiguration.getName(), "sample");
		verify(mockdataSourceConfigRepository, times(1)).findById(dataSourceId);
	}

	@Test
	public void testGetDataSourceConfigByIdNotExists() throws ResourceNotFoundException {
		long dataSourceId = 1L;
		DataSource mockdataSourceConfigValue = null;
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
		DataSourceDTO mockDataSourceDTO = new DataSourceDTO("sample", "oracle", 1L, "Oracle", "ReadOnly", "12345",
				"@!@#234", 1L, "123.13.34.56", "PT", "y");

		DataSource mockdataSourceConfigValue = new DataSource(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");
		;
		when(mockdataSourceConfigRepository.findById(dataSourceId)).thenReturn(Optional.of(mockdataSourceConfigValue));
		dataSourceConfigService.updateDataSourceById(mockDataSourceDTO,1L);
		verify(mockdataSourceConfigRepository, times(1)).findById(dataSourceId);
	}

	@Test
	public void testUpdateDataSourceConfigByIdNotExists() throws ResourceNotFoundException {
		long dataSourceId = 1L;
		DataSourceDTO mockDataSourceDTO = new DataSourceDTO("sample", "oracle", 1L, "Oracle", "ReadOnly", "12345",
				"@!@#234", 1L, "123.13.34.56", "PT", "y");

		DataSource mockdataSourceConfigValue = new DataSource(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");

		when(mockdataSourceConfigRepository.findById(dataSourceId)).thenReturn(Optional.empty());
		// .thenReturn(Optional.ofNullable(mockdataSourceConfigValue));
		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> dataSourceConfigService.updateDataSourceById(mockDataSourceDTO,1L),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("DataSource Configuration not exist with this id :1"));
	}

	@Test
	public void testAllDataSourceConfig() {
		List<DataSource> mockDataSourceConfigList = new ArrayList<DataSource>();
		DataSource dataSourceConfigValue1 = new DataSource(1L, "Oracle", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");
		DataSource dataSourceConfigValue2 = new DataSource(1L, "FLEX", "FLEX_UAT", BigInteger.ZERO, "Oracle",
				"ReadOnly", "6789", "@3$45", BigInteger.ZERO, "101.20.12.43", "RS", "y", "UAE");
		DataSource dataSourceConfigValue3 = new DataSource(1L, "Oracle", "null", BigInteger.ZERO, "Oracle", "ReadOnly",
				"34567", "@!()*$5", BigInteger.ZERO, "123.13.34.56", "sample", "y", "UAE");

		mockDataSourceConfigList.add(dataSourceConfigValue1);
		mockDataSourceConfigList.add(dataSourceConfigValue2);
		mockDataSourceConfigList.add(dataSourceConfigValue3);

		when(mockdataSourceConfigRepository.findAll()).thenReturn(mockDataSourceConfigList);

		// test
		Map<String, Object> dataSourceConfig = dataSourceConfigService.allDataSourceConfig(0, 0, null);

		assertEquals(3, dataSourceConfig.size());
		verify(mockdataSourceConfigRepository, times(1)).findAll();
	}

	@Test
	public void testDeleteDataSourceConfigById() {
		long dataSourceId = 1L;

		when(mockdataSourceConfigRepository.existsById(dataSourceId)).thenReturn(true);
		doNothing().when(mockdataSourceConfigRepository).deleteById(dataSourceId);

		dataSourceConfigService.deleteDataSourceById(dataSourceId);

		verify(mockdataSourceConfigRepository).existsById(1L);
		verify(mockdataSourceConfigRepository).deleteById(1L);
	}

	@Test
	void testDeleteDataSourceConfigByIdNotExists() throws ResourceNotFoundException {
		long dataSourceId = 1L;
		when(mockdataSourceConfigRepository.existsById(dataSourceId)).thenReturn(false);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> dataSourceConfigService.deleteDataSourceById(dataSourceId),
				"Expected dataSourceConfigService.deleteDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
		assertTrue(thrown.getMessage().contains("DataSource Configuration not exist with this id :1"));
	}
}