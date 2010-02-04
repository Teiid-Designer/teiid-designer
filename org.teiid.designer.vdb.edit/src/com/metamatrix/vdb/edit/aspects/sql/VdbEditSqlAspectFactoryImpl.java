/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;

/**
 * VdbEditSqlAspectFactoryImpl
 */
public class VdbEditSqlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case ManifestPackage.VIRTUAL_DATABASE: return new VirtualDatabaseAspect(entity);
            case ManifestPackage.MODEL_REFERENCE: return null;
            case ManifestPackage.PROBLEM_MARKER: return null;
            case ManifestPackage.MODEL_SOURCE: return null;
            case ManifestPackage.MODEL_SOURCE_PROPERTY: return null;
            default:
                throw new IllegalArgumentException("The class '" + classifier.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

}
