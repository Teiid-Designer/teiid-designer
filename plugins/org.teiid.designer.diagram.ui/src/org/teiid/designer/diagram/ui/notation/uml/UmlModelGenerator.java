/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.notation.uml;

//import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlAssociation;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.notation.NotationModelGenerator;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlAssociationNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlAttributeNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlClassifierNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlOperationNode;
import org.teiid.designer.diagram.ui.notation.uml.model.UmlPackageNode;
import org.teiid.designer.metamodels.diagram.Diagram;


/**
 * @author mdrilling Factory for creating UML Model Nodes Responsibility for creating and managing the diagram is at the
 *         DiagramModelFactory level.
 */
public class UmlModelGenerator implements NotationModelGenerator {
    // private final static int SOURCE = 0;
    // private final static int TARGET = 1;

    /**
     * Create a DiagramModelNode.
     */
    @Override
	public DiagramModelNode createModel( Object inputEObject,
                                         Diagram diagramContainerObject ) {

        // Return null if the baseObject is not a EObject
        if (!(inputEObject instanceof EObject)) {
            return null;
        }
        EObject eObj = (EObject)inputEObject;

        DiagramModelNode newDiagramModelNode = null;

        // DiagramEntity diagramEntity = getDiagramEntity(eObj, diagramContainerObject);

        // Get UML Aspect for this EObject
        MetamodelAspect someAspect = getUmlAspect(eObj);

        if (someAspect != null) {
            // Create Model Node for the aspect type
            if (someAspect instanceof UmlPackage) {
                newDiagramModelNode = new UmlPackageNode(diagramContainerObject, eObj, (UmlPackage)someAspect);
            } else if (someAspect instanceof UmlClassifier) {
                newDiagramModelNode = new UmlClassifierNode(diagramContainerObject, eObj, (UmlClassifier)someAspect);
            }
        }
        return newDiagramModelNode;
    }

    /**
     * Create a DiagramModelNode.
     */
    @Override
	public DiagramModelNode createChildModel( DiagramModelNode parentDiagramModelNode,
                                              Object inputEObject ) {
        // Return null if the baseObject is not a EObject
        if (!(inputEObject instanceof EObject)) {
            return null;
        }

        DiagramModelNode newDiagramModelNode = null;

        if (parentDiagramModelNode instanceof UmlPackageNode) {
            // Do Nothing
        } else if (parentDiagramModelNode instanceof UmlClassifierNode) {
            newDiagramModelNode = ((UmlClassifierNode)parentDiagramModelNode).addChild((EObject)inputEObject);
        } else if (parentDiagramModelNode.getModelObject() instanceof Diagram) {
            newDiagramModelNode = createModel(inputEObject, (Diagram)parentDiagramModelNode.getModelObject());
            if (newDiagramModelNode != null) newDiagramModelNode.setParent(parentDiagramModelNode);
        } else {
            String message = "UmlModelGenerator.createChildModel() Couldn't Add child to parent Node = " + parentDiagramModelNode; //$NON-NLS-1$
            DiagramUiConstants.Util.log(IStatus.ERROR, message);
        }
        return newDiagramModelNode;
    }

    /**
     * Helper method to get the UmlAspect given an EObject
     */
    public MetamodelAspect getUmlAspect( EObject eObject ) {

        return DiagramUiPlugin.getDiagramAspectManager().getUmlAspect(eObject);
    }

    @Override
	public boolean isAssociation( EObject eObj ) {
        MetamodelAspect aspect = getUmlAspect(eObj);
        return aspect instanceof UmlAssociation;
    }

    @Override
	public List getAssociations( List candidateAssociationNodes,
                                 List diagramModelNodes ) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.notation.NotationModelGenerator#performUpdate(org.teiid.designer.diagram.ui.model.DiagramModelNode, org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void performUpdate( DiagramModelNode targetNode,
                               Notification setNotification ) {
        if (targetNode instanceof UmlClassifierNode) {
            ((UmlClassifierNode)targetNode).refreshForNameChange();
            ((UmlClassifierNode)targetNode).refreshForPathChange();
        } else if (targetNode instanceof UmlPackageNode) {
            ((UmlPackageNode)targetNode).refreshForNameChange();
            ((UmlPackageNode)targetNode).refreshForPathChange();
        } else if (targetNode instanceof UmlAttributeNode) {
            ((UmlAttributeNode)targetNode).refreshForNameChange();
        } else if (targetNode instanceof UmlAssociationNode) {
            ((UmlAssociationNode)targetNode).refreshForNameChange();
        } else if (targetNode instanceof UmlOperationNode) {
            ((UmlOperationNode)targetNode).refreshForNameChange();
        }

    }

}
