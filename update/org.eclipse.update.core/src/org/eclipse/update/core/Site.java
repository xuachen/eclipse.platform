package org.eclipse.update.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.core.runtime.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.internal.core.*;
import org.eclipse.update.internal.core.Writer;

public abstract class Site extends SiteMapModel implements ISite, IWritable {

	/**
	 * default path under the site where plugins will be installed
	 */
	public static final String DEFAULT_PLUGIN_PATH = "plugins/";
	/**
	 * default path under the site where plugins will be installed
	 */
	//FIXME: fragment
	public static final String DEFAULT_FRAGMENT_PATH = "fragments/";

	/**
	 * default path, under site, where featuresConfigured will be installed
	 */
	public static final String DEFAULT_FEATURE_PATH = "features/";

	public static final String SITE_FILE = "site";
	public static final String SITE_XML = SITE_FILE + ".xml";
	private SiteParser parser;

	private ListenersList listeners = new ListenersList();
	private URL siteURL;
	private URL infoURL;

	/**
	 * The content consumer of the Site
	 */
	private ISiteContentConsumer contentConsumer;

	/**
	 * The content provider of the Site
	 */
	private ISiteContentProvider siteContentProvider;

	/**
	 * Constructor for Site
	 */
	public Site() throws CoreException, InvalidSiteTypeException {
		super();
	}

	/**
	 * Saves the site into the site.xml
	 */
	public void save() throws CoreException {
		File file = new File(getURL().getFile() + SITE_XML);
		try {
			PrintWriter fileWriter = new PrintWriter(new FileOutputStream(file));
			Writer writer = new Writer(); 
			writer.writeSite(this, fileWriter);
			fileWriter.close();
		} catch (FileNotFoundException e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Cannot save site into " + file.getAbsolutePath(), e);
			throw new CoreException(status);
		}
	}

	/*
	 * @see ISite#addSiteChangedListener(ISiteChangedListener)
	 */
	public void addSiteChangedListener(ISiteChangedListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/*
	 * @see ISite#removeSiteChangedListener(ISiteChangedListener)
	 */
	public void removeSiteChangedListener(ISiteChangedListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/*
	 * @see ISite#install(IFeature, IProgressMonitor)
	 */
	public IFeatureReference install(IFeature sourceFeature, IProgressMonitor monitor) throws CoreException {
		// should start Unit Of Work and manage Progress Monitor
		IFeature localFeature = createExecutableFeature(sourceFeature);
		sourceFeature.install(localFeature, monitor);
		IFeatureReference localFeatureReference = new FeatureReference();
		localFeatureReference.setSite(this);
		localFeatureReference.setURL(localFeature.getURL());
		this.addFeatureReference(localFeatureReference);

		// notify listeners
		Object[] siteListeners = listeners.getListeners();
		for (int i = 0; i < siteListeners.length; i++) {
			((ISiteChangedListener) siteListeners[i]).featureInstalled(localFeature);
		}
		return localFeatureReference;
	}

	/**
	 * adds a feature reference
	 * @param feature The feature reference to add
	 */
	private void addFeatureReference(IFeatureReference feature) {
		addFeatureReferenceModel((FeatureReferenceModel) feature);
	}

	/*
	 * @see ISite#remove(IFeature, IProgressMonitor)
	 */
	public void remove(IFeature feature, IProgressMonitor monitor) throws CoreException {

		((Feature) feature).remove(monitor);

		// remove feature reference
		IFeatureReference[] featureReferences = getFeatureReferences();
		if (featureReferences != null) {
			for (int indexRef = 0; indexRef < featureReferences.length; indexRef++) {
				IFeatureReference element = featureReferences[indexRef];
				if (element.getURL().equals(feature.getURL())) {
					removeFeatureReferenceModel((FeatureReferenceModel) element);
					break;
				}
			}
		}

		// notify listeners

		Object[] siteListeners = listeners.getListeners();
		for (int i = 0; i < siteListeners.length; i++) {
			((ISiteChangedListener) siteListeners[i]).featureUninstalled(feature);
		}

	}

	/**
	 * 
	 */
	private IFeature createExecutableFeature(IFeature sourceFeature) throws CoreException {
		String executableFeatureType = getDefaultExecutableFeatureType();
		IFeature result = null;
		if (executableFeatureType != null) {
			IFeatureFactory factory = FeatureTypeFactory.getInstance().getFactory(executableFeatureType);
			ContentReference localFeatureContentReference = this.getSiteContentProvider().getFeatureArchivesReferences(sourceFeature);
			result = factory.createFeature(localFeatureContentReference.asURL(), this);
		}
		return result;
	}

	/**
	 * returns true if we need to optimize the install by copying the 
	 * archives in teh TEMP directory prior to install
	 * Default is true
	 */
	public boolean optimize() {
		return true;
	}

	/*
	 * @see ISite#getURL()
	 */
	public URL getURL() {
		return siteURL;
	}

	/*
	 * @see ISite#getFeatureReferences()
	 */
	public IFeatureReference[] getFeatureReferences() {
		int length = getFeatureReferenceModels().length;
		IFeatureReference[] result = new IFeatureReference[length];
		if (length > 0) {
			result = (IFeatureReference[]) getFeatureReferenceModels();
		}
		return result;
	}

	/*
	 * @see ISite#getArchives()
	 */
	public IURLEntry[] getArchives() {
		int length = getArchiveReferenceModels().length;
		IURLEntry[] result = new IURLEntry[length];
		if (length > 0) {
			result = (IURLEntry[]) getArchiveReferenceModels();
		}
		return result;
	}

	/*
	 * @see ISite#getInfoURL()
	 */
	public URL getInfoURL() {
		return infoURL;
	}

	/*
	 * @see ISite#getCategories()
	 */
	public ICategory[] getCategories() {
		int length = getCategoryModels().length;
		ICategory[] result = new ICategory[length];
		if (length > 0) {
			result = (ICategory[]) getCategoryModels();
		}
		return result;
	}

	/*
	 * @see ISite#getCategory(String)
	 */
	public ICategory getCategory(String key) {
		ICategory result = null;
		boolean found = false;
		int length = getCategoryModels().length;

		for (int i = 0; i < length; i++) {
			if (getCategoryModels()[i].getName().equals(key)) {
				result = (ICategory) getCategoryModels()[i];
				found = true;
				break;
			}
		}

		//DEBUG:
		if (UpdateManagerPlugin.DEBUG && UpdateManagerPlugin.DEBUG_SHOW_WARNINGS && !found) {
			UpdateManagerPlugin.getPlugin().debug("Cannot find:" + key + " category in site:" + this.getURL().toExternalForm());
			if (getCategoryModels().length <= 0)
				UpdateManagerPlugin.getPlugin().debug("The Site does not contain any categories.");
		}

		return result;
	}

	/*
	 * @see IPluginContainer#getPluginEntries()
	 */
	public IPluginEntry[] getPluginEntries() {
		return null;
	}

	/*
	* @see ISite#setContentConsumer(ISiteContentConsumer)
	*/
	public void setContentConsumer(ISiteContentConsumer contentConsumer) {
		this.contentConsumer = contentConsumer;
	}

	/*
	 * @see ISite#getContentConsumer()
	 */
	public ISiteContentConsumer getContentConsumer() throws CoreException {
		if (contentConsumer == null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "FeatureContentConsumer not set for site:" + getURL().toExternalForm(), null);
			throw new CoreException(status);
		}

		return contentConsumer;
	}

	/*
	 * @see ISite#setSiteContentProvider(ISiteContentProvider)
	 */
	public void setSiteContentProvider(ISiteContentProvider siteContentProvider) {
		this.siteContentProvider = siteContentProvider;
	}

	/*
	 * @see ISite#getSiteContentProvider()
	 */
	public ISiteContentProvider getSiteContentProvider() throws CoreException {
		if (contentConsumer == null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Content Provider not set for site:" + getURL().toExternalForm(), null);
			throw new CoreException(status);
		}
		return siteContentProvider;
	}


	/*
	 * @see IWritable#write(int, PrintWriter)
	 */
	public void write(int indent, PrintWriter w) {
	}

	/*
	 * @see IPluginContainer#getPluginEntryCount()
	 */
	public int getPluginEntryCount() {
		return 0;
	}

	/*
	 * @see IPluginContainer#getDownloadSize(IPluginEntry)
	 */
	public long getDownloadSize(IPluginEntry entry) {
		return 0;
	}

	/*
	 * @see IPluginContainer#getInstallSize(IPluginEntry)
	 */
	public long getInstallSize(IPluginEntry entry) {
		return 0;
	}

	/*
	 * @see IPluginContainer#addPluginEntry(IPluginEntry)
	 */
	public void addPluginEntry(IPluginEntry pluginEntry) {
	}

	/*
	 * @see IPluginContainer#store(IPluginEntry, String, InputStream, IProgressMonitor)
	 */
	public void store(IPluginEntry entry, String name, InputStream inStream, IProgressMonitor monitor) throws CoreException {
	}

	/*
	 * @see IPluginContainer#remove(IPluginEntry, IProgressMonitor)
	 */
	public void remove(IPluginEntry entry, IProgressMonitor monitor) throws CoreException {
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}