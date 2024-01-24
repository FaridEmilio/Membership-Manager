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

	@GetMapping("/listar")
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			@Param("keyWord") String keyWord) {
		
		//membresiaService.verificarYActualizarMembresias();
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

	@ModelAttribute("listaPlanes")
	public List<Plan> listaPlanes() {
		return planService.findAll();
	}

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

	// Método para eliminar un cliente por ID
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			clienteService.deleteConMembresia(id);
			flash.addFlashAttribute("success", "Miembro eliminado con éxito!");
		}
		return "redirect:/listar";
	}

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

}
