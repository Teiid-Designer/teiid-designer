/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metadata.runtime.impl.ColumnRecordImpl;
import com.metamatrix.modeler.core.metadata.runtime.ColumnRecord;

/**
 * TestColumnRecordComparator
 */
public class TestColumnRecordComparator extends TestCase {
    /**
     * Constructor for TestCompositeMappingAdapter.
     * 
     * @param name
     */
    public TestColumnRecordComparator( String name ) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestColumnRecordComparator"); //$NON-NLS-1$
        suite.addTestSuite(TestColumnRecordComparator.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    public void testNullArguments() {
        ColumnRecordComparator comparator = new ColumnRecordComparator();
        try {
            comparator.compare(null, null);
        } catch (NullPointerException e) {
            // Expected Result
            return;
        }
        fail("Expected to fail with NullPointerException"); //$NON-NLS-1$
    }

    public void testComparatorEquals() {
        ColumnRecordComparator comparator1 = new ColumnRecordComparator();
        ColumnRecordComparator comparator2 = new ColumnRecordComparator();
        assertFalse(comparator1.equals(comparator2));
    }

    public void testEquals() {
        ColumnRecordComparator comparator = new ColumnRecordComparator();
        ColumnRecordImpl rec1 = new ColumnRecordImpl();
        rec1.setPosition(1);
        ColumnRecordImpl rec2 = new ColumnRecordImpl();
        rec2.setPosition(1);
        assertEquals(comparator.compare(rec1, rec2), 0);
    }

    public void testLessThan() {
        ColumnRecordComparator comparator = new ColumnRecordComparator();
        ColumnRecordImpl rec1 = new ColumnRecordImpl();
        rec1.setPosition(1);
        ColumnRecordImpl rec2 = new ColumnRecordImpl();
        rec2.setPosition(2);
        assertEquals(comparator.compare(rec1, rec2), -1);

    }

    public void testGreaterThan() {
        ColumnRecordComparator comparator = new ColumnRecordComparator();
        ColumnRecordImpl rec1 = new ColumnRecordImpl();
        rec1.setPosition(2);
        ColumnRecordImpl rec2 = new ColumnRecordImpl();
        rec2.setPosition(1);
        assertEquals(comparator.compare(rec1, rec2), 1);
    }

    public void testSort() {
        ColumnRecordComparator comparator = new ColumnRecordComparator();
        ArrayList list = new ArrayList();

        ColumnRecordImpl rec1 = new ColumnRecordImpl();
        rec1.setPosition(2);
        list.add(rec1);

        ColumnRecordImpl rec2 = new ColumnRecordImpl();
        rec2.setPosition(1);
        list.add(rec2);

        ColumnRecordImpl rec3 = new ColumnRecordImpl();
        rec3.setPosition(3);
        list.add(rec3);

        Collections.sort(list, comparator);
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            ColumnRecord recd = (ColumnRecord)iter.next();
            System.out.println(recd.getPosition());
        }
    }
}
