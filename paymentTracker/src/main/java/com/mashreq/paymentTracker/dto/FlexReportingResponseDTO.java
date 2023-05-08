package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FlexReportingResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7085109674625205108L;
	private List<Map<String, Object>> data;
	private FlexReportExecuteResponseMetaDTO meta;
	private List<FlexReportExecuteResponseColumnDef> columnDefs;

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public FlexReportExecuteResponseMetaDTO getMeta() {
		return meta;
	}

	public void setMeta(FlexReportExecuteResponseMetaDTO meta) {
		this.meta = meta;
	}

	public List<FlexReportExecuteResponseColumnDef> getColumnDefs() {
		return columnDefs;
	}

	public void setColumnDefs(List<FlexReportExecuteResponseColumnDef> columnDefs) {
		this.columnDefs = columnDefs;
	}

}