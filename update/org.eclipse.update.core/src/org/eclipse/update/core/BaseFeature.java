
// NOTE: this is a placeholder for the eventual common Feature implementation.
//       This class will eventually be renamed to Feature and 
//       will implement IFeature

package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */  

import org.eclipse.core.runtime.IProgressMonitor;

public class BaseFeature {
	
	/**
	 * Delegating wrapper for IProgressMonitor used for feature
	 * installation handling.
	 * 
	 * NOTE: currently is just a straight delegating wrapper.
	 *       Extended handling function TBA 
	 * 
	 * @since 2.0
	 */	
	public static class ProgressMonitor implements IProgressMonitor {
		
		private IProgressMonitor monitor;
		
		public ProgressMonitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}
		/*
		 * @see IProgressMonitor#beginTask(String, int)
		 */
		public void beginTask(String name, int totalWork) {
			monitor.beginTask(name, totalWork);
		}

			/*
		 * @see IProgressMonitor#done()
		 */
		public void done() {
			monitor.done();
		}

		/*
		 * @see IProgressMonitor#internalWorked(double)
		 */
		public void internalWorked(double work) {
			monitor.internalWorked(work);
		}

		/*
		 * @see IProgressMonitor#isCanceled()
		 */
		public boolean isCanceled() {
			return monitor.isCanceled();
		}

		/*
		 * @see IProgressMonitor#setCanceled(boolean)
		 */
		public void setCanceled(boolean value) {
			monitor.setCanceled(value);
		}

		/*
		 * @see IProgressMonitor#setTaskName(String)
		 */
		public void setTaskName(String name) {
			monitor.setTaskName(name);
		}

		/*
		 * @see IProgressMonitor#subTask(String)
		 */
		public void subTask(String name) {
			monitor.subTask(name);
		}

		/*
		 * @see IProgressMonitor#worked(int)
		 */
		public void worked(int work) {
			monitor.worked(work);
		}
	}
}
