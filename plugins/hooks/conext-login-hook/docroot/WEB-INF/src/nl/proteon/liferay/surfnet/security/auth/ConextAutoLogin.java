package nl.proteon.liferay.surfnet.security.auth;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.security.auth.AutoLoginException;
import com.liferay.portal.security.ldap.PortalLDAPImporterUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.portlet.PortletProps;

public class ConextAutoLogin implements AutoLogin {

	@Override
	public String[] login(HttpServletRequest request, HttpServletResponse response)
			throws AutoLoginException {

		String[] credentials = null;
		
		try {
			long companyId = PortalUtil.getCompanyId(request);
			
			String uniqueId = StringPool.BLANK;
			String emailAddress = StringPool.BLANK;
			String screenName = StringPool.BLANK;
			
			User user = null;

			List<String> headers = Collections.list((Enumeration<String>)request.getHeaderNames());
			for(String headerName : headers) {
				_log.info(headerName + ": " + request.getHeader(headerName));
			}
			
			if(Validator.isNotNull(request.getHeader(PortletProps.get("saml2.header.mapping.email")))) {
				emailAddress = request.getHeader(PortletProps.get("saml2.header.mapping.email"));
			}
			if(Validator.isNotNull(request.getHeader(PortletProps.get("saml2.header.mapping.screenname")))) {
				screenName = request.getHeader(PortletProps.get("saml2.header.mapping.screenname"));
			}
			if(Validator.isNotNull(request.getHeader(PortletProps.get("saml2.header.mapping.id")))) {
				uniqueId = request.getHeader(PortletProps.get("saml2.header.mapping.id"));
			}
			
			user = UserLocalServiceUtil.getUserByOpenId(companyId, uniqueId);

			user = PortalLDAPImporterUtil.importLDAPUser(
					companyId, emailAddress, screenName);
			
			user.setOpenId(uniqueId);
			
			credentials = new String[3];

			credentials[0] = String.valueOf(user.getUserId());
			credentials[1] = user.getPassword();
			credentials[2] = Boolean.TRUE.toString();
		}
		catch (Exception e) {
			_log.error(e, e);
		}
		
		return credentials;
	}
	
	private static Log _log = LogFactoryUtil.getLog(ConextAutoLogin.class);

}
