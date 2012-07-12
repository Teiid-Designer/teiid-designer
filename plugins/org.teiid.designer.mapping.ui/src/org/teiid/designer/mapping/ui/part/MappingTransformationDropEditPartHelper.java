/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.part;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;
import org.teiid.designer.diagram.ui.util.DiagramUiUtilities;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.ui.actions.TransformationSourceManager;
import org.teiid.designer.transformation.ui.part.TransformationDropEditPartHelper;
import org.teiid.designer.transformation.ui.part.TransformationEditPart;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


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
