package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.InvalidSiteTypeException;
import org.eclipse.update.core.model.SiteModelFactory;

public class SiteFileFactory extends BaseSiteFactory {


	/*
	 * @see ISiteFactory#createSite(URL)
	 */
	public ISite createSite(URL url) throws CoreException {

		Site site = null;
		InputStream siteStream = null;
		
		try {		
			SiteFileContentProvider contentProvider = new SiteFileContentProvider(url);
			SiteFileContentConsumer contentConsumer = new SiteFileContentConsumer();
					
			try {
				siteStream = contentProvider.getSiteManifestReference().asURL().openStream();
				SiteModelFactory factory = (SiteModelFactory) this;
				site = (Site)factory.parseSite(siteStream);		
			} catch (IOException e) {
				site = contentProvider.parseSite();
			}
			
			site.setSiteContentProvider(contentProvider);
			site.setSiteContentConsumer(contentConsumer);			
			
			site.resolve(url, getResourceBundle(url));
			site.markReadOnly();			
			
		} catch (IOException e) {
			// if we cannot find the feature or the feature.xml...
			// We should not stop the execution 
			// but we must Log it all the time, not only when debugging...
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, "Error opening site.xml in the site:" + url.toExternalForm(), e);
			UpdateManagerPlugin.getPlugin().getLog().log(status);
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, "Error parsing site.xml in the site:" + url.toExternalForm(), e);
			throw new CoreException(status);
		} finally {
			try {
				siteStream.close();
			} catch (Exception e) {
			}
		}
		return site;
	}
		

}
