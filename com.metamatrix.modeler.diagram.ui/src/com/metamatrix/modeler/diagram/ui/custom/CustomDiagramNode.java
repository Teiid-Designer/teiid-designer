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

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.diagram.ui.model.AbstractLocalDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * PackageDiagramNode
 */
public class CustomDiagramNode extends AbstractLocalDiagramModelNode {

    public CustomDiagramNode( EObject modelObject, String diagramName) {
        super( modelObject, diagramName );
    }
        
    @Override
    public String toString() {
        return "CustomDiagramNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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

