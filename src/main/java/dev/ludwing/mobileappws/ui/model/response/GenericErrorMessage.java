package dev.ludwing.mobileappws.ui.model.response;

import java.util.Date;

/**
 * Clase POJO para la representación de cualquier excepción.
 * 
 * @author ludwingp
 *
 */
public class GenericErrorMessage {

	private Date timestamp;

	private int status;

	private String error;

	private String message;

	private String path;

	public GenericErrorMessage() {
	}

	public GenericErrorMessage(Date timestamp, int status, String error, String message, String path) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
