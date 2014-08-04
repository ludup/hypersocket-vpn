package com.hypersocket.applications.events;

import com.hypersocket.applications.ApplicationResource;
import com.hypersocket.resource.AssignableResourceEvent;
import com.hypersocket.session.Session;

public class ApplicationResourceEvent extends AssignableResourceEvent {

//	public static final String ATTR_NAME = "attr.name";
	
	private static final long serialVersionUID = 8654327648973401854L;

	public ApplicationResourceEvent(Object source, String resourceKey,
			Session session, ApplicationResource resource) {
		super(source, resourceKey, true, session, resource);

		/**
		 * TODO add attributes of your resource here. Make sure all attributes
		 * have a constant string definition like the commented out example above,
		 * its important for its name to start with ATTR_ as this is picked up during 
		 * the registration process
		 */
	}

	public ApplicationResourceEvent(Object source, String resourceKey,
			ApplicationResource resource, Throwable e, Session session) {
		super(source, resourceKey, resource, e, session);
		

	}

}
