/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom.actions;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * ICustomDiagramActionConstants
 */

public interface ICustomDiagramActionConstants {

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
