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

/**
 * The <code>ITablePasteValidator</code> class validates table data.
 */
public interface ITablePasteValidator {
    
    /**
     * Constructs status record for the proposed value and the specified table row and column.
     * @param theProposedValue the value being validated
     * @param theRow the row where the proposed data will be used
     * @param theColumn the column where the proposed data will be used
     * @return the <code>ClipboardPasteStatusRecord</code> with the validation results
     */
    ClipboardPasteStatusRecord constructPasteStatusRecord(String theProposedValue, int theRow, int theColumn);

    /**
     * Obtains the selected row and column of the table this validator is associated with.
     * @return the selected row (return array index 0) and the selected column (return array index 1)
     */
    int[] getSelectedRowAndColumn();
}
