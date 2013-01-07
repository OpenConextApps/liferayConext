package nl.proteon.liferay.surfnet.security.scribe;

public class ServiceProfile {

	private String baseUrl;
	private String redirectUrl;
	private String consumerKey;
	private String consumerSecret;

	public ServiceProfile(String baseUrl, String redirectUrl,
			String consumerKey, String consumerSecret) {
		super();
		this.baseUrl = baseUrl;
		this.redirectUrl = redirectUrl;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	@Override
	public String toString() {
		return "ServiceProfile [baseUrl=" + baseUrl + ", redirectUrl="
				+ redirectUrl + ",consumerKey=" + consumerKey
				+ ", consumerSecret=" + consumerSecret + "]";
	}

}
