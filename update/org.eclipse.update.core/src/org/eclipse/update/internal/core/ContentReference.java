package org.eclipse.update.internal.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
 
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.update.core.IContentReference;

/**
 * Content Reference default implementation
 */
	
public class ContentReference implements IContentReference {

	private URL url;
	private String identifier;

	/**
	 * Constructor
	 */
	public ContentReference(URL url,String id){
		this.url = url;
		this.identifier = id;
	}

	/*
	 * @see IContentReference#getIdentifier()
	 */
	public String getIdentifier() {
		return identifier;
	}

	/*
	 * @see IContentReference#getInputStream()
	 */
	public InputStream getInputStream()  throws IOException {
		return url.openStream();
	}

}
