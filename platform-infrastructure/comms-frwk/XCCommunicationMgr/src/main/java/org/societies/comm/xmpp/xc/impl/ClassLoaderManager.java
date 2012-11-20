package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
//import org.eclipse.gemini.web.tomcat.internal.loading.BundleWebappClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

public class ClassLoaderManager {
	
	private static Logger LOG = LoggerFactory
			.getLogger(ClassLoaderManager.class);
	
	private Long thisBundleId;
	private Map<Long, ClassLoader> classloaderMap;
	
	public ClassLoaderManager(Object commMgrBean) {
		Bundle b = getBundle(commMgrBean);
		thisBundleId = b.getBundleId();
		classloaderMap = new HashMap<Long, ClassLoader>();
	}

	public ClassLoader classLoaderMagic(Object targetBean) {
		LOG.info("getting classloader for object "+targetBean.toString());
		ClassLoader newClassloader = null;
	
		Bundle b = getBundle(targetBean);
		newClassloader = classloaderMap.get(b.getBundleId());
		
		if (newClassloader!=null) {
			ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			Thread.currentThread().setContextClassLoader(newClassloader);
			return oldClassloader;
		}
		else
			LOG.info("no classloader found!");
		
		return null;
	}

	public void classloaderRegistry(Object targetBean) {
		Bundle b = getBundle(targetBean);
		Long id = b.getBundleId();
			
		if (!id.equals(thisBundleId)) {
			LOG.info("saving classloader "+Thread.currentThread().getContextClassLoader().toString()+" for bundle "+id);
			classloaderMap.put(id, Thread.currentThread().getContextClassLoader());
		}
		else
			LOG.info("this is the local classloader: skipping...");
	}
	
	private void notFound(Class<?> cs, ClassLoader cl) {
		throw new RuntimeException("Unable to get bundle for class: "+cs.toString()+" with context classloader "+cl.toString());
	}
	
	private Bundle getBundle(Object o) {
		Bundle b = null;
		ClassLoader hcl = Thread.currentThread().getContextClassLoader();
		if (hcl instanceof BundleDelegatingClassLoader)
			b = ((BundleDelegatingClassLoader)hcl).getBundle(); // prioritize classloader-based resolution
		
		if (b==null)
			b = FrameworkUtil.getBundle(o.getClass()); // fallback to frameworkUtils resolution
		
		if (b!=null) {
			LOG.info("resolved "+o.toString()+" to bundle '"+b.toString()+"' with id '"+b.getBundleId()+"'");
		}
		else
			notFound(o.getClass(),Thread.currentThread().getContextClassLoader());
		
		return b;
	}
}
