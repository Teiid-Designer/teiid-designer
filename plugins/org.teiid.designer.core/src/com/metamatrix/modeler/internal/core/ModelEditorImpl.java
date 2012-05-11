/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandWrapper;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AbstractOverrideableCommand;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CopyCommand;
import org.eclipse.emf.edit.command.CopyToClipboardCommand;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.CreateCopyCommand;
import org.eclipse.emf.edit.command.CutToClipboardCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.PasteFromClipboardCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.TeiidException;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.id.IDGenerator;
import org.teiid.core.id.InvalidIDException;
import org.teiid.core.id.ObjectID;
import org.teiid.core.id.ObjectIDFactory;
import org.teiid.core.id.UUID;
import com.metamatrix.common.xmi.XMIHeader;
import com.metamatrix.common.xmi.XMIHeaderReader;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.DebuggingStopwatch;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.custom.impl.XsdModelAnnotationImpl;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.core.extension.impl.ExtensionFactoryImpl;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ExtensionDescriptor;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.association.AssociationProvider;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.EObjectFinder;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeCheckerAspect;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;
import com.metamatrix.modeler.core.util.DisabledCommand;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;
import com.metamatrix.modeler.internal.core.container.CloneCommand;
import com.metamatrix.modeler.internal.core.container.ContainerEditingDomain;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.metamodel.MetamodelRootClass;
import com.metamatrix.modeler.internal.core.resource.MMXmiResource;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.core.search.DeleteRelatedWorkspaceSearch;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;
import com.metamatrix.modeler.internal.core.transaction.UnitOfWorkImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

public class ModelEditorImpl implements ModelEditor {

    /**
     * Defines the XML Schema-of-schemas namespace URI - must be consistent with the values found in com.metamatrix.metamodels.xsd
     * plugin.xml
     */
    public static final String XML_SCHEMA_METAMODEL_URI = XSDPackage.eNS_URI;

    private static final String NAME_FEATURE_NAME = "name"; //$NON-NLS-1$
    private static final String ESTRING_MAP_NAME = "EStringToStringMapEntry"; //$NON-NLS-1$

    protected static final String NEW = ModelerCore.Util.getString("ModelEditorImpl.New_1"); //$NON-NLS-1$

    private static final ObjectIDFactory OBJECT_ID_FACTORY = IDGenerator.getInstance().getFactory(UUID.PROTOCOL);

    static final int BUFFER_LENGTH = 8192;
    private static final int UUID_PROTOCOL_LENGTH = UUID.PROTOCOL.length();
    private static final int UUID_PREFIX_LENGTH = UUID_PROTOCOL_LENGTH + 1;

    private static ContainerImpl ctnr;

    public static ContainerImpl getContainer() {
        if (ModelEditorImpl.ctnr != null) {
            return ModelEditorImpl.ctnr;
        }
        try {
            return (ContainerImpl)ModelerCore.getModelContainer();
        } catch (CoreException err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#setContainer(com.metamatrix.modeler.internal.core.container.ContainerImpl)
     * @since 5.0
     */
    public static void setContainer( final ContainerImpl container ) {
        ModelEditorImpl.ctnr = container;
    }

    public boolean isDatatypeFeature( final EObject object,
                                      final EStructuralFeature feature ) {
        if (object == null || feature == null) {
            return false;
        }
        final SqlAspect aspect = AspectManager.getSqlAspect(object);
        if (aspect instanceof SqlDatatypeCheckerAspect) {
            return ((SqlDatatypeCheckerAspect)aspect).isDatatypeFeature(object, feature);
        }
        return false;
    }

    public Collection getTags( final EObject obj ) {
        Annotation annotation = this.getAnnotation(obj, false);
        if (annotation == null) {
            return Collections.EMPTY_LIST;
        }
        final ArrayList tags = new ArrayList();

        final Iterator refs = annotation.eClass().getEReferences().iterator();
        while (refs.hasNext()) {
            final EReference ref = (EReference)refs.next();
            if (ref.getEType() != null && ref.getEType().getName() != null && ref.getEType().getName().equals(ESTRING_MAP_NAME)) {
                Object val = annotation.eGet(ref);
                if (val != null) {
                    tags.add(val);
                }
            }
        }

        return tags;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#equals(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public boolean equals( final EObject eObject1,
                           final EObject eObject2 ) {
        if (eObject1 == null || eObject2 == null) {
            return false;
        }
        if (eObject1 == eObject2) {
            return true;
        }
        EObject eObj1 = eObject1;
        EObject eObj2 = eObject2;
        boolean eObjIsProxy1 = eObj1.eIsProxy();
        boolean eObjIsProxy2 = eObj2.eIsProxy();
        if (eObjIsProxy1 && eObjIsProxy2) {
            URI eProxyUri1 = EcoreUtil.getURI(eObj1);
            URI eProxyUri2 = EcoreUtil.getURI(eObj2);
            return eProxyUri1.equals(eProxyUri2);
        }
        if (eObjIsProxy1) {
            try {
                eObj1 = EcoreUtil.resolve(eObj1, getContainer());
            } catch (Exception e) {
                final String msg = ModelerCore.Util.getString("ModelEditorImpl.Error_getting_model_container._1"); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.ERROR, e, msg);
            }
        }
        if (eObjIsProxy2) {
            try {
                eObj2 = EcoreUtil.resolve(eObj2, getContainer());
            } catch (Exception e) {
                final String msg = ModelerCore.Util.getString("ModelEditorImpl.Error_getting_model_container._2"); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.ERROR, e, msg);
            }
        }
        return eObj1.equals(eObj2);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#createInitialModel(org.eclipse.emf.ecore.resource.Resource,
     *      org.eclipse.emf.ecore.EClass)
     */
    public EObject createInitialModel( final Resource resource,
                                       final EClass eClass ) throws ModelerCoreException {
        if (!isRootObject(eClass)) {
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("ModelEditorImpl.{0}_is_not_a_valid_root_object_1", eClass.getName())); //$NON-NLS-1$
        }

        final EObject rootObject = this.create(eClass);
        if (rootObject != null) {
            resource.getContents().add(rootObject);
        }
        return rootObject;
    }

    /**
     * Return the MetamodelDescriptor for the given EObject
     * 
     * @param object
     * @return the MetamodelDescriptor for the given EObject
     */
    public MetamodelDescriptor getMetamodelDescriptor( final EObject object ) {
        CoreArgCheck.isNotNull(object);
        Container cntr = getContainer();
        String uri = object.eClass().getEPackage().getNsURI();
        // If the object is a proxy look up the descriptor
        // in the container's metamodel registry
        if (uri != null && cntr != null) {
            return cntr.getMetamodelRegistry().getMetamodelDescriptor(URI.createURI(uri));
        }
        // If the object is not a proxy look up the descriptor
        // in the metamodel registry in ModelerCore
        else if (uri != null && cntr == null) {
            return ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(URI.createURI(uri));
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getPrimaryMetamodelDescriptor(com.metamatrix.modeler.core.workspace.ModelResource)
     */
    public MetamodelDescriptor getPrimaryMetamodelDescriptor( final ModelResource resource ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(resource);
        return resource.getPrimaryMetamodelDescriptor();
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getPrimaryMetamodelDescriptor(org.eclipse.emf.ecore.resource.Resource)
     */
    public MetamodelDescriptor getPrimaryMetamodelDescriptor( final Resource resource ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(resource);

        // See if this resource is an EmfResource; if so, there is a shortcut to not look up the Model Resource ...
        if (resource instanceof MMXmiResource) {
            final MMXmiResource emfResource = (MMXmiResource)resource;
            final String primaryMetamodelUri = getPrimaryMetamodelURI(emfResource);
            if (primaryMetamodelUri != null && primaryMetamodelUri.length() != 0) {
                // Look up the descriptor in the metamodels ...
                final URI nsUri = ModelerCore.getMetamodelRegistry().getURI(primaryMetamodelUri);
                final MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(nsUri);

                // Log an error if there is no metamodel with this primary metamodel URI. There may be
                // an inconsistency between the metamodel extenion URIs and the primary metamodel URI
                // in the resource.
                if (descriptor == null) {
                    final StringBuffer sb = new StringBuffer();
                    final MetamodelDescriptor[] descriptors = ModelerCore.getMetamodelRegistry().getMetamodelDescriptors();
                    for (int i = 0; i < descriptors.length; i++) {
                        final MetamodelDescriptor mmd = descriptors[i];
                        if (mmd.isPrimary() && !CoreStringUtil.isEmpty(mmd.getNamespaceURI())) {
                            sb.append(mmd.getNamespaceURI());
                            sb.append(CoreStringUtil.Constants.SPACE);
                        }
                    }
                    final Object[] params = new Object[] {primaryMetamodelUri, sb.toString()};
                    final String msg = ModelerCore.Util.getString("ModelResourceImpl.no_metamodel_found_for_primary_metamodel_URI", params); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                }
                return descriptor;
            }
        }

        // Not found the easy way, so do the lookup ...
        final ModelResource modelResource = this.findModelResource(resource);
        return getPrimaryMetamodelDescriptor(modelResource);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getNewChildDescriptors(org.eclipse.emf.ecore.EObject)
     */
    public Collection getNewChildCommands( final EObject eObject ) throws ModelerCoreException {
        final ContainerImpl container = getContainer();
        if (container != null) {

            // Wrapping this in a transaction to avoid the sub-set operations from being placed in the
            // undo/redo stack. IsSignificant and Undoable have both been set to false so the entire
            // operation will not be present in the edit->undo menu. This functionality is called
            // when you open the New Child sub-menu.
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) {
                    final EditingDomain domain = container.getEditingDomain();
                    Collection result = domain.getNewChildDescriptors(eObject, null);

                    setDescriptorOwner(result, eObject);
                    result = createCommands(result, domain);
                    return (result != null ? result : Collections.EMPTY_LIST);
                }
            };
            Object result = executeAsTransaction(runnable, container, "Getting Child Commands", false, false, eObject); //$NON-NLS-1$
            return result instanceof Collection ? (Collection)result : Collections.EMPTY_LIST;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * This method determines whether the {@link CommandParameter} instance would result in a violation of the maximum
     * multiplicity.
     * 
     * @param owner the parent of a new object created by the command parameter
     * @param commandParams the list of command parameters; may not be null
     * @return true if the command should be disabled, or false otherwise
     */
    protected boolean shouldBeDisabled( final Object owner,
                                        final CommandParameter commandParam ) {
        if (owner instanceof EObject) {
            final EObject parent = (EObject)owner;
            final EStructuralFeature feature = commandParam.getEStructuralFeature();
            final int maxMultiplicity = feature.getUpperBound();
            if (maxMultiplicity == -1) {
                // feature is unbounded ...
                return false;
            }
            if (maxMultiplicity > 1) {
                // Get the current number of values ...
                final Object currentValue = parent.eGet(feature);
                // The current value should be a list
                final int currentSize = ((List)currentValue).size();
                if (currentSize >= maxMultiplicity) {
                    // The current size is that of the maximum allowed, so wrap the command parameter
                    return true;
                }
                // Otherwise it is okay to keep
            } else {
                // The current value is not a list but a single value ...
                final boolean isSet;
                try {
                    isSet = parent.eIsSet(feature);
                } catch (IllegalArgumentException e) {
                    // IAE results if the feature is not settable
                    return true;
                }

                if (isSet) {
                    // The value is current set, so unable to add another
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getNewChildDescriptors(org.eclipse.emf.ecore.EObject)
     */
    public Collection getNewRootObjectCommands( final Resource resource ) {
        final ContainerImpl container = getContainer();
        final EditingDomain domain = container.getEditingDomain();
        Collection descriptors = getRootDescriptors(resource);

        return createCommands(descriptors, domain);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getNewSiblingDescriptors(org.eclipse.emf.ecore.EObject)
     */
    public Collection getNewSiblingCommands( final EObject eObject ) throws ModelerCoreException {
        final ContainerImpl container = getContainer();

        // Wrapping this in a transaction to avoid the sub-set operations from being placed in the
        // undo/redo stack. IsSignificant and Undoable have both been set to false so the entire
        // operation will not be present in the edit->undo menu. This functionality is called
        // when you open the New Sibling sub-menu.
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) {
                Collection descriptors = null;
                final EditingDomain domain = container.getEditingDomain();
                if (eObject.eContainer() == null) {
                    URI nsUri = eObject.eClass().eResource().getURI();
                    List rootClasses = Arrays.asList(ModelerCore.getMetamodelRegistry().getMetamodelRootClasses(nsUri));

                    Collection filteredClasses = new ArrayList(rootClasses.size());
                    for( Object obj : rootClasses ) {
                    	String className = ((MetamodelRootClass)obj).getEClass().getName();
                    	if( !className.equalsIgnoreCase("BaseTable") ) { //$NON-NLS-1$
                    		filteredClasses.add(obj);
                    	}
                    }
                    
                    final EPackage ePackage = ModelerCore.getMetamodelRegistry().getEPackage(nsUri);
                    final EFactory eFactory = ePackage.getEFactoryInstance();
                    descriptors = createSiblingDescriptors(filteredClasses, eFactory, eObject);
                    descriptors = createCommands(descriptors, domain);
                } else {
                    descriptors = domain.getNewChildDescriptors(null, eObject);
                    setDescriptorOwner(descriptors, eObject.eContainer());
                    descriptors = createCommands(descriptors, domain);
                }

                if (descriptors == null) {
                    return Collections.EMPTY_LIST;
                }
                return descriptors;
            }
        };
        Object result = executeAsTransaction(runnable, container, "Getting Sibling Commands", false, false, eObject); //$NON-NLS-1$
        return result instanceof Collection ? (Collection)result : Collections.EMPTY_LIST;
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#executeAsTransaction(com.metamatrix.modeler.core.TransactionRunnable,
     *      java.lang.String, boolean)
     * @since 4.0
     */
    public Object executeAsTransaction( final TransactionRunnable runnable,
                                        final String description,
                                        final boolean significant,
                                        final Object source ) throws ModelerCoreException {
        try {
            return executeAsTransaction(runnable, null, description, significant, source);
        } catch (final CoreException err) {
            throw new ModelerCoreException(err);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#executeAsTransaction(com.metamatrix.modeler.core.TransactionRunnable,
     *      java.lang.String, boolean)
     * @since 4.0
     */
    public Object executeAsTransaction( final TransactionRunnable runnable,
                                        final String description,
                                        final boolean significant,
                                        final boolean undoable,
                                        final Object source ) throws ModelerCoreException {
        try {
            return executeAsTransaction(runnable, null, description, significant, undoable, source);
        } catch (final CoreException err) {
            throw new ModelerCoreException(err);
        }
    }

    /**
	 */
    protected Object executeAsTransaction( final TransactionRunnable runnable,
                                           Container container,
                                           final String operationDescription,
                                           final boolean isSignificant,
                                           final boolean undoable,
                                           final Object source ) throws ModelerCoreException {
        // Use default container if not specified
        if (container == null) {
            container = getContainer();
        }
        // Get the current transaction (this may create one) ...
        final UnitOfWork uow = container.getEmfTransactionProvider().getCurrent();
        boolean startedTxn = false;
        // If the current transaction is new, it must be started ...
        if (uow.requiresStart()) {
            uow.begin();
            startedTxn = true;
            uow.setSignificant(isSignificant);
            uow.setSource(source);
            uow.setUndoable(undoable);
            // Set the description ...
            if (operationDescription != null) {
                uow.setDescription(operationDescription);
            }
        }
        boolean failed = false;
        Object result = null;
        try {
            // Perform the requested work
            result = runnable.run(uow);
        } catch (ModelerCoreException t) {
            // There was a runtime exception while performing the work
            failed = true;
            throw t;
        } catch (RuntimeException t) {
            // There was a runtime exception while performing the work
            failed = true;
            throw new ModelerCoreRuntimeException(t);
        } finally {
            if (startedTxn) {
                // We started the current transaction ...
                if (!failed) {
                    // Commit the transaction ...
                    try {
                        uow.commit();
                    } catch (Throwable e) {
                        ModelerCore.Util.log(e);
                        failed = true;
                    }
                }

                if (failed) {
                    // Rollback the transaction ...
                    // (which may have failed while performing the work or when committing)
                    try {
                        uow.rollback();
                    } catch (ModelerCoreException e1) {
                        ModelerCore.Util.log(e1);
                    }
                }
            }
        }
        return result;
    }

    /**
	 */
    public Object executeAsTransaction( final TransactionRunnable runnable,
                                        final Container container,
                                        final String operationDescription,
                                        final boolean isSignificant,
                                        final Object source ) throws ModelerCoreException {
        return executeAsTransaction(runnable, container, operationDescription, isSignificant, true, source);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#createNewChildFromDescriptor(java.lang.Object)
     */
    public EObject createNewChildFromCommand( final EObject parent,
                                              final Command cmd ) throws ModelerCoreException {
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Create_New_Child_for_{0}_1", getPresentationValue(parent)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                executeCommandInTransaction(uow, parent, cmd);

                EObject child = null;
                if (cmd.getResult() != null) {
                    child = (EObject)cmd.getResult().iterator().next();
                    ModelEditorImpl.this.renameInternal(child, NEW + child.eClass().getName());
                    ModelEditorImpl.this.setUuidFeatureValue(child);
                }
                return child;
            }
        };
        final EObject child = (EObject)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return child;
    }

    protected void setUuidFeatureValue( final EObject eObj ) {
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#createNewChildFromDescriptor(java.lang.Object)
     */
    public EObject createNewRootObjectFromCommand( final Resource parent,
                                                   final Command cmd ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(parent);
        CoreArgCheck.isNotNull(cmd);
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Create_New_Child_for_{0}_1", //$NON-NLS-1$
                                                                       getPresentationValue(parent));
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                executeCommandInTransaction(uow, parent, cmd);

                EObject child = null;
                if (cmd.getResult() != null) {
                    child = (EObject)cmd.getResult().iterator().next();
                    ModelEditorImpl.this.renameInternal(child, NEW + child.eClass().getName());
                    ModelEditorImpl.this.setUuidFeatureValue(child);
                }
                return child;
            }
        };
        final EObject child = (EObject)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return child;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#createNewSiblingFromDescriptor(java.lang.Object)
     */
    public EObject createNewSiblingFromCommand( final EObject sibling,
                                                final Command cmd ) throws ModelerCoreException {
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Create_New_Sibling_for_{0}_2", getPresentationValue(sibling)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                executeCommandInTransaction(uow, sibling, cmd);

                EObject newSibling = null;
                if (cmd.getResult() != null) {
                    newSibling = (EObject)cmd.getResult().iterator().next();
                    ModelEditorImpl.this.renameInternal(newSibling, NEW + newSibling.eClass().getName());
                    setUuidFeatureValue(newSibling);
                }
                return newSibling;
            }
        };
        final EObject newSibling = (EObject)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return newSibling;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#createNewAssociationFromDescriptor(java.lang.Object)
     */
    public EObject createNewAssociationFromDescriptor( Object descriptor ) throws ModelerCoreException {
        if (descriptor != null && descriptor instanceof AbstractAssociationDescriptor) {
            final AbstractAssociationDescriptor assocDescr = (AbstractAssociationDescriptor)descriptor;

            // If the descriptor is considered complete (i.e. containing sufficient information
            // for construction of the association) then create the association
            if (assocDescr.isComplete()) {
                // Start the association construction in a transaction if one does not exist
                final boolean isSignificant = true;
                final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Create_new_Association_3"); //$NON-NLS-1$
                final ContainerImpl cntr = getContainer();
                final TransactionRunnable runnable = new TransactionRunnable() {
                    public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                        // If the descriptor is considered ambiguous then construct the association using
                        // the first AssociationDescriptor instance in the list of creation possibilities
                        EObject result = null;
                        if (assocDescr.isAmbiguous()) {
                            result = ((AbstractAssociationDescriptor)assocDescr.getChildren()[0]).create();
                        }
                        // Else create the association using the single descriptor
                        else {
                            result = assocDescr.create();
                        }

                        ModelEditorImpl.this.renameInternal(result, NEW + result.eClass().getName());
                        return result;
                    }
                };
                final EObject result = (EObject)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
                return result;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getNewAssociationDescriptors(java.util.List)
     */
    public Collection getNewAssociationDescriptors( final List eObjects ) throws ModelerCoreException {

        // Retrieve the list of AssociationProvider instances from the ExtensionDescriptor
        // instances available through the ModelerCore Configuration
        final Configuration config = ModelerCore.getConfiguration();
        final Collection descriptors = config.getAssociationProviderDescriptors();

        // Iterate over the list of AssociationProviders accumulating association descriptors
        final Collection result = new HashSet(descriptors.size());
        for (Iterator iter = descriptors.iterator(); iter.hasNext();) {
            ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
            AssociationProvider provider = (AssociationProvider)descriptor.getExtensionClassInstance();
            if (provider != null) {
                result.addAll(provider.getNewAssociationDescriptors(eObjects));
            }
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#copy(org.eclipse.emf.ecore.EObject)
     */
    public EObject copy( final EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        return copy(eObject, null);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#copy(org.eclipse.emf.ecore.EObject)
     */
    public EObject copy( final EObject eObject,
                         final Map originalsToCopies ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Copy_{0}_4", getPresentationValue(eObject)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                final CopyCommand.Helper helper = new CopyCommand.Helper();
                if (originalsToCopies != null && !originalsToCopies.isEmpty()) {
                    // copy all of the existing entries in the 'originalsToCopies' map into the helper ...
                    // This will change the copies to reference other copies that may have been created
                    // already and are in the map, but are not directly in the graph of contained objects
                    // below the original 'eObject'
                    helper.putAll(originalsToCopies);
                }
                final Command command = createCopyCommand(ed, eObject, helper);
                executeCommandInTransaction(uow, eObject, command);

                EObject result = null;
                if (command.getResult() != null) {
                    result = (EObject)command.getResult().iterator().next();
                    setUuidFeatureValue(result);
                }

                // Populate the map from orig to copies
                if (originalsToCopies != null) originalsToCopies.putAll(helper);

                return result;
            }
        };
        final EObject result = (EObject)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#copy(org.eclipse.emf.ecore.EObject)
     */
    public Collection copyMultiple( final EObject eObject,
                                    final int numCopies ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isPositive(numCopies);

        final ArrayList copies = new ArrayList(numCopies);

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Copy_{0}_4", getPresentationValue(eObject)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                while (copies.size() < numCopies) {
                    final Command command = CopyCommand.create(ed, eObject);
                    executeCommandInTransaction(uow, eObject, command);

                    if (command.getResult() != null) {
                        final EObject copy = (EObject)command.getResult().iterator().next();
                        setUuidFeatureValue(copy);
                        copies.add(copy);
                    } else {
                        throw new ModelerCoreException(ModelerCore.Util.getString("ModelEditorImpl.Error_creating_copies_1")); //$NON-NLS-1$
                    }
                }
                return null;
            }
        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return copies;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#delete(java.util.Collecction)
     */
    public boolean delete( final Collection eObjects ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObjects);

        if (eObjects.isEmpty()) {
            return false;
        }

        boolean result = delete(eObjects, new NullProgressMonitor());

        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#delete(java.util.Collecction)
     */
    public boolean delete( final Collection eObjects,
                           final IProgressMonitor monitor ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObjects);

        if (eObjects.isEmpty()) {
            return false;
        }

        // Execute the command ...
        final boolean isSignificant = true;
        final ContainerImpl cntr = getContainer();
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Delete_multiple_objects_1"); //$NON-NLS-1$
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                Command command = createDeleteManyCommand(ed, eObjects, monitor);

                monitor.subTask(ModelerCore.Util.getString("ModelEditorImpl.executingDeleteMsg")); //$NON-NLS-1$
                monitor.worked(5);

                if (!monitor.isCanceled()) {
                    executeCommandInTransaction(uow, null, command);
                }
                return null;
            }

        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return true;
    }

    public Command createDeleteManyCommand( final EditingDomain ed,
                                            final Collection eObjects,
                                            final IProgressMonitor monitor ) throws ModelerCoreException {
        // You need a delete command for each parent
        final HashMap objectsByContainment = new HashMap();

        final boolean ignoreXsdResources = !includesXsdObject(eObjects);

        DebuggingStopwatch watch = new DebuggingStopwatch(
                                                          "ModelEditorImpl.createDeleteManyCommand(EObject) Ignore XSD's = " + ignoreXsdResources, 10, false); //$NON-NLS-1$
        watch.start();
        watch.startStats();

        // Build up the list of objects that should be deleted in addition to 'eObject' ...
        final LinkedList commands = new LinkedList();
        final Iterator removedObjects = eObjects.iterator();
        while (removedObjects.hasNext()) {
            // object to be deleted
            final EObject eObject = (EObject)removedObjects.next();
            // owner of the object
            final Object owner = eObject.eContainer() == null ? (Object)eObject.eResource() : eObject.eContainer();
            // containment feature of the object in the owner
            final Object feature = eObject.eContainmentFeature();
            // collection of objects by owner or collection of objects by feature by owner
            Object existing = objectsByContainment.get(owner);
            if (existing == null) {
                // no feature just object by owner
                if (feature == null) {
                    existing = new HashSet();
                    // by feature by owner
                } else {
                    existing = new HashMap();
                }
                objectsByContainment.put(owner, existing);
            }
            // update existing with new info
            if (existing instanceof Collection) {
                ((Collection)existing).add(eObject);
            } else if (existing instanceof Map) {
                Collection objectsByFeature = (Collection)((Map)existing).get(feature);
                if (objectsByFeature == null) {
                    objectsByFeature = new HashSet();
                }
                objectsByFeature.add(eObject);
                ((Map)existing).put(feature, objectsByFeature);
            }
        }

        // defect 19592 - moved the following three statements out of the above loop,
        // so that the search for deleted objects can know about all things being deleted.
        // Dennis and Pat suppose the real clincher is that findReferencesToObjectsBeingDeleted
        // is the main beneficiary of this -- findOtherObjectsToBeDeleted is now gathering up
        // everything into one collection for everything instead of one collection per deleted object.

        monitor.worked(5);

        // -----------------------------------------
        // find other objects and references to delete or clear:
        // -----------------------------------------
        // Create Delete-related search (Defect 22774)
        ModelWorkspaceSearch workspaceSearch = new DeleteRelatedWorkspaceSearch(ignoreXsdResources);

        // -----------------------------------------
        // Find other objects to be deleted ... these are returned in a collection containing the original objects
        // and the related objects
        // -----------------------------------------
        final Collection allDeleted = findOtherObjectsToBeDeleted(eObjects, ed, commands, workspaceSearch, monitor);

        monitor.worked(10);

        // -----------------------------------------
        // Find references to objects being deleted ...
        // -----------------------------------------
        findReferencesToObjectsBeingDeleted(allDeleted, ed, commands, workspaceSearch, monitor);

        monitor.subTask(ModelerCore.Util.getString("ModelEditorImpl.preparingCommandMsg")); //$NON-NLS-1$
        monitor.worked(10);

        // -----------------------------------------
        // create commands to delete each eObject to be deleted
        // -----------------------------------------
        for (final Iterator iter1 = objectsByContainment.entrySet().iterator(); iter1.hasNext();) {
            if (monitor.isCanceled()) {
                break;
            }
            // owner to eObjects map
            final Map.Entry entry1 = (Map.Entry)iter1.next();
            Object key = entry1.getKey();
            // owner is a Eobject so there is feature associated with child
            if (key instanceof EObject) {
                final EObject parent = (EObject)key;
                // feature to child map
                final Map objectsByFeature = (Map)entry1.getValue();
                // for each feature delete children
                for (final Iterator iter2 = objectsByFeature.entrySet().iterator(); iter2.hasNext();) {
                    final Map.Entry entry2 = (Map.Entry)iter2.next();
                    final EReference ref = (EReference)entry2.getKey();
                    Collection objects = (Collection)entry2.getValue();
                    if (ref.isMany()) {
                        final CommandParameter param = new CommandParameter(parent, ref, objects);
                        final Command command = ed.createCommand(RemoveCommand.class, param);
                        commands.addFirst(command);
                    } else {
                        final CommandParameter param = new CommandParameter(parent, ref, null);
                        final Command command = ed.createCommand(SetCommand.class, param);
                        commands.addFirst(command);
                    }
                }
            } else {
                // owner to delted objects map
                final Collection values = (Collection)entry1.getValue();
                final Resource parent = (Resource)key;
                final EList contents = parent.getContents();

                final CommandParameter param = new CommandParameter(parent, contents, values);
                final Command command = ed.createCommand(RemoveCommand.class, param);

                commands.addFirst(command);
            }
        }

        Collection finalEObjects = Collections.EMPTY_LIST;
        List finalCommands = Collections.EMPTY_LIST;
        if (!monitor.isCanceled()) {
            finalEObjects = eObjects;
            finalCommands = commands;
        }

        CompoundCommand cCommand = CompoundCommandFactory.create(finalEObjects, finalCommands);

        watch.stop();
        watch.stopStats();
        return cCommand;
    }

    /*
     * Method determines if an object or a Collection of objects is or contains at least ONE EObject contained within an XSD
     * resource. This knowledge can help streamline delete-related searches. See Defect 22774
     */
    private boolean includesXsdObject( Object obj ) {
        try {
            if (obj instanceof EObject) {
                ModelResource mr = findModelResource((EObject)obj);
                if (mr != null && mr.getEmfResource() != null && ModelUtil.isXsdFile(mr.getEmfResource())) {
                    return true;
                }
            } else if (obj instanceof Collection) {
                Collection theObjs = (Collection)obj;
                for (Iterator iter = theObjs.iterator(); iter.hasNext();) {
                    Object nextObj = iter.next();
                    if (nextObj instanceof EObject) {
                        ModelResource mr = findModelResource((EObject)nextObj);
                        if (mr != null && mr.getEmfResource() != null && ModelUtil.isXsdFile(mr.getEmfResource())) {
                            return true;
                        }
                    }
                }
            }
        } catch (ModelWorkspaceException theException) {
            ModelerCore.Util.log(theException);
        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#delete(org.eclipse.emf.ecore.EObject, boolean)
     */
    public boolean delete( final EObject eObject,
                           final boolean performResourceCheck,
                           final boolean performRelatedObjectCheck ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);

        // Check the parent (and resource) to see if the object is already deleted ...
        // Per defect 13042.
        if (performResourceCheck) {
            final Resource resource = eObject.eResource();
            if (resource == null) {
                // The object doesn't exist in a resource, so nothing to delete
                return false;
            }
        }
        final boolean ignoreXsdResources = !includesXsdObject(eObject);

        // Execute the command ...
        final boolean isSignificant = true;
        final ContainerImpl cntr = getContainer();
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Delete_{0}_5", getPresentationValue(eObject)); //$NON-NLS-1$
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                DebuggingStopwatch watch = new DebuggingStopwatch(
                                                                  "ModelEditorImpl.delete(EObject)  Ignore XSD's = " + ignoreXsdResources, 10, false); //$NON-NLS-1$
                watch.start();
                watch.startStats();

                final EditingDomain ed = cntr.getEditingDomain();
                Command command = createDeleteCommand(ed, eObject);

                // some reason command cannot be created
                if (command == null) {
                    return null;
                }
                if (performRelatedObjectCheck) {
                    // Build up the list of objects that should be deleted in addition to 'eObject' ...
                    final LinkedList additionalCommands = new LinkedList();
                    final Resource emfResource = eObject.eResource();

                    // Don't try to delete other object for unattached objects
                    if (emfResource != null) {
                        // ------------------------------
                        // Create a deleted-related workspace search
                        // ------------------------------
                        ModelWorkspaceSearch workspaceSearch = new DeleteRelatedWorkspaceSearch(ignoreXsdResources);

                        // ------------------------------
                        // Find other objects to be deleted ...
                        // ------------------------------
                        final Collection allDeleted = findOtherObjectsToBeDeleted(Collections.singleton(eObject),
                                                                                  ed,
                                                                                  additionalCommands,
                                                                                  workspaceSearch);

                        // ------------------------------
                        // Find references to objects being deleted ...
                        // ------------------------------
                        findReferencesToObjectsBeingDeleted(allDeleted, ed, additionalCommands, workspaceSearch);

                        // ------------------------------
                        // Add any new commands to the compound command ...
                        // ------------------------------
                        if (!additionalCommands.isEmpty()) {
                            additionalCommands.addFirst(command);
                            command = CompoundCommandFactory.create(eObject, additionalCommands);
                        }
                    }
                }

                executeCommandInTransaction(uow, eObject, command);
                watch.stop();
                watch.stopStats();
                return null;
            }

        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#delete(org.eclipse.emf.ecore.EObject)
     */
    public boolean delete( final EObject eObject,
                           final boolean performResourceCheck ) throws ModelerCoreException {
        return this.delete(eObject, performResourceCheck, true);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#delete(org.eclipse.emf.ecore.EObject)
     */
    public boolean delete( final EObject eObject ) throws ModelerCoreException {
        return this.delete(eObject, true);
    }

    /**
     * Create a command to delete the supplied object. This is a utility method.
     * 
     * @param editingDomain
     * @param eObject
     * @return
     */
    protected static Command createDeleteCommand( final EditingDomain editingDomain,
                                                  final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);

        Command command = null;
        if (eObject.eContainer() == null) {
            final Resource rsrc = eObject.eResource();
            if (rsrc != null) {
                EList contents = rsrc.getContents();
                if (contents.contains(eObject)) {
                    command = new RemoveCommand(editingDomain, contents, eObject);
                }
            }
        } else {
            // Create the command through the EditingDomain that way the ItemProvider associated
            // with the "owner" EObject can specialize the createRemoveCommand(...) method.
            // Fix for defect 12328.
            final EStructuralFeature sf = eObject.eContainmentFeature();
            if (sf.isMany()) {
                Collection collection = new HashSet(1);
                collection.add(eObject);
                final CommandParameter param = new CommandParameter(eObject.eContainer(), sf, collection);
                command = editingDomain.createCommand(RemoveCommand.class, param);
            } else {
                final CommandParameter param = new CommandParameter(eObject.eContainer(), sf, null);
                command = editingDomain.createCommand(SetCommand.class, param);
            }
            // final EStructuralFeature sf = eObject.eContainmentFeature();
            // if(sf.isMany() ){
            // command = new RemoveCommand(editingDomain, eObject.eContainer(), sf, eObject);
            // }else{
            // command = new SetCommand(editingDomain, eObject.eContainer(), sf, null);
            // }
        }

        return command;
    }

    /**
     * Find references to the deleted objects and remove/unset them. <i>Note: This method only finds all references from undeleted
     * objects.
     * 
     * @param allDeleted the EObjects being deleted; never null
     * @param editingDomain the editing domain; never null
     * @param additionalCommands the list into which any additional delete commands should be placed; never null
     */
    public void findReferencesToObjectsBeingDeleted( final Collection allDeleted,
                                                     final EditingDomain editingDomain,
                                                     final List additionalCommands,
                                                     final ModelWorkspaceSearch workspaceSearch ) throws ModelerCoreException {

        findReferencesToObjectsBeingDeleted(allDeleted,
                                            editingDomain,
                                            additionalCommands,
                                            workspaceSearch,
                                            new NullProgressMonitor());

    }

    /**
     * Find references to the deleted objects and remove/unset them. <i>Note: This method only finds all references from undeleted
     * objects.
     * 
     * @param allDeleted the EObjects being deleted; never null
     * @param editingDomain the editing domain; never null
     * @param additionalCommands the list into which any additional delete commands should be placed; never null
     */
    public void findReferencesToObjectsBeingDeleted( final Collection allDeleted,
                                                     final EditingDomain editingDomain,
                                                     final List additionalCommands,
                                                     final ModelWorkspaceSearch workspaceSearch,
                                                     final IProgressMonitor monitor ) throws ModelerCoreException {

        int iObj = 1;
        int nObj = allDeleted.size();
        int iInc = 1;
        int halfWay = nObj / 2;
        boolean pastHalfWay = false;
        if (nObj < 20) {
            iInc = 1;
        } else if (nObj < 100) {
            iInc = 10;
        } else if (nObj < 500) {
            iInc = 25;
        } else {
            iInc = 100;
        }

        String startMessage = ModelerCore.Util.getString("ModelEditorImpl.searchingReferencesMsg") + CoreStringUtil.Constants.SPACE; //$NON-NLS-1$
        String ofText = CoreStringUtil.Constants.DBL_SPACE
                        + ModelerCore.Util.getString("ModelEditorImpl.ofText") + CoreStringUtil.Constants.SPACE; //$NON-NLS-1$
        // Construct and use the visitor ...
        final ClearReferencesUponDelete visitor = new ClearReferencesUponDelete(allDeleted, editingDomain, workspaceSearch);
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        for (final Iterator iter = allDeleted.iterator(); iter.hasNext();) {

            if (iObj % iInc == 0) {
                String message = startMessage + iObj + ofText + nObj;
                monitor.subTask(message);
            }

            final EObject eObject = (EObject)iter.next();
            if (eObject.eResource() == null) {
                continue;
            }
            processor.walk(eObject, ModelVisitorProcessor.DEPTH_INFINITE);
            iObj++;

            if (monitor.isCanceled()) {
                break;
            }
            if (!pastHalfWay && iObj > halfWay) {
                monitor.worked(10);
                pastHalfWay = true;
            }
        }

        monitor.worked(10);

        if (!monitor.isCanceled()) {
            // 12/17/03 (LLP) : changed to add new commands to additionalCommands list.
            final List removeAndSetCommands = visitor.getAdditionalCommands();
            if (removeAndSetCommands != null && !removeAndSetCommands.isEmpty()) {
                // 08/31/05 (TBJ) : Added method to check for dups before adding command
                addCommands(removeAndSetCommands, additionalCommands);
            }
        }

    }

    /**
     * This method will add a Command to the master list of addtional commands, if appropriate. Handles instances where an
     * additional command may have already been added as might be the case when there are multiple objects being deleted and they
     * contain bi-directional references such as PrimaryKey and ForeignKeys.
     * 
     * @param removeAndSetCommands List of Remove and Set Commands
     * @param additionalCommands Master list of Remove and Set Commands
     * @since 4.3
     */
    private void addCommands( final List removeAndSetCommands,
                              List additionalCommands ) {
        Object command = null;
        boolean addCommand = true;
        Iterator iter = removeAndSetCommands.iterator();
        /*
         * Iterate through the list of commands to add
         */
        while (iter.hasNext()) {
            command = iter.next();
            /*
             * If this is a RemoveCommand, iterate through through the master list of additional commands to make sure the command
             * has not already been added.
             */
            if (command instanceof RemoveCommand) {
                addCommand = true;
                Iterator addCommandsIter = additionalCommands.iterator();
                while (addCommandsIter.hasNext()) {
                    Object additionalCommand = addCommandsIter.next();
                    if (additionalCommand instanceof RemoveCommand) {
                        if (((RemoveCommand)additionalCommand).getOwner().equals(((RemoveCommand)command).getOwner())
                            && ((RemoveCommand)additionalCommand).getCollection().size() == ((RemoveCommand)command).getCollection().size()
                            && ((RemoveCommand)additionalCommand).getCollection().containsAll(((RemoveCommand)command).getCollection())) {
                            /*
                             * Already added this Command
                             */
                            addCommand = false;
                            break;
                        }
                    }
                }
                if (addCommand) additionalCommands.add(command);
            } else {
                additionalCommands.add(command); // This is not a RemoveCommand, go ahead and add it.
            }
        }
    }

    /**
     * Find related objects to the objects in the objects list and return them.
     * 
     * @param objects the EObjects to the find related objects to
     * @param editingDomain the editing domain
     * @return the list of all objects that are related to the original object or a member of the list of objects.
     */
    public List findRelatedObjects( final Collection objects,
                                    final EditingDomain domain ) {
        if (domain instanceof ContainerEditingDomain) {

            // ------------------------------------------------------
            // Now, see if there are additional things to be done ...
            // ------------------------------------------------------

            // Iterate through the original objects, and find any additional objects that should be copied ...
            final FindRelatedObjectsToBeCopied visitor = new FindRelatedObjectsToBeCopied();
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
            try {
                processor.walk(objects, ModelVisitorProcessor.DEPTH_INFINITE);
            } catch (ModelerCoreException e) {
                ModelerCore.Util.log(e);
            }

            // See if there are any additional objects to be copied ...
            return new ArrayList(visitor.getAdditionalObjects());
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Find external references to the deleted objects. The method will process the original model and all imported models
     * checking for references to either the original object being deleted or one of the members contained in the list of objects
     * being deleted.
     * 
     * @param eObject the original object that is being deleted; never null
     * @param allDeleted the Collection of all related objects that are being deleted
     * @return the Collection of all objects that reference the original object or a member of the list of deleted objects.
     */
    public Collection findExternalReferencesToObjectsBeingDeleted( final EObject eObject,
                                                                   final Collection allDeleted ) {
        CoreArgCheck.isNotNull(eObject);

        final Resource resource = eObject.eResource();
        if (resource == null) {
            return Collections.EMPTY_LIST;
        }

        // Create a list of all deleted objects, including the original object
        final Collection allDeletedObjects = (allDeleted == null ? new HashSet() : new HashSet(allDeleted));
        if (!allDeletedObjects.contains(eObject)) {
            allDeletedObjects.add(eObject);
        }

        // Find all the external references ...
        final FindReferencesToDeletedObjects visitor = new FindReferencesToDeletedObjects(allDeleted);
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            processor.walk(resource, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (Throwable t) {
            ModelerCore.Util.log(IStatus.ERROR, t, t.getMessage());
        }

        return visitor.getReferencesToDeletedObjects();
    }

    /**
     * Find related objects to be deleted. This method only processes the same model in which the deleted object exists.
     * 
     * @param eObject the original object that is being deleted; never null
     * @return the Collection of all objects that are being deleted
     */
    protected Collection findOtherObjectsToBeDeleted( final EObject eObject,
                                                      ModelWorkspaceSearch workspaceSearch ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);

        final ContainerImpl cntr = getContainer();
        final EditingDomain ed = cntr.getEditingDomain();
        final LinkedList additionalCommands = new LinkedList();
        return findOtherObjectsToBeDeleted(Collections.singleton(eObject), ed, additionalCommands, workspaceSearch);
    }

    /**
     * Find related objects to be deleted. This method only processes the same model in which the deleted object exists.
     * 
     * @param eObject the original object that is being deleted; never null
     * @return the Collection of all objects that are being deleted
     */
    public Collection findOtherObjectsToBeDeleted( final EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        final ContainerImpl cntr = getContainer();
        final EditingDomain ed = cntr.getEditingDomain();
        final LinkedList additionalCommands = new LinkedList();
        final boolean ignoreXsdResources = !includesXsdObject(eObject);
        // Defect 22774 - Changed following call to new up a DeleteRelatedWorkspaceSearch to prevent searching xsd resources
        // if no deleted objects are xsd objects. Improves performance.
        ModelWorkspaceSearch search = new DeleteRelatedWorkspaceSearch(ignoreXsdResources);
        return findOtherObjectsToBeDeleted(Collections.singleton(eObject), ed, additionalCommands, search);
    }

    /**
     * Find related objects to be deleted. This method processes all models in which the object being deleted is referenced.
     * 
     * @param eObject the original object that is being deleted; never null
     * @param modelContents the {@link ModelContents} for the {@link Resource} that contains the EObject; never null
     * @param editingDomain the editing domain; never null
     * @param additionalCommands the list into which any additional delete commands should be placed; never null
     * @return the collection of commands for all objects that are being deleted
     */
    public Collection findOtherObjectsToBeDeleted( final Collection eObjects,
                                                   final EditingDomain editingDomain,
                                                   final List additionalCommands,
                                                   final ModelWorkspaceSearch workspaceSearch ) throws ModelerCoreException {
        return findOtherObjectsToBeDeleted(eObjects,
                                           editingDomain,
                                           additionalCommands,
                                           workspaceSearch,
                                           new NullProgressMonitor());
    }

    /**
     * Find related objects to be deleted. This method processes all models in which the object being deleted is referenced.
     * 
     * @param eObject the original object that is being deleted; never null
     * @param modelContents the {@link ModelContents} for the {@link Resource} that contains the EObject; never null
     * @param editingDomain the editing domain; never null
     * @param additionalCommands the list into which any additional delete commands should be placed; never null
     * @return the collection of commands for all objects that are being deleted
     */
    public Collection findOtherObjectsToBeDeleted( final Collection eObjects,
                                                   final EditingDomain editingDomain,
                                                   final List additionalCommands,
                                                   final ModelWorkspaceSearch workspaceSearch,
                                                   final IProgressMonitor monitor ) throws ModelerCoreException {
        // collection to return:
        Collection rv = new HashSet();

        int iObj = 1;
        int nObj = eObjects.size();
        int halfWay = nObj / 2;
        boolean pastHalfWay = false;

        String startMessage = ModelerCore.Util.getString("ModelEditorImpl.searchingRelatedMsg") + CoreStringUtil.Constants.SPACE; //$NON-NLS-1$
        String ofText = CoreStringUtil.Constants.DBL_SPACE
                        + ModelerCore.Util.getString("ModelEditorImpl.ofText") + CoreStringUtil.Constants.SPACE; //$NON-NLS-1$
        Iterator itor = eObjects.iterator();

        while (itor.hasNext()) {
            String message = startMessage + CoreStringUtil.Constants.DBL_SPACE + iObj + ofText + nObj;
            iObj++;
            monitor.subTask(message);
            EObject eObject = (EObject)itor.next();
            final Resource resource = eObject.eResource();
            if (resource == null) {
                continue;
            }

            // linked list of that would be updated by the visitor
            // with the eObject to be deleted
            final LinkedList objectsToDelete = new LinkedList();
            // work on delegate not all eObjects have proxies, collections
            // should have consistent versions for lookup
            objectsToDelete.addFirst(eObject);

            // Construct and use the visitor ...
            final FindRelatedObjectsToDeleted visitor = new FindRelatedObjectsToDeleted(eObject, editingDomain, objectsToDelete,
                                                                                        getRemovedObjectsForCurrentTxn(),
                                                                                        workspaceSearch);
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
            // continue processing the list till there are
            // no objects to be deleted
            while (!objectsToDelete.isEmpty() && !monitor.isCanceled()) {
                processor.walk((EObject)objectsToDelete.getFirst(), ModelVisitorProcessor.DEPTH_INFINITE);
            }

            if (!monitor.isCanceled()) {
                // 12/17/03 (LLP) : changed to add new commands to additionalCommands list.
                if (visitor.getAdditionalDeleteCommands() != null && !visitor.getAdditionalDeleteCommands().isEmpty()) {
                    additionalCommands.addAll(visitor.getAdditionalDeleteCommands());
                }

                rv.addAll(visitor.getAllDeletedObjects());
            }
            if (!pastHalfWay && iObj > halfWay) {
                monitor.worked(10);
                pastHalfWay = true;
            }
        } // endwhile

        monitor.worked(10);

        // System.out.println(" ModelEditorImpl.findOtherObjectsToBeDeleted() Found [" + deadEndObjs.size() + "] Dead End
        // Objects");
        if (monitor.isCanceled()) {
            rv.clear();
        }
        return rv;
    }

    private Collection getRemovedObjectsForCurrentTxn() {
        try {
            final ContainerImpl container = getContainer();
            final UnitOfWorkImpl uow = (UnitOfWorkImpl)container.getEmfTransactionProvider().getCurrent();
            return uow.getRemovedEObjects();
        } catch (Exception err) {
            return new HashSet();
        }

    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#rename(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    public boolean rename( final EObject eObject,
                           final String newName ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        final EStructuralFeature nameFeature = getNameFeature(eObject);
        if (nameFeature == null) {
            return false;
        }

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Rename_{0}_6", getPresentationValue(eObject)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                Command command = SetCommand.create(ed, eObject, nameFeature, newName);
                executeCommandInTransaction(uow, eObject, command);
                return null;
            }
        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return true;
    }

    /**
     * This method currently looks for a feature with a name that case-insensitively matches "name".
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#getNameFeature(org.eclipse.emf.ecore.EObject)
     */
    public EStructuralFeature getNameFeature( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        final EClass eClass = eObject.eClass();
        for (Iterator iter = eClass.getEAllStructuralFeatures().iterator(); iter.hasNext();) {
            final EStructuralFeature feature = (EStructuralFeature)iter.next();
            if (NAME_FEATURE_NAME.equalsIgnoreCase(feature.getName())) {
                return feature;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        final EStructuralFeature nameFeature = getNameFeature(eObject);
        if (nameFeature == null) {
            return null;
        }
        final Object value = eObject.eGet(nameFeature);
        return value != null ? value.toString() : null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#hasName(org.eclipse.emf.ecore.EObject)
     */
    public boolean hasName( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        final EStructuralFeature nameFeature = getNameFeature(eObject);
        return nameFeature != null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#rename(org.eclipse.emf.ecore.resource.Resource, org.eclipse.emf.ecore.EObject,
     *      java.lang.String)
     */
    protected boolean renameInternal( final EObject eObject,
                                      final String newName ) {
        CoreArgCheck.isNotNull(eObject);
        final EStructuralFeature nameFeature = getNameFeature(eObject);
        if (nameFeature != null) {
            generateUniqueInternalName(eObject.eContainer() == null ? eObject.eResource().getContents() : eObject.eContainer().eContents(),
                                       eObject,
                                       nameFeature,
                                       newName);
            return true;
        }
        return false;
    }

    private void generateUniqueInternalName( final EList siblings,
                                             final EObject eObject,
                                             final EStructuralFeature nameFeature,
                                             final String name ) {
        String newName = name;
        if (siblings != null && eObject.eGet(nameFeature) == null) {
            final Set siblingNames = new HashSet();
            for (Iterator it = siblings.iterator(); it.hasNext();) {
                final EObject child = (EObject)it.next();
                if (eObject.getClass().equals(child.getClass())) {
                    siblingNames.add(child.eGet(nameFeature));
                }
            }
            boolean foundUniqueName = false;
            int index = 1;
            while (!foundUniqueName) {
                if (siblingNames.contains(newName)) {
                    newName = name + String.valueOf(index++);
                } else {
                    foundUniqueName = true;
                }
            }
        }
        eObject.eSet(nameFeature, newName);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#executeCommand(org.eclipse.emf.common.command.Command)
     */
    public void executeCommand( final EObject owner,
                                final Command cmd ) throws ModelerCoreException {
        final boolean isSignificant = true;
        final String operationDescription = null;
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                executeCommandInTransaction(uow, owner, cmd);
                return null;
            }
        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
    }

    /**
     * Should only be called by the model container's EMF content adapter.
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#executeCommand(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.common.command.Command)
     * @since 5.0.3
     */
    public void postExecuteCommand( final EObject owner,
                                    final Command cmd ) throws ModelerCoreException {
        executeAsTransaction(new TransactionRunnable() {
            public Object run( UnitOfWork uow ) throws ModelerCoreException {
                ((UnitOfWorkImpl)uow).setAlreadyExecuted(true);
                executeCommandInTransaction(uow, owner, cmd);
                return null;
            }
        }, getContainer(), null, true, this);
    }

    /**
     * Executes a command in the context of an existing transaction ...
     * 
     * @param owner
     * @param cmd
     * @param operationDescription
     * @throws ModelerCoreException
     */
    protected void executeCommandInTransaction( final UnitOfWork uow,
                                                final Object owner,
                                                final Command cmd ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(uow);

        if (cmd == null || !cmd.canExecute()) {
            final String nullMsg = ModelerCore.Util.getString("ModelEditorImpl.Can_not_execute_a_null_command_1"); //$NON-NLS-1$
            final String reason = cmd == null ? nullMsg : getCmdFailureReason(cmd);
            throw new ModelerCoreException(ModelerCore.Util.getString("ModelEditorImpl.Unable_to_execute_Command_1", reason)); //$NON-NLS-1$
        }

        // Execute the command ...
        uow.executeCommand(cmd);
    }

    protected String getCmdFailureReason( final Command cmd ) {
        final StringBuffer failures = new StringBuffer();
        failures.append(cmd.toString());
        try {
            if (cmd instanceof CompoundCommand && !cmd.canExecute()) {
                failures.append(ModelerCore.Util.getString("ModelEditorImpl.Compound_Command_with_unexecutable_children_1")); //$NON-NLS-1$
            } else if (cmd instanceof AddCommand && !cmd.canExecute()) {
                final AddCommand add = (AddCommand)cmd;
                if (add.getOwnerList() == null || add.getCollection() == null || add.getCollection().size() == 0
                    || add.getIndex() != CommandParameter.NO_INDEX
                    && (add.getIndex() < 0 || add.getIndex() > add.getOwnerList().size())) {
                    // Verify index is valid
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl.Invalid_index_for_Add_operation_2")); //$NON-NLS-1$
                    return failures.toString();
                }
                // Check that each object conforms to the requirements of the owner list.
                //
                for (Iterator objects = add.getCollection().iterator(); objects.hasNext();) {
                    Object object = objects.next();

                    if (add.getFeature() != null && !add.getFeature().getEType().isInstance(object)) {
                        failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_add_item_of_type_{0}_to_feature_{1}_3", object.getClass().getName(), add.getFeature().getName())); //$NON-NLS-1$
                    }
                }

                // Check to see if a container is being put into a contained object.
                //
                if (add.getFeature() instanceof EReference && ((EReference)add.getFeature()).isContainment()) {
                    for (EObject container = add.getOwner(); container != null; container = container.eContainer()) {
                        if (add.getCollection().contains(container)) {
                            failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_add_object_to_a_contained_object_4")); //$NON-NLS-1$
                            return failures.toString();
                        }
                    }
                }
            } else if (cmd instanceof CopyToClipboardCommand && !cmd.canExecute()) {
                if (((CopyToClipboardCommand)cmd).getSourceObjects() == null) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nSource_Objects_collection_may_not_be_null_for_Copy_operation_5")); //$NON-NLS-1$
                } else if (((CopyToClipboardCommand)cmd).getSourceObjects().isEmpty()) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nSource_Objects_collection_may_not_be_empty_for_Copy_operation_6")); //$NON-NLS-1$
                } else {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nUnable_to_execute_copy_to_clipboard_command_7")); //$NON-NLS-1$
                }
            } else if (cmd instanceof CommandWrapper && !cmd.canExecute()) {
                final Command tmp = ((CommandWrapper)cmd).getCommand();
                if (tmp != null && tmp != UnexecutableCommand.INSTANCE) {
                    return getCmdFailureReason(tmp);
                }
                failures.append(ModelerCore.Util.getString("ModelEditorImpl._nWrapped_command_may_not_be_null_or_Unexecutable_8")); //$NON-NLS-1$
            } else if (cmd instanceof CreateCopyCommand && !cmd.canExecute()) {
                if (((CreateCopyCommand)cmd).getOwner() == null) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_create_copy_of_NULL_object_9")); //$NON-NLS-1$
                } else {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nUnexecutable_Create_Copy_command_10")); //$NON-NLS-1$
                }
            } else if (cmd instanceof MoveCommand && !cmd.canExecute()) {
                final MoveCommand tmp = (MoveCommand)cmd;
                if (tmp.getOwnerList() == null) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nOwner_list_may_not_be_null_for_a_Move_Command_11")); //$NON-NLS-1$
                } else if (!tmp.getOwnerList().contains(tmp.getValue())) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nOwner_list_must_contain_value_for_a_Move_Command_12")); //$NON-NLS-1$
                } else if (tmp.getIndex() < tmp.getOwnerList().size()) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nInvalid_index_for_Move_Command_13")); //$NON-NLS-1$
                } else {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nUnexpected_failure_for_Move_Command_14")); //$NON-NLS-1$
                }
            } else if (cmd instanceof PasteFromClipboardCommand && !cmd.canExecute()) {
                final PasteFromClipboardCommand tmp = (PasteFromClipboardCommand)cmd;
                final Collection copies = tmp.getChildrenToCopy();
                if (copies == null || copies.isEmpty()) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nNo_objects_found_to_paste_15")); //$NON-NLS-1$
                    return failures.toString();
                } else if (tmp.getFeature() != null && tmp.getFeature() instanceof EStructuralFeature) {
                    final EStructuralFeature feature = (EStructuralFeature)tmp.getFeature();
                    // Check that each object conforms to the requirements of the owner list.
                    //
                    for (Iterator objects = copies.iterator(); objects.hasNext();) {
                        Object object = objects.next();

                        if (!feature.getEType().isInstance(object)) {
                            failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_paste_item_of_type_{0}_to_feature_{1}_16", object.getClass().getName(), feature.getName())); //$NON-NLS-1$
                        }
                    }
                }

                // Check to see if a container is being put into a contained object.
                //
                if (tmp.getOwner() instanceof EObject && tmp.getFeature() instanceof EReference
                    && ((EReference)tmp.getFeature()).isContainment()) {
                    for (EObject container = (EObject)tmp.getOwner(); container != null; container = container.eContainer()) {
                        if (copies.contains(container)) {
                            failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_add_object_to_a_contained_object_17")); //$NON-NLS-1$
                            return failures.toString();
                        }
                    }
                }

                if (failures.length() == 0) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nUnexecutable_Paste_From_Clipboard_command_18")); //$NON-NLS-1$
                }
            } else if (cmd instanceof RemoveCommand && !cmd.canExecute()) {
                RemoveCommand removeCmd = (RemoveCommand)cmd;
                // Owner list must not be null and must contain all values in collection
                if (removeCmd.getOwnerList() == null) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nOwner_list_may_not_be_null_19")); //$NON-NLS-1$
                } else if (!removeCmd.getOwnerList().containsAll(removeCmd.getCollection())) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nOwner_list_must_contain_all_values_to_be_removed_20")); //$NON-NLS-1$
                }

                if (failures.length() == 0) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nUnexecutable_Remove_command_21")); //$NON-NLS-1$
                }
            } else if (cmd instanceof SetCommand && !cmd.canExecute()) {
                final SetCommand tmp = (SetCommand)cmd;
                final EObject owner = tmp.getOwner();
                final EStructuralFeature feature = tmp.getFeature();
                final Object value = tmp.getValue();

                if (feature == null) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_set_value_on_null_feature_22")); //$NON-NLS-1$
                    return failures.toString();
                }

                if (owner == null) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_set_value_on_null_owner_23")); //$NON-NLS-1$
                    return failures.toString();
                }
                // Get the owner's meta object.
                //
                EClass eMetaObject = owner.eClass();

                // Is the feature an attribute of the owner...
                //
                if (eMetaObject.getEAllAttributes().contains(feature)) {
                    // If must be of this type then.
                    //
                    EAttribute eAttribute = (EAttribute)feature;
                    EClassifier eType = eAttribute.getEType();

                    if (eAttribute.isMany()) {
                        if (value instanceof EList) {
                            EList list = (EList)value;
                            for (Iterator objects = list.iterator(); objects.hasNext();) {
                                Object next = objects.next();
                                if (!eType.isInstance(next)) {
                                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nCan_not_add_object_of_type_{0}_to_feature_{1}_24", next.getClass().getName(), tmp.getFeature().getName())); //$NON-NLS-1$
                                }
                            }
                        }
                    }// end isMany
                }
                // Is the feature a reference of the owner...
                //
                else if (eMetaObject.getEAllReferences().contains(feature)) {
                    // It must be of this type.
                    //
                    EReference eReference = (EReference)feature;

                    // Make sure it's a single-valued relation (multi-valued is not supported).
                    //
                    if (!eReference.isMany()) {
                        // We want to make sure the object is of a type compatible with the type of the reference.
                        //
                        if (value == null || eReference.getEType().isInstance(value)) {
                            // Check to see if the container is being put into a contained object
                            //
                            if (eReference.isContainment()) {
                                for (EObject container = owner; container != null; container = container.eContainer()) {
                                    if (value == container) {
                                        failures.append(ModelerCore.Util.getString("ModelEditorImpl._nMay_not_add_value_to_contained_object_25")); //$NON-NLS-1$
                                        break;
                                    }
                                }
                            }// end isContainment
                        }
                    }// end if isMany
                }

                if (failures.length() == 0) {
                    failures.append(ModelerCore.Util.getString("ModelEditorImpl._nUnexpected_failure_executing_Set_command_26")); //$NON-NLS-1$
                }
            }
        } catch (Exception e) {
            // Do nothing... return default message
        }

        if (failures.length() == 0) {
            failures.append(ModelerCore.Util.getString("ModelEditorImpl._nUnexpected_failure_executing_command___Command_not_executable_27")); //$NON-NLS-1$
        }

        return failures.toString();
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#move(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public boolean move( final Object newParent,
                         final EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(newParent);
        CoreArgCheck.isNotNull(eObject);

        if (!isValidParent(newParent, eObject)) {
            throw new ModelerCoreException(ModelerCore.Util.getString("ModelEditorImpl.Invalid_parent_for_child_encountered_1")); //$NON-NLS-1$
        }

        int index = 0;
        if (newParent instanceof EObject) {
            final EObject eParent = (EObject)newParent;
            EReference ref = getChildReference(eParent, eObject);
            if (ref != null && ref.isMany()) {
                if (eParent.eGet(ref) instanceof EList) {
                    index = ((EList)eParent.eGet(ref)).size();
                }
            }
        } else if (newParent instanceof Resource) {
            index = ((Resource)newParent).getContents().size();
        }

        return move(newParent, eObject, index);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#newParent
     */
    public boolean move( final Object newParent,
                         final EObject eObject,
                         final int index ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(newParent);
        CoreArgCheck.isNotNull(eObject);

        if (!isValidParent(newParent, eObject)) {
            throw new ModelerCoreException(ModelerCore.Util.getString("ModelEditorImpl.Invalid_parent_for_child_encountered_1")); //$NON-NLS-1$
        }

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Move_{0}_7", getPresentationValue(eObject)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                Command command = null;
                if (eObject.eContainer() != null && eObject.eContainer() == newParent) {
                    command = MoveCommand.create(ed, newParent, null, eObject, index);
                } else {
                    if (eObject.eContainer() != null) {
                        command = RemoveCommand.create(ed, eObject.eContainer(), eObject.eContainmentFeature(), eObject);
                        executeCommandInTransaction(uow, eObject, command);
                    }

                    command = AddCommand.create(ed, newParent, null, eObject, index);
                }

                executeCommandInTransaction(uow, newParent, command);
                return null;
            }
        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#copyToClipboard
     */
    public void copyToClipboard( final EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        Collection objects = new ArrayList(1);
        objects.add(eObject);

        copyAllToClipboard(objects);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#copyAllToClipboard
     */
    public void copyAllToClipboard( final Collection eObjects ) throws ModelerCoreException {
        CoreArgCheck.isNotEmpty(eObjects);

        final EObject eObject = (EObject)eObjects.iterator().next();
        final boolean isSignificant = false;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Copy_{0}_8", getPresentationValue(eObjects)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                final Command command = CopyToClipboardCommand.create(ed, eObjects);
                executeCommandInTransaction(uow, eObject, command);
                return null;
            }
        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#cutToClipboard
     */
    public void cutToClipboard( final EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        Collection objects = new ArrayList(1);
        objects.add(eObject);

        cutAllToClipboard(objects);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#cutAllToClipboard
     */
    public void cutAllToClipboard( final Collection eObjects ) throws ModelerCoreException {
        CoreArgCheck.isNotEmpty(eObjects);

        final EObject eObject = (EObject)eObjects.iterator().next();
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Cut_{0}_9", getPresentationValue(eObjects)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                final CompoundCommand command = CompoundCommandFactory.create(eObjects, new ArrayList(1));
                command.append(CutToClipboardCommand.create(ed, eObjects));
                executeCommandInTransaction(uow, eObject, command);
                return null;
            }
        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#pasteFromClipboard
     */
    public boolean pasteFromClipboard( final Object owner ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(owner);

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Paste_{0}_10", getPresentationValue(owner)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                final Object realOwner = owner instanceof ModelResource ? ((ModelResource)owner).getEmfResource() : owner;
                final Command command = PasteFromClipboardCommand.create(ed, realOwner, null);
                executeCommandInTransaction(uow, realOwner, command);
                return null;
            }
        };
        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getClipboardContents
     */
    public Collection getClipboardContents( final Object target ) {
        try {
            final ContainerImpl cntr = getContainer();
            final EditingDomain ed = cntr.getEditingDomain();
            final Collection content = ed.getClipboard();
            // have to check - getClipboard can return null
            if (content != null) {
                return Collections.unmodifiableCollection(ed.getClipboard());
            }
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getClipboardContentsOriginalToCopyMapping(java.lang.Object)
     */
    public Map getClipboardContentsOriginalToCopyMapping( Object target ) {
        try {
            final ContainerImpl cntr = getContainer();
            final EditingDomain ed = cntr.getEditingDomain();
            if (ed instanceof ContainerEditingDomain) {
                final ContainerEditingDomain ced = (ContainerEditingDomain)ed;
                return ced.getClipboardContentsOriginalToCopyMapping();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getClipboardContentsCopyToOriginalMapping(java.lang.Object)
     */
    public Map getClipboardContentsCopyToOriginalMapping( Object target ) {
        final Map originalToCopy = getClipboardContentsOriginalToCopyMapping(target);
        if (originalToCopy != null) {
            final Map copyToOriginal = new HashMap();
            final Iterator iter = originalToCopy.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry entry = (Map.Entry)iter.next();
                final Object copy = entry.getValue();
                final Object orig = entry.getKey();
                copyToOriginal.put(copy, orig);
            }
            return copyToOriginal;
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#copyAll(java.util.Collection)
     */
    public Collection copyAll( final Collection eObjects ) throws ModelerCoreException {
        if (eObjects == null || eObjects.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return copyAll(eObjects, null);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#copyAll(java.util.Collection)
     */
    public Collection copyAll( final Collection eObjects,
                               final Map originalsToCopies ) throws ModelerCoreException {
        if (eObjects == null || eObjects.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        final EObject eObject = (EObject)eObjects.iterator().next();
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Copy_{0}_11", getPresentationValue(eObjects)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                final CopyCommand.Helper helper = new CopyCommand.Helper();
                if (originalsToCopies != null && !originalsToCopies.isEmpty()) {
                    // copy all of the existing entries in the 'originalsToCopies' map into the helper ...
                    // This will change the copies to reference other copies that may have been created
                    // already and are in the map, but are not directly in the graph of contained objects
                    // below the 'eObjects' originals
                    for (final Iterator iter = originalsToCopies.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entry = (Map.Entry)iter.next();
                        // putAll does not initialize the collection on the helper
                        helper.put((EObject)entry.getKey(), (EObject)entry.getValue());
                    }
                    helper.putAll(originalsToCopies);
                }
                final Command command = createCopyCommand(ed, eObjects, helper);
                executeCommandInTransaction(uow, eObject, command);

                // Set the uuid on all applicable copies
                for (final Iterator copies = command.getResult().iterator(); copies.hasNext();) {
                    Object next = copies.next();
                    if (next instanceof EObject) {
                        setUuidFeatureValue((EObject)next);
                    }
                }

                // Populate the map from orig to copies
                if (originalsToCopies != null) originalsToCopies.putAll(helper);

                return command.getResult();
            }
        };
        final Collection result = (Collection)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return result;
    }

    /**
     * Tries to add the given value to the given EObject's feature
     * 
     * @param eObject to add new value : may not be null
     * @param value to add - May be a list or single item, but may not be null
     * @param feature EList from eObject to add value, may not be null
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void addValue( final Object owner,
                          final Object value,
                          final EList feature ) throws ModelerCoreException {
        addValue(owner, value, feature, CommandParameter.NO_INDEX);
    }

    /**
     * Tries to add the given value to the given EObject's feature
     * 
     * @param eObject to add new value : may not be null
     * @param value to add - May be a list or single item, but may not be null
     * @param feature EList from eObject to add value, may not be null
     * @param index position to insert the element into the collection standard Eclipse AddCommand
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void addValue( final Object owner,
                          final Object value,
                          final EList feature,
                          final int index ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(owner);
        CoreArgCheck.isNotNull(value);
        CoreArgCheck.isNotNull(feature);

        // convert the value into a collection
        final Collection values = new ArrayList();
        if (value instanceof Collection) {
            values.addAll((Collection)value);
        } else {
            values.add(value);
        }

        final ContainerImpl cntr = getContainer();
        if (cntr == null) {
            feature.add(index, values);
        } else {
            final boolean isSignificant = true;
            final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Add_values_to_{0}_1", getPresentationValue(owner)); //$NON-NLS-1$
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                    final EditingDomain ed = cntr.getEditingDomain();
                    Command command = AddCommandFactory.create(owner, ed, feature, values, index);
                    executeCommandInTransaction(uow, owner, command);
                    return null;
                }
            };
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        }
    }

    /**
     * Tries to add the given value to the given map with the given key
     * 
     * @param eObject : may not be null
     * @param map to add key/value pair - may not be null
     * @param key to add - May be null (depends on map implementation)
     * @param value to add - May be null (depends on map implementation)
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void addMapValue( final Object owner,
                             final Map map,
                             final Object key,
                             final Object value ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(owner);
        CoreArgCheck.isNotNull(map);

        final ContainerImpl cntr = getContainer();
        if (cntr == null) {
            map.put(key, value);
        } else {
            // setting isSignificant to FALSE since we do not want to see this low-level operation in the undo stack
            final boolean isSignificant = false;
            final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Add_value_to_map_for_{0}", getPresentationValue(owner)); //$NON-NLS-1$
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                    final EditingDomain ed = cntr.getEditingDomain();
                    final AddValueToMapCommand command = new AddValueToMapCommand(ed, owner, map, key, value);
                    executeCommandInTransaction(uow, owner, command);
                    return null;
                }
            };
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        }
    }

    /**
     * Tries to remove the given value from the given EObject's feature
     * 
     * @param eObject to remove new value from : may not be null
     * @param value to remove - May be a list or single item, but may not be null
     * @param feature EList from eObject to remove value, may not be null
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void removeValue( final Object owner,
                             final Object value,
                             final EList feature ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(owner);
        CoreArgCheck.isNotNull(value);
        CoreArgCheck.isNotNull(feature);

        // convert the value into a collection
        final Collection values = new ArrayList();
        if (value instanceof Collection) {
            values.addAll((Collection)value);
        } else {
            values.add(value);
        }

        final ContainerImpl cntr = getContainer();
        if (cntr == null) {
            feature.remove(values);
        } else {
            final boolean isSignificant = true;
            final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Remove_values_from_{0}_1", getPresentationValue(owner)); //$NON-NLS-1$
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                    final EditingDomain ed = cntr.getEditingDomain();
                    final RemoveCommand command = new RemoveCommand(ed, feature, values);
                    executeCommandInTransaction(uow, owner, command);
                    return null;
                }
            };
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        }
    }

    /**
     * Tries to remove the value associated with the given key from the given map
     * 
     * @param eObject : may not be null
     * @param map to remove key/value pair - may not be null
     * @param key to add - May be null (depends on map implementation)
     * @param isSignificant - whether this action will be seen in the edit menu as undoable
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void removeMapValue( final Object owner,
                                final Map map,
                                final Object key ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(owner);
        CoreArgCheck.isNotNull(map);

        final ContainerImpl cntr = getContainer();
        if (cntr == null) {
            map.remove(key);
        } else {
            // setting isSignificant to FALSE since we do not want to see this low-level operation in the undo stack
            final boolean isSignificant = false;
            final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Remove_value_from_map_for_{0}", getPresentationValue(owner)); //$NON-NLS-1$
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                    final EditingDomain ed = cntr.getEditingDomain();
                    final RemoveValueFromMapCommand command = new RemoveValueFromMapCommand(ed, owner, map, key);
                    executeCommandInTransaction(uow, owner, command);
                    return null;
                }
            };
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        }
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#setPropertyValue(org.eclipse.emf.ecore.EObject, java.lang.Object,
     *      org.eclipse.emf.edit.provider.ItemPropertyDescriptor)
     */
    public boolean setPropertyValue( final EObject eObject,
                                     final Object value,
                                     final ItemPropertyDescriptor descriptor ) {
        if (eObject == null || descriptor == null) {
            return false;
        }

        // Set the property value via the EMF Command framework which allows
        // the operation to be undone.
        return this.setPropertyValue(eObject, value, descriptor.getFeature(eObject));
    }

    /**
     * Tries to set the given values on a simple datatype's enterprise extensions
     * 
     * @param owner - the owning EObject for the dom node.
     * @param value - Should be an instanceof EnterpriseDatatypeInfo
     * @return true if successful
     */
    public boolean setEnterpriseDatatypePropertyValue( final EObject owner,
                                                       final Object object ) {
        if (owner == null || object == null || !(owner instanceof XSDSimpleTypeDefinition)
            || !(object instanceof EnterpriseDatatypeInfo)) {
            return false;
        }

        final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)owner;
        final EnterpriseDatatypeInfo newEdtInfo = (EnterpriseDatatypeInfo)object;
        final EnterpriseDatatypeInfo oldEdtInfo = ModelerCore.getDatatypeManager(simpleType).getEnterpriseDatatypeInfo(simpleType);
        if (newEdtInfo.equals(oldEdtInfo)) {
            return false;
        }
        fillWithDefaultValues(newEdtInfo, simpleType);

        final ContainerImpl cntr = getContainer();
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Set_{0}_1", object.toString()); //$NON-NLS-1$
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                Command cmd = new EnterpriseDatatypeInfoSetCommand(ed, simpleType, newEdtInfo, oldEdtInfo);
                executeCommandInTransaction(uow, object, cmd);
                return null;
            }
        };
        try {
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Fix me: I'm in the wrong spot
     */
    public static void fillWithDefaultValues( final EnterpriseDatatypeInfo newEdtInfo,
                                              final XSDSimpleTypeDefinition simpleType ) {
        if (newEdtInfo.getUuid() == null || newEdtInfo.getUuid().length() == 0) {
            String objectID = ModelerCore.getObjectIdString(simpleType);
            if (objectID != null) {
                newEdtInfo.setUuid(objectID);
            }
        }
        if (newEdtInfo.getRuntimeType() == null || newEdtInfo.getRuntimeType().length() == 0) {
            newEdtInfo.setRuntimeType(EnterpriseDatatypeInfo.DEFAULT_RUNTIME_TYPE_VALUE);
        }
        if (newEdtInfo.getRuntimeTypeFixed() == null) {
            newEdtInfo.setRuntimeTypeFixed(EnterpriseDatatypeInfo.DEFAULT_RUNTIME_TYPE_FIXED_VALUE);
        }
    }

    /**
     * Tries to clear out the given simple datatype's enterprise extensions
     * 
     * @param owner - the owning EObject for the dom node.
     * @return true if successful
     */
    public boolean unsetEnterpriseDatatypePropertyValue( final EObject owner ) {
        if (owner == null || !(owner instanceof XSDSimpleTypeDefinition)) {
            return false;
        }

        final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)owner;
        final EnterpriseDatatypeInfo oldEdtInfo = ModelerCore.getDatatypeManager(simpleType).getEnterpriseDatatypeInfo(simpleType);

        final ContainerImpl cntr = getContainer();
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Set_{0}_1", owner.toString()); //$NON-NLS-1$
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                Command cmd = new EnterpriseDatatypeInfoUnsetCommand(ed, simpleType, oldEdtInfo);
                executeCommandInTransaction(uow, owner, cmd);
                return null;
            }
        };
        try {
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Tries to set the given value on the given eObjects sf that corresponds to the propertyID
     * 
     * @param eObject
     * @param value
     * @param propertyID String name of the sf
     * @return true if successful
     */
    public boolean setPropertyValue( final EObject eObject,
                                     final Object value,
                                     final Object feature ) {
        if (eObject == null || feature == null || !(feature instanceof EStructuralFeature)) {
            return false;
        }

        final EStructuralFeature sf = (EStructuralFeature)feature;

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Set_{0}_1", sf.getName()); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                if (sf.isMany()) {
                    // Compute which values in the newValue are added and which values in the
                    // existing value need to be removed.
                    final EList currentValues = (EList)eObject.eGet(sf);
                    final Collection addedValues = new ArrayList(currentValues.size());
                    final Collection removedValues = new ArrayList(currentValues.size());
                    Collection newValues = new ArrayList();
                    if (value instanceof Collection) {
                        newValues = (Collection)value;
                    } else {
                        newValues.add(value);
                    }

                    removedValues.addAll(currentValues);
                    addedValues.addAll(newValues);

                    // Clear all old values
                    if (!currentValues.isEmpty()) {
                        removeValue(eObject, removedValues, currentValues);
                    }

                    // Adds all new values
                    if (!addedValues.isEmpty()) {
                        addValue(eObject, addedValues, currentValues);
                    }

                } else {
                    Object newValue = value;

                    // the value could be of the wrong type. if value is of type String
                    // try and convert it to the proper type before setting the feature.
                    if ((value != null) && (value instanceof String)) {
                        final EDataType dt = (EDataType)sf.getEType();
                        final EPackage ePackage = dt.getEPackage();
                        final EFactory fac = ePackage.getEFactoryInstance();
                        newValue = fac.createFromString(dt, (String)value);
                    }

                    // If owner EObject is an XSDParticle but the EStructuralFeature being modified
                    // belongs to the content of that XSDParticle and not the XSDParticle itself
                    // then reset the owner reference to the content EObject (see defect 21088)
                    EObject owner = eObject;
                    if (owner instanceof XSDParticle && !owner.eClass().getEAllStructuralFeatures().contains(sf)) {
                        XSDParticleContent particleContent = ((XSDParticle)owner).getContent();
                        if (particleContent != null && particleContent.eClass().getEAllStructuralFeatures().contains(sf)) {
                            owner = particleContent;
                        }
                    }

                    final SetCommand command = new SetCommand(ed, owner, sf, newValue);
                    executeCommandInTransaction(uow, owner, command);
                }
                return null;
            }
        };
        try {
            executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#clone(org.eclipse.emf.ecore.EObject)
     */
    public EObject clone( final EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Clone_{0}_12", getPresentationValue(eObject)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                final CloneCommand command = new CloneCommand(ed, eObject);
                executeCommandInTransaction(uow, eObject, command);
                command.getAffectedObjects();

                // Look up the result ...
                return command.getHelper().getCopy(eObject);
            }
        };
        final EObject result = (EObject)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return result;
    }

    /*
     * 
     */
    public void cloneProject( final String originalProjectPath,
                              final String clonedProjectPath ) throws IOException {
        cloneProject(originalProjectPath, clonedProjectPath, null);
    }

    void cloneProject( String originalProjectPath,
                       String clonedProjectPath,
                       Tester tester ) throws IOException {
        Map model2RefMap = new HashMap();
        Map genRefMap = new HashMap();
        File originalProject = new File(originalProjectPath);
        cloneFolder(originalProject, originalProject, model2RefMap, genRefMap, tester);
        cloneFolder2(originalProject, originalProject, model2RefMap, genRefMap, new File(clonedProjectPath), tester);
    }

    private void cloneFolder( File originalProject,
                              File originalFolder,
                              Map modelToReferenceMap,
                              Map genRefMap,
                              Tester tester ) throws IOException {
        File[] origFiles = originalFolder.listFiles();
        if (origFiles != null) {
            for (int ndx = origFiles.length; --ndx >= 0;) {
                File origFile = origFiles[ndx];
                String name = origFile.getName();
                if (origFile.isDirectory()) {
                    if (name.charAt(0) != '.') {
                        cloneFolder(originalProject, origFile, modelToReferenceMap, genRefMap, tester);
                    }
                } else {
                    if (name.endsWith(ModelUtil.DOT_EXTENSION_XMI)) {
                        cloneFile(originalProject, origFile, modelToReferenceMap, genRefMap, tester);
                    }
                }
            }
        }
    }

    private void cloneFolder2( File originalProject,
                               File originalFolder,
                               Map modelToReferenceMap,
                               Map genRefMap,
                               File clonedFolder,
                               Tester tester ) throws IOException {
        File[] origFiles = originalFolder.listFiles();
        if (origFiles != null) {
            for (int ndx = origFiles.length; --ndx >= 0;) {
                File origFile = origFiles[ndx];
                File clonedFile = new File(clonedFolder, origFile.getName());
                String name = origFile.getName();
                if (origFile.isDirectory()) {
                    if (name.charAt(0) != '.') {
                        cloneFolder2(originalProject, origFile, modelToReferenceMap, genRefMap, clonedFile, tester);
                    }
                } else {
                    if (name.endsWith(ModelUtil.DOT_EXTENSION_XMI) || name.endsWith(ModelUtil.DOT_EXTENSION_XSD)) {
                        clonedFolder.mkdirs();
                        cloneFile2(originalProject, origFile, modelToReferenceMap, genRefMap, clonedFile, tester);
                    }
                }
            }
        }
    }

    void cloneFile( File originalProject,
                    File originalFile,
                    Map modelToReferenceMap,
                    Map genRefMap,
                    Tester tester ) throws IOException {
        Reader in = new FileReader(originalFile);

        char[] buf = new char[8192];
        char state = '\0';
        StringBuffer elemNameBuilder = null;
        StringBuffer attrNameBuilder = null;
        StringBuffer attrValBuilder = null;
        String attrName = null;
        boolean href = false;
        boolean choiceCriteria = false;
        boolean sqlUuid = false;
        char skipState = '\0';

        /*
         * Variables used for calls to the Tester() class.
         */
        int xmiUuidCounter = 0;
        int newUuidCounter = 0;
        int xProjectHRefCounter = 0;

        try {
            for (int bufLen = in.read(buf); bufLen > 0; bufLen = in.read(buf)) {
                for (int bufNdx = 0; bufNdx < bufLen; bufNdx++) {
                    char chr = buf[bufNdx];
                    if (state == '\0') { // Outside of element?
                        if (chr == '<') { // Start of element (either start element or end element)?
                            state = '<';
                        }
                    } else if (state == '<') { // Within element?
                        if (chr == '?') {
                            state = '?';
                        } else if (chr == '!') {
                            state = '!';
                        } else if (chr == '/') {
                            state = '\0';
                        } else if (!Character.isWhitespace(chr)) { // Start of element name?
                            state = 'E';
                            elemNameBuilder = new StringBuffer();
                            elemNameBuilder.append(chr);
                        }
                    } else if (state == 'E') { // Within element name?
                        if (chr == '/' || chr == '>') {
                            state = '\0';
                            if (chr == '/' || skipState == 'H') {
                                skipState = updateSkipMode(skipState, chr, elemNameBuilder);
                            }
                        } else if (Character.isWhitespace(chr)) { // After element name?
                            state = '@';
                            attrNameBuilder = new StringBuffer();
                            if (skipState == '\0') {
                                if ((elemNameBuilder.charAt(0) == 't' || elemNameBuilder.charAt(0) == 'T')
                                    && "transformation:TransformationContainer".equalsIgnoreCase(elemNameBuilder.toString())) { //$NON-NLS-1$
                                    skipState = 'C';
                                }
                            } else if (skipState == 'C') {
                                if ("transformationMappings".equals(elemNameBuilder.toString())) { //$NON-NLS-1$
                                    skipState = 'M';
                                }
                            } else if (skipState == 'M') {
                                if ("helper".equals(elemNameBuilder.toString())) { //$NON-NLS-1$
                                    skipState = 'H';
                                }
                            }
                        } else {
                            elemNameBuilder.append(chr);
                        }
                    } else if (state == 'e') { // Within element name of end tag?
                        if (chr == '>' || Character.isWhitespace(chr)) {
                            state = '\0';
                            skipState = updateSkipMode(skipState, chr, elemNameBuilder);
                        } else {
                            elemNameBuilder.append(chr);
                        }
                    } else if (state == '@') { // After element name?
                        if (chr == '/' || chr == '>') { // End of start element?
                            state = '\0';
                            if (chr == '/' || skipState == 'H') {
                                skipState = updateSkipMode(skipState, chr, elemNameBuilder);
                            }
                        } else if (chr == '\'' || chr == '"') { // Start of attribute value?
                            state = chr;
                            attrName = attrNameBuilder.toString();
                            // Check if this is an attribute that should be skipped
                            if ("xmi:uuid".equals(attrName) || "mmedt:UUID".equals(attrName)) { //$NON-NLS-1$ //$NON-NLS-2$
                                // If so, change mode to not collect attribute value
                                attrValBuilder = null;
                                xmiUuidCounter++;
                            } else if (skipState == 'H' && attrName.endsWith("Sql") //$NON-NLS-1$
                                       && (attrName.startsWith("select") || attrName.startsWith("insert") //$NON-NLS-1$ //$NON-NLS-2$
                                           || attrName.startsWith("update") || attrName.startsWith("delete"))) { //$NON-NLS-1$ //$NON-NLS-2$
                                attrValBuilder = new StringBuffer();
                                sqlUuid = true;
                            } else {
                                // else, create a buffer to store attribute value
                                attrValBuilder = new StringBuffer();
                                if ("href".equals(attrName)) { //$NON-NLS-1$
                                    href = true;
                                } else if ("choiceCriteria".equals(attrName)) { //$NON-NLS-1$
                                    choiceCriteria = true;
                                }
                            }
                        } else if (chr != '=' && !Character.isWhitespace(chr)) { // Start of attribute name
                            attrNameBuilder.append(chr);
                        }
                    } else if (state == '\'' || state == '"') { // Within attribute value?
                        if (chr == state) { // End of attribute value?
                            state = 'E';
                            if (attrValBuilder != null) {
                                String attrVal = attrValBuilder.toString();
                                if (href) {
                                    href = false;
                                    // Skip references to built-in models/metamodels
                                    if (attrVal.startsWith("http:") || attrVal.indexOf(UUID.PROTOCOL) < 0) { //$NON-NLS-1$
                                        continue;
                                    }
                                    // Skip references to artifacts in external projects
                                    int ndx = attrVal.indexOf('#');
                                    String ref = attrVal.substring(0, ndx);
                                    File referencedFile = new File(originalFile.getParentFile().getPath(), ref);
                                    String referencedPath = referencedFile.getCanonicalPath();
                                    if (!referencedPath.startsWith(originalProject.getCanonicalPath())) {
                                        xProjectHRefCounter++;
                                        continue;
                                    }
                                    // Map model to reference
                                    boolean didCreate = mapOriginalUuidToNewUuidForReferencedPath(attrVal.substring(ndx
                                                                                                                    + 1
                                                                                                                    + UUID_PREFIX_LENGTH),
                                                                                                  getOriginalReferenceToNewReferenceMap(referencedPath,
                                                                                                                                        modelToReferenceMap),
                                                                                                  genRefMap);
                                    if (didCreate) {
                                        newUuidCounter++;
                                    }
                                } else if (choiceCriteria) {
                                    choiceCriteria = false;
                                    Map origRef2NewRefMap = getOriginalReferenceToNewReferenceMap(originalFile.getCanonicalPath(),
                                                                                                  modelToReferenceMap);
                                    for (int ndx = attrVal.indexOf(UUID.PROTOCOL); ndx >= 0; ndx = attrVal.indexOf(UUID.PROTOCOL,
                                                                                                                   ndx)) {
                                        ndx += UUID_PREFIX_LENGTH;
                                        boolean didCreate = mapOriginalUuidToNewUuidForReferencedPath(attrVal.substring(ndx,
                                                                                                                        ndx
                                                                                                                        + UUID.FQ_LENGTH
                                                                                                                        - UUID_PREFIX_LENGTH),
                                                                                                      origRef2NewRefMap,
                                                                                                      genRefMap);
                                        if (didCreate) {
                                            newUuidCounter++;
                                        }
                                    }
                                } else if (sqlUuid) {
                                    sqlUuid = false;
                                    for (int ndx = attrVal.indexOf(UUID.PROTOCOL); ndx >= 0; ndx = attrVal.indexOf(UUID.PROTOCOL,
                                                                                                                   ndx)) {
                                        ndx += UUID_PREFIX_LENGTH;
                                        String originalUuid = attrVal.substring(ndx, ndx + UUID.FQ_LENGTH - UUID_PREFIX_LENGTH);
                                        boolean didCreate = mapOriginalUuidToNewUuidForReferencedPath(originalUuid,
                                                                                                      genRefMap,
                                                                                                      genRefMap);

                                        if (didCreate) {
                                            // System.out.println("  File: " + originalFile.getName() + "  ATTR: " + attrName +
                                            // " Created New UUID: "
                                            // + (String)genRefMap.get(originalUuid) + "  OLD UUID: " + originalUuid);
                                            newUuidCounter++;
                                        }
                                    }
                                } else {
                                    // Skip all other references that don't start with the UUID protocol
                                    if (attrValBuilder.length() < UUID_PROTOCOL_LENGTH) {
                                        continue;
                                    }
                                    // Map model to reference
                                    Map origRef2NewRefMap = getOriginalReferenceToNewReferenceMap(originalFile.getCanonicalPath(),
                                                                                                  modelToReferenceMap);
                                    String[] origUuids = attrVal.split(" +"); //$NON-NLS-1$
                                    for (int ndx = origUuids.length; --ndx >= 0;) {
                                        boolean didCreate = mapOriginalUuidToNewUuidForReferencedPath(origUuids[ndx].substring(UUID_PREFIX_LENGTH),
                                                                                                      origRef2NewRefMap,
                                                                                                      genRefMap);
                                        if (didCreate) {
                                            newUuidCounter++;
                                        }
                                    }
                                }
                            }
                        } else if (attrValBuilder != null) {
                            attrValBuilder.append(chr);
                            if (!href && !choiceCriteria && !sqlUuid && attrValBuilder.length() == UUID_PROTOCOL_LENGTH
                                && !UUID.PROTOCOL.equals(attrValBuilder.toString())) {
                                attrValBuilder = null;
                            }
                        }
                    } else if (state == '?') {
                        if (chr == '?') {
                            state = '>';
                        }
                    } else if (state == '>') {
                        if (chr == '>') {
                            state = '\0';
                        } else {
                            state = '?';
                        }
                    } else if (state == '!') {
                        if (chr == '-') {
                            state = '~';
                        }
                    } else if (state == '~') {
                        if (chr == '-') {
                            state = '-';
                        } else {
                            state = '!';
                        }
                    } else if (state == '-') {
                        if (chr == '>') {
                            state = '\0';
                        } else {
                            state = '!';
                        }
                    }
                    if (tester != null) {
                        tester.cloneFile1(originalProject,
                                          originalFile,
                                          modelToReferenceMap,
                                          buf,
                                          bufLen,
                                          bufNdx,
                                          chr,
                                          state,
                                          elemNameBuilder,
                                          attrNameBuilder,
                                          attrValBuilder,
                                          href,
                                          choiceCriteria);
                    }
                }
            }
            if (tester != null) {
                tester.cloneFilePassOneSummary(originalProject,
                                               originalFile,
                                               getOriginalReferenceToNewReferenceMap(originalFile.getCanonicalPath(),
                                                                                     modelToReferenceMap),
                                               xmiUuidCounter,
                                               newUuidCounter,
                                               xProjectHRefCounter);
            }
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    void cloneFile2( File originalProject,
                     File originalFile,
                     Map modelToReferenceMap,
                     Map genRefMap,
                     File clonedFile,
                     Tester tester ) throws IOException {
        Reader in = new FileReader(originalFile);

        int replacedUuidCounter = 0;

        try {
            BufferedWriter out;
            CharArrayWriter testWriter = null;
            if (tester == null) {
                out = new BufferedWriter(new FileWriter(clonedFile));
            } else {
                tester.cloningFile(originalFile);
                testWriter = new CharArrayWriter();
                out = new BufferedWriter(testWriter);
            }
            char[] buf = new char[BUFFER_LENGTH];
            char state = '\0';
            StringBuffer elemNameBuilder = null;
            StringBuffer attrNameBuilder = null;
            StringBuffer attrValBuilder = null;
            String attrName = null;
            boolean href = false;
            boolean choiceCriteria = false;
            boolean sqlUuid = false;
            boolean uuid = false;
            int prevBufLen = 0;
            int outBufNdx = 0;
            boolean xsd = originalFile.getName().endsWith(ModelUtil.DOT_EXTENSION_XSD);
            try {
                for (int bufLen = in.read(buf, 0, BUFFER_LENGTH); bufLen > prevBufLen; bufLen = prevBufLen
                                                                                                + in.read(buf,
                                                                                                          prevBufLen,
                                                                                                          BUFFER_LENGTH)) {
                    for (int inBufNdx = prevBufLen; inBufNdx < bufLen; inBufNdx++) {
                        char chr = buf[inBufNdx];
                        if (state == '\0') { // Outside of element?
                            if (chr == '<') { // Start of element (either start element or end element)?
                                state = '<';
                            }
                        } else if (state == '<') { // Within element?
                            if (chr == '?') {
                                state = '?';
                            } else if (chr == '!') {
                                state = '!';
                            } else if (chr == '/') {
                                state = 'e';
                                elemNameBuilder = new StringBuffer();
                            } else if (!Character.isWhitespace(chr)) { // Start of element name?
                                state = 'E';
                                elemNameBuilder = new StringBuffer();
                                elemNameBuilder.append(chr);
                            }
                        } else if (state == 'E') { // Within element name?
                            if (chr == '/' || chr == '>') {
                                state = '\0';
                            } else if (Character.isWhitespace(chr)) { // After element name?
                                state = '@';
                                attrNameBuilder = new StringBuffer();
                            } else {
                                elemNameBuilder.append(chr);
                            }
                        } else if (state == 'e') { // Within element name of end tag?
                            if (chr == '>' || Character.isWhitespace(chr)) {
                                state = '\0';
                            } else {
                                elemNameBuilder.append(chr);
                            }
                        } else if (state == '@') { // After element name?
                            if (chr == '/' || chr == '>') { // End of start element?
                                state = '\0';
                            } else if (chr == '\'' || chr == '"') { // Start of attribute value?
                                state = chr;
                                // Check if this is an UUID-based SQL attribute that should be skipped
                                attrName = attrNameBuilder.toString();

                                // Write everything up until here
                                if (tester != null) {
                                    tester.writeToOut(buf, outBufNdx, inBufNdx + 1 - outBufNdx);
                                }
                                out.write(buf, outBufNdx, inBufNdx + 1 - outBufNdx);
                                outBufNdx = inBufNdx + 1;

                                if (xsd) {
                                    if ("mmedt:UUID".equals(attrName)) { //$NON-NLS-1$
                                        uuid = true;
                                        attrValBuilder = new StringBuffer();
                                    } else {
                                        // Ignore all other attributes in XSDs
                                        attrValBuilder = null;
                                    }
                                } else {
                                    attrValBuilder = new StringBuffer();
                                    if ("xmi:uuid".equals(attrName)) { //$NON-NLS-1$
                                        uuid = true;
                                    } else if ("href".equals(attrName)) { //$NON-NLS-1$
                                        href = true;
                                    } else if ("choiceCriteria".equals(attrName)) { //$NON-NLS-1$
                                        choiceCriteria = true;
                                    } else if (attrName.endsWith("Sql")) { //$NON-NLS-1$
                                        if (attrName.startsWith("select") || attrName.startsWith("insert") //$NON-NLS-1$ //$NON-NLS-2$
                                            || attrName.startsWith("update") || attrName.startsWith("delete")) { //$NON-NLS-1$ //$NON-NLS-2$
                                            sqlUuid = true;
                                        }
                                    }
                                }
                            } else if (chr != '=' && !Character.isWhitespace(chr)) { // Start of attribute name
                                // Write everything up until here
                                if (attrNameBuilder.length() == 0) {
                                    if (tester != null) {
                                        tester.writeToOut(buf, outBufNdx, inBufNdx - outBufNdx);
                                    }

                                    out.write(buf, outBufNdx, inBufNdx - outBufNdx);
                                    outBufNdx = inBufNdx;
                                }

                                attrNameBuilder.append(chr);
                            }
                        } else if (state == '\'' || state == '"') { // Within attribute value?
                            if (chr == state) { // End of attribute value?
                                state = 'E';
                                if (attrValBuilder == null) {
                                    // if (skipState == 'S') {
                                    // skipState = 'H';
                                    // outBufNdx = inBufNdx + 1;
                                    // }
                                } else {
                                    String attrVal = attrValBuilder.toString();
                                    if (uuid) {
                                        uuid = false;
                                        boolean emptyUuid = false;
                                        String referencedPath = originalFile.getCanonicalPath();
                                        String originalUuid = ""; //$NON-NLS-1$ 
                                        if (attrVal.length() >= UUID_PREFIX_LENGTH) {
                                            originalUuid = attrVal.substring(UUID_PREFIX_LENGTH);
                                        } else if (attrVal.trim().length() == 0) {
                                            emptyUuid = true;
                                        }
                                        // else {
                                        // System.out.println("  File: " + originalFile.getName() + "  ATTR: " + attrName +
                                        // " ATTR VALUE TOO SHORT : Value = " + attrVal);
                                        // }

                                        String newUuid = (String)genRefMap.get(originalUuid);

                                        if (newUuid == null) {
                                            Map origRef2NewRefMap = (Map)modelToReferenceMap.get(referencedPath);
                                            if (origRef2NewRefMap != null) {
                                                newUuid = (String)origRef2NewRefMap.get(originalUuid);
                                            }
                                            if (newUuid == null) {
                                                newUuid = ((UUID)OBJECT_ID_FACTORY.create()).exportableForm();
                                            }
                                        }
                                        replacedUuidCounter++;

                                        // Check if emptyUuid and prefix the generated UUID with "mmedt:UUID
                                        if (xsd && emptyUuid) {
                                            if (tester != null) {
                                                tester.writeToOut(UUID.PROTOCOL + ':');
                                            }

                                            out.write(UUID.PROTOCOL + ':');
                                        } else {
                                            // Write
                                            if (tester != null) {
                                                tester.writeToOut(buf, outBufNdx, UUID_PREFIX_LENGTH);
                                            }

                                            out.write(buf, outBufNdx, UUID_PREFIX_LENGTH);
                                        }
                                        if (tester != null) {
                                            tester.writeToOut(newUuid);
                                        }

                                        out.write(newUuid);
                                        outBufNdx = inBufNdx;
                                    } else if (href) {
                                        href = false;
                                        // Skip references to built-in models/metamodels
                                        if (!attrVal.startsWith("http:")) { //$NON-NLS-1$
                                            int ndx = attrVal.indexOf('#');
                                            // Skip non-UUID references
                                            if (attrVal.regionMatches(ndx + 1, UUID.PROTOCOL, 0, UUID_PROTOCOL_LENGTH)) {
                                                // Skip references to artifacts in external projects
                                                String referencedPath = new File(originalFile.getParentFile().getPath(),
                                                                                 attrVal.substring(0, ndx)).getCanonicalPath();
                                                if (referencedPath.startsWith(originalProject.getCanonicalPath())) {
                                                    Map origRef2NewRefMap = (Map)modelToReferenceMap.get(referencedPath);
                                                    String originalUuid = attrVal.substring(ndx + 1 + UUID_PREFIX_LENGTH);

                                                    String newUuid = (String)genRefMap.get(originalUuid);
                                                    if (newUuid == null) {
                                                        newUuid = (String)origRef2NewRefMap.get(originalUuid);
                                                    }
                                                    replacedUuidCounter++;
                                                    // Write
                                                    if (tester != null) {
                                                        tester.writeToOut(buf, outBufNdx, UUID_PREFIX_LENGTH);
                                                    }

                                                    out.write(buf, outBufNdx, ndx + 1 + UUID_PREFIX_LENGTH);

                                                    if (tester != null) {
                                                        tester.writeToOut(newUuid);
                                                    }

                                                    out.write(newUuid);
                                                    outBufNdx = inBufNdx;
                                                }
                                            }
                                        }
                                    } else if (sqlUuid) {
                                        sqlUuid = false;

                                        for (int startNdx = 0, ndx = attrVal.indexOf(UUID.PROTOCOL); ndx >= 0; ndx = attrVal.indexOf(UUID.PROTOCOL,
                                                                                                                                     startNdx)) {
                                            ndx += UUID_PREFIX_LENGTH;
                                            int endNdx = ndx + UUID.FQ_LENGTH - UUID_PREFIX_LENGTH;
                                            String origUuid = attrVal.substring(ndx, endNdx);
                                            String newUuid = (String)genRefMap.get(origUuid);
                                            if (newUuid == null) {
                                                // We don't know what model this is in
                                                newUuid = getNewUuidForOriginalUuid(origUuid, modelToReferenceMap);
                                            }
                                            replacedUuidCounter++;
                                            // Write
                                            if (tester != null) {
                                                tester.writeToOut(buf, outBufNdx, ndx - startNdx);
                                            }

                                            out.write(buf, outBufNdx, ndx - startNdx);

                                            if (tester != null) {
                                                tester.writeToOut(newUuid);
                                            }

                                            out.write(newUuid);

                                            outBufNdx += endNdx - startNdx;
                                            startNdx = endNdx;
                                        }
                                    } else {
                                        if (choiceCriteria) {
                                            choiceCriteria = false;
                                        }
                                        Map origRef2NewRefMap = (Map)modelToReferenceMap.get(originalFile.getCanonicalPath());
                                        for (int startNdx = 0, ndx = attrVal.indexOf(UUID.PROTOCOL); ndx >= 0; ndx = attrVal.indexOf(UUID.PROTOCOL,
                                                                                                                                     startNdx)) {
                                            ndx += UUID_PREFIX_LENGTH;
                                            int endNdx = ndx + UUID.FQ_LENGTH - UUID_PREFIX_LENGTH;
                                            String origUuid = attrVal.substring(ndx, endNdx);
                                            String newUuid = (String)genRefMap.get(origUuid);
                                            if (newUuid == null) {
                                                newUuid = (String)origRef2NewRefMap.get(origUuid);
                                            }
                                            replacedUuidCounter++;
                                            // Write
                                            if (tester != null) {
                                                tester.writeToOut(buf, outBufNdx, ndx - startNdx);
                                            }

                                            out.write(buf, outBufNdx, ndx - startNdx);

                                            if (tester != null) {
                                                tester.writeToOut(newUuid);
                                            }

                                            out.write(newUuid);
                                            outBufNdx += endNdx - startNdx;
                                            startNdx = endNdx;
                                        }
                                    }
                                }
                            } else if (attrValBuilder != null) {
                                attrValBuilder.append(chr);
                                if (!uuid && !href && !sqlUuid && !choiceCriteria
                                    && attrValBuilder.length() == UUID.PROTOCOL.length()
                                    && !UUID.PROTOCOL.equals(attrValBuilder.toString())) {
                                    attrValBuilder = null;
                                }
                            }
                        } else if (state == '?') {
                            if (chr == '?') {
                                state = '>';
                            }
                        } else if (state == '>') {
                            if (chr == '>') {
                                state = '\0';
                            } else {
                                state = '?';
                            }
                        } else if (state == '!') {
                            if (chr == '-') {
                                state = '~';
                            }
                        } else if (state == '~') {
                            if (chr == '-') {
                                state = '-';
                            } else {
                                state = '!';
                            }
                        } else if (state == '-') {
                            if (chr == '>') {
                                state = '\0';
                            } else {
                                state = '!';
                            }
                        }
                        if (tester != null) {
                            out.flush();
                            tester.cloneFile2(originalProject,
                                              originalFile,
                                              modelToReferenceMap,
                                              buf,
                                              bufLen,
                                              inBufNdx,
                                              chr,
                                              state,
                                              elemNameBuilder,
                                              attrNameBuilder,
                                              attrValBuilder,
                                              href,
                                              choiceCriteria,
                                              clonedFile,
                                              testWriter,
                                              uuid,
                                              outBufNdx);
                        }
                    }

                    // Save any remaining characters that haven't been written
                    if (outBufNdx < bufLen) {
                        prevBufLen = bufLen - outBufNdx;
                        char[] newBuf = new char[prevBufLen + BUFFER_LENGTH];
                        System.arraycopy(buf, outBufNdx, newBuf, 0, prevBufLen);
                        buf = newBuf;
                    } else if (prevBufLen > 0) {
                        prevBufLen = 0;
                        buf = new char[BUFFER_LENGTH];
                    }

                    outBufNdx = 0;
                }

                // Write any remaining characters that haven't been written
                if (prevBufLen > 0) {
                    if (tester != null) {
                        tester.writeToOut(buf, outBufNdx, prevBufLen);
                    }

                    out.write(buf, outBufNdx, prevBufLen);

                    if (tester != null) {
                        out.flush();
                        tester.cloneFile2(originalProject,
                                          originalFile,
                                          modelToReferenceMap,
                                          buf,
                                          prevBufLen,
                                          0,
                                          '\0',
                                          '\0',
                                          null,
                                          null,
                                          null,
                                          false,
                                          false,
                                          clonedFile,
                                          testWriter,
                                          false,
                                          0);
                    }
                }
            } finally {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
            if (tester != null) {
                tester.cloneFilePassTwoSummary(originalProject, originalFile, replacedUuidCounter);
            }
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    private Map getOriginalReferenceToNewReferenceMap( String referencedPath,
                                                       Map modelToReferenceMap ) {
        Map origRef2NewRefMap = (Map)modelToReferenceMap.get(referencedPath);
        if (origRef2NewRefMap == null) {
            origRef2NewRefMap = new HashMap();
            modelToReferenceMap.put(referencedPath, origRef2NewRefMap);
        }
        return origRef2NewRefMap;
    }

    private boolean mapOriginalUuidToNewUuidForReferencedPath( String originalUuid,
                                                               Map origRef2NewRefMap,
                                                               Map genRefMap ) {
        boolean createdNew = false;

        String newUuid = (String)origRef2NewRefMap.get(originalUuid);
        if (newUuid == null) {
            newUuid = (String)genRefMap.get(originalUuid);
            if (newUuid == null) {
                createdNew = true;
                newUuid = ((UUID)OBJECT_ID_FACTORY.create()).exportableForm();
                origRef2NewRefMap.put(originalUuid, newUuid);
                // System.out.println("\t   Map:   => [" + originalUuid + " => " + newUuid + "]");
            }
        }

        return createdNew;
    }

    private String getNewUuidForOriginalUuid( String originalUuid,
                                              Map modelToReferenceMap ) {
        for (Iterator iter = modelToReferenceMap.keySet().iterator(); iter.hasNext();) {
            String nextKey = (String)iter.next();
            if (nextKey != null) {
                Map origRef2NewRefMap = (Map)modelToReferenceMap.get(nextKey);
                String newUuid = (String)origRef2NewRefMap.get(originalUuid);
                if (newUuid != null) {
                    return newUuid;
                }
            }
        }
        return null;
    }

    private char updateSkipMode( char skipMode,
                                 char chr,
                                 StringBuffer elementName ) {
        if (skipMode == 'H' && "helper".equals(elementName.toString())) { //$NON-NLS-1$
            return 'M';
        }
        if (skipMode == 'M' && "transformationMappings".equals(elementName.toString())) { //$NON-NLS-1$
            return 'C';
        }
        if (skipMode == 'C' && "Transformation:TransformationContainer".equals(elementName.toString())) { //$NON-NLS-1$
            return '$';
        }
        return skipMode;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#cloneMultiple(org.eclipse.emf.ecore.EObject, int)
     */
    public Collection cloneMultiple( final EObject eObject,
                                     final int numClones ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isPositive(numClones);

        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Clone_{0}_12", getPresentationValue(eObject)); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) throws ModelerCoreException {
                final EditingDomain ed = cntr.getEditingDomain();
                final Collection resultsList = new ArrayList();
                for (int i = 0; i != numClones; ++i) {
                    final Command command = new CloneCommand(ed, eObject);
                    executeCommandInTransaction(uow, eObject, command);
                    command.getAffectedObjects();

                    // Add the result to the command ...
                    Collection commandResults = command.getResult();
                    for (Iterator iter = commandResults.iterator(); iter.hasNext();) {
                        Object nextObj = iter.next();
                        if (!resultsList.contains(nextObj)) {
                            resultsList.add(nextObj);
                        }
                    }
                }
                // Look up the result ...
                return resultsList;
            }
        };
        final Collection result = (Collection)executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
        return result;
    }

    /**
     * Return true if the child may be added to this parent
     * 
     * @param parent
     * @param child
     * @return true if the child may be added to this parent
     */
    public boolean isValidParent( final Object parent,
                                  final EObject child ) {
        if (parent instanceof EObject) {
            EObject eParent = (EObject)parent;
            final EReference ref = getChildReference(eParent, child);
            // If no EReference for the parent has the child's EClass
            // as an EType then this child cannot be owned by this parent
            if (ref == null) {
                return false;
            }

            // If the EReference is not containment then this
            // parent cannot own this child
            if (!ref.isContainment()) {
                return false;
            }

            // If the potential child and the parent are from different metamodels
            // then we cannot paste even if one EClass extends the other
            final EClass eParentClass = eParent.eClass();
            final EClass eChildClass = child.eClass();
            if (eParentClass != null && eChildClass != null) {
                final URI eParentMetamodelURI = eParentClass.eResource().getURI();
                final URI eChildMetamodelURI = eChildClass.eResource().getURI();
                if (eParentMetamodelURI != null && !eParentMetamodelURI.equals(eChildMetamodelURI)) {
                    return false;
                }
            }

            if (ref.isMany()) {
                return true;
            }

            Object value = eParent.eGet(ref);
            if (value == null) {
                return true;
            }
        } else if (parent instanceof Resource) {
            return isValidRootObject((Resource)parent, child.eClass());
        } else if (parent instanceof IFile) {
            try {
                final ModelResource mr = findModelResource((IFile)parent);
                if (mr != null) {
                    final Resource rsrc = mr.getEmfResource();
                    if (rsrc != null) {
                        return isValidRootObject(rsrc, child.eClass());
                    }
                }
            } catch (ModelWorkspaceException e) {
                return false;
            }
        } else if (parent instanceof ModelResource) {
            try {
                final Resource rsrc = ((ModelResource)parent).getEmfResource();
                if (rsrc != null) {
                    return isValidRootObject(rsrc, child.eClass());
                }
            } catch (ModelWorkspaceException e) {
                // Do nothing... all false to be returned;
            }
        }

        return false;
    }

    public boolean isValidPasteParent( final Object potentialParent ) {
        final ContainerImpl cntr = getContainer();

        if (cntr == null) {
            return false;
        }

        final EditingDomain ed = cntr.getEditingDomain();

        if (ed.getClipboard() == null || ed.getClipboard().isEmpty()) {
            return false;
        }

        final Iterator children = ed.getClipboard().iterator();

        while (children.hasNext()) {
            final EObject next = (EObject)children.next();
            if (!isValidParent(potentialParent, next)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#create(org.eclipse.emf.ecore.EClass)
     */
    public EObject create( final EClass eClass ) {
        return eClass.getEPackage().getEFactoryInstance().create(eClass);
    }

    /**
     * @param result
     * @param eObject
     */
    Collection createCommands( final Collection commandParams,
                               final EditingDomain ed ) {
        if (commandParams == null) {
            return commandParams;
        }

        final Collection commands = new ArrayList(commandParams.size());
        for (final Iterator iter = commandParams.iterator(); iter.hasNext();) {
            final CommandParameter param = (CommandParameter)iter.next();
            final EObject eOwner = param.getEOwner();
            final Object owner = param.getOwner();

            Command command = null;
            if (eOwner == null && owner instanceof Resource) {
                final EObject value = param.getEValue();
                command = new AddCommand(ed, ((Resource)owner).getContents(), value);
            } else {
                final CommandParameter newChildParam = new CommandParameter(param.getOwner(), param.getFeature(), param);
                command = ed.createCommand(CreateChildCommand.class, newChildParam);
            }

            // Determine if this command should be disabled by first checking
            // for a null feature reference. EMF will create child parameters
            // with a null feature to indicate that the feature is disabled
            if (param.getFeature() == null) {
                command = new DisabledCommand(command);
            }
            // Determine if this command should be disabled by checking
            // for violations in the feature multiplicity
            else {
                final boolean disable = shouldBeDisabled(owner, param);
                if (disable && command != null) {
                    command = new DisabledCommand(command);
                }
            }

            commands.add(command);

        }

        return commands;
    }

    private Collection getRootDescriptors( final Resource rsrc ) {
        CoreArgCheck.isNotNull(rsrc);
        final String primaryMetamodelURI = getPrimaryMetamodelURI(rsrc);
        if (primaryMetamodelURI == null) {
            // any older models may not have a primaryMetamodelURI... just return empty list
            // instead of throwing an exception
            return Collections.EMPTY_LIST;
        }

        // Log an error if there is no metamodel with this primary metamodel URI. There may be
        // an inconsistency between the metamodel extenion URIs and the primary metamodel URI
        // in the resource.
        final URI nsUri = ModelerCore.getMetamodelRegistry().getURI(primaryMetamodelURI);
        if (ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(nsUri) == null) {
            final StringBuffer sb = new StringBuffer();
            final MetamodelDescriptor[] descriptors = ModelerCore.getMetamodelRegistry().getMetamodelDescriptors();
            for (int i = 0; i < descriptors.length; i++) {
                final MetamodelDescriptor mmd = descriptors[i];
                if (mmd.isPrimary() && !CoreStringUtil.isEmpty(mmd.getNamespaceURI())) {
                    sb.append(mmd.getNamespaceURI());
                    sb.append(CoreStringUtil.Constants.SPACE);
                }

            }
            final Object[] params = new Object[] {primaryMetamodelURI, sb.toString()};
            final String msg = ModelerCore.Util.getString("ModelEditorImpl.No_root_classes_found_for_metamodel_URI", params); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, msg);
        }

        final EPackage ePackage = ModelerCore.getMetamodelRegistry().getEPackage(nsUri);
        final EFactory eFactory = ePackage.getEFactoryInstance();
        final Collection rootClasses = Arrays.asList(ModelerCore.getMetamodelRegistry().getMetamodelRootClasses(nsUri));
        
        Collection filteredClasses = new ArrayList(rootClasses.size());
        for( Object obj : rootClasses ) {
        	String className = ((MetamodelRootClass)obj).getEClass().getName();
        	if( !className.equalsIgnoreCase("BaseTable") ) { //$NON-NLS-1$
        		filteredClasses.add(obj);
        	}
        }
        
        final Collection descriptors = createSiblingDescriptors(filteredClasses, eFactory, rsrc);

        return descriptors;
    }

    private String getPresentationValue( final Object obj ) {
        if (obj == null) {
            return ""; //$NON-NLS-1$
        }

        if (obj instanceof EObject) {
            for (Iterator iter = ((EObject)obj).eClass().getEAllStructuralFeatures().iterator(); iter.hasNext();) {
                final EStructuralFeature feature = (EStructuralFeature)iter.next();
                if (NAME_FEATURE_NAME.equalsIgnoreCase(feature.getName())) {
                    Object val = ((EObject)obj).eGet(feature);
                    if (val == null) {
                        return ((EObject)obj).eClass().getName();
                    }
                    return val.toString();
                }
            }
            return ((EObject)obj).eClass().getName();
        } else if (obj instanceof Resource) {
            return URI.decode(((Resource)obj).getURI().toString());
        } else if (obj instanceof Collection) {
            if (((Collection)obj).size() > 1) {
                return ModelerCore.Util.getString("ModelEditorImpl.many_1"); //$NON-NLS-1$
            } else if (((Collection)obj).size() == 0) {
                return ModelerCore.Util.getString("ModelEditorImpl.empty_list_2"); //$NON-NLS-1$
            }

            Object first = ((Collection)obj).iterator().next();
            return getPresentationValue(first);
        }

        return obj.toString();
    }

    static void setDescriptorOwner( final Collection descriptors,
                                    final Object owner ) {
        CoreArgCheck.isNotNull(descriptors);
        for (Iterator iter = descriptors.iterator(); iter.hasNext();) {
            final Object descriptor = iter.next();
            if (descriptor instanceof CommandParameter) {
                if (owner instanceof XSDParticle) {
                    ((CommandParameter)descriptor).setOwner(((XSDParticle)owner).getContent());
                } else if (owner instanceof XSDAttributeUse) {
                    ((CommandParameter)descriptor).setOwner(((XSDAttributeUse)owner).getContent());
                } else {
                    ((CommandParameter)descriptor).setOwner(owner);
                }
            }
        }
    }

    /**
     * Return the first EReference of all the parent EClass's EReferences that matches the EType of the child object and is a
     * containment EReference. If no match is found then null is returned.
     * 
     * @param object
     * @param child
     * @return
     */
    private EReference getChildReference( final EObject object,
                                          final Object child ) {
        if (child instanceof EObject) {
            final EObject eChild = (EObject)child;
            final EObject eParent = object;

            // Iterate over all the child references to factor each child to the right reference.
            //
            final Collection refs = eParent.eClass().getEAllReferences();
            for (Iterator childrenReferences = refs.iterator(); childrenReferences.hasNext();) {
                final EReference eReference = (EReference)childrenReferences.next();
                final EClassifier eType = eReference.getEType();

                // If this object is compatible with this reference and
                // represents a containment feature. Only containment
                // features can own this EType as a child ...
                //
                if (eType.isInstance(eChild) && eReference.isContainment()) {
                    return eReference;
                }
            }
        }

        return null;
    }

    private ModelAnnotation getModelAnnotation( final Resource rsrc ) {
        if (rsrc instanceof MMXmiResource) {
            final ModelAnnotation model = ((MMXmiResource)rsrc).getModelAnnotation();
            return model;
        }

        final ModelResource mr = findModelResource(rsrc);
        if (mr != null) {
            try {
                final ModelAnnotation ma = mr.getModelAnnotation();
                if (ma != null) {
                    return ma;
                }
            } catch (ModelWorkspaceException e) {
                ModelerCore.Util.log(e);
            }
        }

        // Couldn't find a model resource... just find the root annotation
        for (final Iterator contents = rsrc.getContents().iterator(); contents.hasNext();) {
            final EObject next = (EObject)contents.next();
            if (next instanceof ModelAnnotation) {
                return (ModelAnnotation)next;
            }
        }
        return null;
    }

    /**
     * @param rsrc
     * @return
     */
    private static String getPrimaryMetamodelURI( Resource rsrc ) {
        String primaryMetamodelUri = null;
        if (ModelUtil.isXsdFile(rsrc)) {
            primaryMetamodelUri = XSDPackage.eNS_URI;
        } else if (ModelUtil.isModelFile(rsrc) && rsrc instanceof MMXmiResource) {
            final MMXmiResource eRsrc = (MMXmiResource)rsrc;
            if (eRsrc.isLoaded()) {
                final ModelAnnotation annotation = eRsrc.getModelAnnotation();
                if (annotation != null) {
                    primaryMetamodelUri = annotation.getPrimaryMetamodelUri();
                }
            } else {
                XMIHeader header = getXmiHeader(eRsrc);
                if (header != null) {
                    primaryMetamodelUri = header.getPrimaryMetamodelURI();
                }
            }
        } else if (ModelUtil.isVdbArchiveFile(rsrc)) {
            primaryMetamodelUri = "http://www.metamatrix.com/metamodels/VirtualDatabase"; //$NON-NLS-1$
        }
        return primaryMetamodelUri;
    }

    /**
     * @param eclasses
     * @param factory
     * @return
     */
    static Collection createSiblingDescriptors( final Collection rootClasses,
                                                final EFactory factory,
                                                final Object owner ) {
        if (factory == null) {
            return Collections.EMPTY_LIST;
        }
        final boolean ownerIsRoot = (owner instanceof Resource || (owner instanceof EObject && ((EObject)owner).eContainer() == null));

        final Collection result = new HashSet(rootClasses.size());
        for (Iterator iter = rootClasses.iterator(); iter.hasNext();) {
            Resource rsrc = null;
            if (owner instanceof Resource) {
                rsrc = (Resource)owner;
            } else if (owner instanceof EObject) {
                rsrc = ((EObject)owner).eResource();
            }

            if (rsrc != null) {
                final Object next = iter.next();
                EClass eClass = null;
                int maxOccurs = -1;
                if (next instanceof MetamodelRootClass) {
                    MetamodelRootClass mmrc = (MetamodelRootClass)next;
                    eClass = mmrc.getEClass();
                    maxOccurs = mmrc.getMaxOccurs();
                } else {
                    eClass = (EClass)next;
                }
                final EObject child = factory.create(eClass);
                boolean disable = false;
                // If no root entities of this type are allowed then disable the command
                if (ownerIsRoot && maxOccurs == 0) {
                    disable = true;
                }
                // If a finite number of root entities of this type are allowed ...
                else if (ownerIsRoot && maxOccurs > 0) {
                    // Count the number of instances for this EClass
                    int count = 0;
                    for (Iterator iterator = rsrc.getContents().iterator(); iterator.hasNext();) {
                        EObject root = (EObject)iterator.next();
                        if (root.eClass().equals(eClass)) {
                            count++;
                        }
                    }

                    // If the current number of instances exceeds the max allowable then disable the command
                    if (count >= maxOccurs) {
                        disable = true;
                    }
                }

                // If the command is to be disable, then null the feature reference. This
                // is one way EMF indicates to its wizards to disable a command.
                if (disable) {
                    result.add(new CommandParameter(rsrc, null, child));
                } else {
                    result.add(new CommandParameter(rsrc, rsrc.getContents(), child));
                }
            } else {
                // return an empty list
                return Collections.EMPTY_LIST;
            }
        }

        return result;
    }

    private static boolean isRootObject( final EClass eclass ) {
        URI uri = eclass.eResource().getURI();
        Collection eclasses = Arrays.asList(ModelerCore.getMetamodelRegistry().getRootMetaClasses(uri));

        if (eclasses == null || eclasses.isEmpty()) {
            return false;
        }

        return eclasses.contains(eclass);
    }

    private static boolean isValidRootObject( final Resource rsrc,
                                              final EClass eclass ) {
        if (rsrc == null || eclass == null || !isRootObject(eclass)) {
            return false;
        }

        try {
            String primaryMetamodelUri = getPrimaryMetamodelURI(rsrc);
            if (primaryMetamodelUri == null) {
                return false;
            }
            final URI nsUri = ModelerCore.getMetamodelRegistry().getURI(primaryMetamodelUri);
            final Collection validRoots = Arrays.asList(ModelerCore.getMetamodelRegistry().getRootMetaClasses(nsUri));
            return validRoots.contains(eclass);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Return a {@link XMIHeader} instance for the specified resource or null if the resource is not an instance of a EmfResource.
     * 
     * @param resource
     * @return
     * @since 4.2
     */
    public static XMIHeader getXmiHeader( final Resource resource ) {
        XMIHeader header = null;
        if (resource != null && resource.getURI().isFile()) {
            final File resourceFile = new File(resource.getURI().toFileString());
            if (resourceFile.exists()) {
                try {
                    header = XMIHeaderReader.readHeader(resourceFile);
                } catch (TeiidException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                }
            }
        }
        return header;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getDescription(org.eclipse.emf.ecore.EObject)
     */
    public String getDescription( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        // Find the existing annotation object ...
        final Annotation annotation = getAnnotation(eObject, false);
        if (annotation != null) {
            return annotation.getDescription();
        }
        // else there was no annotation, so return null
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#setDescription(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    public void setDescription( final EObject eObject,
                                final String desc ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        if (eObject.eIsProxy()) {
            return; // cannot resolve an eProxy without a container/resource set
        }
        final boolean isSignificant = true;
        final String operationDescription = ModelerCore.Util.getString("ModelEditorImpl.Set_Description_on_{0}_14", eObject.toString()); //$NON-NLS-1$
        final ContainerImpl cntr = getContainer();
        final TransactionRunnable runnable = new TransactionRunnable() {
            public Object run( final UnitOfWork uow ) {
                // If the description is null ...
                if (desc == null || desc.trim().length() == 0) {
                    // Find the existing annotation object ...
                    final Annotation annotation = getAnnotation(eObject, false);
                    if (annotation != null) {
                        annotation.setDescription(null);

                        // And see if there is anything else on the annotation ...
                        if (annotation.getKeywords().isEmpty() && annotation.getTags().isEmpty()) {
                            // There isn't, so we can remove!
                            annotation.setAnnotationContainer(null);
                        }
                    } // else there was no annotation, so nothing to do ...
                } else {
                    // The description is non-null and non-trivial,
                    // so we have to make sure to get an Annotation ...
                    final Annotation annotation = getAnnotation(eObject, true);
                    annotation.setDescription(desc);
                }
                return null;
            }
        };

        executeAsTransaction(runnable, cntr, operationDescription, isSignificant, this);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findModelResource(org.eclipse.emf.ecore.resource.Resource)
     */
    public ModelResource findModelResource( final Resource resource ) {
        return ModelerCore.getModelWorkspace().findModelResource(resource);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findModelResource(EObject)
     */
    public ModelResource findModelResource( final EObject eObject ) {
        ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(eObject);
        if (modelResource == null) {
            // See if the object is a transient diagram ...
            if (eObject instanceof Diagram) {
                // See what the target of the diagram is ...
                final EObject target = ((Diagram)eObject).getTarget();
                if (target != null) {
                    modelResource = findModelResource(target);
                }
            }
        }
        return modelResource;
    }

    /*
     * @see com.metamatrix.modeler.core.ModelEditor#findResource(com.metamatrix.modeler.core.container.Container,
     *      org.eclipse.emf.ecore.EObject)
     */
    public Resource findResource( final Container container,
                                  final EObject eObject ) {
        CoreArgCheck.isNotNull(container);
        CoreArgCheck.isNotNull(eObject);
        return this.findResource(container, eObject, true);
    }

    /*
     * @see com.metamatrix.modeler.core.ModelEditor#findResource(com.metamatrix.modeler.core.container.Container,org.eclipse.emf.ecore.EObject,boolean)
     */
    public Resource findResource( final Container container,
                                  final EObject eObject,
                                  final boolean resolve ) {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isNotNull(container);
        Resource resource = eObject.eResource();

        // Obtain the resource by resolving the eProxy
        if (resolve && resource == null && eObject.eIsProxy()) {
            try {
                URI uri = ((InternalEObject)eObject).eProxyURI();
                resource = container.getResource(uri.trimFragment(), resolve);
            } catch (Exception e) {
                // ignore resource cannot be found
            }
        } else if (eObject instanceof XsdModelAnnotationImpl) {
            resource = ((XsdModelAnnotationImpl)eObject).getResource();
        }
        return resource;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findModelResource(org.eclipse.core.resources.IResource)
     */
    public ModelResource findModelResource( final IFile resource ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(resource);
        return (ModelResource)ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(resource);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#addModelImport(com.metamatrix.modeler.internal.core.resource.EmfResource,
     *      org.eclipse.emf.ecore.resource.Resource)
     */
    public boolean addModelImport( final MMXmiResource resource,
                                   final Resource importedResource ) throws ModelerCoreException {
        if (resource != null && importedResource != null && resource != importedResource) {
            // If the resource is the same as this model's (same URI),
            // then return (don't need an import to self)
            final URI importedUri = importedResource.getURI();
            final URI resourceUri = resource.getURI();
            if (resourceUri.isRelative() || resourceUri.equals(importedUri)) {
                return false;
            }

            // If a ModelImport already exists that matches the specified
            // "imported resource" then there is nothing to add.
            ModelImport existingImport = findModelImport(resource, importedResource);
            if (existingImport != null) {
                return false;
            }

            final ModelAnnotation modelAnnotation = resource.getModelAnnotation();
            if (modelAnnotation != null) {

                final ModelImport newModelImport = createModelImport(resource, importedResource);

                if (newModelImport != null) {
                    // If we got here, then the import is not in current list, so go ahead and add it
                    addValue(modelAnnotation, newModelImport, modelAnnotation.getModelImports());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getExistingModelImportForLocation(com.metamatrix.modeler.internal.core.resource.EmfResource,
     *      java.lang.String)
     * @since 5.0.2
     */
    public ModelImport getExistingModelImportForLocation( final MMXmiResource resource,
                                                          String someModelLocation ) {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(someModelLocation);

        // Cache the URI for the importedResource so we can look for it in the import list
        final Container cntr = ModelerCore.getContainer(resource);
        if (cntr != null) {
            final ModelAnnotation modelAnnot = resource.getModelAnnotation();
            if (modelAnnot != null && modelAnnot.getModelImports() != null) {

                for (final Iterator iter = modelAnnot.getModelImports().iterator(); iter.hasNext();) {
                    ModelImport modelImport = (ModelImport)iter.next();
                    // Get the import's location
                    String existingModelImportLocation = modelImport.getModelLocation();

                    if (existingModelImportLocation != null && existingModelImportLocation.equalsIgnoreCase(someModelLocation)) {
                        return modelImport;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#createModelLocation(com.metamatrix.modeler.internal.core.resource.EmfResource,
     *      org.eclipse.emf.ecore.resource.Resource)
     * @since 5.0.2
     */
    public String createModelLocation( final MMXmiResource resource,
                                       final Resource importedResource ) {
        CoreArgCheck.isNotNull(importedResource);

        // Check if the resource being imported is a valid import
        if (!isValidImportResource(importedResource)) {
            return null;
        }
        String newModelLocation = null;

        // Set the relative location of the imported resource
        URI resourceURI = resource.getURI();
        URI importURI = importedResource.getURI();
        String uriString = URI.decode(importURI.toString());

        boolean isVdbResource = false;
        if (resourceURI.toString().indexOf(ResourceFinder.VDB_WORKING_FOLDER_URI_PATH_SEGEMENT) > -1) {
            isVdbResource = true;
        }

        // If the URI of the proxy resource is a logical URI of a built-in resource then use this value as the location
        if (uriString.startsWith("http") || //$NON-NLS-1$
            uriString.startsWith(ResourceFinder.METAMODEL_PREFIX) || uriString.startsWith(ResourceFinder.UML2_METAMODELS_PREFIX)) {
            newModelLocation = uriString;
            if (isVdbResource && newModelLocation.equalsIgnoreCase(DatatypeConstants.BUILTIN_DATATYPES_URI)) {
                // Get the vdb's temp project path
                IPath vdbProjectPath = ModelUtil.getVdbProjectPathURI(resource);
                if (vdbProjectPath != null && !vdbProjectPath.isEmpty()) {
                    // We need to convert this location to the ../builtInDatatypes.xsd string
                    String builtInDatatypesName = DatatypeConstants.DATATYPES_MODEL_FILE_NAME;
                    IPath builtInDatatypesPath = vdbProjectPath.append(builtInDatatypesName);
                    URI builtInDTypeURI = URI.createFileURI(builtInDatatypesPath.toString());
                    URI relURI = ModelUtil.getRelativeLocation(resourceURI, builtInDTypeURI);
                    newModelLocation = relURI.toString();
                }
            }
            // If the import is to a file, compute the location relative to the resource containing the import
        } else if (importURI.isFile()) {
            boolean deresolve = (!resourceURI.isRelative() && resourceURI.isHierarchical());
            if (deresolve && !importURI.isRelative()) {
                URI deresolvedURI = importURI.deresolve(resourceURI, true, true, false);
                if (deresolvedURI.hasRelativePath()) {
                    importURI = deresolvedURI;
                }
            }
            newModelLocation = URI.decode(importURI.toString());

            // If the URI of the imported resource is to one of the built-in resources ...
        } else {

            // Check if the imported resource is one of the XSD global resources
            ResourceSet globalResourceSet = XSDSchemaImpl.getGlobalResourceSet();
            if (globalResourceSet.getResources().contains(importedResource)) {
                if (importedResource == XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).eResource()) {
                    newModelLocation = XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001;

                } else if (importedResource == XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001).eResource()) {
                    newModelLocation = XSDConstants.SCHEMA_INSTANCE_URI_2001;

                } else if (importedResource == XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).eResource()) {
                    newModelLocation = ResourceFinder.MAGIC_SCHEMA_URI.toString();
                }

                // Assume the reference is to some system reosurce
            } else {
                newModelLocation = URI.decode(importURI.toString());
            }

        }
        return newModelLocation;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#createModelImport(com.metamatrix.metamodels.core.ModelImport,
     *      org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public ModelImport createModelImport( final MMXmiResource resource,
                                          final Resource importedResource ) {
        CoreArgCheck.isNotNull(importedResource);

        // Check if the resource being imported is a valid import
        if (!isValidImportResource(importedResource)) {
            return null;
        }
        ModelImport modelImport = null;

        // Check for valid model location
        String newModelLocation = createModelLocation(resource, importedResource);
        if (newModelLocation != null) {

            modelImport = CoreFactory.eINSTANCE.createModelImport();
            modelImport.setName(this.getResourceName(importedResource));

            modelImport.setModelLocation(newModelLocation);

            if (importedResource instanceof MMXmiResource) {
                final MMXmiResource emfResource = (MMXmiResource)importedResource;
                modelImport.setModelType(emfResource.getModelType());
                modelImport.setPrimaryMetamodelUri(emfResource.getPrimaryMetamodelUri().toString());
                modelImport.setUuid(emfResource.getUuid().toString());
            } else if (importedResource instanceof XSDResourceImpl) {
                modelImport.setModelType(ModelType.TYPE_LITERAL);
                modelImport.setPrimaryMetamodelUri(XML_SCHEMA_METAMODEL_URI);
            }
        } else {
            String msg = ModelerCore.Util.getString("ModelEditorImpl.could_not_create_model_import_for_resource_0", importedResource.getURI()); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, msg);
        }

        return modelImport;
    }

    protected boolean isValidImportResource( final Resource importedResource ) {
        if (importedResource instanceof MMXmiResource) {
            final MMXmiResource emfResource = (MMXmiResource)importedResource;
            if (emfResource.getModelType() == null) {
                return false;
            } else if (emfResource.getPrimaryMetamodelUri() == null) {
                return false;
            } else if (emfResource.getUuid() == null) {
                return false;
            }
            return true;
        } else if (importedResource instanceof XSDResourceImpl) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#updateModelImport(com.metamatrix.metamodels.core.ModelImport,
     *      org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public void updateModelImport( final ModelImport modelImport,
                                   final Resource importedResource ) {
        CoreArgCheck.isNotNull(modelImport);
        CoreArgCheck.isNotNull(importedResource);

        Resource resource = modelImport.eResource();
        if (resource != null) {
            URI resourceURI = resource.getURI();
            URI importURI = importedResource.getURI();

            // If the URI of the imported resource is to one of the built-in resources then there is no location to update
            Container cntr = ModelerCore.getContainer(resource);
            if (cntr != null && cntr.getResourceFinder().isBuiltInResource(importedResource)) {
                return;
            }

            if (importURI.isFile()) {
                boolean deresolve = (resourceURI != null && !resourceURI.isRelative() && resourceURI.isHierarchical());
                if (deresolve && !importURI.isRelative()) {
                    URI deresolvedURI = importURI.deresolve(resourceURI, true, true, false);
                    if (deresolvedURI.hasRelativePath()) {
                        importURI = deresolvedURI;
                    }
                }
                modelImport.setName(importURI.trimFileExtension().lastSegment());
                modelImport.setModelLocation(URI.decode(importURI.toString()));
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#removeModelImports(com.metamatrix.modeler.internal.core.resource.EmfResource,
     *      org.eclipse.emf.ecore.resource.Resource)
     */
    public void removeModelImport( final MMXmiResource resource,
                                   final Resource importedResource ) throws ModelerCoreException {
        if (resource != null && importedResource != null && resource != importedResource) {

            // If no ModelImport exists that matches the specified
            // "imported resource" then there is nothing to remove.
            ModelImport importToRemove = findModelImport(resource, importedResource);
            if (importToRemove == null) {
                return;
            }

            // If an external reference still exists to the "imported resource" then we cannot remove the ModelImport
            final Container cntr = ModelerCore.getContainer(resource);
            if (cntr != null) {
                final ResourceFinder finder = cntr.getResourceFinder();
                Resource[] refs = finder.findReferencesFrom(resource, true, true);
                for (int i = 0; i != refs.length; ++i) {
                    if (importedResource == refs[i]) {
                        return;
                    }
                }
            }

            ModelAnnotation modelWithImports = resource.getModelAnnotation();
            if (modelWithImports != null) {
                removeValue(modelWithImports, importToRemove, modelWithImports.getModelImports());
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findModelImport(com.metamatrix.modeler.internal.core.resource.EmfResource,
     *      org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public ModelImport findModelImport( final MMXmiResource resource,
                                        final Resource importedResource ) {
        CoreArgCheck.isNotNull(resource);
        if (importedResource == null || resource.getURI().isRelative() || importedResource.getURI().isRelative()) {
            return null;
        }

        ModelImport theModelImport = null;

        final Container cntr = ModelerCore.getContainer(resource);
        if (cntr != null) {
            // Defect 23340 - create a model location for the importedResource to resource dependency
            String importedModelLocation = createModelLocation(resource, importedResource);
            // if location created, check for existing model import using the location.
            if (importedModelLocation != null) {
                theModelImport = getExistingModelImportForLocation(resource, importedModelLocation);
            }
        } else {
            String importedResourceUuid = null;
            String importedResourceLocation = null;
            if (importedResource instanceof MMXmiResource) {
                importedResourceUuid = ((MMXmiResource)importedResource).getUuid().toString();
            }
            URI resourceURI = resource.getURI();
            URI importURI = importedResource.getURI();

            // Check if the imported resource is one of the XSD global resources
            if (XSDSchemaImpl.getGlobalResourceSet().getResources().contains(importedResource)) {
                if (importedResource == XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).eResource()) {
                    importURI = ResourceFinder.SCHEMA_FOR_SCHEMA_URI;

                } else if (importedResource == XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001).eResource()) {
                    importURI = ResourceFinder.SCHEMA_INSTANCE_URI;

                } else if (importedResource == XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).eResource()) {
                    importURI = ResourceFinder.MAGIC_SCHEMA_URI;
                }

                // If the importURI is an absolute URI to a file then deresolve it to obtain
                // a relative URI like what is stored in the modelLocation feature of ModelImport
            } else if (resourceURI.isFile() && importURI.isFile()) {
                boolean deresolve = (!resourceURI.isRelative() && resourceURI.isHierarchical());
                if (deresolve && !importURI.isRelative()) {
                    URI deresolvedURI = importURI.deresolve(resourceURI, true, true, false);
                    if (deresolvedURI.hasRelativePath()) {
                        importURI = deresolvedURI;
                    }
                }
            }
            importedResourceLocation = URI.decode(importURI.toString());

            final ModelAnnotation modelAnnot = resource.getModelAnnotation();
            if (modelAnnot != null) {
                final List modelImports = modelAnnot.getModelImports();
                if (!modelImports.isEmpty()) {
                    for (Iterator iter = modelImports.iterator(); iter.hasNext();) {
                        ModelImport modelImport = (ModelImport)iter.next();
                        String modelImportUuid = modelImport.getUuid();
                        if (modelImportUuid != null && modelImportUuid.equalsIgnoreCase(importedResourceUuid)) {
                            theModelImport = modelImport;
                        }
                        if (theModelImport == null) {
                            String modelLocation = modelImport.getModelLocation();
                            if (modelLocation != null && modelLocation.equalsIgnoreCase(importedResourceLocation)) {
                                theModelImport = modelImport;
                            }
                        }
                        if (theModelImport != null) {
                            break;
                        }
                    }
                }
            }

        }

        return theModelImport;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getAnnotation(org.eclipse.emf.ecore.EObject, boolean)
     */
    public Annotation getAnnotation( EObject eObject,
                                     boolean forceCreate ) {
        final Resource eObjectResource = eObject.eResource();
        if (eObjectResource instanceof MMXmiResource) {
            // Just look up the annotations right off the resource ...
            final MMXmiResource emfResource = (MMXmiResource)eObjectResource;
            Annotation annotation = emfResource.getAnnotation(eObject);
            if (annotation == null && forceCreate) {
                annotation = ModelResourceContainerFactory.createNewAnnotation(eObject, emfResource.getAnnotationContainer(true));
            }
            return annotation;
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getAnnotation(com.metamatrix.modeler.core.workspace.ModelResource,
     *      org.eclipse.emf.ecore.EObject, boolean)
     */
    public Annotation getAnnotation( ModelResource modelResource,
                                     EObject eObject,
                                     boolean forceCreate ) {
        return getAnnotation(eObject, forceCreate);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getExtension(org.eclipse.emf.ecore.EObject)
     */
    public EObject getExtension( EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);

        final EClass eClass = eObject.eClass();

        // mtkTODO: Check whether the metamodel for this object allows extensions.
        final EPackage ePackage = eClass.getEPackage();

        // There is never an extension object for ECore ...
        if (EcorePackage.eINSTANCE.equals(ePackage)) {
            return null;
        }

        final Resource emfResource = eObject.eResource();
        if (emfResource == null) {
            return null;
        }

        // If the resource is an EmfResource ...
        if (emfResource instanceof MMXmiResource) {
            // Get the ModelAnnotation for the model that contains the eObject ...
            final ModelAnnotation model = getModelAnnotation(emfResource);
            if (model != null) {
                final XPackage extPackage = model.getExtensionPackage();
                if (extPackage != null) {
                    final XClass xclass = extPackage.findXClass(eClass);
                    if (xclass != null) {
                        EObject result = null;
                        try {
                            EPackage pkg = extPackage;
                            ExtensionFactory factory = null;
                            final EFactory existingFactory = pkg.getEFactoryInstance();
                            if (existingFactory == null || !(existingFactory instanceof ExtensionFactory)) {
                                factory = new ExtensionFactoryImpl();
                                factory.setEPackage(extPackage);
                            }
                            result = new ObjectExtension(eObject, xclass, this);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        return result;
                    }
                }
            }

            // mmDefect_12555 - Return the ObjectExtension associated with the XSDResource if one exists.
        } else if (emfResource instanceof XSDResourceImpl) {
            XSDResourceImpl xsdResource = (XSDResourceImpl)emfResource;
            XSDSchema xsdSchema = xsdResource.getSchema();

            if (xsdSchema != null) {
                // Get the extension XPackage for the schema - null is returned if extension is defined.
                final XPackage extPackage = XsdObjectExtension.getExtensionPackage(xsdSchema);
                if (extPackage != null) {
                    final XClass xclass = extPackage.findXClass(eClass);
                    if (xclass != null) {
                        EObject result = null;
                        try {
                            EPackage pkg = extPackage;
                            ExtensionFactory factory = null;
                            final EFactory existingFactory = pkg.getEFactoryInstance();
                            if (existingFactory == null || !(existingFactory instanceof ExtensionFactory)) {
                                factory = new ExtensionFactoryImpl();
                                factory.setEPackage(extPackage);
                            }
                            result = new XsdObjectExtension(eObject, xclass, this);
                        } catch (Throwable e) {
                            final String msg = ModelerCore.Util.getString("ModelEditorImpl.getExtension_0", xsdSchema); //$NON-NLS-1$
                            throw new ModelerCoreException(msg);
                        }
                        return result;
                    }
                    // } else {
                    // final String msg = ModelerCore.Util.getString("ModelEditorImpl.getExtension_0",xsdSchema); //$NON-NLS-1$
                    // throw new ModelerCoreException(msg);
                }
            }
        }

        return null;
        //
        // // mtkTODO: Replace with a call to get the real extension object
        // return getAnnotation(eObject,true);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findModelResource(com.metamatrix.metamodels.core.ModelImport)
     */
    public ModelResource findModelResource( final ModelImport modelImport ) {
        CoreArgCheck.isNotNull(modelImport);
        String thePath = modelImport.getPath();
        if (thePath != null) {
            final IPath pathInWorkspace = new Path(modelImport.getPath());
            return ModelerCore.getModelWorkspace().findModelResource(pathInWorkspace);
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getURI(org.eclipse.emf.ecore.EObject)
     */
    public URI getUri( final EObject object ) {
        CoreArgCheck.isNotNull(object);

        // If the object is a metamodel EClass then we need to return a URI based on the metamodel's
        // logical URI (e.g. http://www.metamatrix.com/metamodels/Relational) and not the actual
        // metamodel resource URI (e.g. mtkplugin:///com.metamatrix.metamodels.Relational).
        if (object instanceof EClass) {
            Resource eResource = object.eResource();
            // Do this only if we are in the Eclipse plugin environment since the metamodel
            // registry will not be loaded otherwise.
            if (eResource != null && ModelerCore.getPlugin() != null) {
                URI eResourceUri = eResource.getURI();
                MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(eResourceUri);
                if (descriptor != null) {
                    String logicalUri = URI.decode(descriptor.getNamespaceURI());
                    return URI.createURI(logicalUri).appendFragment(eResource.getURIFragment(object));
                }
            }
        }
        // Otherwise return the URI based on the EObject's resource URI and
        // it's location within that resource
        return EcoreUtil.getURI(object);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    public ObjectID getObjectID( final EObject object ) {
        CoreArgCheck.isNotNull(object);

        try {
            String uuid = getContainer().getObjectManager().getObjectId(object);

            if (uuid != null) {
                // JPAV: DOES THIS CREATE A NEW OBJECT EACH TIME?
                return stringToUuid(uuid);
            }
        } catch (Exception err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
        }

        return null;
    }

    private ObjectID stringToUuid( String uuidString ) throws InvalidIDException {
        return UUID.stringToObject(uuidString.startsWith(UUID.PROTOCOL) ? uuidString.substring(UUID.PROTOCOL.length() + 1) : uuidString);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#getObjectIdString(org.eclipse.emf.ecore.EObject)
     */
    public String getObjectIdString( EObject object ) {
        CoreArgCheck.isNotNull(object);
        return getContainer().getObjectManager().getObjectId(object);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#setObjectID(org.eclipse.emf.ecore.EObject, com.metamatrix.core.id.ObjectID)
     */
    public void setObjectID( EObject object,
                             ObjectID objectId ) {
        setObjectID(object, objectId.toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.ModelEditor#setObjectID(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    public void setObjectID( EObject object,
                             String objectId ) {
        CoreArgCheck.isNotNull(object);

        try {
            getContainer().getObjectManager().setObjectId(object, objectId);
        } catch (Exception err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
        }
    }

    /**
     * Try to retrieve the ObjectID from the SqlAspect. Certain implementations of SqlAspect override the getObjectID method with
     * specialized logic to determine the identifier. If the ObjectID cannot be obtained from the aspect then resort to the
     * default behavior found in ModelEditor.getObjectID.
     * 
     * @param eObject
     * @return
     * @since 4.2
     */
    public String getSearchIndexObjectID( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        Object objId = null;
        SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);
        if (sqlAspect != null) {
            if (sqlAspect instanceof SqlAnnotationAspect) {
                objId = getObjectIdString(eObject);
            } else {
                objId = sqlAspect.getObjectID(eObject);
            }
        } else if (eObject instanceof XSDConcreteComponent) {
            if (eObject.eIsProxy()) {
                URI proxyURI = EcoreUtil.getURI(eObject);
                if (proxyURI != null) {
                    objId = proxyURI.fragment();
                }
            } else if (eObject.eResource() != null) {
                objId = eObject.eResource().getURIFragment(eObject);
            }
        } else {
            objId = getObjectIdString(eObject);
        }
        if (objId != null) {
            return objId.toString();
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findObjectID(java.lang.Object)
     */
    public EObject findObject( final Object objectId ) {
        CoreArgCheck.isNotNull(objectId);

        // Look in the main container ...
        final Container cntr = getContainer();
        return findObject(objectId, cntr);
    }

    protected EObject findObject( Object objectId,
                                  final Container container ) {
        if (container == null) {
            return null;
        }
        EObject result = null;
        Object resolvedObjectId = null;
        if (objectId instanceof String) {
            final String stringId = (String)objectId;
            // See if the string really is an ObjectID ...
            try {
                resolvedObjectId = IDGenerator.getInstance().stringToObject(stringId);
            } catch (InvalidIDException e1) {
                // malformed ObjectID or not even an ObjectID; just continue
            }
            if (resolvedObjectId == null) {
                // See if the string really is a URI ...
                try {
                    resolvedObjectId = URI.createURI(stringId);
                } catch (IllegalArgumentException e) {
                    // malformed URI or not even a URI; just continue
                }
            }
        } else if (objectId instanceof URI) {
            resolvedObjectId = objectId;
        } else if (objectId instanceof ObjectID) {
            resolvedObjectId = objectId;
        }
        if (resolvedObjectId != null) {
            final EObjectFinder finder = container.getEObjectFinder();
            result = (EObject)finder.find(resolvedObjectId); // works for URI, Proxy, or ObjectID
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findObjectID(java.lang.Object,
     *      com.metamatrix.modeler.core.workspace.ModelResource)
     */
    public EObject findObject( final Object objectId,
                               final ModelResource resource,
                               final IProgressMonitor monitor ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(objectId);
        CoreArgCheck.isNotNull(resource);
        if (!resource.isLoaded() || !resource.isOpen()) {
            resource.open(monitor);
            resource.getEmfResource(); // loads the model
        }
        return findObject(objectId);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getChangedObject(org.eclipse.emf.common.notify.Notification)
     */
    public Object getChangedObject( final Notification notification ) {
        CoreArgCheck.isNotNull(notification);
        return notification.getNotifier();
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findObjectByPath(org.eclipse.emf.ecore.resource.Resource,
     *      org.eclipse.core.runtime.IPath)
     */
    public EObject findObjectByPath( final Resource resource,
                                     final IPath modelRelativePath ) {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(modelRelativePath);
        EObject object = null;
        Collection children = resource.getContents();
        final String[] segments = modelRelativePath.segments();
        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];
            object = findChildByLabel(children, segment);
            if (object == null) {
                return null;
            }
            children = object.eContents();
        }
        return object;
    }

    protected EObject findChildByLabel( final Collection children,
                                        final String label ) {
        for (final Iterator iter = children.iterator(); iter.hasNext();) {
            final EObject child = (EObject)iter.next();
            final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            // Get the "name segment" for the current object ...
            final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(child, IItemLabelProvider.class);
            // We have to remove any ':'s from the label returned, as IPath treats anything before the ':' as a device name. Thus,
            // we remove the entire ':' and type info.
            final String objName = formatLabel(provider.getText(child));
            if (objName.equals(label)) {
                return child;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#findObjectByPath(com.metamatrix.modeler.core.workspace.ModelResource,
     *      org.eclipse.core.runtime.IPath)
     */
    public EObject findObjectByPath( final ModelResource resource,
                                     final IPath modelRelativePath ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(modelRelativePath);
        final Resource emfResource = resource.getEmfResource();
        return findObjectByPath(emfResource, modelRelativePath);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelRelativatePath(org.eclipse.emf.ecore.EObject)
     */
    public IPath getModelRelativePath( EObject object ) {
        return getModelRelativePath(object, true);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelRelativePath(org.eclipse.emf.ecore.EObject, boolean)
     * @since 5.0.1
     */
    public IPath getModelRelativePath( EObject object,
                                       boolean includeUnnamedObjects ) {
        CoreArgCheck.isNotNull(object);
        return computeModelRelativePath(object, false, includeUnnamedObjects);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelRelativatePathIncludingModel(org.eclipse.emf.ecore.EObject)
     */
    public IPath getModelRelativePathIncludingModel( EObject object ) {
        return getModelRelativePathIncludingModel(object, true);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelRelativePathIncludingModel(org.eclipse.emf.ecore.EObject, boolean)
     * @since 5.0.1
     */
    public IPath getModelRelativePathIncludingModel( EObject object,
                                                     boolean includeUnnamedObjects ) {
        CoreArgCheck.isNotNull(object);
        return computeModelRelativePath(object, true, includeUnnamedObjects);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getFullPathToParent(org.eclipse.emf.ecore.EObject)
     */
    public IPath getFullPathToParent( final EObject object ) {
        CoreArgCheck.isNotNull(object);
        // Get the path to the resource ...
        final Resource resource = object.eResource();
        if (resource == null) {
            return Path.ROOT;
        }
        final String resourcePathStr = getResourcePath(resource);
        if (resourcePathStr == null) {
            return Path.ROOT;
        }
        final IPath resourcePath = new Path(resourcePathStr);

        // get the parent of object (may be the model) and generate the path in the model ...
        final EObject container = object.eContainer();
        if (container != null) {
            // Return the path to the parent ...
            final IPath relPathWithoutModel = computeModelRelativePath(container, false);
            return resourcePath.append(relPathWithoutModel);
        }

        // Else, there is no container/parent - that is, it is a root-level object
        // and the parent is the resource ...
        return resourcePath;
    }

    protected IPath computeModelRelativePath( EObject object,
                                              boolean includeModelInPath ) {
        return computeModelRelativePath(object, includeModelInPath, true);
    }

    protected IPath computeModelRelativePath( EObject object,
                                              boolean includeModelInPath,
                                              boolean includeUnnamedObjects ) {
        AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
        // Walk up the tree, prepending the path of the parent to the path seen so far ...
        IPath path = new Path(""); //$NON-NLS-1$
        EObject tmp = object;
        if (tmp instanceof XSDParticle) {
            path = updatePath(path, ((XSDParticle)tmp).getTerm(), adapterFactory, includeUnnamedObjects);
            tmp = tmp.eContainer();
        }
        for (; tmp != null; tmp = tmp.eContainer()) {
            if (!(tmp instanceof XSDParticle)) {
                path = updatePath(path, tmp, adapterFactory, includeUnnamedObjects);
            }
        }
        // Prepend the model name if required
        if (includeModelInPath) {
            String modelName = getModelName(object);
            if (modelName != null) {
                path = new Path(modelName).append(path);
            }
        }
        return path;
    }

    private IPath updatePath( final IPath path,
                              final EObject eObject,
                              final AdapterFactory factory,
                              boolean includeUnnamedObjects ) {
        if (!includeUnnamedObjects && !hasName(eObject)) {
            return path;
        }
        // Get the "name segment" for the current object ...
        final IItemLabelProvider provider = (IItemLabelProvider)factory.adapt(eObject, IItemLabelProvider.class);

        // We have to remove any ':'s from the label returned, as IPath treats anything before the ':' as a device name. Thus, we
        // remove the entire ':' and type info.
        String objName = formatLabel(provider.getText(eObject));

        // Add the segment for the current object
        return new Path(objName).append(path);
    }

    // removes ':'s and trailing white space from object labels
    private String formatLabel( final String name ) {
        final int ndx = name.indexOf(':');
        if (ndx >= 0) {
            return name.substring(0, ndx).trim();
        }
        return name;
    }

    public String getModelName( final EObject object ) {
        CoreArgCheck.isNotNull(object);
        if (object.eIsProxy()) {
            URI proxyUri = ((InternalEObject)object).eProxyURI();
            URI resourceUri = proxyUri.trimFragment();
            return getModelName(resourceUri);
        }
        if (object.eResource() != null) {
            return getModelName(object.eResource().getURI());
        }
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelName(org.eclipse.emf.ecore.resource.Resource)
     */
    public String getModelName( Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        // derive the model name from the EMF resource URI ...
        return getModelName(resource.getURI());
    }

    public String getModelName( final URI resourceUri ) {
        CoreArgCheck.isNotNull(resourceUri);
        // derive the model name from the EMF resource URI ...
        return resourceUri.trimFileExtension().lastSegment();
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelName(com.metamatrix.modeler.core.workspace.ModelResource)
     */
    public String getModelName( ModelResource modelResource ) {
        CoreArgCheck.isNotNull(modelResource);
        final IResource modelFile = modelResource.getResource();
        String modelName = null;
        if (modelFile instanceof IFile) {
            final IFile theFile = (IFile)modelFile;
            final String extension = theFile.getFileExtension(); // may be null, doesn't include the '.'
            modelName = theFile.getName();
            if (extension != null) {
                final int indexOfExtension = modelName.lastIndexOf(extension);
                if (indexOfExtension == 0) {
                    modelName = ""; //$NON-NLS-1$
                } else {
                    // Should find, but remove the extension plus the '.' before it
                    modelName = modelName.substring(0, indexOfExtension - 1);
                }
            }
        } else {
            modelName = modelResource.getItemName();
        }
        return modelName;
    }

    /**
     * This method will return the list of containers defined within a given <code>ModelResource</code> Applicable containers are
     * defined by the specific Metamodel type.
     * 
     * @param emfResource
     * @return
     * @since 4.3
     */
    public Collection getAllContainers( Resource resource ) {
        if (resource == null || !(resource instanceof MMXmiResource)) return Collections.EMPTY_LIST;

        return ModelResourceContainerFactory.getAllContainers(resource, true);
    }

    /**
     * Get the relative path within the workspace to the specified resource
     * 
     * @param resource
     * @return
     */
    public String getResourcePath( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        return WorkspaceResourceFinderUtil.getWorkspaceUri(resource);
    }

    /**
     * Return the name of the specified resource removing any file extension if one exists.
     * 
     * @param resource
     * @return
     */
    private String getResourceName( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);

        final URI resourceUri = resource.getURI();
        final String modelNameWithExt = resourceUri.lastSegment();
        final String extension = resourceUri.fileExtension();
        if (extension != null) {
            final int index = modelNameWithExt.indexOf(extension);
            if (index > 1) {
                return modelNameWithExt.substring(0, index - 1); // also remove the "."
            }
        }
        return modelNameWithExt;
    }

    /**
     * This creates a command that copies the given object.
     */
    public static Command createCopyCommand( EditingDomain domain,
                                             Object owner,
                                             CopyCommand.Helper helper ) {
        return domain.createCommand(CopyCommand.class, new CommandParameter(owner, null, helper));
    }

    /**
     * This creates a command that copies the given collection of objects. If the collection contains more than one object, then a
     * compound command will be created containing individual copy commands for each object.
     */
    public static Command createCopyCommand( final EditingDomain domain,
                                             final Collection collection,
                                             CopyCommand.Helper helper ) {
        if (collection == null || collection.isEmpty()) {
            return UnexecutableCommand.INSTANCE;
        }

        CopyCommand.Helper copyHelper = helper;

        CompoundCommand copyCommand = CompoundCommandFactory.create(CompoundCommand.MERGE_COMMAND_ALL);
        for (Iterator objects = collection.iterator(); objects.hasNext();) {
            copyCommand.append(domain.createCommand(CopyCommand.class, new CommandParameter(objects.next(), null, copyHelper)));
        }

        return copyCommand.unwrap();
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelContents(org.eclipse.emf.ecore.EObject)
     */
    public ModelContents getModelContents( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        final Resource resource = eObject.eResource();
        return getModelContents(resource);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelContents(org.eclipse.emf.ecore.resource.Resource)
     */
    public ModelContents getModelContents( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        if (resource instanceof MtkXmiResourceImpl) {
            return ((MtkXmiResourceImpl)resource).getModelContents();
        }
        final ModelResource modelResource = this.findModelResource(resource);
        return getModelContents(modelResource);
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelContents(com.metamatrix.modeler.core.workspace.ModelResource)
     */
    public ModelContents getModelContents( final ModelResource modelResource ) {
        CoreArgCheck.isNotNull(modelResource);
        try {
            return ModelContents.getModelContents(modelResource);
        } catch (ModelWorkspaceException e) {
            ModelerCore.Util.log(e);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelEditor#getModelAnnotation(org.eclipse.emf.ecore.EObject)
     */
    public ModelAnnotation getModelAnnotation( final EObject eObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(eObject);
        final Resource resource = eObject.eResource();
        if (resource instanceof MMXmiResource) {
            return ((MMXmiResource)resource).getModelAnnotation();
        }
        final ModelResource modelResource = resource == null ? null : this.findModelResource(resource);
        return modelResource == null ? null : modelResource.getModelAnnotation();
    }

    protected static SqlDatatypeAspect getSqlAspect( final EObject obj ) {
        if (obj != null && obj instanceof XSDSimpleTypeDefinition) {
            return (SqlDatatypeAspect)ModelerCore.getMetamodelRegistry().getMetamodelAspect(obj, SqlAspect.class);
        }
        return null;
    }

    static EObject resolveWhenProxy( EObject e ) {
        EObject resolvedEObject = e;
        if (e.eIsProxy()) {
            resolvedEObject = EcoreUtil.resolve(e, getContainer());
            if (resolvedEObject.eIsProxy()) {
                throw new TeiidRuntimeException(
                                                ModelerCore.Util.getString("ModelEditorImpl.Error_EObject_can_not_be_a_proxy", resolvedEObject.toString())); //$NON-NLS-1$
            }
        }
        return resolvedEObject;
    }

    public class EnterpriseDatatypeInfoSetCommand extends AbstractOverrideableCommand {

        private EnterpriseDatatypeInfo oldEdtInfo;
        private final EnterpriseDatatypeInfo newEdtInfo;
        private final XSDSimpleTypeDefinition simpleType;

        public EnterpriseDatatypeInfoSetCommand( final EditingDomain ed,
                                                 final XSDSimpleTypeDefinition simpleType,
                                                 final EnterpriseDatatypeInfo newEdtInfo,
                                                 final EnterpriseDatatypeInfo oldEdtInfo ) {
            super(ed);
            this.simpleType = simpleType;
            this.newEdtInfo = newEdtInfo;
            this.oldEdtInfo = oldEdtInfo;
        }

        protected void invokeSetOperation( EnterpriseDatatypeInfo edtInfo ) {
            ModelEditorImpl.getSqlAspect(this.simpleType).setEnterpriseDataAttributes((XSDSimpleTypeDefinition)ModelEditorImpl.resolveWhenProxy(this.simpleType),
                                                                                      edtInfo);
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doExecute()
         */
        @Override
        public void doExecute() {
            invokeSetOperation(this.newEdtInfo);
            this.simpleType.eNotify(new ENotificationImpl(
                                                          (InternalEObject)this.simpleType,
                                                          Notification.SET,
                                                          this.simpleType.eClass().getEStructuralFeature(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME),
                                                          this.oldEdtInfo, this.newEdtInfo));
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doRedo()
         */
        @Override
        public void doRedo() {
            invokeSetOperation(this.newEdtInfo);
            this.simpleType.eNotify(new ENotificationImpl(
                                                          (InternalEObject)this.simpleType,
                                                          Notification.SET,
                                                          this.simpleType.eClass().getEStructuralFeature(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME),
                                                          this.oldEdtInfo, this.newEdtInfo));
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doUndo()
         */
        @Override
        public void doUndo() {
            invokeSetOperation(this.oldEdtInfo);
            this.simpleType.eNotify(new ENotificationImpl(
                                                          (InternalEObject)this.simpleType,
                                                          Notification.UNSET,
                                                          this.simpleType.eClass().getEStructuralFeature(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME),
                                                          this.newEdtInfo, this.oldEdtInfo));
        }

        /**
         * @see org.eclipse.emf.common.command.AbstractCommand#prepare()
         */
        @Override
        protected boolean prepare() {
            if (this.simpleType == null || this.newEdtInfo == null || this.domain == null) {
                this.isExecutable = false;
                return false;
            }

            this.isExecutable = true;
            this.isPrepared = true;
            return true;
        }
    }

    public class EnterpriseDatatypeInfoUnsetCommand extends AbstractOverrideableCommand {

        private EnterpriseDatatypeInfo oldEdtInfo;
        private final XSDSimpleTypeDefinition simpleType;

        public EnterpriseDatatypeInfoUnsetCommand( final EditingDomain ed,
                                                   final XSDSimpleTypeDefinition simpleType,
                                                   final EnterpriseDatatypeInfo oldEdtInfo ) {
            super(ed);
            this.simpleType = simpleType;
            this.oldEdtInfo = oldEdtInfo;
        }

        protected void invokeUnsetOperation() {
            ModelEditorImpl.getSqlAspect(this.simpleType).unSetEnterpriseDataAttributes((XSDSimpleTypeDefinition)ModelEditorImpl.resolveWhenProxy(this.simpleType));
        }

        protected void invokeSetOperation( EnterpriseDatatypeInfo edtInfo ) {
            ModelEditorImpl.getSqlAspect(this.simpleType).setEnterpriseDataAttributes((XSDSimpleTypeDefinition)ModelEditorImpl.resolveWhenProxy(this.simpleType),
                                                                                      edtInfo);
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doExecute()
         */
        @Override
        public void doExecute() {
            invokeUnsetOperation();
            this.simpleType.eNotify(new ENotificationImpl(
                                                          (InternalEObject)this.simpleType,
                                                          Notification.UNSET,
                                                          this.simpleType.eClass().getEStructuralFeature(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME),
                                                          this.oldEdtInfo, null));
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doRedo()
         */
        @Override
        public void doRedo() {
            invokeUnsetOperation();
            this.simpleType.eNotify(new ENotificationImpl(
                                                          (InternalEObject)this.simpleType,
                                                          Notification.UNSET,
                                                          this.simpleType.eClass().getEStructuralFeature(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME),
                                                          this.oldEdtInfo, null));
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doUndo()
         */
        @Override
        public void doUndo() {
            invokeSetOperation(this.oldEdtInfo);
            this.simpleType.eNotify(new ENotificationImpl(
                                                          (InternalEObject)this.simpleType,
                                                          Notification.SET,
                                                          this.simpleType.eClass().getEStructuralFeature(XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__NAME),
                                                          null, this.oldEdtInfo));
        }

        /**
         * @see org.eclipse.emf.common.command.AbstractCommand#prepare()
         */
        @Override
        protected boolean prepare() {
            if (this.simpleType == null || this.domain == null) {
                this.isExecutable = false;
                return false;
            }

            this.isExecutable = true;
            this.isPrepared = true;
            return true;
        }
    }

    public class AddValueToMapCommand extends AbstractOverrideableCommand {

        private Object owner;
        private Map map;
        private Object key;
        private Object oldValue;
        private Object newValue;

        public AddValueToMapCommand( final EditingDomain ed,
                                     final Object owner,
                                     final Map map,
                                     final Object key,
                                     final Object newValue ) {
            super(ed);
            this.owner = owner;
            this.map = map;
            this.key = key;
            this.newValue = newValue;
            if (map != null) {
                this.oldValue = map.get(key);
            }
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doExecute()
         */
        @Override
        public void doExecute() {
            this.map.put(this.key, this.newValue);
            if (this.owner instanceof EObject) {
                ((EObject)this.owner).eNotify(new ENotificationImpl((InternalEObject)this.owner, Notification.ADD, null,
                                                                    this.oldValue, this.newValue));
            }
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doRedo()
         */
        @Override
        public void doRedo() {
            this.map.put(this.key, this.newValue);
            if (this.owner instanceof EObject) {
                ((EObject)this.owner).eNotify(new ENotificationImpl((InternalEObject)this.owner, Notification.ADD, null,
                                                                    this.oldValue, this.newValue));
            }
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doUndo()
         */
        @Override
        public void doUndo() {
            if (this.oldValue != null) {
                this.map.put(this.key, this.oldValue);
            } else {
                this.map.remove(this.key);
                if (this.key == null && this.owner instanceof XSDSchema) {
                    ((XSDSchema)this.owner).getElement().removeAttribute("xmlns"); //$NON-NLS-1$
                }
            }
            if (this.owner instanceof EObject) {
                ((EObject)this.owner).eNotify(new ENotificationImpl((InternalEObject)this.owner, Notification.REMOVE, null,
                                                                    this.newValue, this.oldValue != null ? this.oldValue : null));
            }
        }

        /**
         * @see org.eclipse.emf.common.command.AbstractCommand#prepare()
         */
        @Override
        protected boolean prepare() {
            if (this.map == null || this.newValue == null || this.domain == null) {
                this.isExecutable = false;
                return false;
            }

            this.isExecutable = true;
            this.isPrepared = true;
            return true;
        }
    }

    public class RemoveValueFromMapCommand extends AbstractOverrideableCommand {

        private Object owner;
        private Map map;
        private Object key;
        private Object oldValue;

        public RemoveValueFromMapCommand( final EditingDomain ed,
                                          final Object owner,
                                          final Map map,
                                          final Object key ) {
            super(ed);
            this.owner = owner;
            this.map = map;
            this.key = key;
            if (map != null) {
                this.oldValue = map.get(key);
            }
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doExecute()
         */
        @Override
        public void doExecute() {
            this.map.remove(this.key);
            if (this.key == null && this.owner instanceof XSDSchema) {
                ((XSDSchema)this.owner).getElement().removeAttribute("xmlns"); //$NON-NLS-1$
            }
            if (this.owner instanceof EObject) {
                ((EObject)this.owner).eNotify(new ENotificationImpl((InternalEObject)this.owner, Notification.REMOVE, null,
                                                                    this.oldValue, null));
            }
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doRedo()
         */
        @Override
        public void doRedo() {
            this.map.remove(this.key);
            if (this.key == null && this.owner instanceof XSDSchema) {
                ((XSDSchema)this.owner).getElement().removeAttribute("xmlns"); //$NON-NLS-1$
            }
            if (this.owner instanceof EObject) {
                ((EObject)this.owner).eNotify(new ENotificationImpl((InternalEObject)this.owner, Notification.REMOVE, null,
                                                                    this.oldValue, null));
            }
        }

        /**
         * @see org.eclipse.emf.edit.command.OverrideableCommand#doUndo()
         */
        @Override
        public void doUndo() {
            if (this.oldValue != null) {
                this.map.put(this.key, this.oldValue);
                if (this.owner instanceof EObject) {
                    ((EObject)this.owner).eNotify(new ENotificationImpl((InternalEObject)this.owner, Notification.ADD, null,
                                                                        null, this.oldValue));
                }
            }
        }

        /**
         * @see org.eclipse.emf.common.command.AbstractCommand#prepare()
         */
        @Override
        protected boolean prepare() {
            if (this.map == null || this.domain == null) {
                this.isExecutable = false;
                return false;
            }

            this.isExecutable = true;
            this.isPrepared = true;
            return true;
        }
    }

    public static class AddCommandFactory {
        public static AddCommand create( Object owner,
                                         EditingDomain domain,
                                         EList list,
                                         Collection collection,
                                         int index ) {
            if (owner instanceof XSDConcreteComponent) {
                return new XsdConcreteComponentAddCommand(domain, list, collection, index);
            }
            return new AddCommand(domain, list, collection, index);
        }
    }

    public static class CompoundCommandFactory {
        /**
         * Note that code using this method does not currently need functionality from the SafeRemoveCompoundCommand, so it just
         * returns a new CompoundCommand.
         */
        public static CompoundCommand create( int index ) {
            return new CompoundCommand(index);
        }

        public static CompoundCommand create( Object owner,
                                              List commands ) {
            if (owner instanceof XSDConcreteComponent) {
                return new SafeRemoveCompoundCommand(commands);

            } else if (owner instanceof Collection) {
                // scan all items to be deleted, looking for an XSDConcreteComp:
                Collection col = (Collection)owner;
                Iterator itor = col.iterator();
                while (itor.hasNext()) {
                    Object element = itor.next();
                    if (element instanceof XSDConcreteComponent) {
                        // found one, go ahead and accept it:
                        return new SafeRemoveCompoundCommand(commands);
                    } // endif -- XSDConcreteComp
                } // endwhile -- items in collection
            } // endif -- need special compoundCommand

            return new CompoundCommand(commands);
        }
    }

    static class Tester {

        protected void cloneFilePassOneSummary( File originalProject,
                                                File originalFile,
                                                Map modelToReferenceMap,
                                                int xmiUuidCount,
                                                int newUuidCount,
                                                int xProjectHRefCounter ) {

        }

        protected void cloneFile1( File originalProject,
                                   File originalFile,
                                   Map modelToReferenceMap,
                                   char[] buf,
                                   int bufLen,
                                   int bufNdx,
                                   char chr,
                                   char state,
                                   StringBuffer elemNameBuilder,
                                   StringBuffer attrNameBuilder,
                                   StringBuffer attrValBuilder,
                                   boolean href,
                                   boolean choiceCriteria ) {
        }

        protected void cloneFilePassTwoSummary( File originalProject,
                                                File originalFile,
                                                int replacedUuidCount ) {

        }

        protected void cloneFile2( File originalProject,
                                   File originalFile,
                                   Map modelToReferenceMap,
                                   char[] buf,
                                   int bufLen,
                                   int inBufNdx,
                                   char chr,
                                   char state,
                                   StringBuffer elemNameBuilder,
                                   StringBuffer attrNameBuilder,
                                   StringBuffer attrValBuilder,
                                   boolean href,
                                   boolean choiceCriteria,
                                   File clonedFile,
                                   CharArrayWriter testWriter,
                                   boolean uuid,
                                   int outBufNdx ) {
        }

        protected void cloningFile( File file ) {
        }

        protected void writeToOut( char[] charBuff,
                                   int startIndex,
                                   int length ) {
        }

        protected void writeToOut( String str ) {

        }
    }
}
