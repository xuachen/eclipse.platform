package org.eclipse.update.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import org.eclipse.update.core.IPluginContainer;
import org.eclipse.update.core.IPluginEntry;
import org.eclipse.update.core.VersionedIdentifier;
import org.eclipse.update.core.model.PluginEntryModel;
import org.eclipse.update.internal.core.*;


public class PluginEntry extends PluginEntryModel implements IPluginEntry {
	
	private IPluginContainer container;
	/**
	 * Constructor
	 */
	public PluginEntry(String id, String ver) {
		super();
		setPluginIdentifier(id);
		setPluginVersion(ver);
	}
	
	/**
	 * Constructor
	 */
	public PluginEntry(VersionedIdentifier identifier) {
		this(identifier.getIdentifier(), identifier.getVersion().toString());
	}

	/**
	 * @see IPluginEntry#getContainer()
	 */
	public IPluginContainer getContainer() {
		return container;
	}


	/**
	 * @see IPluginEntry#getIdentifier()
	 */
	public VersionedIdentifier getIdentifier() {
		return new VersionedIdentifier(getPluginIdentifier(),getPluginVersion());
	}


	/**
	 * Sets the container
	 * @param container The container to set
	 */
	public void setContainer(IPluginContainer container) {
		this.container = container;
	}

	}


