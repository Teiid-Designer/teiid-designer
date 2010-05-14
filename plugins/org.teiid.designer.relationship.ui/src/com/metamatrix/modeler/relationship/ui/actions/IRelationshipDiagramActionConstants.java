/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.actions;

import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public interface IRelationshipDiagramActionConstants {

    interface ContextMenu {

        /** The identifier for the diagram editor's context menu. */
        String DIAGRAM_EDITOR_PAGE = "diagramEditorPage" + IModelerActionConstants.ContextMenu.MENU_ID_SUFFIX; //$NON-NLS-1$

        /** Name of group for start of transformation menu items. */
        String DIAGRAM_START = "diagramStart"; //$NON-NLS-1$

        /** Name of group for end of transformation menu items. */
        String DIAGRAM_END = "diagramEnd"; //$NON-NLS-1$
    }

}
