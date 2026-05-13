package lugus.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/help")
@Controller
public class HelpController {

	@GetMapping("/contact")
	public String contact() {

		return "help/contact";
	}
	
	@GetMapping("/license")
	public String license() {

		return "help/license";
	}
	
	@GetMapping("/releases")
	public String releases() {

		return "help/releaseNotes";
	}
}
