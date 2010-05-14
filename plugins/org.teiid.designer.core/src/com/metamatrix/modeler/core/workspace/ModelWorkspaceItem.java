/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

/**
 * ModelWorkspaceItem is the base interface for all resource items in the Modeler's
 * Workspace model.  Model workspace items are exposed to clients as handles to the actual underlying element.
 * The model workspace may hand out any number of handles for each element. Handles
 * that refer to the same element are guaranteed to be equal, but not necessarily identical.
 * <p>
 * Methods annotated as "handle-only" do not require underlying elements to exist. 
 * Methods that require underlying elements to exist throw
 * a <code>ModelWorkspaceException</code> when an underlying element is missing.
 * <code>ModelWorkspaceException.isDoesNotExist</code> can be used to recognize
 * this common special case.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ModelWorkspaceItem extends IAdaptable {
    
    /**
     * Constant representing a Model Workspace (workspace level object).
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelWorkspace}.
     */
    int MODEL_WORKSPACE = 1;

    /**
     * Constant representing a modeling project.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelProject}.
     */
    int MODEL_PROJECT = 2;

//    /**
//     * Constant representing a model package fragment root.
//     * A model workspace item with this {@link #getItemType() type} can be safely 
//     * cast to {@link ModelPackageFragmentRoot}.
//     */
//    int MODEL_PACKAGE_FRAGMENT_ROOT = 3;

    /**
     * Constant representing a model folder.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelFolder}.
     */
    int MODEL_FOLDER = 4;

    /**
     * Constant representing a model file.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelResource}.
     */
    int MODEL_RESOURCE = 5;

    /**
     * Constant representing a model import container.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelImports}.
     */
    int MODEL_IMPORTS = 6;

    /**
     * Constant representing a model import container.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelImports}.
     */
    int METAMODEL_IMPORTS = 7;

    /**
     * Constant representing a container for diagrams.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelDiagrams}.
     */
    int DIAGRAMS = 8;

    /**
     * Constant representing a container for transformations.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelTransformations}.
     */
    int TRANSFORMATIONS = 9;

    /**
     * Constant representing a container for annotations.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelObjectAnnotations}.
     */
    int ANNOTATIONS = 10;

    /**
     * Constant representing a container for {@link MappingClassSet} objects.
     * A model workspace item with this {@link #getItemType() type} can be safely 
     * cast to {@link ModelMappingClassSets}.
     */
    int MAPPING_CLASS_SETS = 11;

    /*====================================================================
     * Constants defining the depth of resource tree traversal:
     *====================================================================*/
    
    /**
     * Depth constant (value 0) indicating this JdbcNode, but not any of its members.
     */
    public static final int DEPTH_ZERO = 0;

    /**
     * Depth constant (value 1) indicating this JdbcNode and its direct children.
     */
    public static final int DEPTH_ONE = 1;

    /**
     * Depth constant (value 2) indicating this JdbcNode and its direct and
     * indirect children at any depth.
     */
    public static final int DEPTH_INFINITE = 2;

    /**
     * Returns this item's kind encoded as an integer.
     * This is a handle-only method.
     *
     * @return the kind of item; one of the constants declared in
     *   {@link ModelWorkspaceItem}
     */
    int getItemType();
    
    /**
     * Returns the name of this element. This is a handle-only method.
     * @return the element name
     */
    String getItemName();
    
    /**
     * Returns the first openable parent. If this item is openable, the item
     * itself is returned. Returns <code>null</code> if this item doesn't have
     * an openable parent.
     * This is a handle-only method.
     * 
     * @return the first openable parent or <code>null</code> if this item doesn't have
     * an openable parent.
     */
    Openable getOpenable();

    /**
     * Return the model workspace that contains this item,
     * or <code>null</code> if this element is not contained in any model workspace
     * This is a handle-only method.
     * @return the workspace; may be null if it is not contained in a model workspace
     */
    ModelWorkspace getModelWorkspace();

    /**
     * Return the model project that contains this item,
     * or <code>null</code> if this element is not contained in any model project
     * This is a handle-only method.
     * @return the project; may be null if it is not contained in a model project
     */
    ModelProject getModelProject();

    /**
     * Returns the resource that corresponds directly to this element,
     * or <code>null</code> if there is no resource that corresponds to
     * this element.
     * <p>
     * For example, the corresponding resource for an {@link ModelResource}
     * is its underlying {@link org.eclipse.core.resources.IFile}. 
     * The corresponding resource for an {@link ModelPackageFragment}
     * that is not contained in an archive is its underlying 
     * {@link org.eclipse.core.resources.IFolder}. An {@link ModelPackageFragment}
     * contained in an archive has no corresponding resource.
     * <p>
     *
     * @return the corresponding resource, or <code>null</code> if none
     * @exception ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    IResource getCorrespondingResource() throws ModelWorkspaceException;

    /**
     * Returns the innermost Eclipse workspace {@link IResource resource} enclosing this element. 
     * If this element is included in an archive and this archive is not external, 
     * this is the underlying resource corresponding to the archive. 
     * If this element is included in an external archive, <code>null</code>
     * is returned.
     * If this element is a working copy, <code>null</code> is returned.
     * This is a handle-only method.
     * 
     * @return the innermost resource enclosing this element, <code>null</code> if this 
     * element is a working copy or is included in an external archive
     */
    IResource getResource();
    
    /**
     * Returns the smallest underlying resource that contains
     * this element, or <code>null</code> if this element is not contained
     * in a resource.
     *
     * @return the underlying resource, or <code>null</code> if none
     * @exception ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its underlying resource
     */
    IResource getUnderlyingResource() throws ModelWorkspaceException;

    /**
     * Returns the path to the innermost resource enclosing this element. 
     * If this element is not included in an external archive, 
     * the path returned is the full, absolute path to the underlying resource, 
     * relative to the workbench. 
     * If this element is included in an external archive, 
     * the path returned is the absolute path to the archive in the file system.
     * This is a handle-only method.
     * 
     * @return the path to the innermost resource enclosing this element
     */
    IPath getPath();

    /**
     * Returns whether this Java element is read-only. An element is read-only
     * if its structure cannot be modified by the java model. 
     * <p>
     * Note this is different from IResource.isReadOnly(). For example, .jar
     * files are read-only as the java model doesn't know how to add/remove 
     * elements in this file, but the underlying IFile can be writable.
     * <p>
     * This is a handle-only method.
     *
     * @return <code>true</code> if this element is read-only
     */
    boolean isReadOnly();

    /**
     * Returns whether this Model workspace item exists in the model.
     * <p>
     * Model workspace items are handle objects that may or may not be backed by an
     * actual element. Model workspace items that are backed by an actual element are
     * said to "exist", and this method returns <code>true</code>. For Model workspace items
     * that are not working copies, it is always the case that if the
     * item exists, then its parent also exists (provided it has one) and
     * includes the item as one of its children. It is therefore possible
     * to navigated to any existing Model workspace item from the root of the model workspace
     * along a chain of existing Model workspace items. On the other hand, working
     * copies are said to exist until they are destroyed (with
     * <code>IWorkingCopy.destroy</code>). Unlike regular Model workspace items, a
     * working copy never shows up among the children of its parent element
     * (which may or may not exist).
     * </p>
     *
     * @return <code>true</code> if this item exists in the Model workspace, and
     * <code>false</code> if this item does not exist
     */
    boolean exists();

    /**
     * Return the parent that directly contains this workspace item,
     * or <code>null</code> if this element has no parent.
     * This is a handle-only method.
     * @return the parent item, or <code>null</code> if this item has no parent
     */
    ModelWorkspaceItem getParent();

    /**
     * Return the objects that this workspace item contains.  The type of
     * objects returned depends upon the subtype of workspace item on which this
     * method is being called.
     * @return the immediate children of this item; never null
     */
    ModelWorkspaceItem[] getChildren() throws ModelWorkspaceException;
    
    /**
     * Return the {@link ModelResource model resource} for the specified resource and
     * contained by this project.
     * <p>
     * This method returns an object in {@link #getChildren()}.
     * </p>
     * @return the {@link ModelWorkspaceItem} instance contained by this project item that represents
     * the suppplied resource; may be null if the supplied resource doesn't represent a model or a folder
     * @throws ModelWorkspaceException
     */
    ModelWorkspaceItem getChild( String childName ) throws ModelWorkspaceException;

    /**
     * Return the {@link ModelResource model resource} for the specified resource and
     * contained by this project.
     * <p>
     * This method returns an object in {@link #getChildren()}.
     * </p>
     * @return the {@link ModelWorkspaceItem} instance contained by this project item that represents
     * the suppplied resource; may be null if the supplied resource doesn't represent a model or a folder
     * @throws ModelWorkspaceException
     */
    ModelWorkspaceItem getChild( IResource resource ) throws ModelWorkspaceException;

    /**
     * Returns whether this workspace item has one or more immediate children.
     * This is a convenience method, and may be more efficient than
     * testing whether {@link #getChildren()} is empty.
     *
     * @exception ModelWorkspaceException if this item does not exist or if an
     *      exception occurs while accessing its corresponding resource
     * @return true if there are immediate children of this item, false otherwise
     */
    boolean hasChildren() throws ModelWorkspaceException;

    /**
     * Returns whether the structure of this item is known. For example, for a
     * model resource that could not be parsed, <code>false</code> is returned.
     * If the structure of an element is unknown, navigations will return reasonable
     * defaults. For example, <code>getChildren</code> will return an empty collection.
     * <p>
     * Note: This does not imply anything about consistency with the
     * underlying resource/buffer contents.
     * </p>
     *
     * @return <code>true</code> if the structure of this element is known
     * @exception ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    boolean isStructureKnown() throws ModelWorkspaceException;

    /**
     * Accepts the given visitor.
     * The visitor's <code>visit</code> method is called with this node. 
     * If the visitor returns <code>false</code>, this {@link #getChildren() item's children}
     * are not visited.
     * <p>
     * The subtree under the given item is traversed to the supplied depth.
     * </p>
     * @param visitor the visitor; may not be null
     * @param depth the depth to which members of this resource should be
     *      visited.  One of <code>DEPTH_ZERO</code>, <code>DEPTH_ONE</code>,
     *      or <code>DEPTH_INFINITE</code>.
     * @throws IllegalArgumentException if the visitor is null or the depth is invalid
     * @throws ModelWorkspaceException if the visitor failed with this exception
     */
    public void accept( ModelWorkspaceVisitor visitor, int depth ) throws ModelWorkspaceException;
    
    /**
     * Returns whether this item is opening. 
     *
     * @return <code>true</code> if this item is opening
     */
    boolean isOpening();
    
    /**
     * Returns whether this item is closing. 
     *
     * @return <code>true</code> if this item is closing.
     */
    boolean isClosing();
    
}
