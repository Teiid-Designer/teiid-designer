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

package com.metamatrix.metamodels.relational.aspects.sql;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * TableAspect
 */
public class BaseTableAspect extends TableAspect {
    
    public BaseTableAspect(MetamodelEntity entity) {
        super(entity);   
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getUniqueKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Collection getUniqueKeys(EObject eObject) {
        final Collection results = new ArrayList();
        
        // Add the primary key
        ArgCheck.isInstanceOf(BaseTable.class, eObject); 
        BaseTable baseTable = (BaseTable) eObject; 
        Object value = baseTable.getPrimaryKey();
        if(value != null) {
            results.add(value);    
        }

        // Add the unique constraints
        EList constarints = baseTable.getUniqueConstraints();
        if(constarints != null) {        
            results.addAll(constarints);
        }

        return results;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getForeignKeys(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Collection getForeignKeys(EObject eObject) {
        ArgCheck.isInstanceOf(BaseTable.class, eObject); 
        BaseTable baseTable = (BaseTable) eObject; 
        return baseTable.getForeignKeys();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#getPrimaryKey(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getPrimaryKey(EObject eObject) {
        ArgCheck.isInstanceOf(BaseTable.class, eObject); 
        BaseTable baseTable = (BaseTable) eObject; 
        return baseTable.getPrimaryKey();
    }

}
