package com.mashreq.paymentTracker.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.mashreq.paymentTracker.dto.DataSourceRequestDTO;
import com.mashreq.paymentTracker.dto.DataSourceResponseDTO;
import com.mashreq.paymentTracker.exception.CryptographyException;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.service.DataSourceConfigService;
import com.mashreq.paymentTracker.type.EncryptionAlgorithm;
import com.mashreq.paymentTracker.utility.AesUtil;

@Component
public class DataSourceConfigServiceImpl implements DataSourceConfigService {

	private static final Logger log = LoggerFactory.getLogger(DataSourceConfigServiceImpl.class);
	private static final String FILENAME = "DataSourceConfigServiceImpl";

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	DataSourceRepository dataSourceRepo;

	@Override
	public DataSourceResponseDTO saveDataSourceConfiguration(DataSourceRequestDTO dataSourceRequest) {
		DataSource dataSource = new DataSource();
		DataSourceResponseDTO dataSourceResponse = new DataSourceResponseDTO();
		/** Encrypt the password and save into Database **/
		if (null != dataSourceRequest.getPassword()) {
			String password = dataSourceRequest.getPassword();
			String encryptedPassword = null;
			try {
				encryptedPassword = AesUtil.encryptBase64(password, "execueDatasourceConnection",
						EncryptionAlgorithm.TRIPLE_DES);
			} catch (CryptographyException e) {
				e.printStackTrace();
			}
			dataSourceRequest.setEncryptedPassword("Y");
			dataSourceRequest.setPassword(encryptedPassword);
			dataSource = modelMapper.map(dataSourceRequest, DataSource.class);
		}
		dataSource = dataSourceRepo.save(dataSource);
		dataSourceResponse = modelMapper.map(dataSource, DataSourceResponseDTO.class);
		return dataSourceResponse;
	}

	@Override
	public DataSourceResponseDTO getDataSourceConfigById(long dataSourceId) {
		DataSourceResponseDTO dataSourceDTOResponse = new DataSourceResponseDTO();
		DataSource dataSource = dataSourceRepo.getDataSourceById(dataSourceId);
		if (null == dataSource) {
			log.error(FILENAME + "[getDataSourceConfigById] " + ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS
					+ dataSourceId);
			throw new ResourceNotFoundException(ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + dataSourceId);
		}
		dataSourceDTOResponse = modelMapper.map(dataSource, DataSourceResponseDTO.class);
		if (dataSourceDTOResponse.getEncryptedPassword().equals("Y")) {
			String decryptedPassword = null;
			try {
				decryptedPassword = AesUtil.decryptBase64(dataSourceDTOResponse.getPassword(),
						"execueDatasourceConnection", EncryptionAlgorithm.TRIPLE_DES);
			} catch (CryptographyException e) {
				e.printStackTrace();
			}
			dataSourceDTOResponse.setPassword(decryptedPassword);
		}
		log.info(FILENAME + "[getDataSourceConfigById] " + dataSourceDTOResponse.toString());
		return dataSourceDTOResponse;
	}

	@Override
	public void deleteDataSourceById(long dataSourceId) {
		dataSourceRepo.deleteById(dataSourceId);
	}

	@Override
	public Map<String, Object> allDataSourceConfig(int page, int size, List<String> sort) {
		List<DataSourceResponseDTO> dataSourceDTOList = new ArrayList<DataSourceResponseDTO>();
		List<Order> orders = new ArrayList<Order>();
		Map<String, Object> response = new HashMap<>();
		if (sort.get(0).contains(",")) {
			for (String sortOrder : sort) {
				String[] _sort = sortOrder.split(",");
				orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
			}
		} else {
			orders.add(new Order(getSortDirection(sort.get(1)), sort.get(0)));
		}

		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
		Page<DataSource> dataSourceList = dataSourceRepo.findAll(pagingSort);
		if (!dataSourceList.isEmpty()) {
			dataSourceList.stream().forEach(datasource -> {
				DataSourceResponseDTO dataSourceMapper = modelMapper.map(datasource, DataSourceResponseDTO.class);
				if (dataSourceMapper.getEncryptedPassword().equals("Y")) {
					String decryptedPassword;
					try {
						decryptedPassword = AesUtil.decryptBase64(dataSourceMapper.getPassword(),
								"execueDatasourceConnection", EncryptionAlgorithm.TRIPLE_DES);
						dataSourceMapper.setPassword(decryptedPassword);
					} catch (CryptographyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
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

	@Override
	public Map<String, Object> allActiveDataSource(int page, int size, List<String> sort) {
		Map<String, Object> response = new HashMap<>();
		List<DataSourceResponseDTO> dataSourceDTOList = new ArrayList<DataSourceResponseDTO>();
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
		Page<DataSource> dataSourceList = dataSourceRepo.findByActiveContaining(activeStatus, pagingSort);
		if (!dataSourceList.isEmpty()) {

			dataSourceList.stream().forEach(datasource -> {
				DataSourceResponseDTO dataSourceMapper = modelMapper.map(datasource, DataSourceResponseDTO.class);
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
	public DataSourceResponseDTO updateDataSourceById(DataSourceRequestDTO dataSourceRequest, Long datasourceId) {
		DataSource dataSourceResponse = new DataSource();
		DataSourceResponseDTO dataSourceResponseDTO = new DataSourceResponseDTO();
		DataSource dataSource = dataSourceRepo.getDataSourceById(datasourceId);
		if (null == dataSource) {
			log.error(FILENAME + "[updateDataSourceConfigById] "
					+ ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + datasourceId);

			throw new ResourceNotFoundException(ApplicationConstants.DATA_SOURCE_CONFIG_DOES_NOT_EXISTS + datasourceId);
		}

		if (null != dataSourceRequest.getPassword()) {
			String password = dataSourceRequest.getPassword();
			String encryptedPassword = null;
			try {
				encryptedPassword = AesUtil.decryptBase64(password, "execueDatasourceConnection",
						EncryptionAlgorithm.TRIPLE_DES);
			} catch (CryptographyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataSourceRequest.setEncryptedPassword("Y");
			dataSourceRequest.setPassword(encryptedPassword);
			dataSourceResponse = modelMapper.map(dataSourceRequest, DataSource.class);
			dataSourceResponse.setId(datasourceId);
		}
		dataSourceResponse = dataSourceRepo.update(dataSourceResponse);
		dataSourceResponseDTO = modelMapper.map(dataSourceResponse, DataSourceResponseDTO.class);
		return dataSourceResponseDTO;
	}

}