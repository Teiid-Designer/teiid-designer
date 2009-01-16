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

package com.metamatrix.modeler.core.compare;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

/**
 * AbstractEObjectMatcher
 */
public abstract class AbstractEObjectMatcher implements EObjectMatcher {

    /**
     * Construct an instance of AbstractEObjectMatcher.
     * 
     */
    public AbstractEObjectMatcher() {
        super();
    }
    
    protected void addMapping( final EObject input, final EObject output, 
                               final Mapping parentMapping, final MappingFactory factory ) {
        final Mapping nested = factory.createMapping();
        nested.getOutputs().add(output);
        nested.getInputs().add(input);
        parentMapping.getNested().add(nested);
    }

}
