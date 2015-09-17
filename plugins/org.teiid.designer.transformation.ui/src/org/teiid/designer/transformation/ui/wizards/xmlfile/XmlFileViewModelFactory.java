/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.wizards.xmlfile;


import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.datatools.connectivity.model.Parameter;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.util.NewModelObjectHelperManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationFactory;
import org.teiid.designer.query.proc.ITeiidXmlColumnInfo;
import org.teiid.designer.transformation.ui.wizards.file.FlatFileRelationalModelFactory;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationMappingHelper;
import org.teiid.designer.transformation.validation.TransformationValidator;
import org.teiid.designer.ui.viewsupport.DatatypeUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
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
    	final ModelResource resrc = ModelerCore.createModelResource(location, modelName);
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
    
 /**
 * @since 8.6
 */
public void createViewProcedure(ModelResource modelResource, TeiidXmlFileInfo info, String relationalModelName) throws ModelerCoreException {
    	
	// Create a Procedure using the view procedure name in TeiidXmlFileInfo
 	Procedure procedure = factory.createProcedure();
 	procedure.setName(info.getViewProcedureName());
 	
  	for (String parameterKey : info.getParameterMap().keySet()) {
  		//Don't add header parameters. These are just needed for building the transformation
  		//and adding them will cause Data Preview to prompt for values.
  		Object value = info.getParameterMap().get(parameterKey);
  		if ((value instanceof Parameter) &&
  		   ((Parameter)value).getType().equals(Parameter.Type.Header)){
  				continue;
  		}
 		ProcedureParameter parameter = factory.createProcedureParameter();
 		parameter.setName(parameterKey);
 		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
 		parameter.setType(stringType);
 		parameter.setProcedure(procedure);
 	}
	
 	ProcedureResult result = factory.createProcedureResult();
 	result.setName("Result"); //$NON-NLS-1$
 	result.setProcedure(procedure);
 	
 	for(TeiidXmlColumnInfo columnInfo : info.getColumnInfoList() ) {
     	Column column = factory.createColumn();
     	column.setName(columnInfo.getName());
     	EObject type = datatypeManager.findDatatype(columnInfo.getDatatype());
     	if( type != null) {
     		column.setType(type);
     		if( columnInfo.getDatatype().equalsIgnoreCase("string")) { //$NON-NLS-1$
     			column.setLength(DEFAULT_STRING_LENGTH);
     		}
     	}
     	addValue(result, column, result.getColumns());
 	}
 	
	addValue(modelResource, procedure, modelResource.getEmfResource().getContents());
    
 	NewModelObjectHelperManager.helpCreate(procedure, null);
 	String sqlString = info.getSqlString(relationalModelName, info.getModelNameWithoutExtension(modelResource.getItemName()), info.getViewProcedureName());
 	
 	SqlTransformationMappingRoot tRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(procedure);
 	
 	TransformationHelper.setSelectSqlString(tRoot, sqlString, false, this);

     TransformationMappingHelper.reconcileMappingsOnSqlChange(tRoot, this);
     
     QueryValidator validator = new TransformationValidator(tRoot);
     
     validator.validateSql(sqlString, QueryValidator.SELECT_TRNS, true);
    	
    }

 
    
    @SuppressWarnings("unchecked")
    private void createColumns(TeiidXmlFileInfo info, BaseTable baseTable) throws ModelerCoreException {
    	EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
    	
    	for (ITeiidXmlColumnInfo columnInfo : info.getColumnInfoList()) {
    		Column column = factory.createColumn();
    		column.setName(columnInfo.getName());
    		column.setNameInSource(columnInfo.getSymbolName());
    		column.setLength(columnInfo.getWidth());
    		column.setDefaultValue(columnInfo.getDefaultValue());
    		
    		String finalDType = columnInfo.getDatatype();
    		if( INTEGER_STR.equalsIgnoreCase(finalDType) ) {
    			finalDType = INT_STR;
    		}
    		
    		EObject datatype = datatypeManager.findDatatype(finalDType);
    		if (datatype != null) {
    			column.setType(datatype);
    			if( stringType != null && stringType == datatype) {
    				column.setLength(DEFAULT_STRING_LENGTH);
    			} else if( DatatypeUtilities.isNumeric(finalDType)) {
					column.setPrecision(DEFAULT_PRECISION);
				}
    		}
    		
    		baseTable.getColumns().add(column);
    	}
    }
}

