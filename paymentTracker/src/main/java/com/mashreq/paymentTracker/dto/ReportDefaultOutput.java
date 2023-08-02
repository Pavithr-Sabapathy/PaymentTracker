package com.mashreq.paymentTracker.dto;

import java.util.List;

public class ReportDefaultOutput extends ReportBaseOutput {

	private List<Object> rowData;
	private List<String> columnLabels;
	private List<String> transformedData;

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

	@Override
	public String toString() {
		return "ReportDefaultOutput [rowData=" + rowData + ", columnLabels=" + columnLabels + ", transformedData="
				+ transformedData + "]";
	}

}
