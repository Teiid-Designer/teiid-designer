/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * RelationshipRoleSelectionValidator
 */
public class RelationshipRoleSelectionValidator implements ISelectionStatusValidator {

    private static final IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);

    private RelationshipRole role;

    /**
     * Construct an instance of RelationshipRoleSelectionValidator.
     * 
     */
    public RelationshipRoleSelectionValidator(RelationshipRole role) {
        this.role = role;
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
            }
            // check the Role
            IStatus status = role.isValidParticipant(((EObject) selection[i]).eClass());
            if ( ! status.isOK() ) {
                return status;
            }
        }
        
        return OK_STATUS;
    }

}
