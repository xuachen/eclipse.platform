package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Local jar entry content reference. 
 * </p>
 * @since 2.0
 * 
 */

public class JarContentReference extends ContentReference {
	
	protected JarFile jarFile;
	protected JarEntry entry;
	
	/**
	 * Constructor for ContentRef.
	 * 
	 * NOTE: java.util.jar/zip do not allow the File object to be
	 * 		extracted from JarFile/ ZipFile. So both the JarFile
	 * 		and its corresponding File need to be passed into
	 * 		the costructor in order for the reference to function
	 * 		correctly.
	 */
	public JarContentReference(String id, JarFile jarFile, JarEntry entry, File file) {
		super(id, file);
		this.jarFile = jarFile;
		this.entry = entry;
	}

	/*
	 * @see ContentReference#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {		
		return jarFile.getInputStream(entry);
	}

	/*
	 * @see ContentReference#getInputSize()
	 */
	public long getInputSize() {
		return entry.getSize();
	}

	/*
	 * @see ContentReference#asFile()
	 */
	public File asFile() {
		return null;
	}

	/*
	 * @see ContentReference#asURL()
	 */
	public URL asURL() {
		try {
			String fileName = file.getAbsolutePath().replace(File.separatorChar,'/');
			return new URL("jar:file:"+fileName+"!/"+entry.getName());
		} catch(MalformedURLException e) {
			return null;
		}
	}

	/*
	 * @see Object#toString()
	 */
	public String toString() {
		URL url = asURL();
		if (url != null)
			return url.toExternalForm();
		else
			return getClass().getName()+"@"+hashCode();
	}
}
