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

package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizard;

import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;


/** 
 * @since 4.3
 */
public class OpenProjectAction extends Action {
    private static final String TITLE = "Open Existing Project"; // UiPlugin.Util.getString("ImportMetadata.noProjectTitle"); //$NON-NLS-1$;
    /**
     * Construct an instance of ImportMetadata.
     */
    public OpenProjectAction() {
        super(TITLE);
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        WidgetFactory.createWizardDialog(iww.getShell(), new ExternalProjectImportWizard()).open();
    }
}
