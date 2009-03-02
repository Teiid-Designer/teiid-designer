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

public class PersonalSchemaTest extends BaseSchemaProcessingTestClass {

    @Override
    protected List getSchemaPaths() {
        List paths = new ArrayList();
        paths.add("./src/sources/personal.xsd"); //$NON-NLS-1$
        return paths;
    }

    @Override
    protected int getPotentialRootCount() {
        return 8;
    }

    @Override
    protected int getTableCount() {
        return 4;
    }

    @Override
    protected int[] getAttributeCounts() {
        return new int[] {2, 9, 2, 1};
    }

    @Override
    protected int[] getChildCounts() {
        return new int[] {0, 2, 0, 1};
    }

    @Override
    protected int[] getParentCounts() {
        return new int[] {1, 1, 1, 0};
    }

    @Override
    protected boolean representTypes() {
        return false;
    }
}
