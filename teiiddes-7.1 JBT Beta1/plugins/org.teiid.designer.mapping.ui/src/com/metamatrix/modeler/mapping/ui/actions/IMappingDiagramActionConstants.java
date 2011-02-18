/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.actions;

import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * IMappingDiagramActionConstants
 */
public interface IMappingDiagramActionConstants {
    interface DiagramActions {

        /** Values of undo action capabilities */
        boolean UNDO_NEW_MAPPING_CLASS = false;
        boolean UNDO_NEW_MAPPING_LINK = true;
        boolean UNDO_DELETE_MAPPING_LINKS = true; // NO_UCD
        boolean UNDO_NEW_STAGING_TABLE = false;
        boolean UNDO_SPLIT_MAPPING_CLASS = false;
        boolean UNDO_MERGE_MAPPING_CLASSES = false;
        boolean UNDO_GENERATE_MAPPING_CLASSES = false;
    }

    interface ContextMenu {

        /** The identifier for the diagram editor's context menu. */
        String DIAGRAM_EDITOR_PAGE = "diagramEditorPage" + IModelerActionConstants.ContextMenu.MENU_ID_SUFFIX; //$NON-NLS-1$

        /** Name of group for start of transformation menu items. */
        String MAPPING_START = "mappingStart"; //$NON-NLS-1$

        /** Name of group for start of transformation menu items. */
        String TRANS_START = "transformationStart"; //$NON-NLS-1$

        /** Name of group for end of transformation menu items. */
        String TRANS_END = "transformationEnd"; //$NON-NLS-1$

        /** Name of group for start of transformation menu items. */
        String MAPPING_DIAGRAM_START = " mappingDiagramStart"; //$NON-NLS-1$
    }
}
