package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import java.io.InputStream;

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

public class FeatureModelFactory {
	private MultiStatus status;
	
	/**
	 * Creates a factory which can be used to create install model objects.
	 * Errors and warnings during parsing etc. can be logged to the given 
	 * status via the <code>error</code> method.
	 *
	 * @param status the status to which errors should be logged
	 */
	public FeatureModelFactory(MultiStatus status) {
		super();
		this.status = status;
	}
	
	/**
	 * Constructs a feature model from stream
	 * 
	 * @param stream feature stream
	 */
	public FeatureModel parseFeature(InputStream stream) throws Exception {
		DefaultFeatureParser parser = new DefaultFeatureParser(this);
		return parser.parse(stream);
	}

	/**
	 * Returns a new feature model which is not initialized.
	 *
	 * @return a new feature model
	 */
	public FeatureModel createFeatureModel() {
		return new FeatureModel();
	}

	/**
	 * Returns a new feature install handler model which is not initialized.
	 *
	 * @return a new feature install handler model
	 */
	public InstallHandlerModel createInstallHandlerModel() {
		return new InstallHandlerModel();
	}

	/**
	 * Returns a new import model which is not initialized.
	 *
	 * @return a new import model
	 */
	public ImportModel createImportModel() {
		return new ImportModel();
	}

	/**
	 * Returns a new plug-in entry model which is not initialized.
	 *
	 * @return a new plug-in entry model
	 */
	public PluginEntryModel createPluginEntryModel() {
		return new PluginEntryModel();
	}

	/**
	 * Returns a new non-plug-in entry model which is not initialized.
	 *
	 * @return a new non-plug-in entry model
	 */
	public NonPluginEntryModel createNonPluginEntryModel() {
		return new NonPluginEntryModel();
	}

	/**
	 * Returns a new content group model which is not initialized.
	 *
	 * @return a new content group model
	 */
	public ContentGroupModel createContentGroupModel() {
		return new ContentGroupModel();
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
