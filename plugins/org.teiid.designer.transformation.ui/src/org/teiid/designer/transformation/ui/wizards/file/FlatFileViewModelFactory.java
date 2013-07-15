/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.file;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.util.NewModelObjectHelperManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationFactory;
import org.teiid.designer.query.proc.ITeiidColumnInfo;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationMappingHelper;
import org.teiid.designer.transformation.validation.TransformationValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class FlatFileViewModelFactory extends FlatFileRelationalModelFactory {
    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory relationalFactory = RelationalFactory.eINSTANCE;
    public static final TransformationFactory transformationFactory = TransformationFactory.eINSTANCE;
    public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    
    public ModelResource createViewRelationalModel( IPath location, String modelName) throws ModelWorkspaceException {
    	final ModelResource resrc = ModelerCore.createModelResource(location, modelName);
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
        ModelUtilities.initializeModelContainers(resrc, "Create Model Containers", this); //$NON-NLS-1$ 

        return resrc;
    }
    
    /**
     * Create columns in the {@link BaseTable} based on the column 
     * metadata contained in the given {@link TeiidMetadataFileInfo} object.
     *  
     * @param info
     * @param baseTable
     * @throws ModelerCoreException
     */
    @SuppressWarnings("unchecked")
    private void createColumns(ITeiidMetadataFileInfo info, BaseTable baseTable) throws ModelerCoreException {
    	EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
    	
    	for (ITeiidColumnInfo columnInfo : info.getColumnInfoList()) {
    		Column column = factory.createColumn();
    		column.setName(columnInfo.getName());
    		column.setNameInSource(columnInfo.getSymbolName());
    		column.setLength(columnInfo.getWidth());
    		column.setDefaultValue(columnInfo.getDefaultValue());
    		column.setFixedLength(info.isFixedWidthColumns());
    		
    		EObject datatype = datatypeManager.findDatatype(columnInfo.getDatatype());
    		if (datatype != null) {
    			column.setType(datatype);
    			if( stringType != null && stringType == datatype) {
    				if( info.isFixedWidthColumns()) {
    					column.setLength(columnInfo.getWidth());
    				} else {
    					column.setLength(DEFAULT_STRING_LENGTH);
    				}
    			}
    		}
    		
    		baseTable.getColumns().add(column);
    	}
    }
    
    public void createViewTable(ModelResource modelResource, TeiidMetadataFileInfo info, String relationalModelName) throws ModelerCoreException {
    	
    	// Create a Procedure using the text file name
    	BaseTable table = factory.createBaseTable();
    	table.setName(info.getViewTableName());
    	
    	/* 
    	  * Creating the columns here ensures that any fixed lengths are assigned
    	  *  to the columns, otherwise reconcileMappingsOnSqlChange() (below)
    	  *  sets their lengths to default values.
    	  */
    	createColumns(info, table);
    	
    	addValue(modelResource, table, getModelResourceContents(modelResource));
    	
    	NewModelObjectHelperManager.helpCreate(table, null);
    	String sqlString = info.getSqlString(relationalModelName);
    	
    	SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(table);
    	
    	TransformationHelper.setSelectSqlString(tRoot, sqlString, false, this);

        TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, this);
        
        QueryValidator validator = new TransformationValidator(tRoot);
        
        validator.validateSql(sqlString, QueryValidator.SELECT_TRNS, true);
    	
    }
    
}
