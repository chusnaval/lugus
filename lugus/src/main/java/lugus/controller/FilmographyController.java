package lugus.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.PermisoException;
import lugus.model.Filmography;
import lugus.model.ImdbTitleAkas;
import lugus.model.ImdbTitleBasics;
import lugus.model.ImdbTitlePrincipals;
import lugus.model.PeliculasOtros;
import lugus.model.Persona;
import lugus.service.ImdbTitleAkasService;
import lugus.service.ImdbTitleBasicsService;
import lugus.service.ImdbTitlePrincipalsService;
import lugus.service.PeliculasOtrosService;
import lugus.service.PersonaService;

@Controller
@RequestMapping("/filmography")
@RequiredArgsConstructor
public class FilmographyController {

	private final ImdbTitlePrincipalsService imdbTitlePrincipalsService;

	private final PersonaService personaService;

	private final ImdbTitleBasicsService imdbTitleBasicsService;

	private final PeliculasOtrosService peliculasOtrosService;
	
	private final ImdbTitleAkasService imdbTitleAkasService;

	@GetMapping("/{id}")
	public String detail(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {

		Persona person = personaService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));

		model.addAttribute("person", person);

		List<Filmography> films = new ArrayList<Filmography>();
		List<ImdbTitlePrincipals> aux = imdbTitlePrincipalsService.findAllByIdNconst(person.getNconst());
		for (ImdbTitlePrincipals itp : aux) {

			Optional<ImdbTitleBasics> itb = imdbTitleBasicsService.findById(itp.getId().getTconst());
			if (itb.isPresent() && itb.get().getStartyear() != null
					&& ("movie".equals(itb.get().getTitletype()) || "tvMovie".equals(itb.get().getTitletype()))) {

				Filmography film = new Filmography();
				film.setId(id);
				film.setNconst(person.getNconst());
				film.setCategory(itp.getId().getCategory());
				film.setFcharacters(itp.getCharacters());

				film.setStartyear(Integer.parseInt(itb.get().getStartyear()));
				film.setTconst(itp.getId().getTconst());

				List<PeliculasOtros> po = peliculasOtrosService.findByImdbId(itp.getId().getTconst());
				if (!po.isEmpty()) {
					film.setPeliculaId(po.get(0).getPelicula().getId());
				}

				Optional<ImdbTitleAkas> ita = imdbTitleAkasService.findByTitleId(itp.getId().getTconst());
				if(ita.isPresent()) {
					film.setTitle(ita.get().getTitle());
				}else {
					film.setTitle(itb.get().getOriginaltitle());
				}

				films.add(film);
			}
		}
		Collections.sort(films, (o1, o2) -> o1.getStartyear().compareTo(o2.getStartyear()));

		model.addAttribute("films", films);

		return "filmography/detail";
	}

}
