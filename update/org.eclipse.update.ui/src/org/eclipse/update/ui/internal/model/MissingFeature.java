package org.eclipse.update.ui.internal.model;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.update.core.*;
import org.eclipse.update.core.*;

public class MissingFeature implements IFeature {
	private URL url;
	private ISite site;
	private VersionedIdentifier id = new VersionedIdentifier("unknown", "0.0.0");
	public MissingFeature(ISite site, URL url) {
		this.url = url;
	}

	/*
	 * @see IFeature#getIdentifier()
	 */
	public VersionedIdentifier getIdentifier() {
		return id;
	}

	/*
	 * @see IFeature#getSite()
	 */
	public ISite getSite() {
		return site;
	}

	/*
	 * @see IFeature#getLabel()
	 */
	public String getLabel() {
		return url.toString();
	}

	/*
	 * @see IFeature#getURL()
	 */
	public URL getURL() {
		return url;
	}

	/*
	 * @see IFeature#getUpdateInfo()
	 */
	public IURLEntry getUpdateInfo() {
		return null;
	}

	/*
	 * @see IFeature#getDiscoveryInfos()
	 */
	public IURLEntry[] getDiscoveryInfos() {
		return null;
	}

	/*
	 * @see IFeature#getProvider()
	 */
	public String getProvider() {
		return "Unknown";
	}

	/*
	 * @see IFeature#getDescription()
	 */
	public IURLEntry getDescription() {
		return null;
	}

	/*
	 * @see IFeature#getCopyright()
	 */
	public IURLEntry getCopyright() {
		return null;
	}

	/*
	 * @see IFeature#getLicense()
	 */
	public IURLEntry getLicense() {
		return null;
	}

	/*
	 * @see IFeature#getOS()
	 */
	public String getOS() {
		return null;
	}

	/*
	 * @see IFeature#getWS()
	 */
	public String getWS() {
		return null;
	}

	/*
	 * @see IFeature#getNL()
	 */
	public String getNL() {
		return null;
	}

	/*
	 * @see IFeature#getImage()
	 */
	public URL getImage() {
		return null;
	}

	/*
	 * @see IFeature#getImports()
	 */
	public IImport[] getImports() {
		return null;
	}

	/*
	 * @see IFeature#isExecutable()
	 */
	public boolean isExecutable() {
		return false;
	}

	/*
	 * @see IFeature#isInstallable()
	 */
	public boolean isInstallable() {
		return false;
	}

	/*
	 * @see IFeature#getArchives()
	 */
	public String[] getArchives() {
		return null;
	}

	/*
	 * @see IFeature#getNonPluginEntries()
	 */
	public INonPluginEntry[] getNonPluginEntries() {
		return null;
	}

	/*
	 * @see IFeature#addNonPluginEntry(INonPluginEntry)
	 */
	public void addNonPluginEntry(INonPluginEntry dataEntry) {
	}

	/*
	 * @see IFeature#getDownloadSize(ISite)
	 */
	public long getDownloadSize(ISite site) throws CoreException {
		return 0;
	}

	/*
	 * @see IFeature#getInstallSize(ISite)
	 */
	public long getInstallSize(ISite site) throws CoreException {
		return 0;
	}

	/*
	 * @see IFeature#getApplication()
	 */
	public String getApplication() {
		return null;
	}

	/*
	 * @see IPluginContainer#getPluginEntries()
	 */
	public IPluginEntry[] getPluginEntries() {
		return null;
	}

	/*
	 * @see IPluginContainer#getPluginEntryCount()
	 */
	public int getPluginEntryCount() {
		return 0;
	}

	/*
	 * @see IPluginContainer#getDownloadSize(IPluginEntry)
	 */
	public long getDownloadSize(IPluginEntry entry) {
		return 0;
	}

	/*
	 * @see IPluginContainer#getInstallSize(IPluginEntry)
	 */
	public long getInstallSize(IPluginEntry entry) {
		return 0;
	}

	/*
	 * @see IPluginContainer#addPluginEntry(IPluginEntry)
	 */
	public void addPluginEntry(IPluginEntry pluginEntry) {
	}

	/*
	 * @see IPluginContainer#store(IPluginEntry, String, InputStream)
	 */
	public void store(IPluginEntry entry, String name, InputStream inStream)
		throws CoreException {
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

	/*
	 * @see IPluginContainer#remove(IPluginEntry)
	 */
	public void remove(IPluginEntry entry) throws CoreException {
	}

	/*
	 * @see IFeature#getVersionIdentifier()
	 */
	public VersionedIdentifier getVersionIdentifier() {
		return null;
	}

	/*
	 * @see IFeature#setSite(ISite)
	 */
	public void setSite(ISite site) throws CoreException {
	}

	/*
	 * @see IFeature#getUpdateSiteEntry()
	 */
	public IURLEntry getUpdateSiteEntry() {
		return null;
	}

	/*
	 * @see IFeature#getDiscoverySiteEntries()
	 */
	public IURLEntry[] getDiscoverySiteEntries() {
		return null;
	}

	/*
	 * @see IFeature#install(IFeature, IProgressMonitor)
	 */
	public void install(IFeature targetFeature, IProgressMonitor monitor) throws CoreException {
	}

	/*
	 * @see IFeature#remove(IProgressMonitor)
	 */
	public void remove(IProgressMonitor monitor) throws CoreException {
	}

	/*
	 * @see IFeature#setFeatureContentProvider(IFeatureContentProvider)
	 */
	public void setFeatureContentProvider(IFeatureContentProvider featureContentProvider) {
	}

	/*
	 * @see IFeature#getFeatureContentProvider()
	 */
	public IFeatureContentProvider getFeatureContentProvider() throws CoreException {
		return null;
	}

	/*
	 * @see IFeature#setContentConsumer(IFeatureContentConsumer)
	 */
	public void setContentConsumer(IFeatureContentConsumer contentConsumer) {
	}

	/*
	 * @see IFeature#getContentConsumer()
	 */
	public IFeatureContentConsumer getContentConsumer() throws CoreException {
		return null;
	}

	/*
	 * @see IPluginContainer#store(IPluginEntry, String, InputStream, IProgressMonitor)
	 */
	public void store(IPluginEntry entry, String name, InputStream inStream, IProgressMonitor monitor) throws CoreException {
	}

}

