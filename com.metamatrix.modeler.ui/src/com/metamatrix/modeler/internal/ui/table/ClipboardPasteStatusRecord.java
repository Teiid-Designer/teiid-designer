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

package com.metamatrix.modeler.internal.ui.table;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ClipboardPasteStatusRecord
 */
public final class ClipboardPasteStatusRecord implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ClipboardPasteStatusRecord.class);
    
    public static final Integer VALID = new Integer(0);
    public static final Integer ERROR = new Integer(1);
    public static final Integer WARNING = new Integer(2);
    public static final Integer INFO = new Integer(3);
    public static final Integer ROW_TRUNCATED = new Integer(4);
    public static final Integer COLUMN_TRUNCATED = new Integer(5);
    public static final Integer PROTECTED_COLUMN = new Integer(6);
    
    public static final String COLUMN_TRUNCATED_MSG = Util.getString(PREFIX + "truncatedColumn.msg"); //$NON-NLS-1$
    public static final String ERROR_MSG = Util.getString(PREFIX + "genericError.msg"); //$NON-NLS-1$
    public static final String INFO_MSG = Util.getString(PREFIX + "genericInfo.msg"); //$NON-NLS-1$
    public static final String PROTECTED_COLUMN_MSG = Util.getString(PREFIX + "protectedColumn.msg"); //$NON-NLS-1$
    public static final String REFERENCE_COLUMN_MSG = Util.getString(PREFIX + "referenceColumnIsProtected.msg"); //$NON-NLS-1$   
    public static final String ROW_TRUNCATED_MSG = Util.getString(PREFIX + "truncatedRow.msg"); //$NON-NLS-1$
    public static final String WARNING_MSG = Util.getString(PREFIX + "genericWarning.msg"); //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Integer type;
    private String columnName;
    private String data;
    private String description;
        
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>ClipboardPasteStatusRecord</code> with a valid type.
     * @param theColumnName the name of the column being pasted into
     * @param theData the data being pasted
     */
    public ClipboardPasteStatusRecord(String theColumnName,
                                      String theData) {
        this(VALID, theColumnName, theData, null);
    }
        
    /**
     * Constructs a <code>ClipboardPasteStatusRecord</code> with a generic description.
     * @param theType the status type
     * @param theColumnName the name of the column being pasted into
     * @param theData the data being pasted
     */
    public ClipboardPasteStatusRecord(Integer theType,
                                      String theColumnName,
                                      String theData) {
        this(theType, theColumnName, theData, null);
        
        if (theType == ERROR) description = ERROR_MSG;
        else if (theType == ROW_TRUNCATED) description = ROW_TRUNCATED_MSG;
        else if (theType == COLUMN_TRUNCATED) description = COLUMN_TRUNCATED_MSG;
        else if (theType == WARNING) description = WARNING_MSG;
        else if (theType == INFO) description = INFO_MSG;
    }
        
    /**
     * Constructs a <code>ClipboardPasteStatusRecord</code>.
     * @param theType the status type
     * @param theColumnName the name of the column being pasted into
     * @param theData the data being pasted
     * @param theDescription the record description
     */
    public ClipboardPasteStatusRecord(Integer theType,
                                      String theColumnName,
                                      String theData,
                                      String theDescription) {
        type = theType;
        columnName = theColumnName;
        data = theData;
        description = theDescription;
    }
        
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public String getColumnName() {
        return columnName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getPasteData() {
        return data;
    }
    
    public Integer getType() {
        return type;
    }
    
    public boolean isColumnTruncated() {
        return (type == COLUMN_TRUNCATED);
    }
    
    public boolean isError() {
        return (type == ERROR);
    }
        
    public boolean isInfo() {
        return (type == INFO);
    }
        
    public boolean isProtectedColumn() {
        return (type == PROTECTED_COLUMN);
    }
        
    public boolean isRowTruncated() {
        return (type == ROW_TRUNCATED);
    }
    
    public boolean isValid() {
        return (type == VALID);
    }
        
    public boolean isWarning() {
        return (type == WARNING);
    }
    
    public String paramString() {
        return new StringBuffer().append("isValid=").append(isValid()) //$NON-NLS-1$
                                 .append(", isColumnTruncated=").append(isColumnTruncated()) //$NON-NLS-1$
                                 .append(", isError=").append(isError()) //$NON-NLS-1$
                                 .append(", isInfo=").append(isInfo()) //$NON-NLS-1$
                                 .append(", isProtectedColumn=").append(isInfo()) //$NON-NLS-1$
                                 .append(", isRowTruncated=").append(isProtectedColumn()) //$NON-NLS-1$
                                 .append(", isValid=").append(isValid()) //$NON-NLS-1$
                                 .append(", isWarning=").append(isInfo()) //$NON-NLS-1$
                                 .append(", columnName=").append(getColumnName()) //$NON-NLS-1$
                                 .append(", pasteData=").append(getPasteData()) //$NON-NLS-1$
                                 .append(", description=").append(getDescription()) //$NON-NLS-1$
                                 .toString();
    }
    
    public void setDescription(String theDescription) {
        description = theDescription;
    }
    
    public void setPasteData(String theData) {
        data = theData;
    }
    
    public void setType(Integer theType) {
        type = theType;
    }
    
}
