package com.hypersocket.launcher;

import java.util.Map;

import com.hypersocket.session.Session;
import com.hypersocket.utils.ITokenResolver;

public class WebApplicationTokenResolver implements ITokenResolver {

	private Session session;

	public WebApplicationTokenResolver(Session session) {
		this.session = session;
	}

	@Override
	public String resolveToken(String tokenName) {
		switch (tokenName) {
		case "web.user.id":
			return String.valueOf(session.getCurrentPrincipal().getId());
		case "web.user.name":
			return session.getCurrentPrincipal().getName();
		case "web.user.email":
			return session.getCurrentPrincipal().getEmail();
		case "web.user.realm":
			return session.getCurrentPrincipal().getRealm().getName();
		case "web.user.ou":
			return session.getCurrentPrincipal().getOrganizationalUnit();
		case "web.user.description":
			return session.getCurrentPrincipal().getPrincipalDescription();
		}
		return null;
	}

	@Override
	public Map<String, Object> getData() {
		return null;
	}

}