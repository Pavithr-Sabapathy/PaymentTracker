package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ComponentDetails;

@Repository
public interface ComponentsDetailsCustomizedRepository {

	@Query("select reportComponent.* from conf_rpt_comp_det reportComponent join conf_rpt_comp component on reportComponent.report_comp_id = component.report_id where component.report_id =?1")
	ComponentDetails findQueryByReportId(Long reportId);

}