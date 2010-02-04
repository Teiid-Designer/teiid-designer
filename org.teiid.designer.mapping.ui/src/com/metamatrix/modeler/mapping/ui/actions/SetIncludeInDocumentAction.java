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
 * @since 4.3
 */
public class SetIncludeInDocumentAction extends SetExcludeFromDocumentAction {
    
    //============================================================================================================================
    // Constants

    private static String INCLUDE_TITLE 
        = UiConstants.Util.getString( "SetIncludeExcludeAction.Include.title" );  //$NON-NLS-1$
    private static String INCLUDE_TOOLTIP 
        = UiConstants.Util.getString( "SetIncludeExcludeAction.Include.tooltip" );  //$NON-NLS-1$

    //============================================================================================================================
    // Constructors
    
    /**
     * Construct an instance of SetIncludeExcludeAction.
     * 
     */
    public SetIncludeInDocumentAction() {
        super();
        this.setUseWaitCursor( false );
        this.setText( INCLUDE_TITLE );
        this.setToolTipText( INCLUDE_TOOLTIP );
        
        // What distinguishes this 'set included' action is that it sets the 'excluded state'
        //  of the selelcted xml document nodes to 'false'.  (Not Excluded = Included.)
        setDefaultExcludeState( false );
        
//        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.FIND_XSD_COMPONENT));
    }

    // This method is called by determineEnablement().
    @Override
    protected boolean isSelectionAppropriate( List lstSelectedEObjects ) {
        int iSelectionType = getXmlDocNodeSelectionType( lstSelectedEObjects );
        
        if ( iSelectionType == SetExcludeFromDocumentAction.ALL_EXCLUDED 
          || iSelectionType == SetExcludeFromDocumentAction.BOTH_INCLUDED_AND_EXCLUDED ) {
            return true;
        }
        return false;
    }

}
