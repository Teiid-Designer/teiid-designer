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

import org.eclipse.jface.viewers.TableViewer;
import com.metamatrix.ui.table.TableViewerSorter;

/**
 * ModelObjectTableSorter is the ViewerSorter for the ModelTableEditor.  It automatically
 * hooks up the TableViewer's TableColumn for selection and decorates the columns with the
 * appropriate icons.
 */
public class ModelObjectTableSorter extends TableViewerSorter {

    // ==========================================
    // Constructor

    ModelObjectTableSorter(TableViewer tableViewer) {
        super(tableViewer);
    }

    /**<p>
     * </p>
     * @see com.metamatrix.ui.table.TableViewerSorter#compareColumn(org.eclipse.jface.viewers.TableViewer, java.lang.Object, java.lang.Object, int)
     * @since 4.0
     */
    @Override
    protected int compareColumn(final TableViewer viewer, final Object object1, final Object object2, int column) {
        ModelRowElement m1 = (ModelRowElement) object1;
        ModelRowElement m2 = (ModelRowElement) object2;

        Object m1Value = m1.getValueObject(m1.getPropertyIdForColumn(column).toString());
        Object m2Value = m2.getValueObject(m2.getPropertyIdForColumn(column).toString());

        if (m1Value instanceof Comparable) {
            return ((Comparable)m1Value).compareTo(m2Value);
        }

        return getComparator().compare((m1Value == null) ? "" : m1Value.toString(), //$NON-NLS-1$
                                (m2Value == null) ? "" : m2Value.toString()); //$NON-NLS-1$
    }

}
