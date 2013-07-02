/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.relational.RelationalConstants.TYPES;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalReference;

/**
 * DifferenceGenerator - generates a difference report, with the differences between two RelationalModels
 */
public class DifferenceGenerator {
	
	/**
	 * Compare two Relational Models
	 * @param targetModel the 'target' Model with the desired end state.
	 * @param originalModel the 'original' Model
	 * @return the difference report
	 */
	public static DifferenceReport compare(RelationalModel targetModel, RelationalModel originalModel) {
		
		DifferenceReport diffReport = new DifferenceReport();
		
		// Get all Primary Objects for the final desired state
		Collection<RelationalReference> targetChildren = filterForPrimaryRefs(targetModel.getAllReferences());
		
		// Get all Primary Objects for the existing state
		Collection<RelationalReference> originalChildren = filterForPrimaryRefs(originalModel.getAllReferences());		
		
		// The targetChildren will either be a create or a replace
		List<RelationalReference> objsToCreate = new ArrayList<RelationalReference>();
		List<RelationalReference> objsToUpdate = new ArrayList<RelationalReference>();
		List<RelationalReference> objsExactMatch = new ArrayList<RelationalReference>();
		
		Iterator<RelationalReference> iter = targetChildren.iterator();
		while(iter.hasNext()) {
			RelationalReference targetObj = iter.next();
			RelationalReference nameTypeParentMatch = getNameTypeParentMatch(originalChildren,targetObj);
			// The Existing Collection has an object with matching name and type
			if(nameTypeParentMatch!=null) {
				// If not an exact match, put in replace list
				if(!nameTypeParentMatch.equals(targetObj)) {
					objsToUpdate.add(targetObj);
				// Exact match, do nothing with it
				} else {
					objsExactMatch.add(targetObj);
				}
			// No existing children with matching name/type - create it.
			} else {
				objsToCreate.add(targetObj);
			}
		}
		
		// Now determine which of the original objects need to be deleted
		List<RelationalReference> objsToDelete = new ArrayList<RelationalReference>();
		iter = originalChildren.iterator();
		while(iter.hasNext()) {
			RelationalReference origObj = iter.next();
			RelationalReference matchObj = getNameTypeParentMatch(targetChildren,origObj);
			// If no name/type match in the target list, then its a delete
			if(matchObj==null) {
				objsToDelete.add(origObj);
			}
		}

		// Set lists on difference report
		diffReport.setObjectsToCreate(objsToCreate);
		diffReport.setObjectsToDelete(objsToDelete);
		diffReport.setObjectsToUpdate(objsToUpdate);
		
		return diffReport;
	}
	
	/**
	 * Find a RelationalReference in the supplied list which matches another RelationalReference
	 * @param refs the list of RelationalReference objects
	 * @param ref a RelationalReference
	 * @return the matching RelationalReference or null if none found
	 */
	private static RelationalReference getNameTypeParentMatch(Collection<RelationalReference> refs, RelationalReference ref) {
		RelationalReference result = null;
		for(RelationalReference listRef : refs) {
			if(nameTypeParentMatch(listRef,ref)) {
				result = listRef;
				break;
			}
		}
		return result;
	}
	
	/*
	 * Determine if the name, type and parent of the supplied objects match
	 */
	private static boolean nameTypeParentMatch(RelationalReference ref1, RelationalReference ref2) {
		if(ref1==null || ref2==null) {
			return false;
		}
		
        // string properties
        if (!CoreStringUtil.valuesAreEqual(ref1.getName(), ref2.getName()) ) {
            return false;
        }
        
        if(ref1.getType() != ref2.getType()) {
        	return false;
        }

        RelationalReference ref1Parent = ref1.getParent();
        RelationalReference ref2Parent = ref2.getParent();
        if(ref1Parent==null && ref2Parent==null) {
        	return true;
        }
        
        if(ref1Parent==null || ref2Parent==null) {
        	return false;
        }
                
        // Parent types
        if(ref1Parent.getType() != ref1Parent.getType()) {
        	return false;
        }
        
        // Consider model parents equal
        if(ref1Parent.getType()==TYPES.MODEL) {
        	return true;
        // Non-model, names must match
        } else if (!CoreStringUtil.valuesAreEqual(ref1Parent.getName(), ref1Parent.getName()) ) {
            return false;
        }
        
        return true;
	}
	
	/*
	 * Make a list of only the 'primary' objects to create
	 */
	private static Collection<RelationalReference> filterForPrimaryRefs(Collection<RelationalReference> allRefs) {
		List<RelationalReference> filteredList = new ArrayList<RelationalReference>();
		for(RelationalReference rRef : allRefs) {
			if(rRef!=null) {
				int refType = rRef.getType();
				if(refType==TYPES.TABLE || refType==TYPES.PROCEDURE || refType==TYPES.VIEW || refType==TYPES.INDEX ) {
					filteredList.add(rRef);
				}
			}
		}
		return filteredList;
	}
	
	
}
