package com.metamatrix.modeler.relational.ui.webservice;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;

import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor;

public class WebServiceRelationalModelContributor implements INewModelWizardContributor {


    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory factory = RelationalFactory.eINSTANCE;
    public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    
    private static boolean isTransactionable = ModelerCore.getPlugin() != null;
    //
    // Instance variables:
    //
    private IWizardPage[] pages;
    private WebServiceRelationalModelWizardPage webServiceProcedures;
    /**
     * Construct an instance of WebServiceRelationalModelContributor.
     */
    public WebServiceRelationalModelContributor() {
    	super();
    }
    
	@Override
	public boolean canFinishEarly(IWizardPage theCurrentPage) {
		return false;
	}

	@Override
	public void createWizardPages(ISelection selection,
			IResource targetResource, IPath targetFilePath,
			MetamodelDescriptor descriptor, boolean isVirtual) {
        pages = new IWizardPage[1];
        webServiceProcedures = new WebServiceRelationalModelWizardPage(
				"webServiceProceduresPage"); //$NON-NLS-1$
        pages[0] = webServiceProcedures;

	}
	

	@Override
	public void currentPageChanged(IWizardPage page) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFinish(ModelResource modelResource, IProgressMonitor monitor) {
		String transactionName = UiConstants.Util.getString("WebServiceRelationalModelContributor.transactionName"); //$NON-NLS-1$
		boolean started = ModelerCore.startTxn(transactionName, this);
        boolean succeeded = false;
		try {

			if( webServiceProcedures.doGenerateInvoke() ) {
				addInvokeProcedure(modelResource);
			}
			
			if( webServiceProcedures.doGenerateInvokeHttp() ) {
				addInvokeHttpProcedure(modelResource);
			}
	    	
            succeeded = true;
        } catch (Exception ex) {
            String message = UiConstants.Util.getString("WebServiceRelationalModelContributor.doFinishError",     //$NON-NLS-1$
                                                      modelResource.getItemName()); 
            UiConstants.Util.log(IStatus.ERROR, ex, message); 
		} finally {
			if (started) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
			}
		}

	}
	
	private void addInvokeProcedure(ModelResource mr) throws ModelerCoreException {
		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject xmlLiteralType = datatypeManager.findDatatype("XMLLiteral"); //$NON-NLS-1$
		
    	Procedure proc = factory.createProcedure();
    	proc.setName("invoke"); //$NON-NLS-1$
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("binding"); //$NON-NLS-1$
    	if( stringType != null) {
    		param.setType(stringType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("action"); //$NON-NLS-1$
    	if( stringType != null) {
    		param.setType(stringType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("request"); //$NON-NLS-1$
    	if( xmlLiteralType != null) {
    		param.setType(xmlLiteralType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("endpoint"); //$NON-NLS-1$
    	if( stringType != null) {
    		param.setType(stringType);
    	}
    	
    	ProcedureResult result = factory.createProcedureResult();
    	result.setProcedure(proc);
    	result.setName("result"); //$NON-NLS-1$
    	Column column = factory.createColumn();
    	column.setName("result"); //$NON-NLS-1$
    	column.setOwner(result);
    	if( xmlLiteralType != null) {
    		column.setType(xmlLiteralType);
    	}
    	
    	addValue(mr, proc, getModelResourceContents(mr));
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
    	if( stringType != null) {
    		param.setType(stringType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("request"); //$NON-NLS-1$
    	if( objectType != null) {
    		param.setType(objectType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("endpoint"); //$NON-NLS-1$
    	if( objectType != null) {
    		param.setType(stringType);
    	}
    	
    	ProcedureResult result = factory.createProcedureResult();
    	result.setProcedure(proc);
    	result.setName("result"); //$NON-NLS-1$
    	Column column = factory.createColumn();
    	column.setName("result"); //$NON-NLS-1$
    	column.setOwner(result);
    	if( blobType != null) {
    		column.setType(blobType);
    	}
    	
    	param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("contentType"); //$NON-NLS-1$
    	if( stringType != null) {
    		param.setType(stringType);
    	}
    	
    	addValue(mr, proc, getModelResourceContents(mr));
	}
	
    private void addValue(final Object owner, final Object value, EList feature) {
        try {
            if( isTransactionable ) {
                ModelerCore.getModelEditor().addValue(owner, value, feature);
            } else {
                feature.add(value);
            }
        } catch (ModelerCoreException err) {
            UiConstants.Util.log(IStatus.ERROR, err, 
            		UiConstants.Util.getString("FlatFileRelationalModelContributor.addValueError", value, owner)); //$NON-NLS-1$
        }
    }
	
    private EList getModelResourceContents(ModelResource resource ) {
    	EList eList = null;
    	
    	try {
			eList = resource.getEmfResource().getContents();
		} catch (ModelWorkspaceException e) {
			 UiConstants.Util.log(IStatus.ERROR, e, 
					 UiConstants.Util.getString("FlatFileRelationalModelContributor.getModelContentsError", resource.getItemName())); //$NON-NLS-1$
		}
		
		return eList;
    }

	@Override
	public IWizardPage[] getWizardPages() {
		 return pages;
	}

	@Override
	public void inputChanged(ISelection selection, IResource targetResource,
			MetamodelDescriptor descriptor, boolean isVirtual) {
		// TODO Auto-generated method stub

	}

}
