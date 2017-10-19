package com.hypersocket.applications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.hypersocket.attributes.user.UserAttributeService;
import com.hypersocket.http.HttpUtilsImpl;
import com.hypersocket.netty.HttpRequestDispatcherHandler;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.RealmService;
import com.hypersocket.resource.RealmResource;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.tables.BootstrapTableResult;
import com.hypersocket.transactions.TransactionService;

/**
 * This exists because Hypersocket doesn't have interfaces for realm /
 * assignable resources, making it impossible for two different concrete
 * resource types that extend the same parent class to be of different types
 * with regard to assign capable or realm capable.
 *
 * @param <R>
 *            type of resource
 * @param <T>
 *            type of template
 */
public abstract class ApplicationResourceHelper<R extends RealmResource, T extends ApplicationTemplate<R>> {

	static Logger log = LoggerFactory.getLogger(ApplicationResourceHelper.class);

	@Autowired
	private HttpUtilsImpl httpUtils;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private UserAttributeService attributeService;

	@Autowired
	private RealmService realmService;

	private Map<String, ApplicationTemplateResolver<T>> templateResolvers = new HashMap<>();
	private String bundle;

	protected ApplicationResourceHelper(String bundle) {
		this.bundle = bundle;
	}

	public BootstrapTableResult<?> searchTemplates(String resolver, String search, int iDisplayStart,
			int iDisplayLength) throws IOException, AccessDeniedException {

		if (resolver == null) {
			/*
			 * If no resolver is specified we created a merged list manually.
			 * This could get horribly inefficient as the catalogue gets bigger
			 * so a better way will have to be found
			 * 
			 */
			List<Object> rows = new ArrayList<>();
			Object resource = null;
			for (Map.Entry<String, ApplicationTemplateResolver<T>> en : templateResolvers.entrySet()) {
				BootstrapTableResult<?> b = en.getValue().resolveTemplates(search, 0, Integer.MAX_VALUE);
				if (resource == null)
					resource = b.getResource();
				rows.addAll(b.getRows());
			}
			long total = rows.size();
			if (!rows.isEmpty())
				rows = rows.subList(Math.max(0, iDisplayStart), Math.min(iDisplayStart + iDisplayLength, rows.size()));
			BootstrapTableResult<Object> r = new BootstrapTableResult<>();
			r.setRows(rows);
			r.setResource(resource);
			r.setTotal(total);
			return r;
		} else {
			ApplicationTemplateResolver<T> resolverObj = templateResolvers.get(resolver);
			if (resolverObj == null)
				throw new IOException(String.format("Unknown resolver %s.", resolver));
			return resolverObj.resolveTemplates(search, iDisplayStart, iDisplayLength);
		}
	}

	public void downloadTemplateImage(String uuid, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setAttribute(HttpRequestDispatcherHandler.CONTENT_INPUTSTREAM,
				httpUtils
						.doHttpGet(
								System.getProperty("hypersocket.templateServerImageUrl",
										"https://updates2.hypersocket.com/hypersocket/api/templates/image/") + uuid,
								true));

	}

	public R createFromTemplate(final String script) throws ResourceException, AccessDeniedException {

		R result = transactionService.doInTransaction(new TransactionCallback<R>() {

			@SuppressWarnings("unchecked")
			@Override
			public R doInTransaction(TransactionStatus status) {

				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("beanshell");

				Bindings bindings = engine.createBindings();
				bindings.put("realmService", realmService);
				bindings.put("templateService", ApplicationResourceHelper.this);
				bindings.put("attributeService", attributeService);
				bindings.put("log", log);

				try {
					Object result = engine.eval(script, bindings);
					try {
						return (R) result;
					} catch (ClassCastException cce) {
						throw new IllegalStateException("Transaction failed", new ResourceCreationException(bundle,
								"error.templateFailed", "Script returned invalid object"));
					}
				} catch (ScriptException e) {
					log.error("Failed to create application launcher from template", e);
					if (e.getCause() instanceof ResourceCreationException) {
						throw new IllegalStateException("Transaction failed", e.getCause());
					}
					throw new IllegalStateException("Transaction failed",
							new ResourceCreationException(bundle, "error.templateFailed", e.getMessage()));
				}
			}

		});

		return result;

	}

	public void registerTemplateResolver(ApplicationTemplateResolver<T> resolver) {
		templateResolvers.put(resolver.getId(), resolver);
	}
}
