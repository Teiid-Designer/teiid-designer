/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
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
