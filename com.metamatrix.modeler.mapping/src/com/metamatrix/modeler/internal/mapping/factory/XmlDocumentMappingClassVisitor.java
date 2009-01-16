/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.mapping.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;


/** 
 * Specialized visitor which collects and creates ordered lists of mapping classes and staging tables given a set of 
 * maps defining the location object -to- mapping class maps
 * @since 5.0
 */
public class XmlDocumentMappingClassVisitor {
    private ArrayList mappingClasses;
    private ArrayList stagingTables;
    private HashMap locationToMappingClassMap;
    private HashMap locationToStagingTableMap;
    private HashMap remainingMCLocations;
    private HashMap remainingSTLocations;
    /** 
     * 
     * @since 5.0
     */
    public XmlDocumentMappingClassVisitor(HashMap locToMCMap, HashMap locToSTMap) {
        super();
        locationToMappingClassMap = locToMCMap;
        locationToStagingTableMap = locToSTMap;
        remainingMCLocations = simpleSetToMap(locToMCMap.keySet());
        remainingSTLocations = simpleSetToMap(locToSTMap.keySet());
        mappingClasses = new ArrayList();
        stagingTables = new ArrayList();
    }

    /**
     * checks input EObject, determines if it is a location that exists in the mapping class locations list or the staging tables
     * list. If so, it adds the mapping class/staging table to the ordered list, then remove all locations referencing the mapping
     * class/ST from the remaining location lists.
     * @param object
     * @since 5.0
     */
    public void visit(EObject object) {
        if( remainingMCLocations.containsKey(object)) {
            EObject mappingClass = (EObject)locationToMappingClassMap.get(object);
            if( mappingClass != null ) {
                mappingClasses.add(mappingClass);
                removeLocationsForMappingClass(mappingClass);
            }
        } 
        if( remainingSTLocations.containsKey(object)) {
            EObject stagingTable = (EObject)locationToStagingTableMap.get(object);
            if( stagingTable != null ) {
                stagingTables.add(stagingTable);
                removeLocationsForStagingTable(stagingTable);
            }
        }
    }
    
    /**
     * Get ordered list of mapping classes
     * @return
     * @since 5.0
     */
     
    public ArrayList getOrderedMappingClasses() {
        return mappingClasses;
    }
    
    /**
     * Get ordered list of staging tables
     * @return
     * @since 5.0
     */
    public ArrayList getOrderedStagingTables() {
        return stagingTables;
    }
    
    // ---------
    // PRIVATE
    // ---------
    
    private void removeLocationsForMappingClass(EObject mappingClass) {
        List removeList = new ArrayList();
        Iterator iter = locationToMappingClassMap.keySet().iterator();
        // Gather up all locations for this mapping class
        while( iter.hasNext() ) {
            EObject nextLoc = (EObject)iter.next();
            EObject nextMC = (EObject)locationToMappingClassMap.get(nextLoc);
            if( nextMC == mappingClass ) {
                removeList.add(nextLoc);
            }
        }
        
        // Now remove them from the remaining locations so we don't process any other locations that this
        // mapping  class is mapped to.
        if( ! removeList.isEmpty() ) {
            for( Iterator removeIter = removeList.iterator(); removeIter.hasNext(); ) {
                remainingMCLocations.remove(removeIter.next());
            }
        }
    }
    
    private void removeLocationsForStagingTable(EObject stagingTable) {
        List removeList = new ArrayList();
        Iterator iter = locationToStagingTableMap.keySet().iterator();
        // Gather up all locations for this staging table
        while( iter.hasNext() ) {
            EObject nextLoc = (EObject)iter.next();
            EObject nextMC = (EObject)locationToStagingTableMap.get(nextLoc);
            if( nextMC == stagingTable ) {
                removeList.add(nextLoc);
            }
        }
        
        // Now remove them from the remaining locations so we don't process any other locations that this
        // mapping  class is mapped to.
        if( ! removeList.isEmpty() ) {
            for( Iterator removeIter = removeList.iterator(); removeIter.hasNext(); ) {
                remainingSTLocations.remove(removeIter.next());
            }
        }
    }
    
    private HashMap simpleSetToMap( Set theSet ) {
        HashMap hmap = new HashMap( theSet.size() );
        Iterator iter = theSet.iterator();      
        while( iter.hasNext() ) {
            Object oTemp = iter.next();
            hmap.put( oTemp, oTemp );
        }
        return hmap;
    }

}
