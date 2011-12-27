/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;


/** 
 * The ModelObjectCollector is a simple utility used to return all EObject instances
 * in a specified org.eclipse.emf.ecore.resource.Resource.  The implementation is 
 * intended to efficiently walk the model while tolerating ConcurrentModificationException
 * if the resource is modified while the EObject instances are collected.
 * @since 4.2
 */
public class ModelObjectCollector {
    
    private final Resource resource;
    private final Class eObjectType;
    private List result;

    /** 
     * Construct an instance of ModelObjectCollector
     * @param resource the emf resource to process
     * @since 4.2
     */
    public ModelObjectCollector(final Resource resource) {
        this(resource, null);
    }
    
    /** 
     * Construct an instance of ModelObjectCollector
     * @param resource the emf resource to process
     * @param objectType the Class that the collected EObjects must be instances of
     * @since 4.2
     */
    public ModelObjectCollector(final Resource resource,
                                final Class objectType) {
        super();
        this.resource = resource;
        this.eObjectType = objectType;
        this.result = null;
    }
    /**
     * Return a list of all <code>EObject</code> instances in the resource.
     */
    public List getEObjects() {
        if (this.result == null) {
            this.result = new ArrayList();
            
            final Object[] children = this.resource.getContents().toArray();
            for (int i = 0; i != children.length; ++i) {
                final Object obj = children[i];
                if ( obj instanceof EObject ) {
                    this.result.add(obj);
                    addChildrenToList( (EObject)obj, this.result );
                }
            }
            // If a specific type of EObject is requested, filter out all instances
            // that are not of the correct type.
            if (this.eObjectType != null) {
                for (final Iterator iter = this.result.iterator(); iter.hasNext();) {
                    final EObject obj = (EObject)iter.next();
                    if ( !this.eObjectType.isInstance(obj) ) {
                        iter.remove();
                    }
                }
            }
        }
        return this.result;
    }

    private static void addChildrenToList( final EObject eObject, final List contents ) {
        if (eObject == null || contents == null) {
            return;
        }

        int currentIndex = contents.size();
        try {
            List children = eObject.eContents();
            for (final Iterator iter = children.iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if ( obj instanceof EObject ) {
                    contents.add(obj);
                }
            }
        } catch (ConcurrentModificationException err) {
            for (int i = currentIndex, n = contents.size(); i < n; i++) {
                contents.remove(i);
            }
            final Object[] children = eObject.eContents().toArray();
            for (int i = 0; i != children.length; ++i) {
                final Object obj = children[i];
                if ( obj instanceof EObject ) {
                    contents.add(obj);
                }
            }
            
        }
        
        for (int i = currentIndex, n = contents.size(); i < n; i++) {
            final EObject obj = (EObject)contents.get(i);
            addChildrenToList(obj,contents);
        }
    }
    
}
