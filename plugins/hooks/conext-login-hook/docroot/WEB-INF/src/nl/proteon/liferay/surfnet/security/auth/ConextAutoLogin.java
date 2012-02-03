package nl.proteon.liferay.surfnet.security.auth;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AutoLogin;
import com.liferay.portal.security.auth.AutoLoginException;
import com.liferay.portal.service.ServiceContext;
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
			
			String emailAddress = StringPool.BLANK;
			String firstName = StringPool.BLANK;
			String lastName = StringPool.BLANK;
			String middleName = StringPool.BLANK;
			String screenName = StringPool.BLANK;
			String openId = StringPool.BLANK;
			
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
				screenName = StringUtil.replace(
						screenName,
						new String[] {StringPool.SLASH, StringPool.UNDERLINE, StringPool.SPACE},
						new String[] {StringPool.PERIOD, StringPool.PERIOD, StringPool.PERIOD});
			}
			if(Validator.isNotNull(request.getHeader(PortletProps.get("saml2.header.mapping.id")))) {
				openId = request.getHeader(PortletProps.get("saml2.header.mapping.id"));
			}
			if(Validator.isNotNull(request.getHeader(PortletProps.get("saml2.header.mapping.fullname"))) 
					&& PortletProps.get("saml2.header.mapping.firstname").equals("")
					&& PortletProps.get("saml2.header.mapping.middlename").equals("")
					&& PortletProps.get("saml2.header.mapping.lastname").equals("")) {
				String fullName = request.getHeader(PortletProps.get("saml2.header.mapping.fullname"));
				//Simplified name extraction.
				firstName = fullName.substring(0, fullName.indexOf(" "));
				middleName = fullName.substring(fullName.indexOf(" "),fullName.lastIndexOf(" "));
				lastName = fullName.substring(fullName.lastIndexOf(" ")+1);
			} else {
				firstName = request.getHeader(PortletProps.get("saml2.header.mapping.firstname"));
				middleName = request.getHeader(PortletProps.get("saml2.header.mapping.middlename"));
				lastName = request.getHeader(PortletProps.get("saml2.header.mapping.lastname"));
			} 
			
			user = getUserByOpenId(companyId, openId);
			
			_log.info("first: "+firstName);
			_log.info("middle: "+middleName);
			_log.info("last: "+lastName);
			_log.info("screen: "+screenName);
			_log.info("email: "+emailAddress);
			_log.info("unique: "+openId);
			_log.info("companyId: "+companyId);
			
			if(!(user==null)) {
				user.setCompanyId(companyId);
				user.setCreateDate(DateUtil.newDate());
				user.setEmailAddress(emailAddress);
				user.setFirstName(firstName);
				user.setMiddleName(middleName);
				user.setLastName(lastName);
				user.setScreenName(screenName);
				
				UserLocalServiceUtil.updateUser(user);
			} else {
				user = addUser(companyId, screenName, emailAddress, openId, firstName, middleName, lastName);				
			}

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

	public User getUserByOpenId(long companyId, String openId) {
		User user = null;
		try {
			user = UserLocalServiceUtil.getUserByOpenId(companyId, openId);
		} catch (PortalException e) {
			_log.debug(e,e);
		} catch (SystemException e) {
			_log.debug(e,e);
		}
		return user;
	}
	
	public User addUser(long companyId, String screenName, String emailAddress, 
			String openId, String firstName, String middleName, String lastName) {
		
		User user = null;
		
		boolean autoPassword = true;
		String password1 = "";
		String password2 = "";
		boolean autoScreenName = true;
		long facebookId = 0;
		int prefixId = -1;
		int suffixId = -1;
		boolean male = true;
		int birthdayMonth = 1;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;
		long creatorUserId = 0;
		Locale locale = LocaleUtil.getDefault();
		String jobTitle = "";
		
		ServiceContext serviceContext = new ServiceContext();
		
		try {
			user = UserLocalServiceUtil.addUser(creatorUserId, companyId, autoPassword, password1, 
					password2, autoScreenName, screenName, emailAddress, facebookId, 
					openId, locale, firstName, 
					middleName, lastName, prefixId, suffixId, male, birthdayMonth, birthdayDay, birthdayYear, jobTitle,
					groupIds, organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
		} catch (PortalException e) {
		} catch (SystemException e) {
		}
		
		return user;
	}
}
