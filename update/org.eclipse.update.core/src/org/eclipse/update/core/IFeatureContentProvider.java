package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
 
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
 
 /**
  * Provides 
  * 
  */
 //FIXME: javadoc
 
public interface IFeatureContentProvider {

	/**
	 * Returns an array of content references for the whole Feature
	 * 
	 * @return an array of ContentReferenece or an empty array if no references are found
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences();

	/**
	 * Returns an array of content references for the IPluginEntry
	 * 
	 * @return an array of ContentReferenece or an empty array if no references are found
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences(IPluginEntry pluginEntry);

	/**
	 * Returns an array of content references for the INONPluginEntry
	 * 
	 * @return an array of ContentReferenece or an empty array if no references are found
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences(INonPluginEntry nonPluginEntry);

	/**
	 * Returns an array of content references representing the Feature information
	 * 
	 * @return an array of ContentReferenece or an empty array if no references are found
	 * @since 2.0 
	 */

	IContentReference[] getArchivesReferences(IFeature feature);

	/**
	 * Returns an array of content references composing the IFeature information
	 * 
	 * @return an array of ContentReferenece or an empty array if no references are found
	 * @since 2.0 
	 */

	IContentReference[] getArchivesContentReferences(IFeature feature);

	/**
	 * Returns an array of content references composing the IPluginEntry
	 * 
	 * @return an array of ContentReferenece or an empty array if no references are found
	 * @since 2.0 
	 */

	IContentReference[] getArchivesContentReferences(IPluginEntry feature);
	
}


