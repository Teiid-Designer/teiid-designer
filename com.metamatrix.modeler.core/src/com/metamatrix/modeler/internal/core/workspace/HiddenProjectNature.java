/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;

/**
 *
 */
public class HiddenProjectNature implements IProjectNature {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private IProject project;

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IProjectNature#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
     */
    public void setProject( IProject project ) {
        this.project = project;
    }
}
