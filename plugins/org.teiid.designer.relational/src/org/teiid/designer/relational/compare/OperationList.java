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
 * List of Operations used in the DifferenceReport
 * 1) Create, 2)Delete, 3)Update
 */
public class OperationList {
	
	@SuppressWarnings("javadoc")
	public enum OperationType {
		CREATE,
		DELETE,
		UPDATE
	}
	private List<RelationalReference> refList;
	private OperationType operationType;
	
	/**
	 * OperationList constructor
	 * @param references the list of RelationalReference objects for this operation
	 * @param operationType the type of operation
	 */
	public OperationList(List<RelationalReference> references, OperationType operationType) {
		this.refList = references;
		this.operationType = operationType;
	}
	
	/**
	 * Get the list of RelationalReferences
	 * @return the list of RelationalReferences
	 */
	public List<RelationalReference> getList() {
		return this.refList;
	}
	
	/**
	 * Get the type of operation for this list
	 * @return the type of operation
	 */
	public OperationType getOperationType() {
		return this.operationType;
	}

}
