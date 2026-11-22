package lugus.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lugus.model.values.Genere;

/**
 * Converter between {@link Genere} and its identifier (String) to database.
 */
@Converter(autoApply = true)
public class GeneroConverter implements AttributeConverter<Genere, String> {

	@Override
	public String convertToDatabaseColumn(final Genere attribute) {
		return attribute == null ? null : attribute.getCodigo(); 
	}

	@Override
	public Genere convertToEntityAttribute(final String dbData) {
		if (dbData == null) {
			return null;
		}
		// Buscamos el enum cuyo código coincida
		return Genere.getById(dbData);
	}
}