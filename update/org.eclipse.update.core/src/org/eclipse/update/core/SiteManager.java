/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Laurent Fourrier (laurent@fourrier.nom.fr) - HTTP Proxy code and NetAccess Plugin 
 *******************************************************************************/
package org.eclipse.update.core;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.boot.*;
import org.eclipse.core.runtime.*;
import org.eclipse.update.configuration.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.internal.core.*;
import org.eclipse.update.internal.core.URLEncoder;
import org.eclipse.update.internal.operations.*;
import org.xml.sax.*;

/**
 * Update Site Manager.
 * A helper class used for creating site instance. 
 * Site manager is a singleton class. It cannot be instantiated; 
 * all functionality is provided by static methods.
 * 
 * @see org.eclipse.update.core.ISite
 * @see org.eclipse.update.configuration.ILocalSite
 * @see org.eclipse.update.configuration.IConfiguredSite
 * @since 2.0
 */
public class SiteManager {
	private static final String P_HTTP_HOST = "http.proxyHost";
	private static final String P_HTTP_PORT = "http.proxyPort";	
	private static final String P_HTTP_PROXY = "http.proxySet";
	private static final String SIMPLE_EXTENSION_ID = "deltaHandler";
	//$NON-NLS-1$
	
	private static boolean isHttpProxyEnable;
	public static ILocalSite localSite;

	private static Map estimates;

	// cache found sites
	private static Map sites = new HashMap();
	// cache timestamps
	private static Map siteTimestamps = new HashMap();

	// true if an exception occured creating localSite
	// so we cache it and don't attempt to create it again
	private static CoreException exceptionOccured = null;
	
	private static UpdateSiteParser updateSiteParser = new UpdateSiteParser();
	private static InstalledSiteParser installedSiteParser = new InstalledSiteParser();

	private SiteManager() {
	}
	
	/**
	 * Returns the "local site". A local site is a logical collection
	 * of configuration information plus one or more file system 
	 * installation directories, represented as intividual sites. 
	 * These are potential targets for installation actions.
	 * 
	 * @return the local site
	 * @exception CoreException
	 * @since 2.0 
	 */
	public static ILocalSite getLocalSite() throws CoreException {
		return internalGetLocalSite(false);
	}

	/*
	 * Internal call if optimistic reconciliation needed
	 */
	private static ILocalSite internalGetLocalSite(boolean isOptimistic) throws CoreException {

		// if an exception occured while retrieving the Site
		// rethrow it
		if (exceptionOccured != null)
			throw exceptionOccured;

		if (localSite == null) {
			try {
				localSite = SiteLocal.internalGetLocalSite(isOptimistic);
			} catch (CoreException e) {
				exceptionOccured = e;
				throw e;
			}
		}
		return localSite;
	}
	
	private static boolean isValidCachedSite(URL siteURL) {
		if (!sites.containsKey(siteURL))
			return false;
			
		Long timestamp = (Long)siteTimestamps.get(siteURL);
		if (timestamp == null)
			return false;
		long localLastModified = timestamp.longValue();
		
		return UpdateManagerUtils.isSameTimestamp(siteURL, localLastModified);
	}

	/**
	 * Returns an estimate of bytes per second transfer rate for this URL
	 * @param URL the URL of the site
	 * @return long a bytes per second estimate rate
	 * @since 2.1
 	 */	
	public static long getEstimatedTransferRate(URL site) {
		if (site == null)
			return 0;
		if (estimates == null)
			return 0;
		String host = site.getHost();
		Long value = (Long) estimates.get(host);
		if (value == null)
			return 0;
		return value.longValue();
	}

	/**
	 * Returns the HTTP Proxy Server or <code>null</code> if none
	 * @return the HTTP proxy Server 
	 */
	public static String getHttpProxyServer() {
		return System.getProperty(P_HTTP_HOST);
	}
	/**
	 * Returns the HTTP Proxy Port or <code>null</code> if none
	 * @return the HTTP proxy Port 
	 */
	public static String getHttpProxyPort() {
		return System.getProperty(P_HTTP_PORT);
	}
	/**
	 * Returns <code>true</code> if the connection should use the 
	 * http proxy server, <code>false</code> otherwise
	 * @return is the http proxy server enable
	 */
	public static boolean isHttpProxyEnable() {
		return isHttpProxyEnable;
	}
	/**
	 * Sets the HTTP Proxy information
	 * Sets the HTTP proxy server for the HTTP proxy server 
	 * Sets the HTTP proxy port for the HTTP proxy server 
	 * If the proxy name is <code>null</code> or the proxy port is
	 * <code>null</code> the connection will not use HTTP proxy server.
	 * 
	 * @param enable <code>true</code> if the connection should use an http
	 * proxy server, <code>false </code> otherwise.
	 * @param httpProxyServer the HTTP proxy server name or IP adress
	 * @param httpProxyPort the HTTP proxy port
	 */
	public static void setHttpProxyInfo(boolean enable, String httpProxyServer, String httpProxyPort) {
		isHttpProxyEnable = enable;

		// if enable is false, or values are null,
		// we should remove the properties and save the fact that proxy is disable 
		if (!enable || httpProxyServer == null || httpProxyPort == null) {
			System.getProperties().remove(P_HTTP_HOST);
			System.getProperties().remove(P_HTTP_PORT);
			System.getProperties().remove(P_HTTP_PROXY);
			//if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_WARNINGS)
			UpdateCore.warn("Remove proxy server info");
			UpdateCore.getPlugin().getPluginPreferences().setValue(UpdateCore.HTTP_PROXY_ENABLE, isHttpProxyEnable());
			UpdateCore.getPlugin().savePluginPreferences();
			return;
		}
		
		//System.getProperties().put("proxySet", "true");
		//System.getProperties().put("proxyHost", proxyHost);
		//System.getProperties().put("proxyPort", proxyPort);
		
		System.getProperties().setProperty(P_HTTP_PROXY, enable?"true":"false");
		System.getProperties().setProperty(P_HTTP_HOST, httpProxyServer);
		System.getProperties().setProperty(P_HTTP_PORT, httpProxyPort);
		//if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_WARNINGS)
		UpdateCore.warn("Added proxy server info:" + httpProxyServer + ":" + httpProxyPort);
		UpdateCore.getPlugin().getPluginPreferences().setValue(UpdateCore.HTTP_PROXY_HOST, getHttpProxyServer());
		UpdateCore.getPlugin().getPluginPreferences().setValue(UpdateCore.HTTP_PROXY_PORT, getHttpProxyPort());
		UpdateCore.getPlugin().getPluginPreferences().setValue(UpdateCore.HTTP_PROXY_ENABLE, isHttpProxyEnable());
		UpdateCore.getPlugin().savePluginPreferences();
	}

	/** 
		 * Returns a site object for the installed site specified by the argument URL.
		 * Typically, the URL references a directory containing installed features and plugins.
		 * 
		 * @param siteURL site URL
		 * @param monitor the progress monitor
		 * @return site object for the url or <samp>null</samp> in case a 
		 * user canceled the connection in the progress monitor.
		 * @exception CoreException
		 * @since 2.1 
		 */
		public static IInstalledSite getInstalledSite(URL siteURL, IProgressMonitor monitor) throws CoreException {
			IInstalledSite site = null;
			if (monitor==null) monitor = new NullProgressMonitor();

			if (siteURL == null)
				return null;

			if (isValidCachedSite(siteURL)) {
				if (sites.get(siteURL) instanceof IInstalledSite)
					site = (IInstalledSite) sites.get(siteURL);
				else
				throw Utilities.newCoreException("The site " + siteURL + " is used as an update site", null);
			
				return site;
			}

			monitor.beginTask(Policy.bind("InternalSiteManager.ConnectingToSite"), 8);
			try {
				monitor.worked(3);
				site = createInstalledSite(siteURL, monitor);
				monitor.worked(1);
			} catch (CoreException preservedException) {
				if (!monitor.isCanceled()) 
					throw preservedException;
			}
			
			if (site != null) {
				sites.put(siteURL, site);
				try {
					Response response = UpdateCore.getPlugin().get(URLEncoder.encode(siteURL));
					siteTimestamps.put(siteURL, new Long(response.getLastModified()));
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}

			return site;
		}
		
	private static IInstalledSite createInstalledSite(URL url, IProgressMonitor monitor) throws CoreException {
		IInstalledSite site = null;

		if (monitor != null)
			monitor.worked(1);

		if ("file".equalsIgnoreCase(url.getProtocol())) {
			File dir = new File(url.getFile());
			if (dir != null && dir.isDirectory() && !(new File(dir, Site.SITE_XML).exists())) {
				installedSiteParser.createSite(url) ;
			}
		}
		
		return site;
	}

	/** 
	 * Returns a site object for the site specified by the argument URL.
	 * Typically, the URL references a site manifest file on an update 
	 * site. An update site acts as a source of features for installation
	 * actions.
	 * 
	 * @param siteURL site URL
	 * @param monitor the progress monitor
	 * @return site object for the url or <samp>null</samp> in case a 
	 * user canceled the connection in the progress monitor.
	 * @exception CoreException
	 * @since 2.1 
	 */
	public static IUpdateSite getUpdateSite(URL siteURL, IProgressMonitor monitor) throws CoreException {
		IUpdateSite site = null;
		if (monitor==null) monitor = new NullProgressMonitor();

		if (siteURL == null)
			return null;

		if (isValidCachedSite(siteURL)) {
			if (sites.get(siteURL) instanceof IUpdateSite)
				site = (IUpdateSite) sites.get(siteURL);
			else
				throw Utilities.newCoreException("The site " + siteURL + " is already used as an installed site", null);
			return site;
		}

		monitor.beginTask(Policy.bind("InternalSiteManager.ConnectingToSite"), 8);
		try {
			monitor.worked(3);
			site = createUpdateSite(siteURL, monitor);
			monitor.worked(1);
		} catch (CoreException preservedException) {
			if (!monitor.isCanceled()) 
				throw preservedException;
		}
			
		if (site != null) {
			sites.put(siteURL, site);
			try {
				Response response = UpdateCore.getPlugin().get(URLEncoder.encode(siteURL));
				siteTimestamps.put(siteURL, new Long(response.getLastModified()));
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
		}

		//flush the JarFile we may hold on to
		// we keep the temp not to create them again
		JarContentReference.shutdown(); // make sure we are not leaving jars open for this site

		return site;
	}


	/*
	 * Create an instance of a class that implements IUpdateSite.
	 * 
	 * The algorithm for various url's:
	 * 
	 * 1) protocol://...../
	 *     Attempt to open the stream. If it fails, add site.xml and attempt to open the stream
	 * 
	 * 2) protocol://.....
	 * 	   Attempt to open the stream
	 * 	fail
	 * 		add '/site.xml' and attempt to open the stream
	 * 	sucess
	 * 		attempt to parse, if it fails, add '/site.xml' and attempt to open the stream
	 * 
	 * 3) protocol://..../site.xml
	 *       Open the stream
	 * 
	 * 4) protocol://...#...
	 *       Open the stream
	 */
	private static IUpdateSite createUpdateSite(URL url, IProgressMonitor monitor) throws CoreException {
		IUpdateSite site = null;

		try {
			if (monitor != null)
				monitor.worked(1);
			site = doCreateUpdateSite( url, monitor);
		} catch (CoreException e) {
			if (monitor != null && monitor.isCanceled())
				return null;

			// if the URL is pointing to either a file 
			// or a directory, without reference			
			if (url.getRef() != null) {
				// 4 nothing we can do
				throw Utilities.newCoreException(Policy.bind("InternalSiteManager.UnableToAccessURL", url.toExternalForm()), e);
				//$NON-NLS-1$
			} else if (url.getFile().endsWith("/")) { //$NON-NLS-1$
				// 1 try to add site.xml
				URL urlRetry = null;
				try {
					urlRetry = new URL(url, Site.SITE_XML);
					if (monitor != null)
						monitor.worked(1);
					site = doCreateUpdateSite(urlRetry, monitor);
				} catch (MalformedURLException e1) {
					throw Utilities.newCoreException(Policy.bind("InternalSiteManager.UnableToCreateURL", url.toExternalForm() + "+" + Site.SITE_XML), e1);
					//$NON-NLS-1$ //$NON-NLS-2$
				} catch (CoreException e1) {
					throw Utilities.newCoreException(Policy.bind("InternalSiteManager.UnableToAccessURL", url.toExternalForm()), url.toExternalForm(), urlRetry.toExternalForm(), e, e1);
					//$NON-NLS-1$
				}
			} else if (url.getFile().endsWith(Site.SITE_XML)) {
				// 3 nothing we can do
				throw Utilities.newCoreException(Policy.bind("InternalSiteManager.UnableToAccessURL", url.toExternalForm()), e);
				//$NON-NLS-1$
			} else {
				// 2 try to add /site.xml 
				URL urlRetry = null;
				try {
					urlRetry = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "/" + Site.SITE_XML);
					if (monitor != null)
						monitor.worked(1);
					site = doCreateUpdateSite(urlRetry, monitor);
				} catch (MalformedURLException e1) {
					throw Utilities.newCoreException(Policy.bind("InternalSiteManager.UnableToCreateURL", url.toExternalForm() + "+" + Site.SITE_XML), e1);
					//$NON-NLS-1$ //$NON-NLS-2$
				} catch (CoreException e1) {
					throw Utilities.newCoreException(Policy.bind("InternalSiteManager.UnableToAccessURL", url.toExternalForm()), url.toExternalForm(), urlRetry.toExternalForm(), e, e1);
					//$NON-NLS-1$
				}
			}
		}

		return site;
	}
	
	private static IUpdateSite doCreateUpdateSite( URL url, IProgressMonitor monitor) throws CoreException {
		try {
			//URL resolvedURL = URLEncoder.encode(url);
			InputStream stream = openStream(url);
			updateSiteParser.init();
			UpdateSite site = updateSiteParser.parse(stream);
			if (updateSiteParser.getStatus()!=null) {
				// some internalError were detected
				IStatus status = updateSiteParser.getStatus();
				throw new CoreException(status);
			}
			return site;
		} catch (SAXException e) {
			throw Utilities.newCoreException(Policy.bind("SiteModelObject.ErrorParsingSiteStream"),e); //$NON-NLS-1$
		} catch (IOException e){
			throw Utilities.newCoreException(Policy.bind("SiteModelObject.ErrorAccessingSiteStream"),e); //$NON-NLS-1$
		}
	}
	

//	/*
//	 * Creates a new site on the file system
//	 * This is the only Site we can create.
//	 * 
//	 * @param siteLocation
//	 * @throws CoreException
//	 */
//	public static IUpdateSite2 createSite(File siteLocation) throws CoreException {
//		IUpdateSite2 site = null;
//		if (siteLocation != null) {
//			try {
//				URL siteURL = siteLocation.toURL();
//				site = getSite(siteURL, false, null);
//			} catch (MalformedURLException e) {
//				throw Utilities.newCoreException(Policy.bind("InternalSiteManager.UnableToCreateURL", siteLocation.getAbsolutePath()), e);
//				//$NON-NLS-1$
//			}
//		}
//		return site;
//	}


	/**
	 * Trigger handling of newly discovered features. This method
	 * can be called by the executing application whenever it
	 * is invoked with the -newUpdates command line argument.
	 *<p>
	 * Prompts the user to configure or unconfigure
	 * newly discoverd features.
	 * </p>
	 * @throws CoreException if an error occurs.
	 * @since 2.0
	 */
	public static void handleNewChanges() throws CoreException {
		// find extension point
		IInstallDeltaHandler handler = null;

		String pluginID = UpdateCore.getPlugin().getDescriptor().getUniqueIdentifier();

		IPluginRegistry pluginRegistry = Platform.getPluginRegistry();

		IConfigurationElement[] elements = pluginRegistry.getConfigurationElementsFor(pluginID, SIMPLE_EXTENSION_ID);

		if (elements == null || elements.length == 0) {
			throw Utilities.newCoreException(Policy.bind("SiteReconciler.UnableToFindInstallDeltaFactory", pluginID + "." + SIMPLE_EXTENSION_ID), null);
			//$NON-NLS-1$
		} else {
			IConfigurationElement element = elements[0];
			handler = (IInstallDeltaHandler) element.createExecutableExtension("class");
			//$NON-NLS-1$
		}

		// instanciate and open
		if (handler != null) {
			handler.init(UpdateUtils.getSessionDeltas());
			handler.open();
		}
	}



	/*
	 * Reconcile the local site following a specific reconciliation strategy 
	 * The parameter is true if we need to follow an optimistic reconciliation
	 * returns true if there are delta to process
	 * 
	 * Called internally by UpdateManagerReconciler aplication
	 */
	public static boolean reconcile(boolean optimisticReconciliation) throws CoreException {
		// reconcile
		internalGetLocalSite(optimisticReconciliation);

		// check if new features have been found
		if (localSite instanceof SiteLocal) {
			return SiteLocal.newFeaturesFound;
		}
		return false;
	}

	/**
	 * Method downloaded.
	 * @param l size downloaded in bytes
	 * @param l1 time in seconds
	 * @param uRL
	 */
	public static void downloaded(long downloadSize, long time, URL url) {
		if (downloadSize <= 0 || time < 0)
			return;
		String host = url.getHost();
		long sizeByTime = (time == 0) ? 0 : downloadSize / time;
		Long value = new Long(sizeByTime);
		if (estimates == null) {
			estimates = new HashMap();
		} else {
			Long previous = (Long) estimates.get(host);
			if (previous != null) {
				value = new Long((previous.longValue() + sizeByTime) / 2);
			}
		}
		estimates.put(host, value);
	}


	/**
	 * Open a stream on a URL.
	 * manages a time out if the connection is locked or fails
	 * 
	 * @param resolvedURL
	 * @return InputStream
	 */
	private  static InputStream openStream(URL resolvedURL)  throws IOException {
		Response response = UpdateCore.getPlugin().get(resolvedURL);
		//siteTimestamps.put(siteURL, new Long(response.getLastModified()));
		return response.getInputStream();
	}
}