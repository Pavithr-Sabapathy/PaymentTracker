package com.mashreq.paymentTracker.dao;

import com.mashreq.paymentTracker.model.ComponentsCountry;

public interface ComponentsCountryDAO {

	ComponentsCountry save(ComponentsCountry componentsCountryObject);

	ComponentsCountry findBycomponentsId(Long reportComponentId);

}