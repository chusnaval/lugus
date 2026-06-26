package lugus.service.films;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import lugus.model.films.Edicion;
import lugus.model.values.Formato;
import lugus.repository.films.EdicionRepository;


@Service
@RequiredArgsConstructor
public class EdicionService {

	private final EdicionRepository edicionRepository;
	
	public void calculateCodeSuffix(Edicion p) {
		// we must find if the code starts exists in not pack films,
		// and if not exists we add "-1" to the code,
		// and if exists we add "-2", and so on, until we find a code that not exists
		boolean codeExists = true;
		int suffix = 1;
		String baseCode = p.getCodigo();
		while (codeExists) {
			if (edicionRepository.existsByCodigo(p.getCodigo())) {
				p.setCodigo(baseCode + "-" + suffix);
				suffix++;
			} else {
				codeExists = false;
			}
		}
	}
	
	public int addedInLastDays(int days) {
		Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);
		return edicionRepository.countByTsAltaAfter(limit);
	}

	public long contarTodasCompradas() {
		return edicionRepository.countByComprado(true);
	}
	public int contarPorFormato(Formato format) {
		return edicionRepository.countByFormatoAndComprado(format, true);
	}

	public int contarNoCompradas() {
		return edicionRepository.countByComprado(false);
	}

	public Edicion findById(int id) {
		return edicionRepository.findById(id);
	}
}
