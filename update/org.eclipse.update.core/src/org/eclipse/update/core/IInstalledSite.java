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

import org.eclipse.core.runtime.*;
import org.eclipse.update.configuration.*;
import org.eclipse.update.core.model.*;

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
public interface IInstalledSite extends ISite {


	/**
	 * If we are unable to access a site, the returned CoreException will contain
	 * this return code.
	 * 
	 * @since 2.0.1
	 */
	public static final int SITE_ACCESS_EXCEPTION = 42;


	/**
	 * Returns an array of entries corresponding to plug-ins that are
	 * installed on this site and are referenced only by the specified
	 * feature. These are plug-ins that are not shared with any other
	 * feature.
	 * 
	 * @param feature feature
	 * @return an array of plug-in entries, or an empty array.
	 * @exception CoreException
	 * @since 2.0
	 */
	public IPluginEntry[] getPluginEntriesOnlyReferencedBy(IFeature feature) throws CoreException;

	/**
	 * Installs the specified feature on this site.
	 * Only optional features passed as parameter will be installed.
	 * 
	 * @param feature feature to install
	 * @param optionalfeatures list of optional features to be installed
	 * @param verifier jar verifier
	 * @param verificationListener install verification listener
	 * @param monitor install monitor, can be <code>null</code>
	 * @exception InstallAbortedException when the user cancels the install
	 * @exception CoreException
	 * @since 2.0 
	 */
	public IFeature install(IFeature feature, IFeature[] optionalfeatures, IVerifier verifier, IVerificationListener verificationListener, IProgressMonitor monitor) throws InstallAbortedException, CoreException;

	/**
	 * Removes (uninstalls) the specified feature from this site. This method
	 * takes into account plug-in entries referenced by the specified fetaure
	 * that continue to be required by other features installed on this site.
	 * 
	 * @param feature feature to remove
	 * @param monitor progress monitor
	 * @exception CoreException
	 * @since 2.0 
	 */
	public void remove(IFeature feature, IProgressMonitor monitor) throws CoreException;

	/** 
	 * Returns the <code>IConfiguredSite</code> for this site in the current 
	 * configuration or <code>null</code> if none found.
	 * 
	 * @since 2.0.2
	 */
	public IConfiguredSite getCurrentConfiguredSite();

}
