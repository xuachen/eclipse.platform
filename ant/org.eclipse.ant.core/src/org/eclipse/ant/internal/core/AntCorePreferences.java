package org.eclipse.ant.internal.core;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.core.runtime.*;
/**
 * 
 */
public class AntCorePreferences {

	protected List defaultTasks;
	protected List defaultTypes;
	protected List defaultURLs;
	protected List customTasks;
	protected List customTypes;
	protected List customURLs;
	protected Map defaultObjects;
	protected List pluginClassLoaders;

	protected static final String PREFERENCES_FILE_NAME = ".preferences";

public AntCorePreferences(Map defaultTasks, Map defaultObjects, Map defaultTypes) {
	initializePluginClassLoaders();
	defaultURLs = new ArrayList(20);
	this.defaultTasks = computeDefaultTasks(defaultTasks);
	this.defaultTypes = computeDefaultTypes(defaultTypes);
	this.defaultObjects = computeDefaultObjects(defaultObjects);
	restoreCustomObjects();
}

protected void restoreCustomObjects() {
	customTasks = new ArrayList(10);
	customTypes = new ArrayList(10);
	customURLs = computeCustomURLs();
}

protected List computeCustomURLs() {
	List result = new ArrayList(10);
	IPluginDescriptor descriptor = Platform.getPlugin("org.apache.ant").getDescriptor();
	addLibraries(descriptor, result);
	descriptor = Platform.getPlugin("org.apache.xerces").getDescriptor();
	addLibraries(descriptor, result);
	addToolsJar(result);
	return result;
}

protected List computeDefaultTasks(Map tasks) {
	List result = new ArrayList(10);
	for (Iterator iterator = tasks.entrySet().iterator(); iterator.hasNext();) {
		Map.Entry entry = (Map.Entry) iterator.next();
		Task task = new Task();
		task.setTaskName((String) entry.getKey());
		IConfigurationElement element = (IConfigurationElement) entry.getValue();
		task.setClassName(element.getAttribute(AntCorePlugin.CLASS));
		String library = element.getAttribute(AntCorePlugin.LIBRARY);
		if (library == null)
			continue; // FIXME: can it be null?
		IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
		try {
			URL url = Platform.asLocalURL(new URL(descriptor.getInstallURL(), library));
			task.setLibrary(url);
			defaultURLs.add(url);
		} catch (Exception e) {
			e.printStackTrace(); // FIXME
		}
		result.add(task);
		addPluginClassLoader(descriptor.getPluginClassLoader());
	}
	return result;
}

protected List computeDefaultTypes(Map types) {
	List result = new ArrayList(10);
	for (Iterator iterator = types.entrySet().iterator(); iterator.hasNext();) {
		Map.Entry entry = (Map.Entry) iterator.next();
		Type type = new Type();
		type.setTypeName((String) entry.getKey());
		IConfigurationElement element = (IConfigurationElement) entry.getValue();
		type.setClassName(element.getAttribute(AntCorePlugin.CLASS));
		String library = element.getAttribute(AntCorePlugin.LIBRARY);
		if (library == null)
			continue; // FIXME: can it be null?
		IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
		try {
			URL url = Platform.asLocalURL(new URL(descriptor.getInstallURL(), library));
			type.setLibrary(url);
			defaultURLs.add(url);
		} catch (Exception e) {
			e.printStackTrace(); // FIXME
		}
		result.add(type);
		addPluginClassLoader(descriptor.getPluginClassLoader());
	}
	return result;
}

/**
 * It returns the same objects as passed in the arguments. The only difference
 * is that it does extract other useful information.
 */
protected Map computeDefaultObjects(Map objects) {
	for (Iterator iterator = objects.entrySet().iterator(); iterator.hasNext();) {
		Map.Entry entry = (Map.Entry) iterator.next();
		IConfigurationElement element = (IConfigurationElement) entry.getValue();
		String library = element.getAttribute(AntCorePlugin.LIBRARY);
		if (library == null)
			continue; // FIXME: can it be null?
		IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
		try {
			URL url = Platform.asLocalURL(new URL(descriptor.getInstallURL(), library));
			defaultURLs.add(url);
		} catch (Exception e) {
			e.printStackTrace(); // FIXME
		}
		addPluginClassLoader(descriptor.getPluginClassLoader());
	}
	return objects;
}

/**
 * Ant running through the command line tries to find tools.jar to help the user. Try
 * emulating the same behaviour here.
 */
protected void addToolsJar(List destination) {
	IPath path = new Path(System.getProperty("java.home"));
	if (path.lastSegment().equalsIgnoreCase("jre"))
		path = path.removeLastSegments(1);
	path = path.append("lib").append("tools.jar");
	File tools = path.toFile();
	if (!tools.exists())
		return;
	try {
		destination.add(tools.toURL());
	} catch (MalformedURLException e) {
		e.printStackTrace(); // FIXME
	}
}

protected void addLibraries(IPluginDescriptor source, List destination) {
	URL root = source.getInstallURL();
	ILibrary[] libraries = source.getRuntimeLibraries();
	for (int i = 0; i < libraries.length; i++) {
		try {
			URL url = new URL(root, libraries[i].getPath().toString());
			destination.add(Platform.asLocalURL(url));
		} catch (Exception e) {
			e.printStackTrace(); // FIXME
		}
	}
}

protected void readCustomURLs() {
	customURLs = new ArrayList(10);
	Properties urls = new Properties();
	try {
		IPath location = AntCorePlugin.getPlugin().getStateLocation().append(".urls");
		urls.load(new FileInputStream(location.toOSString()));
		for (Iterator iterator = urls.values().iterator(); iterator.hasNext();) {
			String url = (String) iterator.next();
			try {
				customURLs.add(new URL(url));
			} catch (MalformedURLException e) {
				e.printStackTrace(); // FIXME
			}
		}
	} catch (IOException e) {
		e.printStackTrace(); // FIXME
	}
}

/**
 * Param source is a Collection of IConfigurationElement.
 */
protected void addPluginClassLoaders(Collection source, List destination) {
	for (Iterator iterator = source.iterator(); iterator.hasNext();) {
		IConfigurationElement element = (IConfigurationElement) iterator.next();
		IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
		ClassLoader loader = descriptor.getPluginClassLoader();
		if (!destination.contains(loader))
			destination.add(loader);
	}
}

protected void addPluginClassLoader(ClassLoader loader) {
	if (!pluginClassLoaders.contains(loader))
		pluginClassLoaders.add(loader);
}

/**
 * Param source is a Collection of IConfigurationElement.
 */
protected void addURLs(Collection source, List destination) {
	for (Iterator iterator = source.iterator(); iterator.hasNext();) {
		IConfigurationElement element = (IConfigurationElement) iterator.next();
		String library = element.getAttribute(AntCorePlugin.LIBRARY);
		if (library == null)
			continue; // FIXME: can it be null?
		IPluginDescriptor descriptor = element.getDeclaringExtension().getDeclaringPluginDescriptor();
		try {
			URL url = Platform.asLocalURL(new URL(descriptor.getInstallURL(), library));
			if (!destination.contains(url))
				destination.add(url);
		} catch (Exception e) {
			e.printStackTrace(); // FIXME
		}
	}
}


public URL[] getURLs() {
	List result = new ArrayList(10);
	if (defaultURLs != null)
		result.addAll(defaultURLs);
	if (customURLs != null)
		result.addAll(customURLs);
	return (URL[]) result.toArray(new URL[result.size()]);
}

public ClassLoader[] getPluginClassLoaders() {
	return (ClassLoader[]) pluginClassLoaders.toArray(new ClassLoader[pluginClassLoaders.size()]);
}

protected void initializePluginClassLoaders() {
	pluginClassLoaders = new ArrayList(20);
	// ant.core should always be present
	pluginClassLoaders.add(Platform.getPlugin(AntCorePlugin.PI_ANTCORE).getDescriptor().getPluginClassLoader());
}


/**
 * Returns default + custom tasks.
 */
public List getTasks() {
	List result = new ArrayList(10);
	if (defaultTasks != null)
		result.addAll(defaultTasks);
	if (customTasks != null)
		result.addAll(customTasks);
	return result;
}

public List getCustomTasks() {
	List result = new ArrayList(10);
	if (customTasks != null)
		result.addAll(customTasks);
	return result;
}

public List getCustomTypes() {
	List result = new ArrayList(10);
	if (customTypes != null)
		result.addAll(customTypes);
	return result;
}

public List getCustomURLs() {
	List result = new ArrayList(10);
	if (customURLs != null)
		result.addAll(customURLs);
	return result;
}

public void addCustomTask(Task task) {
	customTasks.add(task);
}

public void addCustomType(Type type) {
	customTypes.add(type);
}

public void addCustomURL(URL url) {
	customURLs.add(url);
}

public void removeCustomTask(Task task) {
	// FIXME:
}

public void removeCustomType(Type type) {
	// FIXME:
}

public void removeCustomURL(URL url) {
	// FIXME:
}

/**
 * Returns default + custom types.
 */
public List getTypes() {
	List result = new ArrayList(10);
	if (defaultTypes != null)
		result.addAll(defaultTypes);
	if (customTypes != null)
		result.addAll(customTypes);
	return result;
}

}