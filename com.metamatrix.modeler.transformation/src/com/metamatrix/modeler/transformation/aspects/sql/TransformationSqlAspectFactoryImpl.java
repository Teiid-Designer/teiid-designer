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

package com.metamatrix.modeler.transformation.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * TransformationSqlAspectFactoryImpl
 */
public class TransformationSqlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case TransformationPackage.MAPPING_CLASS_COLUMN: 
                return new MappingClassColumnSqlAspect(entity);
            case TransformationPackage.MAPPING_CLASS: 
            case TransformationPackage.STAGING_TABLE: 
                return new MappingClassSqlAspect(entity);
            case TransformationPackage.MAPPING_CLASS_SET: 
                return new MappingClassSetSqlAspect(entity);
            case TransformationPackage.SQL_TRANSFORMATION_MAPPING_ROOT: 
                return new SqlTransformationMappingRootSqlAspect(entity);
			case TransformationPackage.TREE_MAPPING_ROOT: 
				return new TreeMappingRootSqlAspect(entity);
            case TransformationPackage.INPUT_PARAMETER: 
                return new InputParameterSqlAspect(entity);
			case TransformationPackage.INPUT_SET: 
				return new InputSetSqlAspect(entity);
            case TransformationPackage.XQUERY_TRANSFORMATION_MAPPING_ROOT: 
                return new XQueryTransformationMappingRootSqlAspect(entity);
            default:
                return null;
        }
    }
}
