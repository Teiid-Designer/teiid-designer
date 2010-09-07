/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.Label;

/**
 * 
 */
public class PreviewMissingPasswordDialog extends Dialog implements InternalUiConstants.Widgets {
    //============================================================================================================================
    // Constants
	
	private static final String TITLE = DqpUiConstants.UTIL.getString("PreviewMissingPasswordDialog.title"); //$NON-NLS-1$
    
    private static final int COLUMN_COUNT = 2;

    //============================================================================================================================
    // Variables
    
    private Text pwdFld;
    
    private String pwd;
    
    private String message;

    //============================================================================================================================
    // Constructors
        
    /**<p>
     * </p>
     * @param parent
     * @param title
     * @since 4.0
     */
    public PreviewMissingPasswordDialog(final Shell shell, String message) {
        super(shell, TITLE);
        this.message = message;
    }
    
    //============================================================================================================================
    // Overridden Methods

    /**<p>
     * </p>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite dlgPanel = (Composite)super.createDialogArea(parent);
        ((GridLayout)dlgPanel.getLayout()).numColumns = COLUMN_COUNT;
        
        Label msgLabel = WidgetFactory.createLabel(dlgPanel, this.message);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        msgLabel.setLayoutData(gd);
        
        WidgetFactory.createLabel(dlgPanel, PASSWORD_LABEL);
        this.pwdFld = WidgetFactory.createPasswordField(dlgPanel);
        
        this.pwdFld.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if( pwdFld.getText() != null && pwdFld.getText().length() > 0 ) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					pwd = pwdFld.getText();
				} else {
					pwd = ""; //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				
			}
		});

        return dlgPanel;
    }
    
    /**<p>
     * </p>
     * @see org.eclipse.jface.window.Window#create()
     * @since 4.0
     */
    @Override
    public void create() {
        super.create();
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }
    
    /**<p>
     * </p>
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 4.0
     */
    @Override
    protected void okPressed() {
    	//pwd = pwdFld.getText();
        super.okPressed();
    }

	/**
	 * @return password
	 */
	public String getPassword() {
		return pwd;
	}
    
    
}
