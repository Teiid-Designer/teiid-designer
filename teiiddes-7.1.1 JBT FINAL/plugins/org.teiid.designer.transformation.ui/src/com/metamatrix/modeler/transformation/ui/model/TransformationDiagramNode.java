/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.model;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.diagram.ui.model.AbstractLocalDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * TransformationDiagramNode
 */
public class TransformationDiagramNode extends AbstractLocalDiagramModelNode {

    public TransformationDiagramNode( EObject modelObject, String diagramName) {
        super( modelObject, diagramName );
    }
    
    @Override
    public String toString() {
        return "TransformDiagramNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    @Override
    public boolean wasLayedOut() {
        // This method has to override abstract class
        // if any one of it's children answer true to same method
        // then we assume that this diagram had entities defined and
        // was already layed out at one time.
        List children = getChildren();
        Iterator iter = children.iterator();
        DiagramModelNode nextChild = null;
        while( iter.hasNext()) {
            nextChild = (DiagramModelNode)iter.next();
            if( nextChild.wasLayedOut())
                return true;
        }
        
        return false;
    }

    @Override
    public void recoverObjectProperties(){
        // This method has to override abstract class
        // if any one of it's children answer true to wasLayedOut
        // then we assume that this diagram had entities defined and
        // was already layed out at one time.
        // This should be called after the initial layout method only!!!
        List children = getChildren();
        Iterator iter = children.iterator();
        DiagramModelNode nextChild = null;
        while( iter.hasNext()) {
            nextChild = (DiagramModelNode)iter.next();
            if( nextChild.wasLayedOut())
                nextChild.recoverObjectProperties();
        }
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addChild(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode)
     */
    @Override
    public void addChild(DiagramModelNode child) {
        child.setParent(this);
        
        super.addChild(child);
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addChildren(java.util.List)
     */
    @Override
    public void addChildren(List newChildren) {
        if( newChildren != null && !newChildren.isEmpty() ) {

            Object nextChild;
            Iterator iter = newChildren.iterator();
            while( iter.hasNext() ) {
                nextChild = iter.next();
                ((DiagramModelNode)nextChild).setParent(this);
            }
            super.addChildren(newChildren);
        }
    }
}
