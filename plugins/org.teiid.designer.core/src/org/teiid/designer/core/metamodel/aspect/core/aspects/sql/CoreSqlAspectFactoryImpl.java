/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.core.CorePackage;


/**
 * AnnotationSqlAspectFactoryImpl
 */
public class CoreSqlAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, org.teiid.designer.core.metamodel.aspect.MetamodelEntity)
     */
    @Override
	public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case CorePackage.ANNOTATION: return createAnnotationAspect(entity);
            case CorePackage.MODEL_ANNOTATION: return createModelAspect(entity);
            case CorePackage.ANNOTATION_CONTAINER: return null;
            case CorePackage.LINK: return null;
            case CorePackage.LINK_CONTAINER: return null;
            case CorePackage.MODEL_IMPORT: return null;
            default:
                throw new IllegalArgumentException(ModelerCore.Util.getString("AnnotationSqlAspectFactory.Invalid_Classifer_ID,_for_creating_SQL_Aspect__1")+classifier); //$NON-NLS-1$
        }
    }

    private ModelAspect createModelAspect(MetamodelEntity entity) {
        return new ModelAspect(entity);
    }

    private AnnotationAspect createAnnotationAspect(MetamodelEntity entity) {
        return new AnnotationAspect(entity);
    }

}
