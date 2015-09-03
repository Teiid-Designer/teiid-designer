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
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.container.ContainerEditingDomain;
import org.teiid.designer.core.container.DefaultEObjectFinder;
import org.teiid.designer.core.container.DefaultResourceFinder;
import org.teiid.designer.core.container.ObjectManagerImpl;
import org.teiid.designer.core.container.ResourceDescriptor;
import org.teiid.designer.core.container.ResourceDescriptorImpl;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.metamodel.MetamodelRegistry;
import org.teiid.designer.core.resource.EmfResourceSetImpl;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.transaction.UnitOfWorkProviderImpl;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.types.DatatypeManagerLifecycle;

/**
 * 
 */
@SuppressWarnings( "javadoc" )
public final class ModelWorkspaceMock {

    private final EclipseMock eclipseMock;
    private Container container;
    private EditingDomain editingDomain;
    private ModelEditorMock editorMock;

    /**
     * Mocks core modeling classes used when running Designer.
     */
    public ModelWorkspaceMock() throws Exception {
        this(new EclipseMock());
    }

    /**
     * Mocks core modelling classes used when running Designer.
     */
    public ModelWorkspaceMock(final EclipseMock mock) throws Exception {
        CoreArgCheck.isNotNull(mock, "mock"); //$NON-NLS-1$
        eclipseMock = mock;

        //
        // Mock the container
        //
        container = mock(Container.class);
        ((RegistrySPI)ModelerCore.getRegistry()).register(ModelerCore.DEFAULT_CONTAINER_KEY, container);

        //
        // Initialise the internal resource set
        //
        final EmfResourceSetImpl resourceSet = new EmfResourceSetImpl(container);
        when(container.getResources()).thenReturn(resourceSet.getResources());
        when(container.getResource(isA(URI.class), anyBoolean())).thenAnswer(new Answer<Resource>() {
            @Override
            public Resource answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return resourceSet.getResource((URI)args[0], (Boolean)args[1]);
            }
        });
        when(container.createResource(isA(URI.class))).thenAnswer(new Answer<Resource>() {
            @Override
            public Resource answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return resourceSet.createResource((URI)args[0]);
            }
        });

        //
        // Resource Finder
        //
        DefaultResourceFinder resourceFinder = new DefaultResourceFinder(container);
        when(container.getResourceFinder()).thenReturn(resourceFinder);

        //
        // EObject Finder
        //
        DefaultEObjectFinder eObjectFinder = new DefaultEObjectFinder(container);
        when(container.getEObjectFinder()).thenReturn(eObjectFinder);

        ObjectManagerImpl objectManager = new ObjectManagerImpl(container);
        when(container.getObjectManager()).thenReturn(objectManager);

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
        ChangeNotifier changeNotifier = new ChangeNotifier();
        when(container.getChangeNotifier()).thenReturn(changeNotifier);

        //
        // Initialise the data type manager
        //
        DatatypeManager datatypeManager = ModelerCore.getDatatypeManager();
        when(container.getDatatypeManager()).thenReturn(datatypeManager);
        ((DatatypeManagerLifecycle)datatypeManager).initialize(container);

        // Register the known resource descriptors
        final Iterator iter = ModelerCore.getConfiguration().getResourceDescriptors().iterator();
        while (iter.hasNext()) {
            final ResourceDescriptor resourceDescriptor = (ResourceDescriptor)iter.next();
            ResourceDescriptorImpl.register(resourceDescriptor, resourceSet);
        }

        //
        // ModelEditor
        //
        editorMock = new ModelEditorMock(this);
    }

    /**
     * Dispose this {@link ModelWorkspaceMock}.
     * 
     * This is necessary to remove the mocked objects from the
     * {@link ModelerCore} registry that have been registered by this instance.
     */
    public void dispose() throws Exception {
        ((RegistrySPI)ModelerCore.getRegistry()).unregister(ModelerCore.DEFAULT_CONTAINER_KEY);
        ((RegistrySPI)ModelerCore.getRegistry()).unregister(ModelerCore.MODEL_EDITOR_KEY);

        Mockito.reset(container);
        container = null;

        editingDomain = null;

        editorMock.dispose();

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
        return editorMock.getModelEditor();
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
        return container.getResourceFinder();
    }

    public void setFinder(ResourceFinder finder) {
        when(container.getResourceFinder()).thenReturn(finder);
    }

    /**
     * @return editing domain
     */
    public EditingDomain getEditingDomain() {
        if (editingDomain == null) {
            // Retrieve the adapter factory that yields item providers.
            final ComposedAdapterFactory adapterFactory = (ComposedAdapterFactory)ModelerCore.getMetamodelRegistry().getAdapterFactory();
            CoreArgCheck.isNotNull(container.getEmfTransactionProvider());

            // Create the command stack
            final BasicCommandStack commandStack = new BasicCommandStack();

            // Create the editing domain
            editingDomain = new ContainerEditingDomain(adapterFactory, commandStack, container);
        }

        return editingDomain;
    }
}
