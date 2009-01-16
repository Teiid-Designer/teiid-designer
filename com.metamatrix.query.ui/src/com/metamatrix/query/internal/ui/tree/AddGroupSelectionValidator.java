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

package com.metamatrix.query.internal.ui.tree;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.query.sql.lang.From;
import com.metamatrix.query.sql.lang.Query;
import com.metamatrix.query.sql.lang.Select;
import com.metamatrix.query.sql.lang.SetQuery;
import com.metamatrix.query.ui.UiConstants;


/** 
 * The selection validator for the QueryTreeSelectionDialog when used for adding a group
 * to a virtual transformation as either a UNION or a join in a FROM clause.
 * @since 4.2
 */
public class AddGroupSelectionValidator implements ISelectionStatusValidator {

    /** The IStatus code meaning the selection is valid to add to the FROM clause */
    public static final int OK_FROM_CODE = 100;
    /** The IStatus code meaning the selection is valid to add as a UNION to the selected query */
    public static final int OK_UNION_CODE = 200;
    /** The IStatus code meaning the selection is not valid */
    public static final int ERROR_CODE = 300;
    
    /** The title for the QueryTreeSelectionDialog.setTitle() when using this validator */
    public static final String DIALOG_TITLE = UiConstants.Util.getString("AddGroupSelectionValidator.dialogTitle"); //$NON-NLS-1$
    /** The message for the QueryTreeSelectionDialog.setMessage() when using this validator */
    public static final String DIALOG_MESSAGE = UiConstants.Util.getString("AddGroupSelectionValidator.dialogMessage"); //$NON-NLS-1$
    
    private static final String INVALID_MESSAGE = UiConstants.Util.getString("AddGroupSelectionValidator.invalidStatusMessage"); //$NON-NLS-1$
    private static final String OK_UNION_MESSAGE = UiConstants.Util.getString("AddGroupSelectionValidator.okUnionStatusMessage"); //$NON-NLS-1$
    private static final String OK_FROM_MESSAGE = UiConstants.Util.getString("AddGroupSelectionValidator.okFromStatusMessage"); //$NON-NLS-1$
    
    private static final Status INVALID_STATUS
      	= new Status(IStatus.ERROR,  UiConstants.PLUGIN_ID, ERROR_CODE, INVALID_MESSAGE, null);
    private static final Status OK_FROM_STATUS
    	= new Status(IStatus.OK,  UiConstants.PLUGIN_ID, OK_FROM_CODE, OK_FROM_MESSAGE, null);
    private static final Status OK_UNION_STATUS
    	= new Status(IStatus.OK,  UiConstants.PLUGIN_ID, OK_UNION_CODE, OK_UNION_MESSAGE, null);
    
    
    private boolean isUnion = false;
    
    public boolean isUnionSelection() {
        return this.isUnion;
    }
    
    /** 
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     * @since 4.2
     */
    public IStatus validate(Object[] selection) {
        this.isUnion = false;
        if ( selection.length != 1 ) {
            return INVALID_STATUS;
        } else if ( selection[0] instanceof From ) {
            return OK_FROM_STATUS;
        } else if ( selection[0] instanceof SetQuery 
                        || selection[0] instanceof Query
                        || selection[0] instanceof Select ) {
            this.isUnion = true;
            return OK_UNION_STATUS;
        } 
        return INVALID_STATUS;
    }

}
