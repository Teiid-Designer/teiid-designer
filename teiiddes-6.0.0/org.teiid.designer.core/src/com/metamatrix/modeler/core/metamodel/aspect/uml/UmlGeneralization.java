/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.uml;

import org.eclipse.emf.ecore.EObject;

/**
 * UmlGeneralization - A generalization is a taxonomic relationship between a more general classifier 
 * and a more specific classifier. Eachinstance of the specific classifier is also an instance of the 
 * general classifier. Thus, the specific classifier indirectly has features of the more general 
 * classifier.
 */
public interface UmlGeneralization extends UmlRelationship {
    
    /**
     * Returns the value of the '<em><b>Specific</b></em>' container reference.
     * References the specializing classifier in the Generalization relationship. 
     * Subsets DirectedRelationship::source and Element::owner.
     * @param the eObject
     * @return the value of the '<em>Specific</em>' container reference.
     */
    EObject getSpecific(Object eObject);
    
    /**
     * Returns the value of the '<em><b>General</b></em>' reference.
     * References the general classifier in the Generalization relationship. 
     * Subsets DirectedRelationship::target.
     * @param the eObject 
     * @return the value of the '<em>General</em>' reference.
     */
    EObject getGeneral(Object eObject);
    
//    /**
//     * Returns the value of the '<em><b>Generalization Set</b></em>' reference list.
//     * The list contents are of type {@link org.eclipse.uml2.GeneralizationSet}.
//     * It is bidirectional and its opposite is '{@link org.eclipse.uml2.GeneralizationSet#getGeneralization <em>Generalization</em>}'.
//     * @return the value of the '<em>Generalization Set</em>' reference list.
//     */
//    List getGeneralizationSet();

}
