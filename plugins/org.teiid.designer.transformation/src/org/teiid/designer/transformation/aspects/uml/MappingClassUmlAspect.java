/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * MappingClassUmlAspect
 */
public class MappingClassUmlAspect extends AbstractTransformationUmlAspect implements UmlClassifier {

    /**
     * Construct an instance of MappingClassUmlAspect.
     * 
     */
    public MappingClassUmlAspect() {
        super();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            MappingClass mappingClass = assertMappingClass(eObject);
            mappingClass.setName(newSignature);
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
        final MappingClass mappingClass = assertMappingClass(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(mappingClass.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$                
                result.append(mappingClass.getName() );        
                break;
            default :
                throw new TeiidRuntimeException(TransformationPlugin.Util.getString("MappingClassUmlAspect.Invalid_show_mask_for_getSignature") + showMask); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected MappingClass assertMappingClass(Object eObject) {
        CoreArgCheck.isInstanceOf(MappingClass.class, eObject);
        return (MappingClass)eObject;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     */
    @Override
	public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     */
    @Override
	public Collection getSupertypes(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     */
    @Override
    public int getVisibility(Object eObject) {
        return VISIBILITY_PUBLIC;
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
                return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_MappingClass_type"); //$NON-NLS-1$
            } else if (type.equals(ModelType.LOGICAL_LITERAL)) {
                return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_FragmentClass_type"); //$NON-NLS-1$
            }
        }
        // If we get this far, assume it's a mapping  class
        return org.teiid.designer.metamodels.transformation.TransformationPlugin.Util.getString("_UI_MappingClass_type"); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    @Override
	public String getEditableSignature(Object eObject) {
        return getSignature(eObject,UmlClassifier.SIGNATURE_NAME);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    @Override
	public boolean isAbstract(Object eObject) {
        return false;
    }
    
}
