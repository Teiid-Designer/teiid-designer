/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.lang;

import java.util.ArrayList;
import java.util.List;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.lang.ICreate;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.BaseColumn.NullType;
import org.teiid.metadata.Column;
import org.teiid.metadata.Table;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;


/** 
 * @since 5.5
 */
public class Create extends Command
    implements TargetedCommand, ICreate<Expression, LanguageVisitor> {

    @Since(Version.TEIID_8_10)
    public enum CommitAction {
        PRESERVE_ROWS,
    }

    /**
     * @param p
     * @param id
     */
    public Create(TeiidParser p, int id) {
        super(p, id);
    }

    /** Identifies the table to be created. */
    private GroupSymbol table;
    private List<ElementSymbol> primaryKey = new ArrayList<ElementSymbol>();
    private List<Column> columns = new ArrayList<Column>();
    private List<ElementSymbol> columnSymbols;
    private Table tableMetadata;
    private String on;

    @Since(Version.TEIID_8_10)
    private CommitAction commitAction;

    public GroupSymbol getTable() {
        return table;
    }

    public GroupSymbol getGroup() {
    	return table;
    }

    public void setTable(GroupSymbol table) {
        this.table = table;
    }
    
    public List<Column> getColumns() {
        return columns;
    }
    
    public List<ElementSymbol> getPrimaryKey() {
		return primaryKey;
	}
    
    /**
     * Derived ElementSymbol list.  Do not modify without also modifying the columns.
     * @return
     */
    public List<ElementSymbol> getColumnSymbols() {
    	if (columnSymbols == null) {
    		columnSymbols = new ArrayList<ElementSymbol>(columns.size());
    		for (Column column : columns) {
				ElementSymbol es = getTeiidParser().createASTNode(ASTNodes.ELEMENT_SYMBOL); 
				es.setName(column.getName());
				es.setType(getTeiidParser().getDataTypeService().getDataTypeClass(column.getRuntimeType()));
				es.setGroupSymbol(table);
				columnSymbols.add(es);
			}
    	}
		return columnSymbols;
	}
    
    /** 
     * @see org.teiid.query.sql.lang.Command#getType()
     * @since 5.5
     */
    public int getType() {
        return Command.TYPE_CREATE;
    }

    /** 
     * @see org.teiid.query.sql.lang.Command#getProjectedSymbols()
     * @since 5.5
     */
    public List getProjectedSymbols() {
        return getUpdateCommandSymbol();
    }

    /** 
     * @see org.teiid.query.sql.lang.Command#areResultsCachable()
     * @since 5.5
     */
    public boolean areResultsCachable() {
        return false;
    }

    public void setElementSymbolsAsColumns(List<ElementSymbol> columns) {
    	this.columns.clear();
    	for (ElementSymbol elementSymbol : columns) {
    		Column c = new Column(getTeiidVersion());

    		if (isTeiidVersionOrGreater(Version.TEIID_8_5))
    		    c.setName(elementSymbol.getShortName());
    		else
    		    c.setName(elementSymbol.getName());

    		c.setRuntimeType(getTeiidParser().getDataTypeService().getDataTypeName(elementSymbol.getType()));
    		c.setNullType(NullType.Nullable);
    		this.columns.add(c);
		}
    }

    public String getOn() {
		return on;
	}
    
    public void setOn(String on) {
		this.on = on;
	}
    
    public Table getTableMetadata() {
		return tableMetadata;
	}
    
    public void setTableMetadata(Table tableMetadata) {
    	if (tableMetadata != null) {
    		this.columns = tableMetadata.getColumns();
    		this.table = getTeiidParser().createASTNode(ASTNodes.GROUP_SYMBOL); 
    		table.setName(tableMetadata.getName());
    	}
		this.tableMetadata = tableMetadata;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.columnSymbols == null) ? 0 : this.columnSymbols.hashCode());
        result = prime * result + ((this.columns == null) ? 0 : this.columns.hashCode());
        result = prime * result + ((this.on == null) ? 0 : this.on.hashCode());
        result = prime * result + ((this.primaryKey == null) ? 0 : this.primaryKey.hashCode());
        result = prime * result + ((this.table == null) ? 0 : this.table.hashCode());
        result = prime * result + ((this.tableMetadata == null) ? 0 : this.tableMetadata.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {      
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Create other = (Create)obj;
        if (this.columnSymbols == null) {
            if (other.columnSymbols != null)
                return false;
        } else if (!this.columnSymbols.equals(other.columnSymbols))
            return false;
        if (this.columns == null) {
            if (other.columns != null)
                return false;
        } else {
            if (other.columns.size() != this.columns.size()) {
                return false;
            }

            for (int i = 0; i < this.columns.size(); i++) {
                Column c = this.columns.get(i);
                Column o = other.columns.get(i);
                DataTypeManagerService dataTypeManager = getTeiidParser().getDataTypeService();
                if (!c.getName().equalsIgnoreCase(o.getName()) 
                    || dataTypeManager.getDataTypeClass(c.getRuntimeType().toLowerCase()) != dataTypeManager.getDataTypeClass(o.getRuntimeType().toLowerCase())
                    || c.isAutoIncremented() != o.isAutoIncremented()
                    || c.getNullType() != o.getNullType()) {
                    return false;
                }
            }
        }
        if (this.on == null) {
            if (other.on != null)
                return false;
        } else if (!this.on.equals(other.on))
            return false;
        if (this.primaryKey == null) {
            if (other.primaryKey != null)
                return false;
        } else if (!this.primaryKey.equals(other.primaryKey))
            return false;
        if (this.table == null) {
            if (other.table != null)
                return false;
        } else if (!this.table.equals(other.table))
            return false;
        if (this.tableMetadata == null) {
            if (other.tableMetadata != null)
                return false;
        } else if (!this.tableMetadata.equals(other.tableMetadata))
            return false;

        if (getTeiidVersion().isGreaterThanOrEqualTo(Version.TEIID_8_10.get())) {
            if (this.commitAction == null) {
                if (other.commitAction != null)
                    return false;
            } else if (!this.commitAction.equals(other.commitAction))
                return false;
        }

        return true;
    }

    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Create clone() {
        Create clone = new Create(this.parser, this.id);

        if(getTable() != null)
            clone.setTable(getTable().clone());

        clone.columns = new ArrayList<Column>(columns.size());
        for (Column column : columns) {
            Column copyColumn = new Column(getTeiidVersion());
            copyColumn.setName(column.getName());
            copyColumn.setRuntimeType(column.getRuntimeType());
            copyColumn.setAutoIncremented(column.isAutoIncremented());
            copyColumn.setNullType(column.getNullType());
            clone.columns.add(copyColumn);
        }

        clone.primaryKey = cloneList(primaryKey);
        copyMetadataState(clone);
        clone.setTableMetadata(this.tableMetadata);
        if(getOn() != null)
            clone.setOn(getOn());

        clone.commitAction = this.commitAction;
        return clone;        
    }

    @Since(Version.TEIID_8_10)
    public CommitAction getCommitAction() {
        return commitAction;
    }

    @Since(Version.TEIID_8_10)
    public void setCommitAction(CommitAction commitAction) {
        this.commitAction = commitAction;
    }
}
