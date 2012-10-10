/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.builder.translator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.metamodels.builder.MetamodelBuilderPlugin;
import org.teiid.designer.metamodels.builder.MetamodelEntityRecord;
import org.teiid.designer.metamodels.builder.ModelRecord;
import org.teiid.designer.metamodels.builder.util.BuilderUtil;
import org.teiid.designer.metamodels.builder.util.MetaClassUriHelper;


/**
 * RecordGenerator - creates Model or Entity Records from ResultSets
 *
 * @since 8.0
 */
public class RecordGenerator {

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
        return MetamodelBuilderPlugin.Util.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object param1,
                                     final Object param2 ) {
        return MetamodelBuilderPlugin.Util.getString(I18N_PREFIX + id, param1, param2);
    }

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
            } else {
                try {
                    record = new ModelRecord(modelType, subType, name, nameInSource, desc, extPackage);
                } catch (IllegalArgumentException ex) {
                    final String msg = getString("unableToCreateModelRecord"); //$NON-NLS-1$
                    BuilderUtil.addStatus(status, IStatus.WARNING, msg, ex);
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
                // Create the record
            } else {
                try {
                    record = new MetamodelEntityRecord(metaClassUri, parentPath, parentMetaclassUri, recordMap);
                } catch (IllegalArgumentException ex) {
                    String metaClass = "NullMetaClass"; //$NON-NLS-1$
                    metaClass = MetaClassUriHelper.getEClassName(metaClassUri);
                    final String msg = getString("unableToCreateEntityRecord", metaClass, name); //$NON-NLS-1$
                    BuilderUtil.addStatus(status, IStatus.WARNING, msg, ex);
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
