package org.eclipse.update.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.internal.core.*;
/**
 * Abstract Class that implements most of the behavior of a feature
 * A feature ALWAYS belongs to an ISite
 */
public abstract class Feature extends FeatureModel implements IFeature {


	/**
	 * Delegating wrapper for IProgressMonitor used for feature
	 * installation handling.
	 * 
	 * NOTE: currently is just a straight delegating wrapper.
	 *       Extended handling function TBA 
	 * 
	 * @since 2.0
	 */	
	public static class ProgressMonitor implements IProgressMonitor {
		
		private IProgressMonitor monitor;
		
		public ProgressMonitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
		/*
		 * @see IProgressMonitor#beginTask(String, int)
		 */
		public void beginTask(String name, int totalWork) {
			monitor.beginTask(name, totalWork);
		}

			/*
		 * @see IProgressMonitor#done()
		 */
		public void done() {
			monitor.done();
		}

		/*
		 * @see IProgressMonitor#internalWorked(double)
		 */
		public void internalWorked(double work) {
			monitor.internalWorked(work);
		}

		/*
		 * @see IProgressMonitor#isCanceled()
		 */
		public boolean isCanceled() {
			return monitor.isCanceled();
		}

		/*
		 * @see IProgressMonitor#setCanceled(boolean)
		 */
		public void setCanceled(boolean value) {
			monitor.setCanceled(value);
		}

		/*
		 * @see IProgressMonitor#setTaskName(String)
		 */
		public void setTaskName(String name) {
			monitor.setTaskName(name);
		}

		/*
		 * @see IProgressMonitor#subTask(String)
		 */
		public void subTask(String name) {
			monitor.subTask(name);
		}

		/*
		 * @see IProgressMonitor#worked(int)
		 */
		public void worked(int work) {
			monitor.worked(work);
		}
	}

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
	 * The content provider of the Feature
	 */
	private IFeatureContentProvider featureContentProvider;

	/**
	 * The content consumer of the Feature
	 */
	private IContentConsumer contentConsumer;

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
		this(targetSite);
		this.setFeatureContentProvider(sourceFeature.getFeatureContentProvider());
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
	public Feature(ISite targetSite) throws CoreException {
		this.site = targetSite;
	}

	/*
	 * @see IFeature#getIdentifier()
	 */
	public VersionedIdentifier getIdentifier() {
		return new VersionedIdentifier(getFeatureIdentifier(), getFeatureVersion());
	}

	/*
	 * @see IFeature#getSite()
	 */
	public ISite getSite() {
		return site;
	}

	/*
	 * @see IFeature#getURL()
	 */
	public URL getURL() {
		IFeatureContentProvider contentProvider=null;
		try {
			contentProvider = getFeatureContentProvider();
		} catch (CoreException e){}
		return (contentProvider!=null)?contentProvider.getURL():null;
	}

	/*
	 * @see IFeature#getUpdateSiteEntry()
	 */
	public IURLEntry getUpdateSiteEntry() {
		return (IURLEntry) getUpdateSiteEntryModel();
	}

	/*
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

	/*
	 * @see IFeature#getDescription()
	 */
	public IURLEntry getDescription() {
		return (IURLEntry) getDescriptionModel();
	}

	/*
	 * @see IFeature#getCopyright()
	 */
	public IURLEntry getCopyright() {
		return (IURLEntry) getCopyrightModel();
	}

	/*
	 * @see IFeature#getLicense()
	 */
	public IURLEntry getLicense() {
		return (IURLEntry) getLicenseModel();
	}

	/*
	 * @see IFeature#getImage()
	 */
	public URL getImage() {
		return getImageURL();
	}
	/**
	 * Sets the site
	 * @param site The site to set
	 */
	public void setSite(ISite site) throws CoreException {
		if (site != null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Site already set for the feature " + getURL().toExternalForm(), null);
			throw new CoreException(status);
		}
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
	/*
	 * @see IFeature#isExecutable()
	 */
	public boolean isExecutable() {
		return false;
	}
	/*
	 * @see IFeature#isInstallable()
	 */
	public boolean isInstallable() {
		return false;
	}

	/*
	 * @see IFeature#install(IFeature, IProgressMonitor) throws CoreException
	 */
	public void install(IFeature targetFeature, IProgressMonitor monitor) throws CoreException {

		IPluginEntry[] sourceFeaturePluginEntries = getPluginEntries();
		ISite targetSite = targetFeature.getSite();
		IPluginEntry[] targetSitePluginEntries = (targetSite!=null)?site.getPluginEntries():null;
		Site tempSite = (Site) SiteManager.getTempSite();
		List contentReferencesToInstall = new ArrayList();

		//
		IContentConsumer consumer = getContentConsumer();

		//finds the contentReferences for this IFeature
		ContentReference[] references = getFeatureContentProvider().getFeatureEntryContentReferences();
		for (int i = 0; i < references.length; i++) {
			consumer.store(references[i], monitor);
		}

		// determine list of plugins to install
		// find the intersection between the two arrays of IPluginEntry...
		// The one teh site contains and teh one the feature contains
		IPluginEntry[] pluginsToInstall = intersection(sourceFeaturePluginEntries, targetSitePluginEntries);

		//finds the contentReferences for this IPluginEntry
		for (int i = 0; i < pluginsToInstall.length; i++) {
			IContentConsumer pluginConsumer = consumer.opens(pluginsToInstall[i]);
			references = getFeatureContentProvider().getPluginEntryContentReferences(pluginsToInstall[i]);
			for (int j = 0; j < references.length; j++) {
				consumer.store(references[j], monitor);
			}
			pluginConsumer.close();
		}

		// download and install non plugins bundles
		INonPluginEntry[] nonPluginsContentReferencesToInstall = getNonPluginEntries();
		for (int i = 0; i < nonPluginsContentReferencesToInstall.length; i++) {
			IContentConsumer nonPluginConsumer = consumer.opens(nonPluginsContentReferencesToInstall[i]);
			references = getFeatureContentProvider().getNonPluginEntryArchiveReferences(nonPluginsContentReferencesToInstall[i]);
			for (int j = 0; j < references.length; j++) {
				consumer.store(references[j], monitor);
			}
			nonPluginConsumer.close();
		}

		consumer.close();

	}

	/**
	 * remove myself...
	 */
	public void remove(IProgressMonitor monitor) throws CoreException {

		// remove the feature and the plugins if they are not used and not activated

		//
		IContentConsumer consumer = getContentConsumer();

		ContentReference[] references = null;

		// get the plugins from the feature
		IPluginEntry[] pluginsToRemove = ((SiteLocal) SiteManager.getLocalSite()).getUnusedPluginEntries(this);
		
		//finds the contentReferences for this IPluginEntry
		for (int i = 0; i < pluginsToRemove.length; i++) {
			IContentConsumer pluginConsumer = consumer.opens(pluginsToRemove[i]);
			references = getFeatureContentProvider().getPluginEntryContentReferences(pluginsToRemove[i]);
			for (int j = 0; j < references.length; j++) {
				consumer.remove(references[j], monitor);
			}
			pluginConsumer.close();
		}
		
		//finds the contentReferences for this IFeature
		references = getFeatureContentProvider().getFeatureEntryContentReferences();
		for (int i = 0; i < references.length; i++) {
			consumer.remove(references[i], monitor);
		}
		
		consumer.close();		
	}

	/*
	 * @see IPluginContainer#remove(IPluginEntry)
	 */
	public void remove(IPluginEntry entry) throws CoreException {
		((Site) getSite()).remove(entry);
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

	/*
	 * @see IPluginContainer#getPluginEntries()
	 */
	public IPluginEntry[] getPluginEntries() {
		int length = getPluginEntryModels().length;
		IPluginEntry[] result = new IPluginEntry[length];
		if (length > 0) {
			result = (IPluginEntry[]) getPluginEntryModels();
		}
		return result;
	}

	/*
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

	/*
	 * @see IPluginContainer#getPluginEntryCount()
	 */
	public int getPluginEntryCount() {
		return getPluginEntryModels().length;
	}

	/*
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

	/*
	 * @see IPluginContainer#addPluginEntry(IPluginEntry)
	 */
	public void addPluginEntry(IPluginEntry pluginEntry) {
		if (pluginEntry != null) {
			addPluginEntryModel((PluginEntryModel) pluginEntry);
		}
	}

	/*
	 * @see IFeature#addNonPluginEntry(INonPluginEntry)
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

	/*
	* @see IAdaptable#getAdapter(Class)
	*/
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * @see IFeature#setFeatureContentProvider(IFeatureContentProvider)
	 */
	public void setFeatureContentProvider(IFeatureContentProvider featureContentProvider) {
		this.featureContentProvider = featureContentProvider;
	}

	/**
	 * Gets the featureContentProvider.
	 * @return Returns a IFeatureContentProvider
	 */
	public IFeatureContentProvider getFeatureContentProvider() throws CoreException {
		if (featureContentProvider == null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Content Provider not set for feature:" + getURL().toExternalForm(), null);
			throw new CoreException(status);
		}
		return this.featureContentProvider;
	}


	/*
	 * @see IFeature#setContentConsumer(IContentConsumer)
	 */
	public void setContentConsumer(IContentConsumer contentConsumer) {
		this.contentConsumer = contentConsumer;
	}

	/*
	 * @see IFeature#getContentConsumer()
	 */
	public IContentConsumer getContentConsumer() throws CoreException {
		if (contentConsumer == null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "ContentConsumer not set for feature:" + getURL().toExternalForm(), null);
			throw new CoreException(status);
		}

		return contentConsumer;
	}
	/**
	 * remove myself...
	 */
	public void old_remove(IProgressMonitor monitor) throws CoreException {

		// remove the feature and the plugins if they are not used and not activated

		// get the plugins from the feature
		IPluginEntry[] pluginsToRemove = ((SiteLocal) SiteManager.getLocalSite()).getUnusedPluginEntries(this);

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

			// remove the Feature info
			String[] names = getStorageUnitNames(this);
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

}