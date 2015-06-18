/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.metamodel.MetamodelRegistry;
import org.teiid.designer.core.resource.EmfResourceSetImpl;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.transaction.UnitOfWorkProviderImpl;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.types.DatatypeManagerLifecycle;
import org.teiid.designer.core.workspace.ModelUtil;

/**
 * 
 */
@SuppressWarnings( "javadoc" )
public final class ModelWorkspaceMock {

    private final EclipseMock eclipseMock;
    private ResourceFinder finder;
    private ModelEditor modelEditor;
    private Container container;

    /**
     * Mocks core modeling classes used when running Designer.
     */
    public ModelWorkspaceMock() throws Exception {
        this(new EclipseMock());
    }

    /**
     * Mocks core modelling classes used when running Designer.
     */
    public ModelWorkspaceMock(final EclipseMock mock ) throws Exception {
        CoreArgCheck.isNotNull(mock, "mock"); //$NON-NLS-1$
        eclipseMock = mock;

        //
        // Mock the container
        //
        container = mock(Container.class);
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.DEFAULT_CONTAINER_KEY, container);

        //
        // Initialise the internal resource set
        //
        final EmfResourceSetImpl resourceSet = new EmfResourceSetImpl(container);
        when(container.getResources()).thenReturn(resourceSet.getResources());
        when(container.getResource(isA(URI.class), anyBoolean())).thenAnswer(new Answer<Resource>() {
            @Override
            public Resource answer(InvocationOnMock invocation) throws Throwable {
              Object[] args = invocation.getArguments();
              return resourceSet.getResource((URI) args[0], (Boolean) args[1]);
            }
          });

        //
        // Add external resource sets
        //
        List<ResourceSet> extResourceSets = ModelerCore.getExternalResourceSets();
        for (ResourceSet rsrcSet : extResourceSets) {
            resourceSet.addExternalResourceSet(rsrcSet);
        }

        //
        // Initialise the transaction provider
        //
        UnitOfWorkProviderImpl emfTransactionProvider = new UnitOfWorkProviderImpl(resourceSet);
        when(container.getEmfTransactionProvider()).thenReturn(emfTransactionProvider);

        //
        // Set the metamodel registry
        //
        MetamodelRegistry registry = ModelerCore.getMetamodelRegistry();
        when(container.getMetamodelRegistry()).thenReturn(registry);

        //
        // Mock the change notifier
        //
        ChangeNotifier changeNotifier = mock(ChangeNotifier.class);
        when(container.getChangeNotifier()).thenReturn(changeNotifier);
        
        //
        // Mock the resource finder
        //
        finder = mock(ResourceFinder.class);
        when(container.getResourceFinder()).thenReturn(finder);

        //
        // Initialise the data type manager
        //
        DatatypeManager datatypeManager = ModelerCore.getDatatypeManager();
        when(container.getDatatypeManager()).thenReturn(datatypeManager);
        ((DatatypeManagerLifecycle)datatypeManager).initialize(container);

        //
        // Mock the model editor
        //
        modelEditor = mock(ModelEditor.class);
        ((RegistrySPI) ModelerCore.getRegistry()).register(ModelerCore.MODEL_EDITOR_KEY, modelEditor);
        when(modelEditor.getName(isA(EObject.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ModelUtil.getName((EObject)args[0]);
            }
        });
        when(modelEditor.getNameFeature(isA(EObject.class))).thenAnswer(new Answer<EStructuralFeature>() {
            @Override
            public EStructuralFeature answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return ModelUtil.getNameFeature((EObject)args[0]);
            }
        });
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
     * @return the editor
     */
    public ModelEditor getModelEditor() {
        return this.modelEditor;
    }

    /**
     * @return the container
     */
    public Container getContainer() {
        return this.container;
    }

    /**
     * @return finder
     */
    public ResourceFinder getFinder() {
        return finder;
    }
}
