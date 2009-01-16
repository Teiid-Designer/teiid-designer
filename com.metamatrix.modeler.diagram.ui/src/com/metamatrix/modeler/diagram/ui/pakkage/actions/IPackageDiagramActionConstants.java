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

package com.metamatrix.modeler.diagram.ui.pakkage.actions;

import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * IPackageDiagramActionConstants
 */
public interface IPackageDiagramActionConstants {

    interface DiagramActions {
        
        /** Keys for accessing transformation actions. */
        String ADD_TO_DIAGRAM = AddToDiagramAction.class.getName();
        String REMOVE_FROM_DIAGRAM = RemoveFromDiagramAction.class.getName();
        String CLEAR_DIAGRAM = ClearDiagramAction.class.getName();
        
        /** All Modeler unique global actions. */
        String[] ALL_ACTIONS = new String[] {
            ADD_TO_DIAGRAM,
            REMOVE_FROM_DIAGRAM,
            CLEAR_DIAGRAM 
        };
    }
    
    
    interface ContextMenu {
        
        /** The identifier for the diagram editor's context menu. */
        String DIAGRAM_EDITOR_PAGE = "diagramEditorPage" + IModelerActionConstants.ContextMenu.MENU_ID_SUFFIX; //$NON-NLS-1$
        
        /** Name of group for start of transformation menu items. */
        String DIAGRAM_START = "diagramStart"; //$NON-NLS-1$

        /** Name of group for end of transformation menu items. */
        String DIAGRAM_END = "diagramEnd"; //$NON-NLS-1$
    }


}
