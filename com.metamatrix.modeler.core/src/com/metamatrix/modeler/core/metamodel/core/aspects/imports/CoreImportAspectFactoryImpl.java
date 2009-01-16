/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
