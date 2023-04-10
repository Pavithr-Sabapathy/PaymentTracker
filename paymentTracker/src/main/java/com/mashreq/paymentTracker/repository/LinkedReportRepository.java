package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.LinkedReportInfo;

@Repository
public interface LinkedReportRepository extends JpaRepository<LinkedReportInfo, Long> {


}
