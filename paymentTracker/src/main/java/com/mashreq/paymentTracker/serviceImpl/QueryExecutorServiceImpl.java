package com.mashreq.paymentTracker.serviceImpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.configuration.PaymentTrackerConfiguration;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dao.ComponentsCountryDAO;
import com.mashreq.paymentTracker.dto.FederatedReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.FederatedReportPromptDTO;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportOutput;
import com.mashreq.paymentTracker.dto.ReportQueryInfoDTO;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.exception.ResourceNotFoundException;
import com.mashreq.paymentTracker.model.ComponentsCountry;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportQueryInfoService;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class QueryExecutorServiceImpl implements QueryExecutorService {

	private static final Logger logger = LoggerFactory.getLogger(QueryExecutorServiceImpl.class);

	@Autowired
	private PaymentTrackerConfiguration trackerConfig;

	@Autowired
	ReportQueryInfoService reportQueryInfoService;

	@Autowired
	ComponentsCountryDAO componentsCountryDAO;

	private static final Logger log = LoggerFactory.getLogger(QueryExecutorServiceImpl.class);

	@Override
	public List<ReportOutput> executeQuery(ReportComponentDetailDTO componentDetail,
			FederatedReportComponentDetailContext context) {
		List<ReportOutput> outputList = new ArrayList<ReportOutput>();
		Long queryExecutionTime = 0L;
		ReportQueryInfoDTO reportQueryInfo = null;
		Date startTime = null;
		Date completionTime = null;
		String failureCause = null;
		CheckType dataFound = CheckType.NO;
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepStat = null;
		String promptValuemap = "Prompt Values";
		String queryKey = componentDetail.getQueryKey();
		//Long dataSourceId = componentDetail.getReportComponent().getDataSourceId();
		String queryString = replacePrompts(context.getQueryString(), context);
		try {
			/*
			 * ComponentsCountry componentsCountry =
			 * processComponentCountry(componentDetail.getReportComponentId()); DataSource
			 * dataSource = componentsCountry.getDataSourceConfig();
			 */startTime = new Date();
			reportQueryInfo = new ReportQueryInfoDTO();
			reportQueryInfo.setExecutionId(context.getExecutionId());
			reportQueryInfo.setDataSourceName("Flex");
			reportQueryInfo.setQueryKey(queryKey);
			reportQueryInfo.setExecutedQuery(queryString);
			reportQueryInfo.setStartTime(startTime);

			reportQueryInfoService.insertReportQueryInfo(reportQueryInfo);

			Class.forName(MashreqFederatedReportConstants.DRIVER_CLASS_NAME);
			//connection = SourceConnectionUtil.getConnection(dataSource.getName());
			connection = DriverManager.getConnection(MashreqFederatedReportConstants.FLEX_DATABASE_URL,
					MashreqFederatedReportConstants.DATABASE_USERNAME,
					MashreqFederatedReportConstants.DATABASE_PASSWORD);

			long conStartTime = System.currentTimeMillis();
			prepStat = connection.prepareStatement(queryString);
			rs = prepStat.executeQuery();
			completionTime = new Date();
			queryExecutionTime = System.currentTimeMillis() - conStartTime;
			if (rs != null) {
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (rs.next()) {
					ReportOutput componentData = new ReportOutput();
					List<Object> rowData = new ArrayList<Object>();
					for (int index = 1; index <= columnCount; index++) {
						Object colValue = rs.getObject(index);
						rowData.add(colValue);
					}
					componentData.setRowData(rowData);
					componentData.setComponentDetailId(componentDetail.getReportComponent().getId());
					outputList.add(componentData);
				}
			}
		} catch (SQLException sqlException) {
			failureCause = constructErrorCause(sqlException);
			logger.error("error executing query: " + queryString);
			logger.error("query execution error :", sqlException);
			sqlException.printStackTrace();
		} catch (Exception e) {
			failureCause = constructErrorCause(e);
			logger.error("query execution error :", e);
		} finally {
			if (null != reportQueryInfo) {
				reportQueryInfo.setExecutedQuery(
						context.getQueryString() + promptValuemap.concat(context.getPromptKeyValueMap().toString()));
				reportQueryInfo.setQueryExecutionTime(queryExecutionTime);
				reportQueryInfo.setFailureCause(failureCause);
				reportQueryInfo.setEndTime(completionTime);
				reportQueryInfo.setDataFound(dataFound);
				reportQueryInfoService.updateReportQueryInfo(reportQueryInfo);
				try {
					closeResources(connection, stmt, rs, prepStat);
				} catch (ReportException reportException) {
					logger.error("Closing datasaources connection Failure : ", reportException);
				}
			}
		}
		return outputList;
	}

	private String replacePrompts(String queryString, FederatedReportComponentDetailContext context) {
		String updatedSQL = queryString;

		LinkedHashMap<String, List<String>> promptKeyValueMap = new LinkedHashMap<>();
		LinkedHashMap<String, List<String>> matchedPromptKeyValueMap = new LinkedHashMap<>();

		LinkedHashSet<String> promptKeys = populatePromptKey(updatedSQL);
		if (!context.getPrompts().isEmpty()) {
			List<FederatedReportPromptDTO> instancePromts = context.getPrompts();
			for (FederatedReportPromptDTO prompts : instancePromts) {
				if (null != prompts) {

					String promptKey = prompts.getPromptKey();
					String promptValue = prompts.getPromptValue();
					List<String> promptValueList = Stream.of(promptValue.split(",", -1)).collect(Collectors.toList());
					if (null != promptKey && updatedSQL.indexOf(promptKey) > 0) {
						String noOfQuestionMarks = UtilityClass.populateSeriesOfQuestionSymbol(promptKey,
								promptValueList);
						promptKeyValueMap.put(promptKey, promptValueList);
						updatedSQL = updatedSQL.replaceAll(
								ApplicationConstants.SINGLE_QUOTE + ApplicationConstants.TILDE + promptKey
										+ ApplicationConstants.TILDE + ApplicationConstants.SINGLE_QUOTE,
								noOfQuestionMarks);
						queryString = queryString.replaceAll(
								ApplicationConstants.TILDE + promptKey + ApplicationConstants.TILDE,
								UtilityClass.getCommaSeperatedStringRepresentation(promptValueList));
					}
				}
			}
		}
		for (String promptKey : promptKeys) {
			matchedPromptKeyValueMap.put(promptKey, promptKeyValueMap.get(promptKey));
		}
		logger.info("Final SQL String with values :" + queryString);
		context.setQueryString(updatedSQL);
		context.setPromptKeyValueMap(matchedPromptKeyValueMap);
		return queryString;
	}

	private LinkedHashSet<String> populatePromptKey(String updatedSQL) {
		LinkedHashSet<String> queryPrompts = new LinkedHashSet<String>();
		Matcher matcher = null;
		matcher = trackerConfig.getPromptKeyConfig().getPromptKeyPattern().matcher(updatedSQL);
		while (matcher.find()) {
			queryPrompts.add(matcher.group().replaceAll(ApplicationConstants.TILDE, "").trim());
		}
		return queryPrompts;
	}

	private String constructErrorCause(Exception exception) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		exception.printStackTrace(printWriter);
		return "Trace: " + writer.toString();
	}

	private void closeResources(Connection connection, Statement statement, ResultSet resultSet,
			PreparedStatement prepStat) throws ReportException {
		closeResultSet(resultSet);
		closeStatement(statement);
		closePreparedStatment(prepStat);
		closeConnection(connection);
	}

	private void closeConnection(Connection connection) throws ReportException {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqlException) {
			logger.error("Exception while closing Connection");
		}

	}

	private void closeStatement(Statement statement) throws ReportException {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException sqlException) {
			logger.error("Exception while closing Statement");
		}

	}

	private void closePreparedStatment(PreparedStatement prepstatement) throws ReportException {
		try {
			if (prepstatement != null) {
				prepstatement.close();
			}
		} catch (SQLException sqlException) {
			logger.error("Exception while closing PreparedStatement");
		}
	}

	private void closeResultSet(ResultSet resultSet) throws ReportException {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException sqlException) {
			logger.error("Exception while closing ResultSet");
		}

	}

	private ComponentsCountry processComponentCountry(Long reportComponentId) {
		ComponentsCountry componentsCountry = componentsCountryDAO.findBycomponentsId(reportComponentId);
		if (null != reportComponentId) {
			if (null == componentsCountry) {
				throw new ResourceNotFoundException(
						ApplicationConstants.COMPONENT_COUNTRY_DOES_NOT_EXISTS + reportComponentId);
			} else {
				// Check if the corresponding DataSoure is Active or not
				if (CheckType.NO.getValue().equalsIgnoreCase(componentsCountry.getDataSourceConfig().getActive())) {
					log.error(componentsCountry.getDataSourceConfig().getName()
							+ " : Source System Connection is not Activie");
					return null;
				}
				if (componentsCountry.getDataSourceConfig().getSchemaName()
						.equalsIgnoreCase(MashreqFederatedReportConstants.DS_SWIFT)) {

					return componentsCountry;
				}
			}
		}
		return null;
	}
}
