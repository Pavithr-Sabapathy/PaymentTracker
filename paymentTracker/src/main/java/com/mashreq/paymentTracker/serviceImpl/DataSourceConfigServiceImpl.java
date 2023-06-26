package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.DataSourceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.service.DataSourceConfigService;
import com.mashreq.paymentTracker.utility.AesUtil;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@Component
public class DataSourceConfigServiceImpl implements DataSourceConfigService {

	private static final Logger log = LoggerFactory.getLogger(DataSourceConfigServiceImpl.class);
	private static final String FILENAME = "DataSourceConfigServiceImpl";

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	private DataSourceRepository dataSourceConfigRepository;

	@Override
	public DataSource saveDataSourceConfiguration(DataSourceDTO dataSourceRequest) {
		DataSource dataSourceResponse = new DataSource();
		/** Encrypt the password and save into Database **/
		AesUtil aesUtil = new AesUtil();
		if (null != dataSourceRequest.getPassword()) {
			String password = dataSourceRequest.getPassword();
			String encryptedPassword = aesUtil.encrypt(password);
			dataSourceRequest.setEncryptedPassword("Y");
			dataSourceRequest.setPassword(encryptedPassword);
			dataSourceResponse = modelMapper.map(dataSourceRequest, DataSource.class);
		}
		dataSourceResponse = dataSourceConfigRepository.save(dataSourceResponse);
		return dataSourceResponse;
	}

	@Override
	public DataSourceDTO getDataSourceConfigById(long dataSourceId) {
		DataSourceDTO dataSourceDTOResponse = new DataSourceDTO();
		Optional<DataSource> dataSourceConfigurationOptional = dataSourceConfigRepository.findById(dataSourceId);
		if (dataSourceConfigurationOptional.isEmpty()) {
			log.error(FILENAME + "[getDataSourceConfigById] " + ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS
					+ dataSourceId);
			throw new ResourceNotFoundException(ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + dataSourceId);
		} else {
			DataSource dataSourceConfigResponse = dataSourceConfigurationOptional.get();
			dataSourceDTOResponse = modelMapper.map(dataSourceConfigResponse, DataSourceDTO.class);
			log.info(FILENAME + "[getDataSourceConfigById] " + dataSourceDTOResponse.toString());
		}
		return dataSourceDTOResponse;
	}

	@Override
	public void deleteDataSourceById(long dataSourceId) {
		if (dataSourceConfigRepository.existsById(dataSourceId)) {
			dataSourceConfigRepository.deleteById(dataSourceId);
		} else {
			log.error(FILENAME + "[deleteDataSourceConfigById] "
					+ ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + dataSourceId);

			throw new ResourceNotFoundException(ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + dataSourceId);
		}
	}

	@Override
	public List<DataSource> allDataSourceConfig() {
		List<DataSource> dataSourceConfigurationResponse = dataSourceConfigRepository.findAll();
		return dataSourceConfigurationResponse;
	}

	@Override
	public List<DataSourceDTO> allActiveDataSource() {
		List<DataSourceDTO> dataSourceDTOList = new ArrayList<DataSourceDTO>();
		String activeStatus = "Y";
		List<DataSource> dataSourceList = dataSourceConfigRepository.findByActive(activeStatus);
		if (!dataSourceList.isEmpty()) {
			dataSourceList.stream().forEach(datasource -> {
				AesUtil aesUtil = new AesUtil();
				DataSourceDTO dataSourceMapper = modelMapper.map(datasource, DataSourceDTO.class);
				if (dataSourceMapper.getEncryptedPassword().equals("Y")) {
					String decryptPassword = aesUtil.decrypt(dataSourceMapper.getPassword());
					dataSourceMapper.setPassword(decryptPassword);
				}
				dataSourceDTOList.add(dataSourceMapper);
			});

			log.info(FILENAME + "[getDataSourceConfigById] " + dataSourceDTOList.toString());
		}
		return dataSourceDTOList;
	}

	@Override
	public DataSource updateDataSourceById(@Valid DataSourceDTO dataSourceRequest, Long datasourceId) {
		AesUtil aesUtil = new AesUtil();
		DataSource dataSourceResponse = new DataSource();
		Optional<DataSource> dataSourceOptional = dataSourceConfigRepository.findById(datasourceId);
		if (dataSourceOptional.isEmpty()) {
			log.error(FILENAME + "[updateDataSourceConfigById] "
					+ ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + datasourceId);

			throw new ResourceNotFoundException(ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + datasourceId);
		}

		if (null != dataSourceRequest.getPassword()) {
			String password = dataSourceRequest.getPassword();
			String encryptedPassword = aesUtil.encrypt(password);
			dataSourceRequest.setEncryptedPassword("Y");
			dataSourceRequest.setPassword(encryptedPassword);
			dataSourceResponse = modelMapper.map(dataSourceRequest, DataSource.class);
		}
		dataSourceResponse = dataSourceConfigRepository.save(dataSourceResponse);
		return dataSourceResponse;
	}

}