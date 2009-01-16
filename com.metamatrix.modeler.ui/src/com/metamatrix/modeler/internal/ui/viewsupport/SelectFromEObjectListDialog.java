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

package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.Collection;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @since 5.0
 */
public class SelectFromEObjectListDialog extends ElementListSelectionDialog {

    private String theTitle;

    /**
     * This simple dialog provides a way to query users to select Objects/elements from a list of pre-determined objects.
     * @since 5.0
     */
    public SelectFromEObjectListDialog(Shell parent,
                                       Collection elements,
                                       boolean allowMultiple,
                                       String dialogTitle,
                                       String initialMessage) {
        super(parent, new SelectModelObjectLabelProvider());
        this.theTitle = dialogTitle;
        setElements(elements.toArray());
        setMultipleSelection(allowMultiple);
        setMessage(initialMessage);
    }

    public SelectFromEObjectListDialog(Shell parent,
                                       Collection elements,
                                       boolean allowMultiple,
                                       String dialogTitle,
                                       String initialMessage,
                                       LabelProvider labelProvider) {
        super(parent, labelProvider);
        this.theTitle = dialogTitle;
        setElements(elements.toArray());
        setMultipleSelection(allowMultiple);
        setMessage(initialMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.dialogs.ElementListSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Control control = super.createDialogArea(parent);
        getShell().setText(this.theTitle);
        return control;
    }
}
