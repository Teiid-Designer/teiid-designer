/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice.aspects.uml;

import java.util.Collection;
import java.util.Collections;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.xmlservice.XmlOperation;
import org.teiid.designer.metamodels.xmlservice.XmlServiceComponent;
import org.teiid.designer.metamodels.xmlservice.XmlServiceMetamodelPlugin;



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
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Collection getSupertypes(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     * @since 4.2
     */
    @Override
	public boolean isAbstract(Object eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    @Override
	public String getStereotype(Object eObject) {
        return XmlServiceMetamodelPlugin.Util.getString("_UI_XmlOperation_type"); //$NON-NLS-1$
    }

    protected XmlOperation assertOperation(Object eObject) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return (XmlOperation)eObject;
    }    

    /**
     * @see org.teiid.designer.metamodels.webservice.aspects.uml.XmlServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected XmlServiceComponent assertXmlServiceComponent(Object eObject) {
        CoreArgCheck.isInstanceOf(XmlOperation.class, eObject);
        return (XmlOperation)eObject;
    }
}
