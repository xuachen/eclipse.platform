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
import org.eclipse.update.internal.core.*;

/**
 * Base site content provider
 */
public class SiteContentProvider implements ISiteContentProvider {
	private URL base;
	private ISite site;

	/**
	 * Constructor for SiteContentProvider
	 */
	public SiteContentProvider(URL url) {
		super();
		this.base = url;
	}

	/**
	 * Returns the URL of this site
	 * 
	 * @see ISiteContentProvider#getURL()
	 * @since 2.0
	 */
	public URL getURL() {
		return base;
	}

	/**
	 * Returns a URL for the identified archive
	 * 
	 * @see ISiteContentProvider#getArchiveReference(String)
	 * @since 2.0
	 */
	public URL getArchiveReference(String archiveId) throws CoreException {
		URL contentURL = null;

		contentURL = getArchiveURLfor(archiveId);
		// if there is no mapping in the site.xml
		// for this archiveId, use the default one
		if (contentURL == null) {
			try {
				return new URL(getURL(), archiveId);
			} catch (MalformedURLException e) {
				throw Utilities.newCoreException(Policy.bind(
						"SiteContentProvider.ErrorCreatingURLForArchiveID",
						archiveId, getURL().toExternalForm()), e);
				//$NON-NLS-1$
			}
		}

		return contentURL;
	}

	/**
	 * Returns the site for this provider
	 * 
	 * @see ISiteContentProvider#getSite()
	 * @since 2.0
	 */
	public ISite getSite() {
		return site;
	}

	/**
	 * Sets the site for this provider
	 * 
	 * @param site
	 *            site for this provider
	 * @since 2.0
	 */
	public void setSite(ISite site) {
		this.site = site;
	}

	/**
	 * return the URL associated with the id of teh archive for this site
	 * return null if the archiveId is null, empty or if teh list of archives
	 * on the site is null or empty of if there is no URL associated with the
	 * archiveID for this site
	 */
	private URL getArchiveURLfor(String archiveId) {
		URL result = null;
		boolean found = false;

		IArchiveReference[] siteArchives = getSite().getArchives();
		if (siteArchives.length > 0) {
			for (int i = 0; i < siteArchives.length && !found; i++) {
				if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_INSTALL)
					UpdateCore.debug("GetArchiveURL for:" + archiveId
							+ " compare to " + siteArchives[i].getPath());
				if (archiveId.trim()
						.equalsIgnoreCase(siteArchives[i].getPath())) {
					result = siteArchives[i].getURL();
					found = true;
					break;
				}
			}
		}
		return result;
	}
}