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

package com.metamatrix.metamodels.uml2.compare;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import org.eclipse.uml2.uml.LiteralBoolean;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralNull;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.ValueSpecification;

import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;

/**
 * UmlValueSpecificationMatcher
 */
public class UmlValueSpecificationMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of UmlValueSpecificationMatcher.
     * 
     */
    public UmlValueSpecificationMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(final List inputs, final List outputs, 
                                    final Mapping mapping, final MappingFactory factory) {
        // Don't do value specifications under the root
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings(final EReference reference, final List inputs, final List outputs, 
                            final Mapping mapping, final MappingFactory factory) {
        if ( !reference.isMany() ) {
            final EObject obj1 = (EObject)inputs.get(0);
            final EObject obj2 = (EObject)outputs.get(0);
            final EClass eclass1 = obj1.eClass();
            final EClass eclass2 = obj2.eClass();
            if ( eclass1 != eclass2 ) {
                return;
            }
            if ( equals(obj1,obj2) ) {
                inputs.clear();
                outputs.clear();
                addMapping(obj1,obj2,mapping,factory);
            }
            return;
        }
        // Should only be used when all the values are instances of ValueSpecification ...
        // Loop over the inputs and accumulate the UUIDs ...
        final Iterator inputIter = inputs.iterator();
        while (inputIter.hasNext()) {
            final EObject input = (EObject)inputIter.next();
            final Iterator outputIter = outputs.iterator();
            while (outputIter.hasNext()) {
                final EObject output = (EObject)outputIter.next();
                if ( equals(output,input) ) {
                    inputIter.remove();
                    outputIter.remove();
                    addMapping(input,output,mapping,factory);
                    break;
                }
            }
        }
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public boolean equals(final Object o1, final Object o2) {
        if ( o1 instanceof ValueSpecification && o2 instanceof ValueSpecification ) {
            final EClass eclass1 = ((ValueSpecification)o1).eClass();
            final EClass eclass2 = ((ValueSpecification)o2).eClass();
            if ( eclass1 != eclass2 ) {
                return false;
            }
            if ( o1 instanceof LiteralUnlimitedNatural && o1 instanceof LiteralUnlimitedNatural ) {
                return true;
            }
            if ( o1 instanceof LiteralBoolean && o1 instanceof LiteralBoolean ) {
                final LiteralBoolean lb1 = (LiteralBoolean)o1;
                final LiteralBoolean lb2 = (LiteralBoolean)o2;
                final boolean b1 = lb1.isValue();
                final boolean b2 = lb2.isValue();
                return ( b1 == b2 );
            }
            if ( o1 instanceof LiteralInteger && o1 instanceof LiteralInteger ) {
                final LiteralInteger lit1 = (LiteralInteger)o1;
                final LiteralInteger lit2 = (LiteralInteger)o2;
                final int v1 = lit1.getValue();
                final int v2 = lit2.getValue();
                return ( v1 == v2 );
            }
            if ( o1 instanceof LiteralNull && o1 instanceof LiteralNull ) {
                return true;
            }
            if ( o1 instanceof LiteralString && o1 instanceof LiteralString ) {
                final LiteralString lit1 = (LiteralString)o1;
                final LiteralString lit2 = (LiteralString)o2;
                final String v1 = lit1.getValue();
                final String v2 = lit2.getValue();
                if ( v1 == null ) {
                    if ( v2 == null ) {
                        return true;
                    }
                    return false;
                }
                if ( v2 == null ) {
                    return false;
                }
                return v1.equals(v2);
            }
            final ValueSpecification val1 = (ValueSpecification)o1;
            final ValueSpecification val2 = (ValueSpecification)o2;
            final String v1 = val1.stringValue();
            final String v2 = val2.stringValue();
            if ( v1 == null ) {
                if ( v2 == null ) {
                    return true;
                }
                return false;
            }
            if ( v2 == null ) {
                return false;
            }
            return v1.equals(v2);
        }
        return false;
    }
    
}
