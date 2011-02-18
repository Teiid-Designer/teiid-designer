/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import com.metamatrix.modeler.diagram.ui.NotationChangeListener;

    /**
     * NotationChoice
     *  - One instance of this action will be created dynamically to represent each Notation extension
     *     found at init time.
     */
    public class NotationChoice extends DiagramAction  {

        private String sNotationExtensionId;
        private String sNotationExtensionDisplayName;
        private NotationChangeListener nclNotationListener;
        
        private int iUseCount = 1;
                
        //==========================================
        // CONSTRUCTORS
        //==========================================
    
        public NotationChoice( String sNotationExtensionId,
                               String sNotationExtensionDisplayName,
                               NotationChangeListener nclNotationListener ) {
                                   
            super( AS_RADIO_BUTTON );  

            setText( sNotationExtensionDisplayName );
            
            this.sNotationExtensionId           = sNotationExtensionId;
            this.sNotationExtensionDisplayName  = sNotationExtensionDisplayName;
            this.nclNotationListener            = nclNotationListener;                                  
        }
    
        //==========================================
        // METHODS
        //==========================================
    
    	/* 1. Fire a NotationChanged method.  The likely listener will be the current diagram editor.
         *    When the listener gets this event it will extract the Notation Id string and use it to 
         *    get a new set of factories from DiagramNotationManager.
         *  
         */
        @Override
        protected void doRun() {
//            DiagramUiConstants.Util.log( IStatus.INFO, "[NotationChoice.run] UseCount: " + iUseCount + ";  my name is: " + sNotationExtensionDisplayName  ); //$NON-NLS-1$            
            nclNotationListener.setNotationId( sNotationExtensionId );
            
            iUseCount++;
                                                        
        }
        
        public String getExtensionId() {
            return sNotationExtensionId;
        }

        public String getExtensionDisplayName() {
            return sNotationExtensionDisplayName;
        }

        public void setNotationChangeListener( NotationChangeListener ncl ) {
            this.nclNotationListener = ncl;
        }


}


