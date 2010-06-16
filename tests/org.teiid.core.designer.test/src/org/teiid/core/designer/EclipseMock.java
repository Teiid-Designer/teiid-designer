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
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * 
 */
public final class EclipseMock {

    private final IWorkspaceRoot root;
    private final IPath rootLocation;

    public EclipseMock() {
        mockStatic(ResourcesPlugin.class);
        final IWorkspace workspace = mock(IWorkspace.class);
        when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);
        root = mock(IWorkspaceRoot.class);
        when(workspace.getRoot()).thenReturn(root);
        rootLocation = mock(IPath.class);
        when(root.getLocation()).thenReturn(rootLocation);
    }
    
    /**
     * @return the mock workspace root
     */
    public IWorkspaceRoot getRoot() {
        return root;
    }

    /**
     * @return rootPath
     */
    public IPath getRootLocation() {
        return rootLocation;
    }
}
