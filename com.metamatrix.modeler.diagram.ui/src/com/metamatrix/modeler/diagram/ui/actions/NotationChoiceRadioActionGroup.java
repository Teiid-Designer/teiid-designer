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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.NotationChangeListener;

/**
 * NotationChoiceRadioActionGroup
 */
public class NotationChoiceRadioActionGroup extends MenuManager 
                                         implements NotationChangeListener,
                                                    DiagramUiConstants  {

    private ArrayList<NotationChoice> arylNotationActions;
    private NotationChangeListener nclNotationListener;    
    private String sNotationId;
    
    private static final String NOTATION_SUBMENU_TITLE = "DiagramActions.NotationSubmenuTitle"; //$NON-NLS-1$

    
    /**
     * Construct an instance of NotationChoiceRadioActionGroup.
     * 
     */
    public NotationChoiceRadioActionGroup( NotationChangeListener nclNotationListener, String currentNotationId ) {
        this( nclNotationListener, Util.getString( NOTATION_SUBMENU_TITLE ), currentNotationId );
    }

    /**
     * Construct an instance of NotationChoiceRadioActionGroup.
     * @param text
     */
    public NotationChoiceRadioActionGroup( NotationChangeListener nclNotationListener, String sText, String currentNotationId) {
        
        super( sText );
        this.nclNotationListener = nclNotationListener;

        List<String> lstNotationExtensions 
            = DiagramUiPlugin.getDiagramNotationManager().getExtensionIds();
        arylNotationActions = new ArrayList<NotationChoice>();            
    
        Iterator<String> it = lstNotationExtensions.iterator();
        
        while ( it.hasNext() ) {
            String sExtensionId = it.next();
            String sExtensionDisplayName 
                = DiagramUiPlugin.getDiagramNotationManager()
                        .getExtensionDisplayName( sExtensionId );
                
            // wire the choice to this class, the RadioActionGroup    
            NotationChoice action 
                = new NotationChoice( sExtensionId, sExtensionDisplayName, this );
                            
            // set initial checked state
            if ( sExtensionId.equals( currentNotationId ) ) {
                action.setChecked( true );
            } else {
                action.setChecked( false );
            }
                            
            add(action); 
            arylNotationActions.add( action );                            
        }
    }
        

    public void setNotationId( String sNotationId ) {
        //Util.log( IStatus.INFO, "[NotationChoiceRadioActionGroup.setNotationId] TOP" ); //$NON-NLS-1$
        
        // the action that was clicked will call our (the ActionGroup's)
        //  setNotatationId method; and we in turn will call the 
        //  NotationChangeListener we got at construction time, probably our owner. 
        if ( this.sNotationId == null 
          || ( sNotationId != null 
          &&   this.sNotationId != null 
          &&   !this.sNotationId.equals( sNotationId ) ) ) {
                        
                                    
            nclNotationListener.setNotationId( sNotationId );
            this.sNotationId = sNotationId;            
        }
        
        updateNotationActions( this.sNotationId );  
    }
    
    public void updateNotationActions( String sNotationId ) {
        Iterator<NotationChoice> it = arylNotationActions.iterator();
        
        while( it.hasNext() ) {
            NotationChoice ncAction = it.next();
            if( ncAction.getExtensionId().equals( sNotationId ) ) {
                ncAction.setChecked( true );
            } else {
                ncAction.setChecked( false );
            }
        }
    }    
    
    public void setNotationChangeListener( NotationChangeListener nclNotationListener ) {
        this.nclNotationListener = nclNotationListener;
    }

    public NotationChangeListener getNotationChangeListener() {
        return nclNotationListener;
    }

}
