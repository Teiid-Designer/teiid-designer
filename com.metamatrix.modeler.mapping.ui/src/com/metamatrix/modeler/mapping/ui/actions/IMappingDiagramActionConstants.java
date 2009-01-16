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

package com.metamatrix.modeler.mapping.ui.actions;

import com.metamatrix.modeler.transformation.ui.actions.AddTransformationSourceAction;
import com.metamatrix.modeler.transformation.ui.actions.ClearTransformationAction;
import com.metamatrix.modeler.transformation.ui.actions.ReconcileTransformationAction;
import com.metamatrix.modeler.transformation.ui.actions.RemoveTransformationSourceAction;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * IMappingDiagramActionConstants
 */
public interface IMappingDiagramActionConstants {
        interface DiagramActions {
        
            /** Keys for accessing transformation actions. */
            String SHOW_DETAILED_MAPPING = ShowDetailedMappingDiagramAction.class.getName();
            String NEW_MAPPING_CLASS = NewMappingClassAction.class.getName();
            String NEW_MAPPING_LINK = NewMappingLinkAction.class.getName();
            String DELETE_MAPPING_LINKS = DeleteMappingLinksAction.class.getName();
            String NEW_STAGING_TABLE = NewStagingTableAction.class.getName();
            String SPLIT_MAPPING_CLASS = SplitMappingClassAction.class.getName();
            String TOGGLE_DISPLAY_ALL_MAPPING_CLASSES = ToggleDisplayAllMappingClassesAction.class.getName();
            String TOGGLE_FOLD_ALL_MAPPING_CLASSES = ToggleFoldAllMappingClassesAction.class.getName();
            String TOGGLE_SYNC_TREE_AND_DIAGRAM = ToggleSyncTreeAndDiagramExpandsAction.class.getName();
            String TOGGLE_POPULATE_DIAGRAM_FROM_TREE_SELECTION = TogglePopulateDiagramFromTreeSelectionAction.class.getName();
            String MERGE_MAPPING_CLASSES = MergeMappingClassesAction.class.getName();
            
            /** Keys for accessing transformation actions. */
            String ADD_TRANSFORMATION_SOURCE = AddTransformationSourceAction.class.getName();
            String REMOVE_TRANSFORMATION_SOURCE = RemoveTransformationSourceAction.class.getName();
            String CLEAR_TRANSFORMATION = ClearTransformationAction.class.getName();
            String RECONCILE_TRANSFORMATION = ReconcileTransformationAction.class.getName();
        
            /** All Modeler unique global actions. */
            String[] ALL_ACTIONS = new String[] {
                SHOW_DETAILED_MAPPING,
                NEW_MAPPING_CLASS,
                NEW_MAPPING_LINK,
                DELETE_MAPPING_LINKS, 
                NEW_STAGING_TABLE,
                SPLIT_MAPPING_CLASS,
                TOGGLE_DISPLAY_ALL_MAPPING_CLASSES,
                TOGGLE_FOLD_ALL_MAPPING_CLASSES,
                TOGGLE_SYNC_TREE_AND_DIAGRAM,
                TOGGLE_POPULATE_DIAGRAM_FROM_TREE_SELECTION,
                MERGE_MAPPING_CLASSES,
                ADD_TRANSFORMATION_SOURCE,
                REMOVE_TRANSFORMATION_SOURCE,
                CLEAR_TRANSFORMATION, 
                RECONCILE_TRANSFORMATION 
            };
            
            /** Values of undo action capabililities */
            boolean UNDO_SHOW_DETAILED_MAPPING = false;
            boolean UNDO_NEW_MAPPING_CLASS = false;
            boolean UNDO_NEW_MAPPING_LINK = true;
            boolean UNDO_DELETE_MAPPING_LINKS = true;
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

            /** Name of group for end of transformation menu items. */
            String MAPPING_END = "mappingEnd"; //$NON-NLS-1$
            
            /** Name of group for start of transformation menu items. */
            String TRANS_START = "transformationStart"; //$NON-NLS-1$

            /** Name of group for end of transformation menu items. */
            String TRANS_END = "transformationEnd"; //$NON-NLS-1$
            
            /** Name of group for start of transformation menu items. */
            String MAPPING_DIAGRAM_START = " mappingDiagramStart"; //$NON-NLS-1$

            /** Name of group for end of transformation menu items. */
            String MAPPING_DIAGRAM_END = "mappingDiagramEnd"; //$NON-NLS-1$
        }
}
