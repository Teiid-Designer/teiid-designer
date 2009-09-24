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

import com.metamatrix.modeler.modelgenerator.uml2.processor.RelationalFragmentGenerator;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalProcessor;

/**
 * TrivialRelationalFragmentGenerator
 */
public class TrivialRelationalFragmentGenerator implements RelationalFragmentGenerator {

    /**
     * Construct an instance of TrivialRelationalFragmentGenerator.
     * 
     */
    public TrivialRelationalFragmentGenerator() {
        super();
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.uml2relational.RelationalFragmentGenerator#createModelFragments(com.metamatrix.modeler.modelgenerator.uml2relational.Uml2RelationalProcessor, java.util.List, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createModelFragments(
        Uml2RelationalProcessor processor,
        List problems,
        IProgressMonitor monitor) {

    }

}
