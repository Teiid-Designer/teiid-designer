/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * AliasEntryDialog
 */
public class ColumnEntryDialog extends Dialog {

    //=============================================================
    // Instance variables
    //=============================================================
    private ColumnEntryPanel panel;
    private String columnName;
    private EObject datatype;
        
    //=============================================================
    // Constructors
    //=============================================================
    /**
     * AliasEntryDialog constructor.
     * 
     * @param parent   parent of this dialog
     * @param transObj the transformation EObject
     * @param title    dialog display title
     */
    public ColumnEntryDialog(Shell parent, String title) {
        super(parent,title);
    }
        
    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        panel = new ColumnEntryPanel(this, composite);
        
        return composite;
    }
    
    @Override
    public void create() {
        super.create();
        setOkEnabled(false);
    }
    @Override
    protected void okPressed() {
        columnName = panel.getColumnName();
        datatype = panel.getDatatype();
        super.okPressed();
    }
    
    public void setOkEnabled(boolean enabled) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
    
    /**
     *  Get the alias name entry
     * @return the desired alias name
     */
    public String getColumnName() {
        return columnName;
    }
    
    public EObject getDatatype() {
        return this.datatype;
    }
}
