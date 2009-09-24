/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * ModelProjectSelectionStatusValidator is an implementation of ISelectionStatusValidator.
 * It checks that the selection is not empty, not multi, a folder or project, and ensures that
 * the project is open and is a Model Project.
 */
public class ModelProjectSelectionStatusValidator implements ISelectionStatusValidator, UiConstants {

    private static final IStatus OK_STATUS = new StatusInfo(PLUGIN_ID);
    
    private static final String NO_SELECTION = Util.getString("ModelProjectSelectionStatusValidator.noSelection"); //$NON-NLS-1$
    private static final String NO_MULTI_SELECTION = Util.getString("ModelProjectSelectionStatusValidator.noMultiSelection"); //$NON-NLS-1$
    private static final String PROJECT_CLOSED = Util.getString("ModelProjectSelectionStatusValidator.projectClosed"); //$NON-NLS-1$
    private static final String NOT_MODELING_PROJECT = Util.getString("ModelProjectSelectionStatusValidator.notModelProject"); //$NON-NLS-1$
    private static final String NOT_FOLDER = Util.getString("ModelProjectSelectionStatusValidator.notFolderSelection");  //$NON-NLS-1$
    
    public IStatus validate(Object[] selection) {
        IStatus result = OK_STATUS;
        
        String message = null;
        // check empty
        if ( selection == null || selection.length == 0 ) {
            message = NO_SELECTION;

        // check multi-selection
        } else if ( selection.length > 1 ) {
            message = NO_MULTI_SELECTION;

        } else {
            Object obj = selection[0];
            // check null
            if ( obj == null ) {
                message = NO_SELECTION;

            // check folder or project
            } else {
                if ( obj instanceof IContainer ) {
                    IProject project = ((IContainer) obj).getProject();

                    // check for closed project
                    if ( ! project.isOpen() ) {
                        message = PROJECT_CLOSED;

                    // check modelProject nature
                    } else {
                        try {
                            if ( project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) == null ) {
                                message = NOT_MODELING_PROJECT;
                            }
                        } catch (CoreException e) {
                            message = NOT_MODELING_PROJECT;
                            Util.log(e);
                        }
                    }
                } else {
                    message = NOT_FOLDER;
                }
            }
        }

        if ( message != null ) {
            result = new StatusInfo(PLUGIN_ID, IStatus.ERROR, message);
        }
        
        return result;
    }
}
