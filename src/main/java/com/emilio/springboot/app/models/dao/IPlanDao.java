package com.emilio.springboot.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.emilio.springboot.app.models.entities.Plan;

public interface IPlanDao extends JpaRepository<Plan, Integer>{

}
