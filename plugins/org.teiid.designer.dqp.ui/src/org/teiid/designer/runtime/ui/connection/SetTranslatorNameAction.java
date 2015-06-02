package org.teiid.designer.runtime.ui.connection;

import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.runtime.connection.TranslatorUtils;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
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


/**
 * @since 8.0
 */
public class SetTranslatorNameAction extends SortableSelectionAction  implements IConnectionAction {
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
    

    public String queryUserForTranslatorName( String existingName ) {

        EnterTranslatorNameDialog dialog = new EnterTranslatorNameDialog(Display.getCurrent().getActiveShell(), existingName);

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
    
    class EnterTranslatorNameDialog extends Dialog {
        //============================================================================================================================
        // Constants
        private static final int COLUMN_COUNT = 2;

        //============================================================================================================================
        // Variables
        
        private Text translatorField;
        
        private String translatorName;
        
        private Collection<String> translatorNames = new ArrayList<String>();
        private Combo translatorNameCombo;

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
        	ITeiidServer server = TranslatorUtils.getDefaultServer();
        	boolean serverAvailable =  (server != null && server.isConnected());
        	
            final Composite dlgPanel = (Composite)super.createDialogArea(parent);
            GridData pgd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
            pgd.minimumWidth = 400;
            pgd.grabExcessHorizontalSpace = true;
            dlgPanel.setLayoutData(pgd);
            ((GridLayout)dlgPanel.getLayout()).numColumns = COLUMN_COUNT;
            
            String message = DqpUiConstants.UTIL.getString("EnterTranslatorNameDialog.message"); //$NON-NLS-1$
            if( serverAvailable ) {
            	message = DqpUiConstants.UTIL.getString("EnterTranslatorNameDialog.selectTranslatorMessage"); //$NON-NLS-1$
            }
            Label msgLabel = WidgetFactory.createLabel(dlgPanel, message);
            GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
            gd.horizontalSpan = 2;
            msgLabel.setLayoutData(gd);
            
            
            if( !serverAvailable ) {
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
            } else {
            	// -------------------------------------
                // Combo for Translator selection
                // -------------------------------------

                Label translatorLabel = new Label(dlgPanel,SWT.NONE);
                translatorLabel.setText("Translator");
                
                /*
                 * Refresh the list of currently available translators on the server
                 */
                try {
                    translatorNames.clear();
                    Collection<ITeiidTranslator> availableTranslators = TranslatorUtils.getTranslators();
                    for(ITeiidTranslator translator: availableTranslators) {
                        translatorNames.add(translator.getName());
                    }
                } catch (Exception ex) {
                    translatorNames.clear();
                    DqpUiPlugin.UTIL.log(ex);
                }
                
                this.translatorNameCombo = WidgetFactory.createCombo(dlgPanel,
                                                                         SWT.READ_ONLY,
                                                                         GridData.FILL_HORIZONTAL,
                                                                         translatorNames.toArray());
				this.translatorNameCombo.setVisibleItemCount(8);
				this.translatorNameCombo.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
			            int selIndex = translatorNameCombo.getSelectionIndex();
			            translatorName = translatorNameCombo.getItem(selIndex);
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
		    	if( translatorName != null ) {
		            // walk through the metamodel classes and select the matching type
		            String[] names = translatorNameCombo.getItems();
		            for (int i = 0; i < names.length; i++) {
		                if (names[i].equalsIgnoreCase(translatorName)) {
		                	translatorNameCombo.select(i);
		                    break;
		                }
		            }
		           
		    	} else {
		    		translatorNameCombo.select(-1);
		    	}
            }

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
            getButton(IDialogConstants.OK_ID).setEnabled(this.translatorName != null);
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