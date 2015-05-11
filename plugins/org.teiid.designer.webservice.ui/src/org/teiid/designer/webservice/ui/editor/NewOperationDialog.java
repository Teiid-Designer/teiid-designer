package org.teiid.designer.webservice.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.ui.common.eventsupport.IDialogStatusListener;
import org.teiid.designer.ui.common.widget.ScrollableTitleAreaDialog;
import org.teiid.designer.webservice.ui.WebServiceUiPlugin;

public class NewOperationDialog extends ScrollableTitleAreaDialog  implements IDialogStatusListener  {
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(NewOperationDialog.class);
	Interface intFace;
	
	WebServiceOperation operation;
	
	NewOperationPanel editorPanel;
	
    private static String getString( final String id ) {
        return WebServiceUiPlugin.UTIL.getString(I18N_PREFIX + id);
    }

	public NewOperationDialog(Shell parentShell, Interface intFace) {
		super(parentShell);

		this.intFace =intFace;
		
		this.operation = new WebServiceOperation("new_operation", this.intFace);  //$NON-NLS-1$
		this.operation.setInputMessageName("input_msg");  //$NON-NLS-1$
		this.operation.setOutputMessageName("output_msg");  //$NON-NLS-1$
	}

	public NewOperationDialog(Shell parentShell, int nColumns) {
		super(parentShell, nColumns);
	}
	
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);
        shell.setText(getString("title"));  //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
    * @see org.eclipse.jface.window.Window#setShellStyle(int)
    */
    @Override
    protected void setShellStyle( int newShellStyle ) {
        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);
    }

    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(getString("title"));  //$NON-NLS-1$
        setMessage(getString("initialMessage")); //$NON-NLS-1$
    	
        Composite composite = (Composite)super.createDialogArea(parent);
        
        //------------------------------        
        // Set layout for the Composite
        //------------------------------        
        ((GridData)composite.getLayoutData()).grabExcessHorizontalSpace = true;
        ((GridData)composite.getLayoutData()).widthHint = 600;
        ((GridData)composite.getLayoutData()).heightHint = 400;
        
        // Create the RadioButton Group for Template selection
        editorPanel = new NewOperationPanel(composite, SWT.NONE, this.intFace, this.operation, this);

        sizeScrolledPanel();

        return composite;
    }
    
    public WebServiceOperation getOperationData() {
    	return this.operation;
    }
    
    @Override
    public void notifyStatusChanged(IStatus status) {
        if( status.isOK() ) {
            setErrorMessage(null);
            setMessage(getString("okToCreateOperation")); //$NON-NLS-1$
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
    
    
    private void setOkEnabled( boolean enabled ) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
}
