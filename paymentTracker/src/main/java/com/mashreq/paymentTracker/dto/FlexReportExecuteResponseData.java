package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FlexReportExecuteResponseData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3647758976108096324L;
	private ReportExecuteResponseMetaDTO meta;
	private List<ReportExecuteResponseColumnDefDTO> columnDefs;
	private List<Map<String, Object>> data;
	private List<Object> rowData;

	public ReportExecuteResponseMetaDTO getMeta() {
		return meta;
	}

	public void setMeta(ReportExecuteResponseMetaDTO meta) {
		this.meta = meta;
	}

	public List<ReportExecuteResponseColumnDefDTO> getColumnDefs() {
		return columnDefs;
	}

	public void setColumnDefs(List<ReportExecuteResponseColumnDefDTO> columnDefs) {
		this.columnDefs = columnDefs;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public List<Object> getRowData() {
		return rowData;
	}

	public void setRowData(List<Object> rowData) {
		this.rowData = rowData;
	}

	@Override
	public String toString() {
		return "FlexReportExecuteResponseData [meta=" + meta + ", columnDefs=" + columnDefs + ", data=" + data + "]";
	}

}
