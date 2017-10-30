package org.teiid.designer.ui.util;

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
import org.teiid.designer.core.util.JndiUtil;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;

public class JndiNameHelper  extends StringNameValidator {
    private static final String DIALOG_TITLE = UiConstants.Util.getString("EnterDataSourceJNDINameDialog.title"); //$NON-NLS-1$
    
	private ConnectionInfoHelper connectionInfoHelper;
	
	private boolean addPrefix = true;
	
	public JndiNameHelper() {
		this(StringNameValidator.DEFAULT_MINIMUM_LENGTH, StringNameValidator.DEFAULT_MAXIMUM_LENGTH);
	}
	
	public JndiNameHelper(boolean addPrefix) {
		this(StringNameValidator.DEFAULT_MINIMUM_LENGTH, StringNameValidator.DEFAULT_MAXIMUM_LENGTH);
		this.addPrefix = addPrefix;
	}
	
    public JndiNameHelper( int minLength,
                                    int maxLength ) {
        super(minLength, maxLength, new char[] {UNDERSCORE_CHARACTER, '-', '.', ':', '/'});
        
        this.connectionInfoHelper = new ConnectionInfoHelper();
    }

    @Override
    public String getValidNonLetterOrDigitMessageSuffix() {
        return UiConstants.Util.getString("JndiNameHelper.or_other_valid_characters"); //$NON-NLS-1$
    }
    @Override
    public String checkValidName( final String name ) {
    	return checkValidName(name, addPrefix);
    }
    
    protected String checkValidName(final String name, boolean addPrefix) {
    	String msg = super.checkValidName(name);
    	
    	// One last check for java prefix
    	if( msg == null ) {
    		if( !JndiUtil.hasJavaPrefix(name) ) {
    			return UiConstants.Util.getString("JndiNameHelper.jndiPrefixErrorMessage"); //$NON-NLS-1$
    		}
    		String shortName = JndiUtil.removeJavaPrefix(name);
    		if( StringUtilities.isEmpty(shortName) ) {
    			return UiConstants.Util.getString("JndiNameHelper.jndiEmptyMessage"); //$NON-NLS-1$
    		}
    	} else {
    		if( addPrefix ) {
    			String prefix = UiConstants.Util.getString("JndiNameHelper.errorMessagesPrefix"); //$NON-NLS-1$
    			msg = prefix + msg;
    		}
    	}
    	
    	return msg;
    }
    
	public String getExistingJndiName(ModelResource mr) {
		String existingName = connectionInfoHelper.getJndiProperty(mr);

        return existingName;
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

				jndiName = JndiUtil.addJavaPrefix(jndiName);
				
				connectionInfoHelper.setJNDIName(modelResource, jndiName);

				if (!isDirty && editor.isDirty()) {
					editor.doSave(new NullProgressMonitor());
				}
				succeeded = true;
			}
		} catch (Exception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					UiConstants.Util.getString("SetConnectionInfo.exceptionMessage"), e.getMessage()); //$NON-NLS-1$
			IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID,
					UiConstants.Util.getString("SetConnectionInfo.exceptionMessage"), e); //$NON-NLS-1$
			UiConstants.Util.log(status);

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
	        	setJNDINameInTxn(mr, /*JNDI_PREFIX +*/ newJNDIName);
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
            
            final String message = UiConstants.Util.getString("EnterDataSourceJNDINameDialog.message"); //$NON-NLS-1$

            final Label msgLabel = WidgetFactory.createLabel(dlgPanel, message);
            GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
            gd.horizontalSpan = 2;
            msgLabel.setLayoutData(gd);
            
            WidgetFactory.createLabel(dlgPanel, UiConstants.Util.getString("EnterDataSourceJNDINameDialog.nameLabel") + StringConstants.SPACE); //$NON-NLS-1$
            this.jndiNameField = WidgetFactory.createTextField(dlgPanel);
            if( this.jndiName != null && this.jndiName.length() > 0 ) {
            	this.jndiNameField.setText(this.jndiName);
            }
            this.jndiNameField.addModifyListener(new ModifyListener() {
    			
    			@Override
    			public void modifyText(ModifyEvent e) {
    				String name = jndiNameField.getText();
    				String status = validate(name);
    				if( StringUtilities.isEmpty(status)) {
    					jndiName = jndiNameField.getText();
    					msgLabel.setText(message);
    					msgLabel.setImage(null);
    					enable(true);
    				} else {
    					msgLabel.setText(status);
    					msgLabel.setImage(UiPlugin.getDefault().getImage(UiPlugin.Images.ERROR_ICON));
    					enable(false);
    				}
    				
    			}
    		});
            

            return dlgPanel;
        }
        
        private void enable(boolean enable) {
        	getButton(IDialogConstants.OK_ID).setEnabled(enable);
        }
        
        private String validate(String name) {
        	// validate the JNDI name and return message, can be null
        	return JndiNameHelper.this.checkValidName(name, false);
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
