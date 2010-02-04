/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.selector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;

/**
 * ModelResourceSelector
 */
public abstract class TemporaryResourceModelSelector extends AbstractModelSelector {

    private Container resourceSet;
    private String label;
    private static final String CONTAINER_NAME = "Container for difference analysis"; //$NON-NLS-1$

    /**
     * Construct an instance of ModelResourceSelector.
     * 
     */
    public TemporaryResourceModelSelector() {
        super();
        try {
            this.resourceSet = ModelerCore.createContainer(CONTAINER_NAME);
        } catch (CoreException e) {
            ModelerComparePlugin.Util.log(e);
        }
    }
    
    protected ResourceSet getResourceSet() {
        return this.resourceSet;
    }
    
    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getLabel()
     */
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel( final String label ) {
        this.label = label;
    }


}
