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

package com.metamatrix.modeler.diagram.ui.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DropEditPartHelper;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;


/** 
 * This class provides the Add To Diagram capability for DND on to Custom diagrams
 * @since 5.0
 */
public class CustomDiagramDropEditPartHelper extends DropEditPartHelper {
    private CustomDiagramEditPart customDiagramEditPart;
    
    /** 
     * 
     * @since 4.3
     */
    public CustomDiagramDropEditPartHelper(CustomDiagramEditPart customDiagramEditPart) {
        super();
        
        this.customDiagramEditPart = customDiagramEditPart;
    }

    /**
     * Implemented to determine which role container to drop the incoming eObject list. 
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#drop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.2
     */
    @Override
    public void drop(Point dropPoint, List dropList) {
        // Get some pertinent information. Diagram, Diagram's model node & DiagramEditor
        Diagram diagram = (Diagram)customDiagramEditPart.getModelObject();
        DiagramModelNode diagramRootModelNode = (DiagramModelNode)customDiagramEditPart.getModel();
        DiagramEditor diagramEditor = DiagramEditorUtil.getDiagramEditor(diagram);
        
        // Now add the objects to the custom diagram
        List addedObjects = CustomDiagramContentHelper.addToCustomDiagram(diagram, dropList, diagramEditor, this);
        
        // Let's do some layout here based on the dropPoint
        
        layoutNewNodes(dropPoint, addedObjects, diagram, diagramRootModelNode);
        
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#allowsDrop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.3
     */
    public boolean allowsDrop(Object target,
                              List dropList) {
        if( target instanceof CustomDiagramEditPart ) {
            return dropList != null && !dropList.isEmpty();
        }
        return false;
    }
    
    private void layoutNewNodes(Point dropPoint, List addedObjects, Diagram diagram, DiagramModelNode diagramRootModelNode) {
        List diagramNodes = new ArrayList(addedObjects.size());
        for( Iterator iter = addedObjects.iterator(); iter.hasNext(); ) {
            EObject nextEObj = (EObject)iter.next();
            DiagramModelNode nextNode = DiagramUiUtilities.getDiagramModelNode(nextEObj, diagramRootModelNode);
            if( nextNode != null ) {
                diagramNodes.add(nextNode);
            }
        }
        Point startPoint = new Point(dropPoint);
        for( Iterator iter = diagramNodes.iterator(); iter.hasNext(); ) {
            DiagramModelNode nextNode = (DiagramModelNode)iter.next();
            nextNode.setPosition(startPoint);
            startPoint.x += 20;
            startPoint.y += 20;
        }
    }
}
