package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ReportData;

@Repository
public interface ReportDataRepository extends JpaRepository<ReportData, Long> {

}
