package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;

/**
 * Default implementation of an Executable Feature Content Provider
 */

public class FeatureExecutableContentProvider implements IFeatureContentProvider {

	/**
	 * URL of the feature, used to create other URLs
	 */
	private URL rootURL;

	/**
	 * the feature 
	 */
	private IFeature feature;

	/**
	 * Constructor for DefaultExecutableFeature
	 */
	public FeatureExecutableContentProvider(URL url) throws CoreException {
		this.rootURL = url;
		try {
			if (!rootURL.getFile().endsWith("/")) {
				rootURL = new URL(rootURL.getProtocol(), rootURL.getHost(), rootURL.getFile() + "/");
			}
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error adding '/' at the end of URL:" + url.toExternalForm(), e);
			throw new CoreException(status);
		}
	}

	/**
	 * return the path for a pluginEntry
	 */
	private String getPath(IPluginEntry pluginEntry) throws Exception {
		String result = null;

		// get the URL of the Archive file that contains the plugin entry
		ISiteContentProvider provider = feature.getSite().getSiteContentProvider();
		URL fileURL = provider.getArchivesReferences(getArchiveID(pluginEntry));
		result = UpdateManagerUtils.getPath(fileURL);

		// return the list of all subdirectories
		if (!result.endsWith(File.separator))
			result += File.separator;
		File pluginDir = new File(result);
		if (!pluginDir.exists())
			throw new IOException("The File:" + result + "does not exist.");

		return result;
	}

	/**
	 * return the path for the Feature
	 */
	private String getFeaturePath() throws IOException {
		String result = UpdateManagerUtils.getPath(feature.getURL());
		;

		// return the list of all subdirectories
		if (!(result.endsWith(File.separator) || result.endsWith("/")))
			result += File.separator;
		File pluginDir = new File(result);
		if (!pluginDir.exists())
			throw new IOException("The File:" + result + "does not exist.");

		return result;
	}

	/**
	 * return all the files under the directory
	 */
	private List getFiles(File dir) throws IOException {
		List result = new ArrayList();

		if (!dir.isDirectory())
			throw new IOException(dir.getPath() + " is not a valid directory");

		File[] files = dir.listFiles();
		if (files != null) // be careful since it can be null
			for (int i = 0; i < files.length; ++i) {
				if (files[i].isDirectory()) {
					result.addAll(getFiles(files[i]));
				} else {
					result.add(files[i]);
				}
			}
		return result;
	}

	/**
	 * return the archive ID for a plugin
	 * The id is based on the feature
	 * the default ID is plugins/pluginId_pluginVer or
	 * the default ID is fragments/pluginId_pluginVer or
		*/
	public String getArchiveID(IPluginEntry entry) {
		//FIXME: fragments
		String type = (entry.isFragment()) ? Site.DEFAULT_FRAGMENT_PATH : Site.DEFAULT_PLUGIN_PATH;
		return type + entry.getIdentifier().toString();
	}

	/*
	 * @see IFeatureContentProvider#getFeatureManifest()
	 */
	public URL getFeatureManifest() throws MalformedURLException {
		return new URL(rootURL, Feature.FEATURE_XML);
	}

	/*
	 * @see IFeatureContentProvider#getArchivesReferences()
	 */
	public IContentReference[] getArchivesReferences() throws CoreException {
		return null;
	}

	/*
	 * @see IFeatureContentProvider#getArchivesReferences(IPluginEntry)
	 */
	public IContentReference[] getArchivesReferences(IPluginEntry pluginEntry) throws CoreException {
		IContentReference[] result = new IContentReference[1];
		try {
			// get the URL of the Archive file that contains the plugin entry
			ISiteContentProvider provider = feature.getSite().getSiteContentProvider();
			URL fileURL = provider.getArchivesReferences(getArchiveID(pluginEntry));
			String fileString = UpdateManagerUtils.getPath(fileURL);

			// return the list of all subdirectories
			if (!fileString.endsWith(File.separator))
				fileString += File.separator;
			File pluginDir = new File(fileString);
			if (!pluginDir.exists())
				throw new IOException("The File:" + fileString + "does not exist.");

			result[1] = new ContentReference(pluginDir.toURL(), null);
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving archive names for:" + pluginEntry.getIdentifier().toString(), e);
			throw new CoreException(status);
		}
		return result;
	}

	/*
	 * @see IFeatureContentProvider#getArchivesReferences(INonPluginEntry)
	 */
	public IContentReference[] getArchivesReferences(INonPluginEntry nonPluginEntry) throws CoreException {
		IContentReference[] result = new IContentReference[1];
		try {
			// get the URL of the Archive file that contains the plugin entry
			ISiteContentProvider provider = feature.getSite().getSiteContentProvider();
			URL fileURL = provider.getArchivesReferences(nonPluginEntry.getIdentifier());
			String fileString = UpdateManagerUtils.getPath(fileURL);

			// return the list of all subdirectories
			if (!fileString.endsWith(File.separator))
				fileString += File.separator;
			File pluginDir = new File(fileString);
			if (!pluginDir.exists())
				throw new IOException("The File:" + fileString + "does not exist.");

			result[1] = new ContentReference(pluginDir.toURL(), null);
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving archive references:" + nonPluginEntry.getIdentifier().toString(), e);
			throw new CoreException(status);
		}
		return result;
	}

	/*
	 * @see IFeatureContentProvider#getArchivesReferences(IFeature)
	 */
	public IContentReference[] getArchivesReferences(IFeature feature) throws CoreException {
		IContentReference[] contentReferences = new ContentReference[1];
		contentReferences[1] = new ContentReference(rootURL, null);
		return contentReferences;
	}

	/*
	 * @see IFeatureContentProvider#getArchivesContentReferences(IFeature)
	 */
	public IContentReference[] getArchivesContentReferences(IFeature feature) throws CoreException {
		IContentReference[] result = new IContentReference[0];
		try {
			File featureDir = new File(getFeaturePath());
			List files = getFiles(featureDir);
			result = new IContentReference[files.size()];
			for (int i = 0; i < result.length; i++) {
				File currentFile = (File) files.get(i);
				result[i] = new ContentReference(currentFile.toURL(), null);
			}
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving archive content references for:" + feature.getIdentifier().toString(), e);
			throw new CoreException(status);
		}
		return result;
	}

	/*
	 * @see IFeatureContentProvider#getArchivesContentReferences(IPluginEntry)
	 */
	public IContentReference[] getArchivesContentReferences(IPluginEntry pluginEntry) throws CoreException {

		IContentReference[] result = new IContentReference[0];

		try {
			// return the list of all subdirectories
			File pluginDir = new File(getPath(pluginEntry));
			List files = getFiles(pluginDir);
			result = new IContentReference[files.size()];
			for (int i = 0; i < result.length; i++) {
				File currentFile = (File) files.get(i);
				result[i] = new ContentReference(currentFile.toURL(), null);
			}
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving archive content references for:" + pluginEntry.getIdentifier().toString(), e);
			throw new CoreException(status);
		}
		return result;
	}

	/*
	 * @see IFeatureContentProvider#setFeature(IFeature)
	 */
	public void setFeature(IFeature feature) {
		this.feature = feature;
	}

}