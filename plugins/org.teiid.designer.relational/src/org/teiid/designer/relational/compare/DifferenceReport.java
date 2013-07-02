/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.compare;

import java.util.List;

import org.teiid.designer.relational.model.RelationalReference;

/**
 * DifferenceReport keeps track of the differences between two RelationalModels
 */
public class DifferenceReport {
	
	private OperationList objectsToCreate;
	private OperationList objectsToDelete;
	private OperationList objectsToUpdate;
	
	/**
	 * Set the RelationalReference objects to Create
	 * @param objsToCreate the list of objects for create
	 */
	public void setObjectsToCreate(List<RelationalReference> objsToCreate) {
		this.objectsToCreate = new OperationList(objsToCreate,OperationList.OperationType.CREATE);
	}
	
	/**
	 * Set the RelationalReference objects to Delete
	 * @param objsToDelete the list of objects for delete
	 */
	public void setObjectsToDelete(List<RelationalReference> objsToDelete) {
		this.objectsToDelete = new OperationList(objsToDelete,OperationList.OperationType.DELETE);
	}
	
	/**
	 * Set the RelationalReference objects to Update
	 * @param objsToUpdate the list of objects for update
	 */
	public void setObjectsToUpdate(List<RelationalReference> objsToUpdate) {
		this.objectsToUpdate = new OperationList(objsToUpdate,OperationList.OperationType.UPDATE);
	}
	
	/**
	 * Get the RelationalReference objects for Create
	 * @return the list of objects for create
	 */
	public OperationList getObjectsToCreate() {
		return objectsToCreate;
	}
	
	/**
	 * Get the RelationalReference objects for Delete
	 * @return the list of objects for delete
	 */
	public OperationList getObjectsToDelete() {
		return objectsToDelete;
	}
	
	/**
	 * Get the RelationalReference objects for Update
	 * @return the list of objects for update
	 */
	public OperationList getObjectsToUpdate() {
		return objectsToUpdate;
	}
	
	/**
	 * Determine if the DifferenceReport has any operations
	 * @return 'true' if any operations to process, 'false' if not
	 */
	public boolean hasOperations() {
		if( !this.objectsToCreate.getList().isEmpty() || 
			!this.objectsToDelete.getList().isEmpty() ||
			!this.objectsToUpdate.getList().isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determine if the Difference report has any selected operations
	 * @return 'true' if any operations are selected, 'false' if not
	 */
	public boolean hasSelectedOperations() {
		boolean hasSelectedOp = false;
		List<RelationalReference> createList = this.objectsToCreate.getList();
		for(RelationalReference createRef: createList) {
			if(createRef.isChecked()) {
				hasSelectedOp = true;
				break;
			}
		}
		if(!hasSelectedOp) {
			List<RelationalReference> deleteList = this.objectsToDelete.getList();
			for(RelationalReference deleteRef: deleteList) {
				if(deleteRef.isChecked()) {
					hasSelectedOp = true;
					break;
				}
			}
		}
		if(!hasSelectedOp) {
			List<RelationalReference> updateList = this.objectsToUpdate.getList();
			for(RelationalReference updateRef: updateList) {
				if(updateRef.isChecked()) {
					hasSelectedOp = true;
					break;
				}
			}
		}
		return hasSelectedOp;
	}

}
