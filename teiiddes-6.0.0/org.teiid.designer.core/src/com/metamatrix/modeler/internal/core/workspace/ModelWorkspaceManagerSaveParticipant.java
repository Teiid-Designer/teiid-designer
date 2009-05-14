/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * ModelWorkspaceManagerSaveParticipantSaveParticipant
 */
public class ModelWorkspaceManagerSaveParticipant implements ISaveParticipant {

    /**
     * Construct an instance of ModelWorkspaceManagerSaveParticipantSaveParticipant.
     */
    public ModelWorkspaceManagerSaveParticipant() {
        super();
    }

    /**
     * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
     */
    public void doneSaving( final ISaveContext context ) {
        if (ModelerCore.DEBUG_MODEL_WORKSPACE_SAVE) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ModelWorkspaceManagerSaveParticipant.DEBUG.doneSaving(_context_)_1", context)); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
     */
    public void prepareToSave( final ISaveContext context ) {
        if (ModelerCore.DEBUG_MODEL_WORKSPACE_SAVE) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ModelWorkspaceManagerSaveParticipant.DEBUG.prepareToSave(_context_)_2", context)); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
     */
    public void rollback( final ISaveContext context ) {
        if (ModelerCore.DEBUG_MODEL_WORKSPACE_SAVE) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ModelWorkspaceManagerSaveParticipant.DEBUG.rollback(_context_)_3", context)); //$NON-NLS-1$
        }
    }

    /**
     * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
     */
    public void saving( final ISaveContext context ) {
        if (ModelerCore.DEBUG_MODEL_WORKSPACE_SAVE) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ModelWorkspaceManagerSaveParticipant.DEBUG.saving(_context_)_4", context)); //$NON-NLS-1$
        }
    }

}
