/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.mockito.Mockito;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;


/**
 * 
 */
public final class EclipseMock {

    private final IWorkspace workspace;
    private final IWorkspaceRoot workspaceRoot;
    private final IPath workspaceRootLocation;

    public EclipseMock() {
        //
        // Mocks the workspace but does not have a notification manager so
        // cannot fire resource change events to the DeltaProcessor. Tests that
        // rely on receipt of these events will have to execute the resource change
        // event operation manually in order to ensure resources end up the same
        // as those in Designer.
        //
        workspace = mock(IWorkspace.class);
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.WORKSPACE_KEY, workspace);

        workspaceRoot = mock(IWorkspaceRoot.class);
        when(workspaceRoot.getProjects()).thenReturn(new IProject[0]);
        when(workspace.getRoot()).thenReturn(workspaceRoot);

        workspaceRootLocation = mock(IPath.class);
        when(workspaceRoot.getLocation()).thenReturn(workspaceRootLocation);

        //
        // Initialise the workspace manager
        //
        ModelWorkspaceManager.getModelWorkspaceManager();
    }
    
    public void dispose() throws Exception {
        ModelWorkspaceManager.shutdown();

        Mockito.reset(workspace);
        Mockito.reset(workspaceRoot);
        Mockito.reset(workspaceRootLocation);

        ((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.WORKSPACE_KEY);
    }

    /**
     * @return workspace
     */
    public IWorkspace workspace() {
        return workspace;
    }

    /**
     * @return workspace root
     */
    public IWorkspaceRoot workspaceRoot() {
        return workspaceRoot;
    }

    /**
     * @return workspace root location
     */
    public IPath workspaceRootLocation() {
        return workspaceRootLocation;
    }
}
