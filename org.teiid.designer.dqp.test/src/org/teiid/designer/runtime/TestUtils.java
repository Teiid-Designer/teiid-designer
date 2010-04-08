/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 *
 */
public class TestUtils {

    /**
     * @param name the name of the model
     * @param parentPath the parent path of the model
     * @return the model resource
     */
    public static ModelResource createModelResource( String name,
                                                     String parentPath ) {
        ModelResource parent = mock(ModelResource.class);
        when(parent.getPath()).thenReturn(new Path(parentPath));

        ModelResource modelResource = mock(ModelResource.class);
        when(modelResource.getItemName()).thenReturn(name);
        when(modelResource.getParent()).thenReturn(parent);

        return modelResource;
    }

    /**
     * Mocks static Eclipse classes used when running Eclipse. Needs to be called from the @Before method of the test class.
     */
    public static void initializeStaticWorkspaceClasses() {
        // ResourcesPlugin
        mockStatic(ResourcesPlugin.class);
        IWorkspace workspace = mock(IWorkspace.class);
        when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);

        // ModelWorkspaceManager
        mockStatic(ModelWorkspaceManager.class);
        ModelWorkspaceManager modelWorkspaceMgr = mock(ModelWorkspaceManager.class);
        when(ModelWorkspaceManager.getModelWorkspaceManager()).thenReturn(modelWorkspaceMgr);

        // ModelerCore
        mockStatic(ModelerCore.class);
        ModelerCore modelerCore = mock(ModelerCore.class);
        when(ModelerCore.getPlugin()).thenReturn(modelerCore);
    }

    /**
     * Don't allow construction.
     */
    private TestUtils() {
        // nothing to do
    }

}
