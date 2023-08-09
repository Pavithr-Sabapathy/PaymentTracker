package com.mashreq.paymentTracker.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mashreq.paymentTracker.dto.DataSourceRequestDTO;
import com.mashreq.paymentTracker.dto.DataSourceResponseDTO;
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

	@Mock
	private ModelMapper modelMapper;

	@Test
    public void testSaveDataSourceConfiguration() throws Exception {
		DataSourceRequestDTO dataSourceRequest = new DataSourceRequestDTO("sample", "oracle", 1L, "Oracle", "ReadOnly",
				"12345", "@!@#234", 1L, "123.13.34.56", "PT", "y");


        DataSource dataSource = new DataSource(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "Y", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");

        DataSourceResponseDTO expectedResponse = new DataSourceResponseDTO();
        expectedResponse.setName("sample");
        expectedResponse.setEncryptedPassword("Y");
        expectedResponse.setPassword("encrypted_password_here");

        when(modelMapper.map(dataSourceRequest, DataSource.class)).thenReturn(dataSource);
        when(mockdataSourceConfigRepository.save(dataSource)).thenReturn(dataSource);
        when(modelMapper.map(dataSource, DataSourceResponseDTO.class)).thenReturn(expectedResponse);

        DataSourceResponseDTO result = dataSourceConfigService.saveDataSourceConfiguration(dataSourceRequest);

        assertNotNull(result);
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getPassword(), result.getPassword());
    }

	@Test
    public void testAllActiveDataSource() {
        int page = 0;
        int size = 20;
        List<String> sort = Arrays.asList("name,asc");

        DataSource dataSource = new DataSource(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "Y", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");

        DataSourceResponseDTO expectedResponse = new DataSourceResponseDTO();
        expectedResponse.setName("sample");
        expectedResponse.setActive("Y");

        Page<DataSource> dataSourcePage = new PageImpl<>(Collections.singletonList(dataSource));

        when(mockdataSourceConfigRepository.findByActiveContaining(eq("Y"), any(Pageable.class))).thenReturn(dataSourcePage);
        when(modelMapper.map(dataSource, DataSourceResponseDTO.class)).thenReturn(expectedResponse);

        Map<String, Object> result = dataSourceConfigService.allActiveDataSource(page, size, sort);

        assertNotNull(result);       
    }
	
	@Test
    public void testGetDataSourceConfigById() throws Exception {
        long dataSourceId = 123L;

        DataSource dataSource = new DataSource(1L, "sample", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "Y", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");
        
        DataSourceResponseDTO expectedResponse = new DataSourceResponseDTO();
        expectedResponse.setName("sample");
        expectedResponse.setEncryptedPassword("Y");
        
        when(mockdataSourceConfigRepository.getDataSourceById(dataSourceId)).thenReturn(dataSource);
        when(modelMapper.map(dataSource, DataSourceResponseDTO.class)).thenReturn(expectedResponse);

        DataSourceResponseDTO result = dataSourceConfigService.getDataSourceConfigById(dataSourceId);

        assertNotNull(result);
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getPassword(), result.getPassword());
    }

	@Test
	public void testGetDataSourceConfigByIdNotExists() throws ResourceNotFoundException {
		long dataSourceId = 1L;
		when(mockdataSourceConfigRepository.getDataSourceById(dataSourceId))
				.thenReturn(null);

		ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class,
				() -> dataSourceConfigService.getDataSourceConfigById(dataSourceId),
				"Expected dataSourceConfigService.getDataSourceConfigById to throw, but it didn't");
		assertNotNull(thrown);
	}

	
	@Test
    public void testUpdateDataSourceById() {
        Long dataSourceId = 123L;

        DataSourceRequestDTO dataSourceRequest = new DataSourceRequestDTO("sample", "oracle", 1L, "Oracle", "ReadOnly",
				"12345", "@!@#234", 1L, "123.13.34.56", "PT", "y");
        
        DataSource dataSource = new DataSource(1L, "Oracle", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");

        DataSource updatedDataSource = new DataSource();
        updatedDataSource.setId(dataSourceId);

        DataSourceResponseDTO expectedResponse = new DataSourceResponseDTO();

        when(mockdataSourceConfigRepository.getDataSourceById(dataSourceId)).thenReturn(dataSource);
        when(modelMapper.map(dataSourceRequest, DataSource.class)).thenReturn(updatedDataSource);
        when(mockdataSourceConfigRepository.update(updatedDataSource)).thenReturn(updatedDataSource);
        when(modelMapper.map(updatedDataSource, DataSourceResponseDTO.class)).thenReturn(expectedResponse);

        DataSourceResponseDTO result = dataSourceConfigService.updateDataSourceById(dataSourceRequest, dataSourceId);

        assertNotNull(result);
      
    }
	@Test
    public void testUpdateDataSourceConfigByIdNotExists() throws Exception {
		 Long dataSourceId = 123L;

		 DataSourceRequestDTO dataSourceRequest = new DataSourceRequestDTO("sample", "oracle", 1L, "Oracle", "ReadOnly",
					"12345", "@!@#234", 1L, "123.13.34.56", "PT", "y");

	     when(mockdataSourceConfigRepository.getDataSourceById(dataSourceId)).thenReturn(null);

	     assertThrows(ResourceNotFoundException.class, () -> 
	                      dataSourceConfigService.updateDataSourceById(dataSourceRequest, dataSourceId));
	}
	
	@Test
    public void testAllDataSourceConfig() throws Exception {
        int page = 0;
        int size = 20;
        List<String> sort = Arrays.asList("name,asc");

        DataSource dataSource = new DataSource(1L, "Oracle", "oracle", BigInteger.ZERO, "Oracle",
				"ReadOnly", "12345", "@!@#234", BigInteger.ZERO, "123.13.34.56", "PT", "y", "UAE");
        
        DataSourceResponseDTO expectedResponse = new DataSourceResponseDTO();
        expectedResponse.setName("sample");
        expectedResponse.setEncryptedPassword("Y");
        expectedResponse.setPassword("decrypted_password_here"); 
        
        Page<DataSource> dataSourcePage = new PageImpl<>(Collections.singletonList(dataSource));

        when(mockdataSourceConfigRepository.findAll(any(Pageable.class))).thenReturn(dataSourcePage);
        when(modelMapper.map(dataSource, DataSourceResponseDTO.class)).thenReturn(expectedResponse);

        Map<String, Object> result = dataSourceConfigService.allDataSourceConfig(page, size, sort);

        assertNotNull(result);
    }

	@Test
	public void testDeleteDataSourceConfigById() {
		long dataSourceId = 1L;

		doNothing().when(mockdataSourceConfigRepository).deleteById(dataSourceId);

		dataSourceConfigService.deleteDataSourceById(dataSourceId);

		verify(mockdataSourceConfigRepository).deleteById(1L);
	}

}