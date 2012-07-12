/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.core.extension.ExtensionPackage;


/**
 * RelationalValidationAspectFactoryImpl
 */
public class ExtensionValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, org.teiid.designer.core.metamodel.aspect.MetamodelEntity)
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
