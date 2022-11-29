package dev.ludwing.mobileappws.exceptions;

/**
 * Esta es una excepción específica para ser utilizada dentro de los
 * serivicios de User.
 * @author ludwingp
 *
 */
public class UserServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	
	public UserServiceException(String message) {
		super(message);
	}
}
