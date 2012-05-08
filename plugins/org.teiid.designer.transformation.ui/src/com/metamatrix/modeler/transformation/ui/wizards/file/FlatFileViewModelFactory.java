/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.NewModelObjectHelperManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;

public class FlatFileViewModelFactory extends FlatFileRelationalModelFactory {
    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory relationalFactory = RelationalFactory.eINSTANCE;
    public static final TransformationFactory transformationFactory = TransformationFactory.eINSTANCE;
    public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    
    public ModelResource createViewRelationalModel( IPath location, String modelName) throws ModelWorkspaceException {
        ModelWorkspaceItem mwItem = null;

        // One Segment -- Project
        if( location.segmentCount() == 1 ) {
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.PROJECT);
        } else {
            // Multiple Segments -- Folder
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.FOLDER);
        }
        
        // Get the Project
        IProject project = mwItem.getResource().getProject();

        // Create the model at the specified relative path
        IPath relativeModelPath = mwItem.getPath().removeFirstSegments(1).append(modelName);
        final IFile modelFile = project.getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
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
    private void createColumns(TeiidMetadataFileInfo info, BaseTable baseTable) throws ModelerCoreException {
    	EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
    	
    	for (TeiidColumnInfo columnInfo : info.getColumnInfoList()) {
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
