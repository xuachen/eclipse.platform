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
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;

/**
 * Feature model object.
 * <p>
 * This class may be instantiated or subclassed by clients. However, in most 
 * cases clients should instead instantiate or subclass the provided 
 * concrete implementation of this model.
 * </p>
 * @see org.eclipse.update.core.Feature
 * @since 2.0
 */
public class Feature extends FeatureReference implements IFeature{
	/**
	 * Simple file name of the default feature manifest file
	 * @since 2.0
	 */
	public static final String FEATURE_FILE = "feature"; //$NON-NLS-1$

	/**
	 * File extension of the default feature manifest file
	 * @since 2.0
	 */
	public static final String FEATURE_XML = FEATURE_FILE + ".xml"; //$NON-NLS-1$
	
	
	private String localizedLabel;
	private String provider;
	private String localizedProvider;
	private String imageURLString;
	private URL imageURL;
	private String os;
	private String ws;
	private String nl;
	private String arch;
	private boolean primary = false;
	private boolean exclusive=false;
	private String primaryPluginID;
	private String application;
	private String affinity;
	private InstallHandlerEntry installHandler;
	private URLEntry description;
	private URLEntry copyright;
	private URLEntry license;
	private URLEntry updateSiteInfo;
	private List /*of URLEntry*/	discoverySiteInfo;
	private List /*of Import */	imports;
	private List /*of PluginEntry*/pluginEntries;
	private List /*of IncludedFeatureReference */	featureIncludes;
	private List /*of NonPluginEntry*/	nonPluginEntries;
	private IFeatureContentProvider contentProvider;

	// performance
	private URL bundleURL;
	private URL base;
	private boolean resolved = false;

	/**
	 * Creates an uninitialized feature object.
	 * 
	 * @since 2.0
	 */
	public Feature() {
		super();
	}

	/**
	 * Compares 2 feature models for equality
	 *  
	 * @param obj feature model to compare with
	 * @return <code>true</code> if the two models are equal, 
	 * <code>false</code> otherwise
	 * @since 2.0
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Feature))
			return false;
		Feature model = (Feature) obj;

		return (getVersionedIdentifier().equals(model.getVersionedIdentifier()));
	}


	/**
	 * Retrieve the displayable label for the feature provider. If the model
	 * object has been resolved, the label is localized.
	 * 
	 * @return displayable label, or <code>null</code>.
	 * @since 2.0
	 */
	public String getProvider() {
		delayedResolve();
		if (localizedProvider != null)
			return localizedProvider;
		else
			return provider;
	}

	/**
	 * Retrieve the non-localized displayable label for the feature provider.
	 * 
	 * @return non-localized displayable label, or <code>null</code>.
	 * @since 2.0
	 */
	public String getProviderNonLocalized() {
		return provider;
	}

	/**
	 * Returns the unresolved URL string for the feature image.
	 *
	 * @return url string, or <code>null</code>
	 * @since 2.0
	 */
	public String getImageURLString() {
		delayedResolve();
		return imageURLString;
	}

	/**
	 * Returns the resolved URL for the image.
	 * 
	 * @return url, or <code>null</code>
	 * @since 2.0
	 */
	public URL getImageURL() {
		delayedResolve();
		return imageURL;
	}

	/**
	 * Get optional operating system specification as a comma-separated string.
	 * 
	 * @see org.eclipse.core.boot.BootLoader 
	 * @return the operating system specification string, or <code>null</code>.
	 * @since 2.0
	 */
	public String getOS() {
		return os;
	}

	/**
	 * Get optional windowing system specification as a comma-separated string.
	 * 
	 * @see org.eclipse.core.boot.BootLoader 
	 * @return the windowing system specification string, or <code>null</code>.
	 * @since 2.0
	 */
	public String getWS() {
		return ws;
	}

	/**
	 * Get optional system architecture specification as a comma-separated string.
	 * 
	 * @see org.eclipse.core.boot.BootLoader 
	 * @return the system architecture specification string, or <code>null</code>.
	 * @since 2.0
	 */
	public String getOSArch() {
		return arch;
	}

	/**
	 * Get optional locale specification as a comma-separated string.
	 * 
	 * @return the locale specification string, or <code>null</code>.
	 * @since 2.0
	 */
	public String getNL() {
		return nl;
	}

	/**
	 * Indicates whether the feature can be used as a primary feature.
	 * 
	 * @return <code>true</code> if this is a primary feature, 
	 * otherwise <code>false</code>
	 * @since 2.0
	 */
	public boolean isPrimary() {
		return primary;
	}
	
	/**
	 * Indicates whether the feature must be processed alone
	 * during installation and configuration. Features that
	 * are not exclusive can be installed in a batch.
	 * 
	 * @return <code>true</code> if feature requires
	 * exclusive processing, <code>false</code> otherwise.
	 * @since 2.1
	 */
	public boolean isExclusive() {
		return exclusive;
	}

	/**
	 * Returns an optional identifier for the feature application
	 * 
	 * @return application identifier, or <code>null</code>.
	 * @since 2.0
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Returns an optional identifier for the colocation affinity feature
	 * 
	 * @return feature identifier, or <code>null</code>.
	 * @since 2.0
	 */
	public String getAffinityFeature() {
		return affinity;
	}

	/**
	 * Returns and optional custom install handler entry.
	 * 
	 * @return install handler entry, or <code>null</code> if
	 * none was specified
	 * @since 2.0
	 */
	public IInstallHandlerEntry getInstallHandler() {
		//delayedResolve(); no delay
		return installHandler;
	}

	/**
	 * Returns the feature description.
	 * 
	 * @return feature rescription, or <code>null</code>.
	 * @since 2.0
	 */
	public IURLEntry getDescription() {
		//delayedResolve(); no delay
		return description;
	}

	/**
	 * Returns the copyright information for the feature.
	 * 
	 * @return copyright information, or <code>null</code>.
	 * @since 2.0
	 */
	public IURLEntry getCopyright() {
		//delayedResolve(); no delay
		return copyright;
	}

	/**
	 * Returns the license information for the feature.
	 * 
	 * @return feature license, or <code>null</code>.
	 * @since 2.0
	 */
	public IURLEntry getLicense() {
		//delayedResolve(); no delay;
		return license;
	}

	/**
	 * Returns an information entry referencing the location of the
	 * feature update site.
	 * 
	 * @return update site entry, or <code>null</code>.
	 * @since 2.0
	 */
	public IURLEntry getUpdateSiteEntry() {
		//delayedResolve(); no delay;
		// if update site is null, we could have it set to the site the feature
		// was downloaded from
		return updateSiteInfo;
	}

	/**
	 * Return an array of information entries referencing locations of other
	 * update sites. 
	 * 
	 * @return an array of site entries, or an empty array.
	 * @since 2.0 
	 * @since 2.0
	 */
	public IURLEntry[] getDiscoverySiteEntries() {
		//delayedResolve(); no delay;
		if (discoverySiteInfo == null)
			return new URLEntry[0];

		return (URLEntry[]) discoverySiteInfo.toArray(arrayTypeFor(discoverySiteInfo));
	}

	/**
	 * Return a list of plug-in dependencies for this feature.
	 * 
	 * @return the list of required plug-in dependencies, or an empty array.
	 * @since 2.0
	 */
	public IImport[] getImports(boolean environmentFilter) {
		//delayedResolve(); no delay;
		if (imports == null)
			return new Import[0];

		IImport[] entries = (Import[]) imports.toArray(arrayTypeFor(imports));
		if (environmentFilter)
			return filterImports(entries);
		else
			return entries;
	}

	/**
	 * Returns an array of plug-in entries referenced by this feature
	 * 
	 * @return an erray of plug-in entries, or an empty array.
	 * @since 2.0
	 */
	public IPluginEntry[] getPluginEntries(boolean environmentFilter) {
		if (pluginEntries == null)
			return new PluginEntry[0];
		
		IPluginEntry[] entries = (PluginEntry[]) pluginEntries.toArray(arrayTypeFor(pluginEntries));
		if (environmentFilter)
			return filterPluginEntry(entries);
		else
			return entries;
	}


	/**
	 * Returns an array of features  included by this feature
	 * filtered by the operating system, windowing system and architecture system
	 * set in <code>Sitemanager</code>
	 * 
	 * @param environmentFilter if true, return the entries valid in the current environment only
	 * @return an erray of feature references, or an empty array.
	 * @since 2.0
	 */
	public IFeature[] getIncludedFeatures(boolean environmentFilter) throws CoreException {
		if (featureIncludes == null)
			return new IFeature[0];
				
			IIncludedFeatureReference[] entries = (IIncludedFeatureReference[]) featureIncludes.toArray(
			arrayTypeFor(featureIncludes));
			
		if (environmentFilter)
			entries = filterFeatures(entries);

		// obtain features now
		ArrayList features = new ArrayList(entries.length);
		for (int i=0; i<entries.length; i++) {
			IFeature f = getSite().getFeature(entries[i], null);
			// TODO create missing feature
			//if (f == null)
			//	f = new missingFeature(0....)
			if (f != null)
				features.add(getSite().getFeature(entries[i], null));
		}
		
		return (IFeature[])features.toArray(new IFeature[features.size()]);
	}
	

	/**
	 * Returns an array of non-plug-in entries referenced by this feature
	 * 
	 * @return an erray of non-plug-in entries, or an empty array.
	 * @since 2.0
	 */
	public INonPluginEntry[] getNonPluginEntries(boolean environmentFilter) {
		if (nonPluginEntries == null)
			return new NonPluginEntry[0];

		INonPluginEntry[] entries = (NonPluginEntry[]) nonPluginEntries.toArray(arrayTypeFor(nonPluginEntries));
		if (environmentFilter)
			return filterNonPluginEntry(entries);
		else
			return entries;
	}

	/**
	 * Sets the feature provider displayable label.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param provider provider displayable label
	 * @since 2.0
	 */
	void setProvider(String provider) {
		assertIsWriteable();
		this.provider = provider;
		this.localizedProvider = null;
	}

	/**
	 * Sets the unresolved URL for the feature image.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param imageURLString unresolved URL string
	 * @since 2.0
	 */
	void setImageURLString(String imageURLString) {
		assertIsWriteable();
		this.imageURLString = imageURLString;
		this.imageURL = null;
	}

	/**
	 * Sets the operating system specification.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @see org.eclipse.core.boot.BootLoader
	 * @param os operating system specification as a comma-separated list
	 * @since 2.0
	 */
	void setOS(String os) {
		assertIsWriteable();
		this.os = os;
	}

	/**
	 * Sets the windowing system specification.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @see org.eclipse.core.boot.BootLoader
	 * @param ws windowing system specification as a comma-separated list
	 * @since 2.0
	 */
	void setWS(String ws) {
		assertIsWriteable();
		this.ws = ws;
	}

	/**
	 * Sets the locale specification.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param nl locale specification as a comma-separated list
	 * @since 2.0
	 */
	void setNL(String nl) {
		assertIsWriteable();
		this.nl = nl;
	}

	/**
	 * Sets the system architecture specification.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @see org.eclipse.core.boot.BootLoader
	 * @param arch system architecture specification as a comma-separated list
	 * @since 2.0
	 */
	void setArch(String arch) {
		assertIsWriteable();
		this.arch = arch;
	}

	/**
	 * Indicates whether this feature can act as a primary feature.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param primary <code>true</code> if this feature can act as primary,
	 * <code>false</code> otherwise
	 * 
	 * @since 2.0
	 */
	void setPrimary(boolean primary) {
		assertIsWriteable();
		this.primary = primary;
	}
	
	/**
	 * Indicates whether this feature can act as a primary feature.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param exclusive <code>true</code> if this feature must be
	 * processed independently from other features, <code>false</code> 
	 * if feature can be processed in a batch with other features.
	 * 
	 * @since 2.1
	 */
	void setExclusive(boolean exclusive) {
		assertIsWriteable();
		this.exclusive = exclusive;
	}

	/**
	 * Sets the feature application identifier.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param application feature application identifier
	 * @since 2.0
	 */
	void setApplication(String application) {
		assertIsWriteable();
		this.application = application;
	}

	/**
	 * Sets the identifier of the Feature this feature should be
	 * installed with.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param affinity the identifier of the Feature
	 * @since 2.0
	 */
	void setAffinityFeature(String affinity) {
		assertIsWriteable();
		this.affinity = affinity;
	}

	/**
	 * Sets the custom install handler for the feature.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param installHandler install handler entry
	 * @since 2.0
	 */
	void setInstallHandler(InstallHandlerEntry installHandler) {
		assertIsWriteable();
		this.installHandler = installHandler;
	}

	/**
	 * Sets the feature description information.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param description feature description information
	 * @since 2.0
	 */
	void setDescription(URLEntry description) {
		assertIsWriteable();
		this.description = description;
	}

	/**
	 * Sets the feature copyright information.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param copyright feature copyright information
	 * @since 2.0
	 */
	void setCopyright(URLEntry copyright) {
		assertIsWriteable();
		this.copyright = copyright;
	}

	/**
	 * Sets the feature license information.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param license feature license information
	 * @since 2.0
	 */
	void setLicense(URLEntry license) {
		assertIsWriteable();
		this.license = license;
	}

	/**
	 * Sets the feature update site reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param updateSiteInfo feature update site reference
	 * @since 2.0
	 */
	void setUpdateSiteEntry(URLEntry updateSiteInfo) {
		assertIsWriteable();
		this.updateSiteInfo = updateSiteInfo;
	}

	/**
	 * Sets additional update site references.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param discoverySiteInfo additional update site references
	 * @since 2.0
	 */
	void setDiscoverySiteEntries(URLEntry[] discoverySiteInfo) {
		assertIsWriteable();
		if (discoverySiteInfo == null)
			this.discoverySiteInfo = null;
		else
			this.discoverySiteInfo = new ArrayList(Arrays.asList(discoverySiteInfo));
	}

	/**
	 * Sets the feature plug-in dependency information.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param imports feature plug-in dependency information
	 * @since 2.0
	 */
	void setImports(Import[] imports) {
		assertIsWriteable();
		if (imports == null)
			this.imports = null;
		else
			this.imports = new ArrayList(Arrays.asList(imports));
	}

	/**
	 * Sets the feature plug-in references.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param pluginEntries feature plug-in references
	 * @since 2.0
	 */
	void setPluginEntries(PluginEntry[] pluginEntries) {
		assertIsWriteable();
		if (pluginEntries == null)
			this.pluginEntries = null;
		else
			this.pluginEntries = new ArrayList(Arrays.asList(pluginEntries));
	}

	/**
	 * Sets the feature non-plug-in data references.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param nonPluginEntries feature non-plug-in data references
	 * @since 2.0
	 */
	void setNonPluginEntries(NonPluginEntry[] nonPluginEntries) {
		assertIsWriteable();
		if (nonPluginEntries == null)
			this.nonPluginEntries = null;
		else
			this.nonPluginEntries = new ArrayList(Arrays.asList(nonPluginEntries));
	}
	void setFeatureContentProvider(IFeatureContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		((FeatureContentProvider)contentProvider).setFeature(this);
	}

	/**
	 * Adds an additional update site reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param discoverySiteInfo update site reference
	 * @since 2.0
	 */
	void addDiscoverySiteEntry(URLEntry discoverySiteInfo) {
		assertIsWriteable();
		if (this.discoverySiteInfo == null)
			this.discoverySiteInfo = new ArrayList();
		if (!this.discoverySiteInfo.contains(discoverySiteInfo))
			this.discoverySiteInfo.add(discoverySiteInfo);
	}

	/**
	 * Adds a plug-in dependency entry.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param importEntry plug-in dependency entry
	 * @since 2.0
	 */
	void addImport(Import importEntry) {
		assertIsWriteable();
		if (this.imports == null)
			this.imports = new ArrayList();
		if (!this.imports.contains(importEntry))
			this.imports.add(importEntry);
	}

	/**
	 * Adds a plug-in reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param pluginEntry plug-in reference
	 * @since 2.0
	 */
	void addPluginEntry(PluginEntry pluginEntry) {
		assertIsWriteable();
		if (this.pluginEntries == null)
			this.pluginEntries = new ArrayList();
		//PERF: no ListContains()
		//if (!this.pluginEntries.contains(pluginEntry))
		this.pluginEntries.add(pluginEntry);
	}

	/**
	 * Adds a feature identifier.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param identifier feature identifer
	 * @param options the options associated with the nested feature
	 * @since 2.1
	 */
	void addIncludedFeatureReference(IncludedFeatureReference include) {
		assertIsWriteable();
		if (this.featureIncludes == null)
			this.featureIncludes = new ArrayList();
		//PERF: no ListContains()
		//if (!this.featureIncludes.contains(include))
		this.featureIncludes.add(include);
	}

	/**
	 * Adds a non-plug-in data reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param nonPluginEntry non-plug-in data reference
	 * @since 2.0
	 */
	void addNonPluginEntry(NonPluginEntry nonPluginEntry) {
		assertIsWriteable();
		if (this.nonPluginEntries == null)
			this.nonPluginEntries = new ArrayList();
		//PERF: no ListContains()
		//if (!this.nonPluginEntries.contains(nonPluginEntry))
		this.nonPluginEntries.add(nonPluginEntry);
	}

	/**
	 * Removes an update site reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param discoverySiteInfo update site reference
	 * @since 2.0
	 */
	void removeDiscoverySiteEntry(URLEntry discoverySiteInfo) {
		assertIsWriteable();
		if (this.discoverySiteInfo != null)
			this.discoverySiteInfo.remove(discoverySiteInfo);
	}

	/**
	 * Removes a plug-in dependency entry.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param importEntry plug-in dependency entry
	 * @since 2.0
	 */
	void removeImport(Import importEntry) {
		assertIsWriteable();
		if (this.imports != null)
			this.imports.remove(importEntry);
	}

	/**
	 * Removes a plug-in reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param pluginEntry plug-in reference
	 * @since 2.0
	 */
	void removePluginEntry(PluginEntry pluginEntry) {
		assertIsWriteable();
		if (this.pluginEntries != null)
			this.pluginEntries.remove(pluginEntry);
	}

	/**
	 * Removes a non-plug-in data reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param nonPluginEntry non-plug-in data reference
	 * @since 2.0
	 */
	void removeNonPluginEntry(NonPluginEntry nonPluginEntry) {
		assertIsWriteable();
		if (this.nonPluginEntries != null)
			this.nonPluginEntries.remove(nonPluginEntry);
	}

	/**
	 * Marks the model object as read-only.
	 * 
	 * @since 2.0
	 */
	public void markReadOnly() {
		super.markReadOnly();
		markReferenceReadOnly((URLEntry)getDescription());
		markReferenceReadOnly((URLEntry)getCopyright());
		markReferenceReadOnly((URLEntry)getLicense());
		markReferenceReadOnly((URLEntry)getUpdateSiteEntry());
		markListReferenceReadOnly((URLEntry[])getDiscoverySiteEntries());
		markListReferenceReadOnly((Import[])getImports(false));
		markListReferenceReadOnly((PluginEntry[])getPluginEntries(false));
		markListReferenceReadOnly((NonPluginEntry[])getNonPluginEntries(false));
	}

	/**
	 * Resolve the model object.
	 * Any URL strings in the model are resolved relative to the 
	 * base URL argument. Any translatable strings in the model that are
	 * specified as translation keys are localized using the supplied 
	 * resource bundle.
	 * 
	 * @param base URL
	 * @param bundle resource bundle
	 * @exception MalformedURLException
	 * @since 2.0
	 */
	public void resolve(URL base,URL bundleURL) throws MalformedURLException {
		this.bundleURL = bundleURL;
		this.base = base;

		// plugin entry and nonpluginentry are optimized too
		resolveListReference((PluginEntry[])getPluginEntries(false), base, bundleURL);
		resolveListReference((NonPluginEntry[])getNonPluginEntries(false), base, bundleURL);
		
		//URLSiteModel are optimized
		resolveReference((URLEntry)getDescription(),base, bundleURL);
		resolveReference((URLEntry)getCopyright(),base, bundleURL);
		resolveReference((URLEntry)getLicense(),base, bundleURL);
		resolveReference((URLEntry)getUpdateSiteEntry(),base, bundleURL);
		resolveListReference((URLEntry[])getDiscoverySiteEntries(),base, bundleURL);
		
		// Import Models are optimized
		resolveListReference((Import[])getImports(false),base, bundleURL);
	}

	protected void delayedResolve() {

		// PERF: delay resolution
		if (resolved)
			return;

		resolved = true;
		super.delayedResolve();
		localizedProvider = resolveNLString(bundleURL, provider);
		try {
			imageURL = resolveURL(base,bundleURL, imageURLString);
		} catch (MalformedURLException e){
			UpdateCore.warn("",e);
		}
	}

	/**
	 * Method setPrimaryPlugin.
	 * @param plugin
	 */
	void setPrimaryPluginID(String plugin) {
		if (primary && primaryPluginID == null) {
			primaryPluginID = getFeatureIdentifier();
		}
		primaryPluginID = plugin;
	}
	/**
	 * Returns the primaryPluginID.
	 * @return String
	 */
	public String getPrimaryPluginID() {
		return primaryPluginID;
	}

	/**
	 * Returns <code>true</code> if this feature is patching another feature,
	 * <code>false</code> otherwise
	 * @return boolean
	 */
	public boolean isPatch() {
		IImport[] imports = getImports(false);

		for (int i = 0; i < imports.length; i++) {
			if (imports[i].isPatch())
				return true;
		}
		return false;
	}
	
	/*
	 * Method filterFeatures.
	 * Also implemented in Site
	 * 
	 * @param list
	 * @return List
	 */
	private IIncludedFeatureReference[] filterFeatures(IIncludedFeatureReference[] allIncluded) {
		List list = new ArrayList();
		if (allIncluded != null) {
			for (int i = 0; i < allIncluded.length; i++) {
				IIncludedFeatureReference included = allIncluded[i];
				if (UpdateManagerUtils.isValidEnvironment(included))
					list.add(included);
				else {
					if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_WARNINGS) {
						UpdateCore.warn(
							"Filtered out feature reference:" + included);
					}
				}
			}
		}

		IIncludedFeatureReference[] result =
			new IIncludedFeatureReference[list.size()];
		if (!list.isEmpty()) {
			list.toArray(result);
		}

		return result;
	}
	
	/**
	 * Method filterImports.
	 * @param iImports
	 * @return IImport[]
	 */
	private IImport[] filterImports(IImport[] all) {
		List list = new ArrayList();
		if (all != null) {
			for (int i = 0; i < all.length; i++) {
				if (UpdateManagerUtils.isValidEnvironment(all[i]))
					list.add(all[i]);
			}
		}

		IImport[] result = new IImport[list.size()];
		if (!list.isEmpty()) {
			list.toArray(result);
		}

		return result;
	}
	
	/**
	 * Method filterPluginEntry.
	 * @param iNonPluginEntrys
	 * @return INonPluginEntry[]
	 */
	private INonPluginEntry[] filterNonPluginEntry(INonPluginEntry[] all) {
		List list = new ArrayList();
		if (all != null) {
			for (int i = 0; i < all.length; i++) {
				if (UpdateManagerUtils.isValidEnvironment(all[i]))
					list.add(all[i]);
			}
		}

		INonPluginEntry[] result = new INonPluginEntry[list.size()];
		if (!list.isEmpty()) {
			list.toArray(result);
		}

		return result;
	}
	
	/*
	 * Method filter.
	 * @param result
	 * @return IPluginEntry[]
	 */
	private IPluginEntry[] filterPluginEntry(IPluginEntry[] all) {
		List list = new ArrayList();
		if (all != null) {
			for (int i = 0; i < all.length; i++) {
				if (UpdateManagerUtils.isValidEnvironment(all[i]))
					list.add(all[i]);
			}
		}

		IPluginEntry[] result = new IPluginEntry[list.size()];
		if (!list.isEmpty()) {
			list.toArray(result);
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IFeature#getFeatureContentProvider()
	 */
	public IFeatureContentProvider getFeatureContentProvider() {
		return contentProvider;
	}

}