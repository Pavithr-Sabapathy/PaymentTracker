package com.mashreq.paymentTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Report;

@Repository
@RepositoryRestResource(exported = false)
public interface ReportConfigurationRepository extends JpaRepository<Report, Long> {

	Report findByReportName(String reportName);

	@Query("select report from Report report join ApplicationModule module on report.moduleId = module.id where module.moduleName =:moduleName")
	List<Report> findReportByModule(String moduleName);

	List<Report> findByModuleId(Long moduleId);
}
