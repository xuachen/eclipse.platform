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
package org.eclipse.update.core.model;
import java.io.*;
import java.net.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;
import org.xml.sax.*;

public class InstalledSiteParser {
	
	// private when parsing file system
	private InstalledSite site;

	public InstalledSiteParser() {
	}
	
	public void init() {
		// PERF: separate instance creation from parsing
	}
	
	/*
	 * @see ISiteFactory#createSite(URL,boolean)
	 */
	public IInstalledSite parse(URL url) throws CoreException{
		InputStream siteStream = null;
	
		try {
			// if url points to a directory
			// attempt to parse site.xml
			String path = url.getFile();
			File siteLocation = new File(path);
			if (siteLocation.isDirectory()) {
				site = parseSite(siteLocation);
			} else {
				throw Utilities.newCoreException(Policy.bind("SiteFileFactory.DirectoryDoesNotExist", siteLocation.getAbsolutePath()), null);
			}
//	
//			SiteContentProvider contentProvider = new SiteFileContentProvider(url);
//			site.setSiteContentProvider(contentProvider);
//			contentProvider.setSite(site);
			site.resolve(url, url);
	
			// Do not set read only as may install in it
			//site.markReadOnly();
		} catch (MalformedURLException e) {
			throw Utilities.newCoreException(Policy.bind("SiteFileFactory.UnableToCreateURL", url == null ? "" : url.toExternalForm()), e);
			//$NON-NLS-1$
		} finally {
			try {
				if (siteStream != null)
					siteStream.close();
			} catch (IOException e) {
			}
		}
		return site;
	}
	/**
	 * Method parseSite.
	 */
	private InstalledSite parseSite(File directory) throws CoreException {

		this.site = new InstalledSite();

		if (!directory.exists())
			throw Utilities.newCoreException(Policy.bind("SiteFileFactory.FileDoesNotExist", directory.getAbsolutePath()), null);
		//$NON-NLS-1$

		File pluginPath = new File(directory, Site.DEFAULT_PLUGIN_PATH);

		//PACKAGED
		parsePackagedFeatures(directory); // in case it contains JAR files

		parsePackagedPlugins(pluginPath);

		// INSTALLED	
		parseInstalledFeatures(directory);

		parseInstalledPlugins(pluginPath);

		return site;

	}

	/**
	 * Method parseFeature.
	 * @throws CoreException
	 */
	private void parseInstalledFeatures(File directory) throws CoreException {

		File featuresDir = new File(directory, Site.DEFAULT_INSTALLED_FEATURE_PATH);
		if (featuresDir.exists()) {

			FeatureReference featureRef;
			URL featureURL;


			// handle the installed features under the features directory
			File[] dirs = featuresDir.listFiles(new FileFilter() {
				public boolean accept(File f) {
					boolean valid = f.isDirectory() && (new File(f,Feature.FEATURE_XML).exists());
					if (!valid)
						UpdateCore.warn("Unable to find feature.xml in directory:" + f.getAbsolutePath());
					return valid;
				}
			});
			try {
				for (int index = 0; index < dirs.length; index++) {
					// PERF: remove code
					//SiteFileFactory archiveFactory = new SiteFileFactory();
					featureURL = dirs[index].toURL();
					featureRef = new FeatureReference();
					featureRef.setSite(site);
					featureRef.setURLString(featureURL.toExternalForm());
					site.addFeatureReference(featureRef);
				}
			} catch (MalformedURLException e) {
				throw Utilities.newCoreException(Policy.bind("SiteFileFactory.UnableToCreateURLForFile", featuresDir.getAbsolutePath()), e);
				//$NON-NLS-1$
			}
		}
	}

	/**
	* Method parseFeature.
	* @throws CoreException
	*/
	private void parsePackagedFeatures(File directory) throws CoreException {

		// FEATURES
		File featureDir = new File(directory, Site.DEFAULT_FEATURE_PATH);
		if (featureDir.exists()) {
			String[] dir;
			FeatureReference featureRef;
			URL featureURL;
			File currentFeatureFile;
			String newFilePath = null;

			try {
				// only list JAR files
				dir = featureDir.list(FeaturePackagedContentProvider.filter);
				for (int index = 0; index < dir.length; index++) {

					// check if the JAR file contains a feature.xml
					currentFeatureFile = new File(featureDir, dir[index]);
					JarContentReference ref = new JarContentReference("", currentFeatureFile);
					ContentReference result = null;
					try {
						result = ref.peek(Feature.FEATURE_XML, null, null);
					} catch (IOException e) {
						UpdateCore.warn("Exception retrieving feature.xml in file:" + currentFeatureFile, e);
					}
					if (result == null) {
						UpdateCore.warn("Unable to find feature.xml in file:" + currentFeatureFile);
					} else {
						featureURL = currentFeatureFile.toURL();
						// PERF: remove code
						//SiteFileFactory archiveFactory = new SiteFileFactory();
						featureRef = new FeatureReference();
						featureRef.setSite(site);
						featureRef.setURLString(featureURL.toExternalForm());
						site.addFeatureReference(featureRef);
					}
				}
			} catch (MalformedURLException e) {
				throw Utilities.newCoreException(Policy.bind("SiteFileFactory.UnableToCreateURLForFile", newFilePath), e);
				//$NON-NLS-1$
			}
		}
	}

	/**
	 * Method parsePlugins.
	 * 
	 * look into each plugin/fragment directory, crack the plugin.xml open (or fragment.xml ???)
	 * get id and version, calculate URL...	
	 * 
	 * @return VersionedIdentifier
	 * @throws CoreException
	 */
	private void parseInstalledPlugins(File dir) throws CoreException {
		File pluginFile = null;

		try {
			if (dir.exists() && dir.isDirectory()) {
				File[] files = dir.listFiles();
				PluginParser parser = new PluginParser();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {

						if (!(pluginFile = new File(files[i], "plugin.xml")).exists()) { //$NON-NLS-1$
							pluginFile = new File(files[i], "fragment.xml"); //$NON-NLS-1$
						}

						if (pluginFile != null && pluginFile.exists() && !pluginFile.isDirectory()) {
							PluginEntry entry = parser.parse(new FileInputStream(pluginFile));
							addParsedPlugin(entry,files[i]);
						}
					} // files[i] is a directory
				}
			} // path is a directory
		} catch (IOException e) {
			String pluginFileString = (pluginFile == null) ? null : pluginFile.getAbsolutePath();
			throw Utilities.newCoreException(Policy.bind("SiteFileFactory.ErrorAccessing", pluginFileString), e);
			//$NON-NLS-1$
		} catch (SAXException e) {
			String pluginFileString = (pluginFile == null) ? null : pluginFile.getAbsolutePath();
			throw Utilities.newCoreException(Policy.bind("SiteFileFactory.ErrorParsingFile", pluginFileString), e);
			//$NON-NLS-1$
		}

	}

	/**
	 * tranform each Plugin and Fragment into an ArchiveReferenceModel
	 * and a PluginEntry for the Site	 
	 */
	// PERF: removed intermediate Plugin object
	private void addParsedPlugin(PluginEntry entry,File file) throws CoreException {

		String location = null;
		try {
			if (entry != null) {

				// create the plugin Entry
				site.addPluginEntry(entry);

				// Create the Site mapping ArchiveRef->PluginEntry
				// the id of the archiveRef is plugins\<pluginid>_<ver>.jar as per the specs
				// PERF: remove code
				//SiteFileFactory archiveFactory = new SiteFileFactory();				
				ArchiveReference archive = new ArchiveReference();
				String id = (entry.getVersionedIdentifier().toString());
				String pluginID = Site.DEFAULT_PLUGIN_PATH + id + FeaturePackagedContentProvider.JAR_EXTENSION;
				archive.setPath(pluginID);
				location = file.toURL().toExternalForm();
				archive.setURLString(location);
				((Site) site).addArchiveReference(archive);

				// TRACE				
				if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_PARSING) {
					UpdateCore.debug("Added archive to site:" + pluginID + " pointing to: " + location);
				}
			}
		} catch (MalformedURLException e) {
			throw Utilities.newCoreException(Policy.bind("SiteFileFactory.UnableToCreateURLForFile", location), e);
			//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	private void parsePackagedPlugins(File pluginDir) throws CoreException {

		File file = null;
		String[] dir;

		ContentReference ref = null;
		String refString = null;

		try {
			if (pluginDir.exists()) {
				dir = pluginDir.list(FeaturePackagedContentProvider.filter);
				for (int i = 0; i < dir.length; i++) {
					file = new File(pluginDir, dir[i]);
					JarContentReference jarReference = new JarContentReference(null, file);
					ref = jarReference.peek("plugin.xml", null, null); //$NON-NLS-1$
					if (ref == null)
						jarReference.peek("fragment.xml", null, null); //$NON-NLS-1$

					refString = (ref == null) ? null : ref.asURL().toExternalForm();

					if (ref != null) {
						PluginEntry entry = new PluginParser().parse(ref.getInputStream());
						addParsedPlugin(entry,file);
					} //ref!=null
				} //for
			}

		} catch (IOException e) {
			throw Utilities.newCoreException(Policy.bind("SiteFileFactory.ErrorAccessing", refString), e);
			//$NON-NLS-1$
		} catch (SAXException e) {
			throw Utilities.newCoreException(Policy.bind("SiteFileFactory.ErrorParsingFile", refString), e);
			//$NON-NLS-1$
		}
	}
}
