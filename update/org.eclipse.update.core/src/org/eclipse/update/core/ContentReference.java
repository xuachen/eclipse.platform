package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.update.internal.core.UpdateManagerPlugin;

/**
 * Default content reference. 
 * Implements a simple URL or local File wrapper.
 * </p>
 * @since 2.0
 */
public class ContentReference {
	
	protected String id;
	protected URL url;	// reference is either URL reference *OR*
	protected File file;	//    local file reference
	
	public static final long UNKNOWN_SIZE = -1;

	/**
	 * Constructor for ContentRef.
	 */
	private ContentReference() {}

	/**
	 * Constructor for ContentRef.
	 */
	public ContentReference(String id, URL url) {
		this.id = id;
		this.url = url;
		this.file = null;
	}
	
	/**
	 * Constructor for ContentRef.
	 */
	public ContentReference(String id, File file) {
		this.id = id;
		this.file = file;
		this.url = null;
	}
	
	/**
	 * @since 2.0
	 */
	public String getIdentifier() {
		return id;
	}
	
	/**
	 * @since 2.0
	 */
	public InputStream getInputStream() throws IOException {
		if (file != null)
			return new FileInputStream(file);
		else
			return url.openStream();
	}	
	
	/**
	 * @since 2.0
	 */
	public long getInputSize() {
		return UNKNOWN_SIZE;
	}
	
	/**
	 * @since 2.0
	 */
	public boolean isLocalReference() {
		if (file != null)
			return true;
		else
			return url.getProtocol().equals("file");
	}	
		
	/**
	 * Returns a local file for the content reference.
	 * Returns <code>null</code> if content reference cannot
	 * be returned as a local file. Note, that this method
	 * <b>does not</b> cause the file to be downloaded if it
	 * is not already local.
	 * 
	 * @since 2.0
	 */
	public File asFile() {
		if (file != null)
			return file;
			
		if (url.getProtocol().equals("file"))
			return new File(url.getFile());
			
		return null;
	}
		
	/**
	 * Returns a URL for the content reference.
	 * Returns <code>null</code> if content reference cannot
	 * be returned as a URL.
	 * 
	 * @since 2.0
	 */
	public URL asURL() {
		if (url != null)
			return url;
			
		try {
			return new URL("file:"+file.getAbsolutePath());
		} catch(MalformedURLException e) {
			return null;
		}
	}
			
	/**
	 * @since 2.0
	 */
	public String toString() {
		if (file != null)
			return file.getAbsolutePath();
		else
			return url.toExternalForm();
	}
}
