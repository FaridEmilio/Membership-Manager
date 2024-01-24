/**
 * Clase utilizada en la paginación de resultados.
 * Representa un ítem de página con un número y un indicador de si es la página actual.
 * 
 * @author Emilio Barrios
 * @version 1.0
 * @since 20-01-2024
 */

package com.emilio.springboot.app.controllers.util.paginator;

public class PageItem {

	private int numero;
	private boolean actual;

	public PageItem(int numero, boolean actual) {
		this.numero = numero;
		this.actual = actual;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public boolean isActual() {
		return actual;
	}

	public void setActual(boolean actual) {
		this.actual = actual;
	}
}
