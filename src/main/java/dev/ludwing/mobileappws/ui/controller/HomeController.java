package dev.ludwing.mobileappws.ui.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Este controlador se agregó para que la ruta "/" retorne un status 200 siempre.
 * Esto es util para desplegar la app en AWS beanstalk
 * 
 * @author ludwingp
 *
 */
@RestController
public class HomeController {
	
	@GetMapping
	public String status() {
		return "ok";
	}

}
