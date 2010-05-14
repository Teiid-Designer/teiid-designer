/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.dialog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * AliasEntryDialog
 */
public class LargeDiagramDialog extends Dialog {

	//=============================================================
	// Instance variables
	//=============================================================
    LargeDiagramPanel dialogPanel;
	private String componentLimit;
	private String nComponents;
	private boolean okToDisplay = false;
        
	//=============================================================
	// Constructors
	//=============================================================
	/**
	 * AliasEntryDialog constructor.
	 * 
	 * @param parent   parent of this dialog
	 * @param transObj the transformation EObject
	 * @param title    dialog display title
	 */
	public LargeDiagramDialog(Shell parent, String title, int nComps, int compLimit) {
		super(parent,title);
		this.nComponents = "" + nComps; //$NON-NLS-1$
		this.componentLimit = "" + compLimit; //$NON-NLS-1$
	}
        
	//=============================================================
	// Instance methods
	//=============================================================

	@Override
    protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		dialogPanel = new LargeDiagramPanel(composite, nComponents, componentLimit);
		return composite;
	}
    
	@Override
    protected void okPressed() {
		okToDisplay = true;
		super.okPressed();
	}
    
	/**
	 *  Get the alias name entry
	 * @return the desired alias name
	 */
	public boolean displayAnyway() {
		return okToDisplay;
	}
    
}
