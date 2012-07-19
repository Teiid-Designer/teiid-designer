/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.function.FunctionPackage;
import org.teiid.designer.metamodels.function.FunctionPlugin;


/**
 * RelationalUmlAspectFactoryImpl
 *
 * @since 8.0
 */
public class FunctionUmlAspectFactoryImpl implements MetamodelAspectFactory {
    @Override
	public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case FunctionPackage.FUNCTION:            return null;      // abstract class
            case FunctionPackage.SCALAR_FUNCTION:     return new ScalarFunctionAspect(entity);
            case FunctionPackage.FUNCTION_PARAMETER:  return new FunctionParameterAspect(entity);
            case FunctionPackage.RETURN_PARAMETER:    return new ReturnParameterAspect(entity);
            default:
                throw new IllegalArgumentException(FunctionPlugin.Util.getString("FunctionUmlAspectFactoryImpl.Invalid_Classifer_ID,_for_creating_UML_Aspect__1",classifier)); //$NON-NLS-1$
        }
    }

}
