/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.webservice.aspects.uml;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.metamodels.webservice.WebServiceMetamodelPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;
import com.metamatrix.modeler.core.types.DatatypeConstants;


/** 
 * @since 4.2
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
     * @see com.metamatrix.metamodels.webservice.aspects.uml.WebServiceComponentAspect#assertWebServiceComponent(java.lang.Object)
     * @since 4.2
     */
    @Override
    protected WebServiceComponent assertWebServiceComponent(Object eObject) {
        ArgCheck.isInstanceOf(Input.class, eObject);
        return (Input)eObject;
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
                throw new MetaMatrixRuntimeException(WebServiceMetamodelPlugin.Util.getString("InputAspect.1") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }
}
