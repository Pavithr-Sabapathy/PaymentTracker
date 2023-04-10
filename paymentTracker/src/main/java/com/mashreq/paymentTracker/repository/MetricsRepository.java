package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Metrics;

@Repository
public interface MetricsRepository extends JpaRepository<Metrics, Long>, MetricsCustomizedRepository {


}