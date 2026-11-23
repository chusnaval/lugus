package lugus.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lugus.model.values.Genre;

/**
 * Converter between {@link Genre} and its identifier (String) to database.
 */
@Converter(autoApply = true)
public class GenreConverter implements AttributeConverter<Genre, String> {

	@Override
	public String convertToDatabaseColumn(final Genre attribute) {
		return attribute == null ? null : attribute.getCodigo(); 
	}

	@Override
	public Genre convertToEntityAttribute(final String dbData) {
		if (dbData == null) {
			return null;
		}
		
		return Genre.getById(dbData);
	}
}