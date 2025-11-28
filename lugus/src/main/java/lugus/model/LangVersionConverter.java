package lugus.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LangVersionConverter implements AttributeConverter<LangVersion, String> {

	@Override
	public String convertToDatabaseColumn(LangVersion attribute) {
		return attribute == null ? null : attribute.name();
	}

	@Override
	public LangVersion convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return LangVersion.valueOf(dbData);
	}

}
