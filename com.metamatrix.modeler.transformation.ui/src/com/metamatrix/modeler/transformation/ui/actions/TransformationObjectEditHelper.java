/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectEditHelper;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TransformationObjectEditHelper extends ModelObjectEditHelper {

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canDelete(java.lang.Object)
	 */
	@Override
    public boolean canDelete(Object obj) {
		// if the obj is a ResultSet then we say NO
		if( obj instanceof EObject ) {
			if( TransformationHelper.isSqlProcedureResultSet(obj) ||
				TransformationHelper.isTransformationObject(obj))
				return false;
		}
        
        
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
                    if( diagram.getType().equals(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID) ) {
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
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canCut(java.lang.Object)
	 */
	@Override
    public boolean canCut(Object obj) {
		if( obj instanceof EObject ) {
			if( TransformationHelper.isSqlProcedureResultSet(obj) ||
				TransformationHelper.isTransformationObject(obj))
				return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.ui.actions.IModelObjectEditHelper#canClone(java.lang.Object)
	 */
	@Override
    public boolean canClone(Object obj) {
		if( obj instanceof EObject ) {
			if( TransformationHelper.isSqlProcedureResultSet(obj) ||
				TransformationHelper.isTransformationObject(obj))
				return false;
		}
		
		return true;
	}

}
