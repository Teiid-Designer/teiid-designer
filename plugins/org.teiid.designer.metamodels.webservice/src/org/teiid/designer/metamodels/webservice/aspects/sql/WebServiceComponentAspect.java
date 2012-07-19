/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;



/** 
 * WebServiceComponentAspect
 *
 * @since 8.0
 */
public abstract class WebServiceComponentAspect extends AbstractMetamodelAspect implements SqlAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;

    protected WebServiceComponentAspect(final MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isQueryable(final EObject eObject) {
        CoreArgCheck.isInstanceOf(WebServiceComponent.class, eObject);
        return true;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getName(final EObject eObject) {
        CoreArgCheck.isInstanceOf(WebServiceComponent.class, eObject); 
        WebServiceComponent entity = (WebServiceComponent) eObject;       
        return entity.getName();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getNameInSource(final EObject eObject) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public void updateObject(final EObject targetObject, final EObject sourceObject) {
    }

}
