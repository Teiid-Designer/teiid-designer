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

package com.metamatrix.modeler.relationship.ui.properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.modeler.internal.ui.viewsupport.WorkspaceTreeAccumulatorSource;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * RelationshipRoleAccumulatorSelectionValidator
 */
public class RelationshipRoleAccumulatorSelectionValidator implements ISelectionStatusValidator {

    private static final IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);

    private RelationshipRole role;
    private WorkspaceTreeAccumulatorSource accumulator;
    private boolean hasUpperBound = false; 

    /**
     * Construct an instance of RelationshipRoleAccumulatorSelectionValidator.
     * 
     */
    public RelationshipRoleAccumulatorSelectionValidator(WorkspaceTreeAccumulatorSource accumulator, RelationshipRole role) {
        this.accumulator = accumulator;
        this.role = role;
        this.hasUpperBound = role.getUpperBound() >= 0;
        
        accumulator.setSelectionValidator(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    public IStatus validate(Object[] selection) {
        for ( int i=0 ; i<selection.length ; ++ i ) {
            
            if ( ! (selection[i] instanceof EObject) ) {
                // must be an EObject
                final String msg = UiConstants.Util.getString("RelationshipRoleSelectionValidator.errorModelObject"); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
            } else if ( role.isUnique() && accumulator.getCurrentValues().contains(selection[i])) {
                // must be unique
                final String msg = UiConstants.Util.getString("RelationshipRoleSelectionValidator.errorUniqueness", accumulator.getLabelProvider().getText(selection[i])); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
            } else {
                // check the Role
                IStatus status = role.isValidParticipant(((EObject) selection[i]).eClass());
                if ( ! status.isOK() ) {
                    return status;
                }
            }
        }
        
        if ( hasUpperBound ) {
            if ( accumulator.getCurrentValues().size() + selection.length > role.getUpperBound() ) {
                final String msg = UiConstants.Util.getString("RelationshipRoleSelectionValidator.errorUpperLimit", new Integer(role.getUpperBound()).toString()); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
            }
        }

        return OK_STATUS;
    }

}
