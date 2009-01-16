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

package com.metamatrix.metamodels.uml2.util;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Type;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;

/**
 * RelationalUtil
 */
public class Uml2Util {

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Prevent allocation
     */
    private Uml2Util() {
        super();
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * Add any {@link org.eclipse.uml2.Class} instances found under the supplied container
     * @param container the EObject or Resource under which the classes are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the classes that were found; may not be null
     */
    public static Collection findClasses( final Object container, final int depth  ) {
        final ClassFinder finder = new ClassFinder();
        executeVisitor(container,finder,depth);
        return finder.getObjects();
    }

    /**
     * Add any {@link org.eclipse.uml2.Association} instances found under the supplied container
     * @param container the EObject or Resource under which the assocations are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the assocations that were found; may not be null
     */
    public static Collection findAssocations( final Object container, final int depth  ) {
        final AssociationFinder finder = new AssociationFinder();
        executeVisitor(container,finder,depth);
        return finder.getObjects();
    }

    /**
     * Add any {@link org.eclipse.uml2.Dependency} instances found under the supplied container
     * @param container the EObject or Resource under which the assocations are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the dependency relationships that were found; may not be null
     */
    public static Collection findDependencies( final Object container, final int depth  ) {
        final DependencyFinder finder = new DependencyFinder();
        executeVisitor(container,finder,depth);
        return finder.getObjects();
    }
    
    /**
     * Helper method to create any possible association between two elements. 
     * @param firstEndType
     * @param firstEndIsNavigable
     * @param firstEndAggregation
     * @param firstEndName
     * @param firstEndLowerBound
     * @param firstEndUpperBound
     * @param secondEndType
     * @param secondEndIsNavigable
     * @param secondEndAggregation
     * @param secondEndName
     * @param secondEndLowerBound
     * @param secondEndUpperBound
     * @return
     */
    public static Association createAssociation(final Type type1,
                                                final boolean end1IsNavigable,
                                                final AggregationKind end1Aggregation,
                                                final String end1Name,
                                                final int end1LowerBound,
                                                final int end1UpperBound,
                                                final Type type2,
                                                final boolean end2IsNavigable,
                                                final AggregationKind end2Aggregation,
                                                final String end2Name,
                                                final int end2LowerBound,
                                                final int end2UpperBound) {
        ArgCheck.isNotNull(type1);

        return type1.createAssociation( end1IsNavigable,
                                        end1Aggregation,
                                        end1Name,
                                        end1LowerBound,
                                        end1UpperBound,
                                        type2,
                                        end2IsNavigable,
                                        end2Aggregation,
                                        end2Name,
                                        end2LowerBound,
                                        end2UpperBound);
 
    }
    
    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================
    
    protected static void executeVisitor( final Object container, final ModelVisitor visitor, final int depth ) {
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            if ( container instanceof Resource ) {
                processor.walk((Resource)container,depth);
            } else if ( container instanceof EObject ) {
                processor.walk((EObject)container,depth);
            }
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(e);
        }
    }

}
