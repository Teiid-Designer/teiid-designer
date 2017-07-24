package org.teiid.designer.relational.ui.flatfile;

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
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.ui.wizards.INewModelWizardContributor;


/**
 * @since 8.0
 */
public class FlatFileRelationalModelContributor implements INewModelWizardContributor {


    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory factory = RelationalFactory.eINSTANCE;
    public static final DatatypeManager datatypeManager = ModelerCore.getWorkspaceDatatypeManager();
    
    private static boolean isTransactionable = ModelerCore.getPlugin() != null;
    //
    // Instance variables:
    //
    private IWizardPage[] pages;
    private FlatFileRelationalModelWizardPage flatFilePage;
    /**
     * Construct an instance of FlatFileRelationalModelContributor.
     */
    public FlatFileRelationalModelContributor() {
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
        flatFilePage = new FlatFileRelationalModelWizardPage(
				"flatFilePage"); //$NON-NLS-1$
        pages[0] = flatFilePage;

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
		String transactionName = UiConstants.Util.getString("FlatFileRelationalModelContributor.transactionName"); //$NON-NLS-1$
		boolean started = ModelerCore.startTxn(transactionName, this);
        boolean succeeded = false;
		try {

			if( flatFilePage.doGenerateGetFiles() ) {
				addGetFilesProcedure(modelResource);
			}
			
			if( flatFilePage.doGenerateGetTextFiles() ) {
				addGetTextFilesProcedure(modelResource);
			}
			
			if( flatFilePage.doGenerateSaveFile() ) {
				addSaveFileProcedure(modelResource);
			}
	    	
            succeeded = true;
        } catch (Exception ex) {
            String message = UiConstants.Util.getString("FlatFileRelationalModelContributor.doFinishError",     //$NON-NLS-1$
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
	
	private void addGetFilesProcedure(ModelResource mr) throws ModelerCoreException {

		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject blobType = datatypeManager.findDatatype("blob"); //$NON-NLS-1$
		EObject timestampType = datatypeManager.findDatatype("timestamp"); //$NON-NLS-1$
		EObject integerType = datatypeManager.findDatatype("int"); //$NON-NLS-1$
		
    	Procedure proc = factory.createProcedure();
    	proc.setName("getFiles"); //$NON-NLS-1$
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
    	}
    	addValue(result, column_2, result.getColumns());
    	
    	Column column_3 = factory.createColumn();
    	column_3.setName("lastModified"); //$NON-NLS-1$
    	if( timestampType != null) {
    		column_3.setType(timestampType);
    	}
    	addValue(result, column_3, result.getColumns());
    	
    	Column column_4 = factory.createColumn();
    	column_4.setName("created"); //$NON-NLS-1$
    	if( timestampType != null) {
    		column_4.setType(timestampType);
    	}
    	addValue(result, column_4, result.getColumns());
    	
    	Column column_5 = factory.createColumn();
    	column_5.setName("size"); //$NON-NLS-1$
    	if( integerType != null) {
    		column_5.setType(integerType);
    	}
    	addValue(result, column_5, result.getColumns());
    	
    	addValue(mr, proc, getModelResourceContents(mr));

	}
	
	private void addGetTextFilesProcedure(ModelResource mr) throws ModelerCoreException {
		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject clobType = datatypeManager.findDatatype("clob"); //$NON-NLS-1$
		EObject timestampType = datatypeManager.findDatatype("timestamp"); //$NON-NLS-1$
		EObject integerType = datatypeManager.findDatatype("int"); //$NON-NLS-1$
		
    	Procedure proc = factory.createProcedure();
    	proc.setName("getTextFiles"); //$NON-NLS-1$
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setProcedure(proc);
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
    	if( clobType != null) {
    		column_1.setType(clobType);
    	}
    	addValue(result, column_1, result.getColumns());
    	Column column_2 = factory.createColumn();
    	column_2.setName("filePath"); //$NON-NLS-1$
    	if( stringType != null) {
    		column_2.setType(stringType);
    	}
    	addValue(result, column_2, result.getColumns());
    	
    	Column column_3 = factory.createColumn();
    	column_3.setName("lastModified"); //$NON-NLS-1$
    	if( timestampType != null) {
    		column_3.setType(timestampType);
    	}
    	addValue(result, column_3, result.getColumns());
    	
    	Column column_4 = factory.createColumn();
    	column_4.setName("created"); //$NON-NLS-1$
    	if( timestampType != null) {
    		column_4.setType(timestampType);
    	}
    	addValue(result, column_4, result.getColumns());
    	
    	Column column_5 = factory.createColumn();
    	column_5.setName("size"); //$NON-NLS-1$
    	if( integerType != null) {
    		column_5.setType(integerType);
    	}
    	addValue(result, column_5, result.getColumns());
    	
    	addValue(mr, proc, getModelResourceContents(mr));

	}
	
	
	private void addSaveFileProcedure(ModelResource mr) throws ModelerCoreException {
		EObject stringType = datatypeManager.findDatatype("string"); //$NON-NLS-1$
		EObject objectType = datatypeManager.findDatatype("object"); //$NON-NLS-1$
		
		Procedure proc = factory.createProcedure();
    	proc.setName("saveFile"); //$NON-NLS-1$
    	ProcedureParameter param = factory.createProcedureParameter();
    	param.setProcedure(proc);
    	param.setName("filePath"); //$NON-NLS-1$
    	param.setProcedure(proc);
    	if( stringType != null) {
    		param.setType(stringType);
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
	
    public void addValue(final Object owner, final Object value, EList feature) {
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
	
    public EList getModelResourceContents(ModelResource resource ) {
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

	@Override
	public ModelResource getSelectedModelResource() {
		return null;
	}
	
	@Override
    public boolean copyAllDescriptions() { 
		return false;
	}
}
