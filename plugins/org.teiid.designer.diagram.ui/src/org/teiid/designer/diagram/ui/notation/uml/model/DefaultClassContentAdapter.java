/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.notation.uml.model;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.diagram.Diagram;

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
     * @See org.teiid.designer.diagram.ui.notation.uml.model.IClassifierContentAdapter#showInnerClasses(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean showInnerClasses(EObject classifierEObject, Diagram diagram) {
        return true;
    }

}
