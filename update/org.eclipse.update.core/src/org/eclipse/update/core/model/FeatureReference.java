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

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;

/**
 * Feature reference model object.
 * <p>
 * This class may be instantiated or subclassed by clients. However, in most 
 * cases clients should instead instantiate or subclass the provided 
 * concrete implementation of this model.
 * </p>
 * @see org.eclipse.update.core.FeatureReference
 * @since 2.0
 */
public class FeatureReference extends ModelObject implements IFeatureReference {

	protected URL url;
	protected String urlString;
	protected String featureId;
	protected String featureVersion;
	protected ISite site;
	protected String label;
	protected String localizedLabel;
	
	protected VersionedIdentifier versionId;

	// performance
	protected URL bundleURL;
	protected URL base;
	protected boolean resolved = false;
	protected String os;
	protected String ws;
	protected String nl;
	protected String arch;
	protected String patch;	

	private IFeature feature;
	
	/**
	 * Creates an uninitialized feature reference model object.
	 * 
	 * @since 2.0
	 */
	public FeatureReference() {
		super();
	}

	/**
	 * Constructor FeatureReferenceModel.
	 * @param ref
	 */
	public FeatureReference(FeatureReference ref) {
		setFeatureIdentifier(ref.getFeatureIdentifier());
		setFeatureVersion(ref.getFeatureVersion());
		setSite(ref.getSite());
		setLabel(ref.getLabel());
		setWS(ref.getWS());
		setOS(ref.getOS());
		setArch(ref.getOSArch());
		setNL(getNL());
		try {
			setURL(ref.getURL());
		} catch (CoreException e) {
			UpdateCore.warn("", e);
		}
	}

	/**
	 * Compares 2 feature reference models for equality
	 *  
	 * @param object feature reference model to compare with
	 * @return <code>true</code> if the two models are equal, 
	 * <code>false</code> otherwise
	 * @since 2.0 
	 */
	public boolean equals(Object object) {

		if (object == null)
			return false;
		if (this == object)
			return true;
		if (getURL() == null)
			return false;

		if (!(object instanceof FeatureReference))
			return false;

		FeatureReference f = (FeatureReference) object;

		return UpdateManagerUtils.sameURL(getURL(), f.getURL());
	}

	/**
	 * Returns the unresolved URL string for the reference.
	 *
	 * @return url string
	 * @since 2.0
	 */
	public String getURLString() {
		return urlString;
	}

	/**
	 * Returns the resolved URL for the feature reference.
	 * 
	 * @return url string
	 * @since 2.0
	 */
	public URL getURL() {
		delayedResolve();
		return url;
	}

	/**
	 * Returns the feature identifier as a string
	 * 
	 * @see org.eclipse.update.core.IFeatureReference#getVersionedIdentifier()
	 * @return feature identifier
	 * @since 2.0
	 */
	public String getFeatureIdentifier() {
		return featureId;
	}

	/**
	 * Returns the feature version as a string
	 * 
	 * @see org.eclipse.update.core.IFeatureReference#getVersionedIdentifier()
	 * @return feature version 
	 * @since 2.0
	 */
	public String getFeatureVersion() {
		return featureVersion;
	}

	/**
	 * Sets the site for the referenced.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param site site for the reference
	 * @since 2.0
	 */
	void setSite(ISite site) {
		assertIsWriteable();
		this.site = site;
	}

	/**
	 * Sets the unresolved URL for the feature reference.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param urlString unresolved URL string
	 * @since 2.0
	 */
	void setURLString(String urlString) {
		assertIsWriteable();
		this.urlString = urlString;
		this.url = null;
	}

	/**
	 * Sets the feature identifier.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param featureId feature identifier
	 * @since 2.0
	 */
	void setFeatureIdentifier(String featureId) {
		assertIsWriteable();
		this.featureId = featureId;
	}

	/**
	 * Sets the feature version.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param featureVersion feature version
	 * @since 2.0
	 */
	void setFeatureVersion(String featureVersion) {
		assertIsWriteable();
		this.featureVersion = featureVersion;
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
	public void resolve(URL base,URL bundleURL) throws MalformedURLException {
		this.base = base;
		this.bundleURL = bundleURL;
	}

	protected void delayedResolve() {

		// PERF: delay resolution
		if (resolved)
			return;

		resolved = true;
		// resolve local elements
		localizedLabel = resolveNLString(bundleURL, label);
		try {
			url = resolveURL(base, bundleURL, urlString);
		} catch (MalformedURLException e){
			UpdateCore.warn("",e);
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().toString() + " :");
		buffer.append(" at ");
		if (url != null)
			buffer.append(url.toExternalForm());
		return buffer.toString();
	}

	/**
	 * @see org.eclipse.update.core.model.ModelObject#getPropertyName()
	 */
	protected String getPropertyName() {
		return Site.SITE_FILE;
	}
	
	/**
	 * Retrieve the displayable label for the feature reference. If the model
	 * object has been resolved, the label is localized.
	 *
	 * @return displayable label, or <code>null</code>.
	 * @since 2.0
	 */
	public String getLabel() {
		delayedResolve();
		if (localizedLabel != null)
			return localizedLabel;
		else
			return label;
	}

	/**
	 * Retrieve the non-localized displayable label for the feature reference.
	 *
	 * @return non-localized displayable label, or <code>null</code>.
	 * @since 2.0
	 */
	public String getLabelNonLocalized() {
		return label;
	}

	/**
	 * Sets the label.
	 * @param label The label to set
	 */
	void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Sets the operating system specification.
	 * Throws a runtime exception if this object is marked read-only.
	 *
	 * @see org.eclipse.core.boot.BootLoader
	 * @param os operating system specification as a comma-separated list
	 * @since 2.1
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
	 * @since 2.1
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
	 * @since 2.1
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
	 * @since 2.1
	 */
	void setArch(String arch) {
		assertIsWriteable();
		this.arch = arch;
	}

	/**
	 * Returns the patch mode.
	 */
	public String getPatch() {
		return patch;
	}


	/**
	 * Sets the patch mode.
	 */
	void setPatch(String patch) {
		this.patch = patch;
	}

	/**
	 * Returns the feature this reference points to 
	 * @return the feature on the Site
	 * @deprecated
	 */
	private IFeature getFeature() throws CoreException {
		if (feature == null) {
			URL refURL = getURL();
			if (site != null)
				feature = site.getFeature(this,null);
			else
				throw Utilities.newCoreException("Feature reference does not have a site", null);
		}
		return feature;
	}


	/**
	 * Returns the update site for the referenced feature
	 * 
	 * @see IFeatureReference#getSite()
	 * @since 2.0 
	 */
	public ISite getSite() {
		return site;
	}

	/** 
	 * Sets the feature reference URL.
	 * This is typically performed as part of the feature reference creation
	 * operation. Once set, the url should not be reset.
	 * 
	 * @see IFeatureReference#setURL(URL)
	 * @since 2.0 
	 */
	void setURL(URL url) throws CoreException {
		if (url != null) {
			setURLString(url.toExternalForm());
			try {
				resolve(url, null);
			} catch (MalformedURLException e) {
				throw Utilities.newCoreException(Policy.bind("FeatureReference.UnableToResolveURL", url.toExternalForm()), e);
				//$NON-NLS-1$
			}
		}
	}


	/**
	* Returns the feature identifier.
	* 
	* @see IFeatureReference#getVersionedIdentifier()
	* @since 2.0
	*/
	public VersionedIdentifier getVersionedIdentifier() {

		if (versionId != null)
			return versionId;

		String id = getFeatureIdentifier();
		String ver = getFeatureVersion();
		if (id != null && ver != null) {
			try {
				versionId = new VersionedIdentifier(id, ver);
				return versionId;
			} catch (Exception e) {
				UpdateCore.warn("Unable to create versioned identifier:" + id + ":" + ver);
			}
		}

		// we need the exact match or we may have an infinite loop
		if (getURL() == null)
			return null;

		try {
			IFeature f = getSite().getFeature(getURL(), null);
			if (f != null)
				versionId = f.getVersionedIdentifier();
		} catch (CoreException e) {
			UpdateCore.warn("", e);
		}
		return versionId;
	}

	/**
	 * @see org.eclipse.update.core.IFeatureReference#getName()
	 */
	public String getName() {
		if (getLabel() != null)
			getLabel();
		try {
			return getFeature().getName();
		} catch (CoreException e) {
			return getVersionedIdentifier().toString();
		}
	}

	/**
	 * Get optional operating system specification as a comma-separated string.
	 *
	 * @see org.eclipse.core.boot.BootLoader
	 * @return the operating system specification string, or <code>null</code>.
	 * @since 2.1
	 */
	public String getOS() {
		if (os == null && getURL()!=null)
			try {
				return getFeature().getOS();
			} catch (CoreException e) {
				return null;
			}
		return os;
	}

	/**
	 * Get optional windowing system specification as a comma-separated string.
	 *
	 * @see org.eclipse.core.boot.BootLoader
	 * @return the windowing system specification string, or <code>null</code>.
	 * @since 2.1
	 */
	public String getWS() {
		if (ws == null && getURL()!=null)
			try {
				return getFeature().getWS();
			} catch (CoreException e) {
				return null;
			}
		return ws;
	}

	/**
	 * Get optional system architecture specification as a comma-separated string.
	 *
	 * @see org.eclipse.core.boot.BootLoader
	 * @return the system architecture specification string, or <code>null</code>.
	 * @since 2.1
	 */
	public String getOSArch() {
		if (arch == null && getURL()!=null)
			try {
				return getFeature().getOSArch();
			} catch (CoreException e) {
				return null;
			}
		return arch;
	}

	/**
	 * Get optional locale specification as a comma-separated string.
	 *
	 * @return the locale specification string, or <code>null</code>.
	 * @since 2.1
	 */
	public String getNL() {
		if (nl == null && getURL()!=null)
			try {
				return getFeature().getNL();
			} catch (CoreException e) {
				return null;
			}
		return nl;
	}

	/**
	 * Returns <code>true</code> if this feature is patching another feature,
	 * <code>false</code> otherwise
	 * @return boolean
	 */
	public boolean isPatch() {
		if (patch == null)
			try {
				return getFeature().isPatch();
			} catch (CoreException e) {
				return false;
			}
		return "true".equalsIgnoreCase(patch);
	}

}
