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

/**
 * Parse the default feature.xml
 */
public class FeaturePackagedContentProvider  extends FeatureContentProvider {

	private JarFile currentOpenJarFile = null;

	private URL rootURL;

	public static final String JAR_EXTENSION = ".jar";

	public static final FilenameFilter filter = new FilenameFilter(){
		 public boolean accept(File dir, String name){
		 	return name.endsWith(FeaturePackaged.JAR_EXTENSION);
		 }
	};

	/**
	 * @see IFeature#getRootURL()
	 * In general, the Root URL is the URL of teh DefaultFeature
	 * 
	 * The RootURL is used to calculate relative URL for teh feature
	 * In case of a file feature, you can just append teh relative path
	 * to the URL of teh feature
	 * 
	 * In case of a JAR file, you cannot *just* append the file 
	 * You have to transfrom the URL
	 * 
	 */
	public URL getRootURL() throws MalformedURLException, IOException, CoreException {
		
		// Extract the JAR file in the TEMP drive
		// and return the URL
		
		if (rootURL == null) {
			// install the DefaultFeature info into the TEMP drive
			// extract teh JAR file
			SiteFile tempSite = (SiteFile)SiteManager.getTempSite();
			
			InputStream inStream = null;
			String[] names = getStorageUnitNames(this);
			if (names != null) {
				openFeature();
				for (int j = 0; j < names.length; j++) {
					if ((inStream = getInputStreamFor(this,names[j])) != null)
						 tempSite.storeFeatureInfo(getIdentifier(), names[j], inStream);
				}
				closeFeature();
			}

			// get the path to the DefaultFeature, which is now pon the file system
			// <TempSite>/install/features/<id>_<ver>/
			// add '/' as it is a directory
			rootURL = UpdateManagerUtils.getURL(tempSite.getURL(),SiteFile.INSTALL_FEATURE_PATH+getIdentifier().toString()+"/",null);
		}
		return rootURL;
	}

	/**
	 * Constructor 
	 */
	public FeaturePackagedContentProvider(URL url)  throws CoreException {
		super(url);
	}

	/**
	 * @see AbstractFeature#getContentReferenceToInstall(IPluginEntry[])
	 */
	public String[] getContentReferenceToInstall(IPluginEntry[] pluginsToInstall) {
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
			String filePath = UpdateManagerUtils.getPath(((Site) getSite()).getURL(getPluginEntryArchiveID(pluginEntry)));						
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
		URL jarURL = ((Site) getSite()).getURL(getPluginEntryArchiveID(pluginEntry));
		String path = UpdateManagerUtils.getPath(jarURL);					
		String[] result = getJAREntries(path);

		return result;
	}

	/**
	 * return the archive ID for a plugin
	 */
	public String getPluginEntryArchiveID(IPluginEntry entry) {
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
		transferLocally();

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

	/**
	 * return the appropriate resource bundle for this feature
	 * Need to override as opening the JAR keeps it locked
	 * 
	 * baseclass + "_" + language1 + "_" + country1 + "_" + variant1 
	 * baseclass + "_" + language1 + "_" + country1 + "_" + variant1 + ".properties" 
	 * baseclass + "_" + language1 + "_" + country1 
	 * baseclass + "_" + language1 + "_" + country1 + ".properties" 
	 * baseclass + "_" + language1 
	 * baseclass + "_" + language1 + ".properties" 
	 * baseclass + "_" + language2 + "_" + country2 + "_" + variant2 
	 * baseclass + "_" + language2 + "_" + country2 + "_" + variant2 + ".properties" 
	 * baseclass + "_" + language2 + "_" + country2 
	 * baseclass + "_" + language2 + "_" + country2 + ".properties" 
	 * baseclass + "_" + language2 
	 * baseclass + "_" + language2 + ".properties" 
	 * baseclass 
	 * baseclass + ".properties" 
	 */
	public ResourceBundle getResourceBundle() throws IOException, CoreException {

		ResourceBundle result = null;
		String[] names = getStorageUnitNames(this);
		String base = FEATURE_FILE;

		// retrive names in teh JAR that starts with the basename
		// remove FEATURE_XML file
		List baseNames = new ArrayList();
		for (int i = 0; i < names.length; i++) {
			if (names[i].startsWith(base))
				baseNames.add(names[i]);
		}
		baseNames.remove(FEATURE_XML);

		// is there any file		
		if (!baseNames.isEmpty()) {

			Locale locale = Locale.getDefault();
			String lang1 = locale.getLanguage();
			String country1 = locale.getCountry();
			String variant1 = locale.getVariant();
			String[] attempt =
				new String[] {
					base + "_" + lang1 + "_" + country1 + "_" + variant1,
					base + "_" + lang1 + "_" + country1 + "_" + variant1 + ".properties",
					base + "_" + lang1 + "_" + country1,
					base + "_" + lang1 + "_" + country1 + ".properties",
					base + "_" + lang1,
					base + "_" + lang1 + ".properties",
					base,
					base + ".properties" };

			boolean found = false;
			int index = 0;
			while (!found && index < attempt.length) {
				if (baseNames.contains(attempt[index])) {
					result = new PropertyResourceBundle(getInputStreamFor(this,attempt[index]));
					found = true;
				}
				index++;
			}

		} // baseNames is empty

		if (result == null) {
			if (UpdateManagerPlugin.DEBUG && UpdateManagerPlugin.DEBUG_SHOW_WARNINGS) {
				UpdateManagerPlugin.getPlugin().debug("Cannot find resourceBundle for:" + base + " - " + Locale.getDefault().toString() + ":" + this.getURL().toExternalForm());
			}
		}
		return result;
	}
	
	
	
		/**
	 */
	private void downloadArchivesLocally(ISite tempSite, String[] archiveIDToInstall, IProgressMonitor monitor) throws CoreException, IOException {

		URL sourceURL;
		String newFile;
		URL newURL;

		if (monitor != null) {
			monitor.beginTask("Download archives bundles to Temporary Space", archiveIDToInstall.length);
		}
		for (int i = 0; i < archiveIDToInstall.length; i++) {

			// transform the id by asking the site to map them to real URL inside the SITE
			if (getSite() != null) {
				sourceURL = getSite().getSiteContentProvider().getArchivesReferences(archiveIDToInstall[i]);
				if (monitor != null) {
					monitor.subTask("..." + archiveIDToInstall[i]);
				}
				// the name of the file in the temp directory
				// should be the regular plugins/pluginID_ver as the Temp site is OUR site
				newFile = Site.DEFAULT_PLUGIN_PATH + archiveIDToInstall[i];
				newURL = UpdateManagerUtils.resolveAsLocal(sourceURL, newFile, monitor);

				// transfer the possible mapping to the temp site						
				 ((Site) tempSite).addArchive(new URLEntry(archiveIDToInstall[i], newURL));
				if (monitor != null) {
					monitor.worked(1);
					if (monitor.isCanceled()) {
						throw CANCEL_EXCEPTION;
					}
				}
			}
		}

		// the site of this feature now becomes the TEMP directory
		// FIXME: make sure there is no other issue
		// like asking for stuff that hasn't been copied
		// or reusing this feature
		// of having an un-manageable temp site

		this.setSite(tempSite);

	}

	/**
	 */
	private void downloadDataLocally(IFeature targetFeature, INonPluginEntry[] dataToInstall, IProgressMonitor monitor) throws CoreException, IOException {

		URL sourceURL;
		// any other data
		INonPluginEntry[] entries = getNonPluginEntries();
		if (entries != null) {
			if (monitor != null) {
				monitor.beginTask("Installing Other Data information", dataToInstall.length);
				if (monitor.isCanceled()) {
					throw CANCEL_EXCEPTION;
				}
			}

			for (int j = 0; j < entries.length; j++) {
				String name = dataToInstall[j].getIdentifier();
				if (monitor != null) {
					monitor.subTask("..." + name);
				}

				// the id is URL format with "/"
				String dataEntryId = Site.DEFAULT_FEATURE_PATH + getIdentifier().toString() + "/" + name;
				// transform the id by asking the site to map them to real URL inside the SITE
				if (getSite() != null) {
					sourceURL = getSite().getSiteContentProvider().getArchivesReferences(dataEntryId);
					((Site) targetFeature.getSite()).storeFeatureInfo(getIdentifier(), name, sourceURL.openStream());
					if (monitor != null) {
						monitor.worked(1);
						if (monitor.isCanceled()) {
							throw CANCEL_EXCEPTION;
						}
					}
				}// getSite==null
			}
		}
	}
	
	
	
	/*
	 * @see IFeatureContentProvider#getFeatureManifest()
	 */
	public ContentReference getFeatureManifest() throws CoreException {
		ContentReference result = null;
		try {
		ContentReference[] featureContentReference = getFeatureEntryArchiveReferences();
		ContentReference localContentReference = asLocalReference(featureContentReference[1],null);
		result = unpack(localContentReference,Feature.FEATURE_XML,null);
		} catch (IOException e){
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error retrieving manifest file in  feature :" + feature.getURL().toExternalForm(), e);
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
		String[] archiveIDs = getFeatureEntryArchiveID();
		try {
		ContentReference[] references = new ContentReference[archiveIDs.length];
		for (int i = 0; i < archiveIDs.length; i++) {
			URL url = feature.getSite().getSiteContentProvider().getArchivesReferences(archiveIDs[i]);
			ContentReference currentReference = new ContentReference(archiveIDs[i],url);
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
		return null;
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
	}

}