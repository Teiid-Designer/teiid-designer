/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.metamodels.function.FunctionParameter;
import org.teiid.designer.metamodels.function.FunctionPlugin;
import org.teiid.designer.metamodels.function.ReturnParameter;
import org.teiid.designer.metamodels.function.ScalarFunction;
import org.teiid.designer.metamodels.function.extension.FunctionModelExtensionConstants;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalViewProcedure;
import org.teiid.designer.transformation.model.RelationalViewModelFactory;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;

public class ConvertFunctionModelHelper {
	private static String UDF_JAR_PROP_KEY = "udfJarPath"; //$NON-NLS-1$
	private static String RETURN_PARAM_STR = "return_parameter"; //$NON-NLS-1$
	private static Status OK_TO_FINISH_STATUS = 
			new Status(IStatus.OK, UiConstants.PLUGIN_ID,
					UiConstants.Util.getString("ConvertFunctionModelHelper.okToFinishMessage"));  //$NON-NLS-1$
	
	private ModelResource functionModel;
	private List<ScalarFunction> scalarFunctions;
	private RelationalViewProcedure[] virtualFunctions;
	private Set<RelationalViewProcedure> selectedProcedures;
	
	private ModelResource targetModel;
	
	private IStatus currentStatus;

	public ConvertFunctionModelHelper(ModelResource functionModel) {
		this();
		
		this.functionModel = functionModel;

		loadFunctions();
		validate();
	}
	
	public ConvertFunctionModelHelper() {
		this.scalarFunctions = new ArrayList<ScalarFunction>();
		selectedProcedures = new HashSet<RelationalViewProcedure>();
	}

	public ModelResource getFunctionModel() {
		return functionModel;
	}

	public List<ScalarFunction> getScalarFunctions() {
		return scalarFunctions;
	}

	public RelationalViewProcedure[] getVirtualFunctions() {
		return virtualFunctions;
	}
	
	public ModelResource getTargetModel() {
		return targetModel;
	}

	public void setTargetModel(ModelResource targetModel) {
		this.targetModel = targetModel;
		validate();
	}

	private void loadFunctions() {
		// get scalar functions
		
		try {
			@SuppressWarnings("unchecked")
			List<EObject> modelChildren = functionModel.getAllRootEObjects();
			scalarFunctions = new ArrayList<ScalarFunction>();
			for( EObject child : modelChildren ) {
				if( child instanceof ScalarFunction ) {
					scalarFunctions.add((ScalarFunction)child);
				}
			}
		} catch (ModelWorkspaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<RelationalViewProcedure> viewProcedures = new ArrayList<RelationalViewProcedure>();
		
		for( ScalarFunction sf : scalarFunctions ) {
			RelationalViewProcedure relationalProcedure = new RelationalViewProcedure(sf.getName());
	        relationalProcedure.setFunction(true);
	        relationalProcedure.setFunctionCategory(sf.getCategory());
	        relationalProcedure.setJavaClassName(sf.getInvocationClass());
	        relationalProcedure.setJavaMethodName(sf.getInvocationMethod());
	        String jarPathPropId = ModelExtensionPropertyDefinition.Utils.getPropertyId(FunctionModelExtensionConstants.NAMESPACE_PROVIDER, UDF_JAR_PROP_KEY);
	        String jarPath = FunctionPlugin.getExtensionProperty(sf, jarPathPropId);
	        relationalProcedure.setUdfJarPath(jarPath);
	        
	        // look for description/annotation
	        String desc = getDescription(sf);
	        if( desc !=  null ) {
	        	relationalProcedure.setDescription(desc);
	        }
	        
	        for (Object param : sf.getInputParameters() ) {
	        	FunctionParameter fp = (FunctionParameter)param;
	        	RelationalParameter udfParam = new RelationalParameter(fp.getName());
	        	udfParam.setDirection(RelationalConstants.DIRECTION.IN);
	        	udfParam.setDatatype(fp.getType());
	        	
	        	desc = getDescription(fp);
		        if( desc !=  null ) {
		        	udfParam.setDescription(desc);
		        }
	        	
	        	relationalProcedure.addParameter(udfParam);
	        }
	        
	        ReturnParameter returnParam = sf.getReturnParameter();
	        if( returnParam != null ) {
		        RelationalParameter param =  new RelationalParameter(RETURN_PARAM_STR); //$NON-NLS-1$
		        param.setDatatype(returnParam.getType()); 
		        param.setDirection(RelationalConstants.DIRECTION.RETURN);
		        
	        	desc = getDescription(returnParam);
		        if( desc !=  null ) {
		        	param.setDescription(desc);
		        }
		        
		        relationalProcedure.addParameter(param);
	        }
	        
	        viewProcedures.add(relationalProcedure);
		}
		
		virtualFunctions = viewProcedures.toArray( new RelationalViewProcedure[viewProcedures.size()]);
	}
	
	public void selectFunction(RelationalViewProcedure procedure) {
		// Add to hash set
		
		selectedProcedures.add(procedure);
		validate();
	}
	
	public void deselectFunction(RelationalViewProcedure procedure) {
		// Add to hash set
		
		selectedProcedures.remove(procedure);
		validate();
	}
	
	public void clearFunctions() {
		selectedProcedures.clear();
		validate();
	}
	
	
	public IStatus generateProcedures() {
		IStatus status = Status.OK_STATUS;

		
        status = createProceduresInTxn();
		
		return status;
	}
	
    private IStatus createProceduresInTxn() {

        RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
        
        for( RelationalViewProcedure proc : selectedProcedures) {
        	relModel.addChild(proc);
        }
        
        boolean requiredStart = ModelerCore.startTxn(true, true, "Create Virtual Procedures From Legacy Functions", this);  //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)targetModel.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalViewModelFactory factory = new RelationalViewModelFactory();
                
                factory.setAllowsZeroStringLength(true);
                
                factory.build(targetModel, relModel, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
        	MessageDialog.openError(Display.getCurrent().getActiveShell(), 
        			UiConstants.Util.getString("ConvertFunctionModelHelper.buildErrorTitle"), e.getMessage());  //$NON-NLS-1$
            IStatus errorStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 
            		UiConstants.Util.getString("ConvertFunctionModelHelper.buildErrorMessage"), e);  //$NON-NLS-1$
            UiConstants.Util.log(errorStatus);

            return errorStatus;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        
        return Status.OK_STATUS;
    }
    
    private void validate() {
    	// Check to see if
    	
    	// 1) One ore more functions exist in the function model
    	
    	if( scalarFunctions.isEmpty() ) {
    		currentStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 
    				UiConstants.Util.getString("ConvertFunctionModelHelper.noFunctionsMessage"));  //$NON-NLS-1$
    		return;
    	}
    	// 2) Target model is defined and exists
    	if( targetModel == null ) {
    		currentStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 
    				UiConstants.Util.getString("ConvertFunctionModelHelper.targetModelUndefined"));  //$NON-NLS-1$
    		return;
    	}
    	
    	// 3) one or more procedures are selected for export
    	if( selectedProcedures.isEmpty() ) {
    		currentStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 
    				UiConstants.Util.getString("ConvertFunctionModelHelper.noFunctionsSelected")); //$NON-NLS-1$
    		return;
    	}
    	
    	currentStatus = OK_TO_FINISH_STATUS;
    	
    }
    
    public IStatus getStatus() {
    	return currentStatus;
    }
    
    private String getDescription(EObject eObj) {
    	String desc = null;
    	
    	try {
			desc = ModelerCore.getModelEditor().getDescription(eObj);
		} catch (ModelerCoreException e) {
			UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
		}
    	
    	return desc;
    }
	
}
