/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.teiid.core.util.Assertion;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 *
 */
public interface CriteriaOperator {

    /**
     * Operators used in criteria clauses
     */
    public enum Operator {

        /** Constant indicating the two operands are equal. */
        EQ("="), //$NON-NLS-1$
    
        /** Constant indicating the two operands are not equal. */
        NE("<>", "!="), //$NON-NLS-1$ //$NON-NLS-2$
    
        /** Constant indicating the first operand is less than the second. */
        LT("<"), //$NON-NLS-1$
    
        /** Constant indicating the first operand is greater than the second. */
        GT(">"), //$NON-NLS-1$

        /** Constant indicating the first operand is less than or equal to the second. */
        LE("<="), //$NON-NLS-1$
    
        /** Constant indicating the first operand is greater than or equal to the second. */
        GE(">="), //$NON-NLS-1$

        /** Constant indicating the operand is like the other */
        @Removed("8.0.0")
        LIKE("like"), //$NON-NLS-1$

        /** Constant indicating the operand is in the other */
        @Removed("8.0.0")
        IN("in"), //$NON-NLS-1$

        /** Constant indicating the operand is the other */
        @Removed("8.0.0")
        IS("is"), //$NON-NLS-1$

        /** Constant indicating the operand is between the others */
        @Removed("8.0.0")
        BETWEEN("between"), //$NON-NLS-1$

        /** Constant indicating the operand is null */
        @Removed("8.0.0")
        NO_TYPE(""); //$NON-NLS-1$

        private Collection<String> symbols = new ArrayList<String>();

        private Operator(String... symbols) {
            this.symbols.addAll(Arrays.asList(symbols));
        }

        /**
         * @return the index of the operator
         */
        public int getIndex() {
            return ordinal() + 1;
        }

        /**
         * Operators can have more than one symbol
         * representing them
         *
         * @return collection of symbols delineating the operator
         */
        public Collection<String> getSymbols() {
            return symbols;
        }

        /**
         * @param other
         *
         * @return this operator's index is less than other's
         */
        public boolean isLessThan(Operator other) {
            return this.getIndex() < other.getIndex();
        }

        /**
         * @param other
         *
         * @return this operator's index is greater than other's
         */
        public boolean isGreaterThan(Operator other) {
            return this.getIndex() > other.getIndex();
        }

        /**
         * @param version of the parser
         * @param symbol
         *
         * @return the {@link Operator} for the given string representation
         */
        public static Operator getOperator(ITeiidServerVersion version, String symbol) {
            for (Operator operator : Operator.values()) {

                if (! operator.getSymbols().contains(symbol))
                    continue;

                // Check if the operator has a removed annotation. If it does then check it
                // against the given parser version. In effect, the annotation will cause the
                // operator to be 'hidden' for server versions greated than the version in
                // the annotation.
                try {
                    if (AnnotationUtils.hasAnnotation(operator, Removed.class)) {
                        Removed removed = AnnotationUtils.getAnnotation(operator, Removed.class);
                        if (AnnotationUtils.isGreaterThanOrEqualTo(removed, version))
                            continue;
                    }
                } catch (Exception ex) {
                    // Unlikely to occur since operators are collected from the enum itself
                }

                return operator;
            }
   
            Assertion.failed("Symbol '" + symbol + "' has no operator in version " + version); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
    }
}
