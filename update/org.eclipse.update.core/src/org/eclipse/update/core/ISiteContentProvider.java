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
	 * Returns the site manifest reference or null if it doesn't exist
	 * 
	 * @return the site manifest
	 * @since 2.0
	 */
	ContentReference getSiteManifestReference() throws MalformedURLException;

	/**
	 * Returns a ContentReference associated to the archiveID
	 * 
	 * @return a URL
	 * @since 2.0 
	 */

	ContentReference getArchivesReferences(String archiveID);
	
	/**
	 * Returns a ContentReference for this feature
	 * If the feature doesn't exist yet, create a new content reference
	 * 
	 * @return a content reference
	 * @since 2.0 
	 */

	ContentReference getFeatureArchivesReferences(IFeature feature);	

	/**
	 * sets the site for this content provider
	 * @param the ISite 
	 * @since 2.0
	 */
	void setSite(ISite site);
	
	
}


