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

    private final IPath rootPath;

    public EclipseMock() {
        mockStatic(ResourcesPlugin.class);
        final IWorkspace workspace = mock(IWorkspace.class);
        when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);
        final IWorkspaceRoot root = mock(IWorkspaceRoot.class);
        when(workspace.getRoot()).thenReturn(root);
        rootPath = mock(IPath.class);
        when(root.getLocation()).thenReturn(rootPath);
    }

    /**
     * @return rootPath
     */
    public IPath getRootPath() {
        return rootPath;
    }
}
