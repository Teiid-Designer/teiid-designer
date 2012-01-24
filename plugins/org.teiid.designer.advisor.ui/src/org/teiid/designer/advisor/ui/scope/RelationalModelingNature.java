/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.scope;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;

import com.metamatrix.modeler.core.ModelerCore;

/**
 * 
 */
public class RelationalModelingNature implements IProjectNature {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private IProject project;

    public static final String NATURE_ID = AdvisorUiConstants.PLUGIN_ID + ".relationalModelingNature"; //$NON-NLS-1$
    public static final String[] NATURES = new String[] {ModelerCore.NATURE_ID, NATURE_ID};

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
