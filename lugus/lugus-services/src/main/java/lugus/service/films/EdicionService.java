package lugus.service.films;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.model.values.Format;
import lugus.repository.films.EditionRepository;


@Service
@RequiredArgsConstructor
public class EdicionService {

	private final EditionRepository edicionRepository;
		
	public int addedInLastDays(final int days) {
		Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);
		return edicionRepository.countByTsCreationAfter(limit);
	}

	public long countAllPurchased() {
		return edicionRepository.countByPurchased(true);
	}
	public int countByFormat(Format format) {
		return edicionRepository.countByFormatAndPurchased(format, true);
	}

	public int countNotPurchased() {
		return edicionRepository.countByPurchased(false);
	}

	public Edition findById(final int id) {
		return edicionRepository.findById(id);
	}


	public String calculateCompleteCode(final int idEdition, final Film film) {
		String filmCode =  film.obtainCode();
		
		return calculateCodeWithSuffix(idEdition, filmCode);
	}
	
	private String calculateCodeWithSuffix(final int idEdition, final String filmCode) {
		// we must find if the code starts exists in not pack films,
		// and if not exists we add "-1" to the code,
		// and if exists we add "-2", and so on, until we find a code that not exists
		boolean codeExists = true;
		int suffix = 1;
		DecimalFormat df = new DecimalFormat("000");
		String suffixText =  df.format(suffix);
		String completeCode = filmCode  + "-" + suffixText;
		
		while (codeExists) {
			if (edicionRepository.existsByCodeAndIdNot(completeCode, idEdition)) {
				suffix++;
				suffixText =  df.format(suffix);
				completeCode = filmCode  + "-" + suffixText;
			} else {
				codeExists = false;
			}
		}
		
		return completeCode;
	}

	public void update(final Edition edition) {
		edicionRepository.save(edition);
		
	}

	
}
