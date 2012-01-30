/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * Finds all the model objects whose class name matches one of the specified ones.
 */
public class ModelObjectClassNameVisitor implements ModelVisitor {

    private final Collection<String> classNames;

    private Collection<EObject> result;

    /**
     * @param classNames the collection of class names to match (cannot be <code>null</code> or empty)
     */
    public ModelObjectClassNameVisitor( Collection<String> classNames ) {
        CoreArgCheck.isNotNull(classNames, "classNames is null"); //$NON-NLS-1$
        CoreArgCheck.isTrue(!classNames.isEmpty(), "className is empty"); //$NON-NLS-1$
        this.classNames = classNames;
    }

    /**
     * @return the matching EObjects (never <code>null</code>)
     */
    public Collection<EObject> getResult() {
        return ((this.result == null) ? Collections.<EObject> emptyList() : this.result);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean visit( EObject object ) {
        String className = object.getClass().getName();

        if (this.classNames.contains(className)) {
            if (this.result == null) {
                this.result = new ArrayList<EObject>();
            }

            this.result.add(object);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     */
    @Override
    public boolean visit( Resource resource ) {
        return true;
    }

}
