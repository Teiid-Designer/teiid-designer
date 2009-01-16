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

package com.metamatrix.modeler.jdbc.relational.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.internal.jdbc.relational.ModelerJdbcRelationalConstants;
import com.metamatrix.modeler.jdbc.JdbcPackage;

/**
 * RelationalSqlAspectFactoryImpl
 */
public class JdbcSqlAspectFactoryImpl implements MetamodelAspectFactory {
    
    
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {

        switch (classifier.getClassifierID()) {
            case JdbcPackage.JDBC_SOURCE_PROPERTY: return null;
            case JdbcPackage.JDBC_DRIVER: return null;
            case JdbcPackage.JDBC_SOURCE: return new JdbcSourceAspect(entity);
            case JdbcPackage.JDBC_DRIVER_CONTAINER: return null;
            case JdbcPackage.JDBC_SOURCE_CONTAINER: return null;
            case JdbcPackage.JDBC_IMPORT_SETTINGS: return null;
            case JdbcPackage.JDBC_IMPORT_OPTIONS: return null;
            default:
                throw new IllegalArgumentException(ModelerJdbcRelationalConstants.Util.getString("JdbcSqlAspectFactoryImpl.Invalid_ClassiferID,_for_creating_SQL_Aspect_1",classifier)); //$NON-NLS-1$
        }
    }

}
