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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

/**
 * Abstract superclass for all tabs that contribute to the ant classpath
 * preference page.
 */
public abstract class CustomizeAntPage extends Object {
	protected FontMetrics fontMetrics;

/**
 * Creates and returns a button with appropriate size and layout.
 * @param parent
 * @param labelKey The button text key, used to fetch the appropriate
 * message from the externalized catalog.
 */
protected Button createButton(Composite parent, String labelKey) {
	Button button = new Button(parent, SWT.PUSH);
	button.setText(Policy.bind(labelKey));
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.BUTTON_WIDTH);
	data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	data.heightHint = Dialog.convertVerticalDLUsToPixels(fontMetrics, IDialogConstants.BUTTON_HEIGHT);
	button.setLayoutData(data);
	return button;
}
protected Label createSeparator(Composite parent) {
	Label separator= new Label(parent, SWT.NONE);
	separator.setVisible(false);
	GridData gd= new GridData();
	gd.horizontalAlignment= gd.FILL;
	gd.verticalAlignment= gd.BEGINNING;
	gd.heightHint= 4;
	separator.setLayoutData(gd);
	return separator;
}
}
