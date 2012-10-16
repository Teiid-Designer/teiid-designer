/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * InputParameterUmlAspect
 *
 * @since 8.0
 */
public class InputParameterUmlAspect extends AbstractTransformationUmlAspect implements UmlProperty {
    
    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;

    /**
     * Construct an instance of InputParameterUmlAspect.
     * 
     */
    public InputParameterUmlAspect() {
        super();
        setID(ASPECT_ID);
    }

    protected InputParameter assertInputParameter(Object eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject);
        return (InputParameter)eObject;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    @Override
	public boolean isAssociationEnd(Object property) {
        return false;
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_InputParameter_type"); //$NON-NLS-1$
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    @Override
	public String getEditableSignature(Object eObject) {
        return getSignature(eObject,UmlProperty.SIGNATURE_NAME);
    }
        
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            InputParameter column = assertInputParameter(eObject);
            column.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
            
        return new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID, 0, TransformationPlugin.Util.getString("InputParameterUmlAspect.Signature_set") + newSignature, null); //$NON-NLS-1$
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    @Override
	public String getSignature(Object eObject, int showMask) {
        final InputParameter col = assertInputParameter(eObject);
        
        // Get the name of the input parameter type
        EObject type = col.getType();
		String typeName = null;
		if(type != null) {
			typeName = ModelerCore.getModelEditor().getName(col.getType());
		}
        if (typeName == null) {
            typeName = CoreStringUtil.Constants.EMPTY_STRING;
        }

        StringBuffer result = new StringBuffer();
        //case 16 is for properties, which should return an empty string, so 
        //it has been added in to the remaining cases where applicable.
        switch (showMask) {
            case 1 :
            case 17:
                //Name
                result.append(col.getName() );
                break;
            case 2 :
            case 18:
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
            case 19:
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );     
                result.append(">> "); //$NON-NLS-1$ 
                result.append(col.getName() );        
                break;
            case 4 :
            case 20: 
                //Type
                if(col.getType() != null){
                    result.append(typeName);
                }
                break;
            case 5 :
            case 21:
                //Name and type
                result.append(col.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(col.getType() != null){
                    result.append(typeName);
                }
                break;
            case 6 :
            case 22:
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );     
                result.append(">>"); //$NON-NLS-1$                 
                result.append(" : "); //$NON-NLS-1$
                if(col.getType() != null){
                    result.append(typeName);
                }
                break;
            case 7 :
            case 23:
                //Name, Stereotype and type
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );     
                result.append(">> "); //$NON-NLS-1$                 
                result.append(col.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(col.getType() != null){
                    result.append(typeName);
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
                result.append(col.getName() );
                break;
            case 10 :
            case 26 :
                //Initial Value and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 11 :
            case 27 :
                //Stereotype, Name and Initial Value, 
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );
                result.append(">> "); //$NON-NLS-1$
                result.append(col.getName() );
                break;
            case 12 :
            case 28 :
                //Initial Value and Type
                if(col.getType() != null){
                    result.append(typeName);
                }
                break;
            case 13 :
            case 29 :
                //Name, Type, InitialValue 
                result.append(col.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(col.getType() != null){
                    result.append(typeName);
                }
                break;
            case 14 :
            case 30 :
                //Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );
                result.append(">>"); //$NON-NLS-1$
                result.append(" : "); //$NON-NLS-1$
                if(col.getType() != null){
                    result.append(typeName);
                }
                break;
            case 15 :
            case 31 :
                //Name, Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(col) );
                result.append(">> "); //$NON-NLS-1$
                result.append(col.getName() );
                result.append(" : "); //$NON-NLS-1$
                if(col.getType() != null){
                    result.append(typeName);
                }
                break;
            case 16 :
                //Properties
                return (""); //$NON-NLS-1$
            default :
                throw new TeiidDesignerRuntimeException(TransformationPlugin.Util.getString("InputParameterUmlAspect.Invalid_show_mask_for_getSignature") + showMask); //$NON-NLS-1$
        }
        return result.toString();
    }

}
