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

import org.eclipse.core.runtime.*;
import org.eclipse.update.configuration.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;

/**
 * Included Feature reference model object.
 * <p>
 * This class may be instantiated or subclassed by clients. However, in most 
 * cases clients should instead instantiate or subclass the provided 
 * concrete implementation of this model.
 * </p>
 * @see org.eclipse.update.core.IncludedFeatureReference
 * @since 2.1
 */
public class IncludedFeatureReference extends FeatureReference implements IIncludedFeatureReference {

	private IFeature bestMatchFeature;
	
	// since 2.0.2
	private boolean isOptional;
	private int matchingRule;
	private int searchLocation;
	
	// since 2.1
	private String os;
	private String ws;
	private String arch;
	private String nl;
	
	/**
	 * Construct a included feature reference
	 * 
	 * @since 2.1
	 */
	public IncludedFeatureReference() {
		super();
		isOptional(false);
		setMatchingRule(IImport.RULE_PERFECT);
		setSearchLocation(IUpdateConstants.SEARCH_ROOT);
	}
	
	
	
	/**
	 * Construct a included feature reference model
	 * 
	 * @param includedFeatureRef the included reference model to copy
	 * @since 2.1
	 */
	public IncludedFeatureReference(IncludedFeatureReference includedFeatureRef) {
		super((FeatureReference)includedFeatureRef);
		isOptional(includedFeatureRef.isOptional());
		setLabel(includedFeatureRef.getLabel());
		setMatchingRule(includedFeatureRef.getMatch());
		setSearchLocation(includedFeatureRef.getSearchLocation());
		setArch(includedFeatureRef.getOSArch());
		setWS(includedFeatureRef.getWS());
		setOS(includedFeatureRef.getOS());
	}

	/**
	 * Constructor IncludedFeatureReferenceModel.
	 * @param featureReference
	 */
	public IncludedFeatureReference(IFeatureReference featureReference) {
		super((FeatureReference)featureReference);
		isOptional(false);
		setMatchingRule(IImport.RULE_PERFECT);
		setSearchLocation(IUpdateConstants.SEARCH_ROOT);
		setLabel(getLabel());		
	}

		
	/**
	 * Returns the matching rule for this included feature.
	 * The rule will determine the ability of the included feature to move version 
	 * without causing the overall feature to appear broken.
	 * 
	 * The default is <code>MATCH_PERFECT</code>
	 * 
	 * @see IImport#RULE_PERFECT
	 * @see IImport#RULE_EQUIVALENT
	 * @see IImport#RULE_COMPATIBLE
	 * @see IImport#RULE_GREATER_OR_EQUAL
	 * @return int representation of feature matching rule.
	 * @since 2.0.2
	 */
	public int getMatch(){
		return matchingRule;
	}

	/**
	 * Returns the search location for this included feature.
	 * The location will be used to search updates for this feature.
	 * 
	 * The default is <code>SEARCH_ROOT</code>
	 * 
	 * @see IUpdateConstants#SEARCH_ROOT
	 * @see IUpdateConstants#SEARCH_SELF
	 * @return int representation of feature searching rule.
	 * @since 2.0.2
	 */

	public int getSearchLocation(){
		return searchLocation;
	}
	


	/**
	 * Returns the isOptional
	 * 
	 * @return isOptional
	 * @since 2.0.1
	 */
	public boolean isOptional() {
		return isOptional;
	}


	

	/**
	 * Sets the isOptional.
	 * @param isOptional The isOptional to set
	 */
	public void isOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	/**
	 * Sets the matchingRule.
	 * @param matchingRule The matchingRule to set
	 */
	void setMatchingRule(int matchingRule) {
		this.matchingRule = matchingRule;
	}

	/**
	 * Sets the searchLocation.
	 * @param searchLocation The searchLocation to set
	 */
	void setSearchLocation(int searchLocation) {
		this.searchLocation = searchLocation;
	}

	/**
	 * Returns the arch.
	 * @return String
	 */
	public String getOSArch() {
		return arch;
	}

	/**
	 * Returns the os.
	 * @return String
	 */
	public String getOS() {
		return os;
	}

	/**
	 * Returns the ws.
	 * @return String
	 */
	public String getWS() {
		return ws;
	}

	/**
	 * Sets the arch.
	 * @param arch The arch to set
	 */
	void setArch(String arch) {
		this.arch = arch;
	}

	/**
	 * Sets the os.
	 * @param os The os to set
	 */
	void setOS(String os) {
		this.os = os;
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

	/**
	* Method matches.
	* @param identifier
	* @param id
	* @param options
	* @return boolean
	*/
	private boolean matches(
		VersionedIdentifier baseIdentifier,
		VersionedIdentifier id) {
		if (baseIdentifier == null || id == null)
			return false;
		if (!id.getIdentifier().equals(baseIdentifier.getIdentifier()))
			return false;

		switch (getMatch()) {
			case IImport.RULE_PERFECT :
				return id.getVersion().isPerfect(baseIdentifier.getVersion());
			case IImport.RULE_COMPATIBLE :
				return id.getVersion().isCompatibleWith(
					baseIdentifier.getVersion());
			case IImport.RULE_EQUIVALENT :
				return id.getVersion().isEquivalentTo(
					baseIdentifier.getVersion());
			case IImport.RULE_GREATER_OR_EQUAL :
				return id.getVersion().isGreaterOrEqualTo(
					baseIdentifier.getVersion());
		}
		UpdateCore.warn("Unknown matching rule:" + getMatch());
		return false;
	}
	
	/*
	 * Method isDisabled.
	 * @return boolean
	 */
	private boolean isDisabled() {
		if (!(getSite() instanceof IInstalledSite) )
			return false; // we could throw exception...
				
		IConfiguredSite cSite = ((IInstalledSite)getSite()).getCurrentConfiguredSite();
		if (cSite == null)
			return false;
		IFeatureReference[] configured = cSite.getConfiguredFeatures();
		for (int i = 0; i < configured.length; i++) {
			if (this.equals(configured[i]))
				return false;
		}
		return true;
		//		// FIXME: the above code was commented out and returned false. 
		//		// Should this be commented out again?
		//		return false;
	}

	/*
	 * Method isInstalled.
	 * @return boolean
	 */
	private boolean isUninstalled() {
		if (!isDisabled())
			return false;
		IFeatureReference[] installed = getSite().getFeatureReferences();
		for (int i = 0; i < installed.length; i++) {
			if (this.equals(installed[i]))
				return false;
		}
		// if we reached this point, the configured site exists and it does not
		// contain this feature reference, so clearly the feature is uninstalled
		return true;
	}

	/**
	 * @see org.eclipse.update.core.IIncludedFeatureReference#getFeature(boolean,
	 * IConfiguredSite)
	 * @deprecated
	 */
	public IFeature getFeature(
		boolean perfectMatch,
		IConfiguredSite configuredSite)
		throws CoreException {
		return getFeature(perfectMatch, configuredSite, null);
	}

	/**
	 * @see org.eclipse.update.core.IIncludedFeatureReference#getFeature(boolean,
	 * IConfiguredSite,IProgressMonitor)
	 */
	public IFeature getFeature(
		boolean perfectMatch,
		IConfiguredSite configuredSite,
		IProgressMonitor monitor)
		throws CoreException {
		// if perfect match is asked or if the feature is disabled
		// we return the exact match 		
		if (perfectMatch
			|| getMatch() == IImport.RULE_PERFECT
			|| isDisabled()) {
			//
			if (isUninstalled())
				throw new CoreException(
					new Status(
						IStatus.ERROR,
						UpdateCore
							.getPlugin()
							.getDescriptor()
							.getUniqueIdentifier(),
						IStatus.OK,
						Policy.bind("IncludedFeatureReference.featureUninstalled", getFeatureIdentifier()),
						null));
			else
				return getSite().getFeature(this,monitor);
		} else {
			if (bestMatchFeature == null) {
				// find best match
				if (configuredSite == null && getSite() instanceof IInstalledSite)
					configuredSite = ((IInstalledSite)getSite()).getCurrentConfiguredSite();
				IFeatureReference bestMatchReference =
					getBestMatch(configuredSite);
				IFeature localBestMatchFeature =
					getSite().getFeature(bestMatchReference, monitor);
				// during reconciliation, we may not have the currentConfiguredSite yet
				// do not preserve the best match
				if (configuredSite == null)
					return localBestMatchFeature;
				else
					bestMatchFeature = localBestMatchFeature;
			}
			return bestMatchFeature;
		}
	}

	/*
	 * Method getBestMatch.
	 * @param enabledFeatures
	 * @param identifier
	 * @param options
	 * @return Object
	 */
	private IIncludedFeatureReference getBestMatch(IConfiguredSite configuredSite)
		throws CoreException {
		IncludedFeatureReference newRef = null;

		if (configuredSite == null)
			return this;
		IFeatureReference[] enabledFeatures =
			configuredSite.getConfiguredFeatures();

		// find the best feature based on match from enabled features
		for (int ref = 0; ref < enabledFeatures.length; ref++) {
			if (enabledFeatures[ref] != null) {
				VersionedIdentifier id =
					enabledFeatures[ref].getVersionedIdentifier();
				if (matches(getVersionedIdentifier(), id)) {
					if (newRef == null
						|| id.getVersion().isGreaterThan(
							newRef.getVersionedIdentifier().getVersion())) {
						newRef =
							new IncludedFeatureReference(enabledFeatures[ref]);
						newRef.setMatchingRule(getMatch());
						newRef.isOptional(isOptional());
						newRef.setLabel(getLabel());
					}
				}
			}
		}

		if (UpdateCore.DEBUG && UpdateCore.DEBUG_SHOW_WARNINGS) {
			UpdateCore.warn(
				"Found best match feature:"
					+ newRef
					+ " for feature reference "
					+ this.getURLString());
		}

		if (newRef != null)
			return newRef;
		else
			return this;
	}
	/**
	 * @see org.eclipse.update.core.IFeatureReference#getFeature()
	 * @deprecated
	 */
	public IFeature getFeature() throws CoreException {
		return getFeature(null);
	}
	/**
	 * @see org.eclipse.update.core.IFeatureReference#getFeature
	 * (IProgressMonitor)
	 */
	public IFeature getFeature(IProgressMonitor monitor) throws CoreException {
		return getFeature(false, null, monitor);
	}
}
