package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;

/**
 * An example feature content provider. It handles features packaged as
 * build zip's using the format used for integration and stable builds
 * posted on the downloads pages at www.eclipse.org
 * </p>
 * @since 2.0
 */

public abstract class FeatureContentProvider implements IFeatureContentProvider {
	
	protected URL base;
	protected IFeature feature;
		
	// local file map in temporary area
	protected static Map entryMap;
	protected static File tmpDir;
	
	// buffer pool
	protected static Stack bufferPool;	
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * @since 2.0
	 */
	public FeatureContentProvider(URL base) {
		this.base = base;
		this.feature = null;
	}

	/*
	 * @see IFeatureContentProvider#getFeatureManifest()
	 */
	public abstract ContentReference getFeatureManifest()
		throws CoreException;

	/*
	 * @see IFeatureContentProvider#getArchiveReferences()
	 */
	public abstract ContentReference[] getArchiveReferences()
		throws CoreException;

	/*
	 * @see IFeatureContentProvider#getFeatureEntryArchiveReferences()
	 */
	public abstract ContentReference[] getFeatureEntryArchiveReferences()
		throws CoreException;

	/*
	 * @see IFeatureContentProvider#getPluginEntryArchiveReferences(IPluginEntry)
	 */
	public abstract ContentReference[] getPluginEntryArchiveReferences(IPluginEntry pluginEntry)
		throws CoreException;

	/*
	 * @see IFeatureContentProvider#getNonPluginEntryArchiveReferences(INonPluginEntry)
	 */
	public abstract ContentReference[] getNonPluginEntryArchiveReferences(INonPluginEntry nonPluginEntry)
		throws CoreException;

	/*
	 * @see IFeatureContentProvider#getFeatureEntryContentReferences()
	 */
	public abstract ContentReference[] getFeatureEntryContentReferences()
		throws CoreException;

	/*
	 * @see IFeatureContentProvider#getPluginEntryContentReferences(IPluginEntry)
	 */
	public abstract ContentReference[] getPluginEntryContentReferences(IPluginEntry pluginEntry)
		throws CoreException;
		
	/*
	 * @see IFeatureContentProvider#setFeature(IFeature)
	 */
	public void setFeature(IFeature feature) {
		this.feature = feature;
	}
	
	/**
	 * Returns the specified reference as a local file system reference.
	 * If required, the file represented by the specified content
	 * reference is first downloaded to the local system
	 * 
	 * @since 2.0
	 */
	public ContentReference asLocalReference(ContentReference ref, BaseFeature.ProgressMonitor monitor) throws IOException {
		
		// check to see if this is already a local reference
		if (ref.isLocalReference())
			return ref;
		
		// check to see if we already have a local file for this reference
		String key = toString();
		File localFile = lookupLocalFile(key);
		if (localFile != null)
			return new ContentReference(ref.getIdentifier(), localFile);
			
		// download the referenced file into local temporary area
		localFile = createLocalFile(key);
		InputStream is = null;
		OutputStream os = null;
		try {
			is = ref.getInputStream();
			os = new FileOutputStream(localFile);
			copy(is, os, monitor);
		} catch(IOException e) {
			removeLocalFile(key);
		} finally {
			if (is != null) try { is.close(); } catch(IOException e) {}
			if (os != null) try { os.close(); } catch(IOException e) {}
		}
		return new ContentReference(ref.getIdentifier(), localFile);
	}
		
	/**
	 * Returns the specified reference as a local file.
	 * If required, the file represented by the specified content
	 * reference is first downloaded to the local system
	 * 
	 * @since 2.0
	 */
	public File asLocalFile(ContentReference ref, BaseFeature.ProgressMonitor monitor) throws IOException {
		File file = ref.asFile();
		if (file != null)
			return file;
		
		ContentReference localRef = asLocalReference(ref, monitor);
		file = asLocalFile(localRef, monitor);
		return file;
	}
	
	/**
	 * Returns local file (in temporary area) matching the
	 * specified key. Returns null if the entry does not exist.
	 * 
	 * @since 2.0
	 */	
	protected synchronized File lookupLocalFile(String key) {
		if (entryMap == null)
			return null;
		return (File) entryMap.get(key);
	}
	
	/**
	 * Create a local file (in temporary area) matching the
	 * specified key.
	 * 
	 * @since 2.0
	 */	
	protected synchronized File createLocalFile(String key) throws IOException {
		
		// ensure we have a temp directory
		if (tmpDir == null) {		
			String tmpName = System.getProperty("java.io.tmpdir");
			tmpName += "eclipse" + File.separator + ".update" + File.separator;
			tmpDir = new File(tmpName);
			tmpDir.mkdirs();
			if (!tmpDir.exists())
				throw new FileNotFoundException(tmpName);
		}
		
		// get a temp file and create a map for it
		File temp = File.createTempFile("eclipse",null,tmpDir);
		temp.deleteOnExit();
		if (entryMap == null)
			entryMap = new HashMap();
		entryMap.put(key,temp);
		return temp;
	}
	
	/**
	 * Removes the specified key from the local file map. The file is
	 * not actually deleted until VM termination.
	 * 
	 * @since 2.0
	 */	
	protected synchronized void removeLocalFile(String key) {
		if (entryMap != null)
			entryMap.remove(key);
	}
	
	/**
	 * Copies specified input stream to the output stream.
	 * 
	 * @since 2.0
	 */	
	protected void copy(InputStream is, OutputStream os, BaseFeature.ProgressMonitor monitor) throws IOException {
		// TBA: progress monitor support
		// TBA: indication of download size (xK of yK)
		byte[] buf = getBuffer();
		try {
			long totalLen = 0; // TBA - to contain total expected length
			long currentLen = 0;
			int len = is.read(buf);
			while(len != -1) {
				currentLen += len;
				os.write(buf,0,len);
				len = is.read(buf);
			}
		} finally {
			freeBuffer(buf);
		}
	}
	
	/**
	 * @since 2.0
	 */
	protected void unpack() {
		// TBA ... support for handling .zip and .jar
	}
	
	/**
	 * @since 2.0
	 */
	protected synchronized byte[] getBuffer() {
		if (bufferPool == null) {
			return new byte[BUFFER_SIZE];
		}
		
		try {
			return (byte[]) bufferPool.pop();
		} catch (EmptyStackException e) {
			return new byte[BUFFER_SIZE];
		}
	}
		
	/**
	 * @since 2.0
	 */
	protected synchronized void freeBuffer(byte[] buf) {
		if (bufferPool == null)
			bufferPool = new Stack();
		bufferPool.push(buf);
	}
}
