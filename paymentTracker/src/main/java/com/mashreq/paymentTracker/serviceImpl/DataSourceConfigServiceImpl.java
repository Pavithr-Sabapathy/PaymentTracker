package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.dto.DataSourceDTO;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.service.DataSourceConfigService;
import com.mashreq.paymentTracker.utility.AesUtil;

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
			AesUtil aesUtil = new AesUtil();
			if (dataSourceDTOResponse.getEncryptedPassword().equals("Y")) {
				String decryptPassword = aesUtil.decrypt(dataSourceDTOResponse.getPassword());
				dataSourceDTOResponse.setPassword(decryptPassword);
			}
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
	public Map<String, Object> allActiveDataSource(int page, int size, List<String> sort) {
		Map<String, Object> response = new HashMap<>();
		List<DataSourceDTO> dataSourceDTOList = new ArrayList<DataSourceDTO>();
		String activeStatus = "Y";
		List<Order> orders = new ArrayList<Order>();

		if (sort.get(0).contains(",")) {
			// will sort more than 2 fields
			// sortOrder="field, direction"
			for (String sortOrder : sort) {
				String[] _sort = sortOrder.split(",");
				orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
			}
		} else {
			// sort=[field, direction]
			orders.add(new Order(getSortDirection(sort.get(1)), sort.get(0)));
		}

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
		Page<DataSource> dataSourceList = dataSourceConfigRepository.findByActiveContaining(activeStatus, pagingSort);
		if (!dataSourceList.isEmpty()) {

			dataSourceList.stream().forEach(datasource -> {
				DataSourceDTO dataSourceMapper = modelMapper.map(datasource, DataSourceDTO.class);
				dataSourceDTOList.add(dataSourceMapper);
			});
			response.put("dataSource", dataSourceDTOList);
			response.put("currentPage", dataSourceList.getNumber());
			response.put("totalItems", dataSourceList.getTotalElements());
			response.put("totalPages", dataSourceList.getTotalPages());
			log.info(FILENAME + "[getDataSourceConfigById] " + response.toString());
		}
		return response;
	}

	private Direction getSortDirection(String _sort) {
		Sort.Direction dire = _sort.contains("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		return dire;
	}

	@Override
	public DataSource updateDataSourceById(DataSourceDTO dataSourceRequest, Long datasourceId) {
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