package dev.ludwing.mobileappws.ui.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 * Este controlador se agregó para que la ruta "/" retorne un status 200 siempre.
 * Esto es util para desplegar la app en AWS beanstalk
 * 
 * @author ludwingp
 *
 */
@RestController
public class HomeController {
	
	@ApiOperation(value="Home web service endpoint", 
			notes="Endpoint that returns a 200 status code to confirm the health of the web service")
	@GetMapping
	public String status() {
		return "ok";
	}

}
