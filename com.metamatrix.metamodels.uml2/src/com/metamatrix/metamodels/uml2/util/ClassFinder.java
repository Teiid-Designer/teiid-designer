/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Package;

/**
 * ClassFinder
 */
public class ClassFinder extends UmlEntityFinder {

    /**
     * Construct an instance of ClassFinder.
     */
    public ClassFinder() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof org.eclipse.uml2.uml.Class) {
            found(object);
            return true;
        }
        // Classes are contained by Packages/Namespaces
        if (object instanceof Package) {
            return true;
        }
        if (object instanceof Namespace) {
            return true;
        }
        if (object instanceof Model) {
            return true;
        }
        return false;
    }

}
