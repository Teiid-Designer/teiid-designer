/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.imports;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * CoreImportAspectFactoryImpl
 */
public class CoreImportAspectFactoryImpl implements MetamodelAspectFactory {

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
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
