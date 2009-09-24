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

public class ContinentsTest extends BaseSchemaProcessingTestClass {

    @Override
    protected List getSchemaPaths() {
        List paths = new ArrayList();
        paths.add("./src/sources/continents.xsd"); //$NON-NLS-1$
        return paths;
    }

    @Override
    protected int getPotentialRootCount() {
        return 96;
    }

    @Override
    protected int getTableCount() {
        return 54;
    }

    @Override
    protected int[] getAttributeCounts() {
        return null;
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
        return true;
    }
}
