/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.edit.IDialogStatusListener;
import org.teiid.designer.relational.ui.edit.RelationalEditorPanel;

/**
 *
 */
public class EditRelationalObjectDialog extends TitleAreaDialog implements IDialogStatusListener {

    private final EditRelationalObjectDialogModel dialogModel;

    private RelationalEditorPanel editorPanel;
    
    /**
     * @param parentShell the parent shell
     * @param dialogModel containing the relational object to edit
     */
    public EditRelationalObjectDialog(Shell parentShell, EditRelationalObjectDialogModel dialogModel) {
        super(parentShell);
        this.dialogModel = dialogModel;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * @see org.eclipse.jface.window.Window#constrainShellSize()
     */
    @Override
    protected void constrainShellSize() {
        super.constrainShellSize();

        final Shell shell = getShell();
        shell.setText(dialogModel.getDialogTitle());

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
        Composite pnlOuter = (Composite) super.createDialogArea(parent);
        
        Composite mainPanel = new Composite(pnlOuter, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(20, 20).applyTo(mainPanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainPanel);

        this.setTitle(dialogModel.getDialogTitle());
        this.setMessage(dialogModel.getHelpText());

        editorPanel = dialogModel.getEditorPanel(this, mainPanel);

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
