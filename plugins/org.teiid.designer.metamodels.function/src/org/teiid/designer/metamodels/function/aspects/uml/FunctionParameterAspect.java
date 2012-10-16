/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.metamodels.function.FunctionParameter;
import org.teiid.designer.metamodels.function.FunctionPlugin;


/**
 * ColumnAspect
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    @Override
	public boolean isAssociationEnd(Object property) {
        return false;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return FunctionPlugin.getPluginResourceLocator().getString("_UI_FunctionParameter_type"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            FunctionParameter param = assertFunctionParameter(eObject);
            param.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, FunctionPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, FunctionPlugin.PLUGIN_ID, 0, FunctionPlugin.Util.getString("FunctionParameterAspect.ok"), null); //$NON-NLS-1$
    }

    @Override
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
                throw new TeiidDesignerRuntimeException(FunctionPlugin.Util.getString("FunctionParameterAspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected FunctionParameter assertFunctionParameter(Object eObject) {
        CoreArgCheck.isInstanceOf(FunctionParameter.class, eObject);
        return (FunctionParameter)eObject;
    }

}
