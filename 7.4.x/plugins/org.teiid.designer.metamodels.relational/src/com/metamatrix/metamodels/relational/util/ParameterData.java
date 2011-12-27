/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

/**
 * Class to hold basic Pushdown-function parameter data structure and method access to key values
 */
public class ParameterData {
	private String name;
	private String type;
	private int length;
	private boolean include;
	
	public ParameterData() {
		super();
	}
	
	public ParameterData(String name, String type, int length) {
		super();
		this.name = name;
		this.type = type;
		if( this.type == null ) {
			this.type = PushdownFunctionData.DEFAULT_TYPE;
		}
		this.length = length;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public void include() {
		this.include = true;
	}
	
	public void exclude() {
		this.include = false;
	}
	
	public boolean isIncluded() {
		return this.include;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ParameterData:") //$NON-NLS-1$
		.append("\n  NAME = ").append(getName()) //$NON-NLS-1$
		.append("\n  TYPE = ").append(getType()) //$NON-NLS-1$
		.append("\n  LENGTH = ").append(Integer.toString(getLength())) //$NON-NLS-1$
		.append("\n  INCLUDED = ").append(isIncluded()); //$NON-NLS-1$
		
		return sb.toString();
	}
}
