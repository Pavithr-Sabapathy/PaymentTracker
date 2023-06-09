package com.mashreq.paymentTracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ComponentsCountry;

@Repository
@RepositoryRestResource(exported =false)
public interface ComponentsCountryRepository extends JpaRepository<ComponentsCountry, Long> {

	Optional<ComponentsCountry> findBycomponentsId(long Id);

}
