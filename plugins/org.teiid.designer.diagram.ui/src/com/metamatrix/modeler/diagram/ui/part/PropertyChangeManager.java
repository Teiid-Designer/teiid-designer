/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;


/** 
 * @since 5.0
 */
public class PropertyChangeManager implements DiagramUiConstants.DiagramNodeProperties {
    public final static int GENERAL            = 0;
    public final static int VISUALS            = 1;
    public final static int NAME               = 2;
    public final static int LABELS             = 3;
    public final static int ANCHORS            = 4;
    public final static int SOURCE_CONNECTIONS = 5;
    public final static int TARGET_CONNECTIONS = 6;
    public final static int PATH               = 7;
    public final static int CHILDREN           = 8;
    public final static int RESIZE_CHILDREN    = 9;
    public final static int LAYOUT             = 10;
    public final static int LAYOUT_ALL         = 11;

    private DiagramEditPart editPart;
    private boolean bRefresh                     = false;
    private boolean bRefreshVisuals              = false;
    private boolean bRefreshName                 = false;
    private boolean bRefreshAllLabels            = false;
    private boolean bRefreshAnchors              = false;
    private boolean bRefreshSourceConnections    = false;
    private boolean bRefreshTargetConnections    = false;
    private boolean bRefreshPath                 = false;
    private boolean bRefreshChildren             = false;
    private boolean bResizeChildren              = false;
    private boolean bLayout                      = false;
    private boolean bLayoutAll                   = false;
    
    private boolean updateLinkedParts            = false;
    
    private static int nStoppedRefreshes = 0;
    /** 
     * 
     * @since 5.0
     */
    public PropertyChangeManager(DiagramEditPart theEditPart) {
        super();
        this.editPart = theEditPart;
    }

    public void refresh(int type, boolean forceRefresh) {
        boolean doIt = false;
        
        if( !this.editPart.isUnderConstruction() || forceRefresh ) {
            doIt = true;
            setState(type, false);
        } else {
            nStoppedRefreshes++;
            if( nStoppedRefreshes % 100 == 0 ) {
               //System.out.println("    =====>> PropertyChangeManger:  N Refreshes Stopped = " + nStoppedRefreshes);
            }
            setState(type, true);
        }
        
        if( doIt ) {
            refresh(type);
        }
    }
    
    private void setState( int type, boolean state) {
        switch( type ) {
            case GENERAL: { 
                this.bRefresh = state;
                if(this.bRefresh ) {
                    bRefreshVisuals = false;
                    bRefreshTargetConnections = false;
                    bRefreshSourceConnections = false;
                    bRefreshChildren = false;
                }
            } break;
            
            case VISUALS: { 
                this.bRefreshVisuals = state;
            } break;
            
            case NAME: { 
                this.bRefreshName = state;
            } break;
            
            case LABELS: { 
                this.bRefreshAllLabels = state;
            } break;
            
            case ANCHORS: { 
                this.bRefreshAnchors = state;
            } break;
            
            case SOURCE_CONNECTIONS: { 
                this.bRefreshSourceConnections = state;
            } break;
            
            case TARGET_CONNECTIONS: { 
                this.bRefreshTargetConnections = state;
            } break;
            
            case PATH: { 
                this.bRefreshPath = state;
            } break;
            
            case CHILDREN: { 
                this.bRefreshChildren = state;
            } break;
            
            case RESIZE_CHILDREN: { 
                this.bResizeChildren = state;
            } break;
            
            case LAYOUT: { 
                this.bLayout = state;
            } break;
            
            case LAYOUT_ALL: { 
                this.bLayoutAll = state;
            } break;
            
            default: { 
                
            } break;
        }
    }
    
    public void refresh(int type) {
        switch( type ) {
            case GENERAL: { 
                this.editPart.refresh();
            } break;
            
            case VISUALS: { 
                this.editPart.refreshVisuals(true);
            } break;
            
            case NAME: { 
                this.editPart.refreshName();
            } break;
            
            case LABELS: { 
                this.editPart.refreshAllLabels(true);
            } break;
            
            case ANCHORS: {
                this.editPart.refreshAnchors(updateLinkedParts);
            } break;
            
            case SOURCE_CONNECTIONS: { 
                this.editPart.refreshSourceConnections(true);
            } break;
            
            case TARGET_CONNECTIONS: { 
                this.editPart.refreshTargetConnections(true);
            } break;
            
            case PATH: { 
                this.editPart.refreshPath(true);
            } break;
            
            case CHILDREN: { 
                this.editPart.refreshChildren(true);
                //this.editPart.resizeChildren(true);
            } break;
            
            case RESIZE_CHILDREN: { 
                this.editPart.resizeChildren(true);
            } break;
            
            case LAYOUT: { 
                this.editPart.layout(false);
            } break;
            
            case LAYOUT_ALL: { 
                this.editPart.layout(true);
            } break;
            
            default: { 
                
            } break;
        }
    }
    
    public void executeRefresh(boolean updateLinkedParts) {
        if( this.editPart.isUnderConstruction() ) {
            //System.out.println("    =====>> PropertyChangeManger:  Executing Refresh on EditPart = " + ((DiagramModelNode)this.editPart.getModel()).getName());
            this.updateLinkedParts = updateLinkedParts;
            boolean refreshEPVisuals = bRefreshVisuals;
            if( bRefresh ) {
                refresh(GENERAL, true);
            }
            if( bRefreshName ) {
                refresh(NAME, true);
            }
            if( bRefreshAllLabels ) {
                refresh(LABELS, true);
            }
            if( bRefreshSourceConnections ) {
                refresh(SOURCE_CONNECTIONS, true);
            }
            if( bRefreshTargetConnections ) {
                refresh(TARGET_CONNECTIONS, true);
            }
            if( bRefreshAnchors ) {
                refresh(ANCHORS, true);
            }
            if( bRefreshPath ) {
                refresh(PATH, true);
            }
            if( bRefreshChildren ) {
                refresh(CHILDREN, true);
            }
            if( bResizeChildren ) {
                refresh(RESIZE_CHILDREN, true);
            }
            if( bLayout ) {
                refresh(LAYOUT, true);
                refreshEPVisuals = true;
            }
            if( bLayoutAll ) {
                refresh(LAYOUT_ALL, true);
                refreshEPVisuals = true;
            }
            // go ahead and refresh the visuals if refreshEPVisuals = true
            if( refreshEPVisuals ) {
                refresh(VISUALS, true);
            }
        }
    }
    
    public void reset() {
        bRefresh                     = false;
        bRefreshVisuals              = false;
        bRefreshName                 = false;
        bRefreshAllLabels            = false;
        bRefreshAnchors              = false;
        bRefreshSourceConnections    = false;
        bRefreshTargetConnections    = false;
        bRefreshPath                 = false;
        bRefreshChildren             = false;
        bResizeChildren              = false;
        bLayout                      = false;
        bLayoutAll                   = false;
        updateLinkedParts            = false;
    }
    
}
