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
package org.eclipse.update.core;

import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.configuration.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.internal.core.*;

/**
 * Convenience implementation of a site.
 * <p>
 * This class may be instantiated or subclassed by clients.
 * </p> 
 * @see org.eclipse.update.core.ISite
 * @see org.eclipse.update.core.model.SiteModel
 * @since 2.0
 */
public class  extends InstalledSite implements IInstalledSite {

	public InstalledSite() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#getCurrentConfiguredSite()
	 */
	public IConfiguredSite getCurrentConfiguredSite() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#getPluginEntriesOnlyReferencedBy(org.eclipse.update.core.IFeature2)
	 */
	/**
	 * Returns an array of entries corresponding to plug-ins that are
	 * installed on this site and are referenced only by the specified
	 * feature. 
	 * 
	 * @see ISite#getPluginEntriesOnlyReferencedBy(IFeature)	 * 
	 * @since 2.0
	 */
	public IPluginEntry[] getPluginEntriesOnlyReferencedBy(IFeature feature) throws CoreException {

		IPluginEntry[] pluginsToRemove = new IPluginEntry[0];
		if (feature == null)
			return pluginsToRemove;

		// get the plugins from the feature
		IPluginEntry[] entries = feature.getPluginEntries();
		if (entries != null) {
			// get all the other plugins from all the other features
			Set allPluginID = new HashSet();
			ISiteFeatureReference[] features = getFeatureReferences();
			if (features != null) {
				for (int indexFeatures = 0; indexFeatures < features.length; indexFeatures++) {
					IFeature featureToCompare = null;
					try {
						featureToCompare = features[indexFeatures].getFeature(null);
					} catch (CoreException e) {
						UpdateCore.warn(null, e);
					}
					if (!feature.equals(featureToCompare)) {
						IPluginEntry[] pluginEntries = features[indexFeatures].getFeature(null).getPluginEntries();
						if (pluginEntries != null) {
							for (int indexEntries = 0; indexEntries < pluginEntries.length; indexEntries++) {
								allPluginID.add(pluginEntries[indexEntries].getVersionedIdentifier());
							}
						}
					}
				}
			}

			// create the delta with the plugins that may be still used by other configured or unconfigured feature
			List plugins = new ArrayList();
			for (int indexPlugins = 0; indexPlugins < entries.length; indexPlugins++) {
				if (!allPluginID.contains(entries[indexPlugins].getVersionedIdentifier())) {
					plugins.add(entries[indexPlugins]);
				}
			}

			// move List into Array
			if (!plugins.isEmpty()) {
				pluginsToRemove = new IPluginEntry[plugins.size()];
				plugins.toArray(pluginsToRemove);
			}
		}

		return pluginsToRemove;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#install(org.eclipse.update.core.IFeature2, org.eclipse.update.core.IFeatureReference2[], org.eclipse.update.core.IVerificationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature install(
		IFeature feature,
		IFeatureReference[] optionalfeatures,
		IVerificationListener verificationListener,
		IProgressMonitor monitor)
		throws InstallAbortedException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#install(org.eclipse.update.core.IFeature2, org.eclipse.update.core.IVerificationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature install(
		IFeature feature,
		IVerificationListener verificationListener,
		IProgressMonitor monitor)
		throws InstallAbortedException, CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#remove(org.eclipse.update.core.IFeature2, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void remove(IFeature feature, IProgressMonitor monitor)
		throws CoreException {
		// TODO Auto-generated method stub

	}

}
