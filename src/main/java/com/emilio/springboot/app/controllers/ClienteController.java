package com.emilio.springboot.app.controllers;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.emilio.springboot.app.controllers.util.paginator.PageRender;
import com.emilio.springboot.app.models.dao.IClienteDao;
import com.emilio.springboot.app.models.entities.Cliente;
import com.emilio.springboot.app.models.entities.Membresia;
import com.emilio.springboot.app.models.entities.Plan;
import com.emilio.springboot.app.models.service.IClienteService;
import com.emilio.springboot.app.models.service.IMembresiaService;
import com.emilio.springboot.app.models.service.IPlanService;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({ "cliente", "membresia" })
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IPlanService planService;

	@Autowired
	private IMembresiaService membresiaService;

	@Autowired
	private IClienteDao clienteDao;

	/**
	 * Endpoint para listar los miembros paginados asi como tambien filtrados por
	 * palabra clave si la keyword no es nula.
	 * 
	 * @param page
	 * @param model
	 * @param keyWord
	 * @return Lista de Clientes paginados
	 */
	@GetMapping("/listar")
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			@Param("keyWord") String keyWord) {

		// membresiaService.verificarYActualizarMembresias();
		Pageable pageRequest = PageRequest.of(page, 10);

		if (keyWord != null) {
			Page<Cliente> clientes = clienteDao.findAll(keyWord, pageRequest);
			PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
			model.addAttribute("clientes", clientes);
			model.addAttribute("keyWord", keyWord);
			model.addAttribute("page", pageRender);
		} else {

			Page<Cliente> clientes = clienteService.findAll(pageRequest);
			PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
			model.addAttribute("clientes", clientes);
			model.addAttribute("page", pageRender);
		}

		long totalActivas = clienteDao.countClientesConMembresiaActiva();
		long totalVencidas = clienteDao.countClientesConMembresiaVencida();
		model.addAttribute("totalActivas", totalActivas);
		model.addAttribute("totalVencidas", totalVencidas);
		model.addAttribute("titulo", "Miembros de Full Gym");

		return "listar";
	}

	/**
	 * Método para cargar la lista de planes en el modelo antes de mostrar el
	 * formulario de creación de cliente.
	 * 
	 * @return Lista de planes disponibles.
	 */
	@ModelAttribute("listaPlanes")
	public List<Plan> listaPlanes() {
		return planService.findAll();
	}

	/**
	 * Endpoint para mostrar el formulario de creación de un nuevo miembro. Se
	 * aplica la logica para el manejo de fechas cuando se crea un miembro por
	 * primera vez.
	 * 
	 * @param model Objeto Model para enviar datos a la vista.
	 * @return Nombre de la vista a mostrar ("form").
	 */
	@GetMapping("/form")
	public String crearCliente(Model model) {
		Cliente cliente = new Cliente();
		Membresia membresia = new Membresia();

		// Configura la membresía con datos iniciales
		membresia.setFechaDeAlta(LocalDate.now());

		// Vencimiento inicialmente un mes después de la fecha
		// de alta

		membresia.setVencimiento(LocalDate.now().plusMonths(1));
		// Inicialmente el estado de membresia esta activo
		membresia.setEstadoMembresiaActiva(true);

		// Asocio la membresia a el cliente
		cliente.setMembresia(membresia);
		// Configura la fecha de inicio como el día actual
		cliente.setFechaInicio(LocalDate.now());
		model.addAttribute("editMode", false); // O false según sea necesario
		model.addAttribute("cliente", cliente);
		model.addAttribute("membresia", membresia);
		model.addAttribute("titulo", "Registrar nuevo miembro");
		model.addAttribute("tituloBoton", "Registrar miembro");

		return "form";

	}

	/**
	 * Endpoint para procesar el formulario de creación de un nuevo miembro.
	 * 
	 * @param cliente      Objeto Cliente con los datos del nuevo miembro.
	 * @param result       Resultado de la validación de datos del cliente.
	 * @param membresia    Objeto Membresia con los datos de la membresía del nuevo
	 *                     miembro.
	 * @param resultMembre Resultado de la validación de datos de la membresía.
	 * @param model        Objeto Model para enviar datos a la vista.
	 * @param flash        Objeto RedirectAttributes para agregar mensajes flash.
	 * @param status       Objeto SessionStatus para gestionar el estado de la
	 *                     sesión.
	 * @return Redirección a la vista de lista de miembros ("redirect:/listar").
	 */
	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result,
			@Valid @ModelAttribute("membresia") Membresia membresia, BindingResult resultMembre, Model model,
			RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Ingrese los datos nuevamente");
			model.addAttribute("tituloBoton", "Registrar miembro");

			return "form";
		} else {
			if (resultMembre.hasErrors()) {

				model.addAttribute("titulo", "Hay error en resultMember");
				model.addAttribute("tituloBoton", "Registrar miembro");

				for (ObjectError error : resultMembre.getAllErrors()) {
					System.out.println("Error: " + error.getDefaultMessage() + error.getObjectName());
					return "form";
				}
			}
		}

		// Guarda la membresía en la base de datos
		membresiaService.save(membresia);

		// Guarda el cliente en la base de datos
		clienteService.save(cliente);

		// Marca la sesión como completa para que Spring MVC limpie la sesión
		status.setComplete();
		flash.addFlashAttribute("success", "Miembro registrado con éxito!");
		return "redirect:/listar";
	}

	/**
	 * Endpoint para cargar datos y mostrar el formulario de edición de un miembro.
	 * 
	 * Es importante guardar la membresia que viene como atributo del cliente.
	 * 
	 * @param id    Identificador único del miembro a editar.
	 * @param model Objeto Model para enviar datos a la vista.
	 * @param flash Objeto RedirectAttributes para agregar mensajes flash.
	 * @return Nombre de la vista a mostrar ("form_editar").
	 */
	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		Cliente cliente = null;
		Membresia membresia = null;
		if (id > 0) {
			cliente = clienteService.findById(id);
			membresia = cliente.getMembresia();
		} else {
			flash.addFlashAttribute("error", "El miembro solicitado no existe");
			return "redirect:/listar";
		}
		model.addAttribute("cliente", cliente);
		model.addAttribute("membresia", membresia);
		model.addAttribute("titulo", "Editar miembro");
		model.addAttribute("editMode", true); // O false según sea necesario
		model.addAttribute("tituloBoton", "Confirmar cambios");

		flash.addFlashAttribute("error", "Miembro actualizado con éxito!");

		return "form_editar";
	}

	/**
	 * Endpoint para procesar el formulario de actualización de un miembro.
	 * 
	 * Se debe controlar si hubo cambios en el DNI que es el id del cliente en la
	 * base de datos y puede causar errores. De ser así, se debe eliminar el cliente
	 * con el id pasado por parametro y guardar el nuevo cliente con los mismos
	 * datos y nuevo DNI(id)
	 * 
	 * EL problema se presenta cuando se tiene 2 miembros con el mismo membresia_id,
	 * ya que este debe ser unico para cada miembro.
	 * 
	 * @param id           Identificador único del miembro a actualizar.
	 * @param cliente      Objeto Cliente con los datos actualizados.
	 * @param result       Resultado de la validación de datos del cliente.
	 * @param resultMembre Resultado de la validación de datos de la membresía.
	 * @param model        Objeto Model para enviar datos a la vista.
	 * @param flash        Objeto RedirectAttributes para agregar mensajes flash.
	 * @param status       Objeto SessionStatus para gestionar el estado de la
	 *                     sesión.
	 * @return Redirección a la vista de lista de miembros ("redirect:/listar").
	 */
	@PostMapping("/form/{id}")
	public String update(@PathVariable(value = "id") Long id, @Valid Cliente cliente, BindingResult result,
			BindingResult resultMembre, Model model, RedirectAttributes flash, SessionStatus status) {
		// logica para que si el usuario edito el numero de dni
		// el sistema verifique si ha cambiado el dni para
		// que al guardar no se superponga el id de membresia en
		// 2 registros ya que arroja un error por el
		// oneToOne

		Cliente clienteExistente = clienteService.findById(id);
		cliente.setMembresia(clienteExistente.getMembresia());

		if (!cliente.getDni().equals(id)) {
			clienteService.delete(id);
		}

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Ingrese los datos nuevamente");
			model.addAttribute("tituloBoton", "Registrar miembro");
			model.addAttribute("editMode", true); // O false según sea necesario

			return "form_editar";
		}

		// Guarda el cliente en la base de datos
		clienteService.save(cliente);

		// Marca la sesión como completa para que Spring MVC limpie la sesión
		status.setComplete();
		flash.addFlashAttribute("success", "Miembro actualizado con éxito!");
		return "redirect:/listar";
	}

	/**
	 * Endpoint para eliminar un miembro por ID.
	 * 
	 * @param id    Identificador único del miembro a eliminar.
	 * @param flash Objeto RedirectAttributes para agregar mensajes flash.
	 * @return Redirección a la vista de lista de miembros ("redirect:/listar").
	 */
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			clienteService.deleteConMembresia(id);
			flash.addFlashAttribute("success", "Miembro eliminado con éxito!");
		}
		return "redirect:/listar";
	}

	/**
	 * Endpoint para cargar datos y mostrar el formulario de pago de membresía.
	 * 
	 * @param id    Identificador único del miembro para el pago de membresía.
	 * @param model Objeto Model para enviar datos a la vista.
	 * @param flash Objeto RedirectAttributes para agregar mensajes flash.
	 * @return Nombre de la vista a mostrar ("pago_membresia").
	 */
	@GetMapping("/pago/{id}")
	public String pagoMembresia(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		Cliente cliente = null;
		Membresia membresia = null;
		if (id > 0) {
			cliente = clienteService.findById(id);
			membresia = cliente.getMembresia();
		} else {
			flash.addFlashAttribute("error", "El miembro que deseas renovar no esta cargado en el sistema");
			return "redirect:/listar";
		}
		model.addAttribute("cliente", cliente);
		model.addAttribute("membresia", membresia);
		model.addAttribute("titulo", "Pago de membresía");
		model.addAttribute("tituloBoton", "Actualizar membresía");

		return "pago_membresia";
	}

	/**
	 * Endpoint para procesar el formulario de pago de membresía.
	 * 
	 * Actualiza la membresia existente.
	 * 
	 * @param id           Identificador único del miembro para el pago de
	 *                     membresía.
	 * @param cliente      Objeto Cliente con los datos actualizados.
	 * @param result       Resultado de la validación de datos del cliente.
	 * @param membresia    Objeto Membresia con los datos de la membresía
	 *                     actualizada.
	 * @param resultMembre Resultado de la validación de datos de la membresía.
	 * @param model        Objeto Model para enviar datos a la vista.
	 * @param flash        Objeto RedirectAttributes para agregar mensajes flash.
	 * @param status       Objeto SessionStatus para gestionar el estado de la
	 *                     sesión.
	 * @return Redirección a la vista de lista de miembros ("redirect:/listar").
	 */
	@PostMapping("/pago/{id}")
	public String pagoMembresia(@PathVariable(value = "id") Long id, @Valid Cliente cliente, BindingResult result,
			@Valid @ModelAttribute("membresia") Membresia membresia, BindingResult resultMembre, Model model,
			RedirectAttributes flash, SessionStatus status) {

		Cliente clienteExistente = clienteService.findById(id);
		Membresia membresiaExistente = clienteExistente.getMembresia();

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Error, por favor rellene los campos nuevamente");
			model.addAttribute("tituloBoton", "Actualizar membresía");
			return "pago_membresia";
		} else {
			if (resultMembre.hasErrors()) {
				model.addAttribute("titulo", "Error, por favor rellene los campos nuevamente");
				model.addAttribute("tituloBoton", "Actualizar membresía");
				return "pago_membresia";
			}
		}

		membresiaExistente.setFechaDeAlta(membresia.getFechaDeAlta());
		membresiaExistente.setPlanMensual(membresia.getPlanMensual());
		membresiaExistente.setVencimiento(membresia.getVencimiento());
		membresiaService.verificarMembresia(membresiaExistente.getId());

		// Guarda la membresía en la base de datos
		membresiaService.save(membresiaExistente);

		// Marca la sesión como completa para que Spring MVC limpie la sesión
		status.setComplete();
		flash.addFlashAttribute("success", "Membresía actualizada con éxito!");
		return "redirect:/listar";
	}

	/**
	 * Endpoint para listar los miembros con membresia activa paginados.
	 * 
	 * @param page  Número de página a mostrar.
	 * @param model Objeto Model para enviar datos a la vista.
	 * @return Nombre de la vista a mostrar ("listar").
	 */
	@GetMapping("/listar/activos")
	public String listarActivos(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		// membresiaService.verificarYActualizarMembresias();
		Pageable pageRequest = PageRequest.of(page, 10);

		Page<Cliente> clientes = clienteDao.clientesConMembresiaActiva(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar/activos", clientes);
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);

		long totalActivas = clienteDao.countClientesConMembresiaActiva();
		long totalVencidas = clienteDao.countClientesConMembresiaVencida();
		model.addAttribute("totalActivas", totalActivas);
		model.addAttribute("totalVencidas", totalVencidas);
		model.addAttribute("titulo", "Miembros de Full Gym");

		return "listar";
	}

	/**
	 * Endpoint para listar los miembros con membresía vencida paginados.
	 * 
	 * @param page  Número de página a mostrar.
	 * @param model Objeto Model para enviar datos a la vista.
	 * @return Nombre de la vista a mostrar ("listar").
	 */
	@GetMapping("/listar/vencidos")
	public String listarVencidos(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		// membresiaService.verificarYActualizarMembresias();
		Pageable pageRequest = PageRequest.of(page, 10);

		Page<Cliente> clientes = clienteDao.clientesConMembresiaVencida(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar/vencidos", clientes);
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);

		long totalActivas = clienteDao.countClientesConMembresiaActiva();
		long totalVencidas = clienteDao.countClientesConMembresiaVencida();
		model.addAttribute("totalActivas", totalActivas);
		model.addAttribute("totalVencidas", totalVencidas);
		model.addAttribute("titulo", "Miembros de Full Gym");

		return "listar";
	}

}
