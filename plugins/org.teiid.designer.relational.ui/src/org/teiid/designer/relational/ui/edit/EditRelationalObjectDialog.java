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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.relational.model.RelationalReference;
import org.teiid.designer.relational.ui.Messages;

/**
 * @since 8.0
 */
public class EditRelationalObjectDialog extends TitleAreaDialog implements IDialogStatusListener {

    private final String TITLE = Messages.createRelationalTableTitle;
    
    private RelationalReference relationalObject;
    private IFile modelFile;

    private RelationalEditorPanel editorPanel;
    
    /**
     * @param parentShell the parent shell
     * @param relationalObject the relation object to edit
     * @param file the target relational model file
     */
    public EditRelationalObjectDialog(Shell parentShell, RelationalReference relationalObject, IFile file) {
        super(parentShell);
        this.setTitle(TITLE);
        this.relationalObject = relationalObject;
        this.modelFile = file;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(RelationalObjectEditorFactory.getDialogTitle(relationalObject));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainPanel = (Composite)super.createDialogArea(parent);
        ((GridLayout)mainPanel.getLayout()).marginHeight=10;
        ((GridLayout)mainPanel.getLayout()).marginWidth=10;
        this.setTitle(RelationalObjectEditorFactory.getDialogTitle(relationalObject));
        this.setMessage(RelationalObjectEditorFactory.getInitialMessage(relationalObject));
        
        editorPanel = RelationalObjectEditorFactory.getEditorPanel(this, mainPanel, relationalObject, modelFile);
        
        return mainPanel;
    }
    
    @Override
	public void notifyStatusChanged(IStatus status) {
    	if( status.isOK() ) {
    		setErrorMessage(null);
    		setMessage(Messages.validationOkCreateObject);
    	} else {
    		if( status.getSeverity() == IStatus.WARNING ) {
    			setErrorMessage(null);
    			setMessage(status.getMessage(), IMessageProvider.WARNING);
    		} else {
    			setErrorMessage(status.getMessage());
    		}
    	}
    	
    	setOkEnabled(editorPanel.canFinish());
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		setOkEnabled(editorPanel.canFinish());
		return control;
	}
    
    private void setOkEnabled( boolean enabled ) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
}