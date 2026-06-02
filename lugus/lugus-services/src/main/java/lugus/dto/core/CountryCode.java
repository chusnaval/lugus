package lugus.dto.core;

import java.util.HashMap;
import java.util.Map;

public enum CountryCode {

    ALBANIA("AL"),
    ARGENTINA("AR"),
    ARMENIA("AM"),
    ARUBA("AW"),
    AUSTRALIA("AU"),
    AUSTRIA("AT"),
    BAHAMAS("BS"),
    BAHRAIN("BH"),
    BELGIUM("BE"),
    BRAZIL("BR"),
    BULGARIA("BG"),
    CAMBODIA("KH"),
    CANADA("CA"),
    CHILE("CL"),
    CHINA("CN"),
    COLOMBIA("CO"),
    CZECH_REPUBLIC("CZ"),
    CZECHOSLOVAKIA("CSX"),
    DENMARK("DK"),
    DOMINICAN_REPUBLIC("DO"),
    EGYPT("EG"),
    ESTONIA("EE"),
    FINLAND("FI"),
    FRANCE("FR"),
    GAMBIA("GM"),
    GEORGIA("GE"),
    GERMANY("DE"),
    GREECE("GR"),
    GUATEMALA("GT"),
    HONG_KONG("HK"),
    HUNGARY("HU"),
    ICELAND("IS"),
    INDIA("IN"),
    INDONESIA("ID"),
    IRAN("IR"),
    IRELAND("IE"),
    ISRAEL("IL"),
    ITALY("IT"),
    JAMAICA("JM"),
    JAPAN("JP"),
    JORDAN("JO"),
    KAZAKHSTAN("KZ"),
    KENYA("KE"),
    LATVIA("LV"),
    LITHUANIA("LT"),
    LUXEMBOURG("LU"),
    MACAO("MO"),
    MALAYSIA("MY"),
    MALTA("MT"),
    MEXICO("MX"),
    MONACO("MC"),
    MOROCCO("MA"),
    NA("NA0"),
    NAMIBIA("NA"),
    NEPAL("NP"),
    NETHERLANDS("NL"),
    NEW_ZEALAND("NZ"),
    NORWAY("NO"),
    PAKISTAN("PK"),
    PANAMA("PA"),
    PARAGUAY("PY"),
    PERU("PE"),
    PHILIPPINES("PH"),
    POLAND("PL"),
    PORTUGAL("PT"),
    PUERTO_RICO("PR"),
    QATAR("QA"),
    ROMANIA("RO"),
    RUSSIA("RU"),
    SERBIA("RS"),
    SINGAPORE("SG"),
    SLOVAKIA("SK"),
    SOUTH_AFRICA("ZA"),
    SOUTH_KOREA("KR"),
    SOVIET_UNION("SUN"),
    SPAIN("ES"),
    SWEDEN("SE"),
    SWITZERLAND("CH"),
    SYRIA("SY"),
    TAIWAN("TW"),
    THAILAND("TH"),
    TUNISIA("TN"),
    TURKEY("TR"),
    UK("GB"),
    USA("US"),
    UKRAINE("UA"),
    UNITED_ARAB_EMIRATES("AE"),
    UNITED_KINGDOM("GB"),
    UNITED_STATES("US"),
    URUGUAY("UY"),
    VENEZUELA("VE"),
    VIETNAM("VN"),
    WEST_GERMANY("DDE"),
    YEMEN("YE"),
    YUGOSLAVIA("YUG"),
    ZAMBIA("ZM");

    private final String code;

    CountryCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    // ---------------------------
    // PARSEO ROBUSTO
    // ---------------------------

    private static final Map<String, CountryCode> LOOKUP = new HashMap<>();

    static {
        for (CountryCode c : values()) {
            // nombre del enum
            LOOKUP.put(c.name().toLowerCase(), c);

            // código ISO
            LOOKUP.put(c.code.toLowerCase(), c);
        }

        // Alias comunes de IMDb/OMDb
        LOOKUP.put("czech republic", CZECH_REPUBLIC);
        LOOKUP.put("czechoslovakia", CZECHOSLOVAKIA);
        LOOKUP.put("soviet union", SOVIET_UNION);
        LOOKUP.put("ussr", SOVIET_UNION);
        LOOKUP.put("west germany", WEST_GERMANY);
        LOOKUP.put("yugoslavia", YUGOSLAVIA);

        LOOKUP.put("hong kong", HONG_KONG);
        LOOKUP.put("macao", MACAO);
        LOOKUP.put("macau", MACAO);

        // UK / USA
        LOOKUP.put("uk", UNITED_KINGDOM);
        LOOKUP.put("united kingdom", UNITED_KINGDOM);

        LOOKUP.put("usa", UNITED_STATES);
        LOOKUP.put("united states", UNITED_STATES);
        LOOKUP.put("united states of america", UNITED_STATES);

        // N/A y variantes
        LOOKUP.put("n/a", NA);
        LOOKUP.put("unknown", NA);
        LOOKUP.put("", NA);
    }

    public static CountryCode fromString(String raw) {
        if (raw == null) return NA;

        String key = raw.trim().toLowerCase();

        return LOOKUP.getOrDefault(key, NA);
    }
}
