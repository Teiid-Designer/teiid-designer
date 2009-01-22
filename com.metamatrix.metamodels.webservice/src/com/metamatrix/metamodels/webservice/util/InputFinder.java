/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;

/**
 * @since 4.2
 */
public class InputFinder extends WebServiceComponentFinder {

    /**
     * @since 4.2
     */
    public InputFinder() {
        super();
    }

    /**
     * This method accumulates the {@link Input} instances. The implementation takes as many shortcuts as possible to prevent
     * unnecessarily visiting unrelated objects.
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        // Inputs are contained by Operations, Interfaces and Resources
        if (object instanceof Input) {
            found((Input)object);
            return false;
        }
        if (object instanceof Operation) {
            // catalogs will contain tables
            return true;
        }
        if (object instanceof Interface) {
            // schemas will contain tables
            return true;
        }
        return false;
    }

}
