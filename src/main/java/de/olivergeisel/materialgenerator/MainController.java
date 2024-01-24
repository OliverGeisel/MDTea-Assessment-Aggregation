package de.olivergeisel.materialgenerator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {


	@GetMapping({"", "/", "index.html", "index"})
	String landing() {
		return "index";
	}

	@GetMapping("login")
	String login() {
		return "login";
	}

	@GetMapping("synchronization")
	String synchronization() {
		return "synchronization";
	}

	@GetMapping("info")
	String info() {
		return "info";
	}

}
