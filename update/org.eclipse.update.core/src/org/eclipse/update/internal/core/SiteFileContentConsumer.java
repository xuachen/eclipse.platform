package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.model.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.InvalidSiteTypeException;
import org.eclipse.update.internal.core.obsolete.FeaturePackaged;

/**
 * Site on the File System
 */
public class SiteFileContentConsumer extends SiteContentConsumer {
	
	private String path;
	
	
	public static final String INSTALL_FEATURE_PATH = "install/features/";
	public static final String SITE_TYPE = "org.eclipse.update.core.file";	

	/**
	 * Constructor for FileSite
	 */
	public SiteFileContentConsumer(){
	}

	/**
	 * @see IPluginContainer#store(IPluginEntry, String, InputStream)
	 */
	public void store(IPluginEntry pluginEntry, String contentKey, InputStream inStream) throws CoreException {

		String path = UpdateManagerUtils.getPath(site.getURL());

		// FIXME: fragment code
		String pluginPath = null;
		if (pluginEntry.isFragment()) {
			pluginPath = path + Site.DEFAULT_FRAGMENT_PATH + pluginEntry.getIdentifier().toString();
		} else {
			pluginPath = path + Site.DEFAULT_PLUGIN_PATH + pluginEntry.getIdentifier().toString();
		}
		pluginPath += pluginPath.endsWith(File.separator) ? contentKey : File.separator + contentKey;

		try {
			UpdateManagerUtils.copyToLocal(inStream, pluginPath, null);

		} catch (IOException e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error creating file:" + pluginPath, e);
			throw new CoreException(status);
		} finally {
			try {
				// close stream
				inStream.close();
			} catch (Exception e) {}
		}
	}

	/**
	 * store DefaultFeature files
	 * Store the inputStream into a file named contentKey in the install feature path of the feature
	 */
	public void storeFeatureInfo(VersionedIdentifier featureIdentifier, String contentKey, InputStream inStream) throws CoreException {

		String featurePath = getFeaturePath(featureIdentifier);
		featurePath += featurePath.endsWith(File.separator) ? contentKey : File.separator+contentKey;		
		try {
			UpdateManagerUtils.copyToLocal(inStream, featurePath, null);
		} catch (IOException e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error creating file:" + featurePath, e);
			throw new CoreException(status);
		} finally {
			try {
				// close stream
				inStream.close();
			} catch (Exception e) {}
		}

	}

	/**
 	 * move into contentSelector, comment to provider and consumer (SiteFile)
 	 */
	private String getFeaturePath(VersionedIdentifier featureIdentifier) {
		String path = UpdateManagerUtils.getPath(site.getURL());
		String featurePath = path + INSTALL_FEATURE_PATH + featureIdentifier.toString();
		return featurePath;
	}
	
	/*
	 * @see Site#removeFeatureInfo(VersionedIdentifier)
	 */
	protected void removeFeatureInfo(VersionedIdentifier featureIdentifier) throws CoreException {

		String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();		
		MultiStatus multiStatus = new MultiStatus(id,IStatus.ERROR,"Some files cannot be removed",null);
		
		String featurePath = getFeaturePath(featureIdentifier);
		File file = new File(featurePath);
		removeFromFileSystem(file,multiStatus);
		
		if (multiStatus.getChildren().length>0){
			throw new CoreException(multiStatus);
		}
		
	}

	/*
	 * @see IPluginContainer#remove(IPluginEntry)
	 */
	public void remove(IPluginEntry pluginEntry) throws CoreException {
		
		String path = UpdateManagerUtils.getPath(site.getURL());

		// FIXME: fragment code
		String pluginPath = null;
		if (pluginEntry.isFragment()) {
			pluginPath = path + Site.DEFAULT_FRAGMENT_PATH + pluginEntry.getIdentifier().toString();
		} else {
			pluginPath = path + Site.DEFAULT_PLUGIN_PATH + pluginEntry.getIdentifier().toString();
		}

		String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();		
		MultiStatus multiStatus = new MultiStatus(id,IStatus.ERROR,"Some files cannot be removed",null);

		File file = new File(pluginPath);
		removeFromFileSystem(file,multiStatus);
		
		if (multiStatus.getChildren().length>0){
			throw new CoreException(multiStatus);
		}
	}
	
	/**
	 * remove a file or directory from the file system.
	 */
	public static void removeFromFileSystem(File file, MultiStatus multiStatus) throws CoreException{

		if (!file.exists())
			return;
		if (file.isDirectory()) {
			String[] files = file.list();
			if (files != null) // be careful since file.list() can return null
				for (int i = 0; i < files.length; ++i)
					removeFromFileSystem(new File(file, files[i]),multiStatus);
		}
		if (!file.delete()) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();				
			IStatus status = new Status(IStatus.WARNING,id,IStatus.OK,"cannot remove: " + file.getPath()+" from the filesystem",new Exception());
			multiStatus.add(status);
		}
		
	}


	/*
	 * @see ISiteContentConsumer#open(INonPluginEntry)
	 */
	public IContentConsumer open(INonPluginEntry nonPluginEntry) throws CoreException {
		return null;
	}

	/*
	 * @see ISiteContentConsumer#open(IPluginEntry)
	 */
	public IContentConsumer open(IPluginEntry pluginEntry) throws CoreException {
		return null;
	}

	/*
	 * @see ISiteContentConsumer#store(ContentReference, IProgressMonitor)
	 */
	public void store(ContentReference contentReference, IProgressMonitor monitor) throws CoreException {
	}

	/*
	 * @see ISiteContentConsumer#remove(ContentReference, IProgressMonitor)
	 */

	/*
	 * @see ISiteContentConsumer#close()
	 */
	public void close() {
	}

}