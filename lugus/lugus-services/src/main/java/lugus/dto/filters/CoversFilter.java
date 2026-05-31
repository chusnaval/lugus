package lugus.dto.filters;

public record CoversFilter(String missing, // "yes" o "no"
		String source, // "FA", "IMDB", etc.
		String title) {

}
