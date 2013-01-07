package nl.proteon.liferay.surfnet.security.opensocial.exceptions;

public class ScribeException extends Exception {

	private static final long serialVersionUID = -8631932070290161232L;

	public ScribeException(String msg) {
		super(msg);
	}

	public ScribeException(String msg, Exception e) {
		super(msg, e);
	}
}
