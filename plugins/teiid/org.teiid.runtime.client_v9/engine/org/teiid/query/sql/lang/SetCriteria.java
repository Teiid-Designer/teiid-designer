/* Generated By:JJTree: Do not edit this line. SetCriteria.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=TeiidNodeFactory,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.teiid.query.sql.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class SetCriteria extends AbstractSetCriteria implements ISetCriteria<Expression, LanguageVisitor> {

    /** The set of value expressions */
    private Collection<Expression> values;

    private boolean allConstants;

    /**
     * @param p
     * @param id
     */
    public SetCriteria(ITeiidServerVersion p, int id) {
        super(p, id);
    }

    /**
     * @return the values
     */
    @Override
    public Collection<Expression> getValues() {
        return this.values;
    }

    /**
     * @param values the values to set
     */
    @Override
    public void setValues(Collection<Expression> values) {
        this.values = values;
    }

    /**
     * @return allConstants
     */
    public boolean isAllConstants() {
        return allConstants;
    }
    
    /**
     * @param allConstants
     */
    public void setAllConstants(boolean allConstants) {
        this.allConstants = allConstants;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (this.allConstants ? 1231 : 1237);
        result = prime * result + ((this.values == null) ? 0 : this.values.hashCode());
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
        SetCriteria other = (SetCriteria)obj;
        if (this.allConstants != other.allConstants)
            return false;
        if (this.values == null) {
            if (other.values != null)
                return false;
        } else if (this.values.size() != other.values.size() || 
            (! this.values.containsAll(other.values)))
            return false;

        return true;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SetCriteria clone() {
        SetCriteria criteriaCopy = new SetCriteria(getTeiidVersion(), this.id);

        Collection copyValues = null;
	    if (isAllConstants()) {
	    	copyValues = new LinkedHashSet(values);
	    } else {
	    	copyValues = LanguageObject.Util.deepClone(new ArrayList(values), Expression.class);
	    }
	    criteriaCopy.setValues( new LinkedHashSet(cloneCollection(copyValues)));
        if(getExpression() != null)
        	criteriaCopy.setExpression(getExpression().clone());
        criteriaCopy.setNegated(isNegated());

        criteriaCopy.allConstants = allConstants;

        return criteriaCopy;
    }

}
/* JavaCC - OriginalChecksum=9f7eb5b2819c59fc94d4b273872b85f1 (do not edit this line) */
