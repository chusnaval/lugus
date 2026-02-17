package lugus.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lugus.service.user.PasswordRecoveryService;

@Validated
@RestController
@RequestMapping("/api/auth/password-recovery")
@RequiredArgsConstructor
public class PasswordRecoveryApiController {

	private final PasswordRecoveryService passwordRecoveryService;

	@PostMapping("/request")
	public ResponseEntity<PasswordRecoveryRequestResponse> requestRecovery(@Valid @RequestBody PasswordRecoveryRequest request) {
		passwordRecoveryService.createRecoveryToken(request.login());
		return ResponseEntity.ok(new PasswordRecoveryRequestResponse(
				"Si el usuario existe, se ha iniciado el proceso de recuperación."));
	}

	@PostMapping("/reset")
	public ResponseEntity<PasswordRecoveryResetResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
		boolean updated = passwordRecoveryService.resetPassword(request.login(), request.token(), request.newPassword());
		if (!updated) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new PasswordRecoveryResetResponse("No se pudo resetear la contraseña. Token inválido o expirado."));
		}

		return ResponseEntity.ok(new PasswordRecoveryResetResponse("Contraseña actualizada correctamente."));
	}

	public record PasswordRecoveryRequest(
			@NotBlank(message = "El login es obligatorio")
			String login) {
	}

	public record PasswordResetRequest(
			@NotBlank(message = "El login es obligatorio")
			String login,
			@NotBlank(message = "El token es obligatorio")
			String token,
			@NotBlank(message = "La nueva contraseña es obligatoria")
			@Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
			String newPassword) {
	}

	public record PasswordRecoveryRequestResponse(String message) {
	}

	public record PasswordRecoveryResetResponse(String message) {
	}
}