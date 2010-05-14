/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metamodel;

import org.eclipse.emf.ecore.EClass;

/**
 * MetamodelRootClass
 */
public class MetamodelRootClass {
    
    private final EClass rootEClass;
    private int maxOccurs;
    

    /**
     * Construct an instance of MetamodelRootClass.
     * 
     */
    public MetamodelRootClass(final EClass eClass, final int maxOccurs) {
        this.rootEClass = eClass;
        this.maxOccurs  = maxOccurs;
    }

    /**
     * @return
     */
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * @return
     */
    public EClass getEClass() {
        return rootEClass;
    }

}
