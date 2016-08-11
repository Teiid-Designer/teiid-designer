/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;

/**
 *
 */
public class TestCriteriaOperator {

    private class ExpectedResults {
        private final ITeiidServerVersion version;
        private final int totalEnums;
        private final String[] missingSymbols;

        public ExpectedResults(ITeiidServerVersion version, int totalEnums, String... missingSymbols) {
            this.version = version;
            this.totalEnums = totalEnums;
            this.missingSymbols = missingSymbols;
        }
    }

    private ITeiidServerVersion version(String versionString) {
        return new TeiidServerVersion(versionString);
    }

    /**
     * Test the {@link Operator#getOperator(ITeiidServerVersion, String)}
     * function to ensure that the correct number of enum values are returned
     * given a specific {@link TeiidServerVersion}.
     */
    @Test
    public void testGetOperator() {
        ExpectedResults[] expectedResults = new ExpectedResults[] {
            new ExpectedResults(version("7.0.0"), Operator.values().length), //$NON-NLS-1$
            new ExpectedResults(version("7.x.0"), Operator.values().length), //$NON-NLS-1$
            new ExpectedResults(version("8.0.0"), Operator.values().length - 5, "like", "in", "is", "is null", "between", ""), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            new ExpectedResults(version("8.x.0"), Operator.values().length - 5, "like", "in", "is", "is null", "between", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        };

        for (ExpectedResults expected : expectedResults) {
            int count = 0;
            for (Operator operator : Operator.values()) {
                try {
                    for (String symbol : operator.getSymbols()) {
                        Operator foundOperator = Operator.getOperator(expected.version, symbol);
                        assertNotNull(foundOperator);
                    }
                    count++;
                } catch (Throwable ex) {
                    // Assertion thrown so operator not found
                    for (String symbol : operator.getSymbols()) {
                        assertTrue(Arrays.asList(expected.missingSymbols).contains(symbol));
                    }
                }
            }
            assertEquals(expected.totalEnums, count);
        }
    }

}
