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
package org.eclipse.update.core.model;

import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.configuration.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;
import org.eclipse.update.internal.model.*;


/**
 * Installed Site model object.
 * <p>
 * This class may be instantiated or subclassed by clients. However, in most 
 * cases clients should instead instantiate or subclass the provided 
 * concrete implementation of this model.
 * </p>
 * @see org.eclipse.update.core.Site
 * @since 2.0
 */
public class InstalledSite extends Site implements IInstalledSite {

	private ConfiguredSite configuredSiteModel;
	private List pluginEntries = new ArrayList(0);

	/**
	 * Creates an uninitialized site model object.
	 * 
	 * @since 2.0
	 */
	public InstalledSite() {
		super();
	}


	/**
	 * 
	 */
	public ConfiguredSite getConfiguredSiteModel() {
		return this.configuredSiteModel;
	}

	/**
	 * 
	 */
	void setConfiguredSiteModel(ConfiguredSite configuredSiteModel) {
		this.configuredSiteModel = configuredSiteModel;
	}

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
		IPluginEntry[] entries = feature.getPluginEntries(false);
		if (entries != null) {
			// get all the other plugins from all the other features
			Set allPluginID = new HashSet();
			IFeatureReference[] featureRefs = getFeatureReferences();
			if (featureRefs != null) {
				for (int indexFeatures = 0; indexFeatures < featureRefs.length; indexFeatures++) {
					IFeature featureToCompare = null;
					try {
						featureToCompare = getFeature(featureRefs[indexFeatures],null);
					} catch (CoreException e) {
						UpdateCore.warn(null, e);
					}
					if (!feature.equals(featureToCompare)) {
						IPluginEntry[] pluginEntries = getFeature(featureRefs[indexFeatures],null).getPluginEntries(false);
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
	 * @see org.eclipse.update.core.IInstalledSite2#install(org.eclipse.update.core.IFeature, org.eclipse.update.core.IFeature[], org.eclipse.update.core.IVerifier, org.eclipse.update.core.IVerificationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature install(
		IFeature feature,
		IFeature[] optionalFeatures,
		IVerifier verifier,
		IVerificationListener verificationListener,
		IProgressMonitor monitor)
		throws InstallAbortedException, CoreException {

		// make sure we have an InstallMonitor		
		if (!(monitor instanceof InstallMonitor))
			monitor = new InstallMonitor(monitor);
		FeatureInstaller installer = new FeatureInstaller(null,this, feature);
		return installer.install(optionalFeatures, verifier, verificationListener, (InstallMonitor)monitor);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#remove(org.eclipse.update.core.IFeature2, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void remove(IFeature feature, IProgressMonitor monitor)
		throws CoreException {
		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#getCurrentConfiguredSite()
	 */
	public IConfiguredSite getCurrentConfiguredSite() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IInstalledSite#getPluginEntries()
	 */
	public IPluginEntry[] getPluginEntries() {
		IPluginEntry[] result = new IPluginEntry[0];
		if (!(pluginEntries == null || pluginEntries.isEmpty())) {
			result = new IPluginEntry[pluginEntries.size()];
			pluginEntries.toArray(result);
		}
		return result;
	}
	
	/**
	 * Adds a plugin entry 
	 * Either from parsing the file system or 
	 * installing a feature
	 * 
	 * We cannot figure out the list of plugins by reading the Site.xml as
	 * the archives tag are optionals
	 */
	void addPluginEntry(IPluginEntry pluginEntry) {
		pluginEntries.add(pluginEntry);
	}

	/*
	 * 
	 */
	private void debug(String trace) {
		//DEBUG
		if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_INSTALL) {
			UpdateCore.debug(trace);
		}
	}
	
	protected IFeatureContentProvider createFeatureContentProvider(URL url) throws CoreException {

		if (url == null)
			throw Utilities.newCoreException(Policy.bind("FeatureExecutableFactory.NullURL"), null);

		return new FeatureExecutableContentProvider(url);
	}

}
