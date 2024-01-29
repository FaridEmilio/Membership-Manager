/**
 * Clase de configuración de Spring Security que define reglas de seguridad
 *  para las solicitudes HTTP.
 */

package com.emilio.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.emilio.springboot.app.auth.handler.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
	
	@Autowired
	private LoginSuccessHandler successHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * este método crea un bean UserDetailsService basado en
	 * InMemoryUserDetailsManager, que contiene un único usuario con nombre "admin",
	 * contraseña "admin" y el rol "ADMIN". Este bean se utiliza luego en la
	 * configuración de Spring Security para gestionar la autenticación y la
	 * autorización de los usuarios en la aplicación.
	 * 
	 * @return la instancia de InMemoryUserDetailsManager configurada con el usuario
	 *         "admin"
	 * @throws Exception
	 */
	@Bean
	public UserDetailsService userDetailsService() throws Exception {

		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build());
        return manager;
	}

	/**
	 * Metodo que configura las reglas de seguridad para las solicitudes HTTP.
	 * 
	 * @param http El objeto HttpSecurity que se configura con reglas de seguridad.
	 * @return Un objeto SecurityFilterChain que representa la configuración de
	 *         seguridad.
	 * @throws Exception Si hay un error al configurar la seguridad.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// Configuración para autorizar ciertas rutas sin autenticación y
		// requerir autenticación para el resto.
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/servicios", "/contacto", "/js/**", "/css/**", "/img/**").permitAll()
				.anyRequest().authenticated());

		// Configuración del formulario de inicio de sesión personalizado y
		// permitir acceso a la página de inicio de sesión
		http.formLogin(fL -> fL.successHandler(successHandler).defaultSuccessUrl("/listar", true).loginPage("/login").permitAll());

		// Configuración para manejar la funcionalidad de cierre de sesión
		http.logout(lOut -> {
			lOut.invalidateHttpSession(true).clearAuthentication(true)
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
					.permitAll();
		});

		// Habilita la autenticación básica de HTTP con configuraciones predeterminadas.
		http.httpBasic(Customizer.withDefaults());

		// Retorna el objeto SecurityFilterChain construido con la configuración
		// proporcionada.
		return http.build();
	}

}