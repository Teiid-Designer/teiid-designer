/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.query.QueryMetadataException;
import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.core.metadata.runtime.ColumnRecord;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.metadata.StoredProcedureInfo;
import com.metamatrix.query.sql.ProcedureReservedWords;
import com.metamatrix.query.sql.lang.SPParameter;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.GroupSymbol;

/**
 * Utilities used to get external metadata.
 */
public class ExternalMetadataUtil {

    // Can't construct
    private ExternalMetadataUtil() {}

    public static List resolveElementsInGroup(GroupSymbol group, QueryMetadataInterface metadata)
    throws QueryMetadataException, MetaMatrixComponentException {

		String groupName = group.getName();
		
		boolean isUUID = UuidUtil.isStringifiedUUID(groupName);

        // get all elements from the metadata
        List elementIDs = metadata.getElementIDsInGroupID(group.getMetadataID());

		if(elementIDs != null) {
	        // ok for each ELEMENT...
	        List elements = new ArrayList(elementIDs.size());
	        Iterator elementIter = elementIDs.iterator();
	        while(elementIter.hasNext()){
	            Object elementID = elementIter.next();
	            Assertion.isInstanceOf(elementID, ColumnRecord.class, null);
				ColumnRecord columnRecord = (ColumnRecord) elementID;
				String fullName = null;
				if(isUUID) {
					fullName = columnRecord.getUUID();
				} else {
					fullName = columnRecord.getFullName();				
				}
	
	            // Form an element symbol from the ID
	            ElementSymbol element = new ElementSymbol(fullName);
	            element.setGroupSymbol(group);
	            element.setMetadataID(elementID);
	            element.setType( DataTypeManager.getDataTypeClass(metadata.getElementType(element.getMetadataID())) );
	
	            elements.add(element);
	        }

	        return elements;
		}

        return Collections.EMPTY_LIST;
    }

	public static Map getProcedureExternalMetadata(GroupSymbol virtualGroup, QueryMetadataInterface metadata)
    throws QueryMetadataException, MetaMatrixComponentException {
		Map externalMetadata = new HashMap();

        // Look up elements for the virtual group
        List elements = ExternalMetadataUtil.resolveElementsInGroup(virtualGroup, metadata);
		// virtual group metadata info
        externalMetadata.put(virtualGroup, elements);

		// INPUT group metadata info
		GroupSymbol inputGroup = new GroupSymbol(ProcedureReservedWords.INPUT);
        List inputElments = new ArrayList(elements.size());
        for(int i=0; i<elements.size(); i++) {
        	ElementSymbol virtualElmnt = (ElementSymbol)elements.get(i);
        	ElementSymbol inputElement = (ElementSymbol)virtualElmnt.clone();
            inputElments.add(inputElement);
        }

        externalMetadata.put(inputGroup, inputElments);

		// CHANGING group metadata info
        // Switch type to be boolean for all CHANGING variables
		GroupSymbol changeGroup = new GroupSymbol(ProcedureReservedWords.CHANGING);
        List changingElments = new ArrayList(elements.size());
        for(int i=0; i<elements.size(); i++) {
        	ElementSymbol changeElement = (ElementSymbol)((ElementSymbol)elements.get(i)).clone();
            changeElement.setType(DataTypeManager.DefaultDataClasses.BOOLEAN);
            changingElments.add(changeElement);
        }

        externalMetadata.put(changeGroup, changingElments);

        return externalMetadata;
	}

    public static Map getStoredProcedureExternalMetadata(GroupSymbol virtualProc, QueryMetadataInterface metadata)
    throws QueryMetadataException, MetaMatrixComponentException {

        Map externalMetadata = new HashMap();

        StoredProcedureInfo info = metadata.getStoredProcedureInfoForProcedure(virtualProc.getName());
        if(info!=null) {
            virtualProc.setMetadataID(info.getProcedureID());

            // List of ElementSymbols - Map Values
            List paramList = info.getParameters();
            Iterator iter = paramList.iterator();
            // Create Symbol List from parameter list
            List symbolList = new ArrayList();
            while(iter.hasNext()) {
                SPParameter param = (SPParameter) iter.next();
                if(param.getParameterType() == SPParameter.IN ||
                    param.getParameterType() == SPParameter.INOUT) {
                    // Create Element Symbol
                    ElementSymbol eSymbol = new ElementSymbol(param.getName());
                    eSymbol.setMetadataID(param.getMetadataID());
                    eSymbol.setType(param.getClassType());
                    eSymbol.setGroupSymbol(virtualProc);
                    symbolList.add(eSymbol);
                }
            }
            // Create external Metadata Map
            externalMetadata = new HashMap();
            externalMetadata.put(virtualProc, symbolList);
        }

        return externalMetadata;
    }
    
    /**
     * Create external metadata objects for virtual procedure input params added to mapping class
     * when a proc is the source for a mapping class 
     * @param virtualProc - GroupSymbol for the Procedure
     * @param mappingClass - GroupSymbol for the MappingClass
     * @param metadata - QMI to use for metadata retrieval
     * @return map of new metadata
     * @throws QueryMetadataException
     * @throws MetaMatrixComponentException
     */
    public static Map getStoredProcedureExternalMetadataForMappingClass(GroupSymbol virtualProc, GroupSymbol mappingClass, QueryMetadataInterface metadata)
    throws QueryMetadataException, MetaMatrixComponentException {

        Map externalMetadata = new HashMap();

        StoredProcedureInfo info = metadata.getStoredProcedureInfoForProcedure(virtualProc.getName());
        if(info!=null) {
            virtualProc.setMetadataID(info.getProcedureID());

            // List of ElementSymbols - Map Values
            List paramList = info.getParameters();
            Iterator iter = paramList.iterator();
            // Create Symbol List from parameter list
            List symbolList = new ArrayList();
            while(iter.hasNext()) {
                SPParameter param = (SPParameter) iter.next();
                if(param.getParameterType() == SPParameter.IN ||
                    param.getParameterType() == SPParameter.INOUT) {
                    // Create Element Symbol
                    ElementSymbol eSymbol = new ElementSymbol(param.getName());
                    eSymbol.setMetadataID(param);
                    eSymbol.setType(param.getClassType());
                    eSymbol.setGroupSymbol(mappingClass);
                    symbolList.add(eSymbol);
                }
            }
            // Create external Metadata Map
            externalMetadata = new HashMap();
            externalMetadata.put(mappingClass, symbolList);
        }

        return externalMetadata;
    }    
}
