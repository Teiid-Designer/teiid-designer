/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.lang.IIsDistinctCriteria;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 * Represents criteria such as:  "<expression> IS DISTINCT FROM <expression>".
 * However due to a lack of direct support for new/old groups as row values,
 * we reference group symbols here instead.
 */
@Since( Version.TEIID_8_12_4 )
public class IsDistinctCriteria extends Criteria
    implements PredicateCriteria, IIsDistinctCriteria<LanguageVisitor> {

    private GroupSymbol leftRowValue;

    private GroupSymbol rightRowValue;

    /** Negation flag. Indicates whether the criteria expression contains a NOT. */
    private boolean negated;

    /**
     * @param p
     * @param id
     */
    public IsDistinctCriteria(TeiidParser p, int id) {
        super(p, id);
    }

    public void setLeftRowValue(GroupSymbol leftRowValue) {
        this.leftRowValue = leftRowValue;
    }

    public void setRightRowValue(GroupSymbol rightRowValue) {
        this.rightRowValue = rightRowValue;
    }

    public GroupSymbol getLeftRowValue() {
        return leftRowValue;
    }

    public GroupSymbol getRightRowValue() {
        return rightRowValue;
    }

    /**
     * Returns whether this criteria is negated.
     * @return flag indicating whether this criteria contains a NOT
     */
    public boolean isNegated() {
        return negated;
    }

    /**
     * Sets the negation flag for this criteria.
     * @param negationFlag true if this criteria contains a NOT; false otherwise
     */
    public void setNegated(boolean negationFlag) {
        negated = negationFlag;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.leftRowValue == null) ? 0 : this.leftRowValue.hashCode());
        result = prime * result + (this.negated ? 1231 : 1237);
        result = prime * result + ((this.rightRowValue == null) ? 0 : this.rightRowValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        IsDistinctCriteria other = (IsDistinctCriteria)obj;
        if (this.leftRowValue == null) {
            if (other.leftRowValue != null)
                return false;
        } else if (!this.leftRowValue.equals(other.leftRowValue))
            return false;
        if (this.negated != other.negated)
            return false;
        if (this.rightRowValue == null) {
            if (other.rightRowValue != null)
                return false;
        } else if (!this.rightRowValue.equals(other.rightRowValue))
            return false;
        return true;
    }

    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Deep copy of object
     * @return Deep copy of object
     */
    @Override
    public IsDistinctCriteria clone() {
        IsDistinctCriteria clone = new IsDistinctCriteria(this.parser, this.id);

        clone.setNegated(isNegated());
        clone.setLeftRowValue(this.getLeftRowValue().clone());
        clone.setRightRowValue(this.getRightRowValue().clone());
        return clone;
    }

}
