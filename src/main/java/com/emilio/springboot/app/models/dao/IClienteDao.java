package com.emilio.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.emilio.springboot.app.models.entities.Cliente;

public interface IClienteDao extends JpaRepository<Cliente, Long> {

	/**
	 * Al eliminar un plan se eliminaran los clientes con membresias asociadas a ese
	 * plan
	 * 
	 * @param planId
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM Cliente c WHERE c.membresia.planMensual.id = :planId")
	public void deleteByMembresiaPlanId(@Param("planId") Integer planId);

	/**
	 * Obtener clientes con membresía activa
	 *
	 * @return Lista de clientes con membresía activa
	 */
	@Query("SELECT c FROM Cliente c WHERE c.membresia.estadoMembresiaActiva = true")
	List<Cliente> findClientesMembresiaActiva();

	/**
	 * Obtener el número total de clientes con membresía activa
	 *
	 * @return Número total de clientes con membresía activa
	 */
	@Query("SELECT COUNT(c) FROM Cliente c WHERE c.membresia.estadoMembresiaActiva = true")
	public long countClientesConMembresiaActiva();

	/**
	 * Obtener el número total de clientes con membresía activa
	 *
	 * @return Número total de clientes con membresía activa
	 */
	@Query("SELECT COUNT(c) FROM Cliente c WHERE c.membresia.estadoMembresiaActiva = false")
	public long countClientesConMembresiaVencida();
	
	
	/**
	 * Metodo para buscar cliente con una palabra clave, Nombre o DNI
	 * @param keyWord
	 * @param pageable
	 * @return Clientes que coincidan con la palabra clave
	 */
	@Query("SELECT c FROM Cliente c WHERE" + " CONCAT(c.nombre,c.apellido,c.dni)" + " LIKE %:keyWord%")
	public Page<Cliente> findAll(@Param("keyWord") String keyWord, Pageable pageable);

	/**
	 * Metodo que obtiene todos los clientes con membresia activa
	 * @return Cliente con membresia activa
	 */
	@Query("SELECT c FROM Cliente c WHERE c.membresia.estadoMembresiaActiva = true")
	public Page<Cliente> clientesConMembresiaActiva(Pageable pageable);
	
	/**
	 * Metodo que obtiene todos los clientes con membresia vencida
	 * @return Cliente con membresia vencida
	 */
	@Query("SELECT c FROM Cliente c WHERE c.membresia.estadoMembresiaActiva = false")
	public Page<Cliente> clientesConMembresiaVencida(Pageable pageable);
}
