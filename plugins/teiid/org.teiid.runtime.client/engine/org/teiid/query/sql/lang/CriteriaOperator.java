/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

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
        @Removed(Version.TEIID_8_0)
        LIKE("like"), //$NON-NLS-1$

        /** Constant indicating the operand is in the other */
        @Removed(Version.TEIID_8_0)
        IN("in"), //$NON-NLS-1$

        /** Constant indicating the operand is null */
        @Removed(Version.TEIID_8_0)
        IS_NULL("is null", "is"), //$NON-NLS-1$ //$NON-NLS-2$

        /** Constant indicating the operand is between the others */
        @Removed(Version.TEIID_8_0)
        BETWEEN("between"), //$NON-NLS-1$

        /** Constant indicating the operand is null */
        @Removed(Version.TEIID_8_0)
        NO_TYPE(""); //$NON-NLS-1$

        private Collection<String> symbols = new ArrayList<String>();

        private Operator(String... symbols) {
            this.symbols.addAll(Arrays.asList(symbols));
        }

        @Override
        public String toString() {
            String symbol = this.symbols.iterator().next();
            if (symbol == null || symbol.length() == 0)
                return "??"; //$NON-NLS-1$

            return symbol;
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
         * @param index
         * @return {@link Operator} with the given quantifier index
         */
        public static Operator findOperator(int index) {
            for (Operator op : values()) {
                if (op.getIndex() == index)
                    return op;
            }

            throw new IllegalStateException();
        }

        /**
         * @param version of the parser
         * @param symbol
         *
         * @return the {@link Operator} for the given string representation
         */
        public static Operator getOperator(ITeiidServerVersion version, String symbol) {
            for (Operator operator : Operator.values()) {

                boolean foundSymbol = false;
                for (String opSymbol : operator.getSymbols()) {
                    if (opSymbol.equalsIgnoreCase(symbol)) {
                        foundSymbol = true;
                        break;
                    }
                }

                if (! foundSymbol)
                    continue;

                if (! AnnotationUtils.isApplicable(operator, version))
                    continue;

                return operator;
            }

            throw new UnsupportedOperationException("Symbol '" + symbol + "' has no operator in version " + version); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
