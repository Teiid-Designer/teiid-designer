/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.uml2.processor;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.teiid.designer.modelgenerator.uml2.processor.RelationalFragmentGenerator;
import org.teiid.designer.modelgenerator.uml2.processor.Uml2RelationalProcessor;

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
     * @See org.teiid.designer.modelgenerator.uml2relational.RelationalFragmentGenerator#createModelFragments(org.teiid.designer.modelgenerator.uml2relational.Uml2RelationalProcessor, java.util.List, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void createModelFragments(
        Uml2RelationalProcessor processor,
        List problems,
        IProgressMonitor monitor) {

    }

}
