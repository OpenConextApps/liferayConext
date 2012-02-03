package nl.proteon.liferay.surfnet.security.opensocial.exceptions;

public class OpenSocialException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3486352066299478783L;

	public OpenSocialException(String msg) {
		super(msg);
	}

	public OpenSocialException(String msg, Exception e) {
		super(msg, e);
	}
}
