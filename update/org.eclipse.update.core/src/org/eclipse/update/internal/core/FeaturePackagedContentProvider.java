package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.update.core.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.obsolete.*;

/**
 * Parse the default feature.xml
 */
public class FeaturePackagedContentProvider  extends FeatureContentProvider {

	private JarFile currentOpenJarFile = null;

	private URL rootURL;

	public static final String JAR_EXTENSION = ".jar";
	
	private FeatureContentProvider.ContentSelector contentSelector = new FeatureContentProvider.ContentSelector(){
		/*
		 * 
		 */
		public boolean include(String entry){
			return true;
		}
		
		/*
		 *
		 */
		public String defineIdentifier(String entry){
			return 	entry;
		}
		
	};

	/**
	 * Constructor 
	 */
	public FeaturePackagedContentProvider(URL url)  throws CoreException {
		super(url);
	}

	/*
	 * @see Feature#getContentReferenceToInstall(IPluginEntry[])
	 */
	private String[] getContentReferenceToInstall(IPluginEntry[] pluginsToInstall) {
		String[] names = null;
		if (pluginsToInstall != null) {
			names = new String[pluginsToInstall.length];
			for (int i = 0; i < pluginsToInstall.length; i++) {
				names[i] = getPluginEntryArchiveID(pluginsToInstall[i]);
			}
		}
		return names;
	}

	/**
	 * @see AbstractFeature#getInputStreamFor(IPluginEntry,String)
	 */
	public InputStream getInputStreamFor(IPluginEntry pluginEntry, String name) throws CoreException {
		InputStream result = null;

		try {
			// check if the site.xml had a coded URL for this plugin or if we
			// should look in teh default place to find it: <site>+/plugins/+archiveId
			String filePath = UpdateManagerUtils.getPath(feature.getSite().getSiteContentProvider().getArchiveReference(getPluginEntryArchiveID(pluginEntry)).asURL());						
			open(filePath);
			if (!(new File(filePath)).exists())
				throw new IOException("The File:" + filePath + "does not exist.");
			ZipEntry entry = currentOpenJarFile.getEntry(name);
			result = currentOpenJarFile.getInputStream(entry);
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error opening :" + name + " in plugin archive:" + pluginEntry.getIdentifier().toString(), e);
			throw new CoreException(status);
		}
		return result;
	}

	/**
	 * @see AbstractFeature#getStorageUnitNames(IPluginEntry)
	 */
	public String[] getStorageUnitNames(IPluginEntry pluginEntry) throws CoreException {

		// try to obtain the URL of the JAR file that contains the plugin entry from teh site.xml
		// if it doesn't exist, use the default one
		String[] result = new String[0];
		try{
			URL jarURL =feature.getSite().getSiteContentProvider().getArchiveReference(getPluginEntryArchiveID(pluginEntry)).asURL();
			String path = UpdateManagerUtils.getPath(jarURL);					
			result = getJAREntries(path);
		} catch (IOException ex){
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Unable to Retrieve URL for feature:" + feature.getURL(), null);
			throw new CoreException(status);
		}
		return result;
	}

	/**
	 * return the archive ID for a plugin
	 */
	private String getPluginEntryArchiveID(IPluginEntry entry) {
		String type = (entry.isFragment())?Site.DEFAULT_FRAGMENT_PATH:Site.DEFAULT_PLUGIN_PATH;
		return type+entry.getIdentifier().toString() + JAR_EXTENSION;
	}

	/**
	 * @see AbstractFeature#getArchiveID()
	 */
	public String[] getFeatureEntryArchiveID() {
		String[] names = new String[feature.getPluginEntryCount()];
		IPluginEntry[] entries = feature.getPluginEntries();
		for (int i = 0; i < feature.getPluginEntryCount(); i++) {
			names[i] = getPluginEntryArchiveID(entries[i]);
		}
		return names;
	}

	/**
	 * @see AbstractFeature#isInstallable()
	 */
	public boolean isInstallable() {
		return true;
	}

	/**
	 * @see AbstractFeature#getInputStreamFor(String)
	 */
	protected InputStream getFeatureInputStreamFor(String name) throws CoreException, IOException {
		InputStream result = null;
		try {

			// ensure teh file is local
			
			
			// teh feature must have a URL as 
			//it has been transfered locally
			String filePath = UpdateManagerUtils.getPath(getURL());						
			if (!(new File(filePath)).exists())
				throw new IOException("The File:" + filePath + "does not exist.");
			open(filePath);
			ZipEntry entry = currentOpenJarFile.getEntry(name);
			result = currentOpenJarFile.getInputStream(entry);

		} catch (IOException e) {
			throw new IOException("Error opening :" + name + " in feature archive:" + feature.getURL().toExternalForm() + "\r\n" + e.toString());
		}
		return result;
	}

	/**
	 * @see AbstractFeature#getStorageUnitNames()
	 */
	protected String[] getStorageUnitNames(IFeature feature) throws CoreException {

		// make sure the feature archive has been transfered locally


		// get the URL of the feature JAR file 
		// must exist as we tranfered it locally
		String path = UpdateManagerUtils.getPath(feature.getURL());					
		String[] result = getJAREntries(path);

		return result;
	}

	/**
	 * return the list of entries in the JAR file
	 * Do not retrun Directory entries
	 * 
	 * do not get directories only entry as the directories will
	 * be created when teh fils will be created
	 * it was difficult to obtain a correct URL for a Directory inside a JAR
	 * because of the last '\' in the entry
	 */
	private String[] getJAREntries(String path) throws CoreException {
		String[] result = new String[0];
		try {
			JarFile jarFile = new JarFile(path);
			List list = new ArrayList();
			Enumeration enum = jarFile.entries();
			int loop = 0;
			while (enum.hasMoreElements()) {
				ZipEntry nextEntry = (ZipEntry) enum.nextElement();
				if (!nextEntry.isDirectory()) {
					list.add(nextEntry.getName());
					loop++;
				}
			}
			jarFile.close();

			// set the result			
			if (loop > 0 && !list.isEmpty()) {
				result = new String[loop];
				list.toArray(result);
			}
		} catch (IOException e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error opening JAR file:" + path, e);
			throw new CoreException(status);
		}
		return result;
	}

	/**
	 * @see AbstractFeature#close(IPluginEntry)
	 */
	protected void close(IPluginEntry entry) throws IOException {
		if (currentOpenJarFile != null)
			currentOpenJarFile.close();
	}

	/**
	 * @see AbstractFeature#closeFeature()
	 */
	public void closeFeature() throws IOException {
		if (currentOpenJarFile != null)
			currentOpenJarFile.close();
	}

	/**
	 * opens a JAR file or returns the one already opened 
	 * if teh path is the same.
	 */
	protected void open(String filePath) throws IOException {
		JarFile newJarFile = new JarFile(filePath);
		open(newJarFile);
	}

	/**
	 * opens a JAR file or returns the one already opened
	 * if teh path is the same.
	 */
	protected void open(JarFile newJarFile) throws IOException {

		// are we looking into teh same Jar file
		// or shoudl we close the previously opened one and open another one ?
		if (currentOpenJarFile != null) {
			if (!currentOpenJarFile.getName().equals(newJarFile.getName())) {
				currentOpenJarFile.close();
				currentOpenJarFile = newJarFile;
			} else {
				newJarFile.close();
			}
		} else {
			currentOpenJarFile = newJarFile;
		}
	}

	/*
	 * @see IFeatureContentProvider#getFeatureManifestReference()
	 */
	public ContentReference getFeatureManifestReference() throws CoreException {
		ContentReference result = null;
		ContentReference[] featureContentReference = getFeatureEntryArchiveReferences();
		try {			
			JarContentReference localContentReference = (JarContentReference)asLocalReference(featureContentReference[0],null);
			result = unpack(localContentReference,Feature.FEATURE_XML,contentSelector,null);
		} catch (IOException e){
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving manifest file in  feature :" + featureContentReference[0].getIdentifier(), e);
			throw new CoreException(status);			
		}
		return result;
	}

	/*
	 * @see IFeatureContentProvider#getArchiveReferences()
	 */
	public ContentReference[] getArchiveReferences() throws CoreException {
		IPluginEntry[] entries = feature.getPluginEntries();
		INonPluginEntry[] nonEntries = feature.getNonPluginEntries();
		List listAllContentRef = new ArrayList();
		ContentReference[] allContentRef = new ContentReference[0];
		
		// feature
		listAllContentRef.addAll(Arrays.asList(getFeatureEntryArchiveReferences()));
		
		// plugins
		for (int i = 0; i < entries.length; i++) {
			listAllContentRef.addAll(Arrays.asList(getPluginEntryArchiveReferences(entries[i])));				
		}
		
		// non plugins
		for (int i = 0; i < nonEntries.length; i++) {
			listAllContentRef.addAll(Arrays.asList(getNonPluginEntryArchiveReferences(nonEntries[i])));				
		}
		
		if (listAllContentRef.size()>0){
			allContentRef = new ContentReference[listAllContentRef.size()];
			listAllContentRef.toArray(allContentRef);
		}
		
		return allContentRef;
	}

	/*
	 * @see IFeatureContentProvider#getFeatureEntryArchiveReferences()
	 */
	public ContentReference[] getFeatureEntryArchiveReferences() throws CoreException {
		//1 jar file <-> 1 feature
		ContentReference[] references = new ContentReference[1]; 		
		try {
				// feature may not be known, 
				// we may be asked for the manifest before the feature is set
				String archiveID = (feature!=null)?contentSelector.defineIdentifier(feature.getVersionIdentifier().toString()):"";				
				ContentReference currentReference = new JarContentReference(archiveID,getURL());
				currentReference = asLocalReference(currentReference,null);
				references[0] = currentReference;
		} catch (IOException e){
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving feature Entry Archive Reference :" + feature.getURL().toExternalForm(), e);
			throw new CoreException(status);			
		}
		return references;
	}

	/*
	 * @see IFeatureContentProvider#getPluginEntryArchiveReferences(IPluginEntry)
	 */
	public ContentReference[] getPluginEntryArchiveReferences(IPluginEntry pluginEntry) throws CoreException {
		return null;
	}

	/*
	 * @see IFeatureContentProvider#getNonPluginEntryArchiveReferences(INonPluginEntry)
	 */
	public ContentReference[] getNonPluginEntryArchiveReferences(INonPluginEntry nonPluginEntry) throws CoreException {
		return null;
	}

	/*
	 * @see IFeatureContentProvider#getFeatureEntryContentReferences()
	 */
	public ContentReference[] getFeatureEntryContentReferences() throws CoreException {
		String[] archiveIDs = getFeatureEntryArchiveID();
		ContentReference[] references = new ContentReference[archiveIDs.length];		
		try {
			for (int i = 0; i < archiveIDs.length; i++) {
				URL url = feature.getSite().getSiteContentProvider().getArchiveReference(archiveIDs[i]).asURL();
				ContentReference currentReference = new JarContentReference(archiveIDs[i],url);
				currentReference = asLocalReference(currentReference,null);
				references[i] = currentReference;
			}
		} catch (IOException e){
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving feature Entry Archive Reference :" + feature.getURL().toExternalForm(), e);
			throw new CoreException(status);			
		}
		return references;
	}

	/*
	 * @see IFeatureContentProvider#getPluginEntryContentReferences(IPluginEntry)
	 */
	public ContentReference[] getPluginEntryContentReferences(IPluginEntry pluginEntry) throws CoreException {
		return null;
	}

	/*
	 * @see IFeatureContentProvider#setFeature(IFeature)
	 */
	public void setFeature(IFeature feature) {
		this.feature = feature;
	}

}