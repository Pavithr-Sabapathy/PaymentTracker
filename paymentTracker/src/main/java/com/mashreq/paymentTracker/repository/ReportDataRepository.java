package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ReportData;

@Repository
@RepositoryRestResource(exported = false)
public interface ReportDataRepository extends JpaRepository<ReportData, Long> {

}
