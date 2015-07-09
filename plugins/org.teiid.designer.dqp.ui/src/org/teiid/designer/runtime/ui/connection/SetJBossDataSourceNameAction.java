/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
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
import org.teiid.designer.ui.actions.IConnectionAction;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class SetJBossDataSourceNameAction extends SortableSelectionAction  implements IConnectionAction {
    private static final String ACTION_TITLE = DqpUiConstants.UTIL.getString("SetJBossDataSourceNameAction.title"); //$NON-NLS-1$
    private static final String DIALOG_TITLE = DqpUiConstants.UTIL.getString("EnterDataSourceJNDINameDialog.title"); //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String JNDI_PREFIX = "java:/";  //$NON-NLS-1$
    private ConnectionInfoHelper connectionInfoHelper;

    /**
     * @since 5.0
     */
    public SetJBossDataSourceNameAction() {
        super(ACTION_TITLE, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SET_CONNECTION_ICON));
        this.connectionInfoHelper = new ConnectionInfoHelper();
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return sourceModelSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        // A) get the selected model and extract a "ConnectionProfileInfo" from it using the ConnectionProfileInfoHandler

        // B) Use ConnectionProfileHandler.getConnectionProfile(connectionProfileInfo) to query the user to
        // select a ConnectionProfile (or create new one)

        // C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
        // via the ConnectionProfileInfoHandler
        IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
        ModelResource mr = ModelUtilities.getModelResourceForIFile(modelFile, true);
        
        String existingName = connectionInfoHelper.getJndiProperty(mr);
        
        // Strip off "java:/"
        String nameOnly = StringConstants.EMPTY_STRING;

        if( !StringUtilities.isEmpty(existingName) ) {
        	nameOnly = existingName;
        	if( existingName.startsWith(JNDI_PREFIX) ) {
        		nameOnly = existingName.substring(6);
        	}
        }
        
        // Query User for Translator name

        String newJNDIName = queryUserForJNDIName(nameOnly);
        
        if( existingName == null || !existingName.equals(newJNDIName)) {
        	setJNDINameInTxn(mr, JNDI_PREFIX + newJNDIName);
        }
    }
    
    private void setJNDINameInTxn(ModelResource modelResource, String newJNDIName) {
        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Data Source JNDI Name", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                connectionInfoHelper.setJNDIName(modelResource, newJNDIName);

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
    

    public String queryUserForJNDIName( String existingName ) {

    	EnterDataSourceJNDINameDialog dialog = new EnterDataSourceJNDINameDialog(Display.getCurrent().getActiveShell(), existingName);

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            return dialog.getTranslatorName();
        }

        return existingName;
    }

    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    @SuppressWarnings("rawtypes")
	private boolean sourceModelSelected( ISelection theSelection ) {
        boolean result = false;

		List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalSourceModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
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
            
            String message = DqpUiConstants.UTIL.getString("EnterDataSourceJNDINameDialog.message"); //$NON-NLS-1$

            Label msgLabel = WidgetFactory.createLabel(dlgPanel, message);
            GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
            gd.horizontalSpan = 2;
            msgLabel.setLayoutData(gd);
            
            WidgetFactory.createLabel(dlgPanel, DqpUiConstants.UTIL.getString("EnterDataSourceJNDINameDialog.nameLabel") + SPACE); //$NON-NLS-1$
            this.jndiNameField = WidgetFactory.createTextField(dlgPanel);
            if( this.jndiName != null && this.jndiName.length() > 0 ) {
            	this.jndiNameField.setText(this.jndiName);
            }
            this.jndiNameField.addModifyListener(new ModifyListener() {
    			
    			@Override
    			public void modifyText(ModifyEvent e) {
    				if( jndiNameField.getText() != null && jndiNameField.getText().length() > 0 ) {
    					getButton(IDialogConstants.OK_ID).setEnabled(true);
    					jndiName = jndiNameField.getText();
    				} else {
    					jndiName = ""; //$NON-NLS-1$
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