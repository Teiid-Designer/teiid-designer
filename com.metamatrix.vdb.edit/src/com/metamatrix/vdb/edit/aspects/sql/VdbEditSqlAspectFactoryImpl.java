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
