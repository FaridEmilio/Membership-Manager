package com.emilio.springboot.app.models.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emilio.springboot.app.models.dao.IMembresiaDao;
import com.emilio.springboot.app.models.entities.Membresia;

@Service
public class MembresiaServiceImpl implements IMembresiaService {

	@Autowired
	private IMembresiaDao membresiaDao;

	@Override
	@Transactional(readOnly = true)
	public Membresia findById(Long id) {
		return membresiaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void save(Membresia membresia) {
		membresiaDao.save(membresia);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Membresia> findAll() {
		return (List<Membresia>) membresiaDao.findAll();
	}

	/**
	 * Método programado para ejecutarse todos los días a las 12 AM, que verifica y actualiza
	 * el estado de todas las membresías almacenadas en la base de datos.
	 * 
	 * Se utiliza la anotación @Transactional para garantizar la integridad de la transacción.
	 * 
	 * Se obtienen todas las membresías almacenadas y se itera sobre ellas para
	 * verificar y actualizar cada una.
	 */
	@Override
	@Transactional
	@Scheduled(cron = "0 0 0 * * ?") // Se ejecuta todos los días a las 12 AM
	public void verificarYActualizarMembresias() {

		List<Membresia> membresias = membresiaDao.findAll();

		for (Membresia membresia : membresias) {
			this.verificarMembresia(membresia.getId());
		}
	}

	@Transactional
	@Override
	public void delete(Long id) {
		membresiaDao.deleteById(id);
	}

	/**
	 * Método encargado de verificar y actualizar el estado de una membresía dado su ID.
	 * Si la membresía existe, compara la fecha actual con la fecha de vencimiento
	 * para determinar si la membresía sigue activa. 
	 * Si hay un cambio en el estado de la membresía, se actualiza y guarda en la base de datos.
	 *
	 * @param id Identificador único de la membresía que se va a verificar y actualizar.
	 */
	@Override
	@Transactional
	public void verificarMembresia(Long id) {
		Membresia membresia = this.findById(id);

		if (!membresia.equals(null)) {

			LocalDate fechaActual = LocalDate.now();
			LocalDate vencimiento = membresia.getVencimiento();

			boolean membresiaActiva = fechaActual.isBefore(vencimiento);

			if (membresia.getEstadoMembresiaActiva() != membresiaActiva) {
				membresia.setEstadoMembresiaActiva(membresiaActiva);
				this.save(membresia);
			}
		}
	}
}
