/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teiid.designer.transformation.reverseeng.util.StringBuilderUtil;


/**
 * @author vanhalbert
 *
 */
public class Table {
    private String name;
    private String className;
    
    private String remarks;
    
    private Map<String, Column> columns = new HashMap<String, Column>();


    public Table(String name) {
    	this.name = name;
    	this.className =  org.teiid.designer.transformation.reverseeng.util.Util.columnNameToMemberName(this.name);
    }
	/**
	 * @return String
	 * @see org.teiid.designer.transformation.reverseeng.api.Table#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return String
	 * @see org.teiid.designer.transformation.reverseeng.api.Table#getRemarks()
	 */
	public String getRemarks() {
		return remarks;
	}
	
	public String getClassName() {
		return this.className;
	}

	public Column getColumn(String name) {
		return columns.get(name);
	}
	/**
	 * @param remarks Sets remarks to the specified value.
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public List<Column> getColumns() {
		List<Column> cols = new ArrayList<Column>(columns.size());
		cols.addAll(columns.values());
		return cols;
	}
	
	
	public void addColumn(Column column) {
		columns.put(column.getName(), column);
	}
	
	/**
	 * Call to determine that at least one column is required.
	 * @return boolean true if at least one column is required
	 */
	public boolean hasRequiredColumn() {
		for (Column  c : columns.values()) {
			if (c.isRequired()) return true;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Table t = (Table) obj;
		if (this.getName().equals(t.getName()) ) {
			List<Column> columns = this.getColumns();
			if (columns.size() != t.getColumns().size()) {
				return false;
			}
			for (Column c : columns) {
				Column tc = t.getColumn(c.getName());
				if (tc == null) return false;
				
				if (!c.equals(tc)) {
					return false;
				}
			}
			
		} else {
			return false;
		}
		
		return true;
	}	
	
    @Override
	public String toString() {
    	StringBuilderUtil sbu = new StringBuilderUtil(this).append("name", getName());
    	
    	return sbu.toString();
    }
    
	
	public Column createColumn(String name) {
		Column col = new Column(this, name);
		this.addColumn(col);
		return col;
	}


}