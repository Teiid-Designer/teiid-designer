/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.relational.model.RelationalReference;

import com.metamatrix.modeler.transformation.model.RelationalViewProcedure;
import com.metamatrix.modeler.transformation.ui.Messages;

public class EditViewProcedureDialog extends TitleAreaDialog implements IDialogStatusListener {

    private final String TITLE = Messages.createRelationalViewProcedureTitle;
    
    private RelationalReference relationalObject;
    private IFile modelFile;

    /*
     * Constructor
     */
    public EditViewProcedureDialog(Shell parentShell, RelationalReference relationalObject, IFile file) {
        super(parentShell);
        this.setTitle(TITLE);
        this.relationalObject = relationalObject;
        this.modelFile = file;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(TITLE);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainPanel = (Composite)super.createDialogArea(parent);
        ((GridLayout)mainPanel.getLayout()).marginHeight=10;
        ((GridLayout)mainPanel.getLayout()).marginWidth=10;
        this.setTitle(TITLE);
        this.setMessage(Messages.createRelationalViewInitialMessage);
        
        new ViewProcedureEditorPanel(parent, (RelationalViewProcedure)relationalObject, modelFile, this);

        return mainPanel;
    }
    
    public void notifyStatusChanged(IStatus status) {
        Button okButton = getButton(IDialogConstants.OK_ID);
    	if( status.isOK() ) {
    		setErrorMessage(null);
    		setMessage(Messages.validationOkCreateObject);
            if (okButton != null) okButton.setEnabled(true);
    	} else {
    		if( status.getSeverity() == IStatus.WARNING ) {
    			setErrorMessage(null);
    			setMessage(status.getMessage(), IMessageProvider.WARNING);
                if (okButton != null) okButton.setEnabled(true);
    		} else {
    			setErrorMessage(status.getMessage());
                if (okButton != null) okButton.setEnabled(false);
    		}
    	}
    }
}