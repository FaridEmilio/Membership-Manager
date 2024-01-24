/**
 * Clase que representa un plan en el sistema.
 * Cada plan define un conjunto de características como nombre, duración en días y precio.
 * Esta clase se utiliza en la capa de persistencia para mapear la entidad "planes" en la base de datos.
 * 
 * @author Emilio Barrios
 * @version 1.0
 * @since 15-01-2024
 */


package com.emilio.springboot.app.models.entities;
import java.io.Serializable;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "planes")
public class Plan implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty
	private String nombre;
	
	@NotNull
	private int dias;
	
	@NotNull
	private int precio;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@OneToMany(mappedBy = "planMensual")  // Nombre del campo en la clase Membresia que mapea la relación
	 private Set<Membresia> membresias;

	/**
	 * Constructor vacío necesario para JPA.
	 */
	public Plan() {
	}

	/**
     * Constructor que inicializa los atributos de un plan.
     * 
     * @param id     Identificador único del plan.
     * @param nombre Nombre del plan.
     * @param dias   Duración en días del plan.
     * @param precio Precio del plan.
     */
	public Plan(Integer id, String nombre, int dias, int precio) {
		this.id = id;
		this.nombre = nombre;
		this.dias = dias;
		this.precio = precio;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getDias() {
		return dias;
	}

	public void setDias(int dias) {
		this.dias = dias;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public Set<Membresia> getMembresias() {
		return membresias;
	}

	public void setMembresias(Set<Membresia> membresias) {
		this.membresias = membresias;
	}
	
}
