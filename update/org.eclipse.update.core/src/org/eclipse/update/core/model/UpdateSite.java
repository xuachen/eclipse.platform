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
package org.eclipse.update.core.model;

import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;



/**
 * Site model object.
 * <p>
 * This class may be instantiated or subclassed by clients. However, in most 
 * cases clients should instead instantiate or subclass the provided 
 * concrete implementation of this model.
 * </p>
 * @see org.eclipse.update.core.Site
 * @since 2.0
 */
public class UpdateSite extends Site implements IUpdateSite {

	private Set /*of CategoryModel*/ categories;

	/**
	 * Creates an uninitialized site model object.
	 * 
	 * @since 2.0
	 */
	public UpdateSite() {
		super();
	}


	/**
	 * Returns an array of category models for this site.
	 * 
	 * @return array of site category models, or an empty array.
	 * @since 2.0
	 */
	public ICategory[] getCategories() {
		if (categories == null)
			return new Category[0];

		return (Category[]) categories.toArray(arrayTypeFor(categories));
	}


	/**
	 * Sets the site categories.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param categories an array of category models
	 * @since 2.0
	 */
	void setCategoryModels(Category[] categories) {
		assertIsWriteable();
		if (categories == null)
			this.categories = null;
		else {
			this.categories = new TreeSet(Category.getComparator());
			this.categories.addAll(Arrays.asList(categories));
		}
	}


	/**
	 * Adds a category model to site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param category category model
	 * @since 2.0
	 */
	void addCategory(Category category) {
		assertIsWriteable();
		if (this.categories == null)
			this.categories = new TreeSet(Category.getComparator());
		if (!this.categories.contains(category))
			this.categories.add(category);
	}


	/**
	 * Removes a category model from site.
	 * Throws a runtime exception if this object is marked read-only.
	 * 
	 * @param category category model
	 * @since 2.0
	 */
	void removeCategory(Category category) {
		assertIsWriteable();
		if (this.categories != null)
			this.categories.remove(category);
	}

	/**
	 * Marks the model object as read-only.
	 * 
	 * @since 2.0
	 */
	public void markReadOnly() {
		super.markReadOnly();
		markListReferenceReadOnly((Category[])getCategories());
	}

	/**
	 * Resolve the model object.
	 * Any URL strings in the model are resolved relative to the 
	 * base URL argument. Any translatable strings in the model that are
	 * specified as translation keys are localized using the supplied 
	 * resource bundle.
	 * 
	 * @param base URL
	 * @param bundleURL resource bundle URL
	 * @exception MalformedURLException
	 * @since 2.0
	 */
	public void resolve(URL base, URL bundleURL) throws MalformedURLException {
		resolveListReference((Category[])getCategories(), base, bundleURL);
	}


	/**
	 * Returns the named site category.
	 * 
	 * @see ISite#getCategory(String)
	 * @since 2.0
	 */
	public ICategory getCategory(String key) {
		ICategory result = null;
		boolean found = false;
		int length = getCategories().length;

		for (int i = 0; i < length; i++) {
			if (getCategories()[i].getName().equals(key)) {
				result = (ICategory) getCategories()[i];
				found = true;
				break;
			}
		}

		//DEBUG:
		if (!found) {
			String URLString = (this.getURL() != null) ? this.getURL().toExternalForm() : "<no site url>";
			UpdateCore.warn(Policy.bind("Site.CannotFindCategory", key, URLString));
			//$NON-NLS-1$ //$NON-NLS-2$
			if (getCategories().length <= 0)
				UpdateCore.warn(Policy.bind("Site.NoCategories"));
			//$NON-NLS-1$
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeature(org.eclipse.update.core.IFeatureReference, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature getFeature(
		IFeatureReference featureRef,
		IProgressMonitor monitor)
		throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeatures()
	 */
	public IFeature[] getFeatures(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.update.core.ISite#getFeature(java.net.URL, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IFeature getFeature(URL featureURL, IProgressMonitor monitor)
		throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.update.core.IUpdateSite#getFeatureReference(org.eclipse.update.core.IFeature)
	 */
	public IFeatureReference getFeatureReference(IFeature feature) {
		// TODO Auto-generated method stub
		return null;
	}

}
