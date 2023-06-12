package com.mashreq.paymentTracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.LinkedReportInfo;

@Repository
public interface LinkedReportRepository extends JpaRepository<LinkedReportInfo, Long> {

	Optional<List<LinkedReportInfo>> findAllByReportId(long reportId);

}
