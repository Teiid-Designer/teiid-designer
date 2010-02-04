/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.compare;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.transformation.MappingClassObject;
import com.metamatrix.modeler.core.compare.AbstractEObjectNameMatcher;


/** 
 * MappingClassObjectNameToNameIgnoreCaseMatcher
 */
public class MappingClassObjectNameToNameIgnoreCaseMatcher extends AbstractEObjectNameMatcher {

    public MappingClassObjectNameToNameIgnoreCaseMatcher() {
        super();
    }

    @Override
    protected String getInputKey( final EObject entity ) {
        if(entity instanceof MappingClassObject) {
            final String name = ((MappingClassObject)entity).getName();
            if(name != null) {
                return name.toUpperCase();
            }
        }
        return null;
    }

    @Override
    protected String getOutputKey( final EObject entity ) {
        if(entity instanceof MappingClassObject) {
            final String name = ((MappingClassObject)entity).getName();
            if(name != null) {
                return name.toUpperCase();
            }
        }
        return null;
    }
}
