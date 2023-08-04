package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import com.mashreq.paymentTracker.type.CountryType;

public class ReportComponentDetailContext implements Serializable {

	private static final long serialVersionUID = -6562215658927484795L;
	private Long queryId;
	private String queryKey;
	private Long executionId;
	private DataSourceRequestDTO dataSource;
	private String queryString;
	private CountryType country = CountryType.UAE;
	private List<FederatedReportPromptDTO> prompts;
	private LinkedHashMap<String, List<String>> promptKeyValueMap;
	private boolean                            populateMetadata = false;
	private List<ReportDefaultOutput> componentDetailData;

	
	
	public List<ReportDefaultOutput> getComponentDetailData() {
		return componentDetailData;
	}

	public void setComponentDetailData(List<ReportDefaultOutput> componentDetailData) {
		this.componentDetailData = componentDetailData;
	}

	public boolean isPopulateMetadata() {
		return populateMetadata;
	}

	public void setPopulateMetadata(boolean populateMetadata) {
		this.populateMetadata = populateMetadata;
	}

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

	public DataSourceRequestDTO getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceRequestDTO dataSource) {
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

	public List<FederatedReportPromptDTO> getPrompts() {
		return prompts;
	}

	public void setPrompts(List<FederatedReportPromptDTO> prompts) {
		this.prompts = prompts;
	}

	public LinkedHashMap<String, List<String>> getPromptKeyValueMap() {
		return promptKeyValueMap;
	}

	public void setPromptKeyValueMap(LinkedHashMap<String, List<String>> promptKeyValueMap) {
		this.promptKeyValueMap = promptKeyValueMap;
	}

	@Override
	public String toString() {
		return "FederatedReportComponentDetailContext [queryId=" + queryId + ", queryKey=" + queryKey + ", executionId="
				+ executionId + ", dataSource=" + dataSource + ", queryString=" + queryString + ", country=" + country
				+ ", prompts=" + prompts + ", promptKeyValueMap=" + promptKeyValueMap + "]";
	}

	
}
