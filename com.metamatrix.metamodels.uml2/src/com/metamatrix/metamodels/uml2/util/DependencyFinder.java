/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Package;

/**
 * AssociationFinder
 */
public class DependencyFinder extends UmlEntityFinder {

    /**
     * Construct an instance of AssociationFinder.
     */
    public DependencyFinder() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof Dependency) {
            found(object);
            return false;
        }
        // Dependencies are contained by Packages
        if (object instanceof Package) {
            return true;
        }
        return false;
    }

}
