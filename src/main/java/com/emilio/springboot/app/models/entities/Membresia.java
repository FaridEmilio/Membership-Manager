package com.emilio.springboot.app.models.entities;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "membresias")
public class Membresia implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "plan_mensual_id")
	private Plan planMensual;

	@NotNull
	@Column(name = "estado_membresia")
	private boolean estadoMembresiaActiva;

	// Es decir, el cliente debe pagar el 20 de cada mes

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@Column(name = "fecha_alta")
	private LocalDate fechaDeAlta;

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@Column(name = "vencimiento")
	private LocalDate vencimiento;

	public Membresia() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Plan getPlanMensual() {
		return planMensual;
	}

	public void setPlanMensual(Plan planMensual) {
		this.planMensual = planMensual;
	}

	public boolean getEstadoMembresiaActiva() {
		return estadoMembresiaActiva;
	}

	public void setEstadoMembresiaActiva(boolean estadoMembresiaActiva) {
		this.estadoMembresiaActiva = estadoMembresiaActiva;
	}

	public LocalDate getFechaDeAlta() {
		return fechaDeAlta;
	}

	public void setFechaDeAlta(LocalDate fechaDeAlta) {
		this.fechaDeAlta = fechaDeAlta;
	}

	public LocalDate getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(LocalDate vencimiento) {
		this.vencimiento = vencimiento;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
