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
