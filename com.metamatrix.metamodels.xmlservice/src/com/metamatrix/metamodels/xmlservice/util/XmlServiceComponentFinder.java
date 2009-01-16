/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.metamodels.xmlservice.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.modeler.core.util.ModelVisitor;

/**
 * XmlServiceComponentFinder
 */
public abstract class XmlServiceComponentFinder implements ModelVisitor {

    private final List objects;
    // The HashSet is only used to efficiently check whether
    // the List contains a given XmlServiceComponent - calling
    // List.contains is very expensive when the list is large.
    private final HashSet uniqueObjects;

    /**
     * Construct an instance of XmlServiceComponentFinder.
     */
    public XmlServiceComponentFinder() {
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

    protected void found( final XmlServiceComponent entity ) {
        // Add only non-null, unique entries to the list (ref defect #11708)
        if (entity != null && !this.uniqueObjects.contains(entity)) {
            this.objects.add(entity);
            this.uniqueObjects.add(entity);
        }
    }

    protected void found( final List entities ) {
        // if ( entities != null ) {
        // this.objects.addAll(entities);
        // }
        // Add only non-null, unique entries to the list (ref defect #11708)
        for (Iterator iter = entities.iterator(); iter.hasNext();) {
            final XmlServiceComponent entity = (XmlServiceComponent)iter.next();
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
