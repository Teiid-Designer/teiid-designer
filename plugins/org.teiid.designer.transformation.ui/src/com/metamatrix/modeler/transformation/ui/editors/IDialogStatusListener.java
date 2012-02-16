/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.editors;

import org.eclipse.core.runtime.IStatus;

/*
 * IDialogStatusListener interface
 */
public interface IDialogStatusListener {
	
	void notifyStatusChanged(IStatus status);
}
