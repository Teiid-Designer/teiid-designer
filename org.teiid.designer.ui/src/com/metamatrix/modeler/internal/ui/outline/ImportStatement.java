/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.outline;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

/**
 * ImportStatement is a node in the ModelOutline view for a model import reference
 */
public class ImportStatement implements IAdaptable {

    public IResource modelFile;

    public ImportStatement(IResource modelFileReference) {
        this.modelFile = modelFileReference;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        return null;
    }

    public IResource getModelFile() {
        return modelFile;
    }

    @Override
    public String toString() {
        return modelFile.getProjectRelativePath().toString();
    }
}
