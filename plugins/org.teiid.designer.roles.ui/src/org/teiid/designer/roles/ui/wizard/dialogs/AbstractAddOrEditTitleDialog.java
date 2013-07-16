/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple abstract base class for a <code>TitleAreaDialog</code> 
 */
public abstract class AbstractAddOrEditTitleDialog extends TitleAreaDialog {

	private String title;
	private String initialMessage;
	private boolean okEnabled;
	

    /**
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingPropertyNames the existing property names (can be <code>null</code>)
     */
    public AbstractAddOrEditTitleDialog( Shell parentShell, String title, String message, boolean okEnabled) {
        super(parentShell);
        this.title = title;
        this.initialMessage = message;
        this.okEnabled = okEnabled;
    }
    
    /*
     * 
     */
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(this.title);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#setShellStyle(int)
     */
     @Override
     protected void setShellStyle( int newShellStyle ) {
         super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

     }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent ) {
        setTitle(this.title);
        setMessage(initialMessage);
    	
        final Composite outerPanel = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(outerPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(outerPanel);
        
        createCustomArea(outerPanel);

        return outerPanel;
    }
    
    protected abstract void createCustomArea(Composite outerPanel);
    
    protected abstract void handleInputChanged();
    
    protected abstract void validate();

    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(this.okEnabled);
    }
}
