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

package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.ui.internal.widget.Dialog;

public class AssociatedLevelsDialog extends Dialog {

    //=============================================================
    // Instance variables
    //=============================================================
    private AssociatedLevelsPanel panel;
    private int levels = 0;

    //=============================================================
    // Constructors
    //=============================================================
    /**
     * AliasEntryDialog constructor.
     * 
     * @param parent
     *            parent of this dialog
     * @param transObj
     *            the transformation EObject
     * @param title
     *            dialog display title
     */
    public AssociatedLevelsDialog(Shell parent,
                                  String title) {
        super(parent, title);
    }

    //=============================================================
    // Instance methods
    //=============================================================

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        panel = new AssociatedLevelsPanel(composite);
        return composite;
    }

    @Override
    protected void okPressed() {
        levels = panel.getLevels();
        super.okPressed();
    }

    /**
     * Get the alias name entry
     * 
     * @return the desired alias name
     */
    public int getLevels() {
        return levels;
    }

}
