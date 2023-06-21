package com.mashreq.paymentTracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Components;

@Repository
@RepositoryRestResource(exported = false)
public interface ComponentsRepository extends JpaRepository<Components, Long>{

	Optional<List<Components>> findAllByreportId(long id);

}
