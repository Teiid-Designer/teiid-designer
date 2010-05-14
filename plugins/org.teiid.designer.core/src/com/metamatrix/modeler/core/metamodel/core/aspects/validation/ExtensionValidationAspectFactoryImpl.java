/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationalValidationAspectFactoryImpl
 */
public class ExtensionValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case ExtensionPackage.XATTRIBUTE: return new XAttributeAspect(entity);
            case ExtensionPackage.XCLASS: return new XClassAspect(entity);
            case ExtensionPackage.XENUM: return new XEnumAspect(entity);
            case ExtensionPackage.XENUM_LITERAL: return new XEnumLiteralAspect(entity);//MyDefect : Added            
            case ExtensionPackage.XPACKAGE: return new XPackageAspect(entity);
            default:
                throw new IllegalArgumentException(ModelerCore.Util.getString("CoreValidationAspectFactoryImpl.Invalid_ClassiferID,_for_creating_Validation_Aspect_1",classifier)); //$NON-NLS-1$
        }
    }
    

}
