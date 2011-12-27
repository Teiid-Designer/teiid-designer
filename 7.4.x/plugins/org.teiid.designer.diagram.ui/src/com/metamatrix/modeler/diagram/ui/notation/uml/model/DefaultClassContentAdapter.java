/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.model;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.diagram.Diagram;

/**
 * DefaultClassContentAdapter
 */
public class DefaultClassContentAdapter implements IClassifierContentAdapter {

    /**
     * Construct an instance of DefaultClassContentAdapter.
     * 
     */
    public DefaultClassContentAdapter() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.notation.uml.model.IClassifierContentAdapter#showInnerClasses(org.eclipse.emf.ecore.EObject)
     */
    public boolean showInnerClasses(EObject classifierEObject, Diagram diagram) {
        return true;
    }

}
