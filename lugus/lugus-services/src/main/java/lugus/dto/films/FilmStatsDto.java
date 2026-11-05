package lugus.dto.films;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmStatsDto {
	private long totalFilms;
	private int recentFilms;
	private int completeGroups;
	private int incompleteGroups;
	private int vhs;
	private int dvd;
	private int bluray;
	private int uhd;
	private int digital;
	private int notOwned;

	private FilmCategoryDto numerosPorCategoria;

	private FilmGenreDto numerosPorGenero;

	public void setAnimacion(final int number) {
		this.numerosPorGenero.setAnimacion(number);
	}

	public void setAnime(final int number) {
		this.numerosPorGenero.setAnime(number);
	}

	public void setInfantil(final int number) {
		this.numerosPorGenero.setInfantil(number);
	}

	public void setMusical(final int number) {
		this.numerosPorGenero.setMusical(number);
	}

	public void setNavidena(final int number) {
		this.numerosPorGenero.setNavidena(number);
	}

	public void setDrama(final int number) {
		this.numerosPorGenero.setDrama(number);
	}

	public void setRomantica(final int number) {
		this.numerosPorGenero.setRomantica(number);
	}

	public void setComedia(final int number) {
		this.numerosPorGenero.setComedia(number);
	}

	public void setCienciaFiccion(final int number) {
		this.numerosPorGenero.setCienciaFiccion(number);
	}

	public void setAccion(final int number) {
		this.numerosPorGenero.setAccion(number);
	}

	public void setAventura(final int number) {
		this.numerosPorGenero.setAventura(number);
	}

	public void setFantasia(final int number) {
		this.numerosPorGenero.setFantasia(number);
	}

	public void setThriller(final int number) {
		this.numerosPorGenero.setThriller(number);
	}

	public void setMisterio(final int number) {
		this.numerosPorGenero.setMisterio(number);
	}

	public void setCrimen(final int number) {
		this.numerosPorGenero.setCrimen(number);
	}

	public void setTerror(final int number) {
		this.numerosPorGenero.setTerror(number);
	}

	public void setBelico(final int number) {
		this.numerosPorGenero.setBelico(number);
	}

	public void setWestern(final int number) {
		this.numerosPorGenero.setWestern(number);
	}

	public void setDocumental(final int number) {
		this.numerosPorGenero.setDocumental(number);
	}

	public void setArteEntretenimiento(final int number) {
		this.numerosPorCategoria.setArteEntretenimiento(number);
	}
	
	public void setLiteraturaNarrativa(final int number) {
		this.numerosPorCategoria.setLiteraturaNarrativa(number);
	}
	
	public void setCienciaFiccionCategory(final int number) {
		this.numerosPorCategoria.setCienciaFiccion(number);
	}
	
	public void setAccionCategory(final int number) {
		this.numerosPorCategoria.setAccion(number);
	}
	
	public void setMisterioCategory(final int number) {
		this.numerosPorCategoria.setMisterio(number);
	}
	
	public void setTerrorCategory(final int number) {
		this.numerosPorCategoria.setTerror(number);
	}
	
	public void setConflicto(final int number) {
		this.numerosPorCategoria.setConflicto(number);
	}
	
	public void setDocumentalCategory(final int number) {
		this.numerosPorCategoria.setDocumental(number);
	}
}
