package org.eclipse.update.internal.core;

import org.eclipse.update.core.INonPluginEntry;
import org.eclipse.update.core.VersionedIdentifier;

public class NonPluginEntry implements INonPluginEntry {
	
	private String identifier;
	private int downloadSize ;
	private int installSize;
	
	/**
	 * Constructor
	 */
	public NonPluginEntry(String identifier) {
		this.identifier = identifier;
	}

	/*
	 * @see INonPluginEntry#getIdentifier()
	 */
	public String getIdentifier() {
		return identifier;
	}

	/*
	 * @see INonPluginEntry#getDownloadSize()
	 */
	public int getDownloadSize() {
		return downloadSize;
	}

	/*
	 * @see INonPluginEntry#getInstallSize()
	 */
	public int getInstallSize() {
		return installSize;
	}

	/**
	 * Sets the identifier.
	 * @param identifier The identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Sets the downloadSize.
	 * @param downloadSize The downloadSize to set
	 */
	public void setDownloadSize(int downloadSize) {
		this.downloadSize = downloadSize;
	}

	/**
	 * Sets the installSize.
	 * @param installSize The installSize to set
	 */
	public void setInstallSize(int installSize) {
		this.installSize = installSize;
	}

}

