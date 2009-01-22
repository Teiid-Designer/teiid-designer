/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;

/**
 * EObjectSelectionValidator
 */
public class EObjectSelectionValidator implements ISelectionStatusValidator {


    private StatusInfo stStatus;
    private static final String WRONG_CLASS_ERROR_MSG 
        = UiConstants.Util.getString("EObjectSelectionValidator.wrongClassErrorMessage.text"); //$NON-NLS-1$
    private static final String NOTHING_SELECTED 
        = UiConstants.Util.getString("EObjectSelectionValidator.nothingSelectedErrorMessage.text"); //$NON-NLS-1$
   
    
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
            if ( !( selection[ i ] instanceof EObject ) ) {
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
