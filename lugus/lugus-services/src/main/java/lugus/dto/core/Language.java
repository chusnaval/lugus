package lugus.dto.core;

import java.util.HashMap;
import java.util.Map;

public enum Language {
	ENGLISH("en"), SPANISH("es"), NA("na");

	private final String code;

	Language(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	private static final Map<String, Language> LOOKUP = new HashMap<>();

	static {

		// Alias comunes de IMDb/OMDb
		LOOKUP.put("eng", ENGLISH);
		LOOKUP.put("spa", SPANISH);
	}

	public static Language fromString(String raw) {
		if (raw == null)
			return NA;

		String key = raw.trim().toLowerCase();

		return LOOKUP.getOrDefault(key, NA);
	}
}
