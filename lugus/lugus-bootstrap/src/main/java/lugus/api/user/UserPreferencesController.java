package lugus.api.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.user.UserPreferencesDTO;
import lugus.service.user.UserPreferencesService;

@RestController
@RequestMapping("/api/user/preferences")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService service;

    @GetMapping
    public UserPreferencesDTO getPreferences(Authentication auth) {
        return service.getPreferences(auth.getName());
    }

    @PostMapping
    public UserPreferencesDTO savePreferences(@RequestBody UserPreferencesDTO dto, Authentication auth) {
        return service.savePreferences(auth.getName(), dto);
    }
    
   

}
