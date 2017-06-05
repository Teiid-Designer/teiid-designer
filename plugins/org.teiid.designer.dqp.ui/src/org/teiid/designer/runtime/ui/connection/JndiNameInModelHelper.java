
/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;

public class JndiNameInModelHelper {
    private static final String DIALOG_TITLE = DqpUiConstants.UTIL.getString("EnterDataSourceJNDINameDialog.title"); //$NON-NLS-1$
    private static final String JNDI_PREFIX = "java:/";  //$NON-NLS-1$
    
	private ConnectionInfoHelper connectionInfoHelper;

	public JndiNameInModelHelper() {
		this.connectionInfoHelper = new ConnectionInfoHelper();
	}

	public String getExistingJndiName(ModelResource mr) {
		String existingName = connectionInfoHelper.getJndiProperty(mr);
        // Strip off "java:/"
        String nameOnly = StringConstants.EMPTY_STRING;

        if( !StringUtilities.isEmpty(existingName) ) {
        	nameOnly = existingName;
//        	if( existingName.startsWith(JNDI_PREFIX) ) {
//        		nameOnly = existingName.substring(6);
//        	}
        }
        
        return nameOnly;
	}

	public void setJNDINameInTxn(ModelResource modelResource, String newJNDIName) {
		String jndiName = newJNDIName;
		
		boolean requiredStart = ModelerCore.startTxn(true, true, "Set Data Source JNDI Name", this); //$NON-NLS-1$
		boolean succeeded = false;
		try {
			ModelEditor editor = ModelEditorManager
					.getModelEditorForFile((IFile) modelResource.getCorrespondingResource(), true);
			if (editor != null) {
				boolean isDirty = editor.isDirty();

				if( !jndiName.startsWith(JNDI_PREFIX)) jndiName = JNDI_PREFIX + jndiName;
				
				connectionInfoHelper.setJNDIName(modelResource, jndiName);

				if (!isDirty && editor.isDirty()) {
					editor.doSave(new NullProgressMonitor());
				}
				succeeded = true;
			}
		} catch (Exception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					DqpUiConstants.UTIL.getString("SetConnectionInfo.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
			IStatus status = new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID,
					DqpUiConstants.UTIL.getString("SetConnectionInfo.exceptionMessage"), e); //$NON-NLS-1$
			DqpUiConstants.UTIL.log(status);

			return;
		} finally {
			// if we started the txn, commit it.
			if (requiredStart) {
				if (succeeded) {
					ModelerCore.commitTxn();
				} else {
					ModelerCore.rollbackTxn();
				}
			}
		}
	}
	
	/**
	 * @param mr
	 * @return true if JNDI name was changed
	 */
	public boolean ensureJndiNameExists(ModelResource mr, boolean queryOnlyIfEmpty) {
		String existingName = getExistingJndiName(mr);
		
		if( !queryOnlyIfEmpty || (queryOnlyIfEmpty && StringUtilities.isEmpty(existingName)) ) {
			String newJNDIName = queryUserForJNDIName(existingName);
	        
			if( StringUtilities.isEmpty(newJNDIName) ) {
				setJNDINameInTxn(mr, null);
			} else if( existingName == null || !existingName.equals(newJNDIName)) {
	        	setJNDINameInTxn(mr, newJNDIName);
	        	return true;
	        }
		}
        
        return false;
	}
	
    private String queryUserForJNDIName( String existingName ) {

    	EnterDataSourceJNDINameDialog dialog = new EnterDataSourceJNDINameDialog(Display.getCurrent().getActiveShell(), existingName);

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            return dialog.getTranslatorName();
        }

        return existingName;
    }
    
    class EnterDataSourceJNDINameDialog extends Dialog {
        //============================================================================================================================
        // Constants
        private static final int COLUMN_COUNT = 2;

        //============================================================================================================================
        // Variables
        
        private Text jndiNameField;
        
        private String jndiName;

        //============================================================================================================================
        // Constructors
            
        /**<p>
         * </p>
         * @param parent
         * @param title
         * @since 4.0
         */
        public EnterDataSourceJNDINameDialog(final Shell shell, final String existingName) {
            super(shell, DIALOG_TITLE);
            this.jndiName = existingName;
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
            GridData pgd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
            pgd.minimumWidth = 400;
            pgd.grabExcessHorizontalSpace = true;
            dlgPanel.setLayoutData(pgd);
            ((GridLayout)dlgPanel.getLayout()).numColumns = COLUMN_COUNT;
            
            final String message = DqpUiConstants.UTIL.getString("EnterDataSourceJNDINameDialog.message"); //$NON-NLS-1$

            final Label msgLabel = WidgetFactory.createLabel(dlgPanel, message);
            GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
            gd.horizontalSpan = 2;
            msgLabel.setLayoutData(gd);
            
            WidgetFactory.createLabel(dlgPanel, DqpUiConstants.UTIL.getString("EnterDataSourceJNDINameDialog.nameLabel") + StringConstants.SPACE); //$NON-NLS-1$
            this.jndiNameField = WidgetFactory.createTextField(dlgPanel);
            if( this.jndiName != null && this.jndiName.length() > 0 ) {
            	this.jndiNameField.setText(this.jndiName);
            }
            this.jndiNameField.addModifyListener(new ModifyListener() {
    			
    			@Override
    			public void modifyText(ModifyEvent e) {
    				if( jndiNameField.getText() != null && jndiNameField.getText().length() > 0 ) {
    					jndiName = jndiNameField.getText();
    					msgLabel.setText(message);
    					msgLabel.setImage(null);
    				} else {
    					jndiName = ""; //$NON-NLS-1$
    					msgLabel.setText("Warning: JNDI name is empty");
    					msgLabel.setImage(DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.WARNING_ICON));
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
            getButton(IDialogConstants.OK_ID).setEnabled(this.jndiName != null);
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
    	public String getTranslatorName() {
    		return jndiName;
    	}
        
        
    }

}
