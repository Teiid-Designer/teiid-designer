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
