package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
 
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
 
 /**
  *A ContentReference is a handle on a resource inside a Feature or a Site
  * 
  */
 
public interface IContentReference {

	/**
	 * Returns the identifier of the ContentReference
	 * @return teh identifier
	 * @since 2.0 
	 */

	String getIdentifier();

	/**
	 * Returns the InputStream of this resource
	 * @return the InputStream of teh resource
	 * @since 2.0 
	 */

	InputStream getInputStream() throws IOException;
	
}


