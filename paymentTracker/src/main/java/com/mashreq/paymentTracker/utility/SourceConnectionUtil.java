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
import com.mashreq.paymentTracker.exception.ExceptionCodes;

import com.mashreq.paymentTracker.model.DataSourceConfig;
import com.mashreq.paymentTracker.exception.DataAccessException;
import com.mashreq.paymentTracker.service.DataSourceConfigService;
import com.mashreq.paymentTracker.configuration.TrackerConfiguration;
import jakarta.annotation.PostConstruct;

public class SourceConnectionUtil {

	private static final Logger log = LoggerFactory.getLogger(SourceConnectionUtil.class);
	private static final String FILENAME = "SourceConnectionUtil";

	@Autowired
	private DataSourceConfigService dataSourceConfigService;

	@Autowired
	private TrackerConfiguration trackerConfig;

	private static Map<Long, String> connectionMap = new HashMap<Long, String>();
	private static Map<String, PoolingDataSource> dataSourceMap = new HashMap<String, PoolingDataSource>();

	@PostConstruct
	void loadDataSources() throws DataAccessException {

		List<DataSourceConfig> dataSources = dataSourceConfigService.allDataSourceConfig();

		try {
			setupDataSources(dataSources);
			log.info("Logging Tracker Configuration Values ");
			log.info(trackerConfig.toString());
		} catch (DataAccessException e) {
			log.error("Data souce load exception" + e);
			throw new DataAccessException(ExceptionCodes.SQL_DATASOURCE_EXCEPTION, e);
		}
	}

	public void setupDataSources(List<DataSourceConfig> dataSources) throws DataAccessException {
		synchronized (dataSourceMap) {
			// Register the required Database Drivers
			registerDrivers();
			// Setup Connection Pooling Factories for each data source
			for (DataSourceConfig dataSource : dataSources) {
				setupDataSource(dataSource);
			}
		}
	}

	private void setupDataSource(DataSourceConfig dataSource) {
		PoolingDataSource poolingDataSource = null;
		poolingDataSource = getPooledDataSource(dataSource);
		addDataSource(dataSource.getDataSourceName(), poolingDataSource);
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

	private PoolingDataSource getPooledDataSource(DataSourceConfig dataSource) {
		String dbConURL = connectionMap.get(dataSource.getDataSourceProvider());
		dbConURL = dbConURL.replaceAll("<hostName>", dataSource.getServerIP());
		dbConURL = dbConURL.replaceAll("<portNumber>", dataSource.getPort().toString());
		dbConURL = dbConURL.replaceAll("<schemaName>", dataSource.getDataSourceSchemaName().toString());
		dbConURL = dbConURL.replaceAll("<db_name>", dataSource.getDataSourceSchemaName().toString());
		GenericObjectPool genericObjectPool = new GenericObjectPool(null);
		genericObjectPool.setTestOnBorrow(trackerConfig.getTestOnBorrow());
		genericObjectPool.setMaxActive(trackerConfig.getMaxActive());
		genericObjectPool.setMaxIdle(trackerConfig.getMaxIdle());
		ObjectPool connectionPool = genericObjectPool;
		String password = dataSource.getDataSourcePassword();
		if (dataSource.getEncryptedPassword().equals("Y")) {
			AesUtil aesUtil = new AesUtil();
			password = aesUtil.decrypt(dataSource.getDataSourcePassword());
		}
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbConURL,
				dataSource.getDataSourceUserName(), password);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,
				connectionPool, null, getValidationQuery(dataSource.getDataSourceProvider()), false, true);
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
