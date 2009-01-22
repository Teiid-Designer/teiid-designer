/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import java.util.List;

import com.metamatrix.modeler.mapping.ui.UiConstants;


/** 
 * @since 5.0
 */
public class SetIncludeInDocumentSpecialAction extends SetExcludeFromDocumentSpecialAction {
    
    //============================================================================================================================
    // Constants

    private static String INCLUDE_TITLE 
        = UiConstants.Util.getString( "SetIncludeExcludeAction.Include.title" );  //$NON-NLS-1$
    private static String INCLUDE_TOOLTIP 
        = UiConstants.Util.getString( "SetIncludeExcludeAction.Include.tooltip" );  //$NON-NLS-1$
    
    /** 
     * 
     * @since 5.0
     */
    public SetIncludeInDocumentSpecialAction() {
        super();
        this.setText( INCLUDE_TITLE );
        this.setToolTipText( INCLUDE_TOOLTIP );
        
        // What distinguishes this 'set included' action is that it sets the 'excluded state'
        //  of the selelcted xml document nodes to 'false'.  (Not Excluded = Included.)
        setDefaultExcludeState( false );
    }
    
    // This method is called by determineEnablement().
    @Override
    protected boolean isSelectionAppropriate( List lstSelectedEObjects ) {
        int iSelectionType = getXmlDocNodeSelectionType( lstSelectedEObjects );
        
        if ( iSelectionType == this.ALL_EXCLUDED 
          || iSelectionType == this.BOTH_INCLUDED_AND_EXCLUDED ) {
            return true;
        }
        return false;
    }
}
