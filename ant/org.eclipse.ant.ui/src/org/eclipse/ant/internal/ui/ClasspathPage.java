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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.internal.core.AntCorePreferences;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.win32.CREATESTRUCT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * A widget group for the jars tab of the ant classpath preference page.
 */
public class ClasspathPage extends CustomizeAntPage {
	protected final ArrayList elements = new ArrayList();
	public class ClasspathLabelProvider extends LabelProvider implements ITableLabelProvider {
		protected Image folderImage;
		protected Image jarImage;
		public ClasspathLabelProvider() {
			folderImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			jarImage = AntUIPlugin.getPlugin().getImageDescriptor(AntUIPlugin.IMG_JAR_FILE).createImage();
		}
		public Image getColumnImage(Object element, int columnIndex) {
			URL url = (URL)element;
			if (url.getFile().endsWith("/")) {
				return folderImage;
			} else {
				return jarImage;
			}
		}
		public String getColumnText(Object element, int columnIndex) {
			URL url = (URL)element;
			return url.getFile();
		}
		public void dispose() {
			//note: folder image is a shared image
			folderImage = null;
			if (jarImage != null) {
				jarImage.dispose();
				jarImage = null;
			}
		}
	}

	class ClasspathContentProvider extends Object implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return (URL[]) inputElement;
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			elements.clear();
			AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();
			elements.addAll(Arrays.asList(prefs.getURLs()));
		}
	}
	
	protected TableViewer jarTable;
	
	protected Button removeButton;
	
public ClasspathPage() {
}
protected void addFolderButtonPressed() {
	DirectoryDialog dialog = new DirectoryDialog(jarTable.getControl().getShell());
	String result = dialog.open();
	if (result != null) {
		try {
			URL url = new URL("file:" + result + "/");
			elements.add(url);
			jarTable.add(url);
		} catch (MalformedURLException e) {
		}
	}
}
protected void addJarButtonPressed() {
	FileDialog dialog = new FileDialog(jarTable.getControl().getShell());
	dialog.setFilterExtensions(new String[] {"*.jar"});
	String result = dialog.open();
	if (result != null) {
		try {
			URL url = new URL("file:" + result);
			elements.add(url);
			jarTable.add(url);
		} catch (MalformedURLException e) {
		}
	}
}
protected void createButtonGroup(Composite top) {
	Composite buttonGroup = new Composite(top, SWT.NONE);
	GridLayout layout = new GridLayout();
	layout.marginHeight = 0;
	layout.marginWidth = 0;
	buttonGroup.setLayout(layout);
	buttonGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	
	Button button = createButton(buttonGroup, "preferences.customize.addJarButtonTitle");
	button.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			addJarButtonPressed();
		}
	});
	button = createButton(buttonGroup, "preferences.customize.addFolderButtonTitle");
	button.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			addFolderButtonPressed();
		}
	});
	createSeparator(buttonGroup);
	removeButton = createButton(buttonGroup, "preferences.customize.removeButtonTitle");
	removeButton.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			removeButtonPressed();
		}
	});
}
/**
 * Creates and returns a control that contains this widget group.
 */
public Control createControl(Composite parent) {
	Composite top = new Composite(parent, SWT.NONE);
	GridLayout layout = new GridLayout();
	layout.numColumns = 2;
	layout.marginHeight = 2;
	layout.marginWidth = 2;
	top.setLayout(layout);
	
	//get font metrics for DLU -> pixel conversions
	GC gc = new GC(top);
	gc.setFont(top.getFont());
	fontMetrics = gc.getFontMetrics();
	gc.dispose();
	
	createTable(top);
	createButtonGroup(top);
	return top;
}
protected void createTable(Composite parent) {
	Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
	table.setLayoutData(new GridData(GridData.FILL_BOTH));
	jarTable = new TableViewer(table);
	jarTable.setContentProvider(new ClasspathContentProvider());
	jarTable.setLabelProvider(new ClasspathLabelProvider());
}
/**
 * Creates and returns a tab item that contains this widget group.
 */
public TabItem createTabItem(TabFolder folder) {
	TabItem item = new TabItem(folder, SWT.NONE);
	item.setText(Policy.bind("preferences.customize.classpathPageTitle"));
//	item.setImage(imageRegistry.get(JavaPluginImages.IMG_OBJS_PACKFRAG_ROOT));
	item.setData(this);
	item.setControl(createControl(folder));
	return item;
}
protected void defaultButtonPressed() {
}
/**
 * Returns the currently listed set of URLs.  Returns null
 * if this widget has not yet been created or has been disposed.
 */
public URL[] getURLs() {
	if (jarTable == null || jarTable.getControl().isDisposed())
		return null;
	return (URL[]) elements.toArray(new URL[elements.size()]);
}
	
protected void removeButtonPressed() {
	IStructuredSelection selection = (IStructuredSelection)jarTable.getSelection();
	jarTable.remove(selection.toArray());
}
/**
 * Sets the currently listed set of URLs.  Has no effect
 * if this widget has not yet been created or has been disposed.
 */
public void setURLs(URL[] urls) {
	if (jarTable == null || jarTable.getControl().isDisposed())
		return;
	jarTable.setInput(urls);
}
}