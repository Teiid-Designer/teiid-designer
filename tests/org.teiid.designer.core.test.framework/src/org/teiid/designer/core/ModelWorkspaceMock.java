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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.spi.RegistrySPI;

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
        this(new EclipseMock());
    }

    /**
     * Mocks core modelling classes used when running Designer.
     */
    public ModelWorkspaceMock( final EclipseMock mock ) {
        CoreArgCheck.isNotNull(mock, "mock"); //$NON-NLS-1$
        eclipseMock = mock;

        // ModelerCore
        Container container = mock(Container.class);
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.DEFAULT_CONTAINER_KEY, container);
        
        ChangeNotifier changeNotifier = mock(ChangeNotifier.class);
        when(container.getChangeNotifier()).thenReturn(changeNotifier);
        
        BasicEList<Resource> resourceList = new BasicEList<Resource>();
        when(container.getResources()).thenReturn(resourceList);
        
        finder = mock(ResourceFinder.class);
        when(container.getResourceFinder()).thenReturn(finder);
        
        final ModelEditor editor = mock(ModelEditor.class);
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.MODEL_EDITOR_KEY, editor);
    }

    /**
     * Dispose this {@link ModelWorkspaceMock}.
     * 
     * This is necessary to remove the mocked objects from the
     * {@link ModelerCore} registry that have been registered by this instance.
     */
    public void dispose() {
        ((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.DEFAULT_CONTAINER_KEY);
        ((RegistrySPI) ModelerCore.getRegistry()).unregister(ModelerCore.MODEL_EDITOR_KEY);
        eclipseMock.dispose();
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
