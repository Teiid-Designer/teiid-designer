/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.modeler.core.util.ModelVisitor;

/**
 * WebServiceComponentFinder
 */
public abstract class WebServiceComponentFinder implements ModelVisitor {

    private final List objects;
    // The HashSet is only used to efficiently check whether
    // the List contains a given WebServiceComponent - calling
    // List.contains is very expensive when the list is large.
    private final HashSet uniqueObjects;

    /**
     * Construct an instance of UniqueKeyFinder.
     */
    public WebServiceComponentFinder() {
        super();
        this.objects = new ArrayList();
        this.uniqueObjects = new HashSet();
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
        return this.objects;
    }

    protected void found( final WebServiceComponent entity ) {
        // Add only non-null, unique entries to the list (ref defect #11708)
        if (entity != null && !this.uniqueObjects.contains(entity)) {
            this.objects.add(entity);
            this.uniqueObjects.add(entity);
        }
    }

    protected void found( final List entities ) { // NO_UCD
        // if ( entities != null ) {
        // this.objects.addAll(entities);
        // }
        // Add only non-null, unique entries to the list (ref defect #11708)
        for (Iterator iter = entities.iterator(); iter.hasNext();) {
            final WebServiceComponent entity = (WebServiceComponent)iter.next();
            found(entity);
        }
    }

    public void removeContainer( Object container ) {
        if (container instanceof EObject) {
            if (this.objects != null) {
                this.objects.remove(container);
                this.uniqueObjects.remove(container);
            }
        }
    }

}
