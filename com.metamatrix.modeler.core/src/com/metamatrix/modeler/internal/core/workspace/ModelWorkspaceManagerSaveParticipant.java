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
