/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * ITransformationDiagramActionConstants
 */
public interface ITransformationDiagramActionConstants {
    interface DiagramActions {

        String RECONCILE_TRANSFORMATION = ReconcileTransformationAction.class.getName();
        String ADD_TO_SQL_FROM = AddToSqlFromAction.class.getName();
        String ADD_TO_SQL_SELECT = AddToSqlSelectAction.class.getName();
        String ADD_JOIN_EXPRESSION = AddJoinExpressionAction.class.getName();

        /** Values of undo action capabilities */
        boolean UNDO_ADD_TRANSFORMATION_SOURCE = false;
        boolean UNDO_ADD_UNION_SOURCE = true; // NO_UCD
        boolean UNDO_REMOVE_TRANSFORMATION_SOURCE = true;
        boolean UNDO_CLEAR_TRANSFORMATION = true;
        boolean UNDO_RECONCILE_TRANSFORMATION = false; // NO_UCD
        boolean UNDO_ADD_TO_SQL_FROM = false; // NO_UCD
        boolean UNDO_ADD_TO_SQL_SELECT = false; // NO_UCD
        boolean UNDO_ADD_JOIN_EXPRESSION = false; // NO_UCD
    }

    interface ContextMenu {

        /** The identifier for the diagram editor's context menu. */
        String DIAGRAM_EDITOR_PAGE = "diagramEditorPage" + IModelerActionConstants.ContextMenu.MENU_ID_SUFFIX; //$NON-NLS-1$

        /** Name of group for start of transformation menu items. */
        String TRANS_START = "transformationStart"; //$NON-NLS-1$

        /** Name of group for end of transformation menu items. */
        String TRANS_END = "transformationEnd"; //$NON-NLS-1$

        /** Name of group for start of transformation diagram menu items. */
        String TRANS_DIAGRAM_START = "transformationDiagramStart"; //$NON-NLS-1$
    }
}
