package com.mashreq.paymentTracker.service;

import java.util.List;

import com.mashreq.paymentTracker.dto.ReportComponentDetailContext;
import com.mashreq.paymentTracker.dto.ReportComponentDetailDTO;
import com.mashreq.paymentTracker.dto.ReportDefaultOutput;

public interface QueryExecutorService {

	List<ReportDefaultOutput> executeQuery(ReportComponentDetailDTO componentDetail,
			ReportComponentDetailContext context);

}
