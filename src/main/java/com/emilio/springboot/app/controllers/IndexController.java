package com.emilio.springboot.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("titulo", "Full Gym");
		return "index";
	}

	@GetMapping("/servicios")
	public String servicios(Model model) {
		model.addAttribute("titulo", "Servicios");
		return "servicios";
	}

	@GetMapping("/contacto")
	public String contacto(Model model) {
		model.addAttribute("titulo", "Contacto");
		return "contacto";
	}

}
