package com.mashreq.paymentTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.LinkedReportDetails;

@Repository
public interface LinkMappingRepository extends JpaRepository<LinkedReportDetails, Long>{

	List<LinkedReportDetails> findByLinkReportId(long linkReportId);

}
