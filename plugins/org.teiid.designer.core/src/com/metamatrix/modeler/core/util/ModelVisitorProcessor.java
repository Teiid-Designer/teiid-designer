/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelFolder;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * The ModelVisitorProcessor is used to walk a {@link ModelVisitor} implementation through one or more
 * models.
 * <p>
 * To use, simply create a {@link ModelVisitor visitor} and a ModelProcessor, and use the processor
 * to walk one or more objects by supply the starting point(s):
 * <pre>
 * final ModelVisitor visitor = new MyVisitor();
 * final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
 * try {
 *    processor.walk(startingPoint,ModelVisitorProcessor.DEPTH_INFINITE);
 * } catch (ModelerCoreException e) {
 *    // handle the exception
 * }
 * </pre>
 * where <code>startingPoint</code> can be one of the following:
 * <ul>
 *   <li>An {@link EObject}</li>
 *   <li>An {@link Resource EMF Resource}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelResource ModelResource}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelFolder ModelFolder}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelProject ModelProject}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelWorkspace ModelWorkspace}</li>
 *   <li>A {@link List list} of any of the above objects</li>
 * </ul>
 * and where <code>depth</code> is one of the following:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.core.util.ModelVisitorProcessor#DEPTH_INFINITE ModelVisitorProcessor.DEPTH_INFINITE}</li>
 *   <li>{@link com.metamatrix.modeler.core.util.ModelVisitorProcessor#DEPTH_ONE ModelVisitorProcessor.DEPTH_ONE}</li>
 *   <li>{@link com.metamatrix.modeler.core.util.ModelVisitorProcessor#DEPTH_ZERO ModelVisitorProcessor.DEPTH_ZERO}</li>
 * </ul>
 * </p>
 * @see ModelVisitor.
 */
public class ModelVisitorProcessor {
    
    /*========================================================================
     * Constants defining the depth of resource tree traversal
     *========================================================================*/
    
    /**
     * Depth constant (value 0) indicating a walk of the supplied object, but not any of its children.
     */
    public static final int DEPTH_ZERO = 0;

    /**
     * Depth constant (value 1) indicating a walk of the supplied object and its direct children.
     */
    public static final int DEPTH_ONE = 1;

    /**
     * Depth constant (value 2) indicating a walk of the supplied object and its direct and
     * indirect children at any depth.
     */
    public static final int DEPTH_INFINITE = 2;

    /*========================================================================
     * Constants defining the mode of obtaining the children of model objects
     *========================================================================*/
    
    /**
     * Mode constant (value 0) indicating that all containment references should be followed.
     * For some metamodels, this exposes more objects than are visible to the user (e.g., in
     * a tree view via the item providers), but does navigate to <i>all</i> objects.
     */
    public static final int MODE_ALL_CONTAINMENTS = 0;
    
    /**
     * Mode constant (value 1) indicating that visible objects should be processed.
     * For some metamodels, this exposes fewer objects than there actually are.
     */
    public static final int MODE_VISIBLE_CONTAINMENTS = 1;
    
    /**
     * The default mode constant, currently set to {@link #MODE_ALL_CONTAINMENTS}.
     */
    public static final int MODE_DEFAULT = MODE_ALL_CONTAINMENTS;
    
    protected ModelVisitor visitor;
    protected Navigator navigator;

    /**
     * Construct an instance of ModelWalker.
     * 
     */
    public ModelVisitorProcessor( final ModelVisitor visitor ) {
        this(visitor,MODE_DEFAULT);
    }
    
    /**
     * Construct an instance of ModelWalker.
     * 
     */
    public ModelVisitorProcessor( final ModelVisitor visitor, final int mode ) {
        super();
        CoreArgCheck.isNotNull(visitor);
        assertValidMode(mode);
        this.visitor = visitor;
        switch(mode) {
            case MODE_VISIBLE_CONTAINMENTS:
                this.navigator = new ItemProviderNavigator();
                break;
            case MODE_ALL_CONTAINMENTS:
                this.navigator = new ContainmentNavigator();
                break;
        }
    }
    
    /**
     * Return the ModelVisitor that this processor is using.
     * @return the ModelVisitor; never null;
     */
    public ModelVisitor getModelVisitor() {
        return this.visitor;
    }
    
    /**
     * Helper method to check that the mode is valid.
     * @param mode
     */
    protected void assertValidMode( final int mode ) {
        if ( mode == MODE_ALL_CONTAINMENTS || mode == MODE_VISIBLE_CONTAINMENTS ) {
            return;
        }
        final String msg = ModelerCore.Util.getString("ModelVisitorProcessor.Invalid_mode"); //$NON-NLS-1$
        throw new IllegalArgumentException(msg);
    }
    
    /**
     * Helper method to check that the depth is valid.
     * @param depth
     */
    protected void assertValidDepth( final int depth ) {
        if ( depth == DEPTH_INFINITE || depth == DEPTH_ONE || depth == DEPTH_ZERO ) {
            return;
        }
        final String msg = ModelerCore.Util.getString("ModelVisitorProcessor.Invalid_depth"); //$NON-NLS-1$
        throw new IllegalArgumentException(msg);
    }
    
    /**
     * Walk the {@link ModelWorkspace} to the supplied depth.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param workspace the workspace to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the workspace
     */
    public void walk( final ModelWorkspace workspace, final int depth ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(workspace);
        assertValidDepth(depth);
        
        // visit this resource      
        if (depth == DEPTH_ZERO)
            return;
            
        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO );
        final ModelWorkspaceItem[] children = workspace.getChildren();
        for (int i = 0; i < children.length; ++i) {
            final ModelWorkspaceItem child = children[i];
            if ( child instanceof ModelProject ) {
                walk((ModelProject)child,nextDepth);
            } else if ( child instanceof ModelFolder ) {
                    walk((ModelFolder)child,nextDepth);
            } else if ( child instanceof ModelResource ) {
                walk((ModelResource)child,nextDepth);
            }
        }
    }

    /**
     * Walk the supplied {@link ModelProject} to the supplied depth.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param project the project to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the project
     */
    public void walk( final ModelProject project, final int depth ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(project);
        assertValidDepth(depth);
        
        // visit this resource      
        if (depth == DEPTH_ZERO)
            return;
            
        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO );
        final ModelWorkspaceItem[] children = project.getChildren();
        for (int i = 0; i < children.length; ++i) {
            final ModelWorkspaceItem child = children[i];
            if ( child instanceof ModelFolder ) {
                walk((ModelFolder)child,nextDepth);
            } else if ( child instanceof ModelResource ) {
                walk((ModelResource)child,nextDepth);
            }
        }
    }

    /**
     * Walk the supplied {@link ModelFolder} to the supplied depth.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param folder the folder to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the folder
     */
    public void walk( final ModelFolder folder, final int depth ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(folder);
        assertValidDepth(depth);
        
        // visit this resource      
        if (depth == DEPTH_ZERO)
            return;
            
        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO );
        final ModelWorkspaceItem[] children = folder.getChildren();
        for (int i = 0; i < children.length; ++i) {
            final ModelWorkspaceItem child = children[i];
            if ( child instanceof ModelFolder ) {
                walk((ModelFolder)child,nextDepth);
            } else if ( child instanceof ModelResource ) {
                walk((ModelResource)child,nextDepth);
            }
        }
    }

    /**
     * Walk the supplied {@link ModelResource} to the supplied depth.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param startingResource the model to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the model
     */
    public void walk( final ModelResource startingResource, final int depth ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(startingResource);
        assertValidDepth(depth);
        
        // visit this resource      
        final Resource resource = startingResource.getEmfResource();
        walk(resource,depth);
    }

    /**
     * Walk the supplied {@link Resource} to the supplied depth.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param startingResource the EMF resource to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the resource
     */
    public void walk( final Resource startingResource, final int depth ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(startingResource);
        assertValidDepth(depth);
        
        // visit this resource      
        if (!getModelVisitor().visit(startingResource) || depth == DEPTH_ZERO)
            return;
            
        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO );
        final List children = startingResource.getContents();
        final Iterator iter = children.iterator();
        while (iter.hasNext()) {
            final EObject object = (EObject)iter.next();
            walk(object,nextDepth);
        }
    }

    /**
     * Walk to the supplied depth the tree of model objects below the supplied {@link EObject}.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param startingResource the root of the tree of model objects to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the model objects
     */
    public void walk( final EObject startingObject, final int depth ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(startingObject);
        assertValidDepth(depth);

        // visit this resource      
        if (!this.visitor.visit(startingObject) || depth == DEPTH_ZERO)
            return;

//        final URI uri = EcoreUtil.getURI(startingObject);
//        System.out.println("Visiting " + uri.fragment() + " (" + startingObject.eClass().getName() + ")");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            
        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO );

        final Object children = this.navigator.getChildren(startingObject);
        if ( children instanceof List ) {
            final Iterator valueIter = ((List)children).iterator();
            while (valueIter.hasNext()) {
                final Object obj = valueIter.next();
                if ( obj instanceof EObject ) {
                    walk((EObject) obj, nextDepth);
                }
            }
        } else if ( children instanceof EObject ) {
            walk((EObject)children,nextDepth);
        }

        if ( this.visitor instanceof ModelVisitorWithFinish ) {
            ((ModelVisitorWithFinish)this.visitor).postVisit(startingObject);
        }
    }

    /**
     * Walk each of the supplied objects to the supplied depth.
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param startingObjects the objects that are all to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the model objects
     */
    public void walk( final Collection startingObjects, final int depth ) throws ModelerCoreException {
        walk(startingObjects,depth,true);
    }
    
    /**
     * Walk each of the supplied objects to the supplied depth.
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param startingObjects the objects that are all to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @param skipDuplicateBranches true if any duplicate branches in the <code>startingObjects</code> 
     * should be skipped. An object is considered to be a duplicate branch if it is a child of another
     * object in the <code>startingObjects</code> list
     * @throws ModelerCoreException if there is an exception walking the model objects
     */
    public void walk( final Collection startingObjects, final int depth, 
                      final boolean skipDuplicateBranches ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(startingObjects);
        assertValidDepth(depth);
        final Collection roots = skipDuplicateBranches ?
                           removeDuplicates(startingObjects) :
                           startingObjects;
        final Iterator iter = roots.iterator();
        while (iter.hasNext()) {
            final Object object = iter.next();
            if ( object != null ) {
                if ( object instanceof Resource ) {
                    walk((Resource)object,depth);
                } else if ( object instanceof EObject ) {
                    walk((EObject)object,depth);
                } else if ( object instanceof ModelResource ) {
                    walk((ModelResource)object,depth);
                } else if ( object instanceof ModelProject ) {
                    walk((ModelProject)object,depth);
                } else if ( object instanceof ModelFolder ) {
                    walk((ModelFolder)object,depth);
                } else if ( object instanceof ModelWorkspace ) {
                    walk((ModelWorkspace)object,depth);
                }
            }
        }
    }
    
    protected Collection removeDuplicates( final Collection startingObjects ) {
        if ( startingObjects.size() < 2 ) {
            return startingObjects;     // definitely no duplicates!
        }
        
        if ( startingObjects instanceof Set ) {
            return startingObjects;        // a Set definitely has no duplicates!
        } 
        final Collection copy = new LinkedList(startingObjects);
        
        // See if there are any ModelWorkspaceItems ...
        final Set setOfUnique = new HashSet();
        boolean includesModelWorkspaceItems = false;
        final Iterator iter = copy.iterator();
        while (iter.hasNext() && !includesModelWorkspaceItems) {
            final Object obj = iter.next();
            if ( obj instanceof ModelWorkspaceItem ) {
                includesModelWorkspaceItems = true;
            }
            if ( setOfUnique.contains(obj) ) {
                iter.remove();
            }
            setOfUnique.add(obj);
        }
        
        // There should be no duplicates ...
        final Iterator iter2 = copy.iterator();
        while (iter2.hasNext()) {
            final Object obj = iter2.next();
            final boolean foundDuplicateBranch = isAncestorInList(obj,copy,includesModelWorkspaceItems);
            if ( foundDuplicateBranch ) {
                iter2.remove();
            }
        }
        
        return copy;
    }
    
    protected boolean isAncestorInList( final Object child, final Collection inputs, final boolean useModelWorkspace ) {
        Object parent = null;
        ModelResource modelResource = null;
        if ( child instanceof EObject ) {
            final EObject childObject = (EObject)child;
            parent = childObject.eContainer();
            if ( parent == null ) {
                parent = childObject.eResource();
                if ( useModelWorkspace && parent instanceof Resource ) {
                    // Find the ModelResource for the resource, since the inputs might contain
                    // the parent Resource or the ModelResource ...
                    modelResource = ModelWorkspaceManager.getModelWorkspaceManager().findModelResource((Resource)parent);
                }
            }
        }
        // Need to find the ancestor for other types of children only if using the ModelWorkspace
        else if ( useModelWorkspace ) {
            if ( child instanceof Resource ) {
                // Find the ModelResource for the resource ...
                final ModelResource mr = ModelWorkspaceManager.getModelWorkspaceManager().findModelResource((Resource)child);
                if ( mr != null ) {
                    parent = mr.getParent();
                }
            }
            else if ( child instanceof ModelWorkspace ) {
                return false;
            }
            else if ( child instanceof ModelWorkspaceItem ) {
                // Find the ModelResource for the resource ...
                parent = ((ModelWorkspaceItem)child).getParent();
            }
        }
        
        // See if the parent is in the list ...
        if ( parent == null ) {
            return false;   // there is no parent, so don't continue ...
        }
        if ( inputs.contains(parent) ) {
            return true;
        }
        if ( modelResource != null ) {
            if ( inputs.contains(modelResource) ) {
                return true;
            }
        }
        return isAncestorInList(parent,inputs,useModelWorkspace);
    }
    
    protected abstract class Navigator {
        /**
         * Return the children for the supplied parent object.
         * @param parent
         * @return the List of children, the sole child, or null if there are no children.
         */
        public abstract Object getChildren(final EObject parent );
    }
    
    /**
     * This navigator examines the containment features, and obtains their values
     * using the reflective methods.
     */
    protected class ContainmentNavigator extends Navigator {
        @Override
        public Object getChildren( final EObject parent ) {
            return parent.eContents();
        }
    }
    
    /**
     * This navigator uses the ItemProvider to obtain
     * ItemProviderNavigator
     */
    protected class ItemProviderNavigator extends Navigator {
        private AdapterFactory adapterFactory;
        public ItemProviderNavigator() {
            this.adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
        }
        @Override
        public Object getChildren(final EObject parent ) {
            final ITreeItemContentProvider provider = (ITreeItemContentProvider)this.adapterFactory.adapt(parent,ITreeItemContentProvider.class);
            if ( provider != null ) {
                return provider.getChildren(parent);
            }
            return null;
        }
    }
    
}
