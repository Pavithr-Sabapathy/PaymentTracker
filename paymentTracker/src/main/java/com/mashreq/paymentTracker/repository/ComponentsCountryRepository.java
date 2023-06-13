package com.mashreq.paymentTracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.ComponentsCountry;

@Repository
public interface ComponentsCountryRepository extends JpaRepository<ComponentsCountry, Long> {

	Optional<List<ComponentsCountry>> findAllBycomponentsId(long Id);

}
