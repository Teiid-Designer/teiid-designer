/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

public class SubqueryHint {
    public static String MJ = "MJ"; //$NON-NLS-1$
    public static String NOUNNEST = "NO_UNNEST"; //$NON-NLS-1$
    public static String DJ = "DJ"; //$NON-NLS-1$

    private boolean mergeJoin;
    private boolean noUnnest;
    private boolean depJoin;
    
    public void setMergeJoin(boolean semiJoin) {
        this.mergeJoin = semiJoin;
    }
    
    public boolean isMergeJoin() {
        return mergeJoin;
    }
    
    public void setNoUnnest(boolean noUnnest) {
        this.noUnnest = noUnnest;
    }
    
    public boolean isNoUnnest() {
        return noUnnest;
    }
    
    public void setDepJoin() {
        this.depJoin = true;
        this.mergeJoin = true;
    }
    
    public boolean isDepJoin() {
        return depJoin;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SubqueryHint)) {
            return false;
        }
        SubqueryHint other = (SubqueryHint) obj;
        return mergeJoin == other.mergeJoin 
        && noUnnest == other.noUnnest 
        && depJoin == other.depJoin;
    }
    
    public SubqueryHint clone() {
        SubqueryHint clone = new SubqueryHint();
        clone.mergeJoin = this.mergeJoin;
        clone.noUnnest = this.noUnnest;
        clone.depJoin = this.depJoin;
        return clone;
    }
    
}