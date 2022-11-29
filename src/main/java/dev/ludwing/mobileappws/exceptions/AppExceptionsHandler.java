package dev.ludwing.mobileappws.exceptions;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import dev.ludwing.mobileappws.ui.model.response.ErrorMessage;

/**
 * Esta clase es la responsable de manejar las excepciones que resulten de la aplicación.
 * 
 * Para ello es necesario que se usen dos anotaciones: @ControllerAdvice para la clase
 * y @ExceptionHandler para el método que gestionará las excepciones.
 * 
 * @author ludwingp
 *
 */
@ControllerAdvice
public class AppExceptionsHandler {

	/**
	 * Este método puede manejar múltiples excepciones, para ello se debe especificar en el parámetro
	 * "value" la lista de las excepciones que se manejarán con este handler.
	 * 
	 * @param ex
	 * @param request: Es la data de la request recibida.
	 * @return
	 */
	@ExceptionHandler(value= {UserServiceException.class})
	public ResponseEntity<Object> handleUserServiceException(UserServiceException ex, WebRequest request) {
		
		ErrorMessage exMessage = new ErrorMessage(new Date(), ex.getMessage());
		
		return new ResponseEntity<>(exMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
