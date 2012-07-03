/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.xmlfile;

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
import com.metamatrix.modeler.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;

public class XmlFileViewModelFactory  extends FlatFileRelationalModelFactory {
    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory relationalFactory = RelationalFactory.eINSTANCE;
    public static final TransformationFactory transformationFactory = TransformationFactory.eINSTANCE;
    public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    
    public static final char DOT = '.';
    public static final char COMMA = ',';
    public static final char SPACE = ' ';
    public static final char S_QUOTE = '\'';
    public static final String HEADER = "HEADER"; //$NON-NLS-1$
    public static final String SKIP = "SKIP"; //$NON-NLS-1$
    public static final String WIDTH = "width"; //$NON-NLS-1$
    
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
    
    public void createViewTable(ModelResource modelResource, TeiidXmlFileInfo info, String relationalModelName) throws ModelerCoreException {
    	
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
    
    @SuppressWarnings("unchecked")
    private void createColumns(TeiidXmlFileInfo info, BaseTable baseTable) throws ModelerCoreException {
    	EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
    	
    	for (TeiidXmlColumnInfo columnInfo : info.getColumnInfoList()) {
    		Column column = factory.createColumn();
    		column.setName(columnInfo.getName());
    		column.setNameInSource(columnInfo.getSymbolName());
    		column.setLength(columnInfo.getWidth());
    		column.setDefaultValue(columnInfo.getDefaultValue());
    		
    		EObject datatype = datatypeManager.findDatatype(columnInfo.getDatatype());
    		if (datatype != null) {
    			column.setType(datatype);
    			if( stringType != null && stringType == datatype) {
    				column.setLength(DEFAULT_STRING_LENGTH);
    			}
    		}
    		
    		baseTable.getColumns().add(column);
    	}
    }
}

