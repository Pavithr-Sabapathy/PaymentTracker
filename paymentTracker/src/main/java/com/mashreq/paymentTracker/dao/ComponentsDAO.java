package com.mashreq.paymentTracker.dao;

import java.util.List;

import com.mashreq.paymentTracker.model.Components;

public interface ComponentsDAO {

	Components save(Components componentsObject);

	void deleteById(long componentId);

	Components findById(long compReportId);

	List<Components> findAllByreportId(long reportId);

	Components update(Components componentsObject);

}