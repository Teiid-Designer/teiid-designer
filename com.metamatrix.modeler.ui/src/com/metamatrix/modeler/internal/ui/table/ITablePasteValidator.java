/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
