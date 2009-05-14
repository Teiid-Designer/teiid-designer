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

public class PersonalRequestTest extends BaseSchemaProcessingTestClass {

    // The tables in this test occur in this order.

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
        return 1;
    }

    @Override
    protected int[] getAttributeCounts() {
        return new int[] {11};
    }

    @Override
    protected int[] getChildCounts() {
        return new int[] {0};
    }

    @Override
    protected int[] getParentCounts() {
        return new int[] {0};
    }

    @Override
    protected boolean representTypes() {
        return false;
    }

    @Override
    protected boolean processAsRequest() {
        return true;
    }
}
