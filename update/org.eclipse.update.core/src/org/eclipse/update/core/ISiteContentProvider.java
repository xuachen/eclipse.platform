package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
 
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
 
 /**
  * Provides 
  * 
  */
 //FIXME: javadoc
 
public interface ISiteContentProvider {
	
	/**
	 * Returns the site manifest 
	 * 
	 * @return the site manifest
	 * @since 2.0
	 */
	URL getSiteManifest() throws MalformedURLException;

	/**
	 * Returns a URL associated to the archiveID
	 * 
	 * @return a URL
	 * @since 2.0 
	 */

	URL getArchivesReferences(String archiveID);

	/**
	 * sets the site for this content provider
	 * @param the ISite 
	 * @since 2.0
	 */
	void setSite(ISite site);
	
	
}


