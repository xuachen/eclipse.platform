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
 * Site represents an update location containing some number of features (packaged
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
public interface IUpdateSite extends ISite {

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
	 * Returns an array of categories defined by the site.
	 * 
	 * @return array of site categories, or an empty array.
	 * @since 2.0 
	 */
	public ICategory[] getCategories();

	/**
	 * Returns the named site category.
	 * 
	 * @param name category name
	 * @return named category, or <code>null</code> ifit does not exist
	 * @since 2.0
	 */
	public ICategory getCategory(String name);

	/**
	 * Returns a reference to the specified feature if 
	 * it is installed on this site.
	 * filtered by the operating system, windowing system and architecture
	 * system set in <code>Sitemanager</code>
	 * 
	 * @param feature feature
	 * @return feature reference, or <code>null</code> if this feature
	 * cannot be located on this site.
	 * @since 2.0
	 */
	public IFeatureReference getFeatureReference(IFeature feature);
}
