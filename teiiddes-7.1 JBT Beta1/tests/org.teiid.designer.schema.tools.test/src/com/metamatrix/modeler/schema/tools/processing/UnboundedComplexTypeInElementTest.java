/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import com.metamatrix.modeler.schema.tools.model.schema.RootElement;

public class UnboundedComplexTypeInElementTest extends BaseSchemaProcessingTestClass {

    // The tables in this test occur in this order.
    // ServiceSummaryList
    // Summary

    @Override
    protected List getSchemaPaths() {
        List paths = new ArrayList();
        paths.add("./src/sources/UnboundedComplexTypeInElement.xsd"); //$NON-NLS-1$
        return paths;
    }

    @Override
    protected int getPotentialRootCount() {
        return 8;
    }

    @Override
    protected int getTableCount() {
        return 2;
    }

    @Override
    protected int[] getAttributeCounts() {
        return new int[] {1, 6};
    }

    @Override
    protected int[] getChildCounts() {
        return new int[] {1, 0};
    }

    @Override
    protected int[] getParentCounts() {
        return new int[] {0, 1};
    }

    @Override
    protected boolean representTypes() {
        return true;
    }

    @Override
    protected HashSet selectRootElements( List qRoots ) {
        HashSet roots = new HashSet();
        for (Iterator iter = qRoots.iterator(); iter.hasNext();) {
            RootElement elem = (RootElement)iter.next();
            if (elem.getName().equals("ServiceSummaryList")) { //$NON-NLS-1$
                roots.add(elem);
            }
        }
        return roots;
    }
}
