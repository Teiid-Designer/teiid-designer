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

package com.metamatrix.metamodels.relational.util;

import java.util.Iterator;
import java.util.List;

import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.Table;

/**
 * BaseTableFinder
 */
public class BaseTableFinder extends TableFinder {

    /**
     * Construct an instance of BaseTableFinder.
     * 
     */
    public BaseTableFinder() {
        super();
    }
    
    /**
     * @see com.metamatrix.metamodels.relational.util.RelationalEntityFinder#found(com.metamatrix.metamodels.relational.RelationalEntity)
     */
    @Override
    protected void found(final RelationalEntity entity) {
        if ( entity instanceof BaseTable ) {
            super.found(entity);
        }
    }
    
    /**
     * @see com.metamatrix.metamodels.relational.util.RelationalEntityFinder#found(java.util.List)
     */
    @Override
    protected void found( final List entities ) {
        if ( entities != null ) {
            final Iterator iter = entities.iterator();
            while (iter.hasNext()) {
                final Table table = (Table)iter.next();
                found(table);
            }
        }
    }




}
