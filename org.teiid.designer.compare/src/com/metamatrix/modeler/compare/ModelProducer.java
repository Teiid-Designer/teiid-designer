/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import com.metamatrix.modeler.compare.selector.ModelSelector;

/**
 * ModelProducer
 */
public interface ModelProducer {

    /**
     * Produce the model contents.  Typically, implementations are supplied with all necessary information
     * (such as options, destinations, etc.) through implementation-specific means.
     * @param monitor the progress monitor; may not be null
     * @param problems the list into which can be placed {@link IStatus} instances denoting informational,
     * warning, and error messages.
     * @throws Exception if there is a catastrophic problem executing the production of the model
     */
    public void execute( IProgressMonitor monitor, final List problems ) throws Exception;

    /**
     * Return the model selector into which the model contents are placed during 
     * {@link #execute(IProgressMonitor, List) execute}.
     * @return the model selector; may not be null
     */
    public ModelSelector getOutputSelector();
}
