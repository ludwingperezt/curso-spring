package dev.ludwing.mobileappws.ui.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.ludwing.mobileappws.ui.model.request.LoginRequestModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

/**
 * Este controlador y método solo sirven para documentar cómo funciona
 * el endpoint de autenticación para swagger.
 * 
 * @author ludwingp
 *
 */
@RestController
public class AuthController {

	@ApiOperation("User login") // esta anotación es para evitar que en swagger ui aparezca el nombre del método.
	@ApiResponses(value= { // Aqui se configuran las respuestas
			@ApiResponse(code=200,
					message="Response Headers",
					responseHeaders= {
							@ResponseHeader(name="authorization", description="Bearer <JWT value here>", response=String.class),
							@ResponseHeader(name="userId", description="<Public User Id value here>", response=String.class)
					})
	})
	@PostMapping("/users/login")
	public void theFakeLogin(@RequestBody LoginRequestModel loginRequestModel) {
		throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security");
	}
}
