package com.mashreq.paymentTracker.dto;

import com.mashreq.paymentTracker.service.ReportOutput;

public class ReportBaseOutput implements ReportOutput{

	private Long componentDetailId;

	public Long getComponentDetailId() {
		return componentDetailId;
	}

	public void setComponentDetailId(Long componentDetailId) {
		this.componentDetailId = componentDetailId;
	}
}