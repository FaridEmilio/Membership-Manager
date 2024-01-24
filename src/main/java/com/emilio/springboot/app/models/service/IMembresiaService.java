package com.emilio.springboot.app.models.service;

import java.util.List;

import com.emilio.springboot.app.models.entities.Membresia;

public interface IMembresiaService {

	
	public Membresia findById(Long id);

	public void save(Membresia membresia);

	/**
	 * Obtiene todas las membresías y llama al método isMembresiaActiva para cada una
	 * de ellas. Este método verificará y actualizará el estado de la membresía
	 * según la lógica implementada
	 */
	public void verificarYActualizarMembresias();

	public List<Membresia> findAll();
	
	public void verificarMembresia(Long id);
	
	public void delete(Long id);
}
