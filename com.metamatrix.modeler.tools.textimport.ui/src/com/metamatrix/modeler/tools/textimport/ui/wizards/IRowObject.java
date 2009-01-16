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
