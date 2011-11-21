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

public abstract class RelationalEditorPanel {
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	RelationalReference relationalObject;
	IFile modelFile;
	
	IStatus currentStatus;
	
	IDialogStatusListener statusListener;

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
	
	public final RelationalReference getRelationalReference() {
		return this.relationalObject;
	}
}