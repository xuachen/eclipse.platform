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
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Local jar entry content reference. 
 * </p>
 * @since 2.0
 * 
 */

public class JarContentReference extends ContentReference {
	
	protected String entryName;
	
	/**
	 * Constructor for ContentRef.
	 */
	public JarContentReference(String id, File file, String entryName) {
		super(id, file);
		this.entryName = entryName;
	}

	/*
	 * @see ContentReference#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {

		JarFile jarArchive = new JarFile(file);
		ZipEntry entry = jarArchive.getEntry(entryName);
		if (entry == null)
			throw new FileNotFoundException(file.getAbsolutePath()+" "+entryName);
		return jarArchive.getInputStream(entry);
	}

	/*
	 * @see ContentReference#getInputSize()
	 */
	public long getInputSize() {
		return ContentReference.UNKNOWN_SIZE;
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
			return new URL("jar:file:"+fileName+"!/"+entryName);
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
