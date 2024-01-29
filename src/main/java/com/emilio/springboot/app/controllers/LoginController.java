package com.emilio.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
	
	@GetMapping("/login")
	public String login(@RequestParam(value ="error", required = false) String error, Model model, Principal principal, RedirectAttributes flash) {
		
		// Si principal es distinto de null, significa que ya ha iniciado sesión antes
		if(principal != null) {
			flash.addFlashAttribute("info", "Ya has iniciado sesión en el sistema");
			return "redirect:/listar";
		}
		if(error!=null) {
			model.addAttribute("error", "Error: Usuario o contraseña incorrecta, por favor vuelva a intentarlo");
		}
		
		model.addAttribute("titulo", "Iniciar Sesión");
		return "login";
	}
	

}
