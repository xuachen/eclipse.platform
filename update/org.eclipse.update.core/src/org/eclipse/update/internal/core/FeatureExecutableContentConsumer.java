package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.*;

/**
 * Default implementation of an Executable DefaultFeature
 */

public class FeatureExecutableContentConsumer extends ContentConsumer {


	/**
	 * Feature
	 */
	private IFeature feature;
	
	/**
	 * parent
	 */
	private IPluginEntry pluginEntry = null;
	private INonPluginEntry nonPluginEntry = null;

	/*
	 * @see IContentConsumer#opens(INonPluginEntry)
	 */
	public IContentConsumer opens(INonPluginEntry nonPluginEntry) throws CoreException {
		if (pluginEntry != null || nonPluginEntry != null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			String text = "Content Consumer already opened: ";
			text += (pluginEntry == null) ? nonPluginEntry.getIdentifier() : pluginEntry.getIdentifier().toString();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, text, null);
			throw new CoreException(status);
		}

		this.nonPluginEntry = nonPluginEntry;
		return this;
	}

	/*
	 * @see IContentConsumer#opens(IPluginEntry)
	 */
	public IContentConsumer opens(IPluginEntry pluginEntry) throws CoreException {
		if (pluginEntry != null || nonPluginEntry != null) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			String text = "Content Consumer already opened: ";
			text += (pluginEntry == null) ? nonPluginEntry.getIdentifier() : pluginEntry.getIdentifier().toString();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, text, null);
			throw new CoreException(status);
		}

		this.pluginEntry = pluginEntry;
		return this;

	}

	/*
	 * @see IContentConsumer#store(ContentReference, IProgressMonitor)
	 */
	public void store(ContentReference contentReference, IProgressMonitor monitor) throws CoreException {

	try {
		if (nonPluginEntry != null) {
			feature.getSite().store(feature,contentReference.getIdentifier(),contentReference.getInputStream(),monitor);			
		} else if (pluginEntry != null) { 
			feature.store(pluginEntry, contentReference.getIdentifier(),contentReference.getInputStream(),monitor);
		} else { // feature
			feature.getSite().store(feature,contentReference.getIdentifier(),contentReference.getInputStream(),monitor);
		}
		} catch (IOException e){
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error obtaining inputStream from :" + contentReference.getIdentifier(), null);
			throw new CoreException(status);
		}

	}

	/*
	 * @see IContentConsumer#remove(ContentReference, IProgressMonitor)
	 */
	public void remove(ContentReference contentReference, IProgressMonitor monitor) throws CoreException {
		if (nonPluginEntry != null) {
			feature.getSite().remove(feature,monitor);			
		} else if (pluginEntry != null) {
			feature.remove(pluginEntry, monitor);
		} else { // feature
			feature.getSite().remove(feature,monitor);
		}
		
	}

	/*
	 * @see IContentConsumer#close()
	 */
	public void close() {
		if (nonPluginEntry != null)
			nonPluginEntry = null;
		if (pluginEntry != null)
			pluginEntry = null;
	}

	/*
	 * @see IContentConsumer#setFeature(IFeature)
	 */
	public void setFeature(IFeature feature) {
		this.feature = feature;
	}

}