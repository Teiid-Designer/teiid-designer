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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.spi.RegistrySPI;


/**
 * 
 */
public final class EclipseMock {

    private final IWorkspace workspace;
    private final IWorkspaceRoot workspaceRoot;
    private final IPath workspaceRootLocation;
    private final List<IProject> projects = new ArrayList<IProject>();

    public EclipseMock() {
        workspace = mock(IWorkspace.class);
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.WORKSPACE_KEY, workspace);

        workspaceRoot = mock(IWorkspaceRoot.class);
        when(workspaceRoot.getProjects()).thenReturn(new IProject[0]);
        when(workspace.getRoot()).thenReturn(workspaceRoot);

        workspaceRootLocation = mock(IPath.class);
        when(workspaceRoot.getLocation()).thenReturn(workspaceRootLocation);
    }

    public void addProject(final IProject project) {
        this.projects.add(project);
        when(workspaceRoot.getProjects()).thenReturn(this.projects.toArray(new IProject[this.projects.size()]));
    }
    
    public void dispose() {
        ((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.WORKSPACE_KEY);
        this.projects.clear();
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
