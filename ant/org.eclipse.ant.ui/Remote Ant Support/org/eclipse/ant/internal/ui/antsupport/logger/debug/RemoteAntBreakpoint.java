/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ant.internal.ui.antsupport.logger.debug;

import org.apache.tools.ant.Location;

public class RemoteAntBreakpoint {
	
	private int fLineNumber;
	private String fFileName;
	
	public RemoteAntBreakpoint(String breakpointRepresentation) {
		String[] data= breakpointRepresentation.split(DebugMessageIds.MESSAGE_DELIMITER);
		String fileName= data[1];
		String lineNumber= data[2];
		fFileName= fileName;
		fLineNumber= Integer.parseInt(lineNumber);
	}

	public boolean isAt(Location location) {
		return fFileName.equals(location.getFileName()) && fLineNumber == location.getLineNumber();
	}
	
	public String toMarshallString() {
		StringBuffer buffer= new StringBuffer(DebugMessageIds.BREAKPOINT);
		buffer.append(DebugMessageIds.MESSAGE_DELIMITER);
		buffer.append(fFileName);
		buffer.append(DebugMessageIds.MESSAGE_DELIMITER);
		buffer.append(fLineNumber);
		return buffer.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof RemoteAntBreakpoint)) {
			return false;
		}
		RemoteAntBreakpoint other= (RemoteAntBreakpoint) obj;
		return other.getFileName().equals(fFileName) && other.getLineNumber() == fLineNumber;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return fFileName.hashCode() + fLineNumber;
	}
	
	public int getLineNumber() {
		return fLineNumber;
	}
	
	/**
	 * @return Returns the fFileName.
	 */
	public String getFileName() {
		return fFileName;
	}
}