package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * An object which can create install related model objects (typically when
 * parsing feature manifest files and site maps).
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 */

public class SiteModelFactory {
	private MultiStatus status;
	
	/**
	 * Creates a factory which can be used to create install model objects.
	 * Errors and warnings during parsing etc. can be logged to the given 
	 * status via the <code>error</code> method.
	 *
	 * @param status the status to which errors should be logged
	 */
	public SiteModelFactory(MultiStatus status) {
		super();
		this.status = status;
	}

	/**
	 * Returns a new site map model which is not initialized.
	 *
	 * @return a new site map model
	 */
	public SiteMapModel createSiteMapModel() {
		return new SiteMapModel();
	}

	/**
	 * Returns a new feture reference model which is not initialized.
	 *
	 * @return a new feature reference model
	 */
	public FeatureReferenceModel createFeatureReferenceModel() {
		return new FeatureReferenceModel();
	}

	/**
	 * Returns a new URL Entry model which is not initialized.
	 *
	 * @return a new URL Entry model
	 */
	public URLEntryModel createURLEntryModel() {
		return new URLEntryModel();
	}

	/**
	 * Returns a new site category model which is not initialized.
	 *
	 * @return a new site category model
	 */
	public SiteCategoryModel createSiteCategoryModel() {
		return new SiteCategoryModel();
	}
	
	/**
	 * Handles an error state specified by the status.  The collection of all logged status
	 * objects can be accessed using <code>getStatus()</code>.
	 *
	 * @param error a status detailing the error condition
	 */
	public void error(IStatus error) {
		status.add(error);
		if (InternalPlatform.DEBUG && InternalPlatform.DEBUG_PLUGINS)
			System.out.println(error.toString());
	}
	
	/**
	 * Returns all of the status objects logged thus far by this factory.
	 *
	 * @return a multi-status containing all of the logged status objects
	 */
	public MultiStatus getStatus() {
		return status;
	}
}

