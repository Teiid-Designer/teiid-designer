/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.dummy;

import com.metamatrix.modeler.diagram.ui.model.AbstractLocalDiagramModelNode;

/**
 * DummyDiagramNode
 */
public class DummyDiagramNode extends AbstractLocalDiagramModelNode {

    public DummyDiagramNode( ) {
        super( null, null );
    }
        
    @Override
    public String toString() {
        return "DummyDiagramNode(" + getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }


    

}
