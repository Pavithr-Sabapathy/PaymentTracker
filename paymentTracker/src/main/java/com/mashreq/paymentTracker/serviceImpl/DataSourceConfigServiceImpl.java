package com.mashreq.paymentTracker.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.repository.DataSourceConfigRepository;
import com.mashreq.paymentTracker.service.DataSourceConfigService;

@Component
public class DataSourceConfigServiceImpl implements DataSourceConfigService {

	private static final Logger log = LoggerFactory.getLogger(DataSourceConfigServiceImpl.class);
	private static final String FILENAME = "DataSourceConfigServiceImpl";

	@Autowired
	private DataSourceConfigRepository dataSourceConfigRepository;

	@Override
	public DataSource saveDataSourceConfiguration(DataSource dataSourceConfigurationRequest)
			throws Exception {
		DataSource dataSourceResponse = new DataSource();
		try {
			/**how to handle password encrypted and active column**/
			dataSourceResponse = dataSourceConfigRepository.save(dataSourceConfigurationRequest);
		} catch (Exception exception) {
			throw new Exception("Exception throw at [saveDataSourceConfiguration]-->" + exception);

		}
		return dataSourceResponse;
	}

	@Override
	public DataSource getDataSourceConfigById(long dataSourceId) {
		DataSource dataSourceConfigResponse = new DataSource();
		Optional<DataSource> dataSourceConfigurationOptional = dataSourceConfigRepository.findById(dataSourceId);
		if (dataSourceConfigurationOptional.isEmpty()) {
			log.error(FILENAME + "[getDataSourceConfigById] " + ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS
					+ dataSourceId);
			throw new ResourceNotFoundException(ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + dataSourceId);
		} else {
			dataSourceConfigResponse = dataSourceConfigurationOptional.get();
		}
		return dataSourceConfigResponse;
	}

	@Override
	public void deleteDataSourceConfigById(long dataSourceId) {
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
	public void updateDataSourceConfigById(DataSource dataSourceupdateRequest) {
		Optional<DataSource> dataSourceOptional = dataSourceConfigRepository
				.findById(dataSourceupdateRequest.getId());
		if (dataSourceOptional.isEmpty()) {
			log.error(FILENAME + "[updateDataSourceConfigById] "
					+ ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + dataSourceupdateRequest.getId());

			throw new ResourceNotFoundException(
					ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + dataSourceupdateRequest.getId());
		}
		dataSourceConfigRepository.save(dataSourceupdateRequest);
	}

}