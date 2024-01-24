/**
 * Clase que representa un cliente de un gimnasio con una membresía asociada.
 * Esta clase se utiliza en la capa de persistencia para mapear la entidad "clientes" en la base de datos.
 * 
 * @author Emilio Barrios
 * @version 1.0
 * @since 15-01-2024
 */

package com.emilio.springboot.app.models.entities;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

	// Aca podemos validar diciendo que tenga un patron es decir aaa.111.q se
	// escribe
	// @Pattern(regexp = "[a-z]{3}[.][\\d]{3}[.][a-z]{1}")
	@NotNull
	@Digits(integer = 8, fraction = 0)
	@Id
	private Long dni;


	@Column(name = "nombre")
	@NotEmpty
	@Size(min = 3, max = 20)
	private String nombre;

	
	@Column(name = "apellido")
	@NotEmpty
	@Size(min = 3, max = 20)
	private String apellido;

	
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@Column(name = "fecha_inicio")
	private LocalDate fechaInicio;

	
	@OneToOne
	@JoinColumn(name = "membresia_id")
	private Membresia membresia;

	@NotEmpty
	private String genero;

	@Column(name = "cel")
	private Long numeroCelular;

	@Column(name = "cel_emergen")
	private Long numeroCelularEmergencia;

	public Long getDni() {
		return dni;
	}

	public Cliente() {
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public void setDni(Long dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Membresia getMembresia() {
		return membresia;
	}

	public void setMembresia(Membresia membresia) {
		this.membresia = membresia;
	}

	public Long getNumeroCelular() {
		return numeroCelular;
	}

	public void setNumeroCelular(Long numeroCelular) {
		this.numeroCelular = numeroCelular;
	}

	public Long getNumeroCelularEmergencia() {
		return numeroCelularEmergencia;
	}

	public void setNumeroCelularEmergencia(Long numeroCelularEmergencia) {
		this.numeroCelularEmergencia = numeroCelularEmergencia;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1L;
	
}
