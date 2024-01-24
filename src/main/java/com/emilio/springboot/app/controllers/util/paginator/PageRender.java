/**
 * Clase utilitaria para la renderización de la paginación de resultados.
 * Permite generar una lista de ítems de página para la navegación entre páginas.
 * 
 * @param <T> Tipo de elemento contenido en la página.
 * @version 1.0
 * @since 22-01-2024
 */

package com.emilio.springboot.app.controllers.util.paginator;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

public class PageRender<T> {

	private String url;
	private Page<T> page;
	private int totalPaginas;
	private int numElementosPorPagina;
	private int paginaActual;
	private List<PageItem> paginas;

	/**
	 * Constructor de la clase PageRender. Logica: Calcula y genera una lista de
	 * ítems de página utilizados en la navegación de la paginación de resultados.
	 * 
	 * Primero calcula los límites de las páginas a mostrar en la paginación en
	 * función del número total de páginas y la posición de la página actual. Luego,
	 * genera una lista de objetos PageItem que representan los ítems de página
	 * necesarios para la navegación entre esas páginas
	 * 
	 * @param url  URL base para la paginación.
	 * @param page Objeto Page que contiene los resultados y la información de
	 *             paginación.
	 */
	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		this.paginas = new ArrayList<PageItem>();
		numElementosPorPagina = page.getSize();
		totalPaginas = page.getTotalPages();
		paginaActual = page.getNumber() + 1;

		int desde, hasta;
		if (totalPaginas <= numElementosPorPagina) {
			desde = 1;
			hasta = totalPaginas;
		} else {
			if (paginaActual <= numElementosPorPagina) {
				desde = 1;
				hasta = numElementosPorPagina;
			} else {
				if (paginaActual >= totalPaginas - numElementosPorPagina / 2) {
					desde = totalPaginas - numElementosPorPagina + 1;
					hasta = numElementosPorPagina;
				} else {
					desde = paginaActual - numElementosPorPagina / 2;
					hasta = numElementosPorPagina;
				}
			}
		}

		for (int i = 0; i < hasta; i++) {
			paginas.add(new PageItem(desde + i, paginaActual == desde + i));
		}

	}

	public String getUrl() {
		return url;
	}

	public Page<T> getPage() {
		return page;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public int getNumElementosPorPagina() {
		return numElementosPorPagina;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}

	/**
	 * Verifica si la página actual es la primera.
	 * 
	 * @return true si la página actual es la primera, false de lo contrario.
	 */
	public boolean isFirst() {
		return page.isFirst();
	}

	/**
	 * Verifica si la página actual es la última.
	 * 
	 * @return true si la página actual es la última, false de lo contrario.
	 */
	public boolean isLast() {
		return page.isLast();
	}

	/**
	 * Verifica si hay una página siguiente.
	 * 
	 * @return true si hay una página siguiente, false de lo contrario.
	 */
	public boolean isHasNext() {
		return page.hasNext();
	}

	/**
	 * Verifica si hay una página anterior.
	 * 
	 * @return true si hay una página anterior, false de lo contrario.
	 */
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
}
