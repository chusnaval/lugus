package lugus.dto.user;

import java.util.List;

import lugus.dto.core.GenreDTO;

public record UserPreferencesDTO(List<GenreDTO> favoritos) {
}
