package org.eclipse.update.internal.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.FeatureModelFactory;

public class FeaturePackagedFactory extends FeatureModelFactory implements IFeatureFactory {

	/*
	 * @see IFeatureFactory#createFeature(URL,ISite)
	 */
	public IFeature createFeature(URL url,ISite site) throws CoreException {
		FeaturePackaged feature = null;
		InputStream featureStream = null;
		
		try {		
			
			// unpack JAR file in TEMP directory first
			url = unjar(url);
			
			featureStream = url.openStream();
			FeatureModelFactory factory = (FeatureModelFactory) this;
			feature = (FeaturePackaged) factory.parseFeature(featureStream);
			feature.setSite(site);
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

	private URL unjar(URL jarURL) throws CoreException {
		URL result = null;
		
		return result;
	}
}
