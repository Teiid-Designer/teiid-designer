/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.metamodels.relationship.RelationshipRole;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.viewsupport.WorkspaceTreeAccumulatorSource;


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
    @Override
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
