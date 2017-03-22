/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.beans.Expression;
import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 * @param <E> 
 * @param <LV> 
 */
public interface ICommand<E extends IExpression, LV extends ILanguageVisitor> 
    extends ILanguageObject<LV>{
    
    /** 
     * Represents an unknown type of command 
     */
    public static final int TYPE_UNKNOWN = 0;
    
    /**
     * Represents a SQL SELECT statement
     */
    public static final int TYPE_QUERY = 1;
    
    /**
     * Represents a SQL INSERT statement
     */
    public static final int TYPE_INSERT = 2;

    /**
     * Represents a SQL UPDATE statement
     */
    public static final int TYPE_UPDATE = 3;

    /**
     * Represents a SQL DELETE statement
     */
    public static final int TYPE_DELETE = 4;

    /**
     * Represents a stored procedure command
     */
    public static final int TYPE_STORED_PROCEDURE = 6;
    
    /**
     * Represents a update stored procedure command
     */
    public static final int TYPE_UPDATE_PROCEDURE = 7;

    /**
     * Represents a batched sequence of UPDATE statements
     */
    public static final int TYPE_BATCHED_UPDATE = 9;
    
    public static final int TYPE_DYNAMIC = 10;
    
    public static final int TYPE_CREATE = 11;
    
    public static final int TYPE_DROP = 12;
    
    public static final int TYPE_TRIGGER_ACTION = 13;
    
    public static final int TYPE_ALTER_VIEW = 14;
    
    public static final int TYPE_ALTER_PROC = 15;
    
    public static final int TYPE_ALTER_TRIGGER = 16;
    
    public static final int TYPE_SOURCE_EVENT = -1;

    /**
     * type of command
     * 
     * @return int value signifying type of command
     */
    int getType();

    /**
     * @return
     */
    IOption getOption();

    /**
     * @return
     */
    List<E> getProjectedSymbols();
    
    /**
     * @return
     */
    List<? extends E> getResultSetColumns();

    /**
     * @return
     */
    boolean isResolved();

}
