/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.util.ModelVisitor;

/**
 * UniqueKeyFinder
 */
public abstract class UmlEntityFinder implements ModelVisitor {

    private final Collection objects;

    /**
     * Construct an instance of UniqueKeyFinder.
     */
    public UmlEntityFinder() {
        super();
        this.objects = new HashSet();
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     */
    public boolean visit( Resource resource ) {
        return true;
    }

    /**
     * Return the objects that were found by this finder.
     * 
     * @return the Collection of objects; never null
     */
    public Collection getObjects() {
        return objects;
    }

    protected void found( final EObject entity ) {
        // Add only non-null, unique entries to the collection
        if (entity != null) {
            this.objects.add(entity);
        }
    }

    protected void found( final Collection entities ) {
        // Add only non-null, unique entries to the collection
        for (Iterator iter = entities.iterator(); iter.hasNext();) {
            final EObject entity = (EObject)iter.next();
            found(entity);
        }
    }

}
