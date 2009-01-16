/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
