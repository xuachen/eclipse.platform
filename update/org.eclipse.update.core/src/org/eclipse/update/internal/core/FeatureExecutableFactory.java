package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.FeatureModelFactory;

public class FeatureExecutableFactory extends FeatureModelFactory implements IFeatureFactory {

	/*
	 * @see IFeatureFactory#createFeature(URL,ISite)
	 */
	public IFeature createFeature(URL url, ISite site) throws CoreException {

		Feature feature = null;
		InputStream featureStream = null;
		
		try {	
			IFeatureContentProvider contentProvider = new FeatureExecutableContentProvider(url);
			IContentConsumer contentConsumer = new FeatureExecutableContentConsumer();
			
			featureStream = contentProvider.getFeatureManifest().asURL().openStream();
			FeatureModelFactory factory = (FeatureModelFactory) this;
			feature = (Feature)factory.parseFeature(featureStream);
			feature.setSite(site);
			
			feature.setFeatureContentProvider(contentProvider);
			feature.setContentConsumer(contentConsumer);
			
			feature.resolve(url, getResourceBundle(url));
			feature.markReadOnly();
		} catch (IOException e) {
			// if we cannot find the feature or the feature.xml...
			// We should not stop the execution 
			// but we must Log it all the time, not only when debugging...
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, "Error opening feature.xml in the feature archive:" + url.toExternalForm(), e);
			UpdateManagerPlugin.getPlugin().getLog().log(status);
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, "Error parsing feature.xml in the feature archive:" + url.toExternalForm(), e);
			throw new CoreException(status);
		} finally {
			try {
				featureStream.close();
			} catch (Exception e) {
			}
		}
		return feature;
	}

	/**
	 * return the appropriate resource bundle for this feature
	 */
	private ResourceBundle getResourceBundle(URL url) throws IOException, CoreException {
		ResourceBundle bundle = null;
		try {
			ClassLoader l = new URLClassLoader(new URL[] { url }, null);
			bundle = ResourceBundle.getBundle(DefaultFeature.FEATURE_FILE, Locale.getDefault(), l);
		} catch (MissingResourceException e) {
			//ok, there is no bundle, keep it as null
			//DEBUG:
			if (UpdateManagerPlugin.DEBUG && UpdateManagerPlugin.DEBUG_SHOW_WARNINGS) {
				UpdateManagerPlugin.getPlugin().debug(e.getLocalizedMessage() + ":" + url.toExternalForm());
			}
		}
		return bundle;
	}

}