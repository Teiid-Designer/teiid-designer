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

package com.metamatrix.modeler.modelgenerator.uml2.processor;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;


/**
 * The RelationalFragmentGenerator is used by the {@link Uml2RelationalProcessor} to create/generate
 * the individual fragments of the Relational model using the UML2 inputs available
 * from the processor.  The {@link Uml2RelationalProcessor} then compares these fragments to
 * objects in the existing model (using differencing), and merges the objects into the existing
 * (or new) relational model.
 */
public interface RelationalFragmentGenerator {

    /**
     * Create the different fragments of the Relational model based upon the
     * {@link Uml2RelationalProcessor#getInputModelSelectors() selected UML2 objects}, and
     * return a list containing the root-level object for each of the fragments.
     * @param processor the processor, which contains the inputs, options and other information; never null
     * @param problems the list into which should be placed {@link IStatus} instances denoting
     * errors, warnings, and other messages; never null
     * @param monitor the progress monitor; never null
     * @return the List of root-level {@link EObject object} for each of the fragments; may not be null
     * but may be empty if no fragments are to be created.
     */
    void createModelFragments( Uml2RelationalProcessor processor, List problems, IProgressMonitor monitor );

}
