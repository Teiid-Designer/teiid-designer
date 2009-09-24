/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.aspects.uml;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.metamodels.webservice.WebServiceMetamodelPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlPackage;

/** 
 * InterfaceAspect
 */
public class InterfaceAspect extends WebServiceComponentAspect implements UmlPackage {

    /** 
     * InterfaceAspect
     * @param entity
     * @since 4.2
     */
    public InterfaceAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    public String getStereotype(Object eObject) {
        return WebServiceMetamodelPlugin.Util.getString("_UI_Interface_type"); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected WebServiceComponent assertWebServiceComponent(Object eObject) {
        ArgCheck.isInstanceOf(Interface.class, eObject);
        return (Interface)eObject;
    }
}
