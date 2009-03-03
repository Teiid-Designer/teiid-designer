/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.modeler.core.util.ModelVisitor;

/**
 * RelationshipEntityFinder.java
 */
public abstract class RelationshipEntityFinder implements ModelVisitor {

    private final List objects;

    /**
     * Construct an instance of UniqueKeyFinder.
     */
    public RelationshipEntityFinder() {
        super();
        this.objects = new ArrayList(11);
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
     * @return the List of objects; never null
     */
    public List getObjects() {
        return objects;
    }

    protected void found( final RelationshipEntity entity ) {
        // Add only non-null, unique entries to the list (ref defect #11708)
        if (entity != null && !this.objects.contains(entity)) {
            this.objects.add(entity);
        }
    }

    protected void found( final List entities ) {
        // if ( entities != null ) {
        // this.objects.addAll(entities);
        // }
        // Add only non-null, unique entries to the list (ref defect #11708)
        for (Iterator iter = entities.iterator(); iter.hasNext();) {
            final RelationshipEntity entity = (RelationshipEntity)iter.next();
            found(entity);
        }
    }
}
