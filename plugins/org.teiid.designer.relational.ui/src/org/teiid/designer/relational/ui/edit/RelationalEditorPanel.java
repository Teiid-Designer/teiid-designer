/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.edit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.teiid.designer.relational.model.RelationalReference;

/**
 * @since 8.0
 */
public abstract class RelationalEditorPanel {
	RelationalReference relationalObject;
	IFile modelFile;
	
	IStatus currentStatus;
	
	IDialogStatusListener statusListener;

	/**
	 * @param parent the parent panel
	 * @param relationalObject the relational object
	 * @param modelFile the relational model file
	 * @param statusListener the dialog status listener
	 */
	public RelationalEditorPanel(Composite parent, RelationalReference relationalObject, IFile modelFile, IDialogStatusListener statusListener) {
		super();
		this.relationalObject = relationalObject;
		this.modelFile = modelFile;
		this.statusListener = statusListener;
		
		createPanel(parent);
		
		synchronizeUI();
	}
	
	protected abstract void createPanel(Composite parent);
	
	protected abstract void synchronizeUI();

	protected void validate() {
		
	}
	
	protected void setStatus(IStatus status) {
		currentStatus = status;
		
		statusListener.notifyStatusChanged(currentStatus);
	}
	
	/**
	 * @return the relational object reference
	 */
	public final RelationalReference getRelationalReference() {
		return this.relationalObject;
	}
}