/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationalUmlAspectFactoryImpl
 */
public class ExtensionUmlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case ExtensionPackage.XATTRIBUTE:       return new XAttributeUmlAspect(entity);
            case ExtensionPackage.XCLASS:           return new XClassUmlAspect(entity);
            case ExtensionPackage.XPACKAGE:         return new XPackageUmlAspect(entity);
            case ExtensionPackage.XENUM:            return new XEnumUmlAspect(entity);
            case ExtensionPackage.XENUM_LITERAL:    return new XEnumLiteralUmlAspect(entity);
            default:
                throw new IllegalArgumentException(ModelerCore.Util.getString("ExtensionUmlAspectFactoryImpl.Invalid_Classifer_ID_for_creating_UML_Aspect")+classifier); //$NON-NLS-1$
        }
    }

}
