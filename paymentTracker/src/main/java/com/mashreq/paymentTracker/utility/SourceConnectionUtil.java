package com.mashreq.paymentTracker.utility;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mashreq.paymentTracker.configuration.TrackerConfiguration;
import com.mashreq.paymentTracker.exception.CryptographyException;
import com.mashreq.paymentTracker.exception.DataAccessException;
import com.mashreq.paymentTracker.exception.ExceptionCodes;
import com.mashreq.paymentTracker.model.DataSource;
import com.mashreq.paymentTracker.repository.DataSourceRepository;
import com.mashreq.paymentTracker.type.EncryptionAlgorithm;

import jakarta.annotation.PostConstruct;

public class SourceConnectionUtil {

	private static final Logger log = LoggerFactory.getLogger(SourceConnectionUtil.class);

	@Autowired
	private DataSourceRepository dataSourceConfigRepository;

	@Autowired
	private TrackerConfiguration trackerConfig;

	private static Map<Long, String> connectionMap = new HashMap<Long, String>();
	private static Map<String, PoolingDataSource> dataSourceMap = new HashMap<String, PoolingDataSource>();

	@PostConstruct
	void loadDataSources() throws DataAccessException {
		List<DataSource> dataSources = dataSourceConfigRepository.findAll();

		try {
			setupDataSources(dataSources);
			log.info("Logging Tracker Configuration Values ");
			log.info(trackerConfig.toString());
		} catch (DataAccessException e) {
			log.error("Data souce load exception" + e);
			throw new DataAccessException(ExceptionCodes.SQL_DATASOURCE_EXCEPTION, e);
		}
	}

	public void setupDataSources(List<DataSource> dataSources) throws DataAccessException {
		synchronized (dataSourceMap) {
			// Register the required Database Drivers
			registerDrivers();
			// Setup Connection Pooling Factories for each data source
			for (DataSource dataSource : dataSources) {
				setupDataSource(dataSource);
			}
		}
	}

	private void setupDataSource(DataSource dataSource) {
		PoolingDataSource poolingDataSource = null;
		poolingDataSource = getPooledDataSource(dataSource);
		addDataSource(dataSource.getName(), poolingDataSource);
	}

	private void addDataSource(String name, PoolingDataSource poolingDataSource) {
		SourceConnectionUtil.dataSourceMap.put(name, poolingDataSource);
	}

	public static Connection getConnection(String dataSourceName) throws DataAccessException {
		Connection connection = null;
		PoolingDataSource dataSource = null;
		try {
			dataSource = getDataSourceMap().get(dataSourceName);
			connection = getConnectionFromRepository(dataSource);
		} catch (SQLException sqlException) {
			log.error("Data souce Connection exception" + sqlException);
			throw new DataAccessException(ExceptionCodes.SQL_CONNECTION_EXCEPTION, sqlException);
		}
		return connection;
	}

	private static Connection getConnectionFromRepository(PoolingDataSource dataSource) throws SQLException {
		return dataSource.getConnection();
	}

	public static Map<String, PoolingDataSource> getDataSourceMap() {
		return dataSourceMap;
	}

	private PoolingDataSource getPooledDataSource(DataSource dataSource) {
		String dbConURL = connectionMap.get(dataSource.getProvider());
		dbConURL = dbConURL.replaceAll("<hostName>", dataSource.getServerIP());
		dbConURL = dbConURL.replaceAll("<portNumber>", dataSource.getPort().toString());
		dbConURL = dbConURL.replaceAll("<schemaName>", dataSource.getSchemaName().toString());
		dbConURL = dbConURL.replaceAll("<db_name>", dataSource.getSchemaName().toString());
		GenericObjectPool genericObjectPool = new GenericObjectPool(null);
		genericObjectPool.setTestOnBorrow(trackerConfig.getTestOnBorrow());
		genericObjectPool.setMaxActive(trackerConfig.getMaxActive());
		genericObjectPool.setMaxIdle(trackerConfig.getMaxIdle());
		ObjectPool connectionPool = genericObjectPool;
		String password = dataSource.getPassword();
		if (dataSource.getEncryptedPassword().equals("Y")) {
			try {
				password = AesUtil.decryptBase64(dataSource.getPassword(), "execueDatasourceConnection",
						EncryptionAlgorithm.TRIPLE_DES);
			} catch (CryptographyException e) {
				e.getMessage();
			}
		}
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbConURL,
				dataSource.getUserName(), password);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
				connectionPool, null, getValidationQuery(dataSource.getProvider().longValue()), false, true);
		connectionPool = poolableConnectionFactory.getPool();
		PoolingDataSource poolingDataSource = new PoolingDataSource(connectionPool);
		return poolingDataSource;
	}

	private String getValidationQuery(Long provider) {
		if (provider == 1L)
			return "SELECT 1 FROM DUAL";
		else
			return "SELECT 1";
	}

	private void registerDrivers() throws DataAccessException {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver"); // Load oracle driver
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // Load sql server driver
		} catch (ClassNotFoundException e) {
			log.error("could nto load driver class: ", e);
			throw new DataAccessException(ExceptionCodes.SQL_DRIVER_CLASS_NOT_FOUND_EXCEPTION, e);
		}
	}

	static {
		connectionMap.put(DBProviderType.Oracle.getValue(), "jdbc:oracle:thin:@<hostName>:<portNumber>/<schemaName>");
		connectionMap.put(DBProviderType.MSSql.getValue(),
				"jdbc:sqlserver://<hostName>:<portNumber>;databaseName=<db_name>");

	}

	// To check if data source is active or not. If as part of source map we find
	// it, then it is active as we are loading only active sources.
	public static Boolean isSourceActive(String dataSource) {
		return (null != dataSourceMap.get(dataSource));
	}
}
