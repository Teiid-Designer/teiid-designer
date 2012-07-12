/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.metamodels.relationship.RelationshipType;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;


/**
 * EObjectSelectionValidator
 */
public class RelationshipTypeSelectionValidator 
  implements ISelectionStatusValidator {


    private StatusInfo stStatus;
    private static final String WRONG_CLASS_ERROR_MSG 
        = org.teiid.designer.relationship.ui.UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipTypeSelectionValidator.wrongClassErrorMessage.text"); //$NON-NLS-1$
    private static final String NOTHING_SELECTED 
        = org.teiid.designer.relationship.ui.UiConstants.Util.getString("org.teiid.designer.relationship.ui.editor.RelationshipTypeSelectionValidator.nothingSelectedErrorMessage.text"); //$NON-NLS-1$
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    public IStatus validate(Object[] selection) {


        // deal with empty selection
        if ( selection == null || selection.length == 0 ) {
            return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, NOTHING_SELECTED);
        }
        
        // check class
        boolean bAllAreValid = true;
        
        for( int i = 0; i < selection.length; i++  ) {
            if ( !( selection[ i ] instanceof RelationshipType ) ) {
                bAllAreValid = false;
                break;
            }            
        }
        
        
        if ( bAllAreValid ) {
             stStatus =  new StatusInfo( UiConstants.PLUGIN_ID,  
                                        IStatus.OK, 
                                        "" );     //$NON-NLS-1$                
        } else {
            stStatus =  new StatusInfo( UiConstants.PLUGIN_ID,     
                                        IStatus.ERROR, 
                                        WRONG_CLASS_ERROR_MSG );                              
        }
        
        return stStatus;
    }

}
