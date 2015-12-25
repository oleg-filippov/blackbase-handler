package net.filippov.uniastrum.blackbase_handler.exceptions;

public class BlackBaseException extends RuntimeException {

	private static final long serialVersionUID = -2301043358233651741L;

	public BlackBaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BlackBaseException(String message) {
		super(message);
	}
}
