/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.uml;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;


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
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    public String getStereotype(Object eObject) {
        return WebServiceMetamodelPlugin.Util.getString("_UI_Interface_type"); //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected WebServiceComponent assertWebServiceComponent(Object eObject) {
        CoreArgCheck.isInstanceOf(Interface.class, eObject);
        return (Interface)eObject;
    }
}
