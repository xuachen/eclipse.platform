package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

/**
 * An object which represents a category definition in a 
 * site map.
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 * @since 2.0
 */

public class SiteCategoryModel extends ModelObject {
	
	private String name;
	private String label;
	private URLEntryModel description;
	
	/**
	 * Creates an uninitialized model object.
	 * 
	 * @since 2.0
	 */
	public SiteCategoryModel() {
		super();
	}
	
	/**
	 * @since 2.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * @since 2.0
	 */	
	public String getLabel() {
		return label;
	}

	/**
	 * @since 2.0
	 */	
	public URLEntryModel getDescription() {
		return description;
	}

	/**
	 * @since 2.0
	 */
	public void setLabel(String label) {
		assertIsWriteable();
		this.label = label;
	}

	/**
	 * @since 2.0
	 */
	public void setName(String name) {
		assertIsWriteable();
		this.name = name;
	}

	/**
	 * @since 2.0
	 */
	public void setDescription(URLEntryModel description) {
		assertIsWriteable();
		this.description = description;
	}
	
	/**
	 * @since 2.0
	 */
	public void markReadOnly() {		
		markReferenceReadOnly(getDescription());
	}
}
