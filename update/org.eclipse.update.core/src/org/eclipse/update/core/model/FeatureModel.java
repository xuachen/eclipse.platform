package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */ 
 
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An object which represents the feature in the
 * packaging manifest.
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 * @since 2.0
 */

public class FeatureModel extends ModelObject {
	
	private String featureId;
	private String featureVersion;
	private String label;
	private String provider;
	private String imageURL;
	private String os;
	private String ws;
	private String nl;
	private String application;
	private InstallHandlerModel installHandler;
	private URLEntryModel description;
	private URLEntryModel copyright;
	private URLEntryModel license;
	private URLEntryModel updateSiteInfo;
	private List /*of InfoModel*/ discoverySiteInfo;
	private List /*of ImportModel*/ imports;
	private List /*of PluginEntryModel*/ pluginEntries;
	private List /*of NonPluginEntryModel*/ nonPluginEntries;
	private List /*of ContentGroupModel*/ groupEntries;

	/**
	 * Creates an uninitialized model object.
	 * 
	 * @since 2.0
	 */	
	public FeatureModel() {
		super();
	}

	/**
	 * @since 2.0
	 */	
	public String getFeatureIdentifier() {
		return featureId;
	}

	/**
	 * @since 2.0
	 */	
	public String getFeatureVersion() {
		return featureVersion;
	}

	/**
	 * @since 2.0
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @since 2.0
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * @since 2.0
	 */
	public String getImageURLString() {
		return imageURL;
	}

	/**
	 * @since 2.0
	 */
	public String getOS() {
		return os;
	}

	/**
	 * @since 2.0
	 */
	public String getWS() {
		return ws;
	}

	/**
	 * @since 2.0
	 */
	public String getNL() {
		return nl;
	}

	/**
	 * @since 2.0
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @since 2.0
	 */	
	public InstallHandlerModel getInstallHandler() {
		return installHandler;
	}

	/**
	 * @since 2.0
	 */
	public URLEntryModel getDescription() {
		return description;
	}

	/**
	 * @since 2.0
	 */	
	public URLEntryModel getCopyright() {
		return copyright;
	}

	/**
	 * @since 2.0
	 */
	public URLEntryModel getLicense() {
		return license;
	}

	/**
	 * @since 2.0
	 */
	public URLEntryModel getUpdateSiteInfo() {
		return updateSiteInfo;
	}

	/**
	 * @since 2.0
	 */
	public URLEntryModel[] getDiscoverySiteInfo() {
		if (discoverySiteInfo == null)
			return new URLEntryModel[0];
			
		return (URLEntryModel[]) discoverySiteInfo.toArray(new URLEntryModel[0]);
	}

	/**
	 * @since 2.0
	 */
	public ImportModel[] getImports() {
		if (imports == null)
			return new ImportModel[0];
			
		return (ImportModel[]) imports.toArray(new ImportModel[0]);
	}

	/**
	 * @since 2.0
	 */
	public PluginEntryModel[] getPluginEntries() {
		if (pluginEntries == null)
			return new PluginEntryModel[0];
			
		return (PluginEntryModel[]) pluginEntries.toArray(new PluginEntryModel[0]);
	}

	/**
	 * @since 2.0
	 */
	public NonPluginEntryModel[] getNonPluginEntries() {
		if (nonPluginEntries == null)
			return new NonPluginEntryModel[0];
			
		return (NonPluginEntryModel[]) nonPluginEntries.toArray(new NonPluginEntryModel[0]);
	}

	/**
	 * @since 2.0
	 */	
	public ContentGroupModel[] getGroupEntries() {
		if (groupEntries == null)
			return new ContentGroupModel[0];
			
		return (ContentGroupModel[]) groupEntries.toArray(new ContentGroupModel[0]);
	}	

	/**
	 * @since 2.0
	 */	
	public void setFeatureIdentifier(String featureId) {
		assertIsWriteable();
		this.featureId = featureId;
	}

	/**
	 * @since 2.0
	 */	
	public void setFeatureVersion(String featureVersion) {
		assertIsWriteable();
		this.featureVersion = featureVersion;
	}

	/**
	 * @since 2.0
	 */
	public void setLabel(String label) {
		assertIsWriteable();
		this.label = label;
	}

	/**
	 * @since 2.0
	 */
	public void setProvider(String provider) {
		assertIsWriteable();
		this.provider = provider;
	}

	/**
	 * @since 2.0
	 */
	public void setImageURLString(String imageURL) {
		assertIsWriteable();
		this.imageURL = imageURL;
	}

	/**
	 * @since 2.0
	 */
	public void setOS(String os) {
		assertIsWriteable();
		this.os = os;
	}

	/**
	 * @since 2.0
	 */
	public void setWS(String ws) {
		assertIsWriteable();
		this.ws = ws;
	}

	/**
	 * @since 2.0
	 */
	public void setNL(String nl) {
		assertIsWriteable();
		this.nl = nl;
	}

	/**
	 * @since 2.0
	 */
	public void setApplication(String application) {
		assertIsWriteable();
		this.application = application;
	}

	/**
	 * @since 2.0
	 */	
	public void setInstallHandler(InstallHandlerModel installHandler) {
		assertIsWriteable();
		this.installHandler = installHandler;
	}

	/**
	 * @since 2.0
	 */
	public void setDescription(URLEntryModel description) {
		assertIsWriteable();
		this.description = description;
	}

	/**
	 * @since 2.0
	 */	
	public void setCopyright(URLEntryModel copyright) {
		assertIsWriteable();
		this.copyright = copyright;
	}

	/**
	 * @since 2.0
	 */
	public void setLicense(URLEntryModel license) {
		assertIsWriteable();
		this.license = license;
	}

	/**
	 * @since 2.0
	 */
	public void setUpdateSiteInfo(URLEntryModel updateSiteInfo) {
		assertIsWriteable();
		this.updateSiteInfo = updateSiteInfo;
	}

	/**
	 * @since 2.0
	 */
	public void setDiscoverySiteInfo(URLEntryModel[] discoverySiteInfo) {
		assertIsWriteable();
		if (discoverySiteInfo == null)
			this.discoverySiteInfo = null;
		else
			this.discoverySiteInfo = Arrays.asList(discoverySiteInfo);
	}

	/**
	 * @since 2.0
	 */
	public void setImports(ImportModel[] imports) {
		assertIsWriteable();
		if (imports == null)
			this.imports = null;
		else
			this.imports = Arrays.asList(imports);
	}

	/**
	 * @since 2.0
	 */
	public void setPluginEntries(PluginEntryModel[] pluginEntries) {
		assertIsWriteable();
		if (pluginEntries == null)
			this.pluginEntries = null;
		else
			this.pluginEntries = Arrays.asList(pluginEntries);
	}

	/**
	 * @since 2.0
	 */
	public void setNonPluginEntries(NonPluginEntryModel[] nonPluginEntries) {
		assertIsWriteable();
		if (nonPluginEntries == null)
			this.nonPluginEntries = null;
		else
			this.nonPluginEntries = Arrays.asList(nonPluginEntries);
	}

	/**
	 * @since 2.0
	 */	
	public void setGroupEntries(ContentGroupModel[] groupEntries) {
		assertIsWriteable();
		if (groupEntries == null)
			this.groupEntries = null;
		else
			this.groupEntries = Arrays.asList(groupEntries);
	}

	/**
	 * @since 2.0
	 */
	public void addDiscoverySiteInfo(URLEntryModel discoverySiteInfo) {
		assertIsWriteable();
		if (discoverySiteInfo == null)
			this.discoverySiteInfo = new ArrayList();
		if (!this.discoverySiteInfo.contains(discoverySiteInfo))
			this.discoverySiteInfo.add(discoverySiteInfo);
	}

	/**
	 * @since 2.0
	 */
	public void addImport(ImportModel importEntry) {
		assertIsWriteable();
		if (imports == null)
			this.imports = new ArrayList();
		if (!this.imports.contains(importEntry))
			this.imports.add(importEntry);
	}

	/**
	 * @since 2.0
	 */
	public void addPluginEntry(PluginEntryModel pluginEntry) {
		assertIsWriteable();
		if (pluginEntries == null)
			this.pluginEntries = new ArrayList();
		if (!this.pluginEntries.contains(pluginEntry))
			this.pluginEntries.add(pluginEntry);
	}

	/**
	 * @since 2.0
	 */
	public void addNonPluginEntry(NonPluginEntryModel nonPluginEntry) {
		assertIsWriteable();
		if (nonPluginEntries == null)
			this.nonPluginEntries = new ArrayList();
		if (!this.nonPluginEntries.contains(nonPluginEntry))
			this.nonPluginEntries.add(nonPluginEntry);
	}

	/**
	 * @since 2.0
	 */	
	public void addGroupEntry(ContentGroupModel groupEntry) {
		assertIsWriteable();
		if (groupEntries == null)
			this.groupEntries = new ArrayList();
		if (!this.groupEntries.contains(groupEntry))
			this.groupEntries.add(groupEntry);
	}

	/**
	 * @since 2.0
	 */
	public void removeDiscoverySiteInfo(URLEntryModel discoverySiteInfo) {
		assertIsWriteable();
		if (discoverySiteInfo != null)
			this.discoverySiteInfo.remove(discoverySiteInfo);
	}

	/**
	 * @since 2.0
	 */
	public void removeImport(ImportModel importEntry) {
		assertIsWriteable();
		if (imports != null)
			this.imports.remove(importEntry);
	}

	/**
	 * @since 2.0
	 */
	public void removePluginEntry(PluginEntryModel pluginEntry) {
		assertIsWriteable();
		if (pluginEntries != null)
			this.pluginEntries.remove(pluginEntry);
	}

	/**
	 * @since 2.0
	 */
	public void removeNonPluginEntry(NonPluginEntryModel nonPluginEntry) {
		assertIsWriteable();
		if (nonPluginEntries != null)
			this.nonPluginEntries.remove(nonPluginEntry);
	}

	/**
	 * @since 2.0
	 */
	public void removeGroupEntry(ContentGroupModel groupEntry) {
		assertIsWriteable();
		if (groupEntries != null)
			this.groupEntries.remove(groupEntry);
	}
	
	/**
	 * 
	 */
	public void markReadOnly() {		
		markReferenceReadOnly(getDescription());
		markReferenceReadOnly(getCopyright());
		markReferenceReadOnly(getLicense());
		markReferenceReadOnly(getUpdateSiteInfo());
		markListReferenceReadOnly(getDiscoverySiteInfo());
		markListReferenceReadOnly(getImports());
		markListReferenceReadOnly(getPluginEntries());
		markListReferenceReadOnly(getNonPluginEntries());
		markListReferenceReadOnly(getGroupEntries());
	}
}
