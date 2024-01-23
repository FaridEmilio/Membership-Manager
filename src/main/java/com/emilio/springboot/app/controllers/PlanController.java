package com.emilio.springboot.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.emilio.springboot.app.models.entities.Plan;
import com.emilio.springboot.app.models.service.IPlanService;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("plan")
public class PlanController {

	@Autowired
	private IPlanService planService;

	@GetMapping("/planes")
	public String listar(Model model) {

		List<Plan> planes = planService.findAll();
		model.addAttribute("titulo", "Planes actuales");
		model.addAttribute("planes", planes);

		return "listar_planes";
	}

	@GetMapping("/planes/nuevo_plan")
	public String crearPlan(Model model) {

		Plan plan = new Plan();

		model.addAttribute("plan", plan);
		model.addAttribute("titulo", "Nuevo Plan");
		model.addAttribute("tituloBoton", "Crear plan");

		return "form_plan";

	}

	@PostMapping("/planes/nuevo_plan")
	public String guardarPlan(@Valid Plan plan, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Ingrese los datos del plan nuevamente");
			model.addAttribute("tituloBoton", "Crear plan");

			return "form_plan";
		}

		planService.save(plan);

		// Marca la sesión como completa para que Spring MVC limpie la sesión
		status.setComplete();
		flash.addFlashAttribute("success", "Nuevo plan registrado con éxito!");
		return "redirect:/planes";

	}

	@GetMapping("planes/editar/{id}")
	public String editar(@PathVariable(value = "id") Integer id, Model model, RedirectAttributes flash) {

		Plan plan = new Plan();

		if (id > 0) {
			plan = planService.findById(id);
		} else {
			flash.addFlashAttribute("error", "El plan solicitado no existe");
			return "redirect:/planes";
		}

		model.addAttribute("plan", plan);
		model.addAttribute("titulo", "Editar plan");
		model.addAttribute("tituloBoton", "Confirmar cambios");

		return "form_plan_editar";

	}

	@PostMapping("planes/editar/{id}")
	public String update(@PathVariable(value = "id") Integer id, @Valid Plan plan, BindingResult result, Model model,
			RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Ingrese los datos nuevamente");
			model.addAttribute("tituloBoton", "Confirmar cambios");

			return "form_plan_editar";

		}

		planService.save(plan);
		status.setComplete();
		flash.addFlashAttribute("success", "Plan actualizado con éxito!");
		return "redirect:/planes";
	}

	@GetMapping("/planes/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Integer id, RedirectAttributes flash) {
		if (id > 0) {
			planService.delete(id);
			flash.addFlashAttribute("success", "Plan eliminado");
		}
		return "redirect:/planes";
	}
}