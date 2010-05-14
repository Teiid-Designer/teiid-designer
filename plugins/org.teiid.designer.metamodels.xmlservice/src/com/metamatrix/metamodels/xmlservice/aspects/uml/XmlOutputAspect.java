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
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.xmlservice.XmlOutput;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;


/** 
 * @since 4.2
 */
public class XmlOutputAspect extends XmlServiceComponentAspect implements UmlClassifier {

    /** 
     * @param entity
     * @since 4.2
     */
    public XmlOutputAspect(MetamodelEntity entity) {
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
     * @see com.metamatrix.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected XmlServiceComponent assertXmlServiceComponent(Object eObject) {
        CoreArgCheck.isInstanceOf(XmlOutput.class, eObject);
        return (XmlOutput)eObject;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    public String getStereotype(Object eObject) {
        return XmlServiceMetamodelPlugin.Util.getString("_UI_XmlOutput_type"); //$NON-NLS-1$
    }

    @Override
    public String getSignature(Object eObject, int showMask) {
        XmlOutput output = (XmlOutput) assertXmlServiceComponent(eObject);
        StringBuffer result = new StringBuffer();
        final String name = output.getName();
        final String stereoType = getStereotype(output);
        switch (showMask) {
            case 1 :
                //Name
                result.append(name);
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);
                result.append(">> "); //$NON-NLS-1$                
                result.append(name);        
                break;
            default :
                throw new MetaMatrixRuntimeException(XmlServiceMetamodelPlugin.Util.getString("XmlOutputAspect.1") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }
}
