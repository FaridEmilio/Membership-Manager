package com.emilio.springboot.app.models.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.emilio.springboot.app.models.dao.IClienteDao;
import com.emilio.springboot.app.models.dao.IMembresiaDao;
import com.emilio.springboot.app.models.entities.Cliente;

@Service
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	private IClienteDao clienteDao;
	
	@Autowired
	private IMembresiaDao membresiaDao;

	@Transactional(readOnly = true)
	@Override
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteDao.findAll();
	}

	@Transactional
	@Override
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
	}

	@Transactional(readOnly = true)
	@Override
	public Cliente findById(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	/**
	 * Método que recupera todos los clientes paginados.
	 *
	 * @param pageable Objeto Pageable que define la paginación y ordenación de los resultados.
	 * @return Página de clientes según la configuración de paginación.
	 */
	@Transactional(readOnly = true)
	@Override
	public Page<Cliente> findAll(Pageable pageable) {
		return clienteDao.findAll(pageable);
	}

	@Transactional
	@Override
	public void delete(Long id) {
		clienteDao.deleteById(id);
	}
	
	/**
	 * Método para eliminar un cliente por su ID, junto con la membresía asociada.
	 * Se obtiene el ID de la membresía asociada al cliente y se eliminan ambos registros
	 * de manera transaccional.
	 *
	 * @param id Identificador único del cliente que se va a eliminar.
	 */
	@Transactional
	@Override
	public void deleteConMembresia(Long id) {		
		Long membresia_id = clienteDao.findById(id).get().getMembresia().getId();
		clienteDao.deleteById(id);
		membresiaDao.deleteById(membresia_id);
	}

}
