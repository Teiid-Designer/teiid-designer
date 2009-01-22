/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.widget.ExtendedTitleAreaDialog;


/** 
 * Dialog provides a mechanism for use to select Element symbols to be used to bind to target attributes in the ReconcilerPanel
 * @since 5.0
 */
public class AddSqlSymbolsDialog extends ExtendedTitleAreaDialog {
    private String DIALOG_TITLE   = UiConstants.Util.getString("AddSqlSymbolsDialog.dialogTitle"); //$NON-NLS-1$
    private String HEADER_TITLE   = UiConstants.Util.getString("AddSqlSymbolsDialog.headerTitle"); //$NON-NLS-1$
    private String HEADER_MESSAGE = UiConstants.Util.getString("AddSqlSymbolsDialog.headerMessage"); //$NON-NLS-1$
    //=============================================================
    // Instance variables
    //=============================================================
    private AddSqlSymbolsPanel panel;
    private String dialogTitle = DIALOG_TITLE;
    private boolean cancelled = false;
    private List availableSymbols = Collections.EMPTY_LIST;
    private List selectedSymbols = Collections.EMPTY_LIST;

    /** 
     * 
     * @since 5.0
     */
    public AddSqlSymbolsDialog(Shell parent,  List availableSymbols) {
        super(parent, UiPlugin.getDefault());
        this.availableSymbols = new ArrayList(availableSymbols);
    }

    /**
     *  
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.0
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        panel = new AddSqlSymbolsPanel(composite, availableSymbols);
        setDialogTitle(dialogTitle);
        setTitle(HEADER_TITLE);
        setMessage(HEADER_MESSAGE);
        getShell().setMinimumSize(500, 500);
        return composite;
    }
    
    /**
     *  
     * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
     * @since 5.0
     */
    @Override
    protected void cancelPressed() {
        cancelled = true;
        super.cancelPressed();
    }
    
    /**
     *  
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     * @since 5.0
     */
    @Override
    protected void okPressed() {
        selectedSymbols = panel.getSelectedSymbols();
        super.okPressed();
    }
    /**<p>
     * </p>
     * @since 4.0
     */
    public String getDialogTitle() {
        final Shell shell = getShell();
        return (shell == null ? this.dialogTitle : getShell().getText());
    }
    
    /**<p>
     * </p>
     * @since 4.0
     */
    public void setDialogTitle(final String title) {
        ArgCheck.isNotNull(title);
        final Shell shell = getShell();
        if (shell == null) {
            this.dialogTitle = title;
        } else {
            shell.setText(title);
        }
    }

    /**
     * Return boolean indicating whether or not "Cancel" was pressed
     * @return  true if "Cancel" was pressed
     */
    public boolean wasCancelled() {
        return cancelled;
    }
    
    /**
     *  
     * @return
     * @since 5.0
     */
    public List getSelectedSymbols() {
        return selectedSymbols;
    }
}
