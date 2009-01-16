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

package com.metamatrix.modeler.mapping.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;


/** 
 * @since 5.0.2
 */
public class UmlEnumeratedTypeClassifierNode extends UmlClassifierNode {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public UmlEnumeratedTypeClassifierNode(Diagram theDiagram,
                                           EObject theModelObject,
                                           UmlClassifier theAspect) {
        super(theDiagram, theModelObject, theAspect);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode#getChildren(org.eclipse.emf.ecore.EObject)
     * @since 5.0.2
     */
    @Override
    protected List getChildren(EObject theParent) {
        // return the enumeration values as the children
        XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)theParent;
        List facets = type.getEnumerationFacets();
        List result = new ArrayList(facets.size());

        for (int numValues = facets.size(), i = 0; i < numValues; ++i) {
            result.add(facets.get(i));
        }

        return result;
    }
    
}
