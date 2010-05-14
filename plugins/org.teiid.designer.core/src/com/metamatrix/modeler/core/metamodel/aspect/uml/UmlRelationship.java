/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.uml;

/**
 * UmlRelationship
 */
public interface UmlRelationship extends UmlDiagramAspect {
    
    /**
     * Return the name of the relationship if it exists otherwise
     * and empty string will be returned.
     * @param eObject
     * @return
     */
    String getName(Object eObject);
    
    /**
     * Return the tooltip string displayed in the diagram for
     * this relationship.
     * @param eObject
     * @return
     */
    String getToolTip(Object eObject);

}
