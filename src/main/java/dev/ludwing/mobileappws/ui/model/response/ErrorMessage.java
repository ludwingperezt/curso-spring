package dev.ludwing.mobileappws.ui.model.response;

import java.util.Date;

/**
 * Esta es una clase POJO que se utiliza para retornar la representación
 * de una excepción personalizada, en este ejemplo para UserServiceException
 * en la clase AppExceptionsHandler
 * 
 * @author ludwingp
 *
 */
public class ErrorMessage {

	private Date timestamp;

	private String message;
	
	public ErrorMessage() {}
	
	public ErrorMessage(Date timestamp, String message) {
		this.timestamp = timestamp;
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
