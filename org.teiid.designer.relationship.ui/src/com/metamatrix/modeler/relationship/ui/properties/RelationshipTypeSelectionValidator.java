/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.RelationshipTypeEditor;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * RelationshipTypeSelectionValidator is an ISelectionStatusValidator that can check the
 * validity of an Object[] selection for setting either the subtypes or supertype of a
 * specified RelationshipType.
 */
public class RelationshipTypeSelectionValidator implements ISelectionStatusValidator {

    private RelationshipTypeEditor typeEditor;
    private boolean testSuperType;
    private boolean testSubType;

    /**
     * Construct an instance of RelationshipTypeSelectionValidator.
     */
    public RelationshipTypeSelectionValidator(RelationshipType type, boolean testSuperType, boolean testSubType) {
        typeEditor = RelationshipPlugin.createEditor( type );
        this.testSuperType = testSuperType;
        this.testSubType = testSubType;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    public IStatus validate(Object[] selection) {
        if ( testSubType ) {
            if ( selection == null || selection.length == 0 || selection[0] == null ) {
                // must be a RelationshipType
                final String msg = UiConstants.Util.getString("RelationshipTypeSelectionValidator.errorTypeObject"); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
            }
            for ( int i=0 ; i<selection.length ; ++i ) {
                if ( ! (selection[i] instanceof RelationshipType) ) {
                    // must be a RelationshipType
                    final String msg = UiConstants.Util.getString("RelationshipTypeSelectionValidator.errorTypeObject"); //$NON-NLS-1$
                    return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
                } else if ( ! typeEditor.canAddSubtype((RelationshipType) selection[i]) ) {
                    // must pass the editor
                    final String msg = UiConstants.Util.getString("RelationshipTypeSelectionValidator.errorCannotAddSubtype"); //$NON-NLS-1$
                    return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
                }
            }
        }
        
        if ( testSuperType ) {
            if ( selection == null || selection.length == 0 || selection[0] == null ) {
                // must be a RelationshipType
                final String msg = UiConstants.Util.getString("RelationshipTypeSelectionValidator.errorTypeObject"); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
            }
            if ( ! (selection[0] instanceof RelationshipType) ) {
                // must be a RelationshipType
                final String msg = UiConstants.Util.getString("RelationshipTypeSelectionValidator.errorTypeObject"); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
            } else if ( ! typeEditor.canSetSupertype((RelationshipType) selection[0]) ) {
                // must pass the editor
                final String msg = UiConstants.Util.getString("RelationshipTypeSelectionValidator.errorCannotSetSupertype"); //$NON-NLS-1$
                return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, msg);
            }
        }

        return new StatusInfo(UiConstants.PLUGIN_ID);
    }

}
