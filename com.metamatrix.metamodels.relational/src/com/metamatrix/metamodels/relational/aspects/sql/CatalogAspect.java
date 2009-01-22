/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * TableAspect
 */
public class CatalogAspect extends RelationalEntityAspect {

    public CatalogAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType( char recordType ) {
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject( EObject targetObject,
                              EObject sourceObject ) {

    }

}
