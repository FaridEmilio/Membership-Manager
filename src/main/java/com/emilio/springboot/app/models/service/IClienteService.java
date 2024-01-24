package com.emilio.springboot.app.models.service;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.emilio.springboot.app.models.entities.Cliente;

public interface IClienteService {
	
	public List<Cliente> findAll();
	
	public Page<Cliente> findAll(Pageable pageable);

	public void save(Cliente cliente);

	public Cliente findById(Long id);

	void delete(Long id);
	
	public void deleteConMembresia(Long id);
		
	}
