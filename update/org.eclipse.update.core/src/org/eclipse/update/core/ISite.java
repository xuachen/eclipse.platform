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

import org.eclipse.core.runtime.*;

/**
 * Site represents a location containing some number of features (packaged
 * or installed). Sites are treated purely as an installation and packaging
 * construct. They do not play a role during Eclipse plug-in execution. 
 * <p>
 * Clients may implement this interface. However, in most cases clients should 
 * directly instantiate or subclass the provided implementation of this 
 * interface.
 * </p>
 * @see org.eclipse.update.core.Site
 * @since 2.0
 */
public interface ISite extends IAdaptable {

	/**
	 * Default type for an installed feature. Different concrete feature
	 * implementations can be registered together with their corresponding type
	 * using the <code>org.eclipse.update.core.featureTypes</code> 
	 * extension point.
	 * 
	 * @since 2.0
	 */
	public static final String DEFAULT_INSTALLED_FEATURE_TYPE = "org.eclipse.update.core.installed";
	//$NON-NLS-1$		

	/**
	 * Default type for a packaged feature. Different concrete feature
	 * implementations can be registered together with their corresponding type
	 * using the <code>org.eclipse.update.core.featureTypes</code> 
	 * extension point.
	 * 
	 * @since 2.0
	 */
	public static final String DEFAULT_PACKAGED_FEATURE_TYPE = "org.eclipse.update.core.packaged";
	//$NON-NLS-1$		

	/**
	 * If we are unable to access a site, the returned CoreException will contain
	 * this return code.
	 * 
	 * @since 2.0.1
	 */
	public static final int SITE_ACCESS_EXCEPTION = 42;

	/**
	 * Returns the site URL
	 * 
	 * @return site URL
	 * @since 2.0 
	 */
	public URL getURL();

	/**
	 * Returns the site description.
	 * 
	 * @return site description, or <code>null</code>.
	 * @since 2.0 
	 */
	public IURLEntry getDescription();

	/**
	 * Returns an array of references to features on this site.
	 * 
	 * @return an array of feature references, or an empty array.
	 * @since 2.0 
	 */
	public IFeatureReference[] getFeatureReferences();

	/**
	 * Returns an array of plug-in and non-plug-in archives located
	 * on this site
	 * 
	 * @return an array of archive references, or an empty array if there are
	 * no archives known to this site. Note, that an empty array does not
	 * necessarily indicate there are no archives accessible on this site.
	 * It simply indicates the site has no prior knowledge of such archives.
	 * @since 2.0 
	 */
	public IArchiveReference[] getArchives();

	/**
	 * Returns an array of references to features on this site.
	 * 
	 * @return an array of feature references, or an empty array.
	 * @since 2.0 
	 */
	public IFeature[] getFeatures(IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns the referenced feature.
	 * This is a factory method that creates the full feature object.
	 * 
	 * @param monitor the progress monitor
	 * @param the referenced feature
	 * @return the feature referenced by featureRef
	 * @since 3.0 
	 */
	public IFeature getFeature(IFeatureReference featureRef, IProgressMonitor monitor) throws CoreException;

	
	/**
	 * Returns the feature of the specified version.
	 * This is a factory method that creates the full feature object.
	 * 
	 * @param monitor the progress monitor
	 * @param versionId the id and version of feature to get
	 * @return the feature referenced by versionId
	 * @since 3.0 
	 */
	public IFeature getFeature(VersionedIdentifier versionId, IProgressMonitor monitor) throws CoreException;

	
	/**
	 * Returns the feature at specified url.
	 * This is a factory method that creates the full feature object.
	 * 
	 * @param monitor the progress monitor
	 * @param url the url of feature to get
	 * @return the feature located at specified url
	 * @since 3.0 
	 */
	public IFeature getFeature(URL url, IProgressMonitor monitor) throws CoreException;
	
	
	/**
	 * Returns a reference to the specified feature if 
	 * it is installed on this site.
	 * filtered by the operating system, windowing system and architecture
	 * system set in <code>Sitemanager</code>
	 * 
	 * @param versionId versioned identifier for feature to find
	 * @return feature reference, or <code>null</code> if this feature
	 * cannot be located on this site.
	 * @since 2.0
	 */
	public IFeatureReference getFeatureReference(VersionedIdentifier versionId);
}
