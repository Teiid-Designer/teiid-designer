/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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

