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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/**
 * A widget group for the types tab of the ant classpath preference page.
 */
public class TypesPage extends CustomizeAntPage {
	protected Control control;
public TypesPage() {
}
/**
 * Creates and returns a tab item that contains this widget group.
 */
public TabItem createTabItem(TabFolder folder) {
	TabItem item = new TabItem(folder, SWT.NONE);
	item.setText(Policy.bind("preferences.customize.typesPageTitle"));
//	item.setImage(imageRegistry.get(JavaPluginImages.IMG_OBJS_PACKFRAG_ROOT));
	item.setData(this);
	item.setControl(createControl(folder));
	return item;
}
/**
 * Creates and returns a control that contains this widget group.
 */
public Control createControl(Composite parent) {
	Label label = new Label(parent, SWT.NONE);
	label.setText("Hello from types page");
	return label;
}
}