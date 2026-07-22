package lugus.dto.films;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmCategoryDto {

	private int arteEntretenimiento;
	private int literaturaNarrativa;
	private int cienciaFiccion;
	private int accion;
	private int misterio;
	private int terror;
	private int conflicto;
	private int documental;

}
