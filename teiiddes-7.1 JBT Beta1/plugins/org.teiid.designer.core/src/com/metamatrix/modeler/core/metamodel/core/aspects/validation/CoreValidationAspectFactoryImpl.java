/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationalValidationAspectFactoryImpl
 */
public class CoreValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case CorePackage.ANNOTATION: return new AnnotationAspect(entity);
            case CorePackage.ANNOTATION_CONTAINER: return null;
            case CorePackage.MODEL_ANNOTATION: return new ModelAnnotationAspect(entity);
            case CorePackage.LINK: return null;
            case CorePackage.LINK_CONTAINER: return null;
            case CorePackage.MODEL_IMPORT: return new ModelImportAspect(entity);
            default:
                throw new IllegalArgumentException(ModelerCore.Util.getString("CoreValidationAspectFactoryImpl.Invalid_ClassiferID,_for_creating_Validation_Aspect_1",classifier)); //$NON-NLS-1$
        }
    }
    

}
