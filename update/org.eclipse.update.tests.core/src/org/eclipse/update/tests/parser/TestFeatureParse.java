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
package org.eclipse.update.tests.parser;

import java.net.URL;

import org.eclipse.update.core.*;
import org.eclipse.update.core.model.FeatureParser;
import org.eclipse.update.core.model.Feature;
import org.eclipse.update.core.model.URLEntry;
import org.eclipse.update.internal.core.*;
import org.eclipse.update.tests.UpdateManagerTestCase;
import org.xml.sax.SAXParseException;

public class TestFeatureParse extends UpdateManagerTestCase {

	/**
	 * Constructor for Test1
	 */
	public TestFeatureParse(String arg0) {
		super(arg0);
	}

	public void testParse() throws Exception {

		String xmlFile = "xmls/feature_1.0.0/";
		ISite remoteSite = SiteManager.getSite(SOURCE_FILE_SITE);
		URL url = UpdateManagerUtils.getURL(remoteSite.getURL(), xmlFile, null);

		SiteFeatureReference ref = new SiteFeatureReference();
		ref.setSite(remoteSite);
		ref.setURL(url);
		IFeature feature = ref.getFeature();

		String prov = feature.getProvider();
		assertEquals("Object Technology International", prov);

	}

	public void testParseValid1() throws Exception {

		try {
			URL remoteURL = new URL(SOURCE_FILE_SITE + "parsertests/feature1.xml");
			FeatureParser parser = new FeatureParser();
			parser.init(new FeatureExecutableFactory());
			URL resolvedURL = URLEncoder.encode(remoteURL);
			Feature remoteFeature = parser.parse(resolvedURL.openStream());
			remoteFeature.resolve(remoteURL, null);

		} catch (SAXParseException e) {
			fail("Exception should NOT be thrown");
		}
	}

	public void testParseValid1bis() throws Exception {

		try {
			URL remoteURL = new URL(SOURCE_FILE_SITE + "parsertests/feature1bis.xml");
			FeatureParser parser = new FeatureParser();
			parser.init(new FeatureExecutableFactory());
			URL resolvedURL = URLEncoder.encode(remoteURL);
			Feature remoteFeature = parser.parse(resolvedURL.openStream());
			remoteFeature.resolve(remoteURL, null);

		} catch (SAXParseException e) {
			fail("Exception should NOT be thrown");
		}
	}

	public void testParseValid2() throws Exception {

		try {
			URL remoteURL = new URL(SOURCE_FILE_SITE + "parsertests/feature2.xml");
			FeatureParser parser = new FeatureParser();
			parser.init(new FeatureExecutableFactory());
			URL resolvedURL = URLEncoder.encode(remoteURL);
			Feature remoteFeature = parser.parse(resolvedURL.openStream());
			remoteFeature.resolve(remoteURL, null);

		} catch (SAXParseException e) {
			fail("Exception should not be thrown" + e.getMessage());
		}
	}

	public void testParseValid3() throws Exception {

		try {
			URL remoteURL = new URL(SOURCE_FILE_SITE + "parsertests/feature3.xml");
			FeatureParser parser = new FeatureParser();
			parser.init(new FeatureExecutableFactory());
			URL resolvedURL = URLEncoder.encode(remoteURL);
			Feature remoteFeature = parser.parse(resolvedURL.openStream());
			remoteFeature.resolve(remoteURL, null);

			String copyrightString = remoteFeature.getCopyright().getURL().getFile();
			boolean resolved = copyrightString.indexOf(UpdateManagerUtils.getOS()) != -1;
			assertTrue("Copyright URL not resolved:" + copyrightString, resolved);
		} catch (SAXParseException e) {
			fail("Exception should not be thrown" + e.getMessage());
		}
	}

	// parse type
	public void testParseValid4() throws Exception {

		try {
			URL remoteURL = new URL(SOURCE_FILE_SITE + "parsertests/feature4.xml");
			FeatureParser parser = new FeatureParser();
			parser.init(new FeatureExecutableFactory());
			URL resolvedURL = URLEncoder.encode(remoteURL);
			Feature remoteFeature = parser.parse(resolvedURL.openStream());
			remoteFeature.resolve(remoteURL, null);

			URLEntry[] models = remoteFeature.getDiscoverySiteEntries();
			assertTrue("Discovery model is not a Web Model", models[0].getType()==IURLEntry.WEB_SITE);
			URLEntry copyright = remoteFeature.getCopyright();
			assertTrue("Copyright model is not an Update Model", copyright.getType()==IURLEntry.UPDATE_SITE);
			
		} catch (SAXParseException e) {
			fail("Exception should not be thrown" + e.getMessage());
		}
	}

}
