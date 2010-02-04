/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.preferences.IEditorPreferencesValidationListener;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * @author SDelap
 * Wraps the TableEditorPreferencesComponent in a dialog for popup use.
 */
public class TableEditorPreferencesDialog extends Dialog implements IEditorPreferencesValidationListener {
    
    private TableEditorPreferencesComponent tableEditorPreferencesComponent;
    private CLabel messageLabel;    
    
    public TableEditorPreferencesDialog(Shell shell) {
        super(shell, UiPlugin.getDefault().getPluginUtil().getString("com.metamatrix.modeler.internal.ui.actions.EditTableEditorPreferencesAction.text")); //$NON-NLS-1$
        this.tableEditorPreferencesComponent = new TableEditorPreferencesComponent();
        this.tableEditorPreferencesComponent.addValidationListener(this);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        c.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        c.setLayoutData(gridData);
        
        this.tableEditorPreferencesComponent.createEditorPreferencesComponent(c);

        this.messageLabel = WidgetFactory.createLabel(c);
        this.messageLabel.setText(" "); //$NON-NLS-1$
        GridData messageData = new GridData(GridData.FILL_BOTH);
        messageData.grabExcessHorizontalSpace = true;
        messageData.grabExcessVerticalSpace = true;
        this.messageLabel.setLayoutData(messageData);
        return c;
    }
    
    

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        TableEditorPreferencesDialog.this.tableEditorPreferencesComponent.performOk();
        this.close();
    }
    
    public void validateDialog() {
        this.tableEditorPreferencesComponent.validate();
    }
    
    public void validationStatus(boolean status, String message) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(status);
        	if (message == null) {
            this.messageLabel.setImage(null);
        	} else {
            	Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            	this.messageLabel.setImage(image);
        	}
        	this.messageLabel.setText(message);
        	this.messageLabel.redraw();
        }
    }
}
