package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

import java.net.URL;

/**
 * An object which represents a feature reference.
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 * @since 2.0
 */

public class FeatureReferenceModel extends ModelObject {
	
	private String type;
	private String url;
	private String[] categoryNames;
	
	/**
	 * Creates an uninitialized model object.
	 * 
	 * @since 2.0
	 */
	public FeatureReferenceModel() {
		super();
	}
	
	/**
	 * @since 2.0
	 */
	public String getType() {
		return type;
	}

	/**
	 * @since 2.0
	 */	
	public String getURLString() {
		return url;
	}

	/**
	 * @since 2.0
	 */	
	public String[] getCategoryNames() {
		return categoryNames;
	}

	/**
	 * @since 2.0
	 */
	public void setType(String type) {
		assertIsWriteable();
		this.type = type;
	}

	/**
	 * @since 2.0
	 */
	public void setURLString(String url) {
		assertIsWriteable();
		this.url = url;
	}

	/**
	 * @since 2.0
	 */
	public void setCategoryNames(String[] categoryNames) {
		assertIsWriteable();
		this.categoryNames = categoryNames;
	}
}
