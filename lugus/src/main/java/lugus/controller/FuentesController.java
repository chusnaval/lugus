package lugus.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.model.core.Fuente;
import lugus.service.core.FuenteService;
@RestController 
@RequestMapping("/fuentes")
@RequiredArgsConstructor
public class FuentesController {


	private final FuenteService fuenteService;
	
	@GetMapping
	public ResponseEntity<List<Fuente>> findAllWhenSuggestNotNull(){
		
		return ResponseEntity.ok(fuenteService.findBySuggestIsNotNull());
	}
}
