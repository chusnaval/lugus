package lugus.dto.core;

import java.io.Serializable;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lugus.model.values.Format;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterDto implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1L;

	private String text;

	private String title;

	private Integer fromYear;

	private Integer toYear;

	private Boolean pack;

	private Integer format;

	private Boolean steelbook;

	private Boolean slipCover;

	private Boolean purchased;

	private Boolean complete;

	private Boolean hasCover;

	private String genre;

	private String location;

	private String notes;

	private String director;

	private String actor;

	private int pageSize = 30;

	private transient Optional<Integer> page = Optional.of(0);

	private transient Optional<String> order = Optional.empty();

	private transient Optional<String> direccion = Optional.empty();

	public static FilterDto reset(String field) {
		FilterDto filtro = new FilterDto();
		filtro.setOrder(Optional.of(field));
		filtro.setPack(false);
		return filtro;
	}

	public static FilterDto buildFilter(Integer page, Integer size, Boolean owned, String title, String casting,
			Integer fromYear, Integer toYear, String format, String genre, Boolean pack, Boolean complete, String sort,
			String sortDirection) {
		FilterDto filtro = new FilterDto();
		if (page != null) {
			filtro.setPage(Optional.of(page));
		}
		if (title != null) {
			filtro.setTitle(title);
		}
		if (casting != null) {
			filtro.setActor(casting);
			filtro.setDirector(casting);
		}
		if (fromYear != null) {
			filtro.setFromYear(fromYear);
		}
		if (toYear != null) {
			filtro.setToYear(toYear);
		}
		if (format != null) {
			if (Format.getById(Short.valueOf(format)) == null) {
				throw new IllegalArgumentException("Formato no válido: " + format);
			}
			filtro.setFormat((int) Format.getById(Short.valueOf(format)).getId());
		}
		if (genre != null) {
			filtro.setGenre(genre);
		}
		if (pack != null) {
			filtro.setPack(pack);
		}
		if (complete != null) {
			filtro.setComplete(complete);
		}
		if (sort != null) {
			filtro.setOrder(Optional.of(sort));
		}
		if (sortDirection != null) {
			filtro.setDireccion(Optional.of(sortDirection));
		}
		if (owned != null) {
			filtro.setPurchased(owned);
		}
		int sizeAux = size;
		if (size == null || size <= 0) {
			sizeAux = Integer.MAX_VALUE;
		}
		filtro.setPageSize(sizeAux);
		return filtro;
	}

}
