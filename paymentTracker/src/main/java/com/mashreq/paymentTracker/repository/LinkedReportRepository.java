package com.mashreq.paymentTracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.LinkedReportInfo;

@Repository
@RepositoryRestResource(exported = false)
public interface LinkedReportRepository extends JpaRepository<LinkedReportInfo, Long> {

	Optional<List<LinkedReportInfo>> findAllByReportId(long reportId);

	@Query("select linkedReport from LinkedReportInfo linkedReport join ApplicationModule module on linkedReport.module = module.id where module.id=:moduleId ")
	Optional<List<LinkedReportInfo>> findByAllModuleId(long moduleId);

}
