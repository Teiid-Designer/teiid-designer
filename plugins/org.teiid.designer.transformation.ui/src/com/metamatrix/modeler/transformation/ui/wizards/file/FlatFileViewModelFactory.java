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

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
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
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;

public class FlatFileViewModelFactory extends FlatFileRelationalModelFactory {
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
        if( location.segmentCount() == 1 ) {
        	// Project for ONE segment
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.PROJECT);
        } else {
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.FOLDER);
        }
        
        IProject project = mwItem.getResource().getProject();
        IPath relativeModelPath = project.getProjectRelativePath().append(modelName);
        final IFile modelFile = project.getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
        ModelUtilities.initializeModelContainers(resrc, "Create Model Containers", this); //$NON-NLS-1$ 
    	if( resrc !=null ) {
    		resrc.save(null, true);
    	}

        return resrc;
    }
    
    public void createViewTable(ModelResource modelResource, TeiidMetadataFileInfo info, String relationalModelName) throws ModelerCoreException {
    	
    	// Create a Procedure using the text file name
    	BaseTable table = factory.createBaseTable();
    	table.setName(info.getViewTableName());
    	
    	addValue(modelResource, table, getModelResourceContents(modelResource));
    	
    	NewModelObjectHelperManager.helpCreate(table, null);
    	String sqlString = getTextFileString(info, relationalModelName);
    	
    	SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(table);
    	
    	TransformationHelper.setSelectSqlString(tRoot, sqlString, false, this);

        TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, this);
        
        QueryValidator validator = new TransformationValidator(tRoot);
        
        validator.validateSql(sqlString, QueryValidator.SELECT_TRNS, true);
    	
    }
    
    private String getTextFileString(TeiidMetadataFileInfo info, String relationalModelName) {
    	/*
    	 * 
    	 * TEXTTABLE(expression COLUMNS <COLUMN>, ... [DELIMITER char] [(QUOTE|ESCAPE) char] [HEADER [integer]] [SKIP integer]) AS name
    	 * 
    	 * DELIMITER sets the field delimiter character to use. Defaults to ','.
    	 * 
    	 * QUOTE sets the quote, or qualifier, character used to wrap field values. Defaults to '"'.
    	 * 
    	 * ESCAPE sets the escape character to use if no quoting character is in use. This is used in situations where the delimiter or new line characters are escaped with a preceding character, e.g. \
    	 * 
    	 * 
			SELECT A.lastName, A.firstName, A.middleName, A.AId FROM
        (EXEC EmployeeData.getTextFiles('EmployeeData.txt')) AS f, TEXTTABLE(file COLUMNS lastName string, firstName string, middleName string, HEADER 3) AS A
    	 
    	 *
    	 * SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, TEXTTABLE(file COLUMNS {3}  HEADER {4}) AS {5}
    	 */
    	String alias = "A"; //$NON-NLS-1$
    	StringBuffer sb = new StringBuffer();
    	int i=0;
    	int nColumns = info.getColumnInfoList().length;
    	for( TeiidColumnInfo columnStr : info.getColumnInfoList()) {
    		sb.append(alias).append(DOT).append(columnStr.getName());
    		
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}
    		i++;
    	}
    	String string_0 = sb.toString();
    	
    	sb = new StringBuffer();
    	i=0;
    	for( TeiidColumnInfo columnStr : info.getColumnInfoList()) {
    		sb.append(columnStr.getName()).append(SPACE).append(columnStr.getDatatype());
			if( info.isFixedWidthColumns()) {
				sb.append(SPACE).append(WIDTH).append(SPACE).append(Integer.toString(columnStr.getWidth()));
			}
    		if(i < (nColumns-1)) {
    			sb.append(COMMA).append(SPACE);
    		}

    		i++;
    	}
    	String string_2 = S_QUOTE + info.getDataFile().getName() + S_QUOTE;
    	String string_3 = sb.toString();
    	
    	sb = new StringBuffer();
    	
    	if( info.doUseDelimitedColumns() && info.getDelimiter() != TeiidMetadataFileInfo.DEFAULT_DELIMITER ) {
    		sb.append("DELIMITER"); //$NON-NLS-1$
    		sb.append(SPACE).append('\'').append(info.getDelimiter()).append('\'');
    	}
    	
    	if( info.doIncludeQuote() ) {
    		if( info.getQuote() != TeiidMetadataFileInfo.DEFAULT_QUOTE) {
	    		sb.append("QUOTE"); //$NON-NLS-1$
	    		sb.append(SPACE).append('\'').append(info.getQuote()).append('\'');
    		}
    	} else if(info.doIncludeEscape() ) {
    		if( info.getEscape() != TeiidMetadataFileInfo.DEFAULT_ESCAPE) {
	    		sb.append("ESCAPE"); //$NON-NLS-1$
	    		sb.append(SPACE).append('\'').append(info.getQuote()).append('\'');
    		}
    	}
    	
    	if( info.doIncludeHeader() ) {
    		sb.append(SPACE).append("HEADER"); //$NON-NLS-1$
    		if( info.getHeaderLineNumber() > 1 ) {
    			sb.append(SPACE).append(Integer.toString(info.getHeaderLineNumber()));
    		}
    	}
    	if( info.doIncludeSkip() && info.getFirstDataRow() > 1 ) {
    		sb.append("SKIP"); //$NON-NLS-1$
    		sb.append(SPACE).append(Integer.toString(info.getFirstDataRow()-1));
    	}
    	String string_4 = sb.toString();
    	
    	String finalSQLString = UiPlugin.Util.getString(
    			"FlatFileViewModelFactory.textTableSqlTemplate", //$NON-NLS-1$
    			string_0,
    			relationalModelName,
    			string_2,
    			string_3,
    			string_4,
    			alias);
    	
    	return finalSQLString;
    	
    }
    
}
