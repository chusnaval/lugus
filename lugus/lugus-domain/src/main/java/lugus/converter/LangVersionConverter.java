package lugus.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lugus.model.values.LangVersion;

@Converter(autoApply = true)
public class LangVersionConverter implements AttributeConverter<LangVersion, String> {

	@Override
	public String convertToDatabaseColumn(final LangVersion attribute) {
		return attribute == null ? null : attribute.name();
	}

	@Override
	public LangVersion convertToEntityAttribute(final String dbData) {
		if (dbData == null) {
			return null;
		}
		return LangVersion.valueOf(dbData);
	}

}
