/*******************************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 * IBM - Initial API and implementation
 ******************************************************************************/
package org.eclipse.ant.internal.ui;

import java.net.URL;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * A page to set the preferences for the classpath
 */
public class CustomizeAntPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
		
	protected ClasspathPage jarsPage;
	protected TasksPage tasksPage;
	protected TypesPage typesPage;
	
	protected CustomizeAntPage currentPage;
	
/**
 * Create the console page.
 */
public CustomizeAntPreferencePage() {
	setDescription(Policy.bind("preferences.customize.description"));
	IPreferenceStore store = AntUIPlugin.getPlugin().getPreferenceStore();
	setPreferenceStore(store);
}
/**
 * @see IWorkbenchPreferencePage#init
 */
public void init(IWorkbench workbench) {
}
protected Control createContents(Composite parent) {
	TabFolder folder= new TabFolder(parent, SWT.NONE);
	folder.setLayout(new GridLayout());	
	folder.setLayoutData(new GridData(GridData.FILL_BOTH));
	folder.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			tabChanged(e.item);
		}	
	});

	jarsPage = new ClasspathPage();
	jarsPage.createTabItem(folder);
	jarsPage.setURLs(AntCorePlugin.getPlugin().getPreferences().getURLs());
	currentPage = jarsPage;
	
	tasksPage = new TasksPage();
	tasksPage.createTabItem(folder);

	typesPage = new TypesPage();
	typesPage.createTabItem(folder);

	return folder;
}
/**
 * @see PreferencePage#performDefaults()
 */
protected void performDefaults() {
	super.performDefaults();
	jarsPage.setURLs(AntCorePlugin.getPlugin().getPreferences().getURLs());
}
/**
 * @see IPreferencePage#performOk()
 */
public boolean performOk() {
	URL[] urls = jarsPage.getURLs();
	if (urls != null) {
		AntCorePlugin.getPlugin().getPreferences().setCustomURLs(urls);
	}
	return super.performOk();
}
protected void tabChanged(Widget widget) {
	currentPage = (CustomizeAntPage)widget.getData();
}
}
