package nl.proteon.liferay.surfnet.security.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.security.auth.AutoLoginException;

public class ConextAutoLogin implements AutoLogin {

	@Override
	public String[] login(HttpServletRequest arg0, HttpServletResponse arg1)
			throws AutoLoginException {

		return null;
	}

}
