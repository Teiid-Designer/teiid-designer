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

package com.metamatrix.modeler.mapping.ui.model;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.AbstractLocalDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.ExpandableDiagram;
import com.metamatrix.modeler.mapping.ui.PluginConstants;

/**
 * MappingDiagramNode
 */
public class MappingDiagramNode extends AbstractLocalDiagramModelNode implements ExpandableDiagram {
	private int coarseCheck = -1;
	private boolean isCoarse = false;
    private DiagramViewer viewer;
    private int currentYOrigin = 0;
	
	public MappingDiagramNode(EObject modelObject, String diagramName) {
		super(modelObject, diagramName);
	}

	@Override
    public String toString() {
		return "MappingDiagramNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean isCoarse() {
		if( coarseCheck < 0 ) {
			Diagram diagram = ((Diagram)getModelObject());
			if( diagram.getType() != null &&
				diagram.getType().equals(PluginConstants.MAPPING_DIAGRAM_TYPE_ID))
				isCoarse = true;
			coarseCheck = 1;
		}
		return isCoarse;
	}
    
    
    /** 
     * @return Returns the viewer.
     * @since 4.2
     */
    public DiagramViewer getViewer() {
        return this.viewer;
    }
    /** 
     * @param viewer The viewer to set.
     * @since 4.2
     */
    public void setViewer(DiagramViewer viewer) {
        this.viewer = viewer;
    }
    /** 
     * @return Returns the currentYOrigin.
     * @since 4.2
     */
    public int getCurrentYOrigin() {
        return this.currentYOrigin;
    }
    /** 
     * @param currentYOrigin The currentYOrigin to set.
     * @since 4.2
     */
    public void setCurrentYOrigin(int currentYOrigin) {
        this.currentYOrigin = currentYOrigin;
    }

    public boolean canExpand() {
    	return isCoarse();
    }
    
	public void collapseAll() {
		// TODO Auto-generated method stub
		
	}

	public void collapse(Object child) {
		// TODO Auto-generated method stub
		
	}

	public void expandAll() {
		// TODO Auto-generated method stub
		
	}

	public void expand(Object child) {
		// TODO Auto-generated method stub
		
	}
}
