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

package com.metamatrix.metamodels.builder.translator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.builder.BuilderConstants;
import com.metamatrix.metamodels.builder.DebugConstants;
import com.metamatrix.metamodels.builder.MetamodelBuilderPlugin;
import com.metamatrix.metamodels.builder.MetamodelEntityRecord;
import com.metamatrix.metamodels.builder.ModelRecord;
import com.metamatrix.metamodels.internal.builder.util.BuilderUtil;
import com.metamatrix.metamodels.internal.builder.util.MetaClassUriHelper;

/**
 * RecordGenerator - creates Model or Entity Records from ResultSets
 */
public class RecordGenerator implements BuilderConstants {

    private static final String MODEL_TYPE = "ModelType"; //$NON-NLS-1$
    private static final String MODEL_SUBTYPE = "SubType"; //$NON-NLS-1$
    private static final String MODEL_NAME = "Name"; //$NON-NLS-1$
    private static final String MODEL_NAME_IN_SOURCE = "NameInSource"; //$NON-NLS-1$
    private static final String MODEL_DESC = "Description"; //$NON-NLS-1$
    private static final String MODEL_EXT_PACKAGE = "ExtPackage"; //$NON-NLS-1$
    private static final String METACLASS_URI = "MetaClassUri"; //$NON-NLS-1$
    private static final String PARENT_PATH = "ParentPath"; //$NON-NLS-1$
    private static final String PARENT_METACLASS_URI = "ParentMetaClassUri"; //$NON-NLS-1$
    private static final String ENTITY_NAME = "name"; //$NON-NLS-1$
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RecordGenerator.class);

    private static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object param1,
                                     final Object param2 ) {
        return UTIL.getString(I18N_PREFIX + id, param1, param2);
    }

    // ==================================================================================
    // S T A T I C M E T H O D S
    // ==================================================================================

    /**
     * Generates ModelRecords from the supplied resultSet.
     * 
     * @param resultSet the query ResultSet
     * @param locationPathStr the location path string
     * @param status the MultiStatus used to record status
     * @return a list of MetamodelRecords
     * @throws IllegalArgumentException if the input is <code>null</code>
     */
    public static List generateModelRecords( ResultSet resultSet,
                                             String locationPathStr,
                                             MultiStatus status,
                                             IProgressMonitor monitor ) throws SQLException {
        boolean builderDebugEnabled = MetamodelBuilderPlugin.Util.isDebugEnabled(DebugConstants.METAMODEL_BUILDER);

        List mapList = ResultSetTranslator.translate(resultSet);
        List recordList = new ArrayList(mapList.size());

        Iterator iter = mapList.iterator();
        while (iter.hasNext()) {
            Map recordMap = (Map)iter.next();
            String modelType = (String)recordMap.get(MODEL_TYPE);
            String subType = (String)recordMap.get(MODEL_SUBTYPE);
            String name = (String)recordMap.get(MODEL_NAME);
            String nameInSource = (String)recordMap.get(MODEL_NAME_IN_SOURCE);
            String desc = (String)recordMap.get(MODEL_DESC);
            String extPackage = (String)recordMap.get(MODEL_EXT_PACKAGE);
            ModelRecord record = null;
            // Check for null modelType or name up front.
            if (modelType == null || name == null) {
                final String msg = getString("unableToCreateModelRecord"); //$NON-NLS-1$
                BuilderUtil.addStatus(status, IStatus.WARNING, msg);
                if (builderDebugEnabled) {
                    MetamodelBuilderPlugin.Util.log(IStatus.WARNING, msg);
                }
            } else {
                try {
                    record = new ModelRecord(modelType, subType, name, nameInSource, desc, extPackage);
                } catch (IllegalArgumentException ex) {
                    final String msg = getString("unableToCreateModelRecord"); //$NON-NLS-1$
                    BuilderUtil.addStatus(status, IStatus.WARNING, msg, ex);
                    if (builderDebugEnabled) {
                        MetamodelBuilderPlugin.Util.log(IStatus.WARNING, msg);
                    }
                }
            }
            if (record != null) {
                record.setLocationPath(locationPathStr);
                recordList.add(record);
            }
            if (monitor != null) {
                monitor.worked(1);
            }
        }

        return recordList;
    }

    /**
     * Generates MetamodelEntityRecords from the supplied resultSet.
     * 
     * @param resultSet the query ResultSet
     * @param status the MultiStatus used to record status
     * @return a list of MetamodelEntityRecords
     * @throws IllegalArgumentException if the input is <code>null</code>
     */
    public static List generateEntityRecords( ResultSet resultSet,
                                              MultiStatus status,
                                              IProgressMonitor monitor ) throws SQLException {
        boolean builderDebugEnabled = MetamodelBuilderPlugin.Util.isDebugEnabled(DebugConstants.METAMODEL_BUILDER);

        List mapList = ResultSetTranslator.translate(resultSet);
        List recordList = new ArrayList(mapList.size());

        Iterator iter = mapList.iterator();
        while (iter.hasNext()) {
            Map recordMap = (Map)iter.next();
            // Get the special mappings - (they are removed since the map is used for setting the features)
            String metaClassUri = (String)recordMap.remove(METACLASS_URI);
            String parentPath = (String)recordMap.remove(PARENT_PATH);
            String parentMetaclassUri = (String)recordMap.remove(PARENT_METACLASS_URI);
            String name = (String)recordMap.get(ENTITY_NAME);
            MetamodelEntityRecord record = null;

            // Error Check for null metaClassUri or parentPath up front
            if (metaClassUri == null || parentPath == null) {
                String metaClass = "NullMetaClass"; //$NON-NLS-1$
                if (metaClassUri != null) {
                    metaClass = MetaClassUriHelper.getEClassName(metaClassUri);
                }
                // Log warning message
                final String msg = getString("unableToCreateEntityRecord", metaClass, name); //$NON-NLS-1$
                BuilderUtil.addStatus(status, IStatus.WARNING, msg);
                if (builderDebugEnabled) {
                    MetamodelBuilderPlugin.Util.log(IStatus.WARNING, msg);
                }
                // Create the record
            } else {
                try {
                    record = new MetamodelEntityRecord(metaClassUri, parentPath, parentMetaclassUri, recordMap);
                } catch (IllegalArgumentException ex) {
                    String metaClass = "NullMetaClass"; //$NON-NLS-1$
                    metaClass = MetaClassUriHelper.getEClassName(metaClassUri);
                    final String msg = getString("unableToCreateEntityRecord", metaClass, name); //$NON-NLS-1$
                    BuilderUtil.addStatus(status, IStatus.WARNING, msg, ex);
                    if (builderDebugEnabled) {
                        MetamodelBuilderPlugin.Util.log(IStatus.WARNING, msg);
                    }
                }
            }
            if (record != null) {
                recordList.add(record);
            }
            if (monitor != null) {
                monitor.worked(1);
            }
        }

        return recordList;
    }
}
