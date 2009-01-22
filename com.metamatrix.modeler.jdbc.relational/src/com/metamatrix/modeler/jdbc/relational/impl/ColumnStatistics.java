/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.sql.Types;

/**
 * Column statistics value object 
 * @since 4.3
 */
public class ColumnStatistics {

    String name;
    int jdbcType;
    boolean nullable;
    int numDistinctValues = -1;
    int numNullValues = -1;
    String min;
    String max;
    String nativeType;

    
    public ColumnStatistics(String name) {
        this.name = name;
    }
    
    public ColumnStatistics(String name, int type, boolean nullable) {
        this(name);
        this.jdbcType = type;
        setNullable(nullable);
    }
    
    // Might be able to cut this down more to exclude floating point types
    public boolean isNDVCalculationRequired() {
        return this.jdbcType == Types.BIGINT ||
            this.jdbcType == Types.CHAR ||
            this.jdbcType == Types.DATE || 
            this.jdbcType == Types.DECIMAL || 
            this.jdbcType == Types.DOUBLE || 
            this.jdbcType == Types.FLOAT ||
            this.jdbcType == Types.INTEGER ||
            this.jdbcType == Types.NUMERIC ||
            this.jdbcType == Types.REAL || 
            this.jdbcType == Types.SMALLINT ||
            this.jdbcType == Types.TIME ||
            this.jdbcType == Types.TIMESTAMP ||
            this.jdbcType == Types.TINYINT ||
            this.jdbcType == Types.VARCHAR;            
    }
            
    // Just grab this for numeric and date/time
    public boolean isMinMaxCalculationRequired() {
        return this.jdbcType == Types.BIGINT ||
            this.jdbcType == Types.DATE || 
            this.jdbcType == Types.DECIMAL || 
            this.jdbcType == Types.DOUBLE || 
            this.jdbcType == Types.FLOAT ||
            this.jdbcType == Types.INTEGER ||
            this.jdbcType == Types.NUMERIC ||
            this.jdbcType == Types.REAL || 
            this.jdbcType == Types.SMALLINT || 
            this.jdbcType == Types.TIME ||
            this.jdbcType == Types.TIMESTAMP ||
            this.jdbcType == Types.TINYINT;
    }
    
    // Only for nullable columns
    public boolean isNNVCalculationRequired() {
        return nullable && this.jdbcType != Types.LONGVARCHAR;
    }

    public int getJdbcType() {
        return this.jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getMax() {
        return this.max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return this.min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;

        // If not nullable, default nnv to 0
        if(! this.nullable) {
            this.numNullValues = 0;
        } else if (this.jdbcType == Types.LONGVARCHAR) {
            this.numNullValues = -1;
        }       
    }

    public int getNumDistinctValues() {
        return this.numDistinctValues;
    }

    public void setNumDistinctValues(int numDistinctValues) {
        this.numDistinctValues = numDistinctValues;
    }

    public int getNumNullValues() {
        return this.numNullValues;
    }

    public void setNumNullValues(int numNullValues) {
        this.numNullValues = numNullValues;
    }

    
    /** 
     * @return Returns the nativeType.
     */
    public String getNativeType() {
        return this.nativeType;
    }

    
    /** 
     * @param nativeType The nativeType to set.
     */
    public void setNativeType(String nativeType) {
        this.nativeType = nativeType;
    }
    
}
