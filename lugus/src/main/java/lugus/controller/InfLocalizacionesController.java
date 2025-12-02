package lugus.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.model.InfLocalizaciones;
import lugus.service.InfLocalizacionesService;

@RestController 
@RequestMapping("/inflocals")
@RequiredArgsConstructor
public class InfLocalizacionesController {

	private final InfLocalizacionesService infLocalizacionesService;
	
	@GetMapping
	public ResponseEntity<List<InfLocalizaciones>> findAllByGeneroAndFormato(final String codigo, final int formato){
		
		return ResponseEntity.ok(infLocalizacionesService
				.findAllByGeneroAndFormato(codigo, formato));
	}
	
}
