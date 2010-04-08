/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * The ModelWorkspaceView represents a filtered view of the {@link ModelWorkspace}, including the contents of models.
 */
public class ModelWorkspaceView {

    public static final boolean DEFAULT_RESTRICT_TO_MODEL_RESOURCES_ONLY = true;

    /**
     * A reusable filter that shows everything in the view.
     */
    public static final ModelWorkspaceFilter SHOW_ALL_FILTER = new ModelWorkspaceFilter() {
        public boolean select( Object parentElement,
                               Object element ) {
            return true;
        }
    };

    private static final Object[] EMPTY_CHILDREN = new Object[] {};

    final List filters;
    final ModelWorkspace workspace;
    private boolean restrictedToModelResourcesOnly;

    /**
     * Construct an instance of ModelWorkspaceView.
     */
    public ModelWorkspaceView() {
        super();
        this.filters = new LinkedList();
        this.workspace = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace();
        CoreArgCheck.isNotNull(this.workspace);
        this.restrictedToModelResourcesOnly = DEFAULT_RESTRICT_TO_MODEL_RESOURCES_ONLY;
    }

    /**
     * Return whether only {@link ModelWorkspaceItem} instances will be shown in the view, or whether other {@link IResource}
     * instances will also be shown. Of course, if any of the {@link #getModelWorkspaceFilters()} filter out non-ModelResource
     * instances, then they will not be visible regardless of this setting. By default, only ModelWorkspaceItems are visible (this
     * method returns true).
     * 
     * @return true if only ModelWorkspaceItem instances will be visible, or false if both ModelWorkspaceItems and other IResource
     *         instances will be visible.
     * @see #setRestrictedToModelWorkspaceItemsOnly(boolean)
     */
    public boolean isRestrictedToModelWorkspaceItemsOnly() {
        return restrictedToModelResourcesOnly;
    }

    /**
     * Set whether only {@link ModelWorkspaceItem} instances will be shown in the view, or whether other {@link IResource}
     * instances will also be shown. Of course, if any of the {@link #getModelWorkspaceFilters()} filter out non-ModelResource
     * instances, then they will not be visible regardless of this setting.
     * 
     * @param b true if only ModelWorkspaceItem instances will be visible, or false if both ModelWorkspaceItems and other
     *        IResource instances will be visible.
     * @see #isRestrictedToModelWorkspaceItemsOnly()
     */
    public void setRestrictedToModelWorkspaceItemsOnly( final boolean b ) {
        restrictedToModelResourcesOnly = b;
    }

    /**
     * Return the list of {@link ModelWorkspaceFilter} instances used by this view. The returned list should be modified to add or
     * remove filters. However, the view does not make any special effort when accessing the filters, so care must be taken to not
     * modify this list at the same time the view is using the filters.
     * 
     * @return the List of {@link ModelWorkspaceFilter} instances; never null
     */
    public List getModelWorkspaceFilters() {
        return filters;
    }

    protected boolean isVisible( final Object parent,
                                 final Object object ) {
        final Iterator iter = this.filters.iterator();
        while (iter.hasNext()) {
            final ModelWorkspaceFilter filter = (ModelWorkspaceFilter)iter.next();
            if (filter.select(parent, object)) {
                return true;
            }
        }
        // no filter said the object was visible
        return false;
    }

    /**
     * Get the current path for the supplied object.
     * 
     * @param object the object for which the path is to be determined; may not be null
     * @return the path for the supplied object; null if the path could not be found or determined for the supplied object
     */
    public IPath getPath( final Object object ) {
        if (object instanceof ModelWorkspaceItem) {
            return ((ModelWorkspaceItem)object).getPath();
        }
        if (object instanceof EObject) {
            final EObject eObject = (EObject)object;
            final ModelEditor editor = ModelerCore.getModelEditor();
            // Find the model resource for the model object (the editor method checks for null) ...
            final ModelResource resource = editor.findModelResource(eObject);
            final IPath modelPath = resource.getPath();
            final IPath pathInModel = editor.getModelRelativePath(eObject);
            return modelPath.append(pathInModel);
        }
        return null;
    }

    public Object findObject( final IPath path ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(path);
        // Loop over the segments in the path ...
        final String[] segments = path.segments();
        Object object = this.workspace;
        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];
            if (object instanceof ModelResource) {
                final ModelResource modelResource = (ModelResource)object;
                final IPath pathInModel = path.removeFirstSegments(i);
                final ModelEditor editor = ModelerCore.getModelEditor();
                return editor.findObjectByPath(modelResource, pathInModel);
            }
            if (object instanceof ModelWorkspaceItem) {
                final ModelWorkspaceItem item = (ModelWorkspaceItem)object;
                object = item.getChild(segment);
            }

        }
        return object;
    }

    /**
     * Find the {@link ModelResource} that is represented by the supplied path or that contains the object with the specified
     * path.
     * 
     * @param path the path; may not be null
     * @return the ModelResource, or null if the path does not reflect a ModelResource or an object within a ModelResource
     * @throws ModelWorkspaceException if there is an error in the ModelWorkspace
     */
    public ModelResource findModelResourceForObject( final IPath path ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(path);
        // Loop over the segments in the path ...
        final String[] segments = path.segments();
        Object object = this.workspace;
        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];
            if (object instanceof ModelResource) {
                return (ModelResource)object;
            }
            if (object instanceof ModelWorkspaceItem) {
                final ModelWorkspaceItem item = (ModelWorkspaceItem)object;
                object = item.getChild(segment);
            }

        }
        return null;
    }

    /**
     * Returns the child elements of the given parent element.
     * 
     * @param parentElement the parent element; may not be null
     * @return an array of child elements
     * @throws ModelWorkspaceException if there is an error obtaining the information
     */
    public Object[] getChildren( final Object parent ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(parent);

        // --------------------------------------------------
        // Compute the children of the supplied parent ...
        // --------------------------------------------------
        final Object children = doGetChildren(parent);
        if (children == null) {
            return EMPTY_CHILDREN;
        }

        // --------------------------------------------------
        // Remove objects the filters say are not visible ...
        // --------------------------------------------------
        final List results = new LinkedList();
        if (children instanceof Object[]) {
            final Object[] childObjects = (Object[])children;
            // The children are represented as an array
            boolean allChildren = true;
            for (int i = 0; i < childObjects.length; ++i) {
                final Object child = childObjects[i];
                if (isVisible(parent, child)) {
                    results.add(child);
                } else {
                    allChildren = false;
                }
            }
            if (allChildren) {
                // Add the children are to be returned, so simply return the array
                return childObjects;
            }
        } else if (children instanceof Collection) {
            final Collection childrenList = (Collection)children;
            // The children are represented as a list
            final Iterator iter = childrenList.iterator();
            while (iter.hasNext()) {
                final Object child = iter.next();
                if (isVisible(parent, child)) {
                    results.add(child);
                }
            }
        }

        // ---------------------------------
        // Convert to the required array ...
        // ---------------------------------
        return results.toArray();
    }

    /**
     * Method that is called by {@link #getChildren(Object)} to actually compute all of the children for the supplied parent. The
     * filtering is not done by this method, but is instead done within the {@link #getChildren(Object)} method.
     * <p>
     * This method should be overridden to change the computation of the children for an object. However, changing which children
     * of an object are visible is accomplished by adding {@link #getModelWorkspaceFilters() filters}.
     * </p>
     * 
     * @param parent the parent; never null (not checked in this method)
     * @return the children for the parent; either an Object[] or a Collection
     * @throws ModelWorkspaceException if there is an error obtaining the information
     */
    protected Object doGetChildren( final Object parent ) throws ModelWorkspaceException {
        // First check whether eobject ...
        if (parent instanceof EObject) {
            return ((EObject)parent).eContents();
        }
        // Then check whether model resource ...
        else if (parent instanceof ModelResource) {
            return ((ModelResource)parent).getEObjects();
        }
        // Then check whether any model workspace item ...
        else if (parent instanceof ModelWorkspaceItem) {
            final ModelWorkspaceItem mwsItem = (ModelWorkspaceItem)parent;
            final ModelWorkspaceItem[] children = mwsItem.getChildren();
            if (this.restrictedToModelResourcesOnly) {
                // Only want to show ModelWorkspaceItems
                return children;
            }
            // Find the non-model resources in the list ...
            final Set modelResources = new HashSet();
            final List results = new ArrayList();
            for (int i = 0; i < children.length; ++i) {
                final ModelWorkspaceItem item = children[i];
                results.add(item);
                final IResource iresource = item.getCorrespondingResource();
                if (iresource != null) {
                    modelResources.add(iresource);
                }
            }
            try {
                final IResource iresource = mwsItem.getCorrespondingResource();
                if (iresource != null && iresource instanceof IContainer) {
                    final IContainer container = (IContainer)iresource;
                    final IResource[] resourceChildren = container.members();
                    for (int i = 0; i < resourceChildren.length; ++i) {
                        final IResource resource = resourceChildren[i];
                        if (!modelResources.contains(resource)) {
                            results.add(resource);
                        }
                    }
                }
            } catch (ModelWorkspaceException e) {
                ModelerCore.Util.log(e);
            } catch (CoreException e) {
                ModelerCore.Util.log(e);
            }
            return results;
        }

        return null;
    }

    /**
     * Returns whether the given element has children.
     * <p>
     * Intended as an optimization for when the viewer does not need the actual children. Clients may be able to implement this
     * more efficiently than <code>getChildren</code>.
     * </p>
     * 
     * @param element the element
     * @return <code>true</code> if the given element has children, and <code>false</code> if it has no children
     * @throws ModelWorkspaceException if there is an error obtaining the information
     */
    public boolean hasChildren( final Object element ) throws ModelWorkspaceException {
        final Object children = doGetChildren(element);

        // Do the fast checks first ...
        if (children == EMPTY_CHILDREN) {
            return false;
        }
        if (children instanceof Object[]) {
            final Object[] childObjects = (Object[])children;
            for (int i = 0; i < childObjects.length; ++i) {
                final Object child = childObjects[i];
                if (isVisible(element, child)) {
                    return true;
                }
            }
            return false;
        }
        if (children instanceof Collection) {
            final Collection childrenList = (Collection)children;
            final Iterator iter = childrenList.iterator();
            while (iter.hasNext()) {
                final Object child = iter.next();
                if (isVisible(element, child)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Returns the parent for the given element, or <code>null</code> indicating that the parent can't be computed.
     * 
     * @param element the element; may not be null
     * @return the parent element, or <code>null</code> if it has none or if the parent cannot be computed
     * @throws ModelWorkspaceException if there is an error obtaining the information
     */
    public Object getParent( final Object element ) {
        CoreArgCheck.isNotNull(element);

        if (element instanceof EObject) {
            final EObject obj = (EObject)element;
            final EObject parentObject = obj.eContainer();
            if (parentObject != null) {
                return parentObject;
            }
            // There is no parent EObject, so return the ModelResource that contains this object ...
            return this.workspace.findModelResource(obj);
        }

        if (element instanceof ModelWorkspaceItem) {
            return ((ModelWorkspaceItem)element).getParent();
        }

        return null;
    }

}
