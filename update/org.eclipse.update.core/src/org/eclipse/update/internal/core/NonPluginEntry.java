package org.eclipse.update.internal.core;

import org.eclipse.update.core.INonPluginEntry;
import org.eclipse.update.core.VersionedIdentifier;
import org.eclipse.update.core.model.NonPluginEntryModel;

public class NonPluginEntry extends NonPluginEntryModel implements INonPluginEntry {
	
	/**
	 * Constructor
	 */
	public NonPluginEntry(String identifier) {
		super();
		setIdentifier(identifier);
	}

	}

