/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.teiid.core.id.ObjectID;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.MMXmiResource;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;

/**
 * ModelEditor
 */
public interface ModelEditor {

	/**
     * Return true if the feature on the given EObject requires a XSDSimpleType as it's value
     * @param EObject owner of the given SF
     * @param feature to check if it requires XSD ST value.
     * @return true if the feature on the given EObject requires a XSDSimpleType as it's value
     */
    public boolean isDatatypeFeature(EObject object, EStructuralFeature feature);

    /**
     * Returns true if the two EObject instances are considered equal.
     * @param eObject1
     * @param eObject2
     * @return
     */
    boolean equals( EObject eObject1, EObject eObject2 );

    /**
     * Return a collection of EStringToStringMapEntryImpl elements
     * that exist on the annotation for the given EObject.
     * Returns an empty list if no annotation exists or there are no
     * annotated properties for this object.
     * @param obj
     * @return collection of EStringToStringMapEntryImpl elements
     */
    Collection getTags(final EObject obj);

    /**<p>
     * Executes the specified TransactionRunnable within a transaction against the default model container.
     * </p>
     * @param runnable    The operation to run within a transaction.
     * @param description A description of the operation being executed.
     * @param significant Indicates whether the operation will be undoable.
     * @param source The source to be used if a new txn is required
     * @return The results of the operation.
     * @since 4.0
     */
    Object executeAsTransaction(TransactionRunnable runnable,
                                String description,
                                boolean significant,
                                Object source) throws ModelerCoreException;

    /**<p>
     * Executes the specified TransactionRunnable within a transaction against the specified model container.
     * </p>
     * @param runnable    The operation to run within a transaction.
     * @param container   The container to which this transaction applies.
     * @param description A description of the operation being executed.
     * @param significant Indicates whether the operation will be undoable.
     * @param source The source to be used if a new txn is required
     * @return The results of the operation.
     * @since 4.0
     */
    Object executeAsTransaction(TransactionRunnable runnable,
                                Container container,
                                String description,
                                boolean significant,
                                Object source) throws ModelerCoreException;

    /**<p>
     * Executes the specified TransactionRunnable within a transaction against the default model container.
     * </p>
     * @param runnable    The operation to run within a transaction.
     * @param description A description of the operation being executed.
     * @param significant Indicates whether the operation's undoable (if applicable) will show in the undo menu.
     * @param undoable Indicates wheter the operation will be undoable
     * @param source The source to be used if a new txn is required
     * @return The results of the operation.
     * @since 4.0
     */
    Object executeAsTransaction(TransactionRunnable runnable,
                                String description,
                                boolean significant,
                                boolean undoable,
                                Object source) throws ModelerCoreException;

    /**
     * Return the {@link ModelResource} that is referenced by the
     * {@link ModelImport}.
     * @param modelImport the ModelImport whose resource is to be returned; may not be null
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    ModelResource findModelResource( final ModelImport modelImport );

    /**
     * Find the object with the given path in the specified model.
     * @param resource the resource in which to find the object; may not be null
     * @param modelRelativePath the path that is relative to the model
     * (i.e., the first segment is <i>not</i> the model name)
     * @return the model object at the path, or null if no object with
     * a matching path could be found
     * @see #findObjectByPath(ModelResource, IPath)
     */
    EObject findObjectByPath( Resource resource, IPath modelRelativePath );

    /**
     * Find the object with the given path in the specified model.
     * @param resource the resource in which to find the object; may not be null
     * @param modelRelativePath the path that is relative to the model
     * (i.e., the first segment is <i>not</i> the model name)
     * @return the model object at the path, or null if no object with
     * a matching path could be found
     * @throws ModelWorkspaceException if there is an error obtaining the contents
     * of the resource
     * @see #findObjectByPath(Resource, IPath)
     */
    EObject findObjectByPath( ModelResource resource, IPath modelRelativePath ) throws ModelWorkspaceException;

    /**
     * Return the path of the supplied object relative to the model.  That is, the first segment of the
     * path will <i>not</i> contain the name of the model.
     * @param object the EObject
     * @return the path of the object relative to the model, including unnamed objects.
     * @see #getModelRelativePathIncludingModel(EObject)
     */
    IPath getModelRelativePath( EObject object );

    /**
     * Return the path of the supplied object relative to the model.  That is, the first segment of the
     * path will <i>not</i> contain the name of the model.
     * @param object the EObject
     * @param includeUnnamedObjects True if unnamed objects should appear in the path (such as XML Document/XSD sequences).
     * @return the path of the object relative to the model
     * @see #getModelRelativePathIncludingModel(EObject)
     */
    IPath getModelRelativePath(EObject object,
                               boolean includeUnnamedObjects);

    /**
     * Return the path of the supplied object starting at the model.  That is, the first segment of the
     * path <i>will</i> contain the name of the model.
     * @param object the EObject
     * @return the path of the object within (and including) the model name, including unnamed objects.
     * @see #getModelRelativePath(EObject)
     */
    IPath getModelRelativePathIncludingModel( EObject object );

    /**
     * Return the path of the supplied object starting at the model.  That is, the first segment of the
     * path <i>will</i> contain the name of the model.
     * @param object the EObject
     * @param includeUnnamedObjects True if unnamed objects should appear in the path (such as XML Document/XSD sequences).
     * @return the path of the object within (and including) the model name
     * @see #getModelRelativePath(EObject)
     */
    IPath getModelRelativePathIncludingModel(EObject object,
                                             boolean includeUnnamedObjects);

    /**
     * Return the path to the parent of the supplied object, starting at the project.  That is, the first segment of the
     * path <i>will</i> contain the name of the project.
     * @param object the EObject
     * @return the path of the object within (and including) the model name
     * @see #getModelRelativePath(EObject)
     */
    IPath getFullPathToParent( EObject object );

    /**
     * Return the name of the model for the supplied object.  Note that in the special case of
     * transient diagrams, the name of the model that contains the target of the diagram is actually
     * returned.
     * @param object the EObject; may not be null
     * @return the name of the model for the given EObject; may be null if the object doesn't exist
     * in a resource.
     */
    String getModelName( EObject object );

    /**
     * Return the name of the model for the supplied EMF resource.
     * @param resource the Resource; may not be null
     * @return the name of the model in the given resource
     */
    String getModelName( Resource resource );

    /**
     * Return the name of the model for the supplied ModelResource.
     * @param modelResource the ModelResource; may not be null
     * @return the name of the model in the given ModelResource
     */
    String getModelName( ModelResource modelResource );

    /**
     * Get the URI to the supplied object.
     * @param object the EObject; may not be null.
     * @return the URI for the object
     */
    URI getUri(EObject object);

    /**
     * Get the unique identifier for the supplied object.  If the object is a
     * {@link java.lang.reflect.Proxy Proxy}, then the returned ID is that of the proxy (which is also what
     * the underlying resource maintains as a unique identifier for the object, if the resource does so).
     * However, if the object is not a Proxy, then the result is the
     * {@link org.eclipse.emf.common.util.URI URI} for the EObject.
     * @param object the EObject; may not be null.
     * @return the identifier object
     */
    ObjectID getObjectID( EObject object );

    String getObjectIdString( EObject object );

    void setObjectID( EObject object,
	                  ObjectID objectId );

    void setObjectID( EObject object,
	                  String objectId );

    /**
     * Get the object that corresponds to the identifier.  The identifier
     * must be the same as returned from {@link #getObjectID(EObject)} or
     * {@link #getURI(EObject)}.
     * @param objectId the {@link URI uri} or {@link com.metamatrix.core.id.ObjectID object}
     * identifier for the object; may not be null
     * @return the EObject; may be null if an object with that identifier could not be found
     * @throws ModelerCoreException if there is an unexpected while trying to locate the object
     * (this is not thrown if the object cannot be found, but rather is a signal of something wrong)
     */
    EObject findObject( Object objectId ) throws ModelerCoreException;

    /**
     * Get the object that corresponds to the unique identifier.  The unique identifier
     * must be the same as returned from {@link #getObjectID(EObject)}.
     * @param objectId the {@link URI uri} or {@link com.metamatrix.core.id.ObjectID object}
     * identifier for the object; may not be null
     * @param resource the model resource in which the object exists; may not be null
     * @param monitor the progress monitor that may be used if the resource needs to be opened;
     * may be null if there is no monitor
     * @return the EObject; may be null if an object with that identifier could not be found
     * @throws ModelerCoreException if there is an unexpected while trying to locate the object
     * (this is not thrown if the object cannot be found, but rather is a signal of something wrong)
     */
    EObject findObject( Object objectId, final ModelResource resource,
                        IProgressMonitor monitor ) throws ModelerCoreException;

    /**
     * Returns the object that was changed or otherwise affected per the supplied
     * notification.
     * @param notification the notification; may not be null
     * @return the object that was the target/notifier of the notification
     */
    Object getChangedObject( Notification notification );

    /**
     * Populate the initial model contents with an instance of the
     * specified {@link org.eclipse.emf.ecore.EClass} for this
     * {@link org.eclipse.emf.ecore.resource.Resource}
     * @param eClass the EClass to use
     */
    EObject createInitialModel(final Resource resource, final EClass eClass) throws ModelerCoreException;

    /**
     * Return the MetamodelDescriptor for the given EObject
     * @param object the object; may not be null
     * @return the MetamodelDescriptor for the given EObject
     */
    public MetamodelDescriptor getMetamodelDescriptor(EObject object);

    /**
     * Return the MetamodelDescriptor for the primary metamodel in the supplied resource
     * @param resource the model resource; may not be null
     * @return the MetamodelDescriptor for the given resource
     * @throws ModelWorkspaceException if the resource does not exist or if an
     *      exception occurs while accessing the information from the resource
     */
    public MetamodelDescriptor getPrimaryMetamodelDescriptor(ModelResource resource) throws ModelWorkspaceException;

    /**
     * Return the MetamodelDescriptor for the primary metamodel in the supplied resource
     * @param resource the EMF resource; may not be null
     * @return the MetamodelDescriptor for the given resource
     * @throws ModelWorkspaceException if the resource does not exist or if an
     *      exception occurs while accessing the information from the resource
     */
    public MetamodelDescriptor getPrimaryMetamodelDescriptor(Resource resource) throws ModelWorkspaceException;

    /**
     * This returns a collection of objects describing the different children
     * that can be added under the specified object.
     * @param eObject the EObject instance for which new child descriptors
     * are requested
     * @return the list of {@link Command} instances for new children.  Note that any Command that
     * should be disabled and not executed (i.e., because doing so would violate the
     * {@link EStructuralFeature#getUpperBound() maximum multiplicity} of the corresponding owner feature)
     * is actually wrapped by a {@link com.metamatrix.modeler.core.util.DisabledCommand} object.
     */
    Collection getNewChildCommands(final EObject eObject) throws ModelerCoreException;

    /**
     * This returns a collection of objects describing the different children
     * that can be added as root objects under the specified resource.
     * @param Resource instance for which new child descriptors
     * are requested
     * @return the list of {@link Command} instances for new roots.  Note that any Command that
     * should be disabled and not executed (i.e., because doing so would violate the
     * {@link EStructuralFeature#getUpperBound() maximum multiplicity} of the corresponding owner feature)
     * is actually wrapped by a {@link com.metamatrix.modeler.core.util.DisabledCommand} object.
     */
    Collection getNewRootObjectCommands(final Resource emfResource) throws ModelerCoreException;

    /**
     * Create a new child instance using the specified child command
     * @param the eObject parent for the new child - may not be null
     * @param command the new child command
     */
    EObject createNewChildFromCommand(final EObject parent, final Command command) throws ModelerCoreException;

    /**
     * Create a new child instance using the specified child command
     * @param the resource parent for the new root object - may not be null
     * @param command the new child command
     */
    EObject createNewRootObjectFromCommand(final Resource parentResource, final Command command) throws ModelerCoreException;

    /**
     * This returns a collection of objects that can be added as siblings of the
     * specified object.
     * @param eObject the EObject instance for which new sibling descriptors
     * are requested
     */
    Collection getNewSiblingCommands(final EObject eObject) throws ModelerCoreException;


    /**
     * Create a new sibling instance using the specified sibling descriptor
     * @param the sibling for which to create a new sibling
     * @param command the new child command
     */
    EObject createNewSiblingFromCommand(final EObject sibling, final Command command) throws ModelerCoreException;

    /**
     * This returns a collection of objects describing the different associations
     * that can be created between the specified list of objects.
     * @param eObjects the list of objects to be included in the
     * prospective association
     * @return applicable assocation descriptors
     */
    Collection getNewAssociationDescriptors(final List eObjects) throws ModelerCoreException;

    /**
     * Create a new association instance using the specified association descriptor. The
     * object that is returned may be used to obtain a UML aspect for display.
     * @param descriptor the new association descriptor to use
     * @return the EObject representing the association.
     */
    EObject createNewAssociationFromDescriptor(final Object descriptor) throws ModelerCoreException;

    /**
     * Returns a self-contained copy of the {@link org.eclipse.emf.ecore.EObject} instance.
     * @param eObject the object to copy.
     * @return the copy.
     * @see #copy(EObject, Map) for a form that maintains the map of original-to-copy
     */
    EObject copy(final EObject eObject) throws ModelerCoreException;

    /**
     * Returns a self-contained copy of the {@link org.eclipse.emf.ecore.EObject} instance,
     * and track which original objects were used to create the copies.
     * <p>
     * The map of originals to copies can be initially empty or null.  After this method is
     * complete, the map will have new entries with keys that include the <code>original</code> and each of the objects
     * contained under it, and values that are the associated copy.
     * </p><p>
     * If, however, the map is <i>not</i> empty when this method is called, then the copy operation
     * will replace any references (in the copies) to objects that are keys in the map with references
     * to objects that are the corresponding values in the map.  For example, the same map can be passed
     * to multiple calls to this method, and the same map will be used to track (and resolve) references.
     * </p>
     * @param original the object to copy.
     * @param originalsToCopies the map into which will be placed the entries detailing the copy for
     * each original object
     * @return the copy.
     * @see #copy(EObject) for a form that does not maintain the map of original-to-copy
     */
    EObject copy(final EObject original, final Map originalsToCopies) throws ModelerCoreException;

    /**
     * Return numCopies of self-contained copies of the {@link org.eclipse.emf.ecore.EObject} instance.
     * @param eObject the object to copy.
     * @param the number of copies to make
     * @return the copies.
     */
    public Collection copyMultiple(final EObject eObject, int numCopies) throws ModelerCoreException;

    /**
     * Delete the specified {@link org.eclipse.emf.ecore.EObject} instance
     * and any references to it from its resource.
     * @param eObject the EObject to delete
     */
    boolean delete(final EObject eObject) throws ModelerCoreException;

    /**
     * Delete the specified {@link org.eclipse.emf.ecore.EObject} instance
     * and any references to it from its resource.  If the performResourceCheck
     * boolean is true, the specified EObject will be checked if it is contained
     * within a model resource, and if it is not contained within a resource, then
     * false is returned and the EObject is not deleted.  If the performResourceCheck
     * boolean is false, then the EObject will be deleted without performing any resource
     * checks.  If performRelatedObjectCheck is true, a search of all related objects
     * in the workspace is performed and the related objects are delete.  If false,
     * the related objects check is bypassed.
     * @param eObject the EObject to delete
     * @param performResourceCheck indicates whether the EObject must be contained
     * within a model resource before it is deleted.
     * @param performRelatedObjectCheck indicates whether to search for all of the
     * related object an delete them as well.
     * @return true if the EObject was deleted otherwise return false;
     */
    boolean delete(final EObject eObject, boolean performResourceCheck, boolean performRelatedObjectCheck) throws ModelerCoreException;

    /**
     * Delete the specified {@link org.eclipse.emf.ecore.EObject} instance
     * and any references to it from its resource.  If the performResourceCheck
     * boolean is true, the specified EObject will be checked if it is contained
     * within a model resource, and if it is not contained within a resource, then
     * false is returned and the EObject is not deleted.  If the performResourceCheck
     * boolean is false, then the EObject will be deleted without performing any resource
     * checks.
     * @param eObject the EObject to delete
     * @param performResourceCheck indicates whether the EObject must be contained
     * within a model resource before it is deleted.
     * @return true if the EObject was deleted otherwise return false;
     */
    boolean delete(final EObject eObject, boolean performResourceCheck) throws ModelerCoreException;

    /**
     * Delete the specified eObjects and any references to it from its resource.
     * If the specified EObject is not contained within a model resource then the method
     * returns false and nothing is deleted.
     * @param eObject the EObject to delete
     * @return true if the EObject was deleted otherwise return false;
     */
    boolean delete(final Collection eObjects) throws ModelerCoreException;


    /**
     * Delete the specified eObjects and any references to it from its resource.
     * and any references to it from its resource.
     * @param eObject the EObject to delete
     * @param monitor the ProgressMonitor
     * @return true if the EObject was deleted otherwise return false;
     */
    boolean delete(final Collection eObjects, final IProgressMonitor monitor) throws ModelerCoreException;

    /**
     * Find related objects to be deleted.  This method only processes the same model in which the deleted
     * object exists.
     * @param eObject the original object that is being deleted; never null
     * @return the collection of all objects that are being deleted
     */
    Collection findOtherObjectsToBeDeleted( final EObject eObject ) throws ModelerCoreException;

    /**
     * Find related objects to be deleted.  This method processes all models in which the object being
     * deleted is referenced.
     * @param eObject the original object that is being deleted; never null
     * @param modelContents the {@link ModelContents} for the {@link Resource} that contains the EObject; never null
     * @param editingDomain the editing domain; never null
     * @param additionalCommands the list into which any additional delete commands should be placed; never null
     * @return the collection of all objects that are being deleted
     */
    Collection findOtherObjectsToBeDeleted(final Collection eObjects, final EditingDomain editingDomain,
                                                final List additionalCommands,
                                                final ModelWorkspaceSearch workspaceSearch) throws ModelerCoreException;

    /**
     * Find external references to the deleted objects.  The method will process the original model
     * and all imported models checking for references to either the original
     * object being deleted or one of the members contained in the list of objects being deleted.
     * @param eObject the original object that is being deleted; never null
     * @param allDeleted the collection of all related objects that are being deleted
     * @return the collection of all objects that reference the original object or a member of the list
     * of deleted objects.
     */
    Collection findExternalReferencesToObjectsBeingDeleted( final EObject eObject, final Collection allDeleted ) throws ModelerCoreException;

    /**
     * Find references to the deleted objects and remove/unset them.  <i>Note: This method only finds
     * all references from undeleted objects.
     * @param allDeleted the EObjects being deleted; never null
     * @param editingDomain the editing domain; never null
     * @param additionalCommands the list into which any additional delete commands should be placed; never null
     */
    void findReferencesToObjectsBeingDeleted(final Collection allDeleted,
                                                        final EditingDomain editingDomain,
                                                        final List additionalCommands,
                                                        final ModelWorkspaceSearch workspaceSearch)
                                                        throws ModelerCoreException;

    /**
     * Find related objects to the objects in the objects list and return them.
     * @param objects the EObjects to the find related objects to
     * @param editingDomain the editing domain
     * @return the list of all objects that are related to the original object or a member of the list
     * of objects.
     */
    List findRelatedObjects(final Collection objects, final EditingDomain domain);

    /**
     * Rename the specified {@link org.eclipse.emf.ecore.EObject} instance
     * assuming a "name" feature for the associated EClass.
     * @param eObject the EObject to rename
     * @return true if the object was renamed, or false if the object was not renamed
     * @see #hasName(EObject)
     * @see #getNameFeature(EObject)
     * @see #getName(EObject)
     */
    boolean rename(final EObject eObject, final String newName) throws ModelerCoreException;

    /**
     * Get the "name" of the specified {@link org.eclipse.emf.ecore.EObject} instance, if
     * the associated {@link EClass} has a "name" feature.
     * @param eObject the EObject for which the name is to be found
     * @return the name value, if there is one, or null if there is no name feature
     * @see #hasName(EObject)
     * @see #getNameFeature(EObject)
     * @see #rename(EObject, String)
     */
    String getName(final EObject eObject);

    /**
     * Determine whether the specified {@link org.eclipse.emf.ecore.EObject} instance
     * has a "name" feature.
     * @param eObject the EObject
     * @return true if the {@link EClass} has a "name" feature, or false otherwise.
     * @see #getNameFeature(EObject)
     * @see #getName(EObject)
     * @see #rename(EObject, String)
     */
    boolean hasName(final EObject eObject);

    /**
     * Obtain the "name" feature for the specified {@link org.eclipse.emf.ecore.EObject} instance.
     * @param eObject the EObject
     * @return the {@link EStructuralFeature feature} that represents the "name" feature, or null
     * if there is no such feature.
     * @see #hasName(EObject)
     * @see #getName(EObject)
     * @see #rename(EObject, String)
     */
    EStructuralFeature getNameFeature(final EObject eObject);

    /**
     * Execute the given command.
     * This method provides declarative txn support (txn will be created if neccessary)
     * @param cmd Command to execute
     * @param owner an EObject owner for this command (used to determine container and emf resource context)
     * @throws ModelerCoreException if the command is null or canExecute returns false
     * @return returns an undoable edit if txn does not already exist... else return null allowing
     * the user to get the undoable edit when the commit the existing txn.
     */
    void executeCommand(final EObject owner, final Command cmd) throws ModelerCoreException;

    /**
     * Move the given eObject to the end of the newParent's child list
     * @param newParent
     * @param eObject
     * @return true if success
     * @throws ModelerCoreException
     */
    public boolean move(final Object newParent, final EObject eObject)throws ModelerCoreException;

    /**
     * Move the given eObject to the given index in the newParent's child list
     * @param newParent
     * @param eObject
     * @return true if success
     * @throws ModelerCoreException
     */
    public boolean move(final Object newParent, final EObject eObject, int index)throws ModelerCoreException;

    /**
     * Copy the given eObject to the editing domain's clipboard
     * @param eObject
     * @throws ModelerCoreException
     */
    public void copyToClipboard(final EObject eObject) throws ModelerCoreException;

    /**
     * Copy the given eObjects to the editing domain's clipboard
     * @param eObject
     * @throws ModelerCoreException
     */
    public void copyAllToClipboard(final Collection eObjects) throws ModelerCoreException;

    /**
     * Cut the given eObject to the editing domain's clipboard
     * @param eObject
     * @throws ModelerCoreException
     */
    public void cutToClipboard(final EObject eObject) throws ModelerCoreException;

    /**
     * Cut the given eObjects to the editing domain's clipboard
     * @param eObject
     * @throws ModelerCoreException
     */
    public void cutAllToClipboard(final Collection eObjects) throws ModelerCoreException;

    /**
     * Paste from the editing domain's clipboard as the last child of the given eObject
     * @param Object - owner is parent of paste
     * @return true if success
     * @throws ModelerCoreException
     */
    public boolean pasteFromClipboard(final Object owner) throws ModelerCoreException;

    /**
     * Obtain a Collection of objects on the editing domain's clipboard
     * @param target the target of a possible paste from the clipboard, necessary to obtain the correct
     * editing domain context for the clipboard.
     * @return an unmodifiable collection of objects from the clipboard
     * @throws ModelerCoreException
     */
    public Collection getClipboardContents(final Object target) throws ModelerCoreException;

    /**
     * If the {@link #getClipboardContents(Object) clipboard} contents were populated by copying other
     * objects, then this method will return a mapping from the
     * {@link #getClipboardContents(Object) clipboard contents} to the originals.
     * @return a map that returns the copied object (on the clipboard) given an original; may be null
     * if the clipboard is empty or if the clipboard contents were populated by cutting or through
     * some non-copying command
     * @throws ModelerCoreException
     * @see #getClipboardContentsCopyToOriginalMapping(Object)
     */
    public Map getClipboardContentsOriginalToCopyMapping(final Object target) throws ModelerCoreException;

    /**
     * If the {@link #getClipboardContents(Object) clipboard} contents were populated by copying other
     * objects, then this method will return a mapping from the
     * {@link #getClipboardContents(Object) clipboard contents} to the originals.
     * @return a map that returns the original object given an object on the clipboard; may be null
     * if the clipboard is empty or if the clipboard contents were populated by cutting or through
     * some non-copying command
     * @throws ModelerCoreException
     * @see #getClipboardContentsOriginalToCopyMapping(Object)
     */
    public Map getClipboardContentsCopyToOriginalMapping(final Object target) throws ModelerCoreException;

    /**
     * Return a copy of all of the eObjets in the given collection
     * @param eObject
     * @return the copied objects
     * @throws ModelerCoreException
     * @see #copyAll(Collection,Map) for a form that maintains the map of original-to-copy
     */
    public Collection copyAll(final Collection eObjects) throws ModelerCoreException;

    /**
     * Return a copy of all of the eObjets in the given collection, and track which original objects
     * were used to create the copies.
     * <p>
     * The map of originals to copies can be initially empty or null.  After this method is
     * complete, the map will have new entries with keys that include the <code>originals</code> and each of the objects
     * contained under it, and values that are the associated copy.
     * </p><p>
     * If, however, the map is <i>not</i> empty when this method is called, then the copy operation
     * will replace any references (in the copies) to objects that are keys in the map with references
     * to objects that are the corresponding values in the map.  For example, the same map can be passed
     * to multiple calls to this method, and the same map will be used to track (and resolve) references.
     * </p>
     * @param originals the collection of original objects that are to be copied; may be null or empty, although
     * in such cases the result will always be an empty collection
     * @param originalsToCopies the map into which will be placed the entries detailing the copy for
     * each original object
     * @return the copied objects
     * @throws ModelerCoreException
     * @see #copyAll(Collection) for a form that does not maintain the map of original-to-copy
     */
    public Collection copyAll(final Collection eObjects, final Map originalsToCopies ) throws ModelerCoreException;

    /**
     * Clone the given eObject
     * @param eObject
     * @return true if success
     * @throws ModelerCoreException
     */
    public EObject clone(final EObject eObject) throws ModelerCoreException;

    /**
     * Return numClones of the {@link org.eclipse.emf.ecore.EObject}
     * instance. The clones will be exact copies of the original
     * including copies of its contents and its references.
     * @param eObject the object to clone.
     * @param numClones the number of clones to create
     * @return the clones.
     */
    public Collection cloneMultiple(final EObject eObject, int numClones) throws ModelerCoreException;

	public void cloneProject( String originalProjectPath,
	                          String clonedProjectPath ) throws IOException;

    /**
     * Return true if the child may be added to this parent
     * @param parent
     * @param child
     * @return true if the child may be added to this parent
     */
    public boolean isValidParent(final Object parent, final EObject child);

    /**
     * Return true if all items on the clipboard are pastable under the given parent
     * @param potentialParent
     * @return true if all items on the clipboard are pastable under the given parent
     */
    public boolean isValidPasteParent(final Object potentialParent);

    /**
     * This method will return the list of containers defined within a given <code>ModelResource</code>
     * Applicable containers are defined by the specific Metamodel type.
     * @param resource
     * @return
     * @since 4.3
     */
    public Collection getAllContainers(Resource resource);

    /**
     * Return the model annotation object; that is, the object that represents the model.
     * @param eObject the model object; may not be null
     * @return the model annotation; null if there is no model annotation
     * @throws ModelerCoreException if there is an error getting the annotation
     */
    public ModelAnnotation getModelAnnotation( final EObject eObject ) throws ModelerCoreException;

    /**
     * Return the description for the supplied model object.
     * @param eObject the model object; may not be null
     * @param forceCreate true if the annotation should be created if one does not exist
     * @return the annotation, or null if there is no annotation and <code>forceCreate</code> was false
     * @throws ModelerCoreException if there is an error getting the annotation
     */
    public Annotation getAnnotation( final EObject eObject, boolean forceCreate ) throws ModelerCoreException;

    /**
     * Return the {@link Annotation} for the supplied model object.  This method takes the
     * {@link ModelResource model resource} that contains the model object; if known, supplying it will
     * provide faster response.
     * @param modelResource the model resource, if known; may be null
     * @param eObject the model object; may not be null
     * @param forceCreate true if the annotation should be created if one does not exist
     * @return the annotation, or null if there is no annotation and <code>forceCreate</code> was false
     * @throws ModelerCoreException if there is an error getting the annotation
     */
    public Annotation getAnnotation( final ModelResource modelResource,
                                     final EObject eObject, boolean forceCreate ) throws ModelerCoreException;

    /**
     * Return the description for the supplied model object.
     * @param eObject the model object; may not be null
     * @return the string containing the description, or null if there is no description
     * @throws ModelerCoreException if there is an error getting the description
     */
    public String getDescription( final EObject eObject ) throws ModelerCoreException;

    /**
     * Return the description for the supplied model object.
     * @param eObject the model object; may not be null
     * @return the string containing the description, or null if there is no description
     * @throws ModelerCoreException if there is an error getting the description
     */
    public void setDescription( final EObject eObject, final String desc ) throws ModelerCoreException;

    /**
     * Return the {@link ModelResource} that contains the opened {@link Resource EMF resource}.
     * @param resource the EMF resource; may not be null
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     */
    public ModelResource findModelResource( final Resource resource );

    /**
     * Return the object that represents the extended information for the supplied model object.
     * @param eObject the model object; may not be null
     * @return the object that is the extension; null if no extension object exists or applies for the
     * supplied object.
     * @throws ModelerCoreException if there is an error getting the extension
     */
    public EObject getExtension( final EObject eObject ) throws ModelerCoreException;

    /**
     * Return the {@link ModelResource} that contains the {@link EObject model object}.
     * Note that in the special case of transient diagrams, the model resource returned is
     * that which contains the {@link com.metamatrix.metamodels.diagram.Diagram#getTarget() target}
     * of the transient diagram.
     * @param eObject the model object; may not be null
     * @return the ModelResource; null only if the model object's {@link Resource EMF resource}
     * is not known to the {@link ModelWorkspace} (that is, does not belong to a resource)
     */
    public ModelResource findModelResource( final EObject eObject );

    /**
     * Return the {@link Resource} that contains the {@link EObject model object}.
     * If the eObject does not have a reference to a Resource and the eObject is an eProxy
     * the method will attempt to resolve the EObject in the specified container.
     * @param eObject the model object; may not be null
     * @param container; the container used to resolve the eObject if it is a proxy
     * @return the EMF Resource; null only if the model object does not belong to a resource.
     */
    public Resource findResource( final Container container, final EObject eObject );

    /**
     * Return the {@link ModelResource} that contains the opened {@link IResource Eclipse file}.
     * Return the {@link Resource} that contains the {@link EObject model object}.
     * @param eObject the model object; may not be null
     * @param container; the container used to resolve the eObject if it is a proxy
     * @param resolve; if true the method will attempt to resolve the eProxy in the specified container,
     * otherwise the method will return null.
     * @return the EMF Resource; null only if the model object does not belong to a resource.
     */
    public Resource findResource( final Container container, final EObject eObject, final boolean resolve );

    /**
     * Return the {@link ModelResource} that contains the opened {@link org.eclipse.core.resources.IResource Eclipse file}.
     * @param resource the Eclipse resource; may not be null
     * @return the ModelResource; null only if the resource is not known to the {@link ModelWorkspace}.
     * @throws ModelWorkspaceException if there is an error obtaining the corresponding resource
     */
    public ModelResource findModelResource( final IFile resource ) throws ModelWorkspaceException;

    public ModelContents getModelContents( final EObject eObject );
    public ModelContents getModelContents( final Resource emfResource );
    public ModelContents getModelContents( final ModelResource modelResource );

    /**
     * Add a <code>ModelImport</code> instance for the specified "imported"
     * resource.  The "imported" resource represents a model that the
     * EMF resource depends upon.  If a dependency to the "imported" resource
     * already exists, no new ModelImport will be added.
     * @param resource the EMF resource to which the ModelImport will be added
     * @param importedResource the resource to be referenced in the ModelImport
     */
    boolean addModelImport( final MMXmiResource resource, final Resource importedResource ) throws ModelerCoreException;

    /**
     * Create a new <code>ModelImport</code> instance for the specified "imported"
     * resource. The "imported" resource represents a model that the EMF resource
     * depends upon. The new instance will <b>NOT</b> be added to the contents of
     * the resource.
     * @param resource the EMF resource for which a new ModelImport will be created
     * @param importedResource the resource to be referenced in the ModelImport
     */
    ModelImport createModelImport( final MMXmiResource resource, final Resource importedResource ) throws ModelerCoreException;

    /**
     * Remove the <code>ModelImport</code> instance for the specified "imported"
     * resource.  The "imported" resource represents a model that the
     * EMF resource depends upon.  If a dependency to the "imported" resource
     * still exists, the ModelImport will not be removed.
     * @param resource the EMF resource containing the ModelImport to be removed
     * @param importedResource the resource referenced in the ModelImport to be removed.
     */
    void removeModelImport( final MMXmiResource resource, final Resource importedResource ) throws ModelerCoreException;

    /**
     * Update the model import information for the specified <code>ModelImport</code> instance.
     * @param modelImport the ModelImport instance to be updated; may not be null
     * @param importedResource the resource corresponding to this import
     */
    public void updateModelImport(final ModelImport modelImport, final Resource importedResource);

    /**
     * Return the <code>ModelImport</code> instance that matches the specified
     * resource.  The "imported" resource represents a model that the
     * EMF resource depends upon.  If no dependency to the "imported" resource
     * exists, then null is returned.
     * @param resource the EMF resource containing the ModelImports to be checked
     * @param importedResource the resource to find the ModelImport for
     * @return the ModelImport reference if it exists.
     */
    public ModelImport findModelImport(final MMXmiResource resource, final Resource importedResource);

    /**
     * Return the <code>ModelImport</code> instance that matches the specified model location
     * The "imported" resource represents a model that the EMF resource depends upon.  If no import dependency is found then null
     * is returned.
     * @param resource
     * @param someModelLocation
     * @return the ModelImport reference if it exists
     * @since 5.0.2
     */
    public ModelImport getExistingModelImportForLocation(final MMXmiResource resource, String someModelLocation );

    /**
     * Return the model location string for the imported resource.
     * @param resource
     * @param importedResource
     * @return the modelLocation string
     * @since 5.0.2
     */
    public String createModelLocation(final MMXmiResource resource, final Resource importedResource);

    /**
     * Tries to set the given value on the given eObject given feature
     * @param eObject
     * @param value
     * @param feature
     * @return true if successful
     */
    public boolean setPropertyValue(EObject eObject, Object value, Object feature);

    /**
     * Tries to set the given values on a simple datatype's enterprise extensions
     * @param owner - the owning EObject for the dom node.
     * @param value - Should be an instanceof EnterpriseDatatypeInfo
     * @return true if successful
     */
    public boolean setEnterpriseDatatypePropertyValue(EObject owner, Object value);

    /**
     * Tries to clear out the given simple datatype's enterprise extensions
     * @param owner - the owning EObject for the dom node.
     * @return true if successful
     */
    public boolean unsetEnterpriseDatatypePropertyValue(EObject owner);

    /**
     * Tries to set the given value on the given eObject using the specified
     * property descriptor.
     * @param eObject
     * @param value
     * @param descriptor
     * @return true if successful
     */
    public boolean setPropertyValue(EObject eObject, Object value, ItemPropertyDescriptor descriptor);

    /**
     * Tries to add the given value to the given owner's feature
     * @param Object to add new value : may not be null
     * @param value to add - May be a list or single item, but may not be null
     * @param feature EList from Object to add value, may not be null
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void addValue(final Object owner, final Object value, EList feature) throws ModelerCoreException;

    /**
     * Tries to add the given value to the given EObject's feature
     * @param eObject to add new value : may not be null
     * @param value to add - May be a list or single item, but may not be null
     * @param feature EList from eObject to add value, may not be null
     * @param index position to insert the element into the collection
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void addValue(final Object owner, final Object value, final EList feature, final int index) throws ModelerCoreException;

    /**
     * Tries to add the given value to the given map with the given key
     * @param eObject  : may not be null
     * @param map to add key/value pair - may not be null
     * @param key to add - May be null (depends on map implementation)
     * @param value to add - May be null (depends on map implementation)
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void addMapValue(final Object owner, final Map map, final Object key, final Object value) throws ModelerCoreException;

    /**
     * Tries to remove the value associated with the given key from the given map
     * @param eObject  : may not be null
     * @param map to remove key/value pair - may not be null
     * @param key to add - May be null (depends on map implementation)
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void removeMapValue(final Object owner, final Map map, final Object key) throws ModelerCoreException;

    /**
     * Tries to remove the given value from the given EObject's feature
     * @param eObject to remove new value from : may not be null
     * @param value to remove - May be a list or single item, but may not be null
     * @param feature EList from eObject to remove value, may not be null
     * @return
     * @throws ModelerCoreException if there is an error executing the command
     */
    public void removeValue(final Object owner, final Object value, EList feature) throws ModelerCoreException;
}
