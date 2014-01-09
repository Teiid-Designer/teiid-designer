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

package org.teiid.runtime.client.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.teiid.runtime.client.lang.TeiidNodeFactory.ASTNodes;
import org.teiid.runtime.client.lang.ast.ElementSymbol;
import org.teiid.runtime.client.lang.ast.Expression;
import org.teiid.runtime.client.lang.parser.TeiidParser;

/**
* Represents a StoredProcedure's parameter for encapsulation in the Query framework
* This is basically a holder object set from the Server's implementation of
* a stored procedure.
* The connector will utilize this class to set the appropriate values at the
* datasource layer.
*/
public class SPParameter {

    /**
     * Enumerator for types of parameters 
     */
    public enum ParameterInfo {
        /** Constant identifying an IN parameter */
        IN,

        /** Constant identifying an OUT parameter */
        OUT,

        /** Constant identifying an INOUT parameter */
        INOUT,

        /** Constant identifying a RETURN parameter */
        RETURN_VALUE,

        /** Constant identifying a RESULT SET parameter */
        RESULT_SET;

        /**
         * Get the index of the enumerator. For compatibility
         * with existing code, the index starts at 1 rather than 0.
         * 
         * @return value of index
         */
        public int index() {
            return ordinal() + 1;
        }
    }

    /** Constant identifying an IN parameter */
    public static final int IN = ParameterInfo.IN.index();

    /** Constant identifying an OUT parameter */
    public static final int OUT = ParameterInfo.OUT.index();

    /** Constant identifying an INOUT parameter */
    public static final int INOUT = ParameterInfo.INOUT.index();

    /** Constant identifying a RETURN parameter */
    public static final int RETURN_VALUE = ParameterInfo.RETURN_VALUE.index();

    /** Constant identifying a RESULT SET parameter */
    public static final int RESULT_SET = ParameterInfo.RESULT_SET.index();

    // Basic state
    private int parameterType = ParameterInfo.IN.index();
    private Expression expression;
    private int index;
    private List<ElementSymbol> resultSetColumns;      //contains List of columns if it is result set
    private List<Object> resultSetIDs;          // contains List of metadataIDs for each column in the result set
    private boolean usingDefault;
	private boolean varArg;
	private ElementSymbol parameterSymbol;

    private TeiidParser teiidParser;

    /**
     * Constructor used when constructing a parameter during execution.  In this case we
     * know what the parameter is being filled with but no metadata about the parameter.
     *
     * @param teiidParser
     * @param index the positional index of this parameter
     * @param expression
     */
    public SPParameter(TeiidParser teiidParser, int index, Expression expression) {
        this.teiidParser = teiidParser;
        setIndex(index);
        setExpression(expression);
        this.parameterSymbol = teiidParser.createASTNode(ASTNodes.ELEMENT_SYMBOL);
    }

    /**
     * Constructor used when constructing a parameter from metadata.
     * In this case we specify the description of the parameter but
     * no notion of what it is being filled with.
     *
     * @param teiidParser
     * @param index Parameter index
     * @param parameterType Type of parameter based on class constant - IN, OUT, etc
     * @param name Full name of parameter (including proc name)
     */
    public SPParameter(TeiidParser teiidParser, int index, int parameterType, String name) {
        this.teiidParser = teiidParser;
        setIndex(index);
        setParameterType(parameterType);
        setName(name);
    }

    private ElementSymbol createElementSymbol(String name) {
        ElementSymbol symbol = teiidParser.createASTNode(ASTNodes.ELEMENT_SYMBOL);
        symbol.setName(name);
        return symbol;
    }

    /**
     * Get full parameter name,.  If unknown, null is returned.
     * @return Parameter name
     */
    public String getName() {
        return this.parameterSymbol.getName();
    }

    /**
     * Set full parameter name
     * @param name Parameter name
     */
    public void setName(String name) {
        if (parameterSymbol == null) {
            this.parameterSymbol = createElementSymbol(name);
        } else {
            ElementSymbol es = createElementSymbol(name);
            es.setMetadataID(parameterSymbol.getMetadataID());
            es.setType(parameterSymbol.getType());
            this.parameterSymbol = es;
        }
    }

    /**
     * @param parameterInfo
     */
    public void setParameterType(ParameterInfo parameterInfo) {
        this.parameterType = parameterInfo.index();
    }
    
    /**
     * Set parameter type according to class constants.
     * @param parameterType Type to set
     */
    public void setParameterType(int parameterType){
        // validate against above types
        if(parameterType < ParameterInfo.IN.index() || parameterType > ParameterInfo.RESULT_SET.index()) {
            throw new IllegalArgumentException();
        }
        this.parameterType = parameterType;
    }

    /**
     * Get type of parameter according to class constants.
     * @return Parameter type
     */
    public int getParameterType(){
        return this.parameterType;
    }

    /**
     * Set class type - MetaMatrix runtime types.
     * @param classType See {@link org.teiid.core.types.DataTypeManager.DefaultDataClasses}
     * for types
     */
    public void setClassType(Class<?> classType){
        this.parameterSymbol.setType(classType);
    }

    /**
     * Get class type - MetaMatrix runtime types.
     * @return MetaMatrix runtime type description
     */
    public Class<?> getClassType(){
        return this.parameterSymbol.getType();
    }

    /**
     * Set the expression defining this parameter
     * @param expression The expression defining this parameter's value
     */
    public void setExpression(Expression expression){
        this.expression = expression;
    }

    /**
     * Return the expression defining the value of this parameter
     * @return Expression defining the value of this parameter
     */
    public Expression getExpression(){
        return this.expression;
    }

    /**
     * Set the positional index of this parameter
     * @param index The positional index of this parameter
     */
    public void setIndex(int index){
        this.index = index;
    }

    /**
     * Return the index of this parameter
     * @return The index
     */
    public int getIndex(){
        return this.index;
    }

    /**
     * Add a result set column if this parameter is a return
     * result set.
     * @param colName Name of column
     * @param type Type of column
     * @param id Object Id
     */
    public void addResultSetColumn(String colName, Class<?> type, Object id) {
        if(resultSetColumns == null){
            resultSetColumns = new ArrayList<ElementSymbol>();
            resultSetIDs = new ArrayList<Object>();
        }

        ElementSymbol rsColumn = createElementSymbol(colName);
        rsColumn.setType(type);
        rsColumn.setMetadataID(id);

        resultSetColumns.add(rsColumn);
        
        resultSetIDs.add(id);
    }

    /**
     * Get the result set columns.  If none exist, return empty list.
     * @return List of ElementSymbol representing result set columns
     */
    public List<ElementSymbol> getResultSetColumns(){
        if(resultSetColumns == null){
            return Collections.emptyList();
        }
        return resultSetColumns;
    }
    
    /**
     * Get the result set metadata IDs.  If none exist, return empty list.
     * @return List of Object representing result set metadata IDs
     */
    public List<Object> getResultSetIDs() {
        if(resultSetIDs == null) { 
            return Collections.emptyList();
        }
        return this.resultSetIDs;
    }

    /**
     * Get a particular result set column at the specified position.
     * @param position Position of the result set column
     * @return Element symbol representing the result set column at position
     * @throws IllegalArgumentException If column doesn't exist
     */
    public ElementSymbol getResultSetColumn(int position){
        if(resultSetColumns == null){
            throw new IllegalArgumentException();
        }

        //position is 1 based
        position--;
        if(position >= 0 && position < resultSetColumns.size()) {
            return resultSetColumns.get(position);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Get actual metadataID for this parameter
     * @return Actual metadata ID for this parameter
     */
    public Object getMetadataID() {
        return this.parameterSymbol.getMetadataID();
    }

    /**
     * Set actual metadataID for this parameter
     * @param metadataID Actual metadataID
     */
    public void setMetadataID(Object metadataID) {
        this.parameterSymbol.setMetadataID(metadataID);
    }

    /**
     * Get element symbol representing this parameter.  The symbol will have the
     * same name and type as the parameter.
     * @return Element symbol representing the parameter
     */
    public ElementSymbol getParameterSymbol() {
		return parameterSymbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SPParameter other = (SPParameter)obj;
        if (this.expression == null) {
            if (other.expression != null) return false;
        } else if (!this.expression.equals(other.expression)) return false;
        if (this.index != other.index) return false;
        if (this.teiidParser == null) {
            if (other.teiidParser != null) return false;
        } else if (!this.teiidParser.equals(other.teiidParser)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.expression == null) ? 0 : this.expression.hashCode());
        result = prime * result + this.index;
        result = prime * result + ((this.teiidParser == null) ? 0 : this.teiidParser.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if(this.expression != null) {
            return this.expression.toString();
        }
        return "?"; //$NON-NLS-1$
    }

	/**
	 * @return usingDefault
	 */
	public boolean isUsingDefault() {
		return usingDefault;
	}

	/**
	 * @param usingDefault
	 */
	public void setUsingDefault(boolean usingDefault) {
		this.usingDefault = usingDefault;
	}

	/**
	 * @param varArg
	 */
	public void setVarArg(boolean varArg) {
		this.varArg = varArg;
	}
	
	/**
	 * @return varArg
	 */
	public boolean isVarArg() {
		return varArg;
	}

}
