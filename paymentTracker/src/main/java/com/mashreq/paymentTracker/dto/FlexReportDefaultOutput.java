package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;

public class FlexReportDefaultOutput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3647758976108096324L;
	private Long componentDetailId;
	private List<Object> rowData;
	private List<String> columnLabels;
	private List<String> transformedData;

	public Long getComponentDetailId() {
		return componentDetailId;
	}

	public void setComponentDetailId(Long componentDetailId) {
		this.componentDetailId = componentDetailId;
	}

	public List<Object> getRowData() {
		return rowData;
	}

	public void setRowData(List<Object> rowData) {
		this.rowData = rowData;
	}

	public List<String> getColumnLabels() {
		return columnLabels;
	}

	public void setColumnLabels(List<String> columnLabels) {
		this.columnLabels = columnLabels;
	}

	public List<String> getTransformedData() {
		return transformedData;
	}

	public void setTransformedData(List<String> transformedData) {
		this.transformedData = transformedData;
	}

}
