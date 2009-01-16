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

package com.metamatrix.modeler.transformation.ui.actions;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.ui.internal.widget.Dialog;

/**
 * AliasEntryDialog
 */
public class AliasEntryDialog extends Dialog {

    //=============================================================
    // Instance variables
    //=============================================================
    private AliasEntryPanel panel;
    private String aliasName;
    private String tableName;
    private Object transRoot;
    private Object sourceEObject;
        
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
    public AliasEntryDialog(Shell parent, String title, String tableName, Object transRoot, Object sourceEObject) {
        super(parent,title);
        this.tableName = tableName;
        this.transRoot = transRoot;
        this.sourceEObject = sourceEObject;
    }
        
    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        panel = new AliasEntryPanel(this, composite, tableName, this.transRoot, this.sourceEObject);
        
        return composite;
    }
    
    @Override
    public void create() {
        super.create();
        setOkEnabled(false);
    }
    @Override
    protected void okPressed() {
        aliasName = panel.getAliasName();
        super.okPressed();
    }
    
    public void setOkEnabled(boolean enabled) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
    }
    
    /**
     *  Get the alias name entry
     * @return the desired alias name
     */
    public String getAliasName() {
        return aliasName;
    }
    
}
