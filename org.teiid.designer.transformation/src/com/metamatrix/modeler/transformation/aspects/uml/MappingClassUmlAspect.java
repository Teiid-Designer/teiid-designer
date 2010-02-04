/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.transformation.TransformationPlugin;

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
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getSignature(java.lang.Object, int)
     */
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
                throw new MetaMatrixRuntimeException(TransformationPlugin.Util.getString("MappingClassUmlAspect.Invalid_show_mask_for_getSignature") + showMask); //$NON-NLS-1$
        }
        return result.toString();
    }

    protected MappingClass assertMappingClass(Object eObject) {
        ArgCheck.isInstanceOf(MappingClass.class, eObject);
        return (MappingClass)eObject;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     */
    public Collection getRelationships(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     */
    public Collection getSupertypes(Object eObject) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getVisibility(java.lang.Object)
     */
    @Override
    public int getVisibility(Object eObject) {
        return VISIBILITY_PUBLIC;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        // If Virtual, then use mapping class
        EmfResource emfResource = (EmfResource)((EObject)eObject).eResource();
        if (emfResource.getModelAnnotation() != null) {
            ModelType type = emfResource.getModelAnnotation().getModelType();
            if (type.equals(ModelType.VIRTUAL_LITERAL)) {
                return com.metamatrix.metamodels.transformation.TransformationPlugin.Util.getString("_UI_MappingClass_type"); //$NON-NLS-1$
            } else if (type.equals(ModelType.LOGICAL_LITERAL)) {
                return com.metamatrix.metamodels.transformation.TransformationPlugin.Util.getString("_UI_FragmentClass_type"); //$NON-NLS-1$
            }
        }
        // If we get this far, assume it's a mapping  class
        return com.metamatrix.metamodels.transformation.TransformationPlugin.Util.getString("_UI_MappingClass_type"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getEditableSignature(java.lang.Object)
     */
    public String getEditableSignature(Object eObject) {
        return getSignature(eObject,UmlClassifier.SIGNATURE_NAME);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    public boolean isAbstract(Object eObject) {
        return false;
    }
    
}
