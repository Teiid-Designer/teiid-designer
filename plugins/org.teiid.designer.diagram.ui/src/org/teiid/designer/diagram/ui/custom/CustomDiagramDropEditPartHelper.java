/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.editor.DiagramEditorUtil;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.part.DropEditPartHelper;
import org.teiid.designer.diagram.ui.util.DiagramUiUtilities;
import org.teiid.designer.metamodels.diagram.Diagram;



/** 
 * This class provides the Add To Diagram capability for DND on to Custom diagrams
 * @since 8.0
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
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#drop(org.eclipse.draw2d.geometry.Point, java.util.List)
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
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#allowsDrop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.3
     */
    @Override
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
