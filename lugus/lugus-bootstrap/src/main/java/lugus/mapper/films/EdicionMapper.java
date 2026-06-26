package lugus.mapper.films;

import lugus.dto.core.FormatDTO;
import lugus.dto.films.ConditionDto;
import lugus.dto.films.EditionDto;
import lugus.dto.films.PackDto;
import lugus.model.films.Edicion;

public class EdicionMapper {

	public static EditionDto toDto(Edicion edicion) {
		EditionDto dto = new EditionDto();
		dto.setId(edicion.getId());
		dto.setMgmtCode(edicion.getCodigo());
		if(edicion.getPack()!=null) {
			dto.setPack(new PackDto(edicion.getPack().getId(), edicion.getPack().getTitulo()));
		}
		

		if (edicion.getEstado() != null) {
			dto.setCondition(new ConditionDto(edicion.getEstado().getId(), edicion.getEstado().getName()));
		}

		if (edicion.getFormato() != null) {
			dto.setFormat(new FormatDTO("" + edicion.getFormato().getId(), edicion.getFormato().name()));
		}
		

		if (edicion.getLocation() != null) {
			dto.setLocation(edicion.getLocation().getDescripcion());
		}

		dto.setSteelbook(edicion.isSteelbook());
		dto.setSlipcover(edicion.isFunda());
		dto.setOwned(edicion.isComprado());
		dto.setNotes(edicion.getNotas());
		
		return dto;
	}

}
