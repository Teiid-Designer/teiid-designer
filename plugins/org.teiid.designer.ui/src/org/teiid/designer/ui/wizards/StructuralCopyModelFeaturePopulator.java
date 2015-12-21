/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.util.NewModelObjectHelperManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.transformation.TransformationContainer;
import org.teiid.designer.transformation.aspects.sql.SqlTransformationMappingRootSqlAspect;
import org.teiid.designer.ui.common.tree.TreeViewerUtil;
import org.teiid.designer.ui.common.widget.InheritanceCheckboxTreeViewer;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * StructuralCopyModelFeaturePopulator
 *
 * @since 8.0
 */
public class StructuralCopyModelFeaturePopulator implements IStructuralCopyTreePopulator {

    private static String VIRTUAL_ROOT = "ROOT"; //$NON-NLS-1$

    protected IFile sourceFile;
    protected ICheckboxTreeViewerListenerController listenerController;
    protected ModelResource targetModelResource;


    /**
     * Constructor
     * 
     * @param sourceFile the source file
     * @param listenerController controller for checkbox selection changes made in the tree viewer
     */
    public StructuralCopyModelFeaturePopulator( IFile sourceFile ) {
        super();
        this.sourceFile = sourceFile;
    }
    
    /**
     * Constructor
     * 
     * @param sourceFile the source file
     * @param listenerController controller for checkbox selection changes made in the tree viewer
     */
    public StructuralCopyModelFeaturePopulator( IFile sourceFile,
                                                ICheckboxTreeViewerListenerController listenerController ) {
        super();
        this.sourceFile = sourceFile;
        this.listenerController = listenerController;
    }

    /**
     * Populate the tree which will display selectable model features.
     * 
     * @param viewer the tree viewer
     * @param theModel ModelResource for the model
     * @param targetIsVirtual flag indicating if target of copy is virtual
     */
    @Override
	public void populateModelFeaturesTree( TreeViewer viewer,
                                           ModelResource theModel,
                                           boolean targetIsVirtual ) {
        ViewerFilter[] filters = viewer.getFilters();
        StructuralCopyTreeViewerFilter filter = findStructuralCopyFilter(filters);
        if (targetIsVirtual) {
            if (filter == null) {
                // need to add a filter:
                viewer.addFilter(new StructuralCopyTreeViewerFilter());
            } // endif

        } else if (filter != null) {
            // not virtual, remove any filter:
            viewer.removeFilter(filter);
        } // endif

        StructuralCopyTreeContentProvider contentProvider = StructuralCopyTreeContentProvider.getInstance();
        viewer.setContentProvider(contentProvider);
        ModelExplorerLabelProvider labelProvider = new ModelExplorerLabelProvider();
        viewer.setLabelProvider(labelProvider);

        // clear out the input to make sure we can set the hash lookup flag:
        viewer.setInput(null);
        viewer.setUseHashlookup(true);
        viewer.setInput(theModel);
    }

    private StructuralCopyTreeViewerFilter findStructuralCopyFilter( ViewerFilter[] filters ) {
        for (int i = 0; i < filters.length; i++) {
            ViewerFilter filter = filters[i];
            if (filter instanceof StructuralCopyTreeViewerFilter) {
                return (StructuralCopyTreeViewerFilter)filter;
            } // endif
        } // endfor

        return null;
    }

    /**
     * Copy the model to the target, only copying those nodes selected in the tree viewer.
     * 
     * @param sourceModelResource modelResource containing the old information
     * @param targetModelResource the target ModelResource
     * @param viewer the tree viewer; root is the ModelResource
     * @param extraProperties optional properties to tweak creation of objects.
     * @param copyAllDescriptions option to copy or suppress copying all descriptions
     * @param monitor a progress monitor
     * @throws ModelerCoreException 
     */
    @Override
	public void copyModel( ModelResource sourceModelResource,
                           ModelResource targetModelResource,
                           InheritanceCheckboxTreeViewer viewer,
                           Map extraProperties,
                           boolean copyAllDescriptions,
                           IProgressMonitor monitor ) {

    	// This method is being revoked due to inadequate design and implementation.
    	throw new UnsupportedOperationException();
    }

    /**
     * Copy the model to the target, only copying those nodes selected in the tree viewer.
     * 
     * @param sourceModelResource modelResource containing the old information
     * @param targetModelResource the target ModelResource
     * @param viewer the tree viewer; root is the ModelResource
     * @param extraProperties optional properties to tweak creation of objects.
     * @param copyAllDescriptions option to copy or suppress copying all descriptions
     * @param monitor a progress monitor
     * @throws ModelerCoreException 
     */
    @Override
	public void copyModel( ModelResource sourceModelResource,
                           ModelResource targetModelResource,
                           Map extraProperties,
                           boolean copyAllDescriptions,
                           IProgressMonitor monitor ) throws ModelerCoreException {

        List /*<EObject>*/allSourceRootContents = sourceModelResource.getEmfResource().getContents();
        boolean sourceIsVirtual = sourceModelResource.getModelAnnotation().getModelType().equals(ModelType.VIRTUAL_LITERAL);
        boolean targetIsVirtual = targetModelResource.getModelAnnotation().getModelType().equals(ModelType.VIRTUAL_LITERAL);
        ModelType targetModelType = targetModelResource.getModelAnnotation().getModelType();

        // Defect 24086 - Creating a Source model from a Virtual model requires we remove the Transformation Container from the
        // equation.
        List filteredChildren = new ArrayList(allSourceRootContents.size());

        // Need to filter the first level children as below....
        // JIRA Issue JBEDSP-257
        // Add Descriptions to the filteredChildren if includeDescriptions = true
        // Defect 24086 - Creating a Source model from a Virtual model requires we remove the Transformation Container from the
        // equation.
        for (Iterator iter = allSourceRootContents.iterator(); iter.hasNext();) {
            EObject nextChild = (EObject)iter.next();

            if (nextChild instanceof AnnotationContainer) {
                if (copyAllDescriptions) {
                    filteredChildren.add(nextChild);
                }
            } else if (nextChild instanceof TransformationContainer) {
                if (targetIsVirtual && sourceIsVirtual) {
                    filteredChildren.add(nextChild);
                }
            } else if (ModelObjectUtilities.isJdbcSource(nextChild)) {
                if (!targetIsVirtual) {
                    filteredChildren.add(nextChild);
                }

            } else {
                filteredChildren.add(nextChild);
            }
        }

        Collection /*<EObject>*/sourceFirstLevelChildrenCopies = null;
        // We cannot modify the source model, so create a deep copy of it
        try {
            sourceFirstLevelChildrenCopies = ModelerCore.getModelEditor().copyAll(filteredChildren);

            // don't allow 2 ModelAnnotations so if there is a ModelAnnotation in the target model remove it exists
            List targetChildren = targetModelResource.getEmfResource().getContents();

            for (int numKids = targetChildren.size(), i = 0; i < numKids; ++i) {
                Object kid = targetChildren.get(i);

                if (kid instanceof ModelAnnotation) {
                    targetChildren.remove(kid);
                    break;
                }
            }
        } catch (ModelerCoreException ex) {
            throw ex;
        }

        /*
         * Replicating the model objects is not quite good enough since the transformations may
         * still contain the name of the source model.
         * Need to replace those references with the new name.
         */
        SqlTransformationMappingRootSqlAspect.replaceTransformationLiteral(
                                                                           sourceFirstLevelChildrenCopies,
                                                                           sourceModelResource.getItemName(),
                                                                           targetModelResource.getItemName());

        // just add the nodes to the target:
        targetModelResource.getEmfResource().getContents().addAll(sourceFirstLevelChildrenCopies);

        // Need to re-set the model type here.
        ModelAnnotation modelAnnotation = targetModelResource.getModelAnnotation();
        modelAnnotation.setModelType(targetModelType);

        /*
         * Need to check that all the model imports are valid
         * When a model is copied it seems to leave behind imports referencing
         * models relative to the old location which can be invalid for the new location.
         *
         * Remove those import statements that are invalid for the new location.
         */
        EList imports = modelAnnotation.getModelImports();
        if (imports != null) {
            ResourceFinder resourceFinder;
            try {
                resourceFinder = ModelerCore.getModelContainer().getResourceFinder();
            } catch (CoreException ex) {
                throw new ModelerCoreException(ex);
            }

            //
            // Iterator through the imports and use the resource finder
            // to determine if the uri location of the import model is still
            // valid relative to the target model resource location
            //
            List<Object> mImports = Arrays.asList(imports.toArray());
            for (Object mImport : mImports) {
                ModelImport modelImport = (ModelImport) mImport;
                URI uri = URI.createURI(modelImport.getModelLocation());

                if (resourceFinder.isBuiltInResource(uri))
                    continue;

                Resource resource = resourceFinder.findByWorkspaceUri(uri, targetModelResource.getEmfResource());
                if (resource == null) {
                    // The resource location is invalid so remove it
                    imports.remove(mImport);
                }
            }
        }
        
        // Now we need to check if virtual, then call the NewModelObjectHelper .....
        if (targetIsVirtual) {
            try {
                List eObjects = targetModelResource.getEObjects();
                for (Iterator iter = eObjects.iterator(); iter.hasNext();) {
                    NewModelObjectHelperManager.helpCreate(iter.next(), extraProperties);
                }
            } catch (ModelerCoreException err) {
                throw err;
            }
        }
    }

    protected DefaultMutableTreeNode[] getChildrenOfNode( DefaultMutableTreeNode node ) {
        int numChildren = node.getChildCount();
        DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[numChildren];
        for (int i = 0; i < numChildren; i++) {
            children[i] = (DefaultMutableTreeNode)node.getChildAt(i);
        }
        return children;
    }

    protected Object getParent( Object curNode,
                                Collection /*<EObject>*/sourceFirstLevelChildrenCopies ) {
        Object parent;
        if (curNode == VIRTUAL_ROOT) {
            parent = null;
        } else if (sourceFirstLevelChildrenCopies.contains(curNode)) {
            parent = VIRTUAL_ROOT;
        } else {
            EObject obj = (EObject)curNode;
            parent = obj.eContainer();
        }
        return parent;
    }

    protected Collection getChildren( Object curNode,
                                      Collection /*<EObject>*/sourceFirstLevelChildrenCopies ) {
        Collection children;
        if (curNode == VIRTUAL_ROOT) {
            children = sourceFirstLevelChildrenCopies;
        } else {
            EObject obj = (EObject)curNode;
            children = obj.eContents();
        }
        return children;
    }

    protected void deleteChildAtIndex( Object parent,
                                       int index,
                                       Collection /*<EObject>*/sourceFirstLevelChildrenCopies ) throws ModelerCoreException {
        Collection children;
        if (parent == VIRTUAL_ROOT) {
            children = sourceFirstLevelChildrenCopies;
        } else {
            EObject obj = (EObject)parent;
            children = obj.eContents();
        }
        Iterator it = children.iterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }

        Object childToRemove = it.next();

        if (parent == VIRTUAL_ROOT) {
            sourceFirstLevelChildrenCopies.remove(childToRemove);
        }

        ModelerCore.getModelEditor().delete((EObject)childToRemove);
    }

    protected Object getChildAtIndex( Object parent,
                                      int index,
                                      Collection /*<EObject>*/sourceFirstLevelChildrenCopies ) {
        Collection children;
        if (parent == VIRTUAL_ROOT) {
            children = sourceFirstLevelChildrenCopies;
        } else {
            EObject obj = (EObject)parent;
            children = obj.eContents();
        }
        Iterator it = children.iterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }
        Object child = it.next();
        return child;
    }

    /**
     * Return the root of the tree as pared down to exclude any unneeded nodes. That is, only nodes which themselves are checked
     * or have any descendants that are checked will be included. Using a {@link javax.swing.tree.DefaultMutableTreeNode} for each
     * node in this tree because this class contains the simple logic needed (methods to get parent and get ordered children for
     * each node) without having any unncessary associated GUI, and because the getUserObject() method is needed to store the
     * position index for each node.
     * 
     * @return the root of the pared down tree, where all nodes are represented as a
     *         {@link javax.swing.tree.DefaultMutableTreeNode}.
     */
    protected DefaultMutableTreeNode getParedTreeRoot( InheritanceCheckboxTreeViewer viewer,
                                                       ModelResource modelResource ) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new IndexAndObject(0, modelResource));
        DefaultMutableTreeNode curNode = root;
        boolean done = false;
        while (!done) {
            IndexAndObject io = (IndexAndObject)curNode.getUserObject();
            Object obj = io.getObject();
            Object[] children = ((ITreeContentProvider)viewer.getContentProvider()).getChildren(obj);
            int childIndex = firstRequiredChildIndex(viewer, 0, children);
            if (childIndex >= 0) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new IndexAndObject(childIndex, children[childIndex]));
                curNode.add(newNode);
                curNode = newNode;
            } else {
                boolean nextNodeFound = false;
                while ((!nextNodeFound) && (!done)) {
                    io = (IndexAndObject)curNode.getUserObject();
                    Object prevNodeContentObject = io.getObject();
                    curNode = (DefaultMutableTreeNode)curNode.getParent();
                    if (curNode == null) {
                        done = true;
                    } else {
                        io = (IndexAndObject)curNode.getUserObject();
                        Object curNodeContentObject = io.getObject();
                        children = ((ITreeContentProvider)viewer.getContentProvider()).getChildren(curNodeContentObject);
                        int prevNodeIndex = indexOf(prevNodeContentObject, children);
                        childIndex = firstRequiredChildIndex(viewer, prevNodeIndex + 1, children);
                        if (childIndex >= 0) {
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new IndexAndObject(childIndex,
                                                                                                           children[childIndex]));
                            curNode.add(newNode);
                            curNode = newNode;
                            nextNodeFound = true;
                        }
                    }
                }
            }
        }
        return root;
    }

    protected int firstRequiredChildIndex( InheritanceCheckboxTreeViewer viewer,
                                           int startingIndex,
                                           Object[] children ) {
        int firstRequiredIndex = -1;
        int curIndex = startingIndex;
        while ((curIndex < children.length) && (firstRequiredIndex < 0)) {
            Object curChild = children[curIndex];
            if (viewer.getChecked(curChild) || TreeViewerUtil.anyDescendantChecked(viewer, curChild)) {
                firstRequiredIndex = curIndex;
            } else {
                curIndex++;
            }
        }
        return firstRequiredIndex;
    }

    protected int indexOf( Object obj,
                           Object[] array ) {
        int index = 0;
        while (obj != array[index]) {
            index++;
        }
        return index;
    }

    protected void insertInitialFirstLevelChildren( DefaultMutableTreeNode root,
                                                    List /*<EObject>*/firstLevelChildren,
                                                    int numToInsert ) {
        for (int i = 0; i < numToInsert; i++) {
            IndexAndObject io = new IndexAndObject(i, firstLevelChildren.get(i));
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(io);
            root.insert(child, i);
        }
    }

    protected void adjustIndexOfFirstLevelChildren( DefaultMutableTreeNode root,
                                                    int increment ) {
        DefaultMutableTreeNode[] children = getChildrenOfNode(root);
        for (int i = 0; i < children.length; i++) {
            IndexAndObject io = (IndexAndObject)children[i].getUserObject();
            io.setIndex(io.getIndex() + increment);
        }
    }

    protected Object getObjectForTreeNodeUserObject( Object treeNodeUserObject ) {
        IndexAndObject io = (IndexAndObject)treeNodeUserObject;
        return io.getObject();
    }
}// end StructuralCopyModelFeaturePopulator

/**
 * Auxilliary data class
 */
class IndexAndObject {
    private int index;
    private Object object;

    public IndexAndObject( int index,
                           Object object ) {
        super();
        this.index = index;
        this.object = object;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex( int newIndex ) {
        index = newIndex;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        String str = "index=" + index //$NON-NLS-1$
                     + ", object=" + object; //$NON-NLS-1$
        return str;
    }
}// end IndexAndObject
