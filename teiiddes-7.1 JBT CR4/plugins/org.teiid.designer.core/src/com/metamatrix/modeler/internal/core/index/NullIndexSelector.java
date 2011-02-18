/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import com.metamatrix.core.index.AbstractIndexSelector;
import com.metamatrix.internal.core.index.Index;

/**
 * NullIndexSelector returns an emtpy list for
 */
public class NullIndexSelector extends AbstractIndexSelector {

    private static final Index[] EMPTY_INDEX_ARRAY = new Index[0];

    /**
     * Construct an instance of NullIndexSelector
     */
    public NullIndexSelector() {
    }

    /**
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() {
        return EMPTY_INDEX_ARRAY;
    }

}
