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
 * Site content provider.
 * A site content provider is an abstraction of each site internal 
 * organization. It allows the site content to be accessed in
 * a standard way regardless of the internal organization. All concrete site
 * implementations need to implement a site content provider.
 * <p>
 * Clients may implement this interface. However, in most cases clients should 
 * directly instantiate or subclass the provided implementation of this 
 * interface.
 * </p>
 * @see org.eclipse.update.core.SiteContentProvider
 * @since 2.0
 */
 
public interface ISiteContentProvider {
	
	/**
	 * Returns the URL of this site
	 * 
	 * @return site URL
	 * @since 2.0
	 */	
	public URL getURL();
			
	
	/**
	 * Returns a URL for the identified archive 
	 * 
	 * @param id archive identifier
	 * @return archive URL, or <code>null</code>.
	 * @exception CoreException 
	 * @since 2.0 
	 */
	public URL getArchiveReference(String id)  throws CoreException;

	/**
	 * Returns the site for this provider
	 * 
	 * @return provider site
	 * @since 2.0
	 */
	public ISite getSite();	

//
//	/**
//	 * Returns an array of entries corresponding to plug-ins installed
//	 * on this site.
//	 * 
//	 * @return array of plug-in entries,or an empty array.
//	 * @since 2.0
//	 */
//	public IPluginEntry[] getPluginEntries();
}


