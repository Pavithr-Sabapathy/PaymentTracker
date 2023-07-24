package com.mashreq.paymentTracker.dao;

import java.util.List;

import com.mashreq.paymentTracker.model.LinkedReportInfo;

public interface LinkedReportDAO {

	void deleteById(long linkedReportId);

	List<LinkedReportInfo> findByAllModuleId(long moduleId);

	LinkedReportInfo findById(long linkedReportId);

	List<LinkedReportInfo> findAllByReportId(long reportId);

	LinkedReportInfo save(LinkedReportInfo linkedReportModel);

	LinkedReportInfo update(LinkedReportInfo linkedReportModel);

}