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
package com.metamatrix.modeler.ui.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * @since 5.0
 */
public class OpenModelEditorJob extends Job {
    private static final String JOB_NAME = "Open Model Editor"; //$NON-NLS-1$

    IFile fileToOpen;
    EObject editableObject;

    /**
     * @param theName
     * @since 5.0
     */
    public OpenModelEditorJob( IFile fileToOpen,
                               EObject eObjectToEdit ) {
        super(JOB_NAME);
        this.fileToOpen = fileToOpen;
        this.editableObject = eObjectToEdit;
    }

    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.0
     */
    @Override
    protected IStatus run( IProgressMonitor theMonitor ) {

        UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                if (editableObject != null) {
                    ModelEditorManager.edit(editableObject);
                } else {
                    final ModelEditor modelEditor = ModelEditorManager.getModelEditorForFile(fileToOpen, true);
                    if (modelEditor != null) {
                        ModelEditorManager.activate(modelEditor);
                    }
                }
            }
        });

        return Status.OK_STATUS;
    }
}
