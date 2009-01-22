/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.xsd.XSDPackage;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.RunnableState;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.DuplicateResourceException;
import com.metamatrix.modeler.core.container.EObjectFinder;
import com.metamatrix.modeler.core.container.ObjectManager;
import com.metamatrix.modeler.core.container.ResourceDescriptor;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.resource.XResource;
import com.metamatrix.modeler.core.transaction.UndoableListener;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.transaction.UnitOfWorkProvider;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.ExternalResourceImportsHelper;
import com.metamatrix.modeler.core.util.ProcessedNotificationResult;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSetImpl;
import com.metamatrix.modeler.internal.core.transaction.UnitOfWorkImpl;
import com.metamatrix.modeler.internal.core.transaction.UnitOfWorkProviderImpl;

/**
 * This class represents a Container implementation for the MetaBase Toolkit that encapsulates the Eclipse Modeling Framework
 * (EMF).
 * 
 * @since 3.1
 */
public class ContainerImpl implements Container, IEditingDomainProvider {

    private static final ResourceSet[] EMPTY_RESOURCE_SET_ARRAY = new ResourceSet[0];

    // ############################################################################################################################
    // # Constants #
    // ############################################################################################################################

    // state flags
    public static final int UNSTARTED = RunnableState.UNSTARTED;
    public static final int STARTING = RunnableState.STARTING;
    public static final int STARTED = RunnableState.STARTED;
    public static final int STOPPING = RunnableState.STOPPING;
    public static final int STOPPED = RunnableState.STOPPED;
    public static final int FAILED = RunnableState.FAILED;

    // ############################################################################################################################
    // # Variables #
    // ############################################################################################################################

    private UnitOfWorkProvider emfTransactionProvider;
    private ResourceSet resourceSet;
    private MetamodelRegistry metamodelRegistry;
    private MultiStatus status;
    private final ObjectManager objectManager;
    private DatatypeManager datatypeManager;
    private Map options;

    /**
     * The started/shutdown state of the container.
     * 
     * @since 3.1
     */
    private RunnableState state;

    /**
     * The name of the container. The name may be changed only if the container has not been started.
     * 
     * @since 3.1
     */
    private String name;

    /**
     * The EventBroker object for this Container. This implementation simply manages and maintains the reference, although the
     * broker's {@link EventBroker#shutdown()} method is called during the AbstractContainer's {@link #shutdown()}.
     * 
     * @since 3.1
     */
    private ChangeNotifier changeNotifier;
    private EObjectFinder finder;
    private ResourceFinder resourceFinder;
    private EditingDomain editingDomain;

    // ############################################################################################################################
    // # Constructors #
    // ############################################################################################################################

    /**
     * Constructor for AbstractContainer.
     * 
     * @since 3.1
     */
    public ContainerImpl() {
        this.objectManager = new ObjectManagerImpl(this);
        initializeDefaults();
    }

    // ############################################################################################################################
    // # Methods #
    // ############################################################################################################################

    /**
     * Returns a resource for the URI that either already exists, is new but represents an existing file, or is new and represents
     * a brand-new file.
     * 
     * @param resourceSet the resource set; may not be null
     * @param uri the URI for the resource; may not be null
     * @param runAfterCreation a Runnable that should be
     * @return the ResourceAction containing the Resource for the URI (which is never null) and a flag stating whether the
     *         resource was created
     * @throws ModelerCoreException if the resource cannot be loaded or created
     */
    public static ResourceAction getOrCreateResource( final ResourceSet resourceSet,
                                                      final URI uri ) throws ModelerCoreException {
        ArgCheck.isNotNull(uri);

        // See if the resource exists already ...
        final Resource existingResource = resourceSet.getResource(uri, false); // returns null if non-existant
        if (existingResource != null) {
            // Found an existing resource, so just return it
            return new ResourceAction(existingResource, false);
        }

        // The resource doesn't already exist in the ResourceSet, so try loading it first
        if (!uri.isFile()) {
            final Object[] params = new Object[] {URI.decode(uri.toString())};
            final String msg = ModelerCore.Util.getString("ContainerImpl.URI_is_not_a_file_and_cannot_be_openned_as_a_resource", params); //$NON-NLS-1$
            throw new ModelerCoreException(msg);
        }

        final File file = new File(uri.toFileString());
        if (file.canRead() && file.exists() && file.length() != 0) {
            // If the file is not empty ...
            try {
                // Try opening the existing file ...
                return new ResourceAction(resourceSet.getResource(uri, true), false);
            } catch (DuplicateResourceException dre) {
                // Caught a duplicate resource exception, so propogate and don't log ...
                throw dre;
            } catch (Throwable t) {
                final Object[] params = new Object[] {URI.decode(uri.toString())};
                final String msg = ModelerCore.Util.getString("ContainerImpl.Unable_to_open_the_resource", params); //$NON-NLS-1$
                throw new ModelerCoreException(t, msg);
            }
        }

        // Could not load an existing file, so try creating the file ...
        try {
            Resource newResource = resourceSet.createResource(uri);

            return new ResourceAction(newResource, true);
        } catch (DuplicateResourceException dre) {
            // Caught a duplicate resource exception, so propogate and don't log ...
            throw dre;
        } catch (Throwable t) {
            final Object[] params = new Object[] {URI.decode(uri.toString())};
            final String msg = ModelerCore.Util.getString("ContainerImpl.Unable_to_create_the_resource", params); //$NON-NLS-1$
            throw new ModelerCoreException(t, msg);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.container.Container#getOrCreateResource(org.eclipse.emf.common.util.URI)
     */
    public Resource getOrCreateResource( final URI uri ) throws ModelerCoreException {
        final ResourceAction action = ContainerImpl.getOrCreateResource(this, uri);
        return action.getResource();
    }

    /**
     * Return true if state == started
     * 
     * @author Lance Phillips
     * @since 3.1
     */
    public boolean isStarted() {
        return this.state.isState(STARTED);
    }

    /**
     * Return true if state == stopped
     * 
     * @author Lance Phillips
     * @since 3.1
     */
    public boolean isStopped() {
        return this.state.isState(STOPPED);
    }

    /**
     * Add the given listener to the emfTransactionProvider's UndoableListener list
     * 
     * @param listener
     */
    public void addUndoableEditListener( UndoableListener listener ) {
        this.emfTransactionProvider.addUndoableEditListener(listener);
    }

    /**
     * Remove the given listener from the emfTransactionProvider's UndoableListener list
     * 
     * @param listener
     */
    public void removeUndoableEditListener( UndoableListener listener ) {
        this.emfTransactionProvider.removeUndoableEditListener(listener);
    }

    /**
     * Determine whether the supplied name is considered valid. This method considers all names to be valid, including null
     * strings. This method is called by the {@link #setName(String)} method prior to actually setting the name.
     * <p>
     * Subclasses should override this method (as opposed to {@link #setName(String)}) if they require more control over whether
     * names are considered valid.
     * </p>
     * 
     * @param potentialName the name that is to be considered
     * @return true if the name is considered valid, or false otherwise.
     * @since 3.1
     */
    protected boolean isValidName( final String potentialName ) {
        return true;
    }

    /**
     * Method to define the activity to be performed when this container is successfully transitioning to the STOPPED state. Only
     * if this method succeeds with the current state be changed to STOPPED.
     * 
     * @throws ModelerCoreException
     * @since 3.1
     */
    protected void performShutdown() {
    }

    /**
     * Sets the eventBroker. Note that this method does not migrate or copy the listeners on the existing broker to the
     * <code>eventBroker</code>
     * 
     * @param eventBroker The eventBroker to set
     * @throws IllegalStateException if the container is currently running or is either initializing or shutting down.
     * @since 3.1
     */
    public void setChangeNotifier( final ChangeNotifier notifier ) {
        ArgCheck.isNotNull(notifier);
        verifySetIsAllowed(this.changeNotifier);
        this.changeNotifier = notifier;
    }

    /**
     * Sets the name of the container
     * 
     * @param name The name for the container
     * @throws IllegalArgumentException if the name is not valid
     * @throws IllegalStateException if the container is currently running or is either initializing or shutting down.
     * @since 3.1
     */
    public void setName( final String newName ) {
        verifySetIsAllowed(this.name);
        if (!isValidName(newName)) {
            throw new IllegalArgumentException(
                                               ModelerCore.Util.getString("ContainerImpl.The_name___2") + newName + ModelerCore.Util.getString("ContainerImpl.__is_not_valid_3")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        final String oldName = this.name;
        this.name = newName;
        updateRegistry(oldName);
    }

    /**
     * Sets the runnable state.
     * 
     * @since 3.1
     */
    protected void setState( final int state ) {
        this.state.setState(state);
    }

    /**
     * Shutdown the currently running container. In general, subclasses should not override this method because it checks the
     * current state to determine whether it can be shut down. If the container can be shutdown, then this method calls the
     * {@link #performShutdown()} method, which is generally the method that should be overridden by subclasses.
     * 
     * @see com.metamatrix.api.mtk.core.Container#shutdown()
     * @since 3.1
     */
    public final void shutdown() {
        if (this.state.isState(STOPPED)) {
            return; // do nothing
        }
        this.state.setState(STOPPING);
        performShutdown();
        this.state.setState(STOPPED);
    }

    /**
     * Start the currently unstarted container. In general, subclasses should not override this method because it checks the
     * current state to determine whether it can be shut down. If the container can be shutdown, then this method calls the
     * {@link #performStart()} method, which is generally the method that should be overridden by subclasses.
     * 
     * @see com.metamatrix.api.mtk.core.Container#start()
     * @since 3.1
     */
    public final void start() {
        if (this.state.isState(STARTED)) {
            return; // do nothing
        }
        this.state.setState(STARTING);
        performStart();
        this.state.setState(STARTED);
    }

    /**
     * @see java.lang.Object#toString()
     * @since 3.1
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Method to update the registry with the new name. When this method is called, the name of the Container has already been
     * changed to reflect the new name.
     * 
     * @param the old name of the Container
     * @since 3.1
     */
    protected void updateRegistry( final String oldName ) {
        final com.metamatrix.modeler.core.Registry registry = ModelerCore.getRegistry();
        // Add this to the Registry using the current (new) name
        registry.register(getName(), this);

        // If there is an old name, then unregister this ...
        if (oldName != null && registry.lookup(oldName) == this) {
            registry.unregister(oldName);
        }
    }

    /**
     * Verifies that a set opertion is allowed, meaning that either the property to be set is currently null or the current
     * runnable state is not {@link #STARTING}, {@link #STARTED}, or {@link #STOPPING}.
     * 
     * @param property The property that will be set.
     * @throws IllegalStateException If the property to be set is not null and the current state is one of the aforementioned
     *         values.
     * @since 3.1
     */
    protected void verifySetIsAllowed( final Object property ) {
        if (property != null) {
            if (this.state.isInTransition()) {
                throw new IllegalStateException(
                                                ModelerCore.Util.getString("ContainerImpl.Unable_to_set_the_name_while_the_container_is_in_transition_4")); //$NON-NLS-1$
            }
            if (this.state.isRunning()) {
                throw new IllegalStateException(
                                                ModelerCore.Util.getString("ContainerImpl.Unable_to_set_the_name_on_a_running_container_5")); //$NON-NLS-1$
            }
        }
    }

    /**
     * Get the name of the Container.
     * 
     * @return the Container's name.
     * @see com.metamatrix.api.mtk.core.Container#getName()
     * @since 3.1
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the state.
     * 
     * @return int
     * @since 3.1
     */
    protected int getState() {
        return state.getState();
    }

    /**
     * This method sets the state of the AbstractContainer to {@link #UNSTARTED}.
     * 
     * @since 3.1
     */
    protected void initializeDefaults() {
        this.state = new RunnableState();
    }

    public ObjectManager getObjectManager() {
        return this.objectManager;
    }

    /**
     * Create default implementation of the finder.
     */
    protected EObjectFinder createDefaultEObjectFinder() {
        return new DefaultEObjectFinder(this);
    }

    /**
     * Create default implementation of the resource finder.
     */
    protected ResourceFinder createDefaultResourceFinder() {
        return new DefaultResourceFinder(this);
    }

    protected EditingDomain createDefaultEditingDomain() {
        // Retrieve the adapter factory that yields item providers.
        final ComposedAdapterFactory adapterFactory = (ComposedAdapterFactory)ModelerCore.getMetamodelRegistry().getAdapterFactory();
        Assertion.isNotNull(this.getEmfTransactionProvider());

        // Create the command stack
        final BasicCommandStack commandStack = new BasicCommandStack();

        // Create the editing domain
        return new ContainerEditingDomain(adapterFactory, commandStack, this);
    }

    /**
     * Called lazily by {@link #getEventBroker()} to create a {@link SyncEventBroker} if an EventBroker has not previously been
     * set.
     * 
     * @since 3.1
     */
    protected ChangeNotifier createDefaultChangeNotifier() {
        return new ChangeNotifier();
    }

    /**
     * Returns the EventBroker for the container. If none exists, one is created via {@link #createDefaultEventBroker()}.
     * 
     * @return The container's EventBroker
     * @since 3.1
     */
    public ChangeNotifier getChangeNotifier() {
        if (this.changeNotifier == null) {
            setChangeNotifier(createDefaultChangeNotifier());
        }
        return this.changeNotifier;
    }

    /**
     * @see com.metamatrix.api.mtk.core.Container#getEObjectFinder()
     * @since 3.1
     */
    public EObjectFinder getEObjectFinder() {
        if (this.finder == null) {
            setEObjectFinder(createDefaultEObjectFinder());
        }
        return this.finder;
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#getResourceFinder()
     * @since 4.2
     */
    public ResourceFinder getResourceFinder() {
        if (this.resourceFinder == null) {
            setResourceFinder(createDefaultResourceFinder());
        }
        return this.resourceFinder;
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#setEObjectFinder(com.metamatrix.modeler.core.container.EObjectFinder)
     * @since 4.3
     */
    public void setEObjectFinder( EObjectFinder finder ) {
        ArgCheck.isNotNull(finder);
        verifySetIsAllowed(this.finder);
        this.finder = finder;
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#setResourceFinder(com.metamatrix.modeler.core.container.ResourceFinder)
     * @since 4.3
     */
    public void setResourceFinder( ResourceFinder finder ) {
        ArgCheck.isNotNull(finder);
        verifySetIsAllowed(this.resourceFinder);
        this.resourceFinder = finder;
    }

    /**
     * Returns the emfTransactionProvider.
     * 
     * @return UnitOfWorkProvider
     */
    public UnitOfWorkProvider getEmfTransactionProvider() {
        if (!isStarted()) {
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("ContainerImpl.Container_must_be_started_prior_to_use_7")); //$NON-NLS-1$
        }

        return emfTransactionProvider;
    }

    /**
     * Sets the emfTransactionProvider.
     * 
     * @param emfTransactionProvider The emfTransactionProvider to set
     */
    protected void setEmfTransactionProvider( UnitOfWorkProvider emfTransactionProvider ) {
        if (emfTransactionProvider == null) {
            ArgCheck.isNotNull(emfTransactionProvider,
                               ModelerCore.Util.getString("ContainerImpl.The_UnitOfWorkProvider_may_not_be_null_8")); //$NON-NLS-1$
        }
        this.emfTransactionProvider = emfTransactionProvider;
    }

    /**
     * Sets the EditingDomain.
     * 
     * @param editingDomain The EditingDomain to set
     */
    protected void setEditingDomain( final EditingDomain editingDomain ) {
        if (editingDomain == null) {
            ArgCheck.isNotNull(editingDomain,
                               ModelerCore.Util.getString("ContainerImpl.The_EditingDomain_reference_may_not_be_null_1")); //$NON-NLS-1$
        }
        this.editingDomain = editingDomain;
    }

    /**
     * Return the {@link IDGenerator} to use for the generation of {@link ObjectID} instances.
     */
    public IDGenerator getIDGenerator() {
        return IDGenerator.getInstance();
    }

    /**
     * Returns the resourceSet.
     * 
     * @return ResourceSet
     */
    public final ResourceSet getResourceSet() {
        if (!isStarted()) {
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("ContainerImpl.Container_must_be_started_prior_to_use_9")); //$NON-NLS-1$
        }

        return this.resourceSet;
    }

    /**
     * Returns the metamodelRegistry.
     * 
     * @return MetamodelRegistry
     */
    public MetamodelRegistry getMetamodelRegistry() {
        if (!isStarted()) {
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("ContainerImpl.Container_must_be_started_prior_to_use_10")); //$NON-NLS-1$
        }

        return this.metamodelRegistry;
    }

    /**
     * Sets the metamodelRegistry.
     * 
     * @param metamodelRegistry The metamodelRegistry to set
     */
    public void setMetamodelRegistry( MetamodelRegistry metamodelRegistry ) {
        if (metamodelRegistry == null) {
            ArgCheck.isNotNull(metamodelRegistry,
                               ModelerCore.Util.getString("ContainerImpl.The_reference_to_the_MetamodelRegistry_may_not_be_null_11")); //$NON-NLS-1$
        }
        this.metamodelRegistry = metamodelRegistry;
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#getDatatypeManager()
     * @since 4.2
     */
    public DatatypeManager getDatatypeManager() {
        if (datatypeManager == null) {
            datatypeManager = ModelerCore.getBuiltInTypesManager();
        }
        return datatypeManager;
    }

    public void setDatatypeManager( final DatatypeManager datatypeManager ) {
        ArgCheck.isNotNull(datatypeManager);
        this.datatypeManager = datatypeManager;
    }

    // ###################################################################################
    // # Public ResourceSet methods (that mostly just delegate to the contained instance)
    // ###################################################################################

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#createResource(org.eclipse.emf.common.util.URI)
     */
    public Resource createResource( final URI uri ) {
        return this.resourceSet.createResource(uri);
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getAdapterFactories()
     */
    public EList<AdapterFactory> getAdapterFactories() {
        return this.resourceSet.getAdapterFactories();
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getAllContents()
     */
    public TreeIterator<Notifier> getAllContents() {
        return this.resourceSet.getAllContents();
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getEObject(org.eclipse.emf.common.util.URI, boolean)
     */
    public EObject getEObject( final URI uri,
                               final boolean loadOnDemand ) {
        EObject result = null;

        // If the URI contains a UUID fragement ...
        if (uri != null && uri.fragment() != null) {
            String uriFragment = uri.fragment();
            URI resourceUri = uri.trimFragment();
            int beginIndex = uriFragment.indexOf(UUID.PROTOCOL);
            if (beginIndex != -1) {
                beginIndex = beginIndex + UUID.PROTOCOL.length() + 1;
                if (beginIndex < uriFragment.length()) {
                    String uuidStringWithoutProtocol = uriFragment.substring(beginIndex);
                    try {
                        ObjectID uuid = UUID.stringToObject(uuidStringWithoutProtocol);
                        result = (EObject)getEObjectFinder().find(uuid);
                        if (result == null) {
                            result = getEObjectFromExternalResourceSets(uri, false);
                        }
                        // If we have found a result for this UUID then check that it's resource is
                        // the same as referenced in the URI. If it is not, then we found the EObject
                        // in one of the external resource sets when it instead should have resolved to a
                        // resource in the workspace.
                        if (result != null) {
                            Resource resource = this.resourceSet.getResource(resourceUri, false);
                            Resource resultantResource = result.eResource();
                            if (resource != null && !resource.equals(resultantResource)) {
                                result = null;
                            }
                        }
                    } catch (InvalidIDException e) {
                        ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                    }
                }
            }
        }

        if (result != null) {
            return result;
        }
        // Try resolving in the Container's resource set ...
        return this.resourceSet.getEObject(uri, loadOnDemand);
    }

    private EObject getEObjectFromExternalResourceSets( final URI uri,
                                                        final boolean loadOnDemand ) {
        EObject result = null;
        if (this.resourceSet instanceof EmfResourceSetImpl) {
            final ResourceSet[] externalResourceSets = ((EmfResourceSetImpl)this.resourceSet).getExternalResourceSets();
            for (int i = 0; i != externalResourceSets.length; ++i) {
                ResourceSet rsrcSet = externalResourceSets[i];
                result = rsrcSet.getEObject(uri, loadOnDemand);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getLoadOptions()
     */
    public Map<Object, Object> getLoadOptions() {
        return this.resourceSet.getLoadOptions();
    }

    /**
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getPackageRegistry()
     * @since 4.3
     */
    public org.eclipse.emf.ecore.EPackage.Registry getPackageRegistry() {
        return this.resourceSet.getPackageRegistry();
    }

    /**
     * @see org.eclipse.emf.ecore.resource.ResourceSet#setPackageRegistry(org.eclipse.emf.ecore.EPackage.Registry)
     * @since 4.3
     */
    public void setPackageRegistry( org.eclipse.emf.ecore.EPackage.Registry packageRegistry ) {
        this.resourceSet.setPackageRegistry(packageRegistry);
    }

    /**
     * Loads the resource at the given URI. The created resource loads the model objects into the ObjectManager as well.
     * 
     * @param the URI for the resource to be loaded.
     * @return ResourceSetImpl
     * @throws ModelerCoreException
     */
    public Resource getResource( final URI uri,
                                 final boolean loadOnDemand ) {
        if (uri == null) {
            ArgCheck.isNotNull(uri, ModelerCore.Util.getString("ContainerImpl.The_URI_to_load_may_not_be_null_12")); //$NON-NLS-1$
        }

        if (!isStarted()) {
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("ContainerImpl.Container_must_be_started_prior_to_use_13")); //$NON-NLS-1$
        }

        // Get txn
        final UnitOfWork txn = getEmfTransactionProvider().getCurrent();
        boolean startedTxn = false;
        if (!txn.isStarted()) {
            try {
                txn.begin();
            } catch (ModelerCoreException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }
            startedTxn = true;
        }

        Resource resource = null;
        boolean created = false;
        try {
            // See if the MetamodelRegistry has the URI ...
            log(ModelerCore.Util.getString("ContainerImpl.>>_ContainerImpl.delegatedGetResource__URI___14") + uri + ", loadOnDemand= " + loadOnDemand); //$NON-NLS-1$ //$NON-NLS-2$
            if (this.metamodelRegistry != null && this.metamodelRegistry.containsURI(uri)) {
                resource = this.metamodelRegistry.getResource(uri);
                log(ModelerCore.Util.getString("ContainerImpl.>>_Returning_Resource_in_the_MetamodelRegistry_with_URI___16") + uri + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // Otherwsise, just delegate to the model resource set ...
            created = this.resourceSet.getResource(uri, false) == null;
            resource = this.resourceSet.getResource(uri, loadOnDemand);
        } finally {
            if (startedTxn) {
                try {
                    if (resource != null) {
                        txn.commit();
                    } else {
                        txn.rollback();
                    }
                } catch (ModelerCoreException e) {
                    throw new ModelerCoreRuntimeException(e);
                }
            }
            // We just read in the file; make sure it does NOT think it has changes.
            // When root objects are created and added to the Resource.getContents() list, the
            // list may mark the resource as changed.
            if (resource != null && created) {
                resource.setModified(false);
            }
        }

        return resource;
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getResourceFactoryRegistry()
     */
    public Registry getResourceFactoryRegistry() {
        return this.resourceSet.getResourceFactoryRegistry();
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getResources()
     */
    public EList<Resource> getResources() {
        return this.resourceSet.getResources();
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getURIConverter()
     */
    public URIConverter getURIConverter() {
        return this.resourceSet.getURIConverter();
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#setResourceFactoryRegistry(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
     */
    public void setResourceFactoryRegistry( final Registry resourceFactoryRegistry ) {
        this.resourceSet.setResourceFactoryRegistry(resourceFactoryRegistry);
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#setURIConverter(org.eclipse.emf.ecore.resource.URIConverter)
     */
    public void setURIConverter( final URIConverter converter ) {
        this.resourceSet.setURIConverter(converter);
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.common.notify.Notifier#eAdapters()
     */
    public EList<Adapter> eAdapters() {
        return this.resourceSet.eAdapters();
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.common.notify.Notifier#eDeliver()
     */
    public boolean eDeliver() {
        return this.resourceSet.eDeliver();
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.common.notify.Notifier#eNotify(org.eclipse.emf.common.notify.Notification)
     */
    public void eNotify( final Notification notification ) {
        this.resourceSet.eNotify(notification);
    }

    /**
     * This implementation forwards the request directly to the contained ResourceSet.
     * 
     * @see org.eclipse.emf.common.notify.Notifier#eSetDeliver(boolean)
     */
    public void eSetDeliver( final boolean deliver ) {
        this.resourceSet.eSetDeliver(deliver);
    }

    // ###################################################################################
    // # Public IEditingDomainProvider methods
    // ###################################################################################

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.domain.IEditingDomainProvider#getEditingDomain()
     */
    public EditingDomain getEditingDomain() {
        if (this.editingDomain == null) {
            this.setEditingDomain(createDefaultEditingDomain());
        }
        return this.editingDomain;
    }

    // ###################################################################################
    // # Protected methods
    // ###################################################################################

    /**
     * Indicates if the specified <code>Notification</code>'s resource should be set modified.
     * 
     * @param notification the notification being checked
     * @return <code>true</code> if it should be processed; <code>false</code> otherwise.
     */
    private boolean shouldProcess( Notification notification ) {
        // don't process schema referencing directives as this will set dependent resources dirty
        if (notification.getNotifier() != null
            && notification.getFeatureID(notification.getNotifier().getClass()) == XSDPackage.XSD_SCHEMA__REFERENCING_DIRECTIVES) {
            return false;
        }

        Object val;

        switch (notification.getEventType()) {
            case Notification.ADD:
            case Notification.ADD_MANY: {
                val = notification.getNewValue();
                break;
            }
            case Notification.REMOVE:
            case Notification.REMOVE_MANY: {
                val = notification.getOldValue();
                break;
            }
            case Notification.RESOLVE: {
                return false;
            }
            case Notification.SET:
            case Notification.UNSET: {
                // only process if value has changed
                boolean result = true;
                Object oldValue = notification.getOldValue();
                Object newValue = notification.getNewValue();

                if (oldValue == null) {
                    result = (newValue != null);
                } else if (newValue != null) {
                    result = !oldValue.equals(newValue);
                }

                return result;
            }
            default: {
                return true;
            }
        }

        // don't process if collections go from null to empty
        if (val == null || (val instanceof Collection && ((Collection)val).isEmpty())
            || (val instanceof Object[] && ((Object[])val).length == 0)) {
            return false;
        }

        return true;
    }

    /**
     * Indicates if the specified <code>Resource</codee> is a member of an external resource set.
     * 
     * @param theResource the resource being checked
     * @return <code>true</code> if a member; <code>false</code> otherwise.
     * @since 5.0.2
     */
    private boolean isExternalResourceSetMember( Resource theResource ) {
        boolean result = false;
        ResourceSet[] sets = ModelerCore.getExternalResourceSets();

        for (int ndx = sets.length; --ndx >= 0;) {
            ResourceSet set = sets[ndx];

            if (theResource.getResourceSet() == set) {
                result = true;
                break;
            }
        }

        return result;
    }

    protected void processNotification( Notification notification ) {
        if (!shouldProcess(notification)) {
            return;
        }

        Resource resource = null;
        Object notifier = notification.getNotifier();
        int eventType = notification.getEventType();
        boolean doit = false;

        // mark the resource modified if the notification is for (1) a resource that is not a member of
        // an external resource set or for (2) an EObject of a resource.
        if (notifier instanceof Resource) {
            // make sure the feature is NOT the modified feature
            resource = (Resource)notifier;
            int featureId = notification.getFeatureID(resource.getClass());
            if (eventType != Notification.REMOVING_ADAPTER && featureId != Resource.RESOURCE__IS_LOADED
                && featureId != Resource.RESOURCE__IS_MODIFIED && featureId != Resource.RESOURCE__RESOURCE_SET
                && !isExternalResourceSetMember(resource)) {
                doit = true;
            }
        } else if (notifier instanceof EObject) {
            resource = ((EObject)notifier).eResource();
            doit = true;
        }

        // Ensure change occurs within a transaction (potentially after the fact)
        Object feature = notification.getFeature();
        Object newVal = notification.getNewValue();
        String desc = (feature == null ? null : ModelerCore.Util.getString("ContainerImpl.setFeatureDescription", feature, newVal)); //$NON-NLS-1$
        boolean xActionStarted = ModelerCore.startTxn(desc, this);
        boolean xActionSucceeded = false;
        try {
            try {
                Container ctnr = ModelerCore.getModelContainer();
                if (xActionStarted && CoreModelerPlugin.getTransactionManager() != null && feature != null
                    && eventType == Notification.SET && ctnr != null && notifier instanceof EObject
                    && (!(resource instanceof XResource) || !((XResource)resource).isLoading())) {
                    CommandParameter prm = new CommandParameter(notifier, feature, newVal);
                    Command cmd = ((ContainerImpl)ctnr).getEditingDomain().createCommand(SetCommand.class, prm);
                    ((ModelEditorImpl)ModelerCore.getModelEditor()).postExecuteCommand((EObject)notifier, cmd);
                }
            } catch (CoreException err) {
                ModelerCore.Util.log(err);
            }

            final UnitOfWorkImpl txn = (UnitOfWorkImpl)getEmfTransactionProvider().getCurrent();

            boolean shouldCheckImports = false;
            if (resource != null) {
                if (resource instanceof XResource) {
                    XResource xRes = (XResource)resource;
                    shouldCheckImports = !xRes.isLoading() && !xRes.isUnloading();
                } else {
                    shouldCheckImports = true;
                }

                if (shouldCheckImports) {
                    ProcessedNotificationResult result = ExternalResourceImportsHelper.processNotification(notification);
                    if (result != null && !result.getDereferencedResources().isEmpty()) {
                        txn.addProcessedNotificationResult(result);
                    }
                }
            }

            //debug("Change", resource, theNotification); //$NON-NLS-1$
            // now mark as modified if it is (1) not already modified or if (2) we're not rolling back a transaction
            if (doit && (resource != null) && !resource.isModified() && !txn.isRollingBack()) {
                resource.setModified(true);
            }

            try {
                if (txn.isStarted() || txn.isRollingBack()) {
                    // now let the transaction fire the events to the workspace
                    txn.processNotification(notification);
                } else {
                    notifyForClosedUoW(txn, notification);
                }
            } catch (ModelerCoreException e) {
                ModelerCore.Util.log(e);
            }

            // Mark transaction as succeeded so it gets committed
            xActionSucceeded = true;
        } finally {
            if (xActionStarted) {
                if (xActionSucceeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    public static void debug( String heading,
                              Notification notification ) {
        System.out.println("\n" + heading + ":"); //$NON-NLS-1$ //$NON-NLS-2$
        Resource resource = null;
        Object notifier = notification.getNotifier();
        if (notifier instanceof Resource) {
            // make sure the feature is NOT the modified feature
            resource = (Resource)notifier;
        } else if (notifier instanceof EObject) {
            resource = ((EObject)notifier).eResource();
        }
        System.out.println("\tResource=" + resource); //$NON-NLS-1$
        System.out.println("\tNotifier=" + notifier); //$NON-NLS-1$
        System.out.print("\tType="); //$NON-NLS-1$
        switch (notification.getEventType()) {
            case Notification.ADD:
                System.out.println("ADD");break; //$NON-NLS-1$
            case Notification.ADD_MANY:
                System.out.println("ADD_MANY");break; //$NON-NLS-1$
            case Notification.MOVE:
                System.out.println("MOVE");break; //$NON-NLS-1$
            case Notification.REMOVE:
                System.out.println("REMOVE");break; //$NON-NLS-1$
            case Notification.REMOVE_MANY:
                System.out.println("REMOVE_MANY");break; //$NON-NLS-1$
            case Notification.REMOVING_ADAPTER:
                System.out.println("REMOVING_ADAPTER");break; //$NON-NLS-1$
            case Notification.RESOLVE:
                System.out.println("RESOLVE");break; //$NON-NLS-1$
            case Notification.SET:
                System.out.println("SET");break; //$NON-NLS-1$
            case Notification.UNSET:
                System.out.println("UNSET");break; //$NON-NLS-1$
        }
        int featureId = notification.getFeatureID(notification.getNotifier().getClass());
        System.out.println("\tFeature ID=" + featureId); //$NON-NLS-1$
        if (notifier instanceof ResourceSet) {
            System.out.println("\tFeature=RESOURCES"); //$NON-NLS-1$
        } else if (notifier instanceof Resource) {
            System.out.print("\tFeature="); //$NON-NLS-1$
            switch (featureId) {
                case Resource.RESOURCE__CONTENTS:
                    System.out.println("CONTENTS");break; //$NON-NLS-1$
                case Resource.RESOURCE__ERRORS:
                    System.out.println("ERRORS");break; //$NON-NLS-1$
                case Resource.RESOURCE__IS_LOADED:
                    System.out.println("IS_LOADED");break; //$NON-NLS-1$
                case Resource.RESOURCE__IS_MODIFIED:
                    System.out.println("IS_MODIFIED");break; //$NON-NLS-1$
                case Resource.RESOURCE__IS_TRACKING_MODIFICATION:
                    System.out.println("IS_TRACKING_MODIFICATION");break; //$NON-NLS-1$
                case Resource.RESOURCE__RESOURCE_SET:
                    System.out.println("RESOURCE_SET");break; //$NON-NLS-1$
                case Resource.RESOURCE__URI:
                    System.out.println("URI");break; //$NON-NLS-1$
                case Resource.RESOURCE__WARNINGS:
                    System.out.println("WARNINGS");break; //$NON-NLS-1$
            }
        } else {
            System.out.println("\tFeature=" + notification.getFeature()); //$NON-NLS-1$
        }
        System.out.println("\tNew Value=" + notification.getNewValue()); //$NON-NLS-1$
        System.out.println("\tOld Value=" + notification.getOldValue()); //$NON-NLS-1$
    }

    /**
     * If there is no currently started uow check to see if we are dealing with non-proxied metamodel objects. If so, start the
     * uow, process the notification and commit the uow.
     */
    private void notifyForClosedUoW( final UnitOfWorkImpl txn,
                                     final Notification msg ) {
        final Object notifier = msg.getNotifier();
        if (notifier instanceof EObject) {
            final String nsUriString = ((EObject)notifier).eClass().getEPackage().getNsURI();
            if (!StringUtil.isEmpty(nsUriString)) {
                final MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(nsUriString);
                if (descriptor != null) {
                    try {
                        txn.begin();
                        txn.setSource(this); // Set the container as the source of this transaction
                        txn.processNotification(msg);
                        return;
                    } catch (ModelerCoreException e) {
                        ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                    } finally {
                        if (txn.isStarted()) {
                            try {
                                txn.commit();
                            } catch (ModelerCoreException e1) {
                                ModelerCore.Util.log(IStatus.ERROR, e1, e1.getMessage());
                            }
                        }
                    }
                }
            }
        }

        if (ModelerCore.DEBUG_NOTIFICATIONS) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ContainerImpl.Could_not_process_notification_for_closed_txn_1")); //$NON-NLS-1$
        }
    }

    /**
     * @see com.metamatrix.api.mtk.core.AbstractContainer#performStart()
     */
    protected void performStart() {
        setState(STARTING);

        // Create the resource set and add the EContentAdapter for processing notifications
        this.resourceSet = new EmfResourceSetImpl(this);
        this.resourceSet.eAdapters().add(new EContentAdapter() {
            @Override
            public void notifyChanged( Notification msg ) {
                super.notifyChanged(msg);
                processNotification(msg);
            }
        });

        // Set the URIConverter instance ...
        resourceSet.setURIConverter(new ExtensibleURIConverterImpl());

        // Initialize the IDGenerator
        this.initializeIDGenerator();

        // Create the EMF Transaction Provider
        this.emfTransactionProvider = new UnitOfWorkProviderImpl(this.resourceSet);

        // Set the Metamodel Registry reference
        MetamodelRegistry registry = ModelerCore.getMetamodelRegistry();
        this.metamodelRegistry = registry;

        setState(STARTED);
    }

    protected boolean isReadOnly( final Resource resource ) {
        if (resource != null && resource.getURI().isFile()) {
            File f = new File(resource.getURI().toFileString());
            if (f.exists() && !f.canWrite()) {
                return true;
            }
        }
        return false;
    }

    // public void testNonPlatformLogIStatus_ErrorMultiStatusWithMessageWithThrowable() {
    // final int embeddedSeverity1 = IStatus.WARNING;
    // final int embeddedSeverity2 = IStatus.ERROR;
    // final int code = 100;
    //        final String embeddedMessage1 = "This is the embedded warning message 1"; //$NON-NLS-1$
    //        final String embeddedMessage2 = "This is the embedded error message 2"; //$NON-NLS-1$
    //        final String message = "This is the message for the outter multistatus"; //$NON-NLS-1$
    //        final Throwable t = new Throwable("This is the throwable"); //$NON-NLS-1$
    // t.fillInStackTrace();
    //        final String pluginID = "my.plugin.id"; //$NON-NLS-1$
    // final IStatus embeddedStatus1 = new Status(embeddedSeverity1,pluginID,code+1,embeddedMessage1,t);
    // final IStatus embeddedStatus2 = new Status(embeddedSeverity2,pluginID,code,embeddedMessage2,t);
    // final IStatus[] embedded = new IStatus[]{embeddedStatus1,embeddedStatus2};
    // final IStatus status = new MultiStatus(pluginID,code,embedded,message,t);
    // CorePlugin.Util.log(status);
    // }

    // #############################################################################
    // # Private methods
    // #############################################################################

    // private void tempInitializeMetamodelRegistry(){
    // String METAMODELS_EXTENSION_POINT_ID = "com.metamatrix.mtk.emf.metamodel";
    // this.metamodelRegistry = new MetamodelRegistryImpl();
    //
    // // Load the MetamodelRegistry will all extensions of the metamodel extension point
    // final IExtension[] extensions = PluginUtilities.getExtensions(METAMODELS_EXTENSION_POINT_ID);
    // for (int i = 0; i < extensions.length; i++) {
    // final IExtension extension = extensions[i];
    // try {
    // metamodelRegistry.register(new MetamodelDescriptorImpl(extension));
    // } catch (ModelerCoreException e) {
    // System.err.println(e);
    // }
    // }
    // }

    private void initializeIDGenerator() {
        // Register all known ObjectIDFactory types with the IDGenerator
        IDGenerator idGen = IDGenerator.getInstance();
        idGen.addBuiltInFactories();
    }

    private void log( final String msg ) {
        if (ModelerCore.DEBUG) {
            ModelerCore.Util.log(msg);
        }
    }

    // /**
    // * @see com.metamatrix.mtk.core.impl.AbstractProxyContainer#resolveDelegate(com.metamatrix.mtk.core.ProxyHandler)
    // * @since 3.1
    // */
    // protected Object resolveDelegate(final ProxyHandler handler) {
    // return ((EmfResource)handler.getResource()).getEObject(handler.getKey());
    // }
    //
    /**
     * Returns the IStatus.
     * 
     * @return Istatus
     */
    public IStatus getStatus() {
        return this.status;
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#addResourceDescriptor(com.metamatrix.modeler.core.container.ResourceDescriptor)
     */
    public void addResourceDescriptor( ResourceDescriptor resourceDescriptor ) throws ModelerCoreException {
        ResourceDescriptorImpl.register(resourceDescriptor, resourceSet);
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#addExternalResourceSet(org.eclipse.emf.ecore.resource.ResourceSet)
     */
    public void addExternalResourceSet( final ResourceSet rsrcSet ) {
        ArgCheck.isNotNull(rsrcSet);
        if (this.resourceSet instanceof EmfResourceSetImpl) {
            ((EmfResourceSetImpl)this.resourceSet).addExternalResourceSet(rsrcSet);
        }
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#getExternalResourceSets()
     * @since 4.3
     */
    public ResourceSet[] getExternalResourceSets() {
        if (this.resourceSet instanceof EmfResourceSetImpl) {
            return ((EmfResourceSetImpl)this.resourceSet).getExternalResourceSets();
        }
        return EMPTY_RESOURCE_SET_ARRAY;
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#getOptions()
     * @since 4.3
     */
    public Map getOptions() {
        return (this.options == null ? Collections.EMPTY_MAP : this.options);
    }

    /**
     * @see com.metamatrix.modeler.core.container.Container#setOptions(java.util.Map)
     * @since 4.3
     */
    public void setOptions( Map options ) {
        verifySetIsAllowed(this.options);
        this.options = options;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#createResource(org.eclipse.emf.common.util.URI, java.lang.String)
     */
    public Resource createResource( URI uri,
                                    String contentType ) {
        return this.resourceSet.createResource(uri, contentType);
    }

}
