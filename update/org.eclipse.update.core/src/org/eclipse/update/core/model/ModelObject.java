package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
 
import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.update.internal.core.Assert;

/**
 * An object which has the general characteristics of all elements
 * in the install/ update support.
 * <p>
 * This class cannot be instantiated and must be subclassed.
 * </p>
 */

public abstract class ModelObject {
	
	private boolean readOnly = false;
		
	/**
	 * Creates a a base model object.
	 * 
	 * @since 2.0
	 */
	protected ModelObject() {
	}
	
	/**
	 * Checks that this model object is writeable.  A runtime exception
	 * is thrown if it is not.
	 */
	protected final void assertIsWriteable() {
		Assert.isTrue(!isReadOnly(), "Model is read-only");
	}
	
	/**
	 * Sets this model object and all of its descendents to be read-only.
	 * Subclasses may extend this implementation.
	 *
	 * @see #isReadOnly
	 */
	public void markReadOnly() {
		readOnly = true;
	}
	
	/**
	 * Returns whether or not this model object is read-only.
	 * 
	 * @return <code>true</code> if this model object is read-only,
	 *		<code>false</code> otherwise
	 * @see #markReadOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * Resolve the model element. This method allows any relative URL strings
	 * to be resolved to actual URL. It also allows any translatable strings
	 * to be localized.
	 * 
	 * Subclasses need to override this method to perform the actual resolution.
	 * 
	 * @since 2.0
	 */
	public void resolve(URL base, ResourceBundle bundle) throws Exception {
		return;
	}
		
	/**
	 * Delegate setting of read-only
	 *
	 * @param o object to delegate to. Must be of type ModelObject.
	 * @see #isReadOnly
	 */
	protected void markReferenceReadOnly(Object o) {
		if (o==null)
			return;
		((ModelObject)o).markReadOnly();	
	}
		
	/**
	 * Delegate setting of read-only
	 *
	 * @param o object array to delegate to. Each element must be of type ModelObject.
	 * @see #isReadOnly
	 */
	protected void markListReferenceReadOnly(Object[] o) {
		if (o==null)
			return;
		for (int i=0; i<o.length; i++) {
			((ModelObject)o[i]).markReadOnly();
		}
	}
}
