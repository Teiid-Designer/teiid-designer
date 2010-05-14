/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestMultiplicity
 */
public class TestMultiplicity extends TestCase {

    /**
     * Constructor for TestMultiplicity.
     * 
     * @param name
     */
    public TestMultiplicity( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Multiplicity.clearPool();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestMultiplicity"); //$NON-NLS-1$
        suite.addTestSuite(TestMultiplicity.class);
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

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    public Multiplicity helpCreateMultiplicity( final String multStr,
                                                final boolean shouldSucceed ) throws Exception {
        try {
            final Multiplicity mult = Multiplicity.get(multStr);
            if (!shouldSucceed) {
                fail("Unexpectedly able to create multiplicity object for string \"" + multStr + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            }
            // Test that the same instance is found again ...
            final Multiplicity mult2 = Multiplicity.get(multStr);
            if (mult != mult2) {
                fail("Multiple calls to Multiplicity.get(...) result in different references"); //$NON-NLS-1$
            }
            return mult;
        } catch (MultiplicityExpressionException e) {
            if (shouldSucceed) {
                throw e;
            }
        }
        return null;
    }

    public void helpTestMultiplicity( final Multiplicity mult,
                                      final int min,
                                      final int max,
                                      final boolean ordered,
                                      final boolean unique,
                                      final boolean unlimited,
                                      final String stringified,
                                      final int[] shouldInclude,
                                      final int[] shouldExclude ) {
        if (mult.getMaximum() != max) {
            fail("The maximum value (" + mult.getMaximum() + //$NON-NLS-1$
                 ") didn't match expected (" + max + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (mult.getMinimum() != min) {
            fail("The minimum value (" + mult.getMinimum() + //$NON-NLS-1$
                 ") didn't match expected (" + min + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (mult.isOrdered() != ordered) {
            fail("The method isOrdered() returned " + mult.isOrdered() + //$NON-NLS-1$
                 " but was expected to return " + ordered); //$NON-NLS-1$
        }
        if (mult.isUnique() != unique) {
            fail("The method isUnique() returned " + mult.isUnique() + //$NON-NLS-1$
                 " but was expected to return " + unique); //$NON-NLS-1$
        }
        if (mult.isUnlimited() != unlimited) {
            fail("The method isUnlimited() returned " + mult.isUnlimited() + //$NON-NLS-1$
                 " but was expected to return " + unlimited); //$NON-NLS-1$
        }
        final String actual = mult.toString();
        if (actual == null) {
            fail("Multiplicity.toString() should never return null"); //$NON-NLS-1$
        }
        if (!actual.equals(stringified)) {
            fail("The toString() returned \"" + actual + '"' + //$NON-NLS-1$
                 " but was expected to return \"" + stringified + '"'); //$NON-NLS-1$
        }

        if (shouldInclude != null) {
            for (int i = 0; i < shouldInclude.length; i++) {
                int val = shouldInclude[i];
                if (!mult.isIncluded(val)) {
                    fail("The multiplicity \"" + mult + "\" should include " + val); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        if (shouldExclude != null) {
            for (int i = 0; i < shouldExclude.length; i++) {
                int val = shouldExclude[i];
                if (mult.isIncluded(val)) {
                    fail("The multiplicity \"" + mult + "\" should exclude " + val); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testUnboundedInstance() {
        final Multiplicity mult = Multiplicity.UNBOUNDED;
        final int min = 0;
        final int max = Multiplicity.UNBOUNDED_VALUE;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = true;
        final String str = Multiplicity.UNBOUNDED_DEFINITION;
        final int[] shouldInclude = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final int[] shouldExclude = new int[] {-1, -100};
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testOneOrMoreInstance() {
        final Multiplicity mult = Multiplicity.ONE_OR_MORE;
        final int min = 1;
        final int max = Multiplicity.UNBOUNDED_VALUE;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = true;
        final String str = "1..*"; //$NON-NLS-1$
        final int[] shouldInclude = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final int[] shouldExclude = new int[] {0, -1, -100};
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testOnlyOneInstance() {
        final Multiplicity mult = Multiplicity.ONLY_ONE;
        final int min = 1;
        final int max = 1;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = "1"; //$NON-NLS-1$
        final int[] shouldInclude = new int[] {1};
        final int[] shouldExclude = new int[] {0, -1, -100, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testZeroOrOneInstance() {
        final Multiplicity mult = Multiplicity.ZERO_OR_ONE;
        final int min = 0;
        final int max = 1;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = "0..1"; //$NON-NLS-1$
        final int[] shouldInclude = new int[] {0, 1};
        final int[] shouldExclude = new int[] {-1, -100, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testRangeMultiplicity1() throws Exception {
        final String multStr = "0,1,2,3,5,7,9,11"; //$NON-NLS-1$
        final int min = 0;
        final int max = 11;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = "0..3,5,7,9,11"; //$NON-NLS-1$
        final int[] shouldInclude = new int[] {0, 1, 2, 3, 5, 7, 9, 11};
        final int[] shouldExclude = new int[] {-1, 4, 6, 8, 10, 12, 13, 14, 15, 16, 17, 100, 1000};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testRangeMultiplicity2() throws Exception {
        final String multStr = "0..3,5,7,9,11"; //$NON-NLS-1$
        final int min = 0;
        final int max = 11;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = multStr;
        final int[] shouldInclude = new int[] {0, 1, 2, 3, 5, 7, 9, 11};
        final int[] shouldExclude = new int[] {-1, 4, 6, 8, 10, 12, 13, 14, 15, 16, 17, 100, 1000};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolOne() throws Exception {
        final String multStr = "1"; //$NON-NLS-1$
        final int min = 1;
        final int max = 1;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = multStr;
        final int[] shouldInclude = new int[] {1};
        final int[] shouldExclude = new int[] {-1, 0, 2, 3, 4, 5, 6, 8, 10, 12, 13, 14, 15, 16, 17, 100, 1000};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        if (mult != Multiplicity.ONLY_ONE) {
            fail("Multiple calls to Multiplicity.get(\"1\") result in different references"); //$NON-NLS-1$
        }
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolUnbounded1() throws Exception {
        final String multStr = "*"; //$NON-NLS-1$
        final int min = 0;
        final int max = Multiplicity.UNBOUNDED_VALUE;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = true;
        final String str = Multiplicity.UNBOUNDED_DEFINITION;
        final int[] shouldInclude = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final int[] shouldExclude = new int[] {-1, -100};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        if (mult != Multiplicity.UNBOUNDED) {
            fail("Multiple calls to Multiplicity.get(\"*\") result in different references"); //$NON-NLS-1$
        }
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolUnbounded2() throws Exception {
        final String multStr = "0..*"; //$NON-NLS-1$
        final int min = 0;
        final int max = Multiplicity.UNBOUNDED_VALUE;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = true;
        final String str = Multiplicity.UNBOUNDED_DEFINITION;
        final int[] shouldInclude = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final int[] shouldExclude = new int[] {-1, -100};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        if (mult != Multiplicity.UNBOUNDED) {
            fail("Multiple calls to Multiplicity.get(\"0..*\") result in different references"); //$NON-NLS-1$
        }
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolOneOrMore() throws Exception {
        final String multStr = "1..*"; //$NON-NLS-1$
        final int min = 1;
        final int max = Multiplicity.UNBOUNDED_VALUE;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = true;
        final String str = multStr;
        final int[] shouldInclude = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final int[] shouldExclude = new int[] {0, -1, -100};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        if (mult != Multiplicity.ONE_OR_MORE) {
            fail("Multiple calls to Multiplicity.get(\"1..*\") result in different references"); //$NON-NLS-1$
        }
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolOnlyOne() throws Exception {
        final String multStr = "1"; //$NON-NLS-1$
        final int min = 1;
        final int max = 1;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = multStr;
        final int[] shouldInclude = new int[] {1};
        final int[] shouldExclude = new int[] {0, -1, -100, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        if (mult != Multiplicity.ONLY_ONE) {
            fail("Multiple calls to Multiplicity.get(\"1\") result in different references"); //$NON-NLS-1$
        }
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolZeroOrOne() throws Exception {
        final String multStr = "0..1"; //$NON-NLS-1$
        final int min = 0;
        final int max = 1;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = multStr;
        final int[] shouldInclude = new int[] {0, 1};
        final int[] shouldExclude = new int[] {-1, -100, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        if (mult != Multiplicity.ZERO_OR_ONE) {
            fail("Multiple calls to Multiplicity.get(\"0..1\") result in different references"); //$NON-NLS-1$
        }
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolZeroToTwo() throws Exception {
        final String multStr = "0..2"; //$NON-NLS-1$
        final int min = 0;
        final int max = 2;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = multStr;
        final int[] shouldInclude = new int[] {0, 1, 2};
        final int[] shouldExclude = new int[] {-1, -100, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }

    public void testPoolTwo() throws Exception {
        final String multStr = "2"; //$NON-NLS-1$
        final int min = 2;
        final int max = 2;
        final boolean ordered = false;
        final boolean unique = false;
        final boolean unlimited = false;
        final String str = multStr;
        final int[] shouldInclude = new int[] {2};
        final int[] shouldExclude = new int[] {-1, -100, 0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000};
        final Multiplicity mult = helpCreateMultiplicity(multStr, true);
        helpTestMultiplicity(mult, min, max, ordered, unique, unlimited, str, shouldInclude, shouldExclude);
    }
}
