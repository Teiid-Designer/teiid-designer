/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.mapping.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ListAndMapUtil;
import com.metamatrix.modeler.mapping.PluginConstants;


/** 
 * TreeMappingClassColumnLocator provides a data-structure and data management methods to maintain an efficient set of information about
 * Mapping Class Columns  mapped and referenced to a specific XML Document & XML Root
 * This class is designed to work in conjunction with the TreeMappingClassLocator (and hence MappingClassFactory).
 * 
 * This improves performance and simplifies follow-on code maintenance.
 * Created: BML 5/9/07
 * @since 5.0
 */
public class TreeMappingClassColumnLocator implements PluginConstants  {

    // ---------------------------------------------------------------------------------------------------------------------------
    // CONSTANTS
    // ---------------------------------------------------------------------------------------------------------------------------
    /** Properties file key prefix. Used for logging and localization. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(TreeMappingAdapter.class);
    
    //private PerformanceTracker pTracker = new PerformanceTracker(getClass().getName());
    
    // ---------------------------------------------------------------------------------------------------------------------------
    // FIELDS
    // ---------------------------------------------------------------------------------------------------------------------------
    
    /** */
    private TreeMappingClassLocator mappingClassLocator;
    
    /** Map of key=tree node, value=Mapping Class */
    private HashMap treeNodesToMappingClassColumnsMap = new HashMap();
    
    /**
     * @since 5.0
     */
    public TreeMappingClassColumnLocator(TreeMappingClassLocator mappingClassLocator) {
        super();
        this.mappingClassLocator = mappingClassLocator;
    }

//    private void startTracking(String method) {
//        pTracker.start(method);
//    }
//
//    private void stopTracking(String method) {
//        pTracker.stop(method);
//    }
//
//    public void print() {
//        pTracker.print();
//    }
//
//    public void resetTracker() {
//        pTracker.reset();
//    }

    /**
     * Maps the specified <code>MappingClassColumn</code> to the specified tree node (<code>EObject</code>).
     * @param theMappingColumn the <code>Mapping</code> input
     * @param theTreeNode the <code>Mapping</code> output
     * @throws IllegalArgumentException if either input parameter is <code>null</code>
     */
    public void addMappingClassColumnLocation(MappingClassColumn theMappingColumn, EObject theTreeNode) {
        //startTracking("addMappingClassColumnLocation()"); //$NON-NLS-1$
        ArgCheck.isNotNull(theMappingColumn);
        ArgCheck.isNotNull(theTreeNode);

        MappingList theColumnMappingList = getMappingClassColumnMappingList(theMappingColumn);
        
        if ( theColumnMappingList != null ) {
            if (!theColumnMappingList.contains(theTreeNode)) {
                try {
                    ModelerCore.getModelEditor().addValue(theColumnMappingList.mapping, theTreeNode, theColumnMappingList.list);
                    
                    // jh Lyra enh: Must update this map as well
                    treeNodesToMappingClassColumnsMap.put( theTreeNode, theMappingColumn );                   

                } catch (Exception e) {
                    PluginConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                }            
            } else {
                // adding mapping that already exists
                Util.log(IStatus.WARNING, Util.getString(PREFIX + "duplicateMapping", //$NON-NLS-1$
                                                         new Object[] {"addLocation", //$NON-NLS-1$
                                                                       "MappingClassColumn", //$NON-NLS-1$
                                                                       theMappingColumn,
                                                                       theTreeNode}));
            }
        } else {
            // should never happen
            Util.log(IStatus.ERROR, Util.getString(PREFIX + "mappingRootsProblem", //$NON-NLS-1$
                                                    new Object[] {"addLocation", //$NON-NLS-1$ 
                                                                  "MappingClass", //$NON-NLS-1$
                                                                  theMappingColumn,
                                                                  theTreeNode}));
        }
        //stopTracking("addMappingClassColumnLocation()"); //$NON-NLS-1$
    }
    
    /**
     * Removes the specified {@link org.eclipse.emf.mapping.Mapping}.
     * 
     * @param theMappingColumn
     *            the <code>Mapping</code> input
     * @param theTreeNode
     *            the <code>Mapping</code> output
     * @throws IllegalArgumentException
     *             if either input parameter is <code>null</code>
     */
    public void removeMappingClassColumnLocation(MappingClassColumn theMappingColumn,
                                                 EObject theTreeNode) {
        //startTracking("removeMappingClassColumnLocation()"); //$NON-NLS-1$
        ArgCheck.isNotNull(theMappingColumn);
        ArgCheck.isNotNull(theTreeNode);

        MappingList theColumnMappingList = getMappingClassColumnMappingList(theMappingColumn);
        if (theColumnMappingList != null) {
            try {
                ModelerCore.getModelEditor().removeValue(theColumnMappingList.mapping, theTreeNode, theColumnMappingList.list);

                // jh Lyra enh: Must update this map as well
                treeNodesToMappingClassColumnsMap.remove(theTreeNode);

            } catch (Exception e) {
                Util.log(IStatus.ERROR, e, Util.getString(PREFIX + "removeLocationTreeNodeNotFound", //$NON-NLS-1$
                                                          new Object[] {
                                                              theTreeNode, "MappingClassColumn", //$NON-NLS-1$
                                                              theMappingColumn
                                                          }));
            }
        } else {
            Util.log(IStatus.ERROR, Util.getString(PREFIX + "removeLocationTreeNodeNotFound", //$NON-NLS-1$
                                                   new Object[] {
                                                       theTreeNode, "MappingClassColumn", //$NON-NLS-1$
                                                       theMappingColumn
                                                   }));
        }
        //stopTracking("removeMappingClassColumnLocation()"); //$NON-NLS-1$
    }
    
    public HashMap getTreeNodesToMappingClassColumnsMap()  {
        //startTracking("getTreeNodesToMappingClassColumnsMap()"); //$NON-NLS-1$
        if( treeNodesToMappingClassColumnsMap == null ) {
            loadTreeNodesToMappingClassColumnsMap();
        }
        //stopTracking("getTreeNodesToMappingClassColumnsMap()"); //$NON-NLS-1$
        return treeNodesToMappingClassColumnsMap;
    }
    
    /**
     * Obtains the <code>MappingClassColumn</code> where the specified tree node is mapped.
     * @param theTreeNode the tree node whose <code>MappingClassColumn</code> is being requested
     * @return the <code>MappingClassColumn</code> or <code>null</code> if not mapped
     */
    public void loadTreeNodesToMappingClassColumnsMap() {
        //startTracking("loadTreeNodesToMappingClassColumnsMap()"); //$NON-NLS-1$
        /*
         * This is a linear search, and the MCs are not necessarily in any 
         *    optimal order.  Let's replace this with a HashMap.
         *     
         *    Note: There can be many treeNodes for one Mapping Class Column,
         *          So the TreeNode must be the key to this hashmap, which is 
         *          what we intended.
         */
        
            
        if ( treeNodesToMappingClassColumnsMap != null ) {
            treeNodesToMappingClassColumnsMap 
                 = new HashMap( treeNodesToMappingClassColumnsMap.keySet().size() );
        } else {
            treeNodesToMappingClassColumnsMap = new HashMap();                
        }
        List mappingClasses = mappingClassLocator.getMappingClasses();
        // for each mapping class, get mapping class columns, see if specified node is mapped
        for (int size = mappingClasses.size(), i = 0; i < size; i++) {
            MappingClass mappingClass = (MappingClass)mappingClasses.get(i);
            
            // null entries are contained in the list so check first
            if (mappingClass != null) {
                List columns = mappingClass.getColumns();
                
                if ( ( columns != null ) && !columns.isEmpty() ) {
                    for ( int numColumns = columns.size(), j = 0; j < numColumns; j++ ) {
                        MappingClassColumn col = (MappingClassColumn)columns.get( j );
                        List treeNodes = getMappingClassColumnOutputLocations( col );
                        Iterator itNodes = treeNodes.iterator();
                        
                        while( itNodes.hasNext() ) {
                            EObject oTreeNodeTemp = (EObject)itNodes.next();
                            /*
                             * jh Defect 21277: Shouldn't we also do this when we add a new MC?
                             * ( Also see: getTreeNodesToMappingClassScopeMap
                             */
                            treeNodesToMappingClassColumnsMap.put( oTreeNodeTemp, col );                   
                        }
                    }
                }
            }
        }
        //stopTracking("loadTreeNodesToMappingClassColumnsMap()"); //$NON-NLS-1$
    }
    
    /**
     * Obtains the tree nodes that are mapped to the specified <code>MappingClassColumn</code>.
     * @param theMappingColumn the <code>MappingClassColumn</code> whose mapped tree nodes are being requested
     * @return  the mapped tree nodes or an empty list
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public MappingList getMappingClassColumnMappingList(MappingClassColumn theMappingColumn) {
        //startTracking("getMappingClassColumnMappingList()"); //$NON-NLS-1$
        ArgCheck.isNotNull(theMappingColumn);

        MappingList result = null;
        MappingClass mappingClass = theMappingColumn.getMappingClass();

        MappingRoot mappingRoot = (MappingRoot)mappingClassLocator.getMappingRoot(mappingClass);
        if (mappingRoot != null) {

            List columnMappings = mappingRoot.getNested();
            
            if (!columnMappings.isEmpty()) {
                for (int size = columnMappings.size(), i = 0; i < size; i++) {
                    Mapping mapping = (Mapping)columnMappings.get(i);

                    // jh PERFORMANCE: change columns from a List to a Map,                    
                    List columns = mapping.getInputs();
                    /**
                     * If column size is only ONE, then let's NOT create a MAP
                     */
                    if( columns.size() == 1 ) {
                        Object column_1 = columns.get(0);
                        if( column_1 != null && column_1 == theMappingColumn) {
                            result = new MappingList(mapping, mapping.getOutputs());
                            break;
                        }
                    } else {
                        HashMap columnsMap = ListAndMapUtil.createMapFromList( columns );
                        
                        if (columnsMap.get(theMappingColumn) != null) {
                            result = new MappingList(mapping, mapping.getOutputs());
                            break;
                        }
                    }
                }
            }
            
            // if mapping not found create it
            if (result == null) {
                Mapping newMapping = createColumnMapping(mappingRoot, theMappingColumn);
                result = new MappingList(newMapping, newMapping.getOutputs());
            }
        }
        //stopTracking("getMappingClassColumnMappingList()"); //$NON-NLS-1$
        return result;
    }
    
    /**
     *  
     * @param theTreeNode
     * @param theMappingClass
     * @return
     * @since 5.0
     */
    public MappingClassColumn getMappingClassColumn(EObject theTreeNode, MappingClass theMappingClass) {
        //startTracking("getMappingClassColumn()"); //$NON-NLS-1$
        ArgCheck.isNotNull(theTreeNode);
        
        MappingClassColumn result = null;

        for ( Iterator iter = theMappingClass.getColumns().iterator() ; iter.hasNext() ; ) {
            MappingClassColumn column = (MappingClassColumn) iter.next();

            // jh PERFORMANCE: change currentLocations from a List to a Map,
            MappingList theColumnMappingList = getMappingClassColumnMappingList(column);
            if ( theColumnMappingList != null ) {
                /**
                 * If List size is only ONE, then let's NOT create a MAP
                 */
                if( theColumnMappingList.list.size() == 1 ) {
                    Object mappedNode = theColumnMappingList.list.get(0);
                    if( mappedNode != null && mappedNode == theTreeNode ) {
                        result = column;
                        break;
                    }
                } else if( theColumnMappingList.contains(theTreeNode) ) {
                    result = column;
                    break;
                }
            }
        }
        //stopTracking("getMappingClassColumn()"); //$NON-NLS-1$
        return result;
    }
    
    public MappingClassColumn getMappingClassColumn(EObject theTreeNode) {
        //startTracking("getMappingClassColumn(TREE NODE)"); //$NON-NLS-1$
        ArgCheck.isNotNull(theTreeNode);
        
        MappingClassColumn result = (MappingClassColumn)treeNodesToMappingClassColumnsMap.get( theTreeNode );
        
        //stopTracking("getMappingClassColumn(TREE NODE)"); //$NON-NLS-1$
        return result;
    }
    
    /**
     * Obtains the tree nodes that are mapped to the specified <code>MappingClassColumn</code>.
     * @param theMappingColumn the <code>MappingClassColumn</code> whose mapped tree nodes are being requested
     * @return an unmodifiable list of mapped tree nodes or an empty list
     * @throws IllegalArgumentException if input parameter is <code>null</code>
     */
    public List getMappingClassColumnOutputLocations(MappingClassColumn theMappingColumn) {
        //startTracking("getMappingClassColumnOutputLocations()"); //$NON-NLS-1$
        MappingList theColumnMappingList = getMappingClassColumnMappingList(theMappingColumn);
        List result = null;
        if( theColumnMappingList == null) {
            result = Collections.EMPTY_LIST;
        } else {
            result = Collections.unmodifiableList(theColumnMappingList.list);
        }
        //stopTracking("getMappingClassColumnOutputLocations()"); //$NON-NLS-1$
        return result;
    }
    
    
    // ---------------------------------------------------------------------------------------------------------------------------
    // PRIVATE METHODS
    // ---------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Creates a <code>Mapping</code> using the specified <code>MappingClassColumn</code> as it's input.
     * @param theMappingRoot the <code>MappingRoot</code> where the nested <code>Mapping</code> is being created
     * @param theMappingColumn the <code>MappingClassColumn</code> used as an input to the new <code>Mapping</code>
     * @return the new <code>Mapping</code>
     */
    private Mapping createColumnMapping(MappingRoot theMappingRoot, MappingClassColumn theMappingColumn) {
        //startTracking("createColumnMapping()"); //$NON-NLS-1$
        ArgCheck.isNotNull(theMappingRoot);
        ArgCheck.isNotNull(theMappingColumn);

        Mapping mapping = theMappingRoot.createMapping(Collections.singletonList(theMappingColumn), Collections.EMPTY_LIST);
        theMappingRoot.getNested().add(mapping);
        //stopTracking("createColumnMapping()"); //$NON-NLS-1$
        return mapping;
    }
    
}
