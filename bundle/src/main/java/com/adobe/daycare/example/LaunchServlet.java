package com.adobe.daycare.example;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.launches.api.Launch;
import com.adobe.cq.launches.api.LaunchManager;
import com.adobe.cq.launches.api.LaunchManagerFactory;
import com.adobe.cq.launches.api.LaunchPromotionParameters;
import com.adobe.cq.launches.api.LaunchPromotionScope;

@SuppressWarnings("serial")
@Component(metatype = true, immediate=true)
@Service()
@Properties({
    @Property(name = "sling.servlet.paths", value = "/bin/examples/promote-launch"),
    @Property(name = "sling.servlet.extensions", value = "html"),
    @Property(name = "sling.servlet.methods", value = "GET")
    })
public class LaunchServlet extends SlingSafeMethodsServlet {
	
	private static final Logger log = LoggerFactory.getLogger(LaunchServlet.class);
	
	@Reference
	private LaunchManagerFactory launchManagerFactory;
	
	private final String PARAM_PATH = "path";
	private final String PARAM_LAUNCH_TITLE = "launchTitle";
	
	/*
	 * This worked for http://10.122.160.151:4502/bin/examples/promote-launch.html?path=/content/launches/bubba/content/geometrixx-media/en&launchTitle=bubba	 
	 * 
	 */
	
	 @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
        
		 String path        = request.getParameter(PARAM_PATH);
		 String launchTitle = request.getParameter(PARAM_LAUNCH_TITLE);
		 
		 log.debug("path={}, launchTitle={}", path, launchTitle);
		 
		 if (launchTitle == null || launchTitle.length() == 0) throw new RuntimeException("Launch title not specified");
		 if (path == null || path.length() == 0) throw new RuntimeException("Path not specified");
		 
		 ResourceResolver resolver = request.getResourceResolver();
		 
		 Resource pageResource = resolver.getResource(path);
		 if (pageResource == null) throw new RuntimeException("Resource not valid at path");
		 
		 LaunchManager lm = launchManagerFactory.getLaunchManager(resolver);
		 
		 String launchPath = "/content/launches/" + launchTitle;
		 Launch launch     = lm.getLaunch(launchPath);
		 Params params     = new Params(pageResource);
		 
		 log.debug("launch.getRootResource().getPath()={}",launch.getRootResource().getPath());
		 log.debug("params.getResource().getPath()={}",params.getResource().getPath());
		 
		 lm.promoteLaunch(launch, params);
		 
    }
	 
	public static class Params implements LaunchPromotionParameters {
		
		private final Resource resource;
		
		public Params(Resource resource) {
			this.resource = resource;
		}

		public LaunchPromotionScope getPromotionScope() {
			return LaunchPromotionScope.RESOURCE;
		}

		public Resource getResource() {
			return resource;
		}

		public String getResourceCollectionPath() {
			return "";
		}
		
	}
	  
}
