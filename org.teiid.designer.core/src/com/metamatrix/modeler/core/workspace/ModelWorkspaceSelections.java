/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.metamatrix.core.selection.TreeSelection;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * ModelWorkspaceSelections
 */
public class ModelWorkspaceSelections implements TreeSelection {
    
    /**
     * A reusable filter that shows everything in the view.
     */
    public static final ModelWorkspaceSelectionFilter ALL_SELECTABLE_FILTER = new ModelWorkspaceSelectionFilter() {
        public boolean isSelectable(final Object element) {
            return true;
        }
    };
    
    /*====================================================================
     * Constants defining the selection modes:
     *====================================================================*/
    
    /**
     * Selection constant (value 0) indicating an object is <i>not</i> selected, nor are any objects
     * (directly or indirectly) below it.
     */
    public static final int UNSELECTED = 0;

    /**
     * Selection constant (value 1) indicating an object <i>is</i> selected as are all of the objects
     * (directly or indirectly) below it.
     */
    public static final int SELECTED = 1;

    /**
     * Selection constant (value 2) indicating an object is <i>not</i> selected, while some of the 
     * objects (directly or indirectly) below it are selected and some are not selected.
     */
    public static final int PARTIALLY_SELECTED = 2;
    
    /**
     * A possible return value from {@link #getSelectionMode(IPath)} when the selection mode
     * cannot be determined.
     */
    public static final int UNKNOWN_SELECTION = -1;
    
    private final Set selecteds;
    private final Set unselecteds;
    private final Set partiallySelected;
    
    private final List selectableFilters;
    private ModelWorkspaceView modelWorkspaceView;

    /**
     * Construct an instance of ModelWorkspaceSelections.
     * 
     */
    public ModelWorkspaceSelections() {
        super();
        this.selecteds = new HashSet();
        this.unselecteds = new HashSet();
        this.partiallySelected = new HashSet();
        this.selectableFilters = new LinkedList();
    }
    
    /**
     * Return the ModelWorkspaceView that this selections object is currently using.
     * This class uses the view when {@link #setSelected(Object, int) setting the selection mode}
     * of various objects so that mode of the ancestors of the affected object are set
     * correctly.  Having no view means tha some of the ancestors modes may be set
     * to something less correct (e.g., {@link #PARTIALLY_SELECTED}).
     * @return the view, or null if there is no view.
     */
    public ModelWorkspaceView getModelWorkspaceView() {
        return modelWorkspaceView;
    }

    /**
     * Set the ModelWorkspaceView that this selections object is to use.
     * This class uses the view when {@link #setSelected(Object, int) setting the selection mode}
     * of various objects so that mode of the ancestors of the affected object are set
     * correctly.  Having no view means tha some of the ancestors modes may be set
     * to something less correct (e.g., {@link #PARTIALLY_SELECTED}).
     * @param view the view to be used, or null if this object should not use a view.
     */
    public void setModelWorkspaceView(ModelWorkspaceView view) {
        modelWorkspaceView = view;
    }

    /**
     * Return the list of {@link ModelWorkspaceSelectionFilter} instances used by
     * this object.  The returned list should be modified to add or remove
     * filters.  However, this object does not make any special effort when
     * accessing the filters, so care must be taken to not modify
     * this list at the same time this object is using the filters.
     * @return the List of {@link ModelWorkspaceSelectionFilter} instances; never null
     */
    public List getModelWorkspaceSelectionFilters() {
        return this.selectableFilters;
    }
        
    protected boolean isSelectable( final Object object ) {
        final Iterator iter = this.selectableFilters.iterator();
        while (iter.hasNext()) {
            final ModelWorkspaceSelectionFilter filter = (ModelWorkspaceSelectionFilter)iter.next();
            if ( filter.isSelectable(object) ) {
                return true;
            }
        }
        // no filter said the object was selectable
        return false;
    }
    
    /**
     * Return whether there are any paths that are known to be selected, unselected or partially selected.
     * @return true if there are at least some paths known to be selected, unselected or partially selected,
     * or false if there no known selection modes for any path
     */
    public boolean hasSelectionModes() {
        return this.selecteds.size() != 0 || this.partiallySelected.size() != 0 || this.unselecteds.size() != 0;
    }
    
    /**
     * Get the current selection mode for the supplied path for a model object.
     * @param path the path of the object for which the selection mode is to be determined;
     * may not be null
     * @return the selection mode; one of {@link #SELECTED}, {@link #UNSELECTED},
     * {@link #PARTIALLY_SELECTED} or {@link #UNKNOWN_SELECTION}.
     */
    public int getSelectionMode( final IPath path ) {
        ArgCheck.isNotNull(path);
        
        if ( this.selecteds.contains(path) ) {
            return SELECTED;
        }
        if ( this.unselecteds.contains(path) ) {
            return UNSELECTED;
        }
        if ( this.partiallySelected.contains(path) ) {
            return PARTIALLY_SELECTED;
        }
        
        // -------------------------------------------------------------------
        // The path is not known.  However, the "default" can be determined by
        // looking at the ancestors.
        // -------------------------------------------------------------------
        
        // If this is the JdbcDatabase path ...
        if ( path.segmentCount() == 0 ) {
            // The default for the JdbcDatabase path should be to be PARTIALLY_SELECTED
            // so that children are figured out
            this.unselecteds.add(path);
            return UNSELECTED;
        }
        // If this path is for a root object ...
        if ( path.segmentCount() == 1 ) {
            // so the default should be to be UNSELECTED
            this.unselecteds.add(path);
            return UNSELECTED;
        }
        
        // Get the parent path and see what it's selection mode is ...
        final IPath parentPath = path.removeLastSegments(1);
        final int parentMode = getSelectionMode(parentPath);    // recursive!!!
        if ( parentMode == SELECTED ) {
            // The parent is fully selected, so should this node ...
            this.selecteds.add(path);
            return SELECTED;
        }
//        if ( parentMode == PARTIALLY_SELECTED ) {
//            // The parent is partially selected, so we don't know what to assume 
//            return UNKNOWN_SELECTION;
//        }
        // Parent is unselected, so this node should be as well ...
        return UNSELECTED;
    }
    
    /**
     * Get the current selection mode for the supplied model object.
     * @param modelObject the model object for which the selection mode is to be determined;
     * may not be null
     * @return the selection mode; one of {@link #SELECTED}, {@link #UNSELECTED},
     * {@link #PARTIALLY_SELECTED} or {@link #UNKNOWN_SELECTION}.
     */
    public int getSelectionMode( final Object modelObject ) {
        ArgCheck.isNotNull(modelObject);
        assertNonNullView();
        final IPath path = this.modelWorkspaceView.getPath(modelObject);
        if ( path != null ) {
            return getSelectionMode(path);
        }
        return UNSELECTED;
    }
    
    protected void assertNonNullView() {
        if ( this.modelWorkspaceView == null ) {
            final String msg = ModelerCore.Util.getString("ModelWorkspaceSelections.NoViewReference"); //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }
    
    /**
     * Set the selection of the supplied model object.
     * @param modelObject
     * @param selectionMode
     */
    public void setSelected( final Object modelObject, final boolean selected ) throws ModelWorkspaceException {
        ArgCheck.isNotNull(modelObject);
        final int mode = selected ? SELECTED : UNSELECTED;
        setSelected(modelObject,mode);
    }
    
    /**
     * Method that actually sets the selection of the supplied model object.
     * @param modelObject
     * @param selectionMode
     */
    protected void setSelected( final Object modelObject, final int selectionMode ) throws ModelWorkspaceException {
        if ( isSelectable(modelObject) ) {
            assertNonNullView();
            final IPath path = this.modelWorkspaceView.getPath(modelObject);
            if ( path != null ) {
                setSelected(path,selectionMode);
                updateParentSelection(modelObject,path,selectionMode);
            }
        }
    }
    
    /**
     * Set the selection of the supplied model object.
     * @param modelObject
     * @param selectionMode
     */
    protected void setSelected( final IPath path, final int selectionMode ) {
        ArgCheck.isNotNull(path);
        if ( selectionMode == SELECTED ) {
            this.selecteds.add(path);
            this.unselecteds.remove(path);
            this.partiallySelected.remove(path);
            removeSubpaths(this.unselecteds,path);
            removeSubpaths(this.partiallySelected,path);
        } else if ( selectionMode == UNSELECTED ) {
            this.selecteds.remove(path);
            this.unselecteds.add(path);
            this.partiallySelected.remove(path);
            removeSubpaths(this.selecteds,path);
            removeSubpaths(this.partiallySelected,path);
        } else if ( selectionMode == PARTIALLY_SELECTED ) {
            this.selecteds.remove(path);
            this.unselecteds.remove(path);
            this.partiallySelected.add(path);
        }
    }
    
    /**
     * Utility method to remove from the supplied set all paths to which the supplied
     * path is considered a prefix.
     * @param paths
     * @param path
     */
    protected void removeSubpaths( final Set paths, final IPath path ) {
        final Iterator iter = paths.iterator();
        while (iter.hasNext()) {
            final IPath existingPath = (IPath)iter.next();
            if ( path.isPrefixOf(existingPath) ) {
                iter.remove();
            }
        }
    }

    protected void updateParentSelection( final Object child, final IPath childPath, final int childSelectionMode ) throws ModelWorkspaceException {
        // If the parent is already set to the selection mode of the child, do nothing ...
        final IPath parentPath = childPath.removeLastSegments(1);
        if ( parentPath.segmentCount() == 0 ) {
            return;
        }
        final int currentParentMode = this.getSelectionMode(parentPath);
        if ( currentParentMode == childSelectionMode ) {
            return;
        }
        if ( this.modelWorkspaceView != null ) {
            // Get the parent ...
            final Object parent = this.modelWorkspaceView.getParent(child);
            if ( parent != null ) {
                // Get the children of the parent ...
                final Object[] children = this.modelWorkspaceView.getChildren(parent);
                // Go through the children ...
                boolean foundFullySelected = false;
                boolean foundUnselected = false;
                for (int i = 0; i < children.length; ++i) {
                    Object childObj = children[i];
                    if ( isSelectable(childObj) ) {
                        final IPath childObjPath = this.modelWorkspaceView.getPath(childObj);
                        final int mode = this.getSelectionMode(childObjPath);
                        if ( mode == SELECTED ) {
                            foundFullySelected = true;
                        } else if ( mode == UNSELECTED ) {
                            foundUnselected = true;
                        }
                        if ( mode == PARTIALLY_SELECTED || (foundFullySelected && foundUnselected) ) {
                            this.setSelected(parent,PARTIALLY_SELECTED);
                            return;
                        }
                    }
                }
                // At this point, all the children are either all unselected or all selected 
                if ( foundFullySelected ) {
                    this.setSelected(parent,SELECTED);
                } else {
                    this.setSelected(parent,UNSELECTED);
                }
                return;
            }
        
        } else {
            // We have to make some assumptions about what the parent mode is ...
            // If the parent is selected and the child is being unselected or partially selected ...
            if ( currentParentMode == SELECTED && childSelectionMode == UNSELECTED || childSelectionMode == PARTIALLY_SELECTED ) {
                // Assume the parent should be partially selected ...
                this.setSelected(parentPath,PARTIALLY_SELECTED);
            }
        }
    }
    
    /**
     * Return the paths that have been explicitly selected.  These paths may not include paths to all
     * objects in a fully-selected branch. 
     * <p>
     * The resulting list contains the IPath objects in their natural order.
     * </p>
     * @return the {@link IPath} instances specifying those objects that have been explicitly
     * selected (not necessarily including any paths to objects contained by fully-selected objects);
     * never null
     */
    public List getSelectedPaths() {
        return createSortedPaths(this.selecteds);
    }

    /**
     * Return the paths that are considered partially selected.  Partially selected objects
     * are those that contain (directly or indirectly) both selected and unselected objects. 
     * <p>
     * The resulting list contains the IPath objects in their natural order.
     * </p>
     * @return the {@link IPath} instances specifying those objects that are partially
     * selected; never null
     */
    public List getPartiallySelectedPaths() {
        return createSortedPaths(this.partiallySelected);
    }
    
    /**
     * Return the paths that are considered unselected.  These paths may not include paths to all
     * objects in a fully-unselected branch. 
     * <p>
     * The resulting list contains the IPath objects in their natural order.
     * </p>
     * @return the {@link IPath} instances specifying those objects that are
     * unselected (not necessarily including any paths to objects contained by unselected objects);
     * never null
     */
    public List getUnselectedPaths() {
        return createSortedPaths(this.unselecteds);
    }
    
    /**
     * Return the list of selected or partially-selected ModelResource instances.
     * @return the list of {@link ModelResource} instances that are either fully-selected
     * or partially selected.
     * @throws IllegalStateException if there is no 
     * {@link #setModelWorkspaceView(ModelWorkspaceView) ModelWorkspaceView} available
     * (since one is required to actually determine the objects that are selected).
     * @throws ModelWorkspaceException if there is an error in the ModelWorkspace
     */
    public List getSelectedOrPartiallySelectedModelResources() throws ModelWorkspaceException {
        assertNonNullView();
        final List result = new LinkedList();
        
        // Go through the selections and add the model resources ...
        for (int i = 0; i < 2; ++i) {
            final List paths = (i==0) ? 
                               this.getSelectedPaths() : 
                               this.getPartiallySelectedPaths();
            final Iterator iter = paths.iterator();
            while (iter.hasNext()) {
                final IPath path = (IPath)iter.next();
                final ModelResource modelResource = this.modelWorkspaceView.findModelResourceForObject(path);
                if ( modelResource != null ) {
                    // A ModelResource (or an object in a ModelResource) was specified by the path ..
                    if ( !result.contains(modelResource)) {
                        result.add(modelResource);
                    }
                } else {
                    // See if the object is a ModelWorkspaceItem ...
                    final Object obj = this.modelWorkspaceView.findObject(path);
                    if ( obj instanceof ModelWorkspaceItem ) {
                        addAllModelResources((ModelWorkspaceItem)obj,result);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * @param item
     * @param result
     */
    private void addAllModelResources( final ModelWorkspaceItem item, final List result) throws ModelWorkspaceException {
        final int mode = getSelectionMode(item);
        if ( mode != SELECTED && mode != PARTIALLY_SELECTED ) {
            // The item is not selected or partially selected, so simply return without doing anything ...
            return;
        }

        // At this point, 'item' is either SELECTED or PARTIALLY_SELECTED.
        if ( item instanceof ModelResource ) {
            if ( !result.contains(item)) {
                // Add to the list only if selected or partially selected
                result.add(item);
            }
            return;
        }
        // It's not a ModelResource, so get all the children and process them ...
        final ModelWorkspaceItem[] children = item.getChildren();
        for (int i = 0; i < children.length; ++i) {
            final ModelWorkspaceItem child = children[i];
            if(this.modelWorkspaceView.isVisible(item,child)) {
                addAllModelResources(child,result);
            }
        }
    }

    /**
     * Return the list of selected or partially-selected IResources that are not ModelResource instances.
     * @return the list of {@link IResource} instances that are either fully-selected
     * or partially selected.
     * @throws IllegalStateException if there is no 
     * {@link #setModelWorkspaceView(ModelWorkspaceView) ModelWorkspaceView} available
     * (since one is required to actually determine the objects that are selected).
     * @throws ModelWorkspaceException if there is an error in the ModelWorkspace
     */
    public List getSelectedOrPartiallySelectedNonModelResources() throws ModelWorkspaceException {
        assertNonNullView();
        final List result = new LinkedList();
        
        // Go through the selections and add the model resources ...
        for (int i = 0; i < 2; ++i) {
            final List paths = (i==0) ? 
                               this.getSelectedPaths() : 
                               this.getPartiallySelectedPaths();
            final Iterator iter = paths.iterator();
            while (iter.hasNext()) {
                final IPath path = (IPath)iter.next();
                final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
                if ( resource != null ) {
                    // A ModelResource (or an object in a ModelResource) was specified by the path ..
                    if ( !result.contains(resource)) {
                        result.add(resource);
                    }
                } else {
                    // See if the object is a ModelWorkspaceItem ...
                    final Object obj = this.modelWorkspaceView.findObject(path);
                    if ( obj instanceof ModelWorkspaceItem ) {
                        addAllNonModelResources((ModelWorkspaceItem)obj,result);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * @param item
     * @param result
     */
    private void addAllNonModelResources( final ModelWorkspaceItem item, final List result) throws ModelWorkspaceException {
        final int mode = getSelectionMode(item);
        if ( mode != SELECTED && mode != PARTIALLY_SELECTED ) {
            // The item is not selected or partially selected, so simply return without doing anything ...
            return;
        }

        // At this point, 'item' is either SELECTED or PARTIALLY_SELECTED.
        final IResource iresource = item.getCorrespondingResource();
        if ( item instanceof ModelResource ) {
            if ( !result.contains(iresource)) {
                // Add to the list only if selected or partially selected
                result.add(iresource);
            }
            return;
        }
        // It's not a ModelResource, so get all the children and process them ...
        final ModelWorkspaceItem[] children = item.getChildren();
        for (int i = 0; i < children.length; ++i) {
            final ModelWorkspaceItem child = children[i];
            addAllNonModelResources(child,result);
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        toString("  Selected Paths",this.selecteds,sb); //$NON-NLS-1$
        toString("\n  Partially-Selected Paths",this.partiallySelected,sb); //$NON-NLS-1$
        toString("\n  Unselected Paths",this.unselecteds,sb); //$NON-NLS-1$
        return sb.toString();
    }
    protected void toString( final String desc, final Set paths, final StringBuffer sb ) {
        sb.append(desc + " (" + paths.size() + "):"); //$NON-NLS-1$ //$NON-NLS-2$ 
        final List sortedPaths = createSortedPaths(paths);
        final Iterator iter = sortedPaths.iterator();
        while (iter.hasNext()) {
            final Object path = iter.next();
            sb.append("\n     " + path); //$NON-NLS-1$
        }
    }
    
    protected List createSortedPaths( final Set paths ) {
        final List sortedPaths = new LinkedList();
        sortedPaths.addAll(paths);
        final Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((IPath)o1).toString().compareTo(((IPath)o2).toString());
            }
            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };
        Collections.sort(sortedPaths,comparator);
        return sortedPaths;
    }
}
