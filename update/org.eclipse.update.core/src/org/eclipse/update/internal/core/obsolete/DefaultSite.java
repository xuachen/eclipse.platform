package org.eclipse.update.internal.core.obsolete;
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
import org.eclipse.update.internal.core.*;
import org.eclipse.update.internal.core.Writer;

/**
 * @deprecated
 */

public abstract class DefaultSite extends Site {

	/**
	 * Constructor for Site
	 */
	public DefaultSite() throws CoreException, InvalidSiteTypeException {
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
	 * Gets the featuresConfigured
	 * @return Returns a IFeatureReference[]
	 */
	public IFeatureReference[] getFeatureReferences() {
		FeatureReferenceModel[] result = getFeatureReferenceModels();
		if (result.length == 0) 
			return new IFeatureReference[0];
		else
			return (IFeatureReference[]) result;
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

	}