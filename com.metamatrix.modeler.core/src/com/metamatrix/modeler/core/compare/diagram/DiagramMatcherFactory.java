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

package com.metamatrix.modeler.core.compare.diagram;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramPackage;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;


/** 
 * DiagramMatcherFactory
 */
public class DiagramMatcherFactory implements EObjectMatcherFactory {

    private final List standardMatchers;

    /** 
     * DiagramMatcherFactory
     * @since 4.2
     */
    public DiagramMatcherFactory() {
        super();
        this.standardMatchers = new LinkedList();
        this.standardMatchers.add( new DiagramRootObjectMatcher() );
        this.standardMatchers.add( new PresentationEntityNameToNameMatcher() );
        this.standardMatchers.add( new PresentationEntityNameToNameIgnoreCaseMatcher() );
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     * @since 4.2
     */
    public List createEObjectMatchersForRoots() {
        // diagram entities are root level objects
        return standardMatchers;
    }

    /** 
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     * @since 4.2
     */
    public List createEObjectMatchers(EReference reference) {
	    // Make sure the reference is in the diagram metamodel ...
	    final EClass containingClass = reference.getEContainingClass();
	    final EPackage metamodel = containingClass.getEPackage();
	    if (!DiagramPackage.eINSTANCE.equals(metamodel)) {
	         //The feature isn't in the transformation or mapping metamodel so return nothing ...
	        return Collections.EMPTY_LIST;
	    }

	    // Create the appropriate matchers ...
        final List results = new LinkedList();
        final int featureId = reference.getFeatureID();
	    if(containingClass.getInstanceClass().equals(DiagramContainer.class)) {
	        if(featureId == DiagramPackage.DIAGRAM_CONTAINER__DIAGRAM) {
	            results.add( new DiagramTargetMatcher() );	            
	        }
	    } else if(containingClass.getInstanceClass().equals(Diagram.class)) {
	        if(featureId == DiagramPackage.DIAGRAM__DIAGRAM_ENTITY ||
	           featureId == DiagramPackage.DIAGRAM__DIAGRAM_LINKS) {
                results.add( new DiagramEntityTargetMatcher() );
	        }
	    }
        return results;
    }
}
