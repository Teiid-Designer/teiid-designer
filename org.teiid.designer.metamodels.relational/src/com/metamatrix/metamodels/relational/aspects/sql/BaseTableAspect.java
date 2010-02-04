/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
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
