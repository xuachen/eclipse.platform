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
package org.eclipse.update.internal.configuration;
 
import java.io.*;
import java.util.*;

import org.eclipse.update.configuration.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.internal.core.*;

public class ConfigurationActivity extends ModelObject implements IActivity, IWritable{
	
	private String label;
	private int action;
	private Date date;
	private int status;
	private InstallConfiguration installConfiguration;
	

	/**
	 * Constructor for ConfigurationActivityModel.
	 */
	public ConfigurationActivity() {
		super();
	}

	/**
	 * Constructor with action
	 */
	public ConfigurationActivity(int action) {
		super();
		setAction(action);
		setStatus(STATUS_NOK);
	}
	
	/**
	 * @since 2.0
	 */
	public int getAction() {
		return action;
	}

	/**
	 * @since 2.0
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @since 2.0
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets the date.
	 * @param date The date to set
	 */
	public void setDate(Date date) {
		assertIsWriteable();
		this.date = date;
	}

	/**
	 * Sets the status.
	 * @param status The status to set
	 */
	public void setStatus(int status) {
		assertIsWriteable();
		this.status = status;
	}

	/**
	 * @since 2.0
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 * @param label The label to set
	 */
	public void setLabel(String label) {
		assertIsWriteable();
		this.label = label;
	}

	/**
	 * Sets the action.
	 * @param action The action to set
	 */
	public void setAction(int action) {
		assertIsWriteable();
		this.action = action;
	}

	/**
	 * Gets the installConfiguration.
	 * @return Returns a InstallConfigurationModel
	 */
	public InstallConfigurationModel getInstallConfigurationModel() {
		return installConfiguration;
	}

	/**
	 * Sets the installConfiguration.
	 * @param installConfiguration The installConfiguration to set
	 */
	public void setInstallConfiguration(InstallConfiguration installConfiguration) {
		assertIsWriteable();		
		this.installConfiguration = installConfiguration;
	}

	/*
	 * @see IWritable#write(int, PrintWriter)
	 */
	public void write(int indent, PrintWriter w) {
		String gap= ""; //$NON-NLS-1$
		for (int i= 0; i < indent; i++)
			gap += " "; //$NON-NLS-1$
		String increment= ""; //$NON-NLS-1$
		for (int i= 0; i < IWritable.INDENT; i++)
			increment += " "; //$NON-NLS-1$
			
		// ACTIVITY	
		w.print(gap + "<" + InstallConfigurationParser.ACTIVITY + " ");
		//$NON-NLS-1$ //$NON-NLS-2$
		w.println("action=\"" + getAction() + "\" "); //$NON-NLS-1$ //$NON-NLS-2$
		if (getLabel() != null) {
			w.println(gap + increment+ "label=\"" + UpdateManagerUtils.Writer.xmlSafe(getLabel()) + "\" ");
			//$NON-NLS-1$ //$NON-NLS-2$
		}
		w.println(gap + increment+"date=\"" + getDate().getTime() + "\" ");
		//$NON-NLS-1$ //$NON-NLS-2$
		w.println(gap + increment+"status=\"" + getStatus() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$

		// end
		w.println(gap + "</" + InstallConfigurationParser.ACTIVITY + ">");
		//$NON-NLS-1$ //$NON-NLS-2$
		w.println(""); //$NON-NLS-1$		
	}
	
	/*
	 * @see IActivity#getInstallConfiguration()
	 */
	public IInstallConfiguration getInstallConfiguration() {
		return installConfiguration;
	}
}

