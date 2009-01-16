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

package com.metamatrix.metamodels.function.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.function.FunctionParameter;
import com.metamatrix.metamodels.function.FunctionPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;

/**
 * ColumnAspect
 */
public class FunctionParameterAspect extends AbstractFunctionAspect implements UmlProperty {
    /**
     * Construct an instance of ColumnAspect.
     * @param entity
     */
    public FunctionParameterAspect(MetamodelEntity entity){
        super(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    public boolean isAssociationEnd(Object property) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        return FunctionPlugin.getPluginResourceLocator().getString("_UI_FunctionParameter_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            FunctionParameter param = assertFunctionParameter(eObject);
            param.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, FunctionPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, FunctionPlugin.PLUGIN_ID, 0, FunctionPlugin.Util.getString("FunctionParameterAspect.ok"), null); //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        FunctionParameter param = assertFunctionParameter(eObject);
        StringBuffer result = new StringBuffer();
        //case 16 is for properties, which should return an empty string, so 
        //it has been added in to the remaining cases where applicable.
        switch (showMask) {
            case 1 :
            case 17:
                //Name
                result.append(param.getName() );
                break;
            case 2 :
            case 18:
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
            case 19:
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );     
                result.append(">> "); //$NON-NLS-1$ 
                result.append(param.getName() );        
                break;
            case 4 :
            case 20: 
                //Type
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 5 :
            case 21:
                //Name and type
                result.append(param.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 6 :
            case 22:
                //Type and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );     
                result.append(">>"); //$NON-NLS-1$                 
                result.append(" : "); //$NON-NLS-1$
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 7 :
            case 23:
                //Name, Stereotype and type
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );     
                result.append(">> "); //$NON-NLS-1$                 
                result.append(param.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 8 :
            case 24:
                //Initial Value
                result.append(""); //$NON-NLS-1$
                break;
            case 9 :
            case 25:
                //Name and Initial Value
                result.append(param.getName() );
                break;
            case 10 :
            case 26 :
                //Initial Value and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 11 :
            case 27 :
                //Stereotype, Name and Initial Value, 
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );
                result.append(">> "); //$NON-NLS-1$
                result.append(param.getName() );
                break;
            case 12 :
            case 28 :
                //Initial Value and Type
                if(param.getType() != null){
                    result.append(param.getType() );
                }
            break;
            case 13 :
            case 29 :
                //Name, Type, InitialValue 
                result.append(param.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 14 :
            case 30 :
                //Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );
                result.append(">>"); //$NON-NLS-1$
                result.append(" : "); //$NON-NLS-1$
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 15 :
            case 31 :
                //Name, Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(param) );
                result.append(">> "); //$NON-NLS-1$
                result.append(param.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 16 :
                //Properties
                return (""); //$NON-NLS-1$
            default :
                throw new MetaMatrixRuntimeException(FunctionPlugin.Util.getString("FunctionParameterAspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected FunctionParameter assertFunctionParameter(Object eObject) {
        ArgCheck.isInstanceOf(FunctionParameter.class, eObject);
        return (FunctionParameter)eObject;
    }

}
