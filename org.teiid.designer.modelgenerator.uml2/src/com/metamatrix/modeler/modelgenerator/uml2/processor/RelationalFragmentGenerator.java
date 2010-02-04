/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
