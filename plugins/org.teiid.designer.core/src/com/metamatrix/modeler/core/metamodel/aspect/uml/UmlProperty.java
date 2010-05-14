/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.uml;

/**
 * UmlProperty
 */
public interface UmlProperty extends UmlDiagramAspect {
    //  Show Mask Constants    
    public final static int SIGNATURE_NAME = 1;
    public final static int SIGNATURE_STEROTYPE = 2;
    public final static int SIGNATURE_TYPE = 4;
    public final static int SIGNATURE_INITIAL_VALUE = 8;
    public final static int SIGNATURE_PROPERTIES = 16;
    
    
    /**
     * Return true if the specified property represents
     * an association end otherwise return false;
     * @param prop
     * @return true if the property is an association end.
     */
    boolean isAssociationEnd(Object property);
}
