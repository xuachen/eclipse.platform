package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

import java.net.URL;

/**
 * An object which represents a site archive reference entry.
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 * @since 2.0
 */

public class ArchiveReferenceModel extends ModelObject {
	
	private String path;
	private String url;
	
	/**
	 * Creates a uninitialized model object.
	 * 
	 * @since 2.0
	 */
	public ArchiveReferenceModel() {
		super();
	}
		
	/**
	 * Returns path.
	 *
	 * @return text string, or <code>null</code>
	 * @since 2.0
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns URL string for the archive.
	 *
	 * @return url string, or <code>null</code>
	 * @since 2.0
	 */
	public String getURLString() {
		return url;
	}
	
	/**
	 * Returns the resolved URL for the archive.
	 * 
	 * @return url, or <code>null</code>
	 * @since 2.0
	 */
	public URL getURL() {
		return null;
	}
	
	/**
	 * Sets the path.
	 * This object must not be read-only.
	 *
	 * @param annotation string. Can be <code>null</code>.
	 * @since 2.0
	 */	
	public void setPath(String path) {
		assertIsWriteable();
		this.path = path;
	}
	
	/**
	 * Sets the URL for the archive.
	 * This object must not be read-only.
	 *
	 * @param url url for additional information. Can be <code>null</code>.
	 * @since 2.0
	 */	
	public void setURLString(String url) {
		assertIsWriteable();
		this.url = url;
	}
}
