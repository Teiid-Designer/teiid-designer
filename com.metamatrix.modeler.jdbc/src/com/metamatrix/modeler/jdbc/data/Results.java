/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.jdbc.data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * The Results interface is the primary means of accessing the cached data returned
 * from a JDBC {@link ResultSet}.
 */
public interface Results {
    
    /**
     * Return the metadata for this results object.
     * @return the result metadata; never null
     */
    ResultsMetadata getMetadata();
    
    /**
     * Returns the total number of records in these results
     * @return
     */
    int getRowCount();
    
    /**
     * Returns the total number of records in the results
     * @return
     */
    int getTotalRowCount();
    
    /**
     * Return the array containing the rows in these results.
     * @return the array of the row objects.
     */
    Object[] getRows();
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell
     */
    Object getObject( Object row, int columnIndex );
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as a string (or converted to a string, if possible);
     * or <code>null</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to a String
     */
    String getString( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as a boolean (or converted to a boolean, if possible);
     * or <code>false</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to a boolean
     */
    boolean getBoolean( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as a short (or converted to a short, if possible);
     * or <code>0</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to a short
     */
    short getShort( Object row, int columnIndex ) throws JdbcConversionException;

    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as an int (or converted to an int, if possible);
     * or <code>0</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to an int
     */
    int getInt( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as a long (or converted to a long, if possible);
     * or <code>0</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to a long
     */
    long getLong( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as a float (or converted to a float, if possible);
     * or <code>0</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to a float
     */
    float getFloat( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as a double (or converted to a double, if possible);
     * or <code>0</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to a double
     */
    double getDouble( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell as a byte (or converted to a byte, if possible);
     * or <code>0</code> if the value of the cell is {@link Types#NULL null}.
     * @throws JdbcConversionException if the value cannot be converted to a byte
     */
    byte getByte( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell
     * @throws JdbcConversionException if the value cannot be converted to a byte[]
     */
    byte[] getBytes( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell
     * @throws JdbcConversionException if the value cannot be converted to a {@link Date}
     */
    Date getDate( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Return the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @return the value in the cell
     * @throws JdbcConversionException if the value cannot be converted to a {@link Timestamp}
     */
    Timestamp getTimestamp( Object row, int columnIndex ) throws JdbcConversionException;
    
    /**
     * Set the value in the cell denoted by the specified column on the specified
     * row.
     * @param row the row from which the cell value is to be obtained
     * @param columnIndex the index of the cell in the row
     * @param newValue the new value for the cell
     */
    void setObject( Object row, int columnIndex, Object newValue );

    /**
     * Return whether this object is a subset of another Results object and whether
     * there is another subset following these Results
     * @return true if there is another Results object following this object, or
     * false otherwise
     */
    boolean hasNextResults();
    
    /**
     * Return the subset of Results that follow this Results object.
     * @return the next Results object, or null if there is none
     */
    Results getNextResults();
    
    /**
     * Return whether this object is a subset of another Results object and whether
     * there is another subset preceding these Results
     * @return true if there is another Results object preceding this object, or
     * false otherwise
     */
    boolean hasPreviousResults();
    
    /**
     * Return the subset of Results that precedes this Results object.
     * @return the previous Results object, or null if there is none
     */
    Results getPreviousResults();

}
