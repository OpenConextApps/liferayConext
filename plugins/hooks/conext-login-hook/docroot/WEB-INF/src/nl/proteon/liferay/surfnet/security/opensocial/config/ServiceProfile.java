package nl.proteon.liferay.surfnet.security.opensocial.config;

public class ServiceProfile{

   private String baseURL;
   private String consumerKey;
   private String consumerSecret;

   public ServiceProfile(String baseURL, String consumerKey, String consumerSecret){
      super();
      this.baseURL = baseURL;
      this.consumerKey = consumerKey;
      this.consumerSecret = consumerSecret;
   }

   public String getBaseURL(){
      return baseURL;
   }

   public void setBaseURL(String baseURL){
      this.baseURL = baseURL;
   }
   
   public String getConsumerKey(){
      return consumerKey;
   }

   public void setConsumerKey(String consumerKey){
      this.consumerKey = consumerKey;
   }
   
   public String getConsumerSecret(){
      return consumerSecret;
   }

   public void setConsumerSecret(String consumerSecret){
      this.consumerSecret = consumerSecret;
   }
   
   @Override
   public String toString(){
      return "ServiceProfile [baseURL=" + baseURL + ", consumerKey=" + consumerKey
               + ", consumerSecret=" + consumerSecret + "]";
   }
   
}
