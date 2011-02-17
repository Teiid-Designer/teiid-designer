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
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.Label;

public class SetTranslatorNameAction extends SortableSelectionAction {
    private static final String ACTION_TITLE = DqpUiConstants.UTIL.getString("SetTranslatorNameAction.title"); //$NON-NLS-1$
    private static final String DIALOG_TITLE = DqpUiConstants.UTIL.getString("EnterTranslatorNameDialog.title"); //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private ConnectionInfoHelper connectionInfoHelper;

    /**
     * @since 5.0
     */
    public SetTranslatorNameAction() {
        super(ACTION_TITLE, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SET_CONNECTION_ICON));
        this.connectionInfoHelper = new ConnectionInfoHelper();
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
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
        
        String existingName = connectionInfoHelper.getTranslatorName(mr);
        
        // Query User for Translator name

        String newTranslatorName = queryUserForTranslatorName(existingName);
        
        if( !existingName.equals(newTranslatorName)) {
        	setTranslatorNameInTxn(mr, newTranslatorName);
        }
    }
    
    private void setTranslatorNameInTxn(ModelResource modelResource, String translatorName) {
        boolean requiredStart = ModelerCore.startTxn(true, true, "Set Translator Name", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                connectionInfoHelper.setTranslatorName(modelResource, translatorName);

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
            		DqpUiConstants.UTIL.getString("SetConnectionProfileAction.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID,
            		DqpUiConstants.UTIL.getString("SetConnectionProfileAction.exceptionMessage"), e); //$NON-NLS-1$
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
    

    public String queryUserForTranslatorName( String existingName ) {

        EnterTranslatorNameDialog dialog = new EnterTranslatorNameDialog(Display.getCurrent().getActiveShell(), existingName);

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            return dialog.getTranslatorName();
        }

        return existingName;
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

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
    
    class EnterTranslatorNameDialog extends Dialog {
        //============================================================================================================================
        // Constants
        private static final int COLUMN_COUNT = 2;

        //============================================================================================================================
        // Variables
        
        private Text translatorField;
        
        private String translatorName;

        //============================================================================================================================
        // Constructors
            
        /**<p>
         * </p>
         * @param parent
         * @param title
         * @since 4.0
         */
        public EnterTranslatorNameDialog(final Shell shell, final String existingName) {
            super(shell, DIALOG_TITLE);
            this.translatorName = existingName;
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
            
            Label msgLabel = WidgetFactory.createLabel(dlgPanel, DqpUiConstants.UTIL.getString("EnterTranslatorNameDialog.message")); //$NON-NLS-1$
            GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
            gd.horizontalSpan = 2;
            msgLabel.setLayoutData(gd);
            
            WidgetFactory.createLabel(dlgPanel, DqpUiConstants.UTIL.getString("EnterTranslatorNameDialog.nameLabel") + SPACE); //$NON-NLS-1$
            this.translatorField = WidgetFactory.createTextField(dlgPanel);
            if( this.translatorName != null && this.translatorName.length() > 0 ) {
            	this.translatorField.setText(this.translatorName);
            }
            this.translatorField.addModifyListener(new ModifyListener() {
    			
    			@Override
    			public void modifyText(ModifyEvent e) {
    				if( translatorField.getText() != null && translatorField.getText().length() > 0 ) {
    					getButton(IDialogConstants.OK_ID).setEnabled(true);
    					translatorName = translatorField.getText();
    				} else {
    					translatorName = ""; //$NON-NLS-1$
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
    	public String getTranslatorName() {
    		return translatorName;
    	}
        
        
    }


}