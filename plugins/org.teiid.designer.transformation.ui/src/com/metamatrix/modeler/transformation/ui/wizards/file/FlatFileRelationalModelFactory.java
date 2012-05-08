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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;

public class FlatFileRelationalModelFactory implements UiConstants {
    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory factory = RelationalFactory.eINSTANCE;
    public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    
    public static final String GET_FILES = "getFiles"; //$NON-NLS-1$
    public static final String GET_TEXT_FILES = "getTextFiles"; //$NON-NLS-1$
    public static final String SAVE_FILE = "saveFile"; //$NON-NLS-1$
    public static final String ALL_PROCEDURES = "allProcedures"; //$NON-NLS-1$
    
    public static final String INVOKE = "invoke"; //$NON-NLS-1$
    public static final String INVOKE_HTTP = "invokeHttp"; //$NON-NLS-1$
    public static final String ALL_INVOKE_PROCEDURES = "allInvokeProcedures"; //$NON-NLS-1$
    
    public static final int DEFAULT_STRING_LENGTH = 4000;
    
    private static boolean isTransactionable = ModelerCore.getPlugin() != null;
    
    public ModelResource createModel(IPath location, String modelName) throws ModelerCoreException {
    	
    	// Create Model Resource
    	ModelResource mr = createRelationalModel(location, modelName);
    	
    	addGetFilesProcedure(mr);
    	
    	addGetTextFilesProcedure(mr);
    	
    	addSaveFileProcedure(mr);
    	
    	return mr;
    }
    
    public ModelResource createRelationalModel( IPath location, String modelName) throws ModelWorkspaceException {
        ModelWorkspaceItem mwItem = null;
        if( location.segmentCount() == 1 ) {
        	// Project for ONE segment
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.PROJECT);
        } else {
        	mwItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(location.makeAbsolute(), IResource.FOLDER);
        }
        
        IProject project = mwItem.getResource().getProject();
        IPath relativeModelPath = mwItem.getPath().removeFirstSegments(1).append(modelName);
        final IFile modelFile = project.getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
        resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
        resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
        ModelUtilities.initializeModelContainers(resrc, "Create Model Containers", this); //$NON-NLS-1$ 
        
        return resrc;
    }
    
    public boolean addMissingProcedure(ModelResource modelResource, String specificProcedure) throws ModelerCoreException{
    	if( modelResource != null ) {
    		if( specificProcedure.equalsIgnoreCase(ALL_PROCEDURES)) {
	    		if( !FlatFileRelationalModelFactory.procedureExists(modelResource, GET_FILES ) ) {
	    			addGetFilesProcedure(modelResource);
	    		}
	    		if( !FlatFileRelationalModelFactory.procedureExists(modelResource, GET_TEXT_FILES ) ) {
	    			addGetTextFilesProcedure(modelResource);
	    		}
	    		if( !FlatFileRelationalModelFactory.procedureExists(modelResource, SAVE_FILE ) ) {
	    			addSaveFileProcedure(modelResource);
	    		}
    		} else if( specificProcedure.equalsIgnoreCase(GET_FILES) && !FlatFileRelationalModelFactory.procedureExists(modelResource, GET_FILES ) ) {
	    		addGetFilesProcedure(modelResource);
    		} else if( specificProcedure.equalsIgnoreCase(GET_TEXT_FILES) && !FlatFileRelationalModelFactory.procedureExists(modelResource, GET_TEXT_FILES ) ) {
	    		addGetTextFilesProcedure(modelResource);
    		} else if( specificProcedure.equalsIgnoreCase(SAVE_FILE) && !FlatFileRelationalModelFactory.procedureExists(modelResource, SAVE_FILE ) ) {
	    		addSaveFileProcedure(modelResource);
    		} else if( specificProcedure.equalsIgnoreCase(ALL_INVOKE_PROCEDURES)) {
	    		if( !FlatFileRelationalModelFactory.procedureExists(modelResource, INVOKE ) ) {
	    			addInvokeProcedure(modelResource);
	    		}
	    		if( !FlatFileRelationalModelFactory.procedureExists(modelResource, INVOKE_HTTP ) ) {
	    			addInvokeHttpProcedure(modelResource);
	    		}
    		} else if( specificProcedure.equalsIgnoreCase(INVOKE) && !FlatFileRelationalModelFactory.procedureExists(modelResource, INVOKE ) ) {
    			addInvokeProcedure(modelResource);
    		} else if( specificProcedure.equalsIgnoreCase(INVOKE_HTTP) && !FlatFileRelationalModelFactory.procedureExists(modelResource, INVOKE_HTTP ) ) {
    			addInvokeHttpProcedure(modelResource);
    		} 
    	}
    	
    	return false;
    }
    
    public static boolean procedureExists(ModelResource modelResource, String procedureName) {
    	if( modelResource != null ) {
    		try {
    			for( Object obj : modelResource.getAllRootEObjects() ) {

                    EObject eObj = (EObject)obj;
                    if (eObj instanceof Procedure  && procedureName.equalsIgnoreCase(ModelObjectUtilities.getName(eObj)) ) {
                        return true;
                    }
                }
            } catch (ModelWorkspaceException err) {
                Util.log(err);
            }
    	}
    	
    	return false;
    }
    
	
	private void addGetFilesProcedure(ModelResource mr) throws ModelerCoreException {

		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject blobType = datatypeManager.findDatatype("blob"); //$NON-NLS-1$
		
    	Procedure proc = factory.createProcedure();
    	proc.setName(GET_FILES);
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setName("pathAndExt"); //$NON-NLS-1$
    	param.setProcedure(proc);
    	if( stringType != null) {
    		param.setType(stringType);
    	}
    	
    	ProcedureResult result = factory.createProcedureResult();
    	result.setName("Result"); //$NON-NLS-1$
    	result.setProcedure(proc);
    	Column column_1 = factory.createColumn();
    	column_1.setName("file"); //$NON-NLS-1$
    	if( blobType != null) {
    		column_1.setType(blobType);
    	}
    	addValue(result, column_1, result.getColumns());
    	Column column_2 = factory.createColumn();
    	column_2.setName("filePath"); //$NON-NLS-1$
    	if( stringType != null) {
    		column_2.setType(stringType);
    		column_2.setLength(DEFAULT_STRING_LENGTH);
    	}
    	addValue(result, column_2, result.getColumns());
    	
    	addValue(mr, proc, getModelResourceContents(mr));

	}
	
	private void addGetTextFilesProcedure(ModelResource mr) throws ModelerCoreException {
		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject clobType = datatypeManager.findDatatype("clob"); //$NON-NLS-1$
		
    	Procedure proc = factory.createProcedure();
    	proc.setName(GET_TEXT_FILES);
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("pathAndExt"); //$NON-NLS-1$
    	param.setProcedure(proc);
    	if( stringType != null) {
    		param.setType(stringType);
    		param.setLength(DEFAULT_STRING_LENGTH);
    	}
    	
    	ProcedureResult result = factory.createProcedureResult();
    	result.setName("Result"); //$NON-NLS-1$
    	result.setProcedure(proc);
    	Column column_1 = factory.createColumn();
    	column_1.setName("file"); //$NON-NLS-1$
    	if( clobType != null) {
    		column_1.setType(clobType);
    	}
    	addValue(result, column_1, result.getColumns());
    	Column column_2 = factory.createColumn();
    	column_2.setName("filePath"); //$NON-NLS-1$
    	if( stringType != null) {
    		column_2.setType(stringType);
    		column_2.setLength(DEFAULT_STRING_LENGTH);
    	}
    	addValue(result, column_2, result.getColumns());
    	
    	addValue(mr, proc, getModelResourceContents(mr));

	}
	
	
	private void addSaveFileProcedure(ModelResource mr) throws ModelerCoreException {
		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject objectType = datatypeManager.findDatatype("object"); //$NON-NLS-1$
		
		Procedure proc = factory.createProcedure();
    	proc.setName(SAVE_FILE);
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("filePath"); //$NON-NLS-1$
    	param.setProcedure(proc);
    	if( stringType != null) {
    		param.setType(stringType);
    		param.setLength(DEFAULT_STRING_LENGTH);
    	}
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("value"); //$NON-NLS-1$
    	param.setProcedure(proc);
    	if( objectType != null) {
    		param.setType(objectType);
    	}

    	addValue(mr, proc, getModelResourceContents(mr));
	}
	
    protected void addValue(final Object owner, final Object value, EList feature) throws ModelerCoreException {
        if( isTransactionable ) {
            ModelerCore.getModelEditor().addValue(owner, value, feature);
        } else {
            feature.add(value);
        }
    }
	
    protected EList getModelResourceContents(ModelResource resource ) throws ModelWorkspaceException {
    	return resource.getEmfResource().getContents();
    }
    
	private void addInvokeHttpProcedure(ModelResource mr) throws ModelerCoreException {
		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject blobType = datatypeManager.findDatatype("blob"); //$NON-NLS-1$
		EObject objectType = datatypeManager.findDatatype("object"); //$NON-NLS-1$
		
    	Procedure proc = factory.createProcedure();
    	proc.setName("invokeHttp"); //$NON-NLS-1$
    	
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("action"); //$NON-NLS-1$
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( stringType != null) {
    		param.setType(stringType);
    		param.setLength(DEFAULT_STRING_LENGTH);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("request"); //$NON-NLS-1$
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( objectType != null) {
    		param.setType(objectType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("endpoint"); //$NON-NLS-1$
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( objectType != null) {
    		param.setType(stringType);
    		param.setLength(DEFAULT_STRING_LENGTH);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("result"); //$NON-NLS-1$
    	param.setProcedure(proc);
    	param.setDirection(DirectionKind.OUT_LITERAL);
    	if( blobType != null) {
    		param.setType(blobType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("contentType"); //$NON-NLS-1$
    	param.setDirection(DirectionKind.OUT_LITERAL);
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( stringType != null) {
    		param.setType(stringType);
    		param.setLength(DEFAULT_STRING_LENGTH);
    	}
    	
    	addValue(mr, proc, getModelResourceContents(mr));
	}
	
	private void addInvokeProcedure(ModelResource mr) throws ModelerCoreException {
		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject xmlLiteralType = datatypeManager.findDatatype("XMLLiteral"); //$NON-NLS-1$
		
    	Procedure proc = factory.createProcedure();
    	proc.setName("invoke"); //$NON-NLS-1$
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("binding"); //$NON-NLS-1$
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( stringType != null) {
    		param.setType(stringType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("action"); //$NON-NLS-1$
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( stringType != null) {
    		param.setType(stringType);
    		param.setLength(DEFAULT_STRING_LENGTH);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("request"); //$NON-NLS-1$
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( xmlLiteralType != null) {
    		param.setType(xmlLiteralType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("endpoint"); //$NON-NLS-1$
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( stringType != null) {
    		param.setType(stringType);
    		param.setLength(DEFAULT_STRING_LENGTH);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("result"); //$NON-NLS-1$
    	param.setProcedure(proc);
    	param.setDirection(DirectionKind.OUT_LITERAL);
    	param.setNullable(NullableType.NULLABLE_LITERAL);
    	if( xmlLiteralType != null) {
    		param.setType(xmlLiteralType);
    	}
    	
    	addValue(mr, proc, getModelResourceContents(mr));
	}
}
