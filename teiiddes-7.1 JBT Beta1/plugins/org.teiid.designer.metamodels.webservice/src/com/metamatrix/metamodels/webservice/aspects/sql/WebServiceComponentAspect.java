/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;


/** 
 * WebServiceComponentAspect
 */
public abstract class WebServiceComponentAspect extends AbstractMetamodelAspect implements SqlAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;

    protected WebServiceComponentAspect(final MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isQueryable(final EObject eObject) {
        CoreArgCheck.isInstanceOf(WebServiceComponent.class, eObject);
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getName(final EObject eObject) {
        CoreArgCheck.isInstanceOf(WebServiceComponent.class, eObject); 
        WebServiceComponent entity = (WebServiceComponent) eObject;       
        return entity.getName();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getNameInSource(final EObject eObject) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public void updateObject(final EObject targetObject, final EObject sourceObject) {
    }

}
