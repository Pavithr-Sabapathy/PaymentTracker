package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import com.mashreq.paymentTracker.type.CountryType;

public class SourceQueryExecutionContext implements Serializable{


	private static final long serialVersionUID = -6562215658927484795L;
	private Long queryId;
	private String queryKey;
	private Long executionId;
	private DataSourceDTO dataSource;
	private String queryString;
	private CountryType country = CountryType.UAE;
	private List<ReportPromptsInstanceDTO> instancePrompts;
	private LinkedHashMap<String, List<String>> promptKeyValueMap;

	public Long getQueryId() {
		return queryId;
	}

	public void setQueryId(Long queryId) {
		this.queryId = queryId;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	public Long getExecutionId() {
		return executionId;
	}

	public void setExecutionId(Long executionId) {
		this.executionId = executionId;
	}

	public DataSourceDTO getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceDTO dataSource) {
		this.dataSource = dataSource;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public CountryType getCountry() {
		return country;
	}

	public void setCountry(CountryType country) {
		this.country = country;
	}

	public List<ReportPromptsInstanceDTO> getInstancePrompts() {
		return instancePrompts;
	}

	public void setInstancePrompts(List<ReportPromptsInstanceDTO> instancePrompts) {
		this.instancePrompts = instancePrompts;
	}

	public LinkedHashMap<String, List<String>> getPromptKeyValueMap() {
		return promptKeyValueMap;
	}

	public void setPromptKeyValueMap(LinkedHashMap<String, List<String>> promptKeyValueMap) {
		this.promptKeyValueMap = promptKeyValueMap;
	}

	@Override
	public String toString() {
		return "SourceQueryExecutionContext [queryId=" + queryId + ", queryKey=" + queryKey + ", executionId="
				+ executionId + ", dataSource=" + dataSource + ", queryString=" + queryString + ", country=" + country
				+ ", instancePrompts=" + instancePrompts + ", promptKeyValueMap=" + promptKeyValueMap + "]";
	}

}
