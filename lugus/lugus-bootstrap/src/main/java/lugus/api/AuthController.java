package lugus.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lugus.config.LoginRequest;
import lugus.service.user.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	
    private final AuthenticationManager authenticationManager;
    
    private final UserService userService;
    
    @Autowired
    public AuthController(final AuthenticationManager authenticationManager, final UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final LoginRequest request, final HttpServletRequest httpRequest) {

        try {
            UsernamePasswordAuthenticationToken authReq =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

            Authentication auth = authenticationManager.authenticate(authReq);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            // create session and cookie JSESSIONID
            httpRequest.getSession(true) .setAttribute("SPRING_SECURITY_CONTEXT", context);

            return ResponseEntity.ok(Map.of("status", "ok"));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(final Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // get roles of Authentication
        List<String> roles = auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority()) // ROLE_ADMIN, ROLE_USER, etc.
                .toList();

        return ResponseEntity.ok(
            Map.of(
                "username", auth.getName(),
                "roles", roles
            )
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody final ChangePasswordDTO dto, final Authentication auth) {
        userService.changePassword(auth.getName(), dto.currentPassword(), dto.newPassword());
        return ResponseEntity.ok().build();
    }
  
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpServletRequest request, final HttpServletResponse response) {
        request.getSession().invalidate();

        // borrar cookie
        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(false) 
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok().build();
    }

    
}
