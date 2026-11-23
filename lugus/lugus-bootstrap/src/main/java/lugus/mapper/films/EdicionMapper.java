package lugus.mapper.films;

import lugus.dto.core.FormatDTO;
import lugus.dto.films.ConditionDto;
import lugus.dto.films.EditionDto;
import lugus.dto.films.PackDto;
import lugus.model.films.Edition;

public class EdicionMapper {

	public static EditionDto toDto(Edition edicion) {
		EditionDto dto = new EditionDto();
		dto.setId(edicion.getId());
		dto.setMgmtCode(edicion.getCode());
		if(edicion.getPack()!=null) {
			dto.setPack(new PackDto(edicion.getPack().getId(), edicion.getPack().getTitle()));
		}
		

		if (edicion.getStatus() != null) {
			dto.setCondition(new ConditionDto(edicion.getStatus().getId(), edicion.getStatus().getName()));
		}

		if (edicion.getFormat() != null) {
			dto.setFormat(new FormatDTO("" + edicion.getFormat().getId(), edicion.getFormat().name()));
		}
		

		if (edicion.getLocation() != null) {
			dto.setLocation(edicion.getLocation().getDescription());
			dto.setLocationCode(edicion.getLocation().getCode());
		}

		dto.setSteelbook(edicion.isSteelbook());
		dto.setSlipcover(edicion.isSlipCover());
		dto.setOwned(edicion.isPurchased());
		dto.setNotes(edicion.getNotes());
		
		return dto;
	}

}
