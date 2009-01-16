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


