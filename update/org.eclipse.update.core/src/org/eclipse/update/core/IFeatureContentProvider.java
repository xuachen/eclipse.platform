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
 
public interface IFeatureContentProvider {
	
	/**
	 * Returns the feature manifest 
	 * 
	 * @return the feature manifest
	 * @since 2.0
	 */
	URL getFeatureManifest() throws MalformedURLException;

	/**
	 * Returns an array of content references for the whole Feature
	 * 
	 * @return an array of ContentReference or an empty array if no references are found
	 * @throws CoreException when an error occurs
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences() throws CoreException;

	/**
	 * Returns an array of content references for the IPluginEntry
	 * 
	 * @return an array of ContentReference or an empty array if no references are found
	 * @throws CoreException when an error occurs 
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences(IPluginEntry pluginEntry) throws CoreException;

	/**
	 * Returns an array of content references for the INONPluginEntry
	 * 
	 * @return an array of ContentReference or an empty array if no references are found
	 * @throws CoreException when an error occurs		 
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences(INonPluginEntry nonPluginEntry) throws CoreException;

	/**
	 * Returns an array of content references representing the Feature information
	 * 
	 * @return an array of ContentReference or an empty array if no references are found
	 * @throws CoreException when an error occurs	 
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences(IFeature feature) throws CoreException;

	/**
	 * Returns an array of content references composing the IFeature information
	 * 
	 * @return an array of ContentReference or an empty array if no references are found
	 * @throws CoreException when an error occurs
	 * @since 2.0 
	 */

	IContentReference[] getArchivesContentReferences(IFeature feature) throws CoreException;

	/**
	 * Returns an array of content references composing the IPluginEntry
	 * 
	 * @return an array of ContentReference or an empty array if no references are found
	 * @throws CoreException when an error occurs
	 * @since 2.0 
	 */

	IContentReference[] getArchivesContentReferences(IPluginEntry pluginEntry) throws CoreException;
	
	/**
	 * sets the feature for this content provider
	 * @param the IFeature 
	 * @since 2.0
	 */
	void setFeature(IFeature feature);
	
	
}


