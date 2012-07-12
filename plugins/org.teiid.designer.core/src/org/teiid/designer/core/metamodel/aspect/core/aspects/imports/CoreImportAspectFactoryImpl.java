/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.imports;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.core.CorePackage;


/**
 * CoreImportAspectFactoryImpl
 */
public class CoreImportAspectFactoryImpl implements MetamodelAspectFactory {

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, org.teiid.designer.core.metamodel.aspect.MetamodelEntity)
	 */
	public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
		switch (classifier.getClassifierID()) {
			case CorePackage.ANNOTATION: return null;
			case CorePackage.MODEL_ANNOTATION: return null;
			case CorePackage.ANNOTATION_CONTAINER: return null;
			case CorePackage.LINK: return null;
			case CorePackage.LINK_CONTAINER: return null;
			case CorePackage.MODEL_IMPORT: return new ModelImportAspect(entity);
			default:
				throw new IllegalArgumentException(ModelerCore.Util.getString("CoreImportAspectFactoryImpl.Invalid_Classifier_ID_{0},_for_creating_SQL_Aspect__1", classifier)); //$NON-NLS-1$
		}
	}

}
