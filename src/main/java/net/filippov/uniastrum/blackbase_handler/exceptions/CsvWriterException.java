package net.filippov.uniastrum.blackbase_handler.exceptions;

public class CsvWriterException extends BlackBaseException {

	private static final long serialVersionUID = -7966414376596704480L;

	public CsvWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvWriterException(String message) {
		super(message);
	}
}
