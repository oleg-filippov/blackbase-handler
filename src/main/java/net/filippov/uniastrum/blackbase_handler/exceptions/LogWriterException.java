package net.filippov.uniastrum.blackbase_handler.exceptions;

public class LogWriterException extends BlackBaseException {

	private static final long serialVersionUID = 1459519768166928586L;

	public LogWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogWriterException(String message) {
		super(message);
	}

}
