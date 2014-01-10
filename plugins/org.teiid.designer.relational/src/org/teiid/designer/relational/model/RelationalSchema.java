/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.teiid.core.designer.HashCodeUtil;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;



/**
 * Relational Schema
 *
 * @since 8.0
 */
public class RelationalSchema extends RelationalReference {

	private List<RelationalIndex> indexes;
	private List<RelationalProcedure> procedures;
	private List<RelationalTable> tables;
    
	/**
	 * RelationalSchema constructor
	 */
    public RelationalSchema() {
        super();
        setType(TYPES.SCHEMA);
        this.indexes = new ArrayList<RelationalIndex>();
        this.procedures = new ArrayList<RelationalProcedure>();
        this.tables = new ArrayList<RelationalTable>();
        setNameValidator(new RelationalStringNameValidator(false));
    }
    
    /**
     * RelationalSchema constructor
     * @param name the schema name
     */
    public RelationalSchema( String name ) {
        super(name);
        setType(TYPES.SCHEMA);
        this.indexes = new ArrayList<RelationalIndex>();
        this.procedures = new ArrayList<RelationalProcedure>();
        this.tables = new ArrayList<RelationalTable>();
        setNameValidator(new RelationalStringNameValidator(false));
    }
    
    /**
     * @return indexes
     */
    public List<RelationalIndex> getIndexes() {
        return this.indexes;
    }
    
    /**
     * @param index the new index
     */
    public void addIndex(RelationalIndex index) {
        if( this.indexes.add(index) ) {
        	index.setParent(this);
    		handleInfoChanged();
        }
    }
    
    /**
     * @param index the index to remove
     * @return if index was removed or not
     */
    public boolean removeIndex(RelationalIndex index) {
    	if( this.indexes.remove(index) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }
    
    /**
     * @return procedures
     */
    public List<RelationalProcedure> getProcedures() {
        return this.procedures;
    }
    
    /**
     * @param procedure the new procedure
     */
    public void addProcedure(RelationalProcedure procedure) {
        if( this.procedures.add(procedure) ) {
        	procedure.setParent(this);
    		handleInfoChanged();
        }
    }
    
    /**
     * @param procedure the procedure to remove
     * @return if procedure was removed or not
     */
    public boolean removeProcedure(RelationalProcedure procedure) {
    	if( this.procedures.remove(procedure) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }
    
    /**
     * @return tables
     */
    public List<RelationalTable> getTables() {
        return this.tables;
    }
    
    /**
     * @param table the new table
     */
    public void addTable(RelationalTable table) {
        if( this.tables.add(table) ) {
        	table.setParent(this);
    		handleInfoChanged();
        }
    }
    
    /**
     * @param table the table to remove
     * @return if table was removed or not
     */
    public boolean removeTable(RelationalTable table) {
    	if( this.tables.remove(table) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append(" : name = ").append(getName()); //$NON-NLS-1$
		return sb.toString();
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object object ) {
		if (!super.equals(object)) {
			return false;
		}
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        final RelationalSchema other = (RelationalSchema)object;

        // Tables
        Collection<RelationalTable> thisTables = getTables();
        Collection<RelationalTable> thatTables = other.getTables();

        if (thisTables.size() != thatTables.size()) {
            return false;
        }
        
        if (!thisTables.isEmpty() && !thisTables.containsAll(thatTables)) {
            return false;
        }

        // Procedures
        Collection<RelationalProcedure> thisProcedures = getProcedures();
        Collection<RelationalProcedure> thatProcedures = other.getProcedures();

        if (thisProcedures.size() != thatProcedures.size()) {
            return false;
        }
        
        if (!thisProcedures.isEmpty() && !thisProcedures.containsAll(thatProcedures)) {
            return false;
        }

        // Indexes
        Collection<RelationalIndex> thisIndexes = getIndexes();
        Collection<RelationalIndex> thatIndexes = other.getIndexes();

        if (thisIndexes.size() != thatIndexes.size()) {
            return false;
        }
        
        if (!thisIndexes.isEmpty() && !thisIndexes.containsAll(thatIndexes)) {
            return false;
        }

        return true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();

        List<RelationalTable> tables = getTables();
        for(RelationalTable table: tables) {
            result = HashCodeUtil.hashCode(result, table);
        }
        List<RelationalProcedure> procs = getProcedures();
        for(RelationalProcedure proc: procs) {
            result = HashCodeUtil.hashCode(result, proc);
        }
        List<RelationalIndex> indexes = getIndexes();
        for(RelationalIndex index: indexes) {
            result = HashCodeUtil.hashCode(result, index);
        }

        return result;
    }    
	
}
