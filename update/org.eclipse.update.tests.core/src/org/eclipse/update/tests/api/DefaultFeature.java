/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.update.tests.api;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.internal.core.FeatureExecutableContentProvider;
/**
 * Abstract Class that implements most of the behavior of a feature
 * A feature ALWAYS belongs to an ISite
 */
public class DefaultFeature extends Feature {


	/**
	 * Copy constructor
	 */
	public DefaultFeature(IFeature sourceFeature, ISite targetSite) throws CoreException {
		this(targetSite);
		this.setFeatureContentProvider(sourceFeature.getFeatureContentProvider());
		this.setIdentifier(sourceFeature.getVersionedIdentifier());
		this.setLabel(sourceFeature.getName());
		this.setUpdateSiteEntry(sourceFeature.getUpdateSiteEntry());
		this.setDiscoverySiteEntries(sourceFeature.getDiscoverySiteEntries());
		this.setProvider(sourceFeature.getProvider());
		this.setDescription(sourceFeature.getDescription());
		this.setCopyright(sourceFeature.getCopyright());
		this.setLicense(sourceFeature.getLicense());
		this.setPluginEntries(sourceFeature.getPluginEntries());
		this.setImage(sourceFeature.getImageURL());
	}
	
	/**
	 * Constructor
	 */
	public DefaultFeature(ISite targetSite) throws CoreException {
		super();
		this.setSite(targetSite);		
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
		setDiscoverySiteEntryModels((URLEntry[]) discoveryInfos);
	}

	/**
	 * Sets the updateSiteEntry
	 * @param updateInfo The updateInfo to set
	 */
	public void setUpdateSiteEntry(IURLEntry updateInfo) {
		setUpdateSiteEntryModel((URLEntry) updateInfo);
	}

	/**
	 * Adds a discoveryInfo
	 * @param discoveryInfo The discoveryInfo to add
	 */
	public void addDiscoverySiteEntry(IURLEntry discoveryInfo) {
		addDiscoverySiteEntryModel((URLEntry) discoveryInfo);
	}

	/**
	 * Sets the description
	 * @param description The description to set
	 */
	public void setDescription(IURLEntry description) {
		setDescriptionModel((URLEntry) description);
	}

	/**
	 * Sets the copyright
	 * @param copyright The copyright to set
	 */
	public void setCopyright(IURLEntry copyright) {
		setCopyrightModel((URLEntry) copyright);
	}

	/**
	 * Sets the license
	 * @param license The license to set
	 */
	public void setLicense(IURLEntry license) {
		setLicenseModel((URLEntry) license);
	}

	/**
	 * Sets the image
	 * @param image The image to set
	 */
	public void setImage(URL image) {
		if (image==null) return;
		setImageURLString(image.toExternalForm());
	}

	/**
	 * Adds an import
	 * @param anImport The import to add
	 */
	public void addImport(IImport anImport) {
		if (anImport != null) {
			addImportModel((Import) anImport);
		}
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
	 * sets teh URL isf not already present
	 */
	public void setURL(URL url) throws CoreException{
		//if (getFeatureContentProvider()==null){
			FeatureContentProvider contentProvider = new FeatureExecutableContentProvider(url);
			this.setFeatureContentProvider(contentProvider);
			contentProvider.setFeature(this);
		//}
	}
	
	
	/*
	 * @see IPluginContainer#addPluginEntry(IPluginEntry)
	 */
	public void addPluginEntry(IPluginEntry pluginEntry) {
		if (pluginEntry != null) {
			addPluginEntry((PluginEntry) pluginEntry);
		}
	}

	/*
	 * @see IFeature#addNonPluginEntry(INonPluginEntry)
	 */
	public void addNonPluginEntry(INonPluginEntry dataEntry) {
		if (dataEntry != null) {
			addNonPluginEntry((NonPluginEntry) dataEntry);
		}
	}	

	}