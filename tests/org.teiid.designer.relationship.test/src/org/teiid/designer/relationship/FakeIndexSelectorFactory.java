/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship;

import java.util.List;

import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.IndexSelectorFactory;


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
     * @see org.teiid.designer.core.index.IndexSelectorFactory#createIndexSelector(java.util.List)
     */
    @Override
	public IndexSelector createIndexSelector( List modelWorkspaceItems ) {
        return null;
    }

}
