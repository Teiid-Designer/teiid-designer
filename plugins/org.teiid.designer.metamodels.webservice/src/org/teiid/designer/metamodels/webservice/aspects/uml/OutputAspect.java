/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.webservice.Output;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;



/** 
 * @since 8.0
 */
public class OutputAspect extends WebServiceComponentAspect implements UmlClassifier {

    /** 
     * @param entity
     * @since 4.2
     */
    public OutputAspect(MetamodelEntity entity) {
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
     * @see org.teiid.designer.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected WebServiceComponent assertWebServiceComponent(Object eObject) {
        CoreArgCheck.isInstanceOf(Output.class, eObject);
        return (Output)eObject;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    @Override
	public String getStereotype(Object eObject) {
        return WebServiceMetamodelPlugin.Util.getString("_UI_Output_type"); //$NON-NLS-1$
    }

    @Override
    public String getSignature(Object eObject, int showMask) {
        Output output = (Output) assertWebServiceComponent(eObject);
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
                throw new TeiidRuntimeException(WebServiceMetamodelPlugin.Util.getString("OutputAspect.1") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }

}
