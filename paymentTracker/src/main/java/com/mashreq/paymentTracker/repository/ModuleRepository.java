package com.mashreq.paymentTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ApplicationModule;

@Repository
public interface ModuleRepository extends JpaRepository<ApplicationModule, Long> {


}