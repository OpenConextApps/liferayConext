package nl.proteon.liferay.surfnet.security.opensocial;

import java.io.IOException;
import java.util.*;
import org.opensocial.*;
import org.opensocial.auth.*;
import org.opensocial.models.Group;
import org.opensocial.providers.*;
import org.opensocial.services.*;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.portlet.PortletProps;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import nl.proteon.liferay.surfnet.security.opensocial.config.*;
import nl.proteon.liferay.surfnet.security.opensocial.exceptions.*;
import nl.proteon.liferay.surfnet.security.opensocial.model.*;

public class OpenSocialGroupLocalServiceUtil {
	
	private static Log _log = LogFactoryUtil.getLog(OpenSocialGroupLocalServiceUtil.class);

	private static ClientConfig cc;

	static{
		cc = new DefaultClientConfig();
		
		// use the following jaxb context resolver
		cc.getClasses().add(JAXBContextResolver.class);
	}

	public static List<OpenSocialGroup> getOpenSocialGroups(long lUserID) throws OpenSocialException{
		_log.debug("[" + lUserID + "] Starting oAuth...");
		ServiceProfile sp = getParams();
      
		List<OpenSocialGroup> listResult = new ArrayList<OpenSocialGroup>();
      
		try{
			User user = UserLocalServiceUtil.getUserById(lUserID);
         
			if(user.getOpenId() == null || "".equals(user.getOpenId())){
				_log.debug("[" + lUserID + "] has got no SurfID. Can not process him.");
			} else {
				String sSurfID = user.getOpenId();
				_log.debug("openId:"+sSurfID);

				Provider provider = new ShindigProvider(true);

				provider.setRestEndpoint(sp.getBaseURL() + "rest/");
				provider.setRpcEndpoint(sp.getBaseURL() + "rpc/");
				provider.setVersion("0.9");

				AuthScheme scheme = new OAuth2LeggedScheme(sp.getConsumerKey(), sp.getConsumerSecret(), sSurfID);

				Client client = new Client(provider, scheme);
				_log.debug("[" + lUserID + "]" + " client.rest.endpoint=" +  client.getProvider().getRestEndpoint());
				_log.debug("[" + lUserID + "]" + " client.rpc.endpoint=" +  client.getProvider().getRpcEndpoint());

				Request request = GroupsService.getGroups(sSurfID);
            
				_log.debug("[" + lUserID + "]" + " request=" +  request.toString());
				_log.debug("[" + lUserID + "]" + " request.getRestUrlTemplate()=" +  request.getRestUrlTemplate());
            
				try {
					Response response = client.send(request);

					List<Group> groups = response.getEntries();
            
					_log.debug("[" + lUserID + "]" + " response=" +  response.toString());
					_log.debug("[" + lUserID + "]" + " response.getStatusLink()=" +  response.getStatusLink());
            		_log.debug("[" + lUserID + "]" + " response.getEntries()=" +  response.getEntries().toString());

            		for(Group group : groups){
            			org.opensocial.models.Model model = (org.opensocial.models.Model) group.getField("id");
            			String sGroupID = "" + model.getField("groupId");
            			String sGroupTitle = "" + group.getTitle();

            			_log.debug("[" + lUserID + "]" + " groupID=" + sGroupID + " groupTitle=" + sGroupTitle);

            			listResult.add(new OpenSocialGroup(sGroupID, sGroupTitle, ""));
            		}
            	
				} catch (RequestException e) { 
					
					_log.debug("[" + lUserID + "]" + " RequestException thrown: " + e.getMessage());	
				
				} catch (IOException e) {
					
					_log.debug("[" + lUserID + "]" + " IOException thrown: " + e.getMessage());
				
				} catch (Exception e) {
					_log.debug("[" + lUserID + "]" + " Exception thrown: " + e.getMessage());
				}
			}
			_log.debug("[" + lUserID + "] oAuth has been finished.");
		
		} catch (Exception e){
			throw new OpenSocialException(lUserID + " " + e.toString(), e);
		}
	
		return listResult;
	}
	
	private static ServiceProfile getParams() throws OpenSocialException { 
	   
	   	ServiceProfile serviceProfile = new ServiceProfile(
			   PortletProps.get("opensocial.server.url"),
			   PortletProps.get("opensocial.consumer.key"),
			   PortletProps.get("opensocial.consumer.secret")
			   );

		if(serviceProfile.getBaseURL() == null) {
			throw new OpenSocialException("opensocial_server_url can not be null!");
		}
      
		if(serviceProfile.getConsumerKey() == null){
			throw new OpenSocialException("opensocial_consumer_key can not be null!");
		}
      
		if(serviceProfile.getConsumerSecret() == null){
			throw new OpenSocialException("opensocial_consumer_secret can not be null!");
		}
		
		_log.debug(serviceProfile.toString());
		
		return serviceProfile;
	}
}
