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

package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;

public class BaseTypeDialogProvider extends TextDialogProvider {
    private static final String DIALOG_BASETYPE_TITLE  = "BaseTypeDialogProvider.dialog.basetype.title"; //$NON-NLS-1$
    private static final String DIALOG_BASETYPE_DESC   = "BaseTypeDialogProvider.dialog.basetype.desc"; //$NON-NLS-1$

    public BaseTypeDialogProvider(String launchButtonText) {
        super(launchButtonText, DIALOG_BASETYPE_TITLE, DIALOG_BASETYPE_DESC);
    }

    @Override
    public void showDialog(Shell shell, Object initialValue) {
        // Do NOT call super, here, because we don't want the text dialog
        // show dialog:
        DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(shell);
        dialog.setAllowSimple(true);
        dialog.setInitialSelections(new Object[] {initialValue});
        int status = dialog.open();

        // process dialog
        if (status == Window.OK) {
            value = dialog.getResult()[0];
        } else {
            value = null;
        } // endif

    }
}
