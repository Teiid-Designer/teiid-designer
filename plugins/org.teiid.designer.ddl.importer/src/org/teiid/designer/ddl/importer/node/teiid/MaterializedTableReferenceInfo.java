/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer.node.teiid;

/**
 * A basic reference object to store and provide information pertaining to materialized table references.
 * 
 * This is needed by the Dynamic VDB class to allow view model DDL importing to store external table reference information and 
 * provide the means to discover the actual EMF table reference and set it on the actual EMF view/table
 * 
 * @author blafond
 *
 */
public class MaterializedTableReferenceInfo {
	private String viewModelName;
	private String sourceModelName;
	private String targetViewName;
	private String materializedTableName;

	/**
	 * @param viewModelName
	 * @param sourceModelName
	 * @param targetViewName
	 * @param materializedTableName
	 */
	public MaterializedTableReferenceInfo(
			String viewModelName,
			String sourceModelName,
			String targetViewName,
			String materializedTableName) {
		super();
		this.viewModelName = viewModelName;
		this.sourceModelName = sourceModelName;
		this.targetViewName = targetViewName;
		this.materializedTableName = materializedTableName;
	}

	/**
	 * @return the view model name containing the target view/table
	 */
	public String getViewModelName() {
		return viewModelName;
	}

	/**
	 * @return the source model name containing the materialized table reference
	 */
	public String getSourceModelName() {
		return sourceModelName;
	}

	/**
	 * @return the target view name where the table reference is set
	 */
	public String getTargetViewName() {
		return targetViewName;
	}

	/**
	 * @return the materialized table name in the source model
	 */
	public String getMaterializedTableName() {
		return materializedTableName;
	}
	
	

}
