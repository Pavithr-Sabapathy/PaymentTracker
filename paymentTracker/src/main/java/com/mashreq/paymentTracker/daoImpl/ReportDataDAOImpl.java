package com.mashreq.paymentTracker.daoImpl;

import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.dao.ReportDataDAO;
import com.mashreq.paymentTracker.model.ReportData;

@Repository
public class ReportDataDAOImpl extends BaseDAOImpl implements ReportDataDAO {

	@Override
	public ReportData save(ReportData reportData) {
		return super.create(reportData);

	}

}