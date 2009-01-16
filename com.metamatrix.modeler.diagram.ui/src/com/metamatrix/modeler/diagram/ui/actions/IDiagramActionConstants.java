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

import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.ui.actions.IActionConstants;

/**
 * IDiagramActionConstants
 */ 
public interface IDiagramActionConstants extends IActionConstants {
 
    interface DiagramGlobalActions {
        /** Key for accessing the global copy action. */
        String ZOOM_IN = ZoomInWrapper.class.getName();
        
        /** Key for accessing the global edit action. */
        String ZOOM_OUT = ZoomOutWrapper.class.getName();
                       
        /** Key for accessing the global print action. This constant should NOT show up in ALL_ACTIONS. */
        String PRINT = PrintWrapper.class.getName();
        
        /** Key for accessing the global increase font action. */
        String FONT_UP = FontUpWrapper.class.getName();
        
        /** Key for accessing the global decrease font action. */
        String FONT_DOWN = FontDownWrapper.class.getName();
        
        /** Key for accessing the global autolayout action. */
        String AUTOLAYOUT = AutoLayoutWrapper.class.getName();
                
        /** All diagram unique global actions. Do not include 'original' global actions, like PRINT.*/
        String[] ALL_ACTIONS = new String[] {
//            ZOOM_IN,
//            ZOOM_OUT,
//            FONT_UP,
//            FONT_DOWN,
//            AUTOLAYOUT,
        };
    }
    
 
    interface ContextMenu {
        /** The identifier for the diagram editor's context menu. */
        String DIAGRAM_EDITOR_PAGE = "diagramEditorPage" + IModelerActionConstants.ContextMenu.MENU_ID_SUFFIX; //$NON-NLS-1$
        
        /** Name of group for start of zoom menu items. */
        String ZOOM_START = "zoomStart"; //$NON-NLS-1$

        /** Name of group for end of zoom menu items. */
        String ZOOM_END = "zoomEnd"; //$NON-NLS-1$

        /** Name of group for start of notation menu items. */
        String NOTATION_START = "notationStart"; //$NON-NLS-1$

        /** Name of group for end of notation menu items. */
        String NOTATION_END = "notationEnd"; //$NON-NLS-1$
        
//        /** Name of group for start of transformation menu items. */
//        String TRANS_START = "transformationStart"; //$NON-NLS-1$
//
//        /** Name of group for end of transformation menu items. */
//        String TRANS_END = "transformationEnd"; //$NON-NLS-1$
        
        /** Name of group for start of transformation menu items. */
        String DIAGRAM_START = "diagramStart"; //$NON-NLS-1$

        /** Name of group for end of transformation menu items. */
        String DIAGRAM_END = "diagramEnd"; //$NON-NLS-1$
    }
    
    interface Toolbar {
        /** Name of group for start of diagram toolbar buttons. */
        String DIAGRAM_START = "diagramStart"; //$NON-NLS-1$

        /** Name of group for end of diagram toolbar buttons. */
        String DIAGRAM_END = "diagramEnd"; //$NON-NLS-1$
    }
    
}
