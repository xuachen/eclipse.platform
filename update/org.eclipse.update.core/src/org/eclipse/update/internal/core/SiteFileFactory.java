package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFactory;
import org.eclipse.update.core.model.InvalidSiteTypeException;

public class SiteFileFactory implements ISiteFactory {


	/*
	 * @see ISiteFactory#createSite(URL)
	 */
	public ISite createSite(URL url) throws CoreException, InvalidSiteTypeException {
		return new SiteFile(url);
	}

}
