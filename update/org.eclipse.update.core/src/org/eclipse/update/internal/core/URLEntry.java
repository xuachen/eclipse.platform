package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.net.URL;
import org.eclipse.update.core.IURLEntry;
import org.eclipse.update.core.model.URLEntryModel;

/**
 * Default implementation of IURLEntry
 */

public class URLEntry extends URLEntryModel implements IURLEntry{

	private URL url;

	/**
	 * Constructor for URLEntry
	 */
	public URLEntry() {
		super();
	}
	
	/**
	 * Constructor for URLEntry
	 */
	public URLEntry(URL url) {
		super();
		setURL(url);
	}
	
	/**
	 * Constructor for URLEntry
	 */
	public URLEntry(String text) {
		super();
		setAnnotation(text);
	}
	
	/**
	 * Constructor for URLEntry
	 */
	public URLEntry(String text,URL url) {
		super();
		setAnnotation(text);
		setURL(url);
	}
	
	

	/**
	 * @see IURLEntry#getURL()
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Sets the url
	 * @param url The url to set
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/*
	 * @see Object#toString()
	 */
	public String toString() {
		String result = "IURLEntry: ";
		 result = result +( (getAnnotation()==null)?url.toExternalForm():getAnnotation() + " : "+url.toExternalForm());
		return result;
	}

}

