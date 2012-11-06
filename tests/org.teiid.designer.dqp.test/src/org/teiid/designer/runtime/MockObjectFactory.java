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
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.workspace.ModelResource;

/**
 *
 */
public class MockObjectFactory {

    /**
     * Test must use <code>@PrepareForTest( {ModelerCore.class} )</code> annotation.
     * 
     * @return the model container
     */
    public static Container createModelContainer() {
        Container container = mock(Container.class);
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.DEFAULT_CONTAINER_KEY, container);
        
        ChangeNotifier changeNotifier = mock(ChangeNotifier.class);
        when(container.getChangeNotifier()).thenReturn(changeNotifier);
       
        return container;
    }

    /**
     * Creates a mock <code>ModelResource</code>. The item name and parent can be obtained from the resource. The path can be
     * obtained from the parent.
     * 
     * @param name the name of the model
     * @param parentPath the model's parent path
     * @return the model resource
     */
    public static ModelResource createModelResource( final String name,
                                                     final String parentPath ) {
        final ModelResource parent = mock(ModelResource.class);
        when(parent.getPath()).thenReturn(new Path(parentPath));

        final ModelResource modelResource = mock(ModelResource.class);
        when(modelResource.getItemName()).thenReturn(name);
        when(modelResource.getParent()).thenReturn(parent);

        return modelResource;
    }

    /**
     * Prevent construction.
     */
    private MockObjectFactory() {
        // nothing to do
    }
    
    /**
     * Ensure that any object created have been unregistered
     */
    public static void dispose() {
    	((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.DEFAULT_CONTAINER_KEY);
    }
}
