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

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.internal.core.*;
import org.eclipse.update.internal.operations.*;

/**
 * Help class for installing/removing features.
 */
public class FeatureInstaller {
	private InstallAbortedException abortedException = null;
	
	private IFeature feature;
	private boolean closed = false;

	// recovery
	private String oldPath;
	private String newPath;

	//  for abort
	private List /* of PluginInstaller */ pluginInstallers = new ArrayList();
	private List /*of path as String */	installedFiles = new ArrayList();
	
	public IFeature install(
		InstalledSite targetSite,
		final IFeature sourceFeature,
		IFeature[] optionalfeatures,
		IVerifier parentVerifier,
		final IVerificationListener verificationListener,
		final InstallMonitor monitor)
		throws InstallAbortedException, CoreException {

		if (sourceFeature == null)
			return null;

		ErrorRecoveryLog recoveryLog = ErrorRecoveryLog.getLog();	
		
		// Setup optional install handler
		InstallHandlerProxy handler =
			new InstallHandlerProxy(
				IInstallHandler.HANDLER_ACTION_INSTALL,
				sourceFeature,
				sourceFeature.getInstallHandlerEntry(),
				(InstallMonitor)monitor);
		
		
		// create new executable feature and install source content into it
		Feature targetFeature = new Feature();
		targetFeature.setFeatureIdentifier(sourceFeature.getVersionedIdentifier().getIdentifier());
		targetFeature.setFeatureVersion(sourceFeature.getVersionedIdentifier().getVersion().toString());
// TODO need to refresh the targetfeature and the installed site one installation is complete

	   
		IFeature alreadyInstalledFeature = getAlreadyInstalledFeature(sourceFeature, targetSite);

		boolean success = false;
		Throwable originalException = null;
		abortedException = null;

	   // Get source feature provider and verifier.
	   // Initialize target variables.
	   final IFeatureContentProvider provider = sourceFeature.getFeatureContentProvider();
	   final IVerifier verifier = provider.getVerifier();
	   verifier.setParent(parentVerifier);

	   try {
		   // determine list of plugins to install
		   // find the intersection between the plugin entries already contained
		   // on the target site, and plugin entries packaged in source feature
		   IPluginEntry[] sourcePlugins = sourceFeature.getPluginEntries();
		   IPluginEntry[] targetPlugins = targetSite.getPluginEntries();
		   IPluginEntry[] pluginsToInstall = UpdateManagerUtils.diff(sourcePlugins, targetPlugins);
		   
		   INonPluginEntry[] nonPluginsToInstall = sourceFeature.getNonPluginEntries();
	
		   IFeature[] children = sourceFeature.getIncludedFeatures();
		   if (optionalfeatures != null) {
			   children =
				   UpdateManagerUtils.optionalChildrenToInstall(
					   children,
					   optionalfeatures);
		   }
	
		   // determine number of monitor tasks
		   //   2 tasks for the feature jar (download/verify + install)
		   // + 2*n tasks for plugin entries (download/verify + install for each)
		   // + 1*m tasks per non-plugin data entry (download for each)
		   // + 1 task for custom non-plugin entry handling (1 for all combined)
		   // + 5*x tasks for children features (5 subtasks per install)
		   int taskCount =
			   2
				   + 2 * pluginsToInstall.length
				   + nonPluginsToInstall.length
				   + 1
				   + 5 * children.length;
		   monitor.beginTask("", taskCount);
		   SubProgressMonitor subMonitor = null;
	
		   // start log
		   recoveryLog.open(ErrorRecoveryLog.START_INSTALL_LOG);
	
		   // Start the installation tasks			
		   handler.installInitiated();
	
		   // Download and verify feature archive(s)
		   ContentReference[] references =
			   provider.getFeatureEntryArchiveReferences(monitor);
		   verifyReferences(
		   	   sourceFeature,
			   verifier,
			   references,
			   monitor,
			   verificationListener,
			   true);
		   
		   monitorWork(monitor, 1);
	
		   final MultiDownloadMonitor distributedMonitor =
			   new MultiDownloadMonitor(monitor, targetFeature);
	
		   final ThreadGroup tgroup =
			   new ThreadGroup("Feature " + sourceFeature.getURL() + " download");
		   // Download and verify plugin archives
		   for (int i = 0; i < pluginsToInstall.length; i++) {
			   final IPluginEntry pluginToInstall = pluginsToInstall[i];
			   Runnable r = new Runnable() {
				   public void run() {
					   try {
						   final ContentReference[] plugin_references =
							   provider.getPluginEntryArchiveReferences(
								   pluginToInstall,
								   distributedMonitor);
						   verifyReferences(
						   	   sourceFeature,
							   verifier,
							   plugin_references,
							   distributedMonitor,
							   verificationListener,
							   false);
						   monitorWork(monitor, 1);
					   } catch (InstallAbortedException e) {
						   abortedException = e;
						   MultiDownloadManager.stopThreads(tgroup);
					   } catch (CoreException e) {
						   UpdateUtils.logException(e);
					   } finally {
						   MultiDownloadManager.releaseThread(
							   Thread.currentThread());
					   }
				   }
			   };
			   Thread downloadThread =
				   MultiDownloadManager.getThread(
					   r,
					   "download " + pluginToInstall.toString(),
					   tgroup);
			   downloadThread.start();
		   }
	
		   MultiDownloadManager.waitForThreads(tgroup);
	
		   handler.pluginsDownloaded(pluginsToInstall);
	
		   // Download non-plugin archives. Verification handled by optional install handler
		   for (int i = 0; i < nonPluginsToInstall.length; i++) {
			   references =
				   provider.getNonPluginEntryArchiveReferences(
					   nonPluginsToInstall[i],
					   monitor);
			   monitorWork(monitor, 1);
		   }
		   handler.nonPluginDataDownloaded(
			   nonPluginsToInstall,
			   verificationListener);
	
		   // All archives are downloaded and verified. Get ready to install
		 
		   // Install child features.
		   // Check if they are optional, and if they should be installed
		   for (int i = 0; i < children.length; i++) {
			   IFeature childFeature = children[i];

			   subMonitor = new SubProgressMonitor(monitor, 5);
			   install(
			   	   targetSite,
			   	   childFeature,
				   optionalfeatures,
				   verifier,
				   verificationListener,
				   new InstallMonitor(subMonitor));
		   }
	
		   // Install plugin files
		   for (int i = 0; i < pluginsToInstall.length; i++) {
			   // if another feature has already installed this plugin, skip it
			   if (InstallRegistry.getInstance().isPluginJustInstalled(pluginsToInstall[i])) {
				   monitor.worked(1);
				   continue;
			   }
			   PluginInstaller pluginInstaller = new PluginInstaller();
			   pluginInstallers.add(pluginInstaller);
			   pluginInstaller.install(pluginsToInstall[i], sourceFeature, targetSite, monitor);
		   }
	
		   // check if we need to install feature files [16718]	
		   // store will throw CoreException if another feature is already
		   // installed in the same place
		   alreadyInstalledFeature = getAlreadyInstalledFeature(sourceFeature, targetSite);
		   // 18867
		   if (alreadyInstalledFeature == null) {
			   //Install feature files
			   references = provider.getFeatureEntryContentReferences(monitor);
	
			   String msg = "";
			   subMonitor = new SubProgressMonitor(monitor, 1);
			   msg = Policy.bind("Feature.TaskInstallFeatureFiles"); //$NON-NLS-1$
	
			   for (int i = 0; i < references.length; i++) {
				   subMonitor.setTaskName( msg + " " + references[i].getIdentifier());
				   storeFeature(sourceFeature, references[i], subMonitor);
			   }
			   // TODO should this be targetFeature ?
			   InstallRegistry.registerFeature(sourceFeature);
		   } else {
			   monitor.worked(1);
		   }
	
		   if (monitor.isCanceled())
			   abort();
	
		   // call handler to complete installation (eg. handle non-plugin entries)
		   handler.completeInstall(consumer);
		   monitorWork(monitor, 1);
	
		   // indicate install success
		   success = true;
	
	   } catch (InstallAbortedException e) {
		   abortedException = e;
	   } catch (CoreException e) {
		   originalException = e;
	   } finally {
		   Exception newException = null;
		   try {

				   if (success) {
					   targetFeature = consumer.close();
					   if (targetFeature == null) {
					   		targetFeature = alreadyInstalledFeature; // 18867
						   if (targetFeature != null
							   && optionalfeatures != null
							   && optionalfeatures.length > 0) {
							   // reinitialize as new optional children may have been installed
							   reinitializeFeature(targetFeature);
						   }
					   }
					   // close the log
					   recoveryLog.close(ErrorRecoveryLog.END_INSTALL_LOG);
				   } else {
					   consumer.abort();
				   }

			   handler.installCompleted(success);
			   // if abort is done, no need for the log to stay
			   recoveryLog.delete();
		   } catch (CoreException e) {
			   newException = e;
		   }
	
		   // original exception wins unless it is InstallAbortedException
		   // and an error occured during abort
		   if (originalException != null) {
			   throw Utilities.newCoreException(
				   Policy.bind("InstallHandler.error", sourceFeature.getName()),
				   originalException);
		   }
	
		   if (newException != null)
			   throw Utilities.newCoreException(
				   Policy.bind("InstallHandler.error", sourceFeature.getName()),
				   newException);
	
		   if (abortedException != null) {
			   throw abortedException;
		   }
	
	   }
	   return targetFeature;
			
// TODO need to refresh the targetfeature and the installed site one installation is complete
			return targetFeature;
	}
	
	private void verifyReferences(
		IFeature feature,
		IVerifier verifier,
		ContentReference[] references,
		InstallMonitor monitor,
		IVerificationListener verificationListener,
		boolean isFeature)
		throws CoreException {
		IVerificationResult vr = null;
		if (verifier != null) {
			for (int j = 0; j < references.length; j++) {
				vr = verifier.verify(feature, references[j], isFeature, monitor);
				if (vr != null) {
					if (verificationListener == null)
						return;

					int result = verificationListener.prompt(vr);

					if (result == IVerificationListener.CHOICE_ABORT) {
						String msg = Policy.bind("JarVerificationService.CancelInstall"); //$NON-NLS-1$
						Exception e = vr.getVerificationException();
						throw new InstallAbortedException(msg, e);
					}
					if (result == IVerificationListener.CHOICE_ERROR) {
						throw Utilities
							.newCoreException(
								Policy.bind(
									"JarVerificationService.UnsucessfulVerification"),
						//$NON-NLS-1$
						vr.getVerificationException());
					}
				}
			}
		}
	}
	
	private void monitorWork(IProgressMonitor monitor, int tick)
		throws CoreException {
		if (monitor != null) {
			monitor.worked(tick);
			if (monitor.isCanceled()) {
				abort();
			}
		}
	}

	
	/*
	 * Installation has been cancelled, abort and revert
	 */
	private void abort() throws CoreException {
		String msg = Policy.bind("Feature.InstallationCancelled"); //$NON-NLS-1$
		throw new InstallAbortedException(msg, null);
	}
	
	private void storeFeature(IFeature feature, ContentReference contentReference, IProgressMonitor monitor) throws CoreException {

		if (closed) {
			UpdateCore.warn("Attempt to store in a closed SiteFileContentConsumer", new Exception());
			return;
		}

		InputStream inStream = null;
		String featurePath = getFeaturePath(feature);
		String contentKey = contentReference.getIdentifier();
		featurePath += contentKey;

		// error recovery
		if (featurePath.endsWith("\\"+Feature.FEATURE_XML) || featurePath.endsWith("/"+Feature.FEATURE_XML)) {
			oldPath = featurePath.replace(File.separatorChar, '/');
			File localFile = new File(oldPath);
			if (localFile.exists()) {
				throw Utilities.newCoreException(Policy.bind("UpdateManagerUtils.FileAlreadyExists", new Object[] { localFile }), null);
			}
			featurePath = ErrorRecoveryLog.getLocalRandomIdentifier(featurePath);
			newPath = featurePath;
			ErrorRecoveryLog.getLog().appendPath(ErrorRecoveryLog.FEATURE_ENTRY, featurePath);
		}

		try {
			inStream = contentReference.getInputStream();
			UpdateManagerUtils.copyToLocal(inStream, featurePath, null);
			UpdateManagerUtils.checkPermissions(contentReference, featurePath); // 20305
			installedFiles.add(featurePath);
		} catch (IOException e) {
			throw Utilities.newCoreException(Policy.bind("GlobalConsumer.ErrorCreatingFile", featurePath), e);
			//$NON-NLS-1$
		} finally {
			if (inStream != null) {
				try {
					// close stream
					inStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	/*
	 * Returns the path in which the Feature will be installed
	 */
	private String getFeaturePath(IFeature feature) throws CoreException {
		String featurePath = null;
		try {
			VersionedIdentifier featureIdentifier = feature.getVersionedIdentifier();
			String path = Site.DEFAULT_INSTALLED_FEATURE_PATH + featureIdentifier.toString() + File.separator;
			URL newURL = new URL(feature.getSite().getURL(), path);
			featurePath = newURL.getFile();
		} catch (MalformedURLException e) {
			throw Utilities.newCoreException(Policy.bind("SiteFileContentConsumer.UnableToCreateURL") + e.getMessage(), e);
			//$NON-NLS-1$
		}
		return featurePath;
	}
	
	/*
	 * returns installed feature if the same feature is installed on the site
	 * [18867]
	 */
	private IFeature getAlreadyInstalledFeature(IFeature feature, IInstalledSite targetSite) {

		IFeature[] features = targetSite.getFeatures();
		for (int i = 0; i < features.length; i++) {
			try {
				if (feature.getVersionedIdentifier().equals(features[i].getVersionedIdentifier()))
					return features[i]; // 18867
			} catch (CoreException e) {
				UpdateCore.warn(null, e);
			}
		}

		UpdateCore.warn(
			"ValidateAlreadyInstalled:Feature "
				+ this
				+ " not found on site:"
				+ feature.getURL());
		return null;
	}
}
