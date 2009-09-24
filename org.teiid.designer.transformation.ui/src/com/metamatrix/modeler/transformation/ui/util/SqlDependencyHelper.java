/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;


/** 
 * @since 4.2
 */
public class SqlDependencyHelper {
    private EObject vGroup;
    private HashMap sourceTables = new HashMap();
    private ModelResource vResource;

    private final String STRING_STRING = "string"; //$NON-NLS-1$
    private final String EMPTY_STRING = ""; //$NON-NLS-1$
    private final char DOUBLE_QUOTE = '"';
    private final String NEWLINE = StringUtil.Constants.LINE_FEED;
    private Collection rows = new ArrayList();
    private int maxDep = 0;
    private HashMap columnStrings = new HashMap();
    boolean includeIntermediates = true;
    
    private String delimeter;
    private String nullValue;
    private String nullColumnString;

    private static final boolean ADD_NULL_COLUMN = true;
    private static final boolean DO_NOT_ADD_NULL_COLUMN = false;
    
    public static final int SEARCHABLE = 0;
    public static final int ALL_EXCEPT_LIKE = 1;
    public static final int LIKE_ONLY = 2;
    public static final int UNSEARCHABLE = 3;
    public static final String SEARCHABLE_STRING        = "SEARCHABLE"; //$NON-NLS-1$
    public static final String ALL_EXCEPT_LIKE_STRING   = "ALL_EXCEPT_LIKE"; //$NON-NLS-1$
    public static final String LIKE_ONLY_STRING         = "LIKE_ONLY"; //$NON-NLS-1$
    public static final String UNSEARCHABLE_STRING      = "UNSEARCHABLE"; //$NON-NLS-1$
    public static final String UNKNOWN_STRING           = "UNKNOWN"; //$NON-NLS-1$
    public static final String NULL_STRING              = "NULL"; //$NON-NLS-1$
    
    /** 
     * 
     * @since 4.2
     */
    public SqlDependencyHelper(EObject virtualGroup, boolean includeIntermediates, String delimeter, String nullValue) {
        super();
        
        if( virtualGroup == null ) {
            Assertion.isNull(virtualGroup, "SqlDependencyHelper got NULL virtual group. Expected Non-Null"); //$NON-NLS-1$
        }
        
        this.vGroup = virtualGroup;
        this.includeIntermediates = includeIntermediates;
        this.delimeter = delimeter;
        this.nullValue = nullValue;
        init();
    }
    /** 
     * @return Returns the vGroup.
     * @since 4.2
     */
    public EObject getVGroup() {
        return this.vGroup;
    }
    
    private void init() {
        vResource = ModelUtilities.getModelResourceForModelObject(vGroup);
        loadSourceTables();
        
        loadDependentAttributes();
    }
    
    private void loadSourceTables() {
        findSourceTables();
    }
    
    
    
    private void findSourceTables() {
        // get transformation object
        EObject transformationEObject = getTransformation(vGroup);
        // get sources
        Iterator sourceIter = getSources(transformationEObject).iterator();
        EObject nextSourceEObject = null;
        // walk through sources and add dependencies if "virtual"
        while( sourceIter.hasNext() ) {
            nextSourceEObject = (EObject)sourceIter.next();
            addSourceTable(nextSourceEObject);
            if( ModelObjectUtilities.isVirtual(nextSourceEObject))
                addDependencies(nextSourceEObject);
        }
    }
    
    private void addSourceTable(EObject sourceTable) {
        if( sourceTables.get(sourceTable) == null ) {
            sourceTables.put(sourceTable, "x"); //$NON-NLS-1$

        }
    }
    
    private void addDependencyStringRow(String rowString) {
        rows.add(rowString);
    }
    
    private void addDependencies(EObject virtualSource) {
        List virtualSources = new ArrayList();

        EObject transformationEObject = getTransformation(virtualSource);
        
        if( transformationEObject != null  ) {
            // Get Source Tables for this transformation
            Iterator sourceIter = getSources(transformationEObject).iterator();
            EObject nextSourceEObject = null;
            
            while( sourceIter.hasNext() ) {
                nextSourceEObject = (EObject)sourceIter.next();
                if( ModelObjectUtilities.isVirtual(nextSourceEObject))
                    virtualSources.add(nextSourceEObject);
                addSourceTable(nextSourceEObject);
            }
        }
            
        if( !virtualSources.isEmpty() ) {
            Iterator vIter = virtualSources.iterator();
            while( vIter.hasNext() ) {
                addDependencies((EObject)vIter.next());
            }
            
        }
    }
    
    private void loadDependentAttributes() {
        // Let's get the columns for table
        
        List columns = TransformationHelper.getTableColumns(vGroup);
        Iterator iter = columns.iterator();
        EObject nextColumn = null;
        
        while( iter.hasNext() ) {
            nextColumn = (EObject)iter.next();
            findAttributeDependencies(nextColumn);
        }
    }
    
    private void findAttributeDependencies(EObject selectedColumn) {

        List targetList = Collections.EMPTY_LIST;

        // Let's check to see if the selected attribute is "virtual" or not.
        if( ModelObjectUtilities.isVirtual(selectedColumn) ) {
            getSourceDependencies(selectedColumn, selectedColumn, targetList);
        }
        
        if( !includeIntermediates )
            maxDep = 1;
    }
    
    /*
     * This method initiates the search for source dependencies
     */
    private boolean getSourceDependencies(EObject selectedColumn, EObject selectedAttribute, List targetList) {
        boolean foundDependencies = false;
        // As long as it's virtual attribute we recursively search.
        if( ModelObjectUtilities.isVirtual(selectedAttribute) ) {
            
            // Get the attribute's parent 
            EObject virtualTarget = selectedAttribute.eContainer();
            // if virtualTarget is a Procedure ResultSet, get the parent Procedure
            if(!TransformationHelper.isValidSqlTransformationTarget(virtualTarget) && 
                TransformationHelper.isSqlColumnSet(virtualTarget)) {
                virtualTarget = virtualTarget.eContainer();                
            }
            
            if( virtualTarget != null ) {
                TransformationMappingRoot tmr = (TransformationMappingRoot)getTransformation(virtualTarget);
                
                List attrDepList = new ArrayList(TransformationHelper.getSourceAttributesForTargetAttr(selectedAttribute, tmr));
                
                if (attrDepList.size() == 0) {
                    addDependencyStringRow(getDependentString(selectedColumn, (EObject) null, DO_NOT_ADD_NULL_COLUMN));
                } else {
                    EObject nextEObject = null;
                	Iterator iter = attrDepList.iterator();
                	while( iter.hasNext() ) {
                    	nextEObject = (EObject)iter.next();
                    	if( !nextEObject.equals(selectedAttribute)) {
                        	// Now see if attribute's table is a source table
                        	EObject attributeParent = nextEObject.eContainer();
                        	if( sourceTables.get(attributeParent) != null ) {
                            	foundDependencies = true;
                            	if( !targetList.contains(nextEObject) ) {
                                	// If we don't find a column string for the nextEObject, we create one and add it
                                	// to the columnStrings Map
                                	if( columnStrings.get(nextEObject) == null)
                                    	columnStrings.put(nextEObject, getSourceColumnString(nextEObject));
                                
                                	List newTargetList = new ArrayList(targetList);
                                    	newTargetList.add(nextEObject);
                                
                                    if( ModelObjectUtilities.isVirtual(nextEObject) ) {
                                    	// Now get it's dependencies and add them. (recursive);
                                    	// When there are no more dependencies (foundDep == FALSE) then we go ahead and create
                                    	// the entire row string from the selectedColumn and the newTargetList of dependent
                                    	// columns
                                    	boolean foundDep = getSourceDependencies(selectedColumn, nextEObject, newTargetList);
                                    
                                    	// If we didn't find any dependencies (virtual has no physical in workspace??)
                                    	// We need to create a row here and treat it as an ending source.
                                    	if( !foundDep ) {
                                        	// We need to add a "NULL" row for this case. This will help flag the user to indicate
                                        	// that there were MISSING models in the workspace.
                                        	if( includeIntermediates ) {
                                               addDependencyStringRow(getDependentString(selectedColumn, newTargetList, ADD_NULL_COLUMN));
                                            } else {
                                                addDependencyStringRow(getDependentString(selectedColumn, nextEObject, ADD_NULL_COLUMN));
                                            }
                                        }
                                    } else {
                                        // Got a physical attribute, so let's create a row here!!
                                        if( includeIntermediates ) {
                                            addDependencyStringRow(getDependentString(selectedColumn, newTargetList, DO_NOT_ADD_NULL_COLUMN));
                                        } else {
                                            addDependencyStringRow(getDependentString(selectedColumn, nextEObject, DO_NOT_ADD_NULL_COLUMN));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return foundDependencies;
    }
    
    private EObject getTransformation(EObject targetVirtualGroupEObject){
        return TransformationHelper.getTransformationMappingRoot(targetVirtualGroupEObject);
    }
    
    protected List getSources(EObject transformationEObject) {
        return TransformationSourceManager.getSourceEObjects(transformationEObject);
    }
    
    /*
     * Create a row string comprising substrings for the virtual column
     * and the list of column dependencies
     */
    private String getDependentString(EObject virtualColumn, List columnDependencies, boolean addNullColumn) {
        StringBuffer buffer = new StringBuffer();
        
        // --- Model Resource Name ---------
        int dotIndex = vResource.getResource().getName().indexOf(ModelerCore.MODEL_FILE_EXTENSION);
        buffer.append(getOutputString(vResource.getResource().getName().substring(0, dotIndex)) + delimeter);
        
        //      --- Virtual Table Name ---------
        buffer.append(getOutputString(getName(vGroup)) + delimeter);
        
        //      --- Virtual Column Name ---------  
        SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(virtualColumn);
        buffer.append(getOutputString(columnAspect.getName(virtualColumn)) + delimeter);
        
        //      --- DataType ---------
        String dName = columnAspect.getDatatypeName(virtualColumn);
        String rName = columnAspect.getRuntimeType(virtualColumn);
        int length = columnAspect.getLength(virtualColumn);
        if( dName == null )
            dName = StringUtil.Constants.EMPTY_STRING;
        else if( (isStringType(dName) || isStringType(rName)) && length > 0 ) { 
                dName = dName + "(" + length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        buffer.append(getOutputString(dName)+ delimeter);
        
        //      --- Description ---------
        buffer.append(getOutputString(getDescription(virtualColumn)) + delimeter);
        
        //      --- Searchable ---------
        buffer.append(getOutputString(getSearchableString(columnAspect.getSearchType(virtualColumn))) + delimeter);
        
        //      --- Selectable ---------
        buffer.append(getOutputString(booleanString(columnAspect.isSelectable(virtualColumn))) + delimeter);
        
        //      --- Added Dependent Column Info ---------
        EObject nextColumn = null;
        Iterator iter = columnDependencies.iterator();
        while( iter.hasNext() ) {
            nextColumn = (EObject)iter.next();
            buffer.append((String)columnStrings.get(nextColumn));
        }
        
        // Add NULL Column Info if no Depdendencies
        if( addNullColumn )
            buffer.append(getNullSourceColumnString());
        
        if( columnDependencies.size() > maxDep )
            maxDep = columnDependencies.size();
        
        return buffer.toString();
    }
    
    private String booleanString(boolean value) {
        if (value) {
            return "true"; //$NON-NLS-1$
        }
        return "false"; //$NON-NLS-1$
    }
    private String getDependentString(EObject virtualColumn, EObject lastDepColumn, boolean addNullColumn) {
        StringBuffer buffer = new StringBuffer();

        // --- Model Resource Name ---------
        int dotIndex = vResource.getResource().getName().indexOf(ModelerCore.MODEL_FILE_EXTENSION);
        buffer.append(getOutputString(vResource.getResource().getName().substring(0, dotIndex)) + delimeter);
        
        // --- Virtual Table Name ---------
        buffer.append(getOutputString(getName(vGroup)) + delimeter);
        
        // --- Virtual Column Name ---------
        SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(virtualColumn);
        buffer.append(getOutputString(columnAspect.getName(virtualColumn)) + delimeter);
        
        // --- Datatype ---------
        String dName = columnAspect.getDatatypeName(virtualColumn);
        String rName = columnAspect.getRuntimeType(virtualColumn);
        int length = columnAspect.getLength(virtualColumn);
        if( dName == null )
            dName = StringUtil.Constants.EMPTY_STRING;
        else if( (isStringType(dName) || isStringType(rName)) && length > 0 ) { 
                dName = dName + "(" + length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        buffer.append(getOutputString(dName)+ delimeter);
        
        // --- Description ---------
        String description = getDescription(virtualColumn);
        if( description == null )
            description = StringUtil.Constants.EMPTY_STRING;
        buffer.append(getOutputString(description) + delimeter);
        
        // --- Searchable ---------
        buffer.append(getOutputString(getSearchableString(columnAspect.getSearchType(virtualColumn))) + delimeter);
        
        // --- Selectable ---------
        buffer.append(getOutputString(booleanString(columnAspect.isSelectable(virtualColumn))) + delimeter);

        // --- Add Last Dependent Column ---------
        if (lastDepColumn == null) {
            buffer.append(getNullSourceColumnString());
        } else {
            buffer.append(columnStrings.get(lastDepColumn));
        }
        
        // Add NULL Column Info if no Depdendencies
        if( addNullColumn )
            buffer.append(getNullSourceColumnString());
        
        return buffer.toString();
    }
    
    private String getSourceColumnString(EObject sourceColumn) {
        StringBuffer buffer = new StringBuffer();
        
        // --- Model and Path to Container ---------
        SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(sourceColumn);
        EObject table = sourceColumn.eContainer();
        buffer.append(getOutputString(getPath(table)) + delimeter);
        
        // --- Name ---------
        buffer.append(getOutputString(columnAspect.getName(sourceColumn))+ delimeter);
        
        // --- Name In Source ---------
        String nis = columnAspect.getNameInSource(sourceColumn);
        if( nis == null )
            nis = StringUtil.Constants.EMPTY_STRING;
        buffer.append(getOutputString(nis) + delimeter);
        
        // --- DataType ---------
        String dName = columnAspect.getDatatypeName(sourceColumn);
        String rName = columnAspect.getRuntimeType(sourceColumn);
        int length = columnAspect.getLength(sourceColumn);
        if( dName == null )
            dName = StringUtil.Constants.EMPTY_STRING;
        else if( (isStringType(dName) || isStringType(rName)) && length > 0 ) { 
                dName = dName + "(" + length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        buffer.append(getOutputString(dName)+ delimeter);
        
        // --- Description ---------
        String description = getDescription(sourceColumn);
        if( description == null )
            description = StringUtil.Constants.EMPTY_STRING;
        buffer.append(getOutputString(description) + delimeter);
        
        // --- Searchable ---------
        buffer.append(getOutputString(getSearchableString(columnAspect.getSearchType(sourceColumn))) + delimeter);
       
        // --- Selectable ---------
        buffer.append(getOutputString(booleanString(columnAspect.isSelectable(sourceColumn))) + delimeter);
        
        return buffer.toString();
    }
    
    private String getNullSourceColumnString() {
        // Create this once
        if( nullColumnString == null ) {
            StringBuffer buffer = new StringBuffer();
            
            // --- Model and Path to Container ---------
            buffer.append(NULL_STRING + delimeter);
            
            // --- Name ---------
            buffer.append(NULL_STRING+ delimeter);
            
            // --- Name In Source ---------
            buffer.append(NULL_STRING + delimeter);
            
            // --- DataType ---------
            buffer.append(NULL_STRING+ delimeter);
            
            // --- Description ---------
            buffer.append(NULL_STRING + delimeter);
            
            // --- Selectable ---------
            buffer.append(NULL_STRING + delimeter);
            
            // --- Searchable ---------
            buffer.append(NULL_STRING + delimeter);
            nullColumnString = buffer.toString();
        }
        return nullColumnString;
    }
    
    private String getName(EObject eObject) {
        return ModelerCore.getModelEditor().getName(eObject);
    }
    
    private boolean isStringType(String type) {
        if( type != null ) {
            if( type.equalsIgnoreCase(STRING_STRING) ) {
                return true;
            }
        }
        return false;
    }
    
    private String getDescription(EObject eObject) {
        String description = StringUtil.Constants.EMPTY_STRING;
        
        try {
            description = ModelerCore.getModelEditor().getDescription(eObject);
            if ((description != null) && (description.length() > 0)) {
                description = DOUBLE_QUOTE + description + DOUBLE_QUOTE;
            }
        } catch (ModelerCoreException err) {
            // NO DESCRIPTION.
        }
        return description;
    }
    
    private String getPath(EObject eObject) {
        return ModelerCore.getModelEditor().getModelRelativePathIncludingModel(eObject).toString();
    }
    
    private String getOutputString(String val) {
        if(  nullValue != null ) {
            if( val == null ||
                val.length() == 0 ||
                val.equals(EMPTY_STRING) ) {
                return nullValue;
            }
        }
        
        return val;
    }
    
    private String getHeaderString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("VirtualModel" + delimeter); //$NON-NLS-1$
        buffer.append("VirtualTableName" + delimeter); //$NON-NLS-1$
        buffer.append("VirtualColumnName" + delimeter); //$NON-NLS-1$
        buffer.append("DataType(LENGTH)" + delimeter); //$NON-NLS-1$
        buffer.append("ColumnDescription" + delimeter); //$NON-NLS-1$
        buffer.append("Searchable" + delimeter); //$NON-NLS-1$
        buffer.append("Selectable" + delimeter); //$NON-NLS-1$

        
        for( int i=0; i<maxDep; i++ ) {
            buffer.append("SourceModel/TableName" + delimeter); //$NON-NLS-1$
            buffer.append("SourceColumnName" + delimeter); //$NON-NLS-1$
            buffer.append("ColumnNameInSource" + delimeter); //$NON-NLS-1$
//    EnumeratedValues
            buffer.append("DataType(LENGTH)" + delimeter); //$NON-NLS-1$
            buffer.append("ColumnDescription" + delimeter); //$NON-NLS-1$
            buffer.append("Searchable" + delimeter); //$NON-NLS-1$
            buffer.append("Selectable" + delimeter); //$NON-NLS-1$
        }

        return buffer.toString();
    }
    
    public String getFileString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getHeaderString() + NEWLINE);
        
        Iterator iter = rows.iterator();
        String nextRow = null;
        while( iter.hasNext() ) {
            nextRow = (String)iter.next();
            buffer.append(nextRow + NEWLINE);
        }
        
        return buffer.toString();
    }
    
    private String getSearchableString(int value) {
        switch(value) {
            case SEARCHABLE: return SEARCHABLE_STRING;
            case ALL_EXCEPT_LIKE: return ALL_EXCEPT_LIKE_STRING;
            case LIKE_ONLY: return LIKE_ONLY_STRING;
            case UNSEARCHABLE: return UNSEARCHABLE_STRING;
        }

        return UNKNOWN_STRING;
    }
    /** 
     * @return Returns the delimeter.
     * @since 4.2
     */
    public String getDelimeter() {
        return this.delimeter;
    }
    /** 
     * @param delimeter The delimeter to set.
     * @since 4.2
     */
    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }
}
