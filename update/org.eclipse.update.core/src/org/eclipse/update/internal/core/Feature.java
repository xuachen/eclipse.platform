package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.*;
/**
 * Abstract Class that implements most of the behavior of a feature
 * A feature ALWAYS belongs to an ISite
 */
public abstract class Feature extends FeatureModel implements IFeature {

	/**
	 * 
	 */
	private static CoreException CANCEL_EXCEPTION;

	/**
	 * 
	 */
	public static final String FEATURE_FILE = "feature";

	/**
	 * 
	 */
	public static final String FEATURE_XML = FEATURE_FILE + ".xml";

	/**
	 * Site in which teh feature resides
	 */
	private ISite site;

	/**
	 * reference to the feature inside the site.
	 * This URL can be a Jar file, a directory or any URL that is understood by the 
	 * Subclass of AbstractFeature.
	 */
	private URL url;

	/**
	 * Static block to initialize the possible CANCEL ERROR
	 * thrown when the USER cancels teh operation
	 */
	static {
		//	in case we throw a cancel exception
		String pluginId = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
		IStatus cancelStatus = new Status(IStatus.ERROR, pluginId, IStatus.OK, "Install has been Cancelled", null);
		CANCEL_EXCEPTION = new CoreException(cancelStatus);
	}

	/**
	 * Copy constructor
	 */
	public Feature(IFeature sourceFeature, ISite targetSite) throws CoreException {
		this(sourceFeature.getURL(), targetSite);
		this.setIdentifier(sourceFeature.getIdentifier());
		this.setLabel(sourceFeature.getLabel());
		this.setUpdateSiteEntry(sourceFeature.getUpdateSiteEntry());
		this.setDiscoverySiteEntries(sourceFeature.getDiscoverySiteEntries());
		this.setProvider(sourceFeature.getProvider());
		this.setDescription(sourceFeature.getDescription());
		this.setCopyright(sourceFeature.getCopyright());
		this.setLicense(sourceFeature.getLicense());
		this.setPluginEntries(sourceFeature.getPluginEntries());
		this.setImage(sourceFeature.getImage());
	}

	/**
	 * Constructor
	 */
	public Feature(URL url, ISite targetSite) throws CoreException {
		this.site = targetSite;
		this.url = url;
	}

	/**
	 * @see IFeature#getIdentifier()
	 */
	public VersionedIdentifier getIdentifier() {
		return new VersionedIdentifier(getFeatureIdentifier(), getFeatureVersion());
	}

	/**
	 * @see IFeature#getSite()
	 * Do not hydrate, value set ins constructor
	 */
	public ISite getSite() {
		return site;
	}

	/**
	 * @see IFeature#getURL()
	 * Do not hydrate. Initialization will not populate the url.
	 * If the URL is null, then the creation hasn't set the URL, so return null.
	 * It has to be set at creation time or using the set method 
	 * Usually done from the site when creating the Feature. 
	 * 
	 * The DefaultSiteParser is setting it at creation time
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * @see IFeature#getRootURL()
	 * In general, the Root URL is the URL of teh Feature
	 * 
	 * The RootURL is used to calculate relative URL for teh feature
	 * In case of a file feature, you can just append teh relative path
	 * to the URL of teh feature
	 * 
	 * In case of a JAR file, you cannot *just* append the file 
	 * You have to transfrom the URL
	 * 
	 * Can be overriden 
	 */
	public URL getRootURL() throws MalformedURLException, IOException, CoreException {
		return url;
	}

	/**
	 * @see IFeature#getUpdateSiteEntry()
	 */
	public IURLEntry getUpdateSiteEntry() {
		return (IURLEntry) getUpdateSiteEntryModel();
	}

	/**
	 * @see IFeature#getDiscoverySiteEntries()
	 */
	public IURLEntry[] getDiscoverySiteEntries() {
		int length = getDiscoverySiteEntryModels().length;
		IURLEntry[] result = new IURLEntry[length];
		if (length > 0) {
			result = (IURLEntry[]) getDiscoverySiteEntryModels();
		}
		return result;
	}

	/**
	 * @see IFeature#getDescription()
	 */
	public IURLEntry getDescription() {
		return (IURLEntry) getDescriptionModel();
	}

	/**
	 * @see IFeature#getCopyright()
	 */
	public IURLEntry getCopyright() {
		return (IURLEntry) getCopyrightModel();
	}

	/**
	 * @see IFeature#getLicense()
	 */
	public IURLEntry getLicense() {
		return (IURLEntry) getLicenseModel();
	}

	/**
	 * @see IFeature#getImage()
	 */
	public URL getImage() {
		return getImageURL();
	}
	/**
	 * Sets the site
	 * @param site The site to set
	 */
	public void setSite(ISite site) {
		this.site = site;
	}

	/**
	 * Sets the identifier
	 * @param identifier The identifier to set
	 */
	public void setIdentifier(VersionedIdentifier identifier) {
		setFeatureIdentifier(identifier.getIdentifier());
		setFeatureVersion(identifier.getVersion().toString());
	}

	/**
	 * Sets the url
	 * @param url The url to set
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/**
	 * Sets the discoverySiteEntries
	 * @param discoveryInfos The discoveryInfos to set
	 */
	public void setDiscoverySiteEntries(IURLEntry[] discoveryInfos) {
		setDiscoverySiteEntryModels((URLEntryModel[]) discoveryInfos);
	}

	/**
	 * Sets the updateSiteEntry
	 * @param updateInfo The updateInfo to set
	 */
	public void setUpdateSiteEntry(IURLEntry updateInfo) {
		setUpdateSiteEntryModel((URLEntryModel) updateInfo);
	}

	/**
	 * Adds a discoveryInfo
	 * @param discoveryInfo The discoveryInfo to add
	 */
	public void addDiscoverySiteEntry(IURLEntry discoveryInfo) {
		addDiscoverySiteEntryModel((URLEntryModel) discoveryInfo);
	}

	/**
	 * Sets the description
	 * @param description The description to set
	 */
	public void setDescription(IURLEntry description) {
		setDescriptionModel((URLEntryModel) description);
	}

	/**
	 * Sets the copyright
	 * @param copyright The copyright to set
	 */
	public void setCopyright(IURLEntry copyright) {
		setCopyrightModel((URLEntryModel) copyright);
	}

	/**
	 * Sets the license
	 * @param license The license to set
	 */
	public void setLicense(IURLEntry license) {
		setLicenseModel((URLEntryModel) license);
	}

	/**
	 * Sets the image
	 * @param image The image to set
	 */
	public void setImage(URL image) {
		setImageURLString(image.toExternalForm());
	}

	/**
	 * @see IPluginContainer#getDownloadSize(IPluginEntry)
	 */
	public long getDownloadSize(IPluginEntry entry) {
		return entry.getDownloadSize();
	}

	/**
	 * @see IPluginContainer#getInstallSize(IPluginEntry)
	 */
	public long getInstallSize(IPluginEntry entry) {
		return entry.getInstallSize();
	}
	/**
	 * returns the download size
	 * of the feature to be installed on the site.
	 * If the site is <code>null</code> returns the maximum size
	 * 
	 * If one plug-in entry has an unknown size.
	 * then the download size is unknown.
	 * 
	 * @see IFeature#getDownloadSize(ISite)
	 * 
	 */
	public long getDownloadSize(ISite site) throws CoreException {
		long result = 0;
		IPluginEntry[] entriesToInstall = this.getPluginEntries();
		if (site != null) {
			IPluginEntry[] siteEntries = site.getPluginEntries();
			entriesToInstall = intersection(entriesToInstall, siteEntries);
		}

		if (entriesToInstall == null || entriesToInstall.length == 0) {
			result = -1;
		} else {
			long pluginSize = 0;
			int i = 0;
			while (i < entriesToInstall.length && pluginSize != -1) {
				pluginSize = getDownloadSize(entriesToInstall[i]);
				result = pluginSize == -1 ? -1 : result + pluginSize;
				i++;
			}
		}
		return result;
	}
	/**
	 * returns the install size
	 * of the feature to be installed on the site.
	 * If the site is <code>null</code> returns the maximum size
	 * 
	 * If one plug-in entry has an unknown size.
	 * then the install size is unknown.
	 * 
	 * @see IFeature#getInstallSize(ISite)
	 */
	public long getInstallSize(ISite site) throws CoreException {
		long result = 0;
		IPluginEntry[] entriesToInstall = this.getPluginEntries();
		if (site != null) {
			IPluginEntry[] siteEntries = site.getPluginEntries();
			entriesToInstall = intersection(entriesToInstall, siteEntries);
		}
		if (entriesToInstall == null || entriesToInstall.length == 0) {
			result = -1;
		} else {
			long pluginSize = 0;
			int i = 0;
			while (i < entriesToInstall.length && pluginSize != -1) {
				pluginSize = getInstallSize(entriesToInstall[i]);
				result = pluginSize == -1 ? -1 : result + pluginSize;
				i++;
			}
		}
		return result;
	}
	/**
	 * @see IFeature#isExecutable()
	 */
	public boolean isExecutable() {
		return false;
	}
	/**
	 * @see IFeature#isInstallable()
	 */
	public boolean isInstallable() {
		return false;
	}

	/**
	 * Method install.
	 * @param targetFeature
	 * @param monitor
	 * @throws CoreException
	 */
	public void install(IFeature targetFeature, IProgressMonitor monitor) throws CoreException {

		IPluginEntry[] sourceFeaturePluginEntries = getPluginEntries();
		IPluginEntry[] targetSitePluginEntries = targetFeature.getSite().getPluginEntries();
		Site tempSite = (Site) SiteManager.getTempSite();

		// determine list of plugins to install
		// find the intersection between the two arrays of IPluginEntry...
		// The one teh site contains and teh one the feature contains
		IPluginEntry[] pluginsToInstall = intersection(sourceFeaturePluginEntries, targetSitePluginEntries);

		// private abstract - Determine list of content references id /archives id /bundles id that 
		// map the list of plugins to install
		String[] archiveIDToInstall = getContentReferenceToInstall(pluginsToInstall);

		try {
			// download and install data bundles
			// before we set the site of teh feature to the TEMP site
			INonPluginEntry[] dataEntries = getNonPluginEntries();
			if (dataEntries.length > 0) {
				downloadDataLocally(targetFeature, dataEntries, monitor);
			}

			// optmization, may be private to implementation
			// copy *blobs/content references/archives/bundles* in TEMP space
			if (((Site) getSite()).optimize()) {
				if (archiveIDToInstall != null) {
					downloadArchivesLocally(tempSite, archiveIDToInstall, monitor);
				}
			}

			// obtain the list of *Streamable Storage Unit*
			// from the archive
			if (monitor != null) {
				int total = pluginsToInstall == null ? 1 : pluginsToInstall.length + 1;
				monitor.beginTask("Install feature " + getLabel(), total);
			}
			if (pluginsToInstall != null) {
				InputStream inStream = null;
				for (int i = 0; i < pluginsToInstall.length; i++) {
					if (monitor != null) {
						monitor.subTask("Installing plug-in: " + pluginsToInstall[i]);
						if (monitor.isCanceled()) {
							throw CANCEL_EXCEPTION;
						}
					}

					open(pluginsToInstall[i]);
					String[] names = getStorageUnitNames(pluginsToInstall[i]);
					if (names != null) {
						for (int j = 0; j < names.length; j++) {
							if ((inStream = getInputStreamFor(pluginsToInstall[i], names[j])) != null)
								targetFeature.store(pluginsToInstall[i], names[j], inStream);
						}
					}
					close(pluginsToInstall[i]);
					if (monitor != null) {
						monitor.worked(1);
						if (monitor.isCanceled()) {
							throw CANCEL_EXCEPTION;
						}
					}

				}
			}

			// install the Feature info
			InputStream inStream = null;
			String[] names = getStorageUnitNames();
			if (names != null) {
				openFeature();
				if (monitor != null) {
					monitor.subTask("Installing Feature information");
					if (monitor.isCanceled()) {
						throw CANCEL_EXCEPTION;
					}
				}

				for (int j = 0; j < names.length; j++) {
					if ((inStream = getInputStreamFor(names[j])) != null)
						 ((Site) targetFeature.getSite()).storeFeatureInfo(getIdentifier(), names[j], inStream);
				}
				closeFeature();
				if (monitor != null) {
					monitor.worked(1);
					if (monitor.isCanceled()) {
						throw CANCEL_EXCEPTION;
					}
				}

			}

		} catch (IOException e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error during Install", e);
			throw new CoreException(status);
		} finally {
			//do not clean up TEMP drive
			// as other feature may be there... clean up when exiting the plugin
		}
	}

	/**
	 * remove myself...
	 */
	public void remove(IProgressMonitor monitor) throws CoreException {

		// remove the feature and the plugins if they are not used and not activated

		// get the plugins from the feature
		IPluginEntry[] pluginsToRemove = ((SiteLocal) SiteManager.getLocalSite()).getDeltaPluginEntries(this);

		try {

			// obtain the list of *Streamable Storage Unit*
			// from the archive
			if (monitor != null) {
				int total = pluginsToRemove == null ? 1 : pluginsToRemove.length + 1;
				monitor.beginTask("Uninstall feature " + getLabel(), total);
			}
			if (pluginsToRemove != null) {
				for (int i = 0; i < pluginsToRemove.length; i++) {
					if (monitor != null) {
						monitor.subTask("Removing plug-in: " + pluginsToRemove[i]);
						if (monitor.isCanceled()) {
							throw CANCEL_EXCEPTION;
						}
					}

					remove(pluginsToRemove[i]);

					if (monitor != null) {
						monitor.worked(1);
						if (monitor.isCanceled()) {
							throw CANCEL_EXCEPTION;
						}
					}

				}
			}

			// install the Feature info
			String[] names = getStorageUnitNames();
			if (names != null) {
				if (monitor != null) {
					monitor.subTask("Removing Feature information");
					if (monitor.isCanceled()) {
						throw CANCEL_EXCEPTION;
					}
				}

				((Site) this.getSite()).removeFeatureInfo(getIdentifier());

				closeFeature();
				if (monitor != null) {
					monitor.worked(1);
					if (monitor.isCanceled()) {
						throw CANCEL_EXCEPTION;
					}
				}

			}

		} catch (IOException e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error during Uninstall", e);
			throw new CoreException(status);
		}
	}

	/*
	 * @see IPluginContainer#remove(IPluginEntry)
	 */
	public void remove(IPluginEntry entry) throws CoreException {
		((Site) getSite()).remove(entry);
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
			sourceURL = ((Site) getSite()).getURL(archiveIDToInstall[i]);
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
				sourceURL = ((Site) getSite()).getURL(dataEntryId);
				((Site) targetFeature.getSite()).storeFeatureInfo(getIdentifier(), name, sourceURL.openStream());
				if (monitor != null) {
					monitor.worked(1);
					if (monitor.isCanceled()) {
						throw CANCEL_EXCEPTION;
					}
				}
			}

		}
	}

	/**
	 * Returns the intersection between two array of PluginEntries.
	 */
	private IPluginEntry[] intersection(IPluginEntry[] array1, IPluginEntry[] array2) {
		if (array1 == null || array1.length == 0) {
			return array2;
		}
		if (array2 == null || array2.length == 0) {
			return array1;
		}

		List list1 = Arrays.asList(array1);
		List result = new ArrayList(0);
		for (int i = 0; i < array2.length; i++) {
			if (!list1.contains(array2[i]))
				result.add(array2[i]);
		}
		return (IPluginEntry[]) result.toArray();
	}

	/**
	 * @see IPluginContainer#getPluginEntries()
	 */
	public IPluginEntry[] getPluginEntries() {
		int length = getPluginEntryModels().length;
		IPluginEntry[] result = new IPluginEntry[length];
		if (length > 0) {
			result = (IPluginEntry[]) getPluginEntryModels();
		}
		//			for (int i = 0; i < length; i++) {
		//				result[i]= (IPluginEntry)getPluginEntryModels()[i];
		//			}
		return result;
	}

	/**
	 * @see IFeature#getDataEntries()
	 */
	public INonPluginEntry[] getNonPluginEntries() {
		int length = getNonPluginEntryModels().length;
		INonPluginEntry[] result = new INonPluginEntry[length];
		if (length > 0) {
			result = (INonPluginEntry[]) getNonPluginEntryModels();
		}
		return result;
	}

	/**
	 * @see IPluginContainer#getPluginEntryCount()
	 */
	public int getPluginEntryCount() {
		return getPluginEntryModels().length;
	}

	/**
	 * @see IFeature#getImports()
	 */
	public IImport[] getImports() {
		int length = getImportModels().length;
		IImport[] result = new IImport[length];
		if (length > 0) {
			result = (IImport[]) getImportModels();
		}
		return result;
	}

	/**
	 * Sets the pluginEntries
	 * @param pluginEntries The pluginEntries to set
	 */
	public void setPluginEntries(IPluginEntry[] pluginEntries) {
		if (pluginEntries != null) {
			for (int i = 0; i < pluginEntries.length; i++) {
				addPluginEntry(pluginEntries[i]);
			}
		}
	}

	/**
	 * Sets the import
	 * @param imports The imports to set
	 */
	public void setImports(IImport[] imports) {
		if (imports != null) {
			for (int i = 0; i < imports.length; i++) {
				addImport(imports[i]);
			}
		}
	}

	/**
	 * @see IPluginContainer#addPluginEntry(IPluginEntry)
	 */
	public void addPluginEntry(IPluginEntry pluginEntry) {
		if (pluginEntry != null) {
			addPluginEntryModel((PluginEntryModel) pluginEntry);
		}
	}

	/**
	 * @see IFeature#addDataEntry(INonPluginEntry)
	 */
	public void addNonPluginEntry(INonPluginEntry dataEntry) {
		if (dataEntry != null) {
			addNonPluginEntryModel((NonPluginEntryModel) dataEntry);
		}
	}

	/**
	 * Adds an import
	 * @param anImport The import to add
	 */
	public void addImport(IImport anImport) {
		if (anImport != null) {
			addImportModel((ImportModel) anImport);
		}
	}

	/**
	 * @see IPluginContainer#store(IPluginEntry, String, InputStream)
	 */
	public void store(IPluginEntry pluginEntry, String contentKey, InputStream inStream) throws CoreException {
		// check if pluginEntry already exists before passing to the site
		// anything else ?
		boolean found = false;
		int i = 0;
		IPluginEntry[] entries = getPluginEntries();
		while (i < entries.length && !found) {
			if (entries[i].equals(pluginEntry)) {
				found = true;
			}
			i++;
		}
		if (!found) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "The plugin:" + pluginEntry.getIdentifier().toString() + " is not part of the plugins of the feature:" + this.getIdentifier().toString(), null);
			throw new CoreException(status);
		}
		getSite().store(pluginEntry, contentKey, inStream);
	}

	/**
	 * perform pre processing before opening a plugin archive
	 * @param entry the plugin about to be opened
	 */
	protected void open(IPluginEntry entry) {
	};

	/**
	 * perform post processing to close a plugin archive
	 * @param entry the plugin about to be closed
	 */
	protected void close(IPluginEntry entry) throws IOException {
	};

	/**
	 * perform pre processing before opening the feature archive
	 */
	protected void openFeature() {
	};

	/**
	 * perform post processing to close a feature archive
	 */
	public void closeFeature() throws IOException {
	};

	/**
	 * @see IFeature#getArchives()
	 * Private implementation of the feature. return the list of ID.
	 * Call the site with the ID to get the URL of the contentReference of the Site
	 */
	public abstract String[] getArchives();

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

	/**
	 * return the list of FILE to be transfered for a Plugin
	 */
	protected abstract String[] getStorageUnitNames(IPluginEntry pluginEntry) throws CoreException;

	/**
	 * return the list of FILE to be transfered from within the Feature
	 */
	protected abstract String[] getStorageUnitNames() throws CoreException;

	/**
	 * return the Stream of the FILE to be transfered for a Plugin
	 */
	protected abstract InputStream getInputStreamFor(IPluginEntry pluginEntry, String name) throws CoreException;

	/**
	 * return the Stream of FILE to be transfered from within the Feature
	 */
	protected abstract InputStream getInputStreamFor(String name) throws IOException, CoreException;

	/**
	 * returns the list of archive to transfer/install
	 * in order to install the list of plugins
	 * 
	 * @param pluginsToInstall list of plugin to install 
	 */
	protected abstract String[] getContentReferenceToInstall(IPluginEntry[] pluginsToInstall);

	/*
	* @see IAdaptable#getAdapter(Class)
	*/
	public Object getAdapter(Class adapter) {
		return null;
	}

}