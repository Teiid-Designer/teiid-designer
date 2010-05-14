/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.util;

import com.metamatrix.core.selection.TreeSelection;

/**
 * NullTreeSelection
 */
public class FakeTreeSelection implements TreeSelection {

    /**
     * Construct an instance of NullTreeSelection.
     * 
     */
    public FakeTreeSelection() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.selection.TreeSelection#getSelectionMode(java.lang.Object)
     */
    public int getSelectionMode(Object node) {
        return SELECTED;
    }

}
