package org.teiid.designer.runtime.ui.connection.properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.ui.DqpUiConstants;

public class EditTOPropertiesDialog extends TitleAreaDialog {


    private EditTOPropertiesPanel editorPanel;

    private TranslatorOverride override;
    
    /**
     * @param parentShell the parent shell
     * @param dialogModel containing the relational object to edit
     */
    public EditTOPropertiesDialog(Shell parentShell, TranslatorOverride override) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);

        this.override = override;
    }

    /**
     * @see org.eclipse.jface.window.Window#constrainShellSize()
     */
    @Override
    protected void constrainShellSize() {
        super.constrainShellSize();

        final Shell shell = getShell();
        shell.setText(DqpUiConstants.UTIL.getString("EditTOPropertiesDialog.title")); //$NON-NLS-1$

        { // center on parent
            final Shell parentShell = (Shell)shell.getParent();
            final Rectangle parentBounds = parentShell.getBounds();
            final Point parentCenter = new Point(parentBounds.x + (parentBounds.width/2), parentBounds.y + parentBounds.height/2);

            final Rectangle r = shell.getBounds();
            final Point shellLocation = new Point(parentCenter.x - r.width/2, parentCenter.y - r.height/2);

            shell.setLocation(Math.max(0, shellLocation.x), Math.max(0, shellLocation.y));
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        
        Composite mainPanel = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(20, 20).applyTo(mainPanel);

        this.setTitle(DqpUiConstants.UTIL.getString("EditTOPropertiesDialog.title")); //$NON-NLS-1$
        this.setMessage(DqpUiConstants.UTIL.getString("EditTOPropertiesDialog.message")); //$NON-NLS-1$

        editorPanel = new EditTOPropertiesPanel(mainPanel, override);

        return mainPanel;
    }
    
    public void notifyStatusChanged(IStatus status) {
        if( status.isOK() ) {
            setErrorMessage(null);
            setMessage(DqpUiConstants.UTIL.getString("EditTOPropertiesDialog.okMessage"));  //$NON-NLS-1$
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

