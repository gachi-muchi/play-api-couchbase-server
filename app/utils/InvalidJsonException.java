package utils;

public class InvalidJsonException extends RuntimeException {

	private static final long serialVersionUID = 8057883665112536508L;

	public InvalidJsonException() {
		super();
	}

	public InvalidJsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidJsonException(String message) {
		super(message);
	}

	public InvalidJsonException(Throwable cause) {
		super(cause);
	}
}
