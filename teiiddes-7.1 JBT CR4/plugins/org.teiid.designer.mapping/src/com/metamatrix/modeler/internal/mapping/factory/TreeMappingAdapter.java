/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
/**
 * The <code>TreeMappingAdapter</code> class 
 */
public class TreeMappingAdapter implements PluginConstants {
    //private PerformanceTracker pTracker = new PerformanceTracker(getClass().getName());
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Properties file key prefix. Used for logging and localization. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(TreeMappingAdapter.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    /////////////////////////////////////////////////////////////////////////////////////////////// 
    
    /** The tree root. */
    private EObject root;
    
    /** Structure content object to keep efficiently keep track of mapping classes, staging tables and their referenced locations
     * in XML Document tree
     * */
    private TreeMappingClassLocator mappingLocator;
    
    private boolean generatingMappingClasses = false;
    
    //private boolean doTrack = true;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>TreeMappingAdapter</code> for the specified tree root.
     * @param theTreeRoot the tree root
     */
    public TreeMappingAdapter(EObject theTreeRoot) {
        //startTracking("TreeMappingAdapter()"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(theTreeRoot);
        
        mappingLocator = new TreeMappingClassLocator(theTreeRoot);
        
        root = theTreeRoot;

        mappingLocator.loadTreeNodesToMappingClassScopeMap();
            
        //stopTracking("TreeMappingAdapter()"); //$NON-NLS-1$
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
//    public void resetTracker() {
//        pTracker.reset();
//        mappingLocator.resetTracker();
//    }
//    
//    
//    public void print() {
//        pTracker.print();
//        mappingLocator.print();
//        //System.out.println(mappingLocator.toString());
//    }
//    
//    private void startTracking(String method) {
//        if( doTrack ) {
//            pTracker.start(method);
//        }
//    }
//    
//    private void stopTracking(String method) {
//        if( doTrack ) {
//            pTracker.stop(method);
//        }
//    }
    
    public boolean isGeneratingMappingClasses() {
        return generatingMappingClasses;
    }
    
    public void setGeneratingMappingClasses(boolean isGenerating) {
        this.generatingMappingClasses = isGenerating;
        mappingLocator.setGeneratingMappingClasses(isGenerating);
    }
    
    public void addMappingClassAtLocation(EObject treeMappingRoot, MappingClass theMappingClass, EObject location) {
        mappingLocator.addMappingClassAtLocation(treeMappingRoot, theMappingClass, location);
    }
    
    public List getAllMappingClassLocations() {
        return new ArrayList(mappingLocator.getAllMappingClassLocations());
    }
    
    public void addStagingTableAtLocation(EObject treeMappingRoot, StagingTable theStagingTable, EObject location) {
        mappingLocator.addMappingClassAtLocation(treeMappingRoot, theStagingTable, location);
    }
    
    public boolean containsMappingClassWithName(String someName) {
        return mappingLocator.containsMappingClassWithName(someName);
    }

    /**
     * Maps the specified <code>MappingClass</code> to the specified tree node (<code>EObject</code>).
     * @param theMappingClass the <code>Mapping</code> input
     * @param theTreeNode the <code>Mapping</code> output
     * @throws IllegalArgumentException if either input parameter is <code>null</code>
     */
    public void addMappingClassLocation( MappingClass theMappingClass, EObject theTreeNode ) {
        //startTracking("addMappingClassLocation()"); //$NON-NLS-1$

        CoreArgCheck.isNotNull(theMappingClass);
        CoreArgCheck.isNotNull(theTreeNode);
        
        if( ! mappingLocator.containsLocation(theMappingClass, theTreeNode) ) {
            try {
                TreeMappingRoot treeMappingRoot = (TreeMappingRoot)mappingLocator.getMappingRoot(theMappingClass);
                mappingLocator.addOutputLocation(theMappingClass, theTreeNode);
                
//                if( !isGeneratingMappingClasses() ) {
                    addMappingClassAtLocation(treeMappingRoot, theMappingClass, theTreeNode);
//                }
                
            } catch (Exception e) {
                PluginConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        //stopTracking("addMappingClassLocation()"); //$NON-NLS-1$
    }
    
    /**
     * Maps the specified <code>MappingClassColumn</code> to the specified tree node (<code>EObject</code>).
     * @param theMappingColumn the <code>Mapping</code> input
     * @param theTreeNode the <code>Mapping</code> output
     * @throws IllegalArgumentException if either input parameter is <code>null</code>
     */
    public void addMappingClassColumnLocation(MappingClassColumn theMappingColumn, EObject theTreeNode) {
        //startTracking("addMappingClassColumnLocation()"); //$NON-NLS-1$

        CoreArgCheck.isNotNull(theMappingColumn);
        CoreArgCheck.isNotNull(theTreeNode);

        mappingLocator.addMappingClassColumnLocation(theMappingColumn, theTreeNode);

        //stopTracking("addMappingClassColumnLocation()"); //$NON-NLS-1$
    }
    
    /**
     * Creates a {@link MappingRoot} having the specified <code>MappingClass</code> as it's input.
     * @param theMappingClass the <code>MappingClass</code> used to create the mapping root
     * @return the index in the <code>mappingRoots</code> list of the new root
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public EObject createTreeMappingRoot(MappingClass theMappingClass) {
        //startTracking("createTreeMappingRoot(GENERATING)"); //$NON-NLS-1$

        CoreArgCheck.isNotNull(theMappingClass);
        if( mappingLocator.hasTreeRoot(theMappingClass) ) {
            return mappingLocator.getMappingRoot(theMappingClass);
        }
        
        TreeMappingRoot newRoot = null;
        try {
            // Defect 18433 - BML 8/31/05 - Changed call to create tree mapping root using a new
            // utility method that correctly adds it to the model (via addValue()) and also performs
            // additional work (i.e.adding nested Sql Helpers.
            newRoot = ModelResourceContainerFactory.createNewTreeMappingRoot(this.root, this.root.eResource());

            // Now add the mapping class to the Inputs list of the tree mapping root
            ModelerCore.getModelEditor().addValue(newRoot, theMappingClass, newRoot.getInputs());
                
        } catch (Exception theException) {
            Util.log(IStatus.ERROR,
                     theException,
                     Util.getString(PREFIX + "createMappingRootProblem", //$NON-NLS-1$
                                    new Object[] {root, theMappingClass}));
        }

        //stopTracking("createTreeMappingRoot(GENERATING)"); //$NON-NLS-1$
        return newRoot;
    }
    
    /**
     * Obtains all {@link MappingClass}es for this adapter's tree root.
     * @return the <code>MappingClass</code>es or an empty list
     */
    public List getAllMappingClasses() {
        //startTracking("getAllMappingClasses()"); //$NON-NLS-1$
        List returnList = Collections.unmodifiableList(mappingLocator.getMappingClasses());
        //stopTracking("getAllMappingClasses()"); //$NON-NLS-1$
        return returnList;
    }
    

    /**
     * Obtains all {@link StagingTable} for this adapter's tree root.
     * @return the <code>StagingTable</code>s or an empty list
     */
    public List getAllStagingTables() {
        return Collections.unmodifiableList(mappingLocator.getStagingTables());
    }

    /**
     * Obtains the <code>FragmentMappingAdapter</code> for this adapter's tree root.
     * @return the <code>FragmentMappingAdapter</code> (never <code>null</code>)
     */
    public FragmentMappingAdapter getFragmentMappingAdapter() {
        return mappingLocator.getFragmentAdapter();
    }
    
    /**
     * Obtains the tree node where the specified <code>StagingTable</code> is mapped.
     * @param theStagingTable the <code>StagingTable</code> whose mapped tree node is being requested
     * @return the mapped tree node
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public EObject getStagingTableOutputLocation(StagingTable theStagingTable) {
        return mappingLocator.getStagingTableLocation(theStagingTable);
    }

    /**
     * Obtains all tree nodes that are mapped to the specified <code>MappingClass</code>.
     * @param theMappingClass the <code>MappingClass</code> whose mapped tree nodes are being requested
     * @return an unmodifiable list of mapped tree nodes or an empty list
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public List getMappingClassOutputLocations(MappingClass theMappingClass) {
        //startTracking("getMappingClassOutputLocations()"); //$NON-NLS-1$

        List resultsList = Collections.unmodifiableList(mappingLocator.getMappingClassLocations(theMappingClass));

        //stopTracking("getMappingClassOutputLocations()"); //$NON-NLS-1$
        return resultsList;
    }

    /**
     * Obtains the tree nodes that are mapped to the specified <code>MappingClassColumn</code>.
     * @param theMappingColumn the <code>MappingClassColumn</code> whose mapped tree nodes are being requested
     * @return an unmodifiable list of mapped tree nodes or an empty list
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public List getMappingClassColumnOutputLocations(MappingClassColumn theMappingColumn) {
        return mappingLocator.getMappingClassColumnOutputLocations(theMappingColumn);
    }

    /**
     * Return the StagingTable located at the specified node, if one exists at this node.
     * @param theTreeNode
     * @return
     */
    public StagingTable getStagingTable(EObject theTreeNode) {
        CoreArgCheck.isNotNull(theTreeNode);
        
        return (StagingTable)mappingLocator.getStagingTable(theTreeNode);
    }

    
    
    /**
     * Return the MappingClass located at the specified node, if one exists at this node.
     * @param theTreeNode
     * @return
     */
    public EObject getMappingClassLocation( MappingClass theMappingClass ) {
        //startTracking("getMappingClassLocation()"); //$NON-NLS-1$
        CoreArgCheck.isNotNull( theMappingClass );
        List locations = mappingLocator.getMappingClassLocations( theMappingClass );
        EObject location = null;
        if( !locations.isEmpty() ) {
            location = (EObject)locations.get(0);
        }
        //stopTracking("getMappingClassLocation()"); //$NON-NLS-1$
        return location;
    }

    /**
     * Return the MappingClass located at the specified node, if one exists at this node.
     * @param theTreeNode
     * @return
     */
    public MappingClass getMappingClass(EObject theTreeNode) {
        CoreArgCheck.isNotNull(theTreeNode);

        return (MappingClass) mappingLocator.getMappingClass(theTreeNode);
    }
    
    public MappingClassColumn getMappingClassColumn(EObject theTreeNode, MappingClass theMappingClass) {
        CoreArgCheck.isNotNull(theTreeNode);
        
        MappingClassColumn result = mappingLocator.getMappingClassColumn(theTreeNode, theMappingClass);
        
        return result;
    }
    
    public MappingClass getMappingClassForTreeNode(EObject theTreeNode) {
        return mappingLocator.getMappingClassForTreeNode(theTreeNode);
    }

    /**
     * Obtain an ordered list of all locations visible in the TreeViewer that are in the extent
     * of the specified MappingClass. 
     * This method is based on getCoarseMappingExtentNodes(MappingClass theMappingClass) from
     * MappingAdapterFilter.
     * @param theMappingClass
     * @return
     */
    public List getTreeNodesInAMappingClassScope(MappingClass theMappingClass) {
        //startTracking("getTreeNodesInAMappingClassScope()"); //$NON-NLS-1$

        List extentNodes = mappingLocator.getTreeNodesInAMappingClassScope(theMappingClass);

        //stopTracking("getTreeNodesInAMappingClassScope()"); //$NON-NLS-1$
        return extentNodes;
    }
    
    /**
     * return a list of all location mapped to the attributes of the specified MappingClass
     * @param theMappingClass
     * @return
     */
    public List getColumnLocations(MappingClass theMappingClass) {
        return mappingLocator.getColumnLocations(theMappingClass);
    }

    /**
     * Obtains the <code>MappingClassColumn</code> where the specified tree node is mapped.
     * @param theTreeNode the tree node whose <code>MappingClassColumn</code> is being requested
     * @return the <code>MappingClassColumn</code> or <code>null</code> if not mapped
     */
    public MappingClassColumn getMappingClassColumn( EObject theTreeNode ) {
        /* 
         * jh Lyra enh:
         * 
         * This is a linear search, and the MCs are not necessarily in any 
         *    optimal order.  Let's replace this with a HashMap.
         * 
         * jhTODO       
         * Major question:  This map is created in the constructor of this class;
         *                  Should it be recreated any other times prior to
         *                  recreating this class (TreeMappingAdapter)?
         *                  Yes: on NewMappingLinkAction and DeleteMappingLinksAction
         *                  [fixed 2/1/2006]         
         */
        CoreArgCheck.isNotNull(theTreeNode);
        
        MappingClassColumn result = mappingLocator.getMappingClassColumn(theTreeNode);
//        
//        result = (MappingClassColumn)getTreeNodesToMappingClassColumnsMap( false ).get( theTreeNode );
//                
        return result;
    }
    
    /**
     * Indicates if the specified tree node has been mapped.
     * @param theTreeNode the tree node whose mapped status is being requested
     * @return <code>true</code> if mapped; <code>false</code> otherwise.
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public boolean isMapped(EObject theTreeNode) {
        CoreArgCheck.isNotNull(theTreeNode);
        return getMappingClassColumn(theTreeNode) != null;              
    }
    
    /** 
     * Indicates if the specified tree node has been mapped.
     * @param theTreeNode the tree node whose mapped status is being requested
     * @return <code>true</code> if mapped; <code>false</code> otherwise.
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public StagingTable getStagingTableForRootTreeNode( EObject theTreeNode ) {     
        return (StagingTable) mappingLocator.getStagingTable(theTreeNode);
    }

    /**
     * Removes the specified {@link org.eclipse.emf.mapping.Mapping}. 
     * @param theMappingClass the <code>Mapping</code> input
     * @param theTreeNode the <code>Mapping</code> output
     * @throws IllegalArgumentException if either input parameter is <code>null</code>
     */
    public void removeMappingClassLocation(MappingClass theMappingClass,
                                EObject theTreeNode) {
        CoreArgCheck.isNotNull(theMappingClass);
        CoreArgCheck.isNotNull(theTreeNode);
        
        try {
            mappingLocator.removeOutputLocation(theMappingClass, theTreeNode);
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, Util.getString(PREFIX + "removeLocationTreeNodeNotFound", //$NON-NLS-1$
                                                                   new Object[] {theTreeNode,
                                                                   "MappingClass", //$NON-NLS-1$
                                                                   theMappingClass}));
        }
    }
    
    /**
     * Deletes all mappings associated with the specified MappingClass, but does not delete
     * the MappingClass itself.
     * @param theMappingClass
     * @throws ModelerCoreException
     */
    public void deleteMappingClass(MappingClass theMappingClass) throws ModelerCoreException {
        CoreArgCheck.isNotNull(theMappingClass);
        
        mappingLocator.deleteMappingClass(theMappingClass);
    }

    /**
     * Removes the specified {@link org.eclipse.emf.mapping.Mapping}. 
     * @param theMappingColumn the <code>Mapping</code> input
     * @param theTreeNode the <code>Mapping</code> output
     * @throws IllegalArgumentException if either input parameter is <code>null</code>
     */
    public void removeMappingClassColumnLocation(MappingClassColumn theMappingColumn,
                                EObject theTreeNode) {

       CoreArgCheck.isNotNull(theMappingColumn);
       CoreArgCheck.isNotNull(theTreeNode);
       
       mappingLocator.removeMappingClassColumnLocation(theMappingColumn, theTreeNode);

    }
    
    public List getParentMappingClasses(MappingClass theMappingClass, EObject docRoot, boolean includeStagingTables) {
        return getParentMappingClasses(theMappingClass, new DefaultMappableTree(docRoot), includeStagingTables);
    }   
    
    /**
     * Obtain an ordered list of all MappingClasses located above the specified instance. 
     * Note: this method may also be called for StagingTables, which extend MappingClass.
     * @param theMappingClass the MappingClass or StagingTable
     * @return
     */
    public List getParentMappingClasses(MappingClass theMappingClass, IMappableTree theMappableTree, boolean includeStagingTables) {
        List result = new ArrayList();
        List locations = null;
        
        if ( theMappingClass instanceof StagingTable ) {
            locations = new ArrayList();
            locations.add(getStagingTableOutputLocation((StagingTable) theMappingClass));
            
        } else {
            locations = new ArrayList();
            locations.addAll(getMappingClassOutputLocations(theMappingClass));
        }
        List mappingClasses = getAllMappingClasses();
        for ( Iterator iter = mappingClasses.iterator() ; iter.hasNext() ; ) {
            MappingClass mc = (MappingClass) iter.next();
            
            MAPPING_CLASS_LOOP:
            if ( mc != null && ! mc.equals(theMappingClass) ) {
                for ( Iterator testLocIter = getMappingClassOutputLocations(mc).iterator() ; testLocIter.hasNext() && locations.size() != 0; ) {
                    EObject possibleLoc = (EObject) testLocIter.next();
                    for ( int i=0 ; i<locations.size() ; ++i ) {
                        // mc is a parent if one of it's locations is an ancestor of theMappingClass's location
                        if ( theMappableTree.isAncestorOf(possibleLoc, (EObject) locations.get(i)) ) {
                            result.add(mc);
                            break MAPPING_CLASS_LOOP;
                        } 
                    }
                }
            }
        }

        return result;
    }
    
    /**
     * Obtain the document node eObject for this mapping
     * @return
     */
    public EObject getDocument() {
        List mappingRoots = mappingLocator.getMappingRoots();
        if( mappingRoots != null && !mappingRoots.isEmpty() ) {
            EObject firstRoot = (EObject)mappingRoots.get(0);
            if( firstRoot != null && firstRoot instanceof TransformationMappingRoot ) {
                return ((TransformationMappingRoot)firstRoot).getTarget();
            }
        }
        
        return null;
    }
    
}
