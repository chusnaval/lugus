package lugus.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lugus.model.series.Serie;
import lugus.service.series.SerieService;

@RestController 
@RequestMapping("/validateSerie")
@RequiredArgsConstructor
public class ValidateSeriesController {

	private final SerieService service;

	@GetMapping("/titlesInYear")
	public ResponseEntity<Serie> validateTitlesInYear(@RequestParam final String title,
			@RequestParam final String titleGest, final Integer year) {
		List<Serie> similars = service.findByTitlesInYear(title, titleGest, year);
		if (similars != null && !similars.isEmpty()) {
			return ResponseEntity.ok(similars.get(0));
		} else {
			return ResponseEntity.ok(Serie.builder().build());
		}
	}

	@RestControllerAdvice
	public class GlobalExceptionHandler {

		@ExceptionHandler(ConstraintViolationException.class)
		public ResponseEntity<Map<String, Object>> handleValidation(ConstraintViolationException ex) {
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("timestamp", Instant.now());
			body.put("status", HttpStatus.BAD_REQUEST.value());

			// Recopilamos todos los mensajes de violación
			List<String> errors = ex.getConstraintViolations().stream()
					.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage()).collect(Collectors.toList());
			body.put("errors", errors);
			return ResponseEntity.badRequest().body(body);
		}
	}
}
