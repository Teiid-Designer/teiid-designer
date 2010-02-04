/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.util;

import java.util.Comparator;
import com.metamatrix.metamodels.core.ModelImport;

/**
 * ModelImportComparator
 */
public class ModelImportComparator implements Comparator {

    /**
     * Construct an instance of ModelImportComparator.
     * 
     */
    public ModelImportComparator() {
        super();
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final Object o1, final Object o2) {
        final ModelImport i1 = (ModelImport)o1;
        final ModelImport i2 = (ModelImport)o2;
        final String path1 = i1.getModelLocation();
        final String path2 = i2.getModelLocation();
//        final String path1 = i1.getPath();
//        final String path2 = i2.getPath();
        if ( path2 == null ) {
            return 0;
        }
        if ( path1 != null ) {
            return path1.compareTo(path2);
        }
        return -1;
    }

}
