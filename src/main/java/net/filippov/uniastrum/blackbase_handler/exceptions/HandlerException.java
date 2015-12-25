package net.filippov.uniastrum.blackbase_handler.exceptions;

public class HandlerException extends BlackBaseException {

	private static final long serialVersionUID = 5087434019944459150L;

	public HandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public HandlerException(String message) {
		super(message);
	}

}
