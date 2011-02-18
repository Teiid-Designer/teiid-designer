/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.TeiidRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.function.FunctionPlugin;
import com.metamatrix.metamodels.function.ReturnParameter;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;

/**
 * ColumnAspect
 */
public class ReturnParameterAspect extends AbstractFunctionAspect implements UmlProperty {
    /**
     * Construct an instance of ColumnAspect.
     * @param entity
     */
    public ReturnParameterAspect(MetamodelEntity entity){
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
        return FunctionPlugin.getPluginResourceLocator().getString("_UI_ReturnParameter_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        return new Status(IStatus.OK, FunctionPlugin.PLUGIN_ID, 0, FunctionPlugin.Util.getString("ReturnParameterAspect.ok"), null); //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        ReturnParameter param = assertReturnParameter(eObject);
        StringBuffer result = new StringBuffer();
        //case 16 is for properties, which should return an empty string, so 
        //it has been added in to the remaining cases where applicable.
        switch (showMask) {
            case 1 :
            case 17:
                //Name (no name use stereotype)
                result.append(getStereotype(param)); 
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
                result.append(getStereotype(param));
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
                result.append(getStereotype(param)); 
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
                result.append(getStereotype(param));
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
                if(param.getType() != null){
                    result.append(param.getType() );
                }
                break;
            case 16 :
                //Properties
                return (""); //$NON-NLS-1$
            default :
                throw new TeiidRuntimeException(FunctionPlugin.Util.getString("ReturnParameterAspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected ReturnParameter assertReturnParameter(Object eObject) {
        CoreArgCheck.isInstanceOf(ReturnParameter.class, eObject);
        return (ReturnParameter)eObject;
    }

}
