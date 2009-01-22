/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import java.util.Collection;

/**
 * DependencyAspect
 */
public interface DependencyAspect extends MetamodelAspect {
    /** 
     * Return the collection of all Suppliers for this Aspect
     * @return Collection of suppliers
     */
    Collection getSuppliers();
}
