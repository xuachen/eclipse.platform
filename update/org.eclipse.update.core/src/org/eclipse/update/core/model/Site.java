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

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;
import org.eclipse.update.internal.core.URLEncoder;

/**
 * Site model object.
 * <p>
 * This class may be instantiated or subclassed by clients. However, in most 
 * cases clients should instead instantiate or subclass the provided 
 * concrete implementation of this model.
 * </p>
 * @see org.eclipse.update.core.Site
 * @since 2.0
 */
public abstract class Site extends ModelObject implements ISite{
	/**
	 * Default installation path for features
	 * 
	 * @since 2.0
	 */
	public static final String DEFAULT_INSTALLED_FEATURE_PATH = "features/";
	//$NON-NLS-1$

	/**
	 * Default installation path for plug-ins and plug-in fragments
	 * 
	 * @since 2.0
	 */
	public static final String DEFAULT_PLUGIN_PATH = "plugins/"; //$NON-NLS-1$

	/**
	 * Default path on a site where packaged features are located
	 * 
	 * @since 2.0
	 */
	public static final String DEFAULT_FEATURE_PATH = "features/"; //$NON-NLS-1$

	/**
	 * Default site manifest file name
	 * 
	 * @since 2.0
	 */
	public static final String SITE_FILE = "site"; //$NON-NLS-1$

	/**
	 * Default site manifest extension
	 * 
	 * @since 2.0
	 */
	public static final String SITE_XML = SITE_FILE + ".xml"; //$NON-NLS-1$
	
	private String type;
	private URLEntry description;
	private List /*of FeatureReferenceModel*/ featureReferences;
	private List /*of ArchiveReferenceModel*/ archiveReferences;
	private Set /*of CategoryModel*/ categories;
	private String locationURLString;
	private URL locationURL;
	private ISiteContentProvider siteContentProvider;
	private List features = new ArrayList(0);
	private boolean featuresLoaded = false;

	private static FeatureParser parser = new FeatureParser();

	/**
	 * Creates an uninitialized site model object.
	 * 
	 * @since 2.0
	 */
	public Site() {
		super();
	}

	/**
	 * Returns an array of feature reference models on this site.
	 * 
	 * @return an array of feature reference models, or an empty array.
	 * @since 2.0
	 */
	public IFeatureReference[] getFeatureReferences() {
		if (featureReferences == null)
			return new FeatureReference[0];

		return (FeatureReference[]) featureReferences.toArray(arrayTypeFor(featureReferences));
	}

	/**
	 * Returns an array of plug-in and non-plug-in archive reference models
	 * on this site
	 * 
	 * @return an array of archive reference models, or an empty array if there are
	 * no archives known to this site.
	 * @since 2.0
	 */
	public IArchiveReference[] getArchives() {
		if (archiveReferences == null)
			return new ArchiveReference[0];

		return (ArchiveReference[]) archiveReferences.toArray(arrayTypeFor(archiveReferences));
	}

	/**
	 * Returns the unresolved URL string for the site.
	 *
	 * @return url string, or <code>null</code>
	 * @since 2.0
	 */
	public String getLocationURLString() {
		return locationURLString;
	}

	/**
	 * Returns the resolved URL for the site.
	 * 
	 * @return url, or <code>null</code>
	 * @since 2.0
	 */
	public URL getLocationURL() {
		return locationURL;
	}

	/**
	 * Sets the site description.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param description site description
	 * @since 2.0
	 */
	public void setDescriptionModel(URLEntry description) {
		assertIsWriteable();
		this.description = description;
	}

	/**
	 * Sets the feature references for this site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param featureReferences an array of feature reference models
	 * @since 2.0
	 */
	public void setFeatureReferenceModels(FeatureReference[] featureReferences) {
		assertIsWriteable();
		if (featureReferences == null)
			this.featureReferences = null;
		else
			this.featureReferences = new ArrayList(Arrays.asList(featureReferences));
	}

	/**
	 * Sets the archive references for this site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param archiveReferences an array of archive reference models
	 * @since 2.0
	 */
	public void setArchiveReferenceModels(ArchiveReference[] archiveReferences) {
		assertIsWriteable();
		if (archiveReferences == null)
			this.archiveReferences = null;
		else
			this.archiveReferences = new ArrayList(Arrays.asList(archiveReferences));
	}

	/**
	 * Sets the unresolved URL for the site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param locationURLString url for the site (as a string)
	 * @since 2.0
	 */
	public void setLocationURLString(String locationURLString) {
		assertIsWriteable();
		this.locationURLString = locationURLString;
	}

	/**
	 * Adds a feature reference model to site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param featureReference feature reference model
	 * @since 2.0
	 */
	public void addFeatureReference(FeatureReference featureReference) {
		assertIsWriteable();
		if (this.featureReferences == null)
			this.featureReferences = new ArrayList();
		// PERF: do not check if already present 
		//if (!this.featureReferences.contains(featureReference))
			this.featureReferences.add(featureReference);
	}

	/**
	 * Adds an archive reference model to site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param archiveReference archive reference model
	 * @since 2.0
	 */
	public void addArchiveReference(ArchiveReference archiveReference) {
		assertIsWriteable();
		if (this.archiveReferences == null)
			this.archiveReferences = new ArrayList();
		if (!this.archiveReferences.contains(archiveReference))
			this.archiveReferences.add(archiveReference);
	}

	/**
	 * Removes a feature reference model from site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param featureReference feature reference model
	 * @since 2.0
	 */
	public void removeFeatureReference(FeatureReference featureReference) {
		assertIsWriteable();
		if (this.featureReferences != null)
			this.featureReferences.remove(featureReference);
	}

	/**
	 * Removes an archive reference model from site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param archiveReference archive reference model
	 * @since 2.0
	 */
	public void removeArchiveReference(ArchiveReference archiveReference) {
		assertIsWriteable();
		if (this.archiveReferences != null)
			this.archiveReferences.remove(archiveReference);
	}

	/**
	 * Marks the model object as read-only.
	 * 
	 * @since 2.0
	 */
	public void markReadOnly() {
		super.markReadOnly();
		markReferenceReadOnly((URLEntry)getDescription());
		markListReferenceReadOnly((FeatureReference[])getFeatureReferences());
		markListReferenceReadOnly((ModelObject[])getArchives());
	}

	/**
	 * Resolve the model object.
	 * Any URL strings in the model are resolved relative to the 
	 * base URL argument. Any translatable strings in the model that are
	 * specified as translation keys are localized using the supplied 
	 * resource bundle.
	 * 
	 * @param base URL
	 * @param bundleURL resource bundle URL
	 * @exception MalformedURLException
	 * @since 2.0
	 */
	public void resolve(URL base, URL bundleURL) throws MalformedURLException {

		// Archives and feature are relative to location URL
		// if the Site element has a URL tag: see spec	
		locationURL = resolveURL(base, bundleURL, getLocationURLString());
		if (locationURL == null)
			locationURL = base;
		resolveListReference((FeatureReference[])getFeatureReferences(), locationURL, bundleURL);
		resolveListReference((ModelObject[])getArchives(), locationURL, bundleURL);

		resolveReference((URLEntry)getDescription(), base, bundleURL);
	}


	/**
	 * Returns the site description.
	 * 
	 * @see ISite#getDescription()
	 * @since 2.0
	 */
	public IURLEntry getDescription() {
		return description;
	}


	/**
	 * Sets the site content provider.
	 * 
	 * @see ISite#setSiteContentProvider(ISiteContentProvider)
	 * @since 2.0
	 */
	public void setSiteContentProvider(ISiteContentProvider siteContentProvider) {
		this.siteContentProvider = siteContentProvider;
	}

	
	/**
	 * Compares two sites for equality
	 * 
	 * @param object site object to compare with
	 * @return <code>true</code> if the two sites are equal, 
	 * <code>false</code> otherwise
	 * @since 2.0
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ISite))
			return false;
		if (getURL() == null)
			return false;
		ISite otherSite = (ISite) obj;

		return UpdateManagerUtils.sameURL(getURL(), otherSite.getURL());
	}
	
	/*
	 * Method filterFeatures.
	 * Also implemented in Feature
	 *  
	 * @param list
	 * @return List
	 */
	private ISiteFeatureReference[] filterFeatures(IFeatureReference[] allIncluded) {
		List list = new ArrayList();
		if (allIncluded!=null){
			for (int i = 0; i < allIncluded.length; i++) {
				IFeatureReference included = allIncluded[i];
				if (UpdateManagerUtils.isValidEnvironment(included))
					list.add(included);
				else{
					if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_WARNINGS){
						UpdateCore.warn("Filtered out feature reference:"+included);
					}
				}
			}
		}
		
		ISiteFeatureReference[] result = new ISiteFeatureReference[list.size()];
		if (!list.isEmpty()){
			list.toArray(result);
		}
		
		return result;	
	}
	
	/**
	 * Returns the site URL
	 * 
	 * @see ISite#getURL()
	 * @since 2.0
	 */
	public URL getURL() {
		URL url = null;
		try {
			url = getSiteContentProvider().getURL();
		} catch (CoreException e) {
			UpdateCore.warn(null, e);
		}
		return url;
	}

	/**
	 * Returns the content provider for this site.
	 * 
	 * @see ISite#getSiteContentProvider()
	 * @since 2.0
	 */
	public ISiteContentProvider getSiteContentProvider() throws CoreException {
		if (siteContentProvider == null) {
			throw Utilities.newCoreException(Policy.bind("Site.NoContentProvider"), null);
			//$NON-NLS-1$
		}
		return siteContentProvider;
	}
	
	public IFeature getFeature(
		VersionedIdentifier versionId,
		IProgressMonitor monitor)
		throws CoreException {

		IFeature[] features = getFeatures(monitor);
		if (features == null)
			return null;
		for (int i = 0; i < features.length; i++) {
			if (versionId.equals(features[i].getVersionedIdentifier()))
				return features[i];
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeatureReference(org.eclipse.update.core.VersionedIdentifier)
	 */
	public IFeatureReference getFeatureReference(VersionedIdentifier versionId) {
		if (versionId == null)
			return null;
		IFeatureReference[] featureRefs = getFeatureReferences();
		for (int i=0; i<featureRefs.length; i++)
			if (versionId.equals(featureRefs[i].getVersionedIdentifier()))
				return featureRefs[i];
		return null;
	}
	
	
	/**
	 * Creates the feature at specified url.
	 * This is a factory method that creates the full feature object.
	 * 
	 * @param monitor the progress monitor
	 * @param url the url of feature to get
	 * @return the feature located at specified url
	 * @since 3.0 
	 */
	private IFeature createFeature(URL url, IProgressMonitor monitor) throws CoreException {

		if (url == null)
			throw Utilities.newCoreException(Policy.bind("FeatureExecutableFactory.NullURL"), null);

		// the URL should point to a directory
		//url = validate(url);

		InputStream featureStream = null;
		if (monitor == null)
			monitor = new NullProgressMonitor();

		try {
			IFeatureContentProvider contentProvider = createFeatureContentProvider(url);
			URL nonResolvedURL = contentProvider.getFeatureManifestReference(null).asURL();
			URL resolvedURL = URLEncoder.encode(nonResolvedURL);
			featureStream = UpdateCore.getPlugin().get(resolvedURL).getInputStream();

			parser.init();
			Feature feature = parser.parse(featureStream);
			monitor.worked(1);
			
			feature.setSite(this);
			//feature.setFeatureContentProvider(contentProvider);
			feature.setURL(url);
			feature.resolve(url, url);
			feature.markReadOnly();
			//featureCache.put(url, feature);
			addFeatureReference(feature);
			features.add(feature);
			return feature;
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			throw Utilities.newCoreException(Policy.bind("FeatureFactory.CreatingError", url.toExternalForm()), e);
			//$NON-NLS-1$
		} finally {
			try {
				if (featureStream != null)
					featureStream.close();
			} catch (IOException e) {
			}
		}
	}
	

	/**
	 * Creates a feature content provider for the specified feature url
	 * @param url
	 * @return
	 * @throws CoreException
	 */
	protected abstract IFeatureContentProvider createFeatureContentProvider(URL url) throws CoreException;
	
	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeature(org.eclipse.update.core.IFeatureReference, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature getFeature(IFeatureReference featureRef, IProgressMonitor monitor)throws CoreException {
		if (featureRef instanceof IFeature)
			return (IFeature)featureRef;
		else {
			VersionedIdentifier versionId = featureRef.getVersionedIdentifier();
			if (versionId != null) {
				return getFeature(versionId,monitor);
			} else {
				return getFeature(featureRef.getURL(), monitor);
			}
		}	
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeature(java.net.URL, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature getFeature(URL url, IProgressMonitor monitor)
			throws CoreException {

		for (int i=0; url != null && i<features.size(); i++)
			if (url.equals(((IFeature)features.get(i)).getURL()))
				return (IFeature)features.get(i);
		return createFeature(url, null);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeatures(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature[] getFeatures(IProgressMonitor monitor)
			throws CoreException {

		if (!featuresLoaded) {
			IFeatureReference[] featureRefs = getFeatureReferences();
			for (int i=0; i<featureRefs.length; i++)
				// create implicity adds the features
				createFeature(featureRefs[i].getURL(), monitor);
			featuresLoaded = true;
		}
		return (IFeature[])features.toArray(new IFeature[features.size()]);
	}
}