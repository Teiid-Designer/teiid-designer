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
