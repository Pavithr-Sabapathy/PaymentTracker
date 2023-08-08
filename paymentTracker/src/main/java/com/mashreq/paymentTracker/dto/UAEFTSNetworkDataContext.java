package com.mashreq.paymentTracker.dto;

import java.sql.Timestamp;

import com.mashreq.paymentTracker.service.ReportInput;

public class UAEFTSNetworkDataContext implements ReportInput {
	 private String    status;
	   public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getAckTime() {
		return ackTime;
	}
	public void setAckTime(Timestamp ackTime) {
		this.ackTime = ackTime;
	}
	private Timestamp ackTime;
}
