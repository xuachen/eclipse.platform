package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.*;

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
	private IContentConsumer contentConsumer;

	/**
	 * The content provider of the Site
	 */
	private ISiteContentProvider siteContentProvider;

	/**
	 * Constructor for Site
	 */
	public Site(URL siteReference) throws CoreException, InvalidSiteTypeException {
		super();
		this.siteURL = siteReference;
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

	/**
	 * @see ISite#addSiteChangedListener(ISiteChangedListener)
	 */
	public void addSiteChangedListener(ISiteChangedListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * @see ISite#removeSiteChangedListener(ISiteChangedListener)
	 */
	public void removeSiteChangedListener(ISiteChangedListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * @see ISite#install(IFeature, IProgressMonitor)
	 */
	public IFeatureReference install(IFeature sourceFeature, IProgressMonitor monitor) throws CoreException {
		// should start Unit Of Work and manage Progress Monitor
		IFeature localFeature = createExecutableFeature(sourceFeature);
		sourceFeature.install(localFeature, monitor);
		IFeatureReference localReference = new FeatureReference(this, localFeature.getURL());
		this.addFeatureReference(localReference);

		// notify listeners
		Object[] siteListeners = listeners.getListeners();
		for (int i = 0; i < siteListeners.length; i++) {
			((ISiteChangedListener) siteListeners[i]).featureInstalled(localFeature);
		}
		return localReference;
	}

	/**
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
	public IFeature createExecutableFeature(IFeature sourceFeature) throws CoreException {
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
	 * store DefaultFeature files/ Features info into the Site
	 */
	protected abstract void storeFeatureInfo(VersionedIdentifier featureIdentifier, String contentKey, InputStream inStream) throws CoreException;

	/**
	 * removes DefaultFeature files/ DefaultFeature info from the Site
	 */
	protected abstract void removeFeatureInfo(VersionedIdentifier featureIdentifier) throws CoreException;

	/**
	 * return the URL of the archive ID
	 */
	public abstract URL getURL(String archiveID) throws CoreException;
	/**
	 * parse the physical site to initialize the site object
	 * @throws CoreException
	 */
	protected abstract void parseSite() throws CoreException;

	/**
	 * returns true if we need to optimize the install by copying the 
	 * archives in teh TEMP directory prior to install
	 * Default is true
	 */
	public boolean optimize() {
		return true;
	}

	/**
	 * Gets the siteURL
	 * @return Returns a URL
	 */
	public URL getURL() {
		return siteURL;
	}

	/**
	 * return the appropriate resource bundle for this site
	 */
	public ResourceBundle getResourceBundle() throws IOException, CoreException {
		ResourceBundle bundle = null;
		try {
			ClassLoader l = new URLClassLoader(new URL[] { this.getURL()}, null);
			bundle = ResourceBundle.getBundle(SITE_FILE, Locale.getDefault(), l);
		} catch (MissingResourceException e) {
			//ok, there is no bundle, keep it as null
			//DEBUG:
			if (UpdateManagerPlugin.DEBUG && UpdateManagerPlugin.DEBUG_SHOW_WARNINGS) {
				UpdateManagerPlugin.getPlugin().debug(e.getLocalizedMessage() + ":" + this.getURL().toExternalForm());
			}
		}
		return bundle;
	}

	/**
	 * Gets the featuresConfigured
	 * @return Returns a IFeatureReference[]
	 */
	public IFeatureReference[] getFeatureReferences() {
		int length = getFeatureReferenceModels().length;
		IFeatureReference[] result = new IFeatureReference[length];
		if (length > 0) {
			result = (IFeatureReference[]) getFeatureReferenceModels();
		}
		return result;
	}

	/**
	 * adds a feature
	 * The feature is considered already installed. It does not install it.
	 * @param feature The feature to add
	 */
	public void addFeatureReference(IFeatureReference feature) {
		addFeatureReferenceModel((FeatureReferenceModel) feature);
	}

	/**
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

	/**
	 * return the URL associated with the id of teh archive for this site
	 * return null if the archiveId is null, empty or 
	 * if teh list of archives on the site is null or empty
	 * of if there is no URL associated with the archiveID for this site
	 */
	public URL getArchiveURLfor(String archiveId) {
		URL result = null;
		boolean found = false;

		int length = getArchiveReferenceModels().length;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				if (archiveId.trim().equalsIgnoreCase(getArchiveReferenceModels()[i].getPath())) {
					result = getArchiveReferenceModels()[i].getURL();
					found = true;
					break;
				}
			}
		}

		//DEBUG:
		if (UpdateManagerPlugin.DEBUG && UpdateManagerPlugin.DEBUG_SHOW_INSTALL) {
			String debugString = "Searching archive ID:" + archiveId + " in Site:" + getURL().toExternalForm() + "...";
			if (found) {
				debugString += "found , pointing to:" + result.toExternalForm();
			} else {
				debugString += "NOT FOUND";
			}
			UpdateManagerPlugin.getPlugin().debug(debugString);
		}

		return result;
	}

	/**
	 * adds an archive
	 * @param archive The archive to add
	 */
	public void addArchive(IURLEntry archive) {
		if (getArchiveURLfor(archive.getAnnotation()) != null) {
			// DEBUG:		
			if (UpdateManagerPlugin.DEBUG && UpdateManagerPlugin.DEBUG_SHOW_WARNINGS) {
				UpdateManagerPlugin.getPlugin().debug("The Archive with ID:" + archive.getAnnotation() + " already exist on the site.");
			}
		} else {
			addArchiveReferenceModel((ArchiveReferenceModel) archive);
		}
	}

	/**
	 * Sets the archives
	 * @param archives The archives to set
	 */
	public void setArchives(IURLEntry[] _archives) {
		if (_archives != null) {
			for (int i = 0; i < _archives.length; i++) {
				this.addArchive(_archives[i]);
			}
		}
	}

	/**
	 * @see ISite#getInfoURL()
	 */
	public URL getInfoURL() {
		return infoURL;
	}

	/**
	 * Sets the infoURL
	 * @param infoURL The infoURL to set
	 */
	public void setInfoURL(URL infoURL) {
		this.infoURL = infoURL;
	}

	/**
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

	/**
	 * adds a category
	 * @param category The category to add
	 */
	public void addCategory(ICategory category) {
		addCategoryModel((SiteCategoryModel) category);
	}

	/**
	 * returns the associated ICategory
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
	 * @see IWritable#write(int, PrintWriter)
	 */
	public void write(int indent, PrintWriter w) {

		String gap = "";
		for (int i = 0; i < indent; i++)
			gap += " ";
		String increment = "";
		for (int i = 0; i < IWritable.INDENT; i++)
			increment += " ";

		w.print(gap + "<" + SiteParser.SITE + " ");
		// FIXME: site type to implement
		// 
		// Site URL
		String URLInfoString = null;
		if (getInfoURL() != null) {
			URLInfoString = UpdateManagerUtils.getURLAsString(this.getURL(), getInfoURL());
			w.print("url=\"" + Writer.xmlSafe(URLInfoString) + "\"");
		}
		w.println(">");
		w.println("");

		IFeatureReference[] refs = getFeatureReferences();
		for (int index = 0; index < refs.length; index++) {
			FeatureReference element = (FeatureReference) refs[index];
			element.write(indent, w);
		}
		w.println("");

		IURLEntry[] archives = getArchives();
		for (int index = 0; index < archives.length; index++) {
			IURLEntry element = (IURLEntry) archives[index];
			URLInfoString = UpdateManagerUtils.getURLAsString(this.getURL(), element.getURL());
			w.println(gap + "<" + SiteParser.ARCHIVE + " id=\"" + Writer.xmlSafe(element.getAnnotation()) + "\" url=\"" + Writer.xmlSafe(URLInfoString) + "\"/>");
		}
		w.println("");

		ICategory[] categories = getCategories();
		for (int index = 0; index < categories.length; index++) {
			Category element = (Category) categories[index];
			w.println(gap + "<" + SiteParser.CATEGORY_DEF + " label=\"" + Writer.xmlSafe(element.getLabel()) + "\" name=\"" + Writer.xmlSafe(element.getName()) + "\">");

			IURLEntry info = element.getDescription();
			if (info != null) {
				w.print(gap + increment + "<" + SiteParser.DESCRIPTION + " ");
				URLInfoString = null;
				if (info.getURL() != null) {
					URLInfoString = UpdateManagerUtils.getURLAsString(this.getURL(), info.getURL());
					w.print("url=\"" + Writer.xmlSafe(URLInfoString) + "\"");
				}
				w.println(">");
				if (info.getAnnotation() != null) {
					w.println(gap + increment + increment + Writer.xmlSafe(info.getAnnotation()));
				}
				w.print(gap + increment + "</" + SiteParser.DESCRIPTION + ">");
			}
			w.println(gap + "</" + SiteParser.CATEGORY_DEF + ">");

		}
		w.println("");
		// end
		w.println("</" + SiteParser.SITE + ">");
	}

	/*
	 * @see IPluginContainer#getPluginEntries()
	 */
	public IPluginEntry[] getPluginEntries() {
		return null;
	}

	/*
	* @see ISite#setContentConsumer(IContentConsumer)
	*/
	public void setContentConsumer(IContentConsumer contentConsumer) {
		this.contentConsumer = contentConsumer;
	}

	/*
	 * @see ISite#getContentConsumer()
	 */
	public IContentConsumer getContentConsumer() throws CoreException {
		if (contentConsumer == null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "ContentConsumer not set for site:" + getURL().toExternalForm(), null);
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

}