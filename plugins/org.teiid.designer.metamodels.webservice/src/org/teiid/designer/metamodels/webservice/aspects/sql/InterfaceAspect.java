/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;


/** 
 * InterfaceAspect
 */
public class InterfaceAspect extends WebServiceComponentAspect {

    protected InterfaceAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(final char recordType) {
        return false;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
    public void updateObject(final EObject targetObject, final EObject sourceObject) {
    }
}
