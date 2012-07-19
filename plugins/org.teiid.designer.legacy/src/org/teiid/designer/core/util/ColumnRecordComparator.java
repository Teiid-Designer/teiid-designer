/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.core.util;

import java.io.Serializable;
import java.util.Comparator;

import org.teiid.designer.metadata.runtime.ColumnRecord;

/**
 *
 * @since 8.0
 */
public class ColumnRecordComparator implements Comparator<ColumnRecord>, Serializable {

    /**
     */
    private static final long serialVersionUID = 1L;

    /* 
     *  This method compares the objects with respect to their position.
     *  @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
	public int compare(ColumnRecord colRec1, ColumnRecord colRec2) {
        int position1 = colRec1.getPosition();
        int position2 = colRec2.getPosition();

        return position1 - position2;
    }

}