/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.uml;

import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.metamodels.webservice.Input;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;



/** 
 * @since 8.0
 */
public class InputAspect extends WebServiceComponentAspect implements
                                                          UmlProperty {

    /** 
     * @param entity
     * @since 4.2
     */
    public InputAspect(MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected WebServiceComponent assertWebServiceComponent(Object eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return (Input)eObject;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     * @since 4.2
     */
    @Override
	public boolean isAssociationEnd(Object property) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     * @since 4.2
     */
    @Override
	public String getStereotype(Object eObject) {
        return WebServiceMetamodelPlugin.Util.getString("_UI_Input_type"); //$NON-NLS-1$
    }
    
    @Override
    public String getSignature(Object eObject, int showMask) {
        Input input = (Input) assertWebServiceComponent(eObject);
        StringBuffer result = new StringBuffer();
        final String name = input.getName();
        final String stereoType = getStereotype(input);
        final String dtName = DatatypeConstants.BuiltInNames.XML_LITERAL;

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
                throw new TeiidDesignerRuntimeException(WebServiceMetamodelPlugin.Util.getString("InputAspect.1") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }
}
