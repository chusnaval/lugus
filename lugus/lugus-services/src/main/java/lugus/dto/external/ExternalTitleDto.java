package lugus.dto.external;

public record ExternalTitleDto(
	    String tconst,
	    String title,
	    String originalTitle,
	    String type,
	    String startYear,
	    String endYear,
	    String runtimeMinutes,
	    String genres,
	    String rating,
	    String votes,
	    String poster,
	    String plot,
	    String director,
	    String actors
	) {}
