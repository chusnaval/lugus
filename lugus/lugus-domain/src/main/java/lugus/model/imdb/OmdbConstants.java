package lugus.model.imdb;

public final class OmdbConstants {

	private OmdbConstants() {
		
	}
	
	public static final String OMDB_API_URL = "https://www.omdbapi.com/?i=";
	public static final String API_KEY_PARAM = "&apikey=";
	public static final String PLOT_PARAM = "&plot=full";
	
	public static final String FIELD_PLOT = "Plot";
	public static final String COUNTRY_PLOT = "Country";
	public static final String POSTER = "Poster";
	public static final String DURATION = "Runtime";
}
