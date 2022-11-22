package dev.ludwing.mobileappws.ui.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// La anotación @RestController identifica la clase como un controlador REST para que pueda
// recibir peticiones HTTP.
// La anotación @RequestMapping sirve para enrutar acciones con paths

@RestController
@RequestMapping("users") // GET|POST|PUT|DELETE http://localhost/users
public class UserController {

	@GetMapping  // Mapea la función al método GET 
	public String getUser() {
		return "get user was called";
	}
	
	@PostMapping
	public String createUser() {
		return "user created";
	}
	
	@PutMapping
	public String updateUser() {
		return "user updated";
	}
	
	@DeleteMapping
	public String deleteUser() {
		return "user deleted";
	}
}
