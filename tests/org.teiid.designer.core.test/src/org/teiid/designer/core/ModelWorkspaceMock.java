/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.eclipse.core.runtime.CoreException;
import org.teiid.core.designer.EclipseMock;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * 
 */
public final class ModelWorkspaceMock {

    private final EclipseMock eclipseMock;
    private ResourceFinder finder;

    /**
     * Mocks core modeling classes used when running Designer.
     */
    public ModelWorkspaceMock() {
        eclipseMock = new EclipseMock();

        // ModelWorkspaceManager
        mockStatic(ModelWorkspaceManager.class);
        final ModelWorkspaceManager modelWorkspaceMgr = mock(ModelWorkspaceManager.class);
        when(ModelWorkspaceManager.getModelWorkspaceManager()).thenReturn(modelWorkspaceMgr);

        // ModelerCore
        mockStatic(ModelerCore.class);
        final ModelerCore modelerCore = mock(ModelerCore.class);
        when(ModelerCore.getPlugin()).thenReturn(modelerCore);
        final Container container = mock(Container.class);
        try {
            when(ModelerCore.getModelContainer()).thenReturn(container);
            finder = mock(ResourceFinder.class);
            when(container.getResourceFinder()).thenReturn(finder);
        } catch (final CoreException notPossible) {
            notPossible.printStackTrace();
        }
    }

    /**
     * @return eclipseMock
     */
    public EclipseMock getEclipseMock() {
        return eclipseMock;
    }

    /**
     * @return finder
     */
    public ResourceFinder getFinder() {
        return finder;
    }
}
