package com.emilio.springboot.app.models.service;

import java.util.List;

import com.emilio.springboot.app.models.entities.Plan;

public interface IPlanService {

	public List<Plan> findAll();

	public Plan findById(Integer id);

	public void save(Plan plan);

	public void delete(Integer id);
}
