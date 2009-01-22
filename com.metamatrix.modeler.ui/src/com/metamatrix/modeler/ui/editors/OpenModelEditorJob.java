/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
