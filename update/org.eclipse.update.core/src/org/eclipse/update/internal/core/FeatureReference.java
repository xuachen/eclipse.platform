package org.eclipse.update.internal.core;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

import org.eclipse.core.internal.runtime.Assert;
import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.model.FeatureReferenceModel;
import org.eclipse.update.internal.core.obsolete.*;
import org.eclipse.update.internal.core.obsolete.FeaturePackaged;

/**
 *
 * 
 */

public class FeatureReference extends FeatureReferenceModel implements IFeatureReference, IWritable {

	private IFeature feature;

	/**
	 * category : delegate to teh site
	 */
	private List categories;

	/**
	 * Constructor
	 */
	public FeatureReference() {
		super();
	}

	/**
	 * Returns the array of categories the feature belong to.
	 * 
	 * The categories are declared in the <code>site.xml</code> file.
	 * 
	 * @see ICategory
	 * @return the array of categories this feature belong to. Returns an empty array
	 * if there are no cateopries.
	 */
	public ICategory[] getCategories() {

		if (categories == null) {
			categories = new ArrayList();
			String[] categoriesAsString = getCategoryNames();
			for (int i = 0; i < categoriesAsString.length; i++){
					categories.add(getSite().getCategory(categoriesAsString[i]));
			}
		}

		ICategory[] result = new ICategory[0];

		if (!(categories == null || categories.isEmpty())) {
			result = new ICategory[categories.size()];
			categories.toArray(result);
		}
		return result;
	}

	/**
	 * Returns the feature this reference points to
	 *  @return teh feature on teh Site
	 */
	public IFeature getFeature() throws CoreException {

		if (feature == null) {
			if (getType() == null || getType().equals("")) {
				if (getURL().toExternalForm().endsWith(FeaturePackaged.JAR_EXTENSION)) {
					// if it ends with JAR, guess it is a FeaturePackaged
					String pluginID = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier()+".";
					setType(pluginID+IFeatureFactory.INSTALLABLE_FEATURE_TYPE);
				} else {
					// ask the Site for the default type 
					setType(getSite().getDefaultExecutableFeatureType());
				}
			}
		feature = createFeature(getType(),getURL(),getSite());				
		}
		
		return feature;
	}

	/*
	 * @see IFeatureReference#addCategory(ICategory)
	 */
	public void addCategory(ICategory category) {
		this. addCategoryName(category.getName());
	}

	/*
	 * @see IWritable#write(int, PrintWriter)
	 */
	public void write(int indent, PrintWriter w) {
	String gap = "";
	for (int i = 0; i < indent; i++) gap += " ";
	String increment = "";
	for (int i = 0; i < IWritable.INDENT; i++) increment += " ";
		
		w.print(gap+"<"+SiteParser.FEATURE+" ");
		// FIXME: feature type to implement
		// 
		// feature URL
		String URLInfoString = null;
		if(getURL()!=null) {
			URLInfoString = UpdateManagerUtils.getURLAsString(getSite().getURL(),getURL());
			w.print("url=\""+Writer.xmlSafe(URLInfoString)+"\"");
		}
		w.println(">");
		
		String[] categoryNames = getCategoryNames();
		for (int i = 0; i < categoryNames.length; i++){
			String element = categoryNames[i];
			w.println(gap+increment+"<"+SiteParser.CATEGORY+" name=\""+Writer.xmlSafe(element)+"\"/>");		
		}
		w.println("</"+SiteParser.FEATURE+">");
	}

	/**
	 * create an instance of a class that implements IFeature
	 */
	private IFeature createFeature(String featureType, URL url, ISite site) throws CoreException{
		IFeature feature = null;
		IFeatureFactory factory = FeatureTypeFactory.getInstance().getFactory(featureType);
		feature = factory.createFeature(url,site);
		return feature;
	}
	

		/*
	 * @see IFeatureReference#setURL(URL)
	 */
	public void setURL(URL url) {
		if (url!=null){
			setURLString(url.toExternalForm());
		}
	}

}