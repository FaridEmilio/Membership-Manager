package com.emilio.springboot.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.emilio.springboot.app.models.entities.Membresia;

public interface IMembresiaDao extends JpaRepository<Membresia, Long> {
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Membresia m WHERE m.planMensual.id = :planId")
	public void deleteByPlanId(@Param("planId") Integer planId);
	
}
