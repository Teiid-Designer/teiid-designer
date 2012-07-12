/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.function.FunctionPackage;
import org.teiid.designer.metamodels.function.FunctionPlugin;


/**
 * FunctionValidationAspectFactoryImpl
 */
public class FunctionValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /*
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, org.teiid.designer.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case FunctionPackage.FUNCTION:
            case FunctionPackage.SCALAR_FUNCTION:       return new ScalarFunctionAspect(entity);
            case FunctionPackage.FUNCTION_PARAMETER:    return new FunctionParameterAspect(entity);
            case FunctionPackage.RETURN_PARAMETER:      return new ReturnParameterAspect(entity);
            default:
                throw new IllegalArgumentException(FunctionPlugin.Util.getString("FunctionValidationAspectFactoryImpl.Invalid_classifer_ID,_for_creating_validation_aspect__1")+classifier); //$NON-NLS-1$
        }
    }

}
