package org.teiid.designer.relational.ui.webservice;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.ui.wizards.INewModelWizardContributor;


/**
 * @since 8.0
 */
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
