package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
 
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
 
 /**
  * A content consumer manages the storage or archives, plugins and
  * feature inside an <code>IFeature</code> or an <code> ISite</code>
  * 
  * A ContentConsumer has a <i>parent</i> which is the entry (IFeature, IPluginEntry
  * or INonPluginEntry) that is going to be used for storage.
  * 
  * Only a ContentConsumer with an IFeature parent can open sub-ContentConsumer.
  */
 
public interface IContentConsumer {

	/**
	 * opens a Non plugin Entry for storage
	 * @return the new ContentConsumer for this <code>INonPluginEntry</code>
	 * @throws CoreException if the opens is done on a ContentConsumer parent other than an IFeature.
	 * @since 2.0 
	 */

	IContentConsumer opens(INonPluginEntry nonPluginEntry) throws CoreException;

	/**
	 * opens a Non plugin Entry for storage
	 * @return the new ContentConsumer for this <code>IPluginEntry</code>
	 * @since 2.0 
	 */

	IContentConsumer opens(IPluginEntry pluginEntry) throws CoreException;
	
	/**
	 * Stores a content reference into the ContentConsumer
	 * @param ContentReference the content reference to store
	 * @param IProgressMonitor the progress monitor
	 * @since 2.0 
	 */

	void store(ContentReference contentReference, IProgressMonitor monitor);
	
	/**
	 * removes a content reference into the ContentConsumer
	 * @param ContentReference the content reference to remove
	 * @param IProgressMonitor the progress monitor
	 * @since 2.0 
	 */

	void remove(ContentReference contentReference, IProgressMonitor monitor);	
	
	/**
	 * closes the opened ContentConsumer
	 * @since 2.0 
	 */

	void close();		
}


