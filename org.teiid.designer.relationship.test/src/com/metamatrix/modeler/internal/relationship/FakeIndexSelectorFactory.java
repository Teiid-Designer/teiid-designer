/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import java.util.List;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.index.IndexSelectorFactory;

/**
 * FakeIndexSelectorFactory
 */
public class FakeIndexSelectorFactory implements IndexSelectorFactory {

    /**
     * Construct an instance of FakeIndexSelectorFactory.
     */
    public FakeIndexSelectorFactory() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.index.IndexSelectorFactory#createIndexSelector(java.util.List)
     */
    public IndexSelector createIndexSelector( List modelWorkspaceItems ) {
        return null;
    }

}
