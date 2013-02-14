package nl.proteon.liferay.surfnet.security.scribe;

import static org.scribe.model.OAuthConstants.EMPTY_TOKEN;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.util.portlet.PortletProps;
import nl.proteon.liferay.surfnet.security.opensocial.exceptions.ScribeException;
import nl.proteon.liferay.surfnet.security.opensocial.model.OpenSocialGroup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class OAuth2ServiceUtil {
	private final static String GROUP_URL = "social/rest/groups/@me";

	/**
	 * Redirect to the authorization URL.
	 * 
	 * @param response
	 *            used to do the redirect
	 * @throws ScribeException
	 * @throws IOException
	 */
	public static void authorize(HttpServletResponse response)
			throws IOException, ScribeException {
		response.sendRedirect(getService().getAuthorizationUrl(EMPTY_TOKEN));
	}

	/**
	 * Get an Access Token based on the code we received in the call back from
	 * the authorize redirect.
	 * 
	 * @param oAuthCode
	 *            the code from the redirect URL
	 * @return access token
	 * @throws ScribeException
	 */
	public static Token getAccesToken(String oAuthCode) throws ScribeException {
		// we are in the oauth callback where we already have a code
		// that we need to convert to an Access Token
		final Verifier verifier = new Verifier(oAuthCode);
		return getService().getAccessToken(EMPTY_TOKEN, verifier);
	}

	public static List<OpenSocialGroup> getGroups(String accessToken,
			String openId) throws ScribeException {
		final ServiceProfile serviceProfile = getParams();
		final String url = serviceProfile.getBaseUrl() + GROUP_URL;
		final OAuthRequest request = new OAuthRequest(Verb.GET, url);
		request.addHeader("Authorization", "Bearer " + accessToken);
		final Response response = request.send();
		if (response.getCode() != 200) {
			_log.error("could not retrieve groups, see info below");
			_log.error("URL: " + request.getCompleteUrl());
			_log.error("headers: " + response.getHeaders());
			_log.error("body: " + response.getBody());
		}
		final String jsonString = response.getBody();
		final List<OpenSocialGroup> listResult = new ArrayList<OpenSocialGroup>();
		final JSONParser parser = new JSONParser();
		try {
			final JSONObject json = (JSONObject) parser.parse(jsonString);
			_log.info("JSON string: " + json);
			// parse groups
			final JSONArray entries = (JSONArray) json.get("entry");
			for (Object rawEntry : entries) {
				final JSONObject entry = (JSONObject) rawEntry;
				listResult.add(new OpenSocialGroup((String) entry.get("id"),
						(String) entry.get("title"), (String) entry
								.get("description")));
			}

		} catch (ParseException e) {
			_log.error("could not parse the following JSON, problem occurs at position "
					+ e.getPosition());
			_log.error(jsonString);
			_log.error(e, e);
		}

		return listResult;
	}

	public static ServiceProfile getParams() throws ScribeException {
		final ServiceProfile serviceProfile = new ServiceProfile(
				PortletProps.get("opensocial.server.url"),
				PortletProps.get("opensocial.redirect.url"),
				PortletProps.get("opensocial.consumer.key"),
				PortletProps.get("opensocial.consumer.secret"));
		if (serviceProfile.getBaseUrl() == null) {
			throw new ScribeException("opensocial_server_url can not be null!");
		}
		if (serviceProfile.getRedirectUrl() == null) {
			throw new ScribeException(
					"opensocial_redirect_url can not be null!");
		}
		if (serviceProfile.getConsumerKey() == null) {
			throw new ScribeException(
					"opensocial_consumer_key can not be null!");
		}
		if (serviceProfile.getConsumerSecret() == null) {
			throw new ScribeException(
					"opensocial_consumer_secret can not be null!");
		}
		return serviceProfile;
	}

	public static OAuthService getService() throws ScribeException {
		final ServiceProfile serviceProfile = getParams();
		final SurfConextApi api = new SurfConextApi(serviceProfile.getBaseUrl());
		// TODO: State attribute should be added to the URL, Scribe 1.3.0 does
		// not support this. The state attribute helps against CSRF attacks.
		return new ServiceBuilder().provider(api)
				.apiKey(serviceProfile.getConsumerKey())
				.apiSecret(serviceProfile.getConsumerSecret())
				.callback(serviceProfile.getRedirectUrl()).scope("read")
				.build();
	}

	private static Log _log = LogFactoryUtil.getLog(OAuth2ServiceUtil.class);
}
