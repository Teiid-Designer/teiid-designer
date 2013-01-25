/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface IJoinType <LV extends ILanguageVisitor> extends ILanguageObject<LV> {

    enum Types {
        /** Represents an inner join:  a INNER JOIN b */
        JOIN_INNER(0, false),

        /** Represents a right outer join:  a RIGHT OUTER JOIN b */
        JOIN_RIGHT_OUTER(1, true),

        /** Represents a left outer join:  a LEFT OUTER JOIN b */
        JOIN_LEFT_OUTER(2, true),

        /** Represents a full outer join:  a FULL OUTER JOIN b */
        JOIN_FULL_OUTER(3, true),

        /** Represents a cross join:  a CROSS JOIN b */
        JOIN_CROSS(4, false),

        /** Represents a union join:  a UNION JOIN b - not used after rewrite */
        JOIN_UNION(5, true),

        /** internal SEMI Join type */
        JOIN_SEMI(6, false),

        /** internal ANTI SEMI Join type */
        JOIN_ANTI_SEMI(7, true);

        private final int typeCode;

        private final boolean outer;

        private Types(int typeCode, boolean outer) {
            this.typeCode = typeCode;
            this.outer = outer;
        }

        public int getTypeCode() {
            return typeCode;
        }
        
        public boolean isOuter() {
            return this.outer;
        }
    }
    
    /**
     * Used only for comparison during equals, not by users of this class
     * 
     * @return Type code for object
     */
    int getTypeCode();

    /**
     * Check if this join type is an outer join.
     * 
     * @return True if left/right/full outer, false if inner/cross
     */
    boolean isOuter();
}
