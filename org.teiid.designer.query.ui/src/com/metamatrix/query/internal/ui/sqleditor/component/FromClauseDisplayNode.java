/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor.component;

import com.metamatrix.query.sql.lang.FromClause;
import com.metamatrix.query.sql.lang.Option;

/**
 * The <code>FromClauseDisplayNode</code> class is used by
 * <code>QueryDisplayComponent</code> to represent a sub-clause within a
 * query from.
 */
public abstract class FromClauseDisplayNode extends DisplayNode {
    
    
    void addFromClauseDepOptions(FromClause obj) {
        if (obj.isMakeDep()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, Option.MAKEDEP));
        }
        if (obj.isMakeNotDep()) {
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this,SPACE));            
            displayNodeList.add(DisplayNodeFactory.createDisplayNode(this, Option.MAKENOTDEP));
        }
    }

}

