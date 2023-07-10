package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.LinkedReportDetails;

@Repository
@RepositoryRestResource(exported = false)
public interface LinkMappingRepository extends JpaRepository<LinkedReportDetails, Long>{
	
	@Query("select linkreport from LinkedReportDetails linkreport where linkReportPromptId=:linkPromptId ")
	LinkedReportDetails findByLinkReportPromptId(long linkPromptId);

}
