package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An object which represents a group of related
 * plug-in or non-plug-in entries in the
 * packaging manifest.
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 * @since 2.0
 */

public class ContentGroupModel extends ModelObject {

	private String id;
	private String label;
	private boolean optional;
	private URLEntryModel description;
	private String[] includes;
	private String[] excludes;
	private List /*of ImportModel*/ imports;
	private List /*of PluginEntryModel*/ pluginEntries;
	private List /*of NonPluginEntryModel*/ nonPluginEntries;
	private List /*of ContentGroupModel*/ nestedGroupEntries;

	/**
	 * Creates a uninitialized model object.
	 * 
	 * @since 2.0
	 */
	public ContentGroupModel() {
		super();
	}

	/**
	 * @since 2.0
	 */	
	public String getIdentifier() {
		return id;
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
	public boolean isOptional() {
		return optional;
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
	public String[] getIncludedGroupEntries() {
		if (includes == null)
			return new String[0];

		String[] list = new String[includes.length];
		System.arraycopy(includes, 0, list, 0, includes.length);
		return list;
	}

	/**
	 * @since 2.0
	 */	
	public String[] getExcludedGroupEntries() {
		if (excludes == null)
			return new String[0];

		String[] list = new String[excludes.length];
		System.arraycopy(excludes, 0, list, 0, excludes.length);
		return list;
	}

	/**
	 * @since 2.0
	 */	
	public ImportModel[] getImports() {
		if (imports == null)
			return new ImportModel[0];

		return (ImportModel[]) imports.toArray(new ImportModel[0]);
	}

	/**
	 * @since 2.0
	 */	
	public PluginEntryModel[] getPluginEntries() {
		if (pluginEntries == null)
			return new PluginEntryModel[0];

		return (PluginEntryModel[]) pluginEntries.toArray(new PluginEntryModel[0]);
	}

	/**
	 * @since 2.0
	 */	
	public NonPluginEntryModel[] getNonPluginEntries() {
		if (nonPluginEntries == null)
			return new NonPluginEntryModel[0];

		return (NonPluginEntryModel[]) nonPluginEntries.toArray(
			new NonPluginEntryModel[0]);
	}

	/**
	 * @since 2.0
	 */	
	public ContentGroupModel[] getNestedGroupEntries() {
		if (nestedGroupEntries == null)
			return new ContentGroupModel[0];

		return (ContentGroupModel[]) nestedGroupEntries.toArray(
			new ContentGroupModel[0]);
	}

	/**
	 * @since 2.0
	 */	
	public void setIdentifier(String id) {
		assertIsWriteable();
		this.id = id;
	}

	/**
	 * @since 2.0
	 */	
	public void getLabel(String label) {
		assertIsWriteable();
		this.label = label;
	}

	/**
	 * @since 2.0
	 */	
	public void isOptional(boolean optional) {
		assertIsWriteable();
		this.optional = optional;
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
	public void setIncludedGroupEntries(String[] includes) {
		assertIsWriteable();
		this.includes = includes;
	}

	/**
	 * @since 2.0
	 */	
	public void setExcludedGroupEntries(String[] excludes) {
		assertIsWriteable();
		this.excludes = excludes;
	}

	public void setImports(ImportModel[] imports) {
		assertIsWriteable();
		if (imports == null)
			this.imports = null;
		else
			this.imports = Arrays.asList(imports);
	}

	/**
	 * @since 2.0
	 */	
	public void setPluginEntries(PluginEntryModel[] pluginEntries) {
		assertIsWriteable();
		if (pluginEntries == null)
			this.pluginEntries = null;
		else
			this.pluginEntries = Arrays.asList(pluginEntries);
	}

	/**
	 * @since 2.0
	 */	
	public void setNonPluginEntries(NonPluginEntryModel[] nonPluginEntries) {
		assertIsWriteable();
		if (nonPluginEntries == null)
			this.nonPluginEntries = null;
		else
			this.nonPluginEntries = Arrays.asList(nonPluginEntries);
	}

	/**
	 * @since 2.0
	 */	
	public void setNestedGroupEntries(ContentGroupModel[] nestedGroupEntries) {
		assertIsWriteable();
		if (nestedGroupEntries == null)
			this.nestedGroupEntries = null;
		else
			this.nestedGroupEntries = Arrays.asList(nestedGroupEntries);
	}

	/**
	 * @since 2.0
	 */	
	public void addImport(ImportModel importEntry) {
		assertIsWriteable();
		if (imports == null)
			this.imports = new ArrayList();
		if (!this.imports.contains(importEntry))
			this.imports.add(importEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void addPluginEntry(PluginEntryModel pluginEntry) {
		assertIsWriteable();
		if (this.pluginEntries == null)
			this.pluginEntries = new ArrayList();
		if (!this.pluginEntries.contains(pluginEntry))
			this.pluginEntries.add(pluginEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void addNonPluginEntry(NonPluginEntryModel nonPluginEntry) {
		assertIsWriteable();
		if (this.nonPluginEntries == null)
			this.nonPluginEntries = new ArrayList();
		if (!this.nonPluginEntries.contains(nonPluginEntry))
			this.nonPluginEntries.add(nonPluginEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void addNestedGroupEntry(ContentGroupModel nestedGroupEntry) {
		assertIsWriteable();
		if (this.nestedGroupEntries == null)
			this.nestedGroupEntries = new ArrayList();
		if (!this.nestedGroupEntries.contains(nestedGroupEntry))
			this.nestedGroupEntries.add(nestedGroupEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void removeImport(ImportModel importEntry) {
		assertIsWriteable();
		if (this.imports != null)
			this.imports.remove(importEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void removePluginEntry(PluginEntryModel pluginEntry) {
		assertIsWriteable();
		if (this.pluginEntries != null)
			this.pluginEntries.remove(pluginEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void removeNonPluginEntry(NonPluginEntryModel nonPluginEntry) {
		assertIsWriteable();
		if (this.nonPluginEntries != null)
			this.nonPluginEntries.remove(nonPluginEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void removeNestedGroupEntry(ContentGroupModel nestedGroupEntry) {
		assertIsWriteable();
		if (this.nestedGroupEntries != null)
			this.nestedGroupEntries.remove(nestedGroupEntry);
	}
	
	/**
	 * @since 2.0
	 */
	public void markReadOnly() {		
		markReferenceReadOnly(getDescription());
		markListReferenceReadOnly(getImports());
		markListReferenceReadOnly(getPluginEntries());
		markListReferenceReadOnly(getNonPluginEntries());
		markListReferenceReadOnly(getNestedGroupEntries());
	}
}