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
import org.xml.sax.*;


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
	
	/**
	 * Creates and populates a default feature from stream.
	 * The parser assumes the stream contains a default feature manifest
	 * (feature.xml) as documented by the platform.
	 * 
	 * @param stream feature stream
	 * @return populated feature model
	 * @exception ParsingException
	 * @exception IOException
	 * @exception SAXException
	 * @since 2.0
	 */
	public Feature parseFeature(InputStream stream)
		throws CoreException, SAXException {
		parser.init();
		Feature featureModel = null;
		try {
			featureModel = parser.parse(stream);
			if (parser.getStatus()!=null) {
				// some internalError were detected
				IStatus status = parser.getStatus();
				throw new CoreException(status);
			}
		} catch (IOException e) {
			throw Utilities.newCoreException(Policy.bind("FeatureModelFactory.ErrorAccesingFeatureStream"), e); //$NON-NLS-1$
		}
		return featureModel;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeature(org.eclipse.update.core.VersionedIdentifier, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature getFeature(
		VersionedIdentifier versionId,
		IProgressMonitor monitor)
		throws CoreException {

		IFeature[] features = getFeatures(monitor);
		for (int i = 0; i < features.length; i++) {
			if (versionId.equals(features[i].getVersionedIdentifier()))
				return features[i];
		}

		return null;
	}

}