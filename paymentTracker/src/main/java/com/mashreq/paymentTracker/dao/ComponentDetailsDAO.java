package com.mashreq.paymentTracker.dao;

import com.mashreq.paymentTracker.model.ComponentDetails;

public interface ComponentDetailsDAO {

	ComponentDetails save(ComponentDetails componentDetailsObject);

	void deleteById(long componentDetailId);

	ComponentDetails findById(long componentDetailId);

	ComponentDetails update(ComponentDetails componentDetailsObject);
	
}