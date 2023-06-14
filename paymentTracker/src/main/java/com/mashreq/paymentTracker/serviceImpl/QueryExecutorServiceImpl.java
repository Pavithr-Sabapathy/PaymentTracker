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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashreq.paymentTracker.configuration.TrackerConfiguration;
import com.mashreq.paymentTracker.constants.ApplicationConstants;
import com.mashreq.paymentTracker.constants.MashreqFederatedReportConstants;
import com.mashreq.paymentTracker.dto.FlexReportExecuteResponseData;
import com.mashreq.paymentTracker.dto.ReportPromptsInstanceDTO;
import com.mashreq.paymentTracker.dto.ReportQueryInfoDTO;
import com.mashreq.paymentTracker.dto.SourceQueryExecutionContext;
import com.mashreq.paymentTracker.exception.ReportException;
import com.mashreq.paymentTracker.repository.ComponentsCountryRepository;
import com.mashreq.paymentTracker.service.QueryExecutorService;
import com.mashreq.paymentTracker.service.ReportQueryInfoService;
import com.mashreq.paymentTracker.utility.CheckType;
import com.mashreq.paymentTracker.utility.UtilityClass;

@Component
public class QueryExecutorServiceImpl implements QueryExecutorService {

	private static final Logger logger = LoggerFactory.getLogger(QueryExecutorServiceImpl.class);

	@Autowired
	private TrackerConfiguration trackerConfig;

	@Autowired
	ReportQueryInfoService reportQueryInfoService;

	@Autowired
	ComponentsCountryRepository componentsCountryRepository;

	@Override
	public final List<FlexReportExecuteResponseData> executeQuery(
			SourceQueryExecutionContext sourceQueryExecutionContext) throws ReportException {
		List<FlexReportExecuteResponseData> flexReportDefaultOutputList = new ArrayList<FlexReportExecuteResponseData>();
		ReportQueryInfoDTO reportQueryInfo = null;
		String failureCause = null;
		Date completionTime = null;
		CheckType dataFound = CheckType.NO;
		Long queryExecutionTime = 0L;
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		PreparedStatement prepStat = null;
		String promptValuemap = "Prompt Values";
		String sql = sourceQueryExecutionContext.getQueryString();
		String queryString = replacePrompts(sql, sourceQueryExecutionContext);

		sourceQueryExecutionContext.setQueryString(queryString);
		try {

			Date startTime = new Date();
			reportQueryInfo = new ReportQueryInfoDTO();
			reportQueryInfo.setExecutionId(sourceQueryExecutionContext.getExecutionId());
			reportQueryInfo.setDataSourceName("Flex");
			reportQueryInfo.setQueryKey(sourceQueryExecutionContext.getQueryKey());
			reportQueryInfo.setExecutedQuery(sourceQueryExecutionContext.getQueryString());
			reportQueryInfo.setStartTime(startTime);

			reportQueryInfoService.insertReportQueryInfo(reportQueryInfo);

			// Fetch Result Set
			/*
			 * DataSourceDTO dataSource = sourceQueryExecutionContext.getDataSource(); 
			 * con = SourceConnectionUtil.getConnection(dataSource.getName());
			 */
			Class.forName(MashreqFederatedReportConstants.DRIVER_CLASS_NAME);
			connection = DriverManager.getConnection(MashreqFederatedReportConstants.FLEX_DATABASE_URL,
					MashreqFederatedReportConstants.DATABASE_USERNAME,
					MashreqFederatedReportConstants.DATABASE_PASSWORD);

			long conStartTime = System.currentTimeMillis();
			prepStat = connection.prepareStatement(sourceQueryExecutionContext.getQueryString());
			rs = prepStat.executeQuery();
			completionTime = new Date();
			queryExecutionTime = System.currentTimeMillis() - conStartTime;
			if (rs != null) {
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				while (rs.next()) {
					FlexReportExecuteResponseData flexReportOutput = new FlexReportExecuteResponseData();
					List<Object> rowData = new ArrayList<Object>();
					for (int index = 1; index < columnCount; index++) {
						Object colValue = rs.getObject(index);
						rowData.add(colValue);
					}
					flexReportOutput.setRowData(rowData);
					flexReportDefaultOutputList.add(flexReportOutput);
				}
			}

		} catch (SQLException sqlException) {
			failureCause = constructErrorCause(sqlException);
			logger.error("error executing query: " + sql);
			logger.error("query execution error :", sqlException);
			sqlException.printStackTrace();
		} catch (Exception e) {
			failureCause = constructErrorCause(e);
			logger.error("query execution error :", e);
		} finally {
			if (null != reportQueryInfo) {
				reportQueryInfo.setExecutedQuery(sourceQueryExecutionContext.getQueryString()
						+ promptValuemap.concat(sourceQueryExecutionContext.getPromptKeyValueMap().toString()));
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
		return flexReportDefaultOutputList;
	}

	@SuppressWarnings("unused")
	private String populatePreparedStatementWithPromptValues1(PreparedStatement ps,
			LinkedHashMap<String, List<String>> promptKeyValueMap) throws ReportException {
		StringBuilder promptValueMapString = new StringBuilder();
		int counter = 1;
		for (Entry<String, List<String>> entry : promptKeyValueMap.entrySet()) {
			for (String promptValue : entry.getValue()) {
				try {
					ps.setString(counter, promptValue);
					promptValueMapString.append(" - " + promptValue);
					System.out.println(counter + " - " + promptValue);
				} catch (SQLException sqlException) {
					logger.error("Error seting up the Prepared Statment values :", sqlException);
				}
				counter++;
			}
		}
		return promptValueMapString.toString();
	}

	private String constructErrorCause(Exception exception) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		exception.printStackTrace(printWriter);
		return "Trace: " + writer.toString();
	}

	private String replacePrompts(String sql, SourceQueryExecutionContext sourceQueryExecutionContext)
			throws ReportException {

		String updatedSQL = sql;

		LinkedHashMap<String, List<String>> promptKeyValueMap = new LinkedHashMap<>();
		LinkedHashMap<String, List<String>> matchedPromptKeyValueMap = new LinkedHashMap<>();

		LinkedHashSet<String> promptKeys = populatePromptKey(updatedSQL);

		if (!sourceQueryExecutionContext.getInstancePrompts().isEmpty()) {
			List<ReportPromptsInstanceDTO> instancePromts = sourceQueryExecutionContext.getInstancePrompts();
			for (ReportPromptsInstanceDTO prompts : instancePromts) {
				if (null != prompts && null != prompts.getPrompt()) {
					String promptKey = prompts.getPrompt().getKey();
					List<String> promptValueList = prompts.getPrompt().getValue();
					if (null != promptKey && updatedSQL.indexOf(promptKey) > 0) {
						String noOfQuestionMarks = UtilityClass.populateSeriesOfQuestionSymbol(promptKey,
								promptValueList);
						promptKeyValueMap.put(promptKey, promptValueList);
						updatedSQL = updatedSQL.replaceAll(
								ApplicationConstants.SINGLE_QUOTE + ApplicationConstants.TILDE + promptKey
										+ ApplicationConstants.TILDE + ApplicationConstants.SINGLE_QUOTE,
								noOfQuestionMarks);
						sql = sql.replaceAll(
								ApplicationConstants.SINGLE_QUOTE + ApplicationConstants.TILDE + promptKey
										+ ApplicationConstants.TILDE + ApplicationConstants.SINGLE_QUOTE,
								UtilityClass.getCommaSeperatedStringRepresentation(promptValueList));
					}
				}
			}
			;
		}
		for (String promptKey : promptKeys) {
			matchedPromptKeyValueMap.put(promptKey, promptKeyValueMap.get(promptKey));
		}

		logger.info("Final SQL String with values :" + sql);

		sourceQueryExecutionContext.setQueryString(updatedSQL);
		sourceQueryExecutionContext.setPromptKeyValueMap(matchedPromptKeyValueMap);

		return sql;

	}

	private LinkedHashSet<String> populatePromptKey(String sql) {

		// TODO need to return LisnkeHashSet
		LinkedHashSet<String> queryPrompts = new LinkedHashSet<String>();
		Matcher matcher = null;
		matcher = trackerConfig.getPromptKeyConfig().getPromptKeyPattern().matcher(sql);
		while (matcher.find()) {
			queryPrompts.add(matcher.group().replaceAll(ApplicationConstants.TILDE, "").trim());
		}
		return queryPrompts;
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

	protected String getColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		return resultSetMetaData.getColumnName(columnIndex);
	}

	protected String getColumnLabel(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		return resultSetMetaData.getColumnLabel(columnIndex);
	}

	public static String getStringRepresentation(Object colValue) {
		String colString = null;
		if (colValue != null) {
			colString = colValue.toString().trim();
		}
		return colString;
	}

	public static Timestamp getTimeStampRepresentation(Object colValue) {
		Timestamp colDate = null;
		if (colValue != null) {
			colDate = (Timestamp) colValue;
		}
		return colDate;
	}

	public static Integer getNumberRepresentation(Object colValue) {
		Integer colString = null;
		if (colValue != null) {
			colString = Integer.parseInt(colValue.toString().trim());
		}
		return colString;
	}

}
