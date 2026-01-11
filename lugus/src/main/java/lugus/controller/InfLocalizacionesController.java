package lugus.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.model.inf.InfLocalizaciones;
import lugus.service.InfLocalizacionesService;
import lugus.service.UtlLocalizacionesService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class InfLocalizacionesController {

	private static final int LIMITE_POR_CARPETA = 28;
	private static final int LIMITE_POR_ESTANTERIA = 60;

	private final InfLocalizacionesService infLocalizacionesService;

	private final UtlLocalizacionesService utlLocalizacionesService;

	@PostMapping("/utl/ubics/calc")
	public ResponseEntity<String> findByGeneroAndFormato(@RequestParam final String codigo,
			@RequestParam final String genero, @RequestParam final Integer formato, @RequestParam final Boolean funda,
			@RequestParam final Boolean steelbook) {
		String resultado = "";
		List<InfLocalizaciones> posibilidades = new ArrayList<InfLocalizaciones>();
		if(formato <= 1 || formato == 4) {
			posibilidades.addAll(infLocalizacionesService.findAllByGeneroAndFormato(genero, formato,
					funda, steelbook));	
		}else {
			posibilidades.addAll(infLocalizacionesService.findAllByGeneroAndFormato(genero, 2,
					funda, steelbook));
			posibilidades.addAll(infLocalizacionesService.findAllByGeneroAndFormato(genero, 3,
					funda, steelbook));
		}
		

		String locAnterior = utlLocalizacionesService.getAnterior(codigo);
		boolean anteriorCompleta = localizacionCompleta(posibilidades, locAnterior, formato);

		String locPosterior = utlLocalizacionesService.getPosterior(codigo);
		boolean posteriorCompleta = localizacionCompleta(posibilidades, locPosterior, formato);

		if (!anteriorCompleta) {
			resultado = "La ubicación debería ser: " + locAnterior;
		} else if (!posteriorCompleta) {
			resultado = "La ubicación debería ser: " + locPosterior;
		} else { // both complete
			// tendremos que desplezar elementos ubicaciones siguientes
			String ubicacionesCompletas = ubicacionesCompletasPosteriores(posibilidades, locPosterior, genero, formato);

			resultado = "La ubicación debería ser: " + locPosterior
					+ " pero te toca desplezar a las siguientes ubicaciones: " + ubicacionesCompletas;

		}

		return ResponseEntity.ok("{\"resultado\": \"" + resultado + "\"}");
	}

	private String ubicacionesCompletasPosteriores(List<InfLocalizaciones> posibilidades, String localizacion,
			String genero, Integer formato) {
		StringBuilder resultado = new StringBuilder();
		int maximo = formato == 1 ? LIMITE_POR_CARPETA : LIMITE_POR_ESTANTERIA;
		int contador = 0;
		for (InfLocalizaciones ubication : posibilidades) {
			if (ubication.getId().getCodigo().compareTo(localizacion) > 0) {
				contador += ubication.getContador();
			}
			if(contador+1>maximo) {
				resultado.append(ubication.getId().getCodigo()).append(",");
			}
		}

		return resultado.substring(0, resultado.lastIndexOf(",") > 0 ? resultado.lastIndexOf(",") : resultado.length());
	}

	private boolean localizacionCompleta(List<InfLocalizaciones> posibilidades, String localizacion, Integer formato) {
		int contador = 0;
		for (InfLocalizaciones ubication : posibilidades) {
			if (localizacion.contains(ubication.getId().getCodigo())) {
				contador = ubication.getContador();
			}
		}
		int maximo = formato == 1 ? LIMITE_POR_CARPETA : LIMITE_POR_ESTANTERIA;
		return contador >= maximo;
	}

}
