/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.update.core.model;

import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;

/**
 * Plug-in dependency model object.
 * <p>
 * This class may be instantiated or subclassed by clients. However, in most 
 * cases clients should instead instantiate or subclass the provided 
 * concrete implementation of this model.
 * </p>
 * @see org.eclipse.update.core.Import
 * @since 2.0
 */
public class Import extends ModelObject implements IImport{

	private String id;
	private String version;
	private String matchingIdRuleName;
	private String matchingRuleName;
	private boolean featureImport;
	private boolean patch;
	private String osArch;
	private String ws;
	private String os;
	private String nl;


	//PERF: new instance variable
	private VersionedIdentifier versionId;

	/**
	 * Creates a uninitialized plug-in dependency model object.
	 * 
	 * @since 2.0
	 */
	public Import() {
		super();
	}
	
	/**
	 * Returns an identifier of the dependent plug-in.
	 * @see IImport#getVersionedIdentifier()
	 */
	public VersionedIdentifier getVersionedIdentifier() {
		if (versionId != null)
			return versionId;

		String id = getIdentifier();
		String ver = getVersion();
		if (id != null && ver != null) {
			try {
				versionId = new VersionedIdentifier(id, ver);
				return versionId;
			} catch (Exception e) {
				UpdateCore.warn("Unable to create versioned identifier:" + id + ":" + ver);
			}
		}

		
		versionId = new VersionedIdentifier("",null);
		return versionId;		
	}

	/**
	 * Returns the matching rule for the dependency.
	 * @see IImport#getRule()
	 */
	public int getRule() {
		return UpdateManagerUtils.getMatchingRule(getMatchingRuleName());
	}
	
	/**
	 * Returns the matching rule for the dependency identifier.
	 * @see IImport#getIdRule()
	 */
	public int getIdRule() {
		return UpdateManagerUtils.getMatchingIdRule(getMatchingIdRuleName());
	}
	
	/**
	 * 
	 * @see org.eclipse.update.core.IImport#getKind()
	 */

	/**
	 * Returns the dependency kind
	 * @see org.eclipse.update.core.IImport#getKind()
	 */
	public int getKind() {
		return isFeatureImport()?KIND_FEATURE:KIND_PLUGIN;
	}


	/**
	 * Returns the dependent plug-in identifier.
	 *
	 * @deprecated use getIdentifier() instead
	 * @return plug-in identifier, or <code>null</code>.
	 * @since 2.0
	 */
	public String getPluginIdentifier() {
		return id;
	}

	/**
	 * Returns the dependent identifier.
	 *
	 * @return  identifier, or <code>null</code>.
	 * @since 2.0.2
	 */
	public String getIdentifier() {
		return id;
	}

	/**
	 * Returns the dependent plug-in version.
	 *
	 * @deprecated use getVersion() instead
	 * @return plug-in version, or <code>null</code>.
	 * @since 2.0
	 */
	public String getPluginVersion() {
		return version;
	}

	/**
	 * Returns the dependent version.
	 *
	 * @return version, or <code>null</code>.
	 * @since 2.0.2
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Returns the dependent version matching rule name.
	 *
	 * @return matching rule name, or <code>null</code>.
	 * @since 2.0
	 */
	public String getMatchingRuleName() {
		return matchingRuleName;
	}
	
	/**
	 * Returns the dependent id matching rule name.
	 *
	 * @return matching rule name, or <code>null</code>.
	 * @since 2.1
	 */
	public String getMatchingIdRuleName() {
		return matchingIdRuleName;
	}

	/**
	 * Sets the dependent plug-in identifier.
	 * Throws a runtime exception if this object is marked read-only.
	 *
	 * @deprecated use setIdentifier()
	 * @param pluginId dependent plug-in identifier
	 * @since 2.0
	 */
	void setPluginIdentifier(String pluginId) {
		assertIsWriteable();
		this.id = pluginId;
	}

	/**
	 * Sets the dependent plug-in version.
	 * Throws a runtime exception if this object is marked read-only.
	 *
	 * @deprecated use setVersion()
	 * @param pluginVersion dependent plug-in version
	 * @since 2.0
	 */
	void setPluginVersion(String pluginVersion) {
		assertIsWriteable();
		this.version = pluginVersion;
	}

	/**
	 * Sets the dependent identifier.
	 * Throws a runtime exception if this object is marked read-only.
	 *
	 * @param id dependent identifier
	 * @since 2.0.2
	 */
	void setIdentifier(String id) {
		assertIsWriteable();
		this.id = id;
	}

	/**
	 * Sets the dependent version.
	 * Throws a runtime exception if this object is marked read-only.
	 *
	 * @param version dependent version
	 * @since 2.0.2
	 */
	void setVersion(String version) {
		assertIsWriteable();
		this.version = version;
	}
	
	/**
	 * Sets the dependent version matching rule name. 
	 * Throws a runtime exception if this object is marked read-only.
	 *
	 * @param matchingRuleName dependent version matching rule.
	 * @since 2.0
	 */
	void setMatchingRuleName(String matchingRuleName) {
		assertIsWriteable();
		this.matchingRuleName = matchingRuleName;
	}
	/**
	 * Sets the dependent id matching rule name. 
	 * Throws a runtime exception if this object is marked read-only.
	 *
	 * @param matchingIdRuleName dependent id matching rule.
	 * @since 2.1
	 */
	void setMatchingIdRuleName(String matchingIdRuleName) {
		assertIsWriteable();
		this.matchingIdRuleName = matchingIdRuleName;
	}
	/**
	 * Returns the isFeatureImport.
	 * @return boolean
	 */
	public boolean isFeatureImport() {
		return featureImport;
	}
	
	/**
	 * Sets the featureImport.
	 * @param featureImport The featureImport to set
	 */
	void setFeatureImport(boolean featureImport) {
		this.featureImport = featureImport;
	}
	
	/**
	 * Returns the patch mode.
	 */
	public boolean isPatch() {
		return patch;
	}
	
	/**
	 * Sets the patch mode.
	 */
	void setPatch(boolean patch) {
		this.patch = patch;
	}
	/**
	 * Returns the os.
	 * @return String
	 */
	public String getOS() {
		return os;
	}

	/**
	 * Returns the osArch.
	 * @return String
	 */
	public String getOSArch() {
		return osArch;
	}

	/**
	 * Returns the ws.
	 * @return String
	 */
	public String getWS() {
		return ws;
	}

	/**
	 * Sets the os.
	 * @param os The os to set
	 */
	void setOS(String os) {
		this.os = os;
	}

	/**
	 * Sets the osArch.
	 * @param osArch The osArch to set
	 */
	void setOSArch(String osArch) {
		this.osArch = osArch;
	}

	/**
	 * Sets the ws.
	 * @param ws The ws to set
	 */
	void setWS(String ws) {
		this.ws = ws;
	}

	/**
	 * Returns the nl.
	 * @return String
	 */
	public String getNL() {
		return nl;
	}

	/**
	 * Sets the nl.
	 * @param nl The nl to set
	 */
	void setNL(String nl) {
		this.nl = nl;
	}

}
