package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A default implementation for IFeatureContentConsumer
 * </p>
 * @since 2.0
 */

public abstract class SiteContentConsumer implements ISiteContentConsumer {
	
	protected ISite site;
	
	/*
	 * @see ISiteContentConsumer#setSite(ISite)
	 */
	public void setSite(ISite site) {
		this.site = site;
	}	

}
