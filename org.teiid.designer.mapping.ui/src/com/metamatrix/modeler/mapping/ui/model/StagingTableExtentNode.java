/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.model;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.mapping.ui.PluginConstants;

/**
 * StagingTableExtentNode
 */
public class StagingTableExtentNode extends MappingExtentNode {

    public StagingTableExtentNode(Diagram diagramModelObject, EObject modelObject, boolean isCoarse) {
        super(diagramModelObject, modelObject, isCoarse);
        setName("ST"); //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return "StagingTableExtentNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

	@Override
    public boolean isOnCoarseMappingDiagram() {
		boolean isCoarse = false;
		// Assume that this node is a child of the diagram.
		DiagramModelNode parentNode = getParent();
		if( parentNode != null && parentNode instanceof MappingDiagramNode &&
			parentNode.getModelObject() instanceof Diagram ) {
			Diagram diagram = (Diagram)parentNode.getModelObject();
			if(diagram.getType() == PluginConstants.MAPPING_DIAGRAM_TYPE_ID ) {
				isCoarse = true;
			}
		}
		return isCoarse;
	}
	
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getModelObject()
	 */
	@Override
    public EObject getModelObject() {
		if( isOnCoarseMappingDiagram() ) {
			return super.getModelObject();
		}
		
		return getExtent().getMappingReference();
	}

}
