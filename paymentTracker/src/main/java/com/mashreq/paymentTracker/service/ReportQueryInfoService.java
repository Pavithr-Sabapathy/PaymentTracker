package com.mashreq.paymentTracker.service;

import com.mashreq.paymentTracker.dto.ReportQueryInfoDTO;

public interface ReportQueryInfoService {

	ReportQueryInfoDTO insertReportQueryInfo(ReportQueryInfoDTO reportQueryInfoDTO);

	void updateReportQueryInfo(ReportQueryInfoDTO reportQueryInfoDTO);

}
