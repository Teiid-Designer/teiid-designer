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
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.xsd.provider.XSDSimpleTypeDefinitionItemProvider;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * MappingClassColumnUmlAspect
 *
 * @since 8.0
 */
public class MappingClassColumnUmlAspect extends AbstractTransformationUmlAspect implements UmlProperty {
    
    /**
     * Construct an instance of MappingClassColumnUmlAspect.
     * 
     */
    public MappingClassColumnUmlAspect() {
        super();
    }

    protected MappingClassColumn assertMappingClassColumn(Object eObject) {
        CoreArgCheck.isInstanceOf(MappingClassColumn.class, eObject);
        return (MappingClassColumn)eObject;
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
        // If Virtual, then use mapping class
        EmfResource emfResource = (EmfResource)((EObject)eObject).eResource();
        if (emfResource.getModelAnnotation() != null) {
            ModelType type = emfResource.getModelAnnotation().getModelType();
            if (type.equals(ModelType.VIRTUAL_LITERAL)) {
                return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_MappingClassColumn_type"); //$NON-NLS-1$
            } else if (type.equals(ModelType.LOGICAL_LITERAL)) {
                return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_FragmentClassElement_type"); //$NON-NLS-1$
            }
        }
        // If we get this far, assume it's a mapping  class column
        return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_MappingClassColumn_type"); //$NON-NLS-1$
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
            MappingClassColumn column = assertMappingClassColumn(eObject);
            column.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID, 0, TransformationPlugin.Util.getString("MappingClassUmlAspect.Signature_set") + newSignature, null); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
    @Override
	public String getSignature(Object eObject, int showMask) {
        final MappingClassColumn col = assertMappingClassColumn(eObject);
        
        // Get the name of the input parameter type
        EObject colType = col.getType();
        String typeName = (colType != null ? ModelerCore.getModelEditor().getName(colType) : null);
        if (typeName == null && colType != null) {
            //the name of the type may be null. In that case, show the label text
            final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
            final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(colType, IItemLabelProvider.class);
            if(provider instanceof XSDSimpleTypeDefinitionItemProvider) {
                typeName = ((XSDSimpleTypeDefinitionItemProvider)provider).getText(colType, false);
            }
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
                //Type and Stereotype
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
                throw new TeiidRuntimeException(TransformationPlugin.Util.getString("MappingClassUmlAspect.Invalid_show_mask_for_getSignature") + showMask); //$NON-NLS-1$
        }
        return result.toString();
    }

}
