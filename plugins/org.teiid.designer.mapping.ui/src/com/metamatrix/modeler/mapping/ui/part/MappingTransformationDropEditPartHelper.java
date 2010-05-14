/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.part;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;
import com.metamatrix.modeler.transformation.ui.part.TransformationDropEditPartHelper;
import com.metamatrix.modeler.transformation.ui.part.TransformationEditPart;

public class MappingTransformationDropEditPartHelper extends
		TransformationDropEditPartHelper {

	public MappingTransformationDropEditPartHelper(Object transformation) {
		super(transformation);
	}
	
    @Override
    protected EObject getTransformation(DiagramEditPart editPart) {
        if( editPart instanceof MappingDiagramEditPart ) {
            return TransformationSourceManager.getTransformationFromDiagram((Diagram)editPart.getModelObject());
        } else if( editPart instanceof TransformationEditPart ) {
            return editPart.getModelObject();
        } else if( editPart instanceof UmlClassifierEditPart ) {
            if( !ModelObjectUtilities.isVirtual(editPart.getModelObject())) {
                return null;
            }
            // check to see if it's the "Target"
            DiagramModelNode parentClassifierNode = DiagramUiUtilities.getClassifierParentNode((DiagramModelNode)editPart.getModel());
            if( parentClassifierNode != null ) {
                if(TransformationHelper.isValidSqlTransformationTarget(parentClassifierNode.getModelObject())) {
                    return TransformationHelper.getTransformationMappingRoot(parentClassifierNode.getModelObject());
                }
                // Now we check for is Virtual?
            }
        }
        
        return null;
    }
}
