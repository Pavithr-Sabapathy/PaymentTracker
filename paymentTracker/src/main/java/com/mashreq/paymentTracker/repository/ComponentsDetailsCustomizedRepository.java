package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ComponentDetails;

@Repository
public interface ComponentsDetailsCustomizedRepository {

	@Query("select reportComponent.id,reportComponent.query,reportComponent.queryKey,reportComponent.components from ComponentDetails reportComponent join Components component on reportComponent.components = component.report where component.report =1")
	ComponentDetails findQueryByReportId(Long reportId);

}