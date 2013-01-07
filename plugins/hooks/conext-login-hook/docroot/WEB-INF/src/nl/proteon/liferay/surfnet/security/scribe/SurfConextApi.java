package nl.proteon.liferay.surfnet.security.scribe;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;

public class SurfConextApi extends DefaultApi20 {
	private static final String AUTHORIZE_URL = "%soauth2/authorize?response_type=code";
	private static final String ACCESS_TOKEN_ENDPOINT_URL = "%soauth2/token?grant_type=authorization_code";

	private String baseUrl;

	public SurfConextApi(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getAccessTokenEndpoint() {
		return String.format(ACCESS_TOKEN_ENDPOINT_URL, baseUrl);
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new JsonTokenExtractor();
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		StringBuilder authUrl = new StringBuilder();
		authUrl.append(String.format(AUTHORIZE_URL, baseUrl));
		// Append scope if present
		if (config.hasScope()) {
			authUrl.append("&scope=").append(
					OAuthEncoder.encode(config.getScope()));
		}

		// add redirect URI if callback isn't equal to 'oob'
		if (!config.getCallback().equalsIgnoreCase("oob")) {
			authUrl.append("&redirect_uri=").append(
					OAuthEncoder.encode(config.getCallback()));
		}

		authUrl.append("&client_id=").append(
				OAuthEncoder.encode(config.getApiKey()));
		return authUrl.toString();
	}
}
