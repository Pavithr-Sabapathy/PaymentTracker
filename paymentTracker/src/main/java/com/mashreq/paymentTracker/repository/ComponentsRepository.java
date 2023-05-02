package com.mashreq.paymentTracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mashreq.paymentTracker.model.Components;

@Repository
public interface ComponentsRepository extends JpaRepository<Components, Long>{

	Optional<Components> findAllByreportId(long id);

}
