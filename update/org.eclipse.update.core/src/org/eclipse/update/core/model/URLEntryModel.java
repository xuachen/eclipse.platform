package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

import java.net.URL;

/**
 * An object which represents an annotated URL entry.
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 * @since 2.0
 */

public class URLEntryModel extends ModelObject {
	
	private String annotation;
	private String url;
	
	/**
	 * Creates a uninitialized information entry model object.
	 * 
	 * @since 2.0
	 */
	public URLEntryModel() {
		super();
	}
		
	/**
	 * Returns annotation.
	 *
	 * @return text string, or <code>null</code>
	 * @since 2.0
	 */
	public String getAnnotation() {
		return annotation;
	}

	/**
	 * Returns URL string containing additional information.
	 *
	 * @return url, <code>null</code>
	 * @since 2.0
	 */
	public String getURLString() {
		return url;
	}
	
	/**
	 * Sets the annotation.
	 * This object must not be read-only.
	 *
	 * @param annotation string. Can be <code>null</code>.
	 * @since 2.0
	 */	
	public void setAnnotation(String annotation) {
		assertIsWriteable();
		this.annotation = annotation;
	}
	
	/**
	 * Sets the URL containing additional information.
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
