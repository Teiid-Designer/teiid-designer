/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingRoot;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.FragmentMappingRoot;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ListAndMapUtil;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.mapping.PluginConstants;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.factory.MappableTreeIterator;


/** 
 * TreeMappingClassLocator provides a data-structure and data management methods to maintain an efficient set of information about
 * Mapping Classes and Staging Tables mapped and referenced to a specific XML Document & XML Root
 * This class is designed to work in conjunction with the MappingClassFactory and contains a companion locator:
 * TreeMappingClassColumnLocator to handle the Mapping Class Column mappings and locations.
 * 
 * This improves performance and simplifies follow-on code maintenance.
 * Created: BML 5/9/07
 * @since 5.0
 */
public class TreeMappingClassLocator implements PluginConstants {
    // ---------------------------------------------------------------------------------------------------------------------------
    // CONSTANTS
    // ---------------------------------------------------------------------------------------------------------------------------
    //private PerformanceTracker pTracker = new PerformanceTracker(getClass().getName());
    
    // ---------------------------------------------------------------------------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------------------------------------------------------------------------
    
    /** The model contents object to assist in creating an EMF Tree iterator */
    private ModelContents modelContents;
    
    /** The tree root. */
    private EObject root;
    
    /** ITreeToRelationalMapper */
    private ITreeToRelationalMapper mapper;
    
    /** Adapter for fragments. */
    private FragmentMappingAdapter fragmentAdapter;
    
    /** Mapping Class Column Locator */
    private TreeMappingClassColumnLocator columnMappingLocator;
    
    /** Collection of {@link org.eclipse.emf.mapping.MappingRoot}s. */
    private List mappingRoots;
    /** Collection of {@link com.metamatrix.metamodels.transformation.FragmentMappingRoot}s. */
    private List fragmentRoots;
    
    /** Maps to give quicker access to mapping class, staging table, tree root and location info */
    
    // KEY = StagingTable, VALUE = TreeMappingRoot
    private HashMap stagingTablesToRootMap      = new HashMap();
    // KEY = MappingClass, VALUE = TreeMapping Root
    private HashMap mappingClassesToRootMap     = new HashMap();
    // KEY = tree location (EObject), VALUE = MappingClass
    // Note:  Multiple locations can reference the same mapping class
    private HashMap locationToMappingClassMap   = new HashMap();
    // KEY = tree location, VALUE = StagingTable
    private HashMap locationToStagingTableMap   = new HashMap();
    // KEY = StagingTable, VALUE = location
    private HashMap stagingTableLocationMap     = new HashMap();
    // KEY = MappingClass Name, Value = MappingClassName
    private HashMap mappingClassesNameMap     = new HashMap();
    
    // Ordered list of MappingClasses
    private List mappingClassesArray = Collections.EMPTY_LIST;
    // Ordered List of StagingTables
    private List stagingTablesArray = Collections.EMPTY_LIST;
    
    /** Map of key=tree node, value=Mapping Class 
     *      This map relates each tree node to a mapping class, if any,
     *      whose scope it is in, independent of whether or not it is currently
     *      MAPPED.
     * */
    private HashMap treeNodesToMappingClassScopeMap = new HashMap();
    
    private boolean hasChanges = false;
    
    private boolean generatingMappingClasses = false;
    
    
    // ---------------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------------------------------------------------------------------
    /** 
     * Basic Constructor. Requires a valid XML Document Tree Root object
     * @since 5.0
     */
    public TreeMappingClassLocator(EObject treeRoot) {
        super();
        this.root = treeRoot;
        this.modelContents = ModelerCore.getModelEditor().getModelContents(root);
        this.mapper = ModelMapperFactory.createModelMapper(root);
        this.columnMappingLocator = new TreeMappingClassColumnLocator(this);
        initialize();
    }
    // ---------------------------------------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    // ---------------------------------------------------------------------------------------------------------------------------
    
    
    public boolean isGeneratingMappingClasses() {
        return generatingMappingClasses;
    }
    
    public void setGeneratingMappingClasses(boolean isGenerating) {
        boolean finished = false;
        if( this.generatingMappingClasses && !isGenerating) {
            finished = true;
        }
        this.generatingMappingClasses = isGenerating;
        if( finished ) {
            resetIfChanged();
        }
    }
    
//    private void startTracking(String method) {
//         pTracker.start(method);
//    }
//    
//    private void stopTracking(String method) {
//        pTracker.stop(method);
//    }
//    
//    public void print() {
//        pTracker.print();
//        columnMappingLocator.print();
//    }
//    
//    public void resetTracker() {
//        pTracker.reset();
//        columnMappingLocator.resetTracker();
//    }
    
    /**
     * Get the XML Document tree root for this mapping
     * @return the root
     * @since 5.0
     */
    public EObject getDocumentTreeRoot() {
        return this.root;
    }
    
    /**
     *  Get the ITreeToRelationalMapper for this mapping
     * @return the mapper
     * @since 5.0
     */
    public ITreeToRelationalMapper getMapper() {
        return this.mapper;
    }
    
    /**
     * return the FragmentMappingAdapter 
     * @return
     * @since 5.0
     */
    public FragmentMappingAdapter getFragmentAdapter() {
        return this.fragmentAdapter;
    }
    
    /**
     * Get the ordered List of MappingClasses currently defined and referenced within the XML Document root 
     * @return
     * @since 5.0
     */
    public List getMappingClasses() {
        resetIfChanged();
        
        return this.mappingClassesArray;
    }
    
    /**
     * Get the mapping class referenced by this location.
     * @param treeNodeLocation
     * @return mapping class EObject, may be null
     * @since 5.0
     */
    public EObject getMappingClass(EObject treeNodeLocation) {
        //startTracking("getMappingClass()"); //$NON-NLS-1$
        resetIfChanged();
        EObject theMappingClass = (EObject)locationToMappingClassMap.get(treeNodeLocation);
        //stopTracking("getMappingClass()"); //$NON-NLS-1$
        return theMappingClass;
    }
    
    /**
     * Get ordered List of StagingTable's currently defined and referenced within the XML Document root 
     * @return
     * @since 5.0
     */
    public List getStagingTables() {
        resetIfChanged();
        
        return stagingTablesArray;
    }
    
    public EObject getStagingTable(EObject treeNodeLocation) {
        return (EObject)locationToStagingTableMap.get(treeNodeLocation);
    }
    
    public List getFragmentRoots() {
        return fragmentRoots;
    }
    
    public List getMappingClassLocations(EObject mappingClass) {
        //startTracking("getMappingClassLocations()"); //$NON-NLS-1$
        resetIfChanged();
        
        List locations = new ArrayList();
        Iterator iter = locationToMappingClassMap.keySet().iterator();
        // Gather up all locations for this mapping class
        while( iter.hasNext() ) {
            EObject nextLoc = (EObject)iter.next();
            EObject nextMC = (EObject)locationToMappingClassMap.get(nextLoc);
            if( nextMC == mappingClass ) {
                locations.add(nextLoc);
            }
        }
        //stopTracking("getMappingClassLocations()"); //$NON-NLS-1$
        return locations;
    }
    
    public EObject getStagingTableLocation(EObject stagingTable) {
        resetIfChanged();
        
        return (EObject)stagingTableLocationMap.get(stagingTable);
    }
    
    public boolean hasTreeRoot(MappingClass mappingClass) {
        return mappingClassesToRootMap.get(mappingClass) != null;
    }
    
    public EObject getMappingRoot(MappingClass mappingClass) {
        //startTracking("getMappingRoot()"); //$NON-NLS-1$
        EObject mappingRoot = null;
        if( mappingClass instanceof StagingTable ) {
            mappingRoot = (EObject)stagingTablesToRootMap.get(mappingClass);
        } else {
            mappingRoot = (EObject)mappingClassesToRootMap.get(mappingClass);
        }
        //stopTracking("getMappingRoot()"); //$NON-NLS-1$
        return mappingRoot;
    }
    
    public List getMappingRoots() {
        return mappingRoots;
    }
    
    public List getAllMappingClassLocations() {
        //startTracking("getAllMappingClassLocations()"); //$NON-NLS-1$
        resetIfChanged();
        
        List result = new ArrayList(locationToMappingClassMap.keySet());
        
        //stopTracking("getAllMappingClassLocations()"); //$NON-NLS-1$
        return result;
    }
    
    
    public void addMappingClassAtLocation(EObject treeMappingRoot, MappingClass mappingClass, EObject location) {
        //startTracking("addMappingClassAtLocation()"); //$NON-NLS-1$
        hasChanges = true;
        // MAPPING CLASS
        if( mappingClass instanceof StagingTable ) {
            // MAPPING CLASS
            if( stagingTablesToRootMap.get(mappingClass) == null ) {
                stagingTablesToRootMap.put(mappingClass, treeMappingRoot);
                mappingRoots.add(treeMappingRoot);
            }
            // Note that Mapping Classes CAN be mapped to multiple locations 
            locationToStagingTableMap.put( location, mappingClass );
            stagingTableLocationMap.put(mappingClass, location);
        } else {
            if( mappingClassesToRootMap.get(mappingClass) == null ) {
                mappingClassesToRootMap.put(mappingClass, treeMappingRoot);
                mappingRoots.add(treeMappingRoot);
                mappingClassesNameMap.put(mappingClass.getName(), mappingClass.getName());
            }
            // Note that Mapping Classes CAN be mapped to multiple locations 
            locationToMappingClassMap.put( location, mappingClass );
        }
        addOutputLocation(mappingClass, location);
        //stopTracking("addMappingClassAtLocation()"); //$NON-NLS-1$
    }
    
    public void removeMappingClassFromLocation(EObject treeMappingRoot, MappingClass mappingClass, EObject location) {
        //startTracking("removeMappingClassFromLocation()"); //$NON-NLS-1$
        hasChanges = true;
        // MAPPING CLASS
        if( mappingClassesToRootMap.get(mappingClass) != null ) {
            mappingClassesToRootMap.remove(mappingClass);
            mappingRoots.remove(treeMappingRoot);
            mappingClassesNameMap.remove(mappingClass.getName());
        }
        // Note that Mapping Classes CAN be mapped to multiple locations 
        locationToMappingClassMap.remove( location);
        
        removeOutputLocation(mappingClass, location);
        //stopTracking("removeMappingClassFromLocation()"); //$NON-NLS-1$
    }
    
    public boolean containsLocation(EObject mappingClass, EObject location) {
        //startTracking("containsLocation()"); //$NON-NLS-1$
        resetIfChanged();
        
        boolean result = false;
        if( mappingClass instanceof StagingTable ) {
            EObject cachedST = (EObject)locationToStagingTableMap.get(location);
            result = cachedST != null && cachedST == mappingClass;
        } else {
            EObject cachedMC = (EObject)locationToMappingClassMap.get(location);
            result = cachedMC != null && cachedMC == mappingClass;
        }
        //stopTracking("containsLocation()"); //$NON-NLS-1$
        return result;
    }
    
    public boolean containsMappingClassWithName(String someName) {
        return mappingClassesNameMap.get(someName) != null;
    }
    
    public boolean addOutputLocation(MappingClass theMappingClass, EObject theTreeNode) {
        boolean result = false;
        //startTracking("addOutputLocation()"); //$NON-NLS-1$
        try {
            TreeMappingRoot treeMappingRoot = (TreeMappingRoot)getMappingRoot(theMappingClass);
            if( !treeMappingRoot.getOutputs().contains(theTreeNode) ) {
                ModelerCore.getModelEditor().addValue(treeMappingRoot, theTreeNode, treeMappingRoot.getOutputs());
                //addEntryToTreeNodesToMappingClassScopeMap( theMappingClass );
                hasChanges = true;
            }
        } catch (Exception e) {
            PluginConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
        
        //stopTracking("addOutputLocation()"); //$NON-NLS-1$
        return result;
    }
    
    public boolean removeOutputLocation(MappingClass theMappingClass, EObject theTreeNode) {
        boolean result = false;
        //startTracking("removeOutputLocation()"); //$NON-NLS-1$
        try {
            TreeMappingRoot treeMappingRoot = (TreeMappingRoot)getMappingRoot(theMappingClass);
            if( treeMappingRoot.getOutputs().contains(theTreeNode) ) {
                ModelerCore.getModelEditor().removeValue(treeMappingRoot, theTreeNode, treeMappingRoot.getOutputs());
                removeEntryFromTreeNodesToMappingClassScopeMap(theMappingClass);
                hasChanges = true;
            }
        } catch (Exception e) {
            PluginConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
        
        //stopTracking("removeOutputLocation()"); //$NON-NLS-1$
        return result;
    }
    
    public void deleteMappingClass(EObject mappingClass) throws ModelerCoreException {
        //startTracking("deleteMappingClass()"); //$NON-NLS-1$
        if( mappingClass instanceof StagingTable) {
            EObject treeMappingRoot = (EObject)stagingTablesToRootMap.get(mappingClass);
            if( treeMappingRoot != null ) {
                mappingClassesToRootMap.remove(mappingClass);
                ModelerCore.getModelEditor().delete(treeMappingRoot,true,false);
            }
            ModelerCore.getModelEditor().delete(mappingClass);
            
            // Remove all "Locations" for this mapping Class
            EObject location = (EObject)stagingTableLocationMap.get(mappingClass);
            if( location != null ) {
                locationToStagingTableMap.remove(location);
            }
            stagingTableLocationMap.remove(mappingClass);
        } else {
            EObject treeMappingRoot = (EObject)mappingClassesToRootMap.get(mappingClass);
            if( treeMappingRoot != null ) {
                mappingClassesToRootMap.remove(mappingClass);
                ModelerCore.getModelEditor().delete(treeMappingRoot,true,false);
            }
            ModelerCore.getModelEditor().delete(mappingClass);
            
            // Remove all "Locations" for this mapping Class
            List locations = getMappingClassLocations(mappingClass);
            for( Iterator iter = locations.iterator(); iter.hasNext(); ) {
                locationToMappingClassMap.remove(iter.next());
            }
        }
        
        mappingClassesNameMap.remove( ((MappingClass)mappingClass).getName());

        //stopTracking("deleteMappingClass()"); //$NON-NLS-1$
        hasChanges = true;
    }
    
    
    /**
     * Recursive method used by getCoarseExtentNodes to walk down a branch of the tree and find all visible nodes in the extent.
     * @param visibleNode the branch node that this method will look beneath
     * @param columnLocations a Collection of tree nodes that should automatically be added in the result
     * @param mappingClassLocations a Collection of mapping class locations.  Any node inside this collection should
     * not be added to the result.
     * @param visibleTreeNodes a Collection of all tree nodes that are visible in the tree.  Nodes that are not in this Collection
     * should not be added to the result.
     * @return
     */
    public List gatherCoarseExtentNodes( EObject visibleNode, HashMap columnLocationsMap ) {
        
        ArrayList result = new ArrayList();
        for ( Iterator childIter = getMapper().getMappableTree().getChildren(visibleNode).iterator() ; childIter.hasNext() ; ) {
            EObject node = (EObject) childIter.next();
            // first, make sure the child is visible

                // next, check to see if this node is mapped into the MappingClass by checking columnLocations
            
                // jh PERFORMANCE: change columnLocations from a List to a Map,
                if ( columnLocationsMap.get(node) != null ) {
                    // if so, then this node is in the extent
                    result.add(node);
                    // recurse down this node's children
                    result.addAll( gatherCoarseExtentNodes( node, columnLocationsMap ) );
                } else {
                    // see if there is a mapping class located at this node
                    // jh PERFORMANCE: change mappingClassLocations from a List to a Map,
                    if ( getMappingClass(node) != null ) {
                        // stop; this node is in another extent.  do not check this node's children.
                    } else {
                        // this node is in the extent
                        result.add(node);
                        // recurse down this node's children
                        result.addAll(gatherCoarseExtentNodes (node, columnLocationsMap ) );
                    }
                }
            
            
        }
        return result;
    }
    
    public MappingClass getMappingClassForTreeNode(EObject theTreeNode) {
        //startTracking("getMappingClassForTreeNode()"); //$NON-NLS-1$
        MappingClass result = (MappingClass)treeNodesToMappingClassScopeMap.get(theTreeNode);
        //stopTracking("getMappingClassForTreeNode()"); //$NON-NLS-1$
        return result;
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
        resetIfChanged();
        
        List columnLocations = getColumnLocations(theMappingClass);
        HashMap columnLocationsMap = ListAndMapUtil.createMapFromList(columnLocations);
        
        List locations = getMappingClassLocations(theMappingClass);
        
        List extentNodes = Collections.EMPTY_LIST;
        
        if (!locations.isEmpty()) {
            extentNodes = new ArrayList();
            for (int size = locations.size(), i = 0; i < size; i++) {
                // iterate over every visible location node
                // add the location to the collection of extent nodes
                extentNodes.add(locations.get(i));

                // recurse down this location and collect up the extent nodes
                extentNodes.addAll( gatherCoarseExtentNodes( (EObject) locations.get(i), columnLocationsMap ) );
            }
        }
        //stopTracking("getTreeNodesInAMappingClassScope()"); //$NON-NLS-1$
        return extentNodes;
    }
    
    public void loadTreeNodesToMappingClassScopeMap() {
        //startTracking("loadTreeNodesToMappingClassScopeMap()"); //$NON-NLS-1$
        treeNodesToMappingClassScopeMap = new HashMap(mappingClassesArray.size());                
        
        // for each mapping class, get the treenodes in its scope
        for (int size = mappingClassesArray.size(), i = 0; i < size; i++) {
            MappingClass mappingClass = (MappingClass)mappingClassesArray.get(i);

            if (mappingClass != null) {
                addEntryToTreeNodesToMappingClassScopeMap( mappingClass );
            }
        }
        columnMappingLocator.loadTreeNodesToMappingClassColumnsMap();
        //stopTracking("loadTreeNodesToMappingClassScopeMap()"); //$NON-NLS-1$
    }
    
    private void addEntryToTreeNodesToMappingClassScopeMap( MappingClass mappingClass ) {
        //startTracking("addEntryToTreeNodesToMappingClassScopeMap()"); //$NON-NLS-1$
        List lstNodes = getTreeNodesInAMappingClassScope( mappingClass );
        
        if ( ( lstNodes != null ) && !lstNodes.isEmpty() ) {
            for ( int iNodes = lstNodes.size(), j = 0; j < iNodes; j++ ) {
                EObject eoTemp = (EObject)lstNodes.get( j );
                
                /*
                 * Now qualify these the way you would the treenodes in MappingAdapterFilter,
                 * namely, we only want mappable columns, so we ignore containers and elements
                 * that have children.  Can we ask isMappable() on elements to determine if it
                 * has children?
                 */
                
                // is isMappable enough?? Let's hope so..........
                if ( getMapper().isMappable( eoTemp ) ) {
                    /*
                     * jh Defect 21277: Shouldn't we also do this when we add a new MC?
                     * ( Also see: getTreeNodesToMappingClassColumnsMap
                     */
                    treeNodesToMappingClassScopeMap.put( eoTemp, mappingClass );
                }
            }
        }
        //stopTracking("addEntryToTreeNodesToMappingClassScopeMap()"); //$NON-NLS-1$
    }
    
    /*
     * jh Defect 21277: This method allows us to remove an MC from this map
     */
    private void removeEntryFromTreeNodesToMappingClassScopeMap( MappingClass mappingClass ) {                
        //startTracking("removeEntryFromTreeNodesToMappingClassScopeMap()"); //$NON-NLS-1$
        Collection colNodes = treeNodesToMappingClassScopeMap.keySet();
        
        if ( ( colNodes != null ) && !colNodes.isEmpty() ) {
            Iterator it = colNodes.iterator();
            
            while( it.hasNext() ) {
                EObject tempNodeKey = (EObject)it.next();
                
                // get the MC for this tree node key
                MappingClass tempMappingClass = (MappingClass)treeNodesToMappingClassScopeMap.get( tempNodeKey );
                
                // if this MC is the same as our arg, remove the key
                if ( tempMappingClass == mappingClass ) {
                    it.remove();
                }
            }
        }
        //stopTracking("removeEntryFromTreeNodesToMappingClassScopeMap()"); //$NON-NLS-1$
    }
    
    // ----------------------------
    // Mapping Class Column Methods
    // ----------------------------
    public void addMappingClassColumnLocation(MappingClassColumn theMappingColumn, EObject theTreeNode) {
        hasChanges = true;
        columnMappingLocator.addMappingClassColumnLocation(theMappingColumn, theTreeNode);
    }
    
    public void removeMappingClassColumnLocation(MappingClassColumn theMappingColumn, EObject theTreeNode) {
        hasChanges = true;
        columnMappingLocator.removeMappingClassColumnLocation(theMappingColumn, theTreeNode);
    }
    
    public MappingList getMappingClassColumnMappingList(MappingClassColumn theMappingColumn) {
        resetIfChanged();
        
        return columnMappingLocator.getMappingClassColumnMappingList(theMappingColumn);
    }
    
    public MappingClassColumn getMappingClassColumn(EObject theTreeNode, MappingClass theMappingClass) {
        resetIfChanged();
        
        return columnMappingLocator.getMappingClassColumn(theTreeNode, theMappingClass);
    }
    
    public MappingClassColumn getMappingClassColumn( EObject theTreeNode ) {
        resetIfChanged();
        
        return columnMappingLocator.getMappingClassColumn(theTreeNode);
    }
    
    public List getMappingClassColumnOutputLocations(MappingClassColumn theMappingColumn) {
        resetIfChanged();
        
        return columnMappingLocator.getMappingClassColumnOutputLocations(theMappingColumn);
    }
    
    /**
     * return a list of all location mapped to the attributes of the specified MappingClass
     * @param theMappingClass
     * @return
     */
    public List getColumnLocations(MappingClass theMappingClass) {
        resetIfChanged();
        
        List result = new ArrayList();
        for (Iterator iter = theMappingClass.getColumns().iterator() ; iter.hasNext() ; ) {
            result.addAll(getMappingClassColumnOutputLocations((MappingClassColumn) iter.next()));
        }
        return result;
    }
//    
//    public void loadTreeNodesToMappingClassColumnsMap() {
//        columnMappingLocator.loadTreeNodesToMappingClassColumnsMap();
//    }
    
    // ---------------------------------------------------------------------------------------------------------------------------
    // PRIVATE METHODS
    // ---------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Interrogate this treeMappingRoot, find and organize the mapping classes and staging tables into maps
     */
    private void initialize() {
        fragmentRoots = new ArrayList();
        mappingRoots = modelContents.getTransformations(root);
    
        if ((mappingRoots == null) || mappingRoots.isEmpty()) {
            mappingRoots = new ArrayList();
        } else {
            for (int size = mappingRoots.size(), i = 0; i < size; i++) {
                MappingRoot mappingRoot = (MappingRoot)mappingRoots.get(i);
                
                Iterator outputIter = mappingRoot.getOutputs().iterator();
                
                if (mappingRoot instanceof TreeMappingRoot) {
                    MappingClass mappingClass = getMappingClass(mappingRoot);
            
                    if (mappingClass instanceof StagingTable) {
                        // STAGING TABLE
                        stagingTablesToRootMap.put(mappingClass, mappingRoot);
                        
                        while ( outputIter.hasNext() ) {
                            // NOTE that STAGING Tables can NOT be mapped to multiple locations so we only have to get the
                            // first output object
                            Object oTreeNode = outputIter.next();
                            stagingTableLocationMap.put( mappingClass, oTreeNode );
                            locationToStagingTableMap.put(oTreeNode, mappingClass);
                        }
                    } else {
                        // MAPPING CLASS
                        mappingClassesToRootMap.put(mappingClass, mappingRoot);
                        mappingClassesNameMap.put(mappingClass.getName(), mappingClass.getName());
                        // Note that Mapping Classes CAN be mapped to multiple locations 
                        while ( outputIter.hasNext() ) {
                            Object oTemp = outputIter.next();
                            locationToMappingClassMap.put( oTemp, mappingClass );
                        }
                    }
                } else if (mappingRoot instanceof FragmentMappingRoot) {
                    fragmentRoots.add(mappingRoot);
                }
            }
        }

        fragmentAdapter = new FragmentMappingAdapter(root, getFragmentRoots());
        resetOrderedLists();
    }
    
    private void resetIfChanged() {
        if( hasChanges && !isGeneratingMappingClasses() ) {
            resetOrderedLists();
            loadTreeNodesToMappingClassScopeMap();
        }
    }

    private void resetOrderedLists() {
        //System.out.println("  <<<< TEMP LOGGING >>>> TreeMappingClassLocator.resetOrderedLists()  # MC Locations =" + locationToMappingClassMap.size()); //$NON-NLS-1$
        //startTracking("resetOrderedLists()"); //$NON-NLS-1$
        if( mappingRoots.isEmpty() ) {
            // DON'T Want to do anything if there are NO roots except set the arrays to NULL (i.e. clear maps and arrays);
            clear();
            return;
        }
        // MappingClasses
        // Get Copy of List of Locations
        
        XmlDocumentMappingClassVisitor visitor = new XmlDocumentMappingClassVisitor(locationToMappingClassMap, locationToStagingTableMap);
        
        MappableTreeIterator nodeIter = new MappableTreeIterator(mapper.getMappableTree());
        
        if ( nodeIter.hasNext() ) {
            // skip over the root itself
            nodeIter.next();
        }
        
        while ( nodeIter.hasNext() ) {
            visitor.visit((EObject) nodeIter.next());
        }
        
        mappingClassesArray = visitor.getOrderedMappingClasses();
        
        stagingTablesArray = visitor.getOrderedStagingTables();
        hasChanges = false;
        //System.out.println(this.toString());
        //stopTracking("resetOrderedLists()"); //$NON-NLS-1$
    }
    
    private void clear() {
        // DON'T Want to do anything if there are NO roots except set the arrays to NULL;
        mappingClassesArray = Collections.EMPTY_LIST;
        stagingTablesArray = Collections.EMPTY_LIST;
        if( !locationToMappingClassMap.isEmpty() ) {
            locationToMappingClassMap.clear();
        }
        if( !stagingTablesToRootMap.isEmpty() ) {
            stagingTablesToRootMap.clear();
        }
        if( !mappingClassesToRootMap.isEmpty() ) {
            mappingClassesToRootMap.clear();
        }
        if( !locationToMappingClassMap.isEmpty() ) {
            locationToMappingClassMap.clear();
        }
        if( !locationToStagingTableMap.isEmpty() ) {
            locationToStagingTableMap.clear();
        }
        if( !stagingTableLocationMap.isEmpty() ) {
            stagingTableLocationMap.clear();
        }
        if( !treeNodesToMappingClassScopeMap.isEmpty() ) {
            treeNodesToMappingClassScopeMap.clear();
        }
        if( !mappingClassesNameMap.isEmpty() ) {
            mappingClassesNameMap.clear();
        }
    }
    
    /**
     * Obtains the <code>MappingClass</code> or <code>StagingTable</code> associated with the specified
     * <code>MappingRoot</code>. The return value can also be a <code>StagingTable</code> since it is
     * a subclass of <code>MappingClass</code>.
     * @param theMappingRoot the <code>MappingRoot</code> whose <code>MappingClass</code> is being requested
     * @return the <code>MappingClass</code> or <code>null</code> if none found
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    private MappingClass getMappingClass(MappingRoot theMappingRoot) {
        ArgCheck.isNotNull(theMappingRoot);
        
        MappingClass result = null;
        List inputs = theMappingRoot.getInputs();
        int size = inputs.size();
        
        if (size > 0) {
            result = (MappingClass)inputs.get(0);
        }
        
        return result;
    }
    
    /**
     *  
     * @see java.lang.Object#toString()
     * @since 5.0
     */
    @Override
    public String toString() {
        int buffSize = 100 + 80*mappingClassesArray.size() + 80*stagingTablesArray.size();
        StringBuffer buffer = new StringBuffer(buffSize);
        
        buffer.append("\n  TreeToEObjectMapping Contents:"); //$NON-NLS-1$
        
        buffer.append('\n').append("  # Mapping Classes = " + mappingClassesArray.size()); //$NON-NLS-1$
        Iterator iter = null;
        String name = null;
        EObject nextEObj = null;
        int count = 0;
        for( iter = mappingClassesArray.iterator(); iter.hasNext(); ) {
            nextEObj = (EObject)iter.next();
            name = ModelerCore.getModelEditor().getName(nextEObj);
            buffer.append('\n').append("        [" + count + "] " + name); //$NON-NLS-1$ //$NON-NLS-2$
            count++;
        }
        count = 0;
        buffer.append('\n').append("  # Staging Tables = " + stagingTablesArray.size()); //$NON-NLS-1$
        for( iter = stagingTablesArray.iterator(); iter.hasNext(); ) {
            nextEObj = (EObject)iter.next();
            name = ModelerCore.getModelEditor().getName(nextEObj);
            buffer.append('\n').append("        [" + count + "] " + name); //$NON-NLS-1$ //$NON-NLS-2$
            count++;
        }
        buffer.append('\n');
        
        return buffer.toString();
    }
}
