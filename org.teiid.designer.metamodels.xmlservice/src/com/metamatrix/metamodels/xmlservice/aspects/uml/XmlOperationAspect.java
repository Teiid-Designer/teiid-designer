/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;


/** 
 * @since 4.2
 */
public class XmlOperationAspect extends XmlServiceComponentAspect implements UmlClassifier {

    /** 
     * @param entity
     * @since 4.2
     */
    public XmlOperationAspect(MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     * @since 4.2
     */
    public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     * @since 4.2
     */
    public Collection getSupertypes(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     * @since 4.2
     */
    public boolean isAbstract(Object eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    public String getStereotype(Object eObject) {
        return XmlServiceMetamodelPlugin.Util.getString("_UI_XmlOperation_type"); //$NON-NLS-1$
    }

    protected XmlOperation assertOperation(Object eObject) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return (XmlOperation)eObject;
    }    

    /**
     * @see com.metamatrix.metamodels.webservice.aspects.uml.XmlServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected XmlServiceComponent assertXmlServiceComponent(Object eObject) {
        ArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return (XmlOperation)eObject;
    }
}
