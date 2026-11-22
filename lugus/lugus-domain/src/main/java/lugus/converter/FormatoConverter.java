package lugus.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lugus.model.values.Format;

/**
 * Converter between {@link Format} y and its identifier (short) for database.
 */
@Converter(autoApply = true) 
public class FormatoConverter implements AttributeConverter<Format, Short> {

	@Override
	public Short convertToDatabaseColumn(final Format attribute) {
		return attribute == null ? null : attribute.getId();
	}

	@Override
	public Format convertToEntityAttribute(final Short dbData) {
		if (dbData == null) {
			return null;
		}
		return Format.getById(dbData);
	}
}