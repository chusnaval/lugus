package lugus.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.PermisoException;
import lugus.model.people.Filmography;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculasOtros;
import lugus.model.people.Persona;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.service.ImdbTitleAkasService;
import lugus.service.ImdbTitleBasicsService;
import lugus.service.ImdbTitlePrincipalsService;
import lugus.service.PeliculaService;
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

	private final PeliculaService peliculaService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/{id}")
	public String detail(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {

		Persona person = personaService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));

		model.addAttribute("person", person);
		Map<String, Filmography> mapFilms = new HashMap<>();
		List<ImdbTitlePrincipals> aux = imdbTitlePrincipalsService.findAllByIdNconst(person.getNconst());
		for (ImdbTitlePrincipals itp : aux) {

			Optional<ImdbTitleBasics> itb = imdbTitleBasicsService.findById(itp.getId().getTconst());
			if (itb.isPresent() && itb.get().getStartyear() != null
					&& ("movie".equals(itb.get().getTitletype()) || "tvMovie".equals(itb.get().getTitletype()))) {

				Filmography film = mapFilms.get(itp.getId().getTconst());

				if (film == null) {

					film = new Filmography();
					film.setId(id);
					film.setNconst(person.getNconst());
					film.setCategory(itp.getId().getCategory());

					film.setStartyear(Integer.parseInt(itb.get().getStartyear()));
					film.setTconst(itp.getId().getTconst());

					List<PeliculasOtros> po = peliculasOtrosService.findByImdbId(itp.getId().getTconst());
					if (!po.isEmpty()) {
						film.setPeliculaId(po.get(0).getPelicula().getId());

						Optional<Pelicula> pel = peliculaService.findById(po.get(0).getPelicula().getId());
						if (pel.isPresent()) {
							film.setComprado(pel.get().isComprado());
							film.setBuscado(!pel.get().isComprado());
						}
					}

					Optional<ImdbTitleAkas> ita = imdbTitleAkasService.findByTitleId(itp.getId().getTconst());
					if (ita.isPresent()) {
						film.setTitle(ita.get().getTitle());
					} else {
						film.setTitle(itb.get().getOriginaltitle());
					}

					mapFilms.put(itp.getId().getTconst(), film);
				} else {
					if (film.getCategory() != null && !film.getCategory().contains(itp.getId().getCategory())) {
						film.setCategory(film.getCategory() + " - " + itp.getId().getCategory());
					}
				}

			}
		}
		List<Filmography> films = new ArrayList(mapFilms.values());
		Collections.sort(films, (o1, o2) -> o1.getStartyear().compareTo(o2.getStartyear()));

		model.addAttribute("films", films);

		return "filmography/detail";
	}

}
