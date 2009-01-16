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

package com.metamatrix.modeler.transformation.ui.actions;

import com.metamatrix.modeler.ui.actions.IModelerActionConstants;


/**
 * ITransformationDiagramActionConstants
 */
public interface ITransformationDiagramActionConstants {
    interface DiagramActions {
        
        /** Keys for accessing transformation actions. */
        String ADD_TRANSFORMATION_SOURCE = AddTransformationSourceAction.class.getName();
        String ADD_UNION_SOURCE = AddUnionSourceAction.class.getName();
        String REMOVE_TRANSFORMATION_SOURCE = RemoveTransformationSourceAction.class.getName();
        String CLEAR_TRANSFORMATION = ClearTransformationAction.class.getName();
        String RECONCILE_TRANSFORMATION = ReconcileTransformationAction.class.getName();
        String ADD_TO_SQL_FROM = AddToSqlFromAction.class.getName();
        String ADD_TO_SQL_SELECT = AddToSqlSelectAction.class.getName();
        String ADD_JOIN_EXPRESSION = AddJoinExpressionAction.class.getName();
        
        /** All Modeler unique global actions. */
        String[] ALL_ACTIONS = new String[] {
            ADD_TRANSFORMATION_SOURCE,
            ADD_UNION_SOURCE,
            REMOVE_TRANSFORMATION_SOURCE,
            CLEAR_TRANSFORMATION, 
            RECONCILE_TRANSFORMATION, 
            ADD_TO_SQL_FROM, 
            ADD_TO_SQL_SELECT, 
            ADD_JOIN_EXPRESSION 
        };
        
        /** Values of undo action capabililities */
        boolean UNDO_ADD_TRANSFORMATION_SOURCE = false;
        boolean UNDO_ADD_UNION_SOURCE = true;
        boolean UNDO_REMOVE_TRANSFORMATION_SOURCE = true;
        boolean UNDO_CLEAR_TRANSFORMATION = true;
        boolean UNDO_RECONCILE_TRANSFORMATION = false;
        boolean UNDO_ADD_TO_SQL_FROM = false;
        boolean UNDO_ADD_TO_SQL_SELECT = false;
        boolean UNDO_ADD_JOIN_EXPRESSION = false;
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

        /** Name of group for end of transformation diagram menu items. */
        String TRANS_DIAGRAM_END = "transformationDiagramEnd"; //$NON-NLS-1$
    }
    
    interface Toolbar {
        /** Name of group for start of diagram toolbar buttons. */
        String DIAGRAM_START = "diagramStart"; //$NON-NLS-1$

        /** Name of group for end of diagram toolbar buttons. */
        String DIAGRAM_END = "diagramEnd"; //$NON-NLS-1$
    }
}
