/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.metamatrix.connector.metadata.MetadataConnectorConstants;
import com.metamatrix.connector.metadata.ResultsIterator;

/**
 * Uses an IObjectSource to process ObjectQuery requests and to produce results which are Lists of Lists of objects.
 * Each inner List corresponds to a row in the result set.  Each object in the inner List corresponds to the value
 * for a specific column and row in the result set.
 * 
 * If the table name contains a method name in "()" at the end of the string then the method name will be called
 * on each result object.  And each result of the method call will result in a new row being added to the result set.
 * In effect, this facility allows multi-valued method calls to serve as the basis of table definitions.
 * 
 * So a table name of the form "TABLE_NAME(getList)" will cause the method "getList" to be called on all result objects.
 * 
 * The method name will be stripped off when the table name is passed down to the object source. 
 */
public class ObjectQueryProcessor implements ResultsIterator.ResultsProcessor {
    private final IObjectSource objectSource;
    private String tableDefiningMethodName = null;
    private IObjectQuery query;
    
    public ObjectQueryProcessor(final IObjectSource objectSource) {
        this.objectSource = objectSource;
    }

    public ResultsIterator process(final IObjectQuery query) throws MetadataException {
        tableDefiningMethodName = null;
        String tableName = query.getTableNameInSource();
        String tableNameForObjectSource = tableName;
        int startMethodNameIndex = tableName.lastIndexOf(String.valueOf(MetadataConnectorConstants.START_METHOD_NAME_CHAR));
        if (startMethodNameIndex > 0) {
            int endMethodNameIndex = tableName.lastIndexOf(String.valueOf(MetadataConnectorConstants.END_METHOD_NAME_CHAR));
            if ( endMethodNameIndex > 0) {
                if (endMethodNameIndex > startMethodNameIndex) {
                    tableDefiningMethodName = tableName.substring(startMethodNameIndex + 1, endMethodNameIndex);
                    tableNameForObjectSource = tableName.substring(0, startMethodNameIndex);
                }
            }
        }
        
        this.query = query;
        Collection results = objectSource.getObjects(tableNameForObjectSource, query.getCriteria());
        return new ResultsIterator(this, results.iterator());
    }
    
    /* (non-Javadoc)
	 * @see com.metamatrix.connector.metadata.internal.ResultsProcessor#createRows(java.lang.Object, java.util.List)
	 */
    public void createRows(Object resultObject, List rows) {        
        ReflectionWrapper wrapper = new ReflectionWrapper(resultObject);
        if (tableDefiningMethodName == null) {
            addRow(wrapper, null, rows);
        } else {
            Collection subTableCollection = null;
            Object subTableResults = wrapper.get(tableDefiningMethodName);
            if (subTableResults.getClass().isArray()) {
                subTableCollection = Arrays.asList((Object[]) subTableResults);
            } else if (subTableResults instanceof Collection) {
                subTableCollection = (Collection) subTableResults;
            }
            createRowsFor(wrapper, subTableCollection, rows);
        }
    }
    
    private void createRowsFor(ReflectionWrapper wrapperAroundResultObject, Collection subTableCollection, List rows) {
        for (Iterator iterator=subTableCollection.iterator(); iterator.hasNext(); ) {
            Object subTableObject = iterator.next();
            addRow(wrapperAroundResultObject, subTableObject, rows);
        }        
    }

    private void addRow(ReflectionWrapper wrapper, Object subTableObject, List rows) {
        List newRow = new ArrayList();
        String[] columnNames = query.getColumnNames();
        //Class[] columnTypes = query.getColumnTypes();
        for (int i = 0; i < columnNames.length; i++) {
            Object value = null;
            
            if (columnNames[i].equals(tableDefiningMethodName)) {
                value = subTableObject;
            } else if (columnNames[i].startsWith(tableDefiningMethodName + MetadataConnectorConstants.METHOD_DELIMITER)) {
                ReflectionWrapper subWrapper = new ReflectionWrapper( subTableObject );
                String columnMethodName = columnNames[i].substring(tableDefiningMethodName.length()+1);
                value = subWrapper.get(columnMethodName);
            } else {
                value = wrapper.get(columnNames[i]);
            }
            query.checkType(i, value);
            if(value != null) {
                query.checkCaseType(i, value);
                Integer caseType = query.getCaseType(i);
                if(caseType.equals(IObjectQuery.UPPER_CASE)) {
                    value = value.toString().toUpperCase();
                } else if(caseType.equals(IObjectQuery.LOWER_CASE)) {
                    value = value.toString().toLowerCase();
                }
            }
            newRow.add(value);
        }
        rows.add(newRow);
    }
}
