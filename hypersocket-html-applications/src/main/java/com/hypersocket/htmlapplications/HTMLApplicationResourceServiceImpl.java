package com.hypersocket.htmlapplications;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.i18n.I18NService;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;

@Service
public class HTMLApplicationResourceServiceImpl implements HTMLApplicationResourceService {

	public static final String RESOURCE_BUNDLE = "HTMLApplicationResourceService";

	public static final String HTML_APPLICATION_ACTIONS = "htmlApplicationActions";
	public static final String HTML_APPLICATIONS_MENU = "hTMLApplications";

	@Autowired
	I18NService i18nService;
	
	@Autowired
	MenuService menuService;

	public HTMLApplicationResourceServiceImpl() {
	}

	@PostConstruct
	private void postConstruct() {
		i18nService.registerBundle(RESOURCE_BUNDLE);

		menuService.registerMenu(
				new MenuRegistration(RESOURCE_BUNDLE, HTML_APPLICATIONS_MENU, "fa-html5", "hTMLApplications", 100),
				MenuService.MENU_RESOURCES);
	}

}
