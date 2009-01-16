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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.actions.TransformationGlobalActionsManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 4.3
 */
public class MappingObjectEditHelper extends ModelObjectEditHelper {

    /** 
     * 
     * @since 4.3
     */
    public MappingObjectEditHelper() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    @Override
    public boolean canDelete(Object obj) {
        if( obj instanceof EObject ) {
            
            // Defect 23466 - Need to put a hack in here to NOT allow deleting if the focused part is Diagram Editor, the diagram is
            // a transformation & the transformation global actions manager says it can or not.
            // This is because the SQL Editor global actions may supercede the DiagramEditor's and there is no framework to restore
            // these actions at the momement. 
            
            // Let's Check out the selection's source. If it's 
            IWorkbenchPart activePart = UiPlugin.getDefault().getCurrentWorkbenchWindow().getPartService().getActivePart();
    
            if( activePart instanceof ModelEditor ) {
                IEditorPart activeSubEditorPart = ((ModelEditor)activePart).getActiveEditor();
                if( activeSubEditorPart instanceof DiagramEditor ) {
                    DiagramViewer viewer = ((DiagramEditor)activeSubEditorPart).getDiagramViewer();
                    if( viewer.isValidViewer() && viewer.hasFocus() ) {
                        // Check for T-Diagram
                        Diagram diagram = viewer.getEditor().getDiagram();
                        if( diagram.getType().equals(PluginConstants.MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID) ) {
                            EObject vTable = diagram.getTarget();
                            if( vTable != null ) {
                                EObject transform = TransformationHelper.getTransformationMappingRoot(vTable);
                                if( transform != null ) {
                                    IStructuredSelection diagramSelection = (IStructuredSelection)viewer.getSelection();
                                    List selectedObjs = SelectionUtilities.getSelectedEObjects(diagramSelection);
                                    if( selectedObjs.contains(obj) ) {
                                        return TransformationGlobalActionsManager.canDelete(transform, selectedObjs);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    @Override
    public boolean canUndoDelete(Object obj) {

        if ( obj instanceof MappingClass ) {
            return false;
        }else if(obj instanceof Collection) {
            //Defect 23550
            //If a collection, return false if any item in the collection is a mapping class
            final Iterator objs = ((Collection)obj).iterator();
            while(objs.hasNext() ) {
                final Object next = objs.next();
                if(next instanceof MappingClass) {
                    return false;
                }
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    public boolean canSplit(Object obj) {

        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    public boolean canUndoSplit(Object obj) {

        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    public boolean canMerge(Object obj) {

        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
     */
    public boolean canUndoMerge(Object obj) {

        return false;
    }
    
    public boolean canEdit(Object obj, Object targetResource) {
        // We need to return FALSE if the object being edited is a "Source" to a transformation
        if( obj instanceof EObject && targetResource instanceof ModelResource ) {
            // Check if the obj's ModelResource is same as target
            ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)obj);
            if( mr != null && mr == targetResource) {
                return true;
            }
            return false;
        }
        return true;
    }
    
}
