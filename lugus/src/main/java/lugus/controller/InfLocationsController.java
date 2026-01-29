package lugus.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.model.inf.InfLocations;
import lugus.service.core.UtlLocationService;
import lugus.service.inf.InfLocationsService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class InfLocationsController {

	private static final int LIMITE_POR_CARPETA = 28;
	private static final int LIMITE_POR_ESTANTERIA = 60;

	private final InfLocationsService infLocService;

	private final UtlLocationService utlLocService;

	@PostMapping("/utl/ubics/calc")
	public ResponseEntity<String> findByGeneroAndFormato(@RequestParam final String codigo,
			@RequestParam final String genero, @RequestParam final Integer formato, @RequestParam final Boolean funda,
			@RequestParam final Boolean steelbook) {
		String resultado = "";
		List<InfLocations> posibilidades = new ArrayList<InfLocations>();
		if(formato <= 1 || formato == 4) {
			posibilidades.addAll(infLocService.findAllByGeneroAndFormato(genero, formato,
					funda, steelbook));	
		}else {
			posibilidades.addAll(infLocService.findAllByGeneroAndFormato(genero, 2,
					funda, steelbook));
			posibilidades.addAll(infLocService.findAllByGeneroAndFormato(genero, 3,
					funda, steelbook));
		}
		

		String locAnterior = utlLocService.getAnterior(codigo);
		boolean anteriorCompleta = completeLocation(posibilidades, locAnterior, formato);

		String locPosterior = utlLocService.getPosterior(codigo);
		boolean posteriorCompleta = completeLocation(posibilidades, locPosterior, formato);

		if (!anteriorCompleta && locAnterior != null) {
			resultado = "La ubicación debería ser: " + locAnterior;
		} else if (!posteriorCompleta  && locPosterior != null) {
			resultado = "La ubicación debería ser: " + locPosterior;
		} else { // both complete
			// tendremos que desplezar elementos ubicaciones siguientes
			String ubicacionesCompletas = ubicacionesCompletasPosteriores(posibilidades, locPosterior, genero, formato);

			resultado = "La ubicación debería ser: " + locPosterior
					+ " pero te toca desplezar a las siguientes ubicaciones: " + ubicacionesCompletas;

		}

		return ResponseEntity.ok("{\"resultado\": \"" + resultado + "\"}");
	}

	private String ubicacionesCompletasPosteriores(List<InfLocations> posibilidades, String location,
			String genero, Integer formato) {
		StringBuilder resultado = new StringBuilder();
		int maximo = formato == 1 ? LIMITE_POR_CARPETA : LIMITE_POR_ESTANTERIA;
		int contador = 0;
		for (InfLocations infLocation : posibilidades) {
			if (infLocation.getId().getCodigo().compareTo(location) > 0) {
				contador += infLocation.getContador();
			}
			if(contador+1>maximo) {
				resultado.append(infLocation.getId().getCodigo()).append(",");
			}
		}

		return resultado.substring(0, resultado.lastIndexOf(",") > 0 ? resultado.lastIndexOf(",") : resultado.length());
	}

	private boolean completeLocation(List<InfLocations> posibilidades, String location, Integer formato) {
		int contador = 0;
		for (InfLocations ubication : posibilidades) {
			if (location!= null && location.contains(ubication.getId().getCodigo())) {
				contador = ubication.getContador();
			}
		}
		int maximo = formato == 1 ? LIMITE_POR_CARPETA : LIMITE_POR_ESTANTERIA;
		return contador >= maximo;
	}

}
