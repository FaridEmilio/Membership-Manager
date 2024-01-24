package com.emilio.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emilio.springboot.app.models.dao.IClienteDao;
import com.emilio.springboot.app.models.dao.IMembresiaDao;
import com.emilio.springboot.app.models.dao.IPlanDao;
import com.emilio.springboot.app.models.entities.Plan;

@Service
public class PlanServiceImpl implements IPlanService {

	@Autowired
	private IPlanDao planDao;

	@Autowired
	private IClienteDao clienteDao;
	
	@Autowired
	private IMembresiaDao membresiaDao;

	@Transactional(readOnly = true)
	@Override
	public List<Plan> findAll() {

		return (List<Plan>) planDao.findAll();
	}

	@Override
	public Plan findById(Integer id) {
		return planDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public void save(Plan plan) {
		planDao.save(plan);

	}

	/**
	 * Método para eliminar un plan por su ID.
	 * 
	 * Al eliminar un plan, se eliminan también todos los clientes asociados a
	 * membresías que tienen este plan, así como las membresías correspondientes al plan.
	 *
	 * @param id Identificador único del plan que se va a eliminar.
	 */
	@Override
	public void delete(Integer id) {
		clienteDao.deleteByMembresiaPlanId(id);
		membresiaDao.deleteByPlanId(id);
		planDao.deleteById(id);
	}

}
