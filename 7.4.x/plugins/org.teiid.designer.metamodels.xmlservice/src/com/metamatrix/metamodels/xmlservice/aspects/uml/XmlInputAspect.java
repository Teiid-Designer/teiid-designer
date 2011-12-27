/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.aspects.uml;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;
import com.metamatrix.modeler.core.types.DatatypeManager;


/** 
 * @since 4.2
 */
public class XmlInputAspect extends XmlServiceComponentAspect implements UmlProperty {

    /** 
     * @param entity
     * @since 4.2
     */
    public XmlInputAspect(MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see com.metamatrix.metamodels.webservice.aspects.uml.XmlServiceComponentAspect#assertXmlServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected XmlServiceComponent assertXmlServiceComponent(Object eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return (XmlInput)eObject;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     * @since 4.2
     */
    public boolean isAssociationEnd(Object property) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    public String getStereotype(Object eObject) {
        return XmlServiceMetamodelPlugin.Util.getString("_UI_XmlInput_type"); //$NON-NLS-1$
    }
    
    @Override
    public String getSignature(Object eObject, int showMask) {
        XmlInput input = (XmlInput) assertXmlServiceComponent(eObject);
        StringBuffer result = new StringBuffer();
        final String name = input.getName();
        final String stereoType = getStereotype(input);

        EObject type = input.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(input,true);
        final String tmpName = dtMgr.getName(type);
        final String dtName = tmpName == null ? "" : tmpName; //$NON-NLS-1$

        //case 16 is for properties, which should return an empty string, so 
        //it has been added in to the remaining cases where applicable.
        switch (showMask) {
            case 1 :
            case 17:
                //Name
                result.append(name);
                break;
            case 2 :
            case 18:
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
            case 19:
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);     
                result.append(">> "); //$NON-NLS-1$ 
                result.append(name);        
                break;
            case 4 :
            case 20: 
                //Type
                result.append(dtName);
                break;
            case 5 :
            case 21:
                //Name and type
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 6 :
            case 22:
                //Type and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);     
                result.append(">>"); //$NON-NLS-1$                 
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 7 :
            case 23:
                //Name, Stereotype and type
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);     
                result.append(">> "); //$NON-NLS-1$                 
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 8 :
            case 24:
                //Initial Value
                result.append(""); //$NON-NLS-1$
                break;
            case 9 :
            case 25:
                //Name and Initial Value
                result.append(name);
                break;
            case 10 :
            case 26 :
                //Initial Value and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);
                result.append(">>"); //$NON-NLS-1$
                break;
            case 11 :
            case 27 :
                //Stereotype, Name and Initial Value, 
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);
                result.append(">> "); //$NON-NLS-1$
                result.append(name);
                break;
            case 12 :
            case 28 :
                //Initial Value and Type
	            result.append(dtName);
	            break;
            case 13 :
            case 29 :
                //Name, Type, InitialValue 
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 14 :
            case 30 :
                //Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);
                result.append(">>"); //$NON-NLS-1$
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 15 :
            case 31 :
                //Name, Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(stereoType);
                result.append(">> "); //$NON-NLS-1$
                result.append(name);
                result.append(" : "); //$NON-NLS-1$
                result.append(dtName);
                break;
            case 16 :
                //Properties
                return (""); //$NON-NLS-1$
            default :
                throw new TeiidRuntimeException(XmlServiceMetamodelPlugin.Util.getString("XmlInputAspect.1") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }
}
