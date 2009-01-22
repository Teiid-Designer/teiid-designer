/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
