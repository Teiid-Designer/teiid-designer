/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing;

import java.util.ArrayList;
import java.util.List;

public class AlphaTest extends BaseSchemaProcessingTestClass {

    @Override
    protected List getSchemaPaths() {
        List paths = new ArrayList();
        paths.add("./src/sources/Alpha.xsd"); //$NON-NLS-1$
        return paths;
    }

    @Override
    protected int getPotentialRootCount() {
        return 2;
    }

    @Override
    protected int getTableCount() {
        return 1;
    }

    @Override
    protected int[] getAttributeCounts() {
        return new int[] {2};
    }

    @Override
    protected int[] getChildCounts() {
        return null;
    }

    @Override
    protected int[] getParentCounts() {
        return null;
    }

    @Override
    protected boolean representTypes() {
        return false;
    }
}
