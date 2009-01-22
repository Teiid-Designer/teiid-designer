/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.textimport.ui.wizards;


/** 
 * @since 4.2
 */
public interface IRowObject {
    public static final char COMMA = ',';
    public static final char DQUOTE = '"';
    public static final char RIGHT_PARENTH = ')';
    public static final char LEFT_PARENTH = '(';

    public static final int UNKNOWN = -1;
    public static final int RESOURCE = 1;

    public boolean isValid();
    
    public String getDescription();
    
    public void setDescription(String desc);
    
    public String getName();
    
    public void setName(String name);
    
    public String getLocation();
    
    public int getObjectType();
    
    public String getDataString();
    
    public void setRawString(String rawString);
    
    public String getRawString();
    
    public void parseRow();
}
