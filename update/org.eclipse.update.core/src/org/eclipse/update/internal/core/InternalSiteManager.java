/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.update.internal.core;


import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.configuration.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.internal.operations.UpdateUtils;

/**
 * 
 */
public class InternalSiteManager {

	public static ILocalSite localSite;

	private static final String SIMPLE_EXTENSION_ID = "deltaHandler";
	//$NON-NLS-1$

	private static Map estimates;

	// cache found sites
	private static Map sites = new HashMap();
	// cache timestamps
	private static Map siteTimestamps = new HashMap();
	public static boolean globalUseCache = true;

	// true if an exception occured creating localSite
	// so we cache it and don't attempt to create it again
	private static CoreException exceptionOccured = null;

	/*
	 * @see SiteManager#getLocalSite()
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
				localSite = LocalSite.internalGetLocalSite(isOptimistic);
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

	/*
	 * Prompt the user to configure or unconfigure
	 * newly discoverd features.
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
		if (localSite instanceof LocalSite) {
			return LocalSite.newFeaturesFound;
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
	 * Method getEstimatedTransferRate rate bytes/seconds.
	 * @param string
	 * @return long
	 */
	public static long getEstimatedTransferRate(String host) {
		if (estimates == null)
			return 0;
		Long value = (Long) estimates.get(host);
		if (value == null)
			return 0;
		return value.longValue();
	}

}
