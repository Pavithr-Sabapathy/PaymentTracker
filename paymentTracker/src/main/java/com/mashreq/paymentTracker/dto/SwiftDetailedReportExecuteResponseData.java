package com.mashreq.paymentTracker.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SwiftDetailedReportExecuteResponseData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8746030120396077816L;
	private ReportExecuteResponseMetaDTO meta;
	private List<ReportExecuteResponseColumnDefDTO> columnDefs;
	private List<Map<String, Object>> data;

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

	@Override
	public String toString() {
		return "SwiftDetailedReportExecuteResponseData [meta=" + meta + ", columnDefs=" + columnDefs + ", data=" + data
				+ "]";
	}

}
