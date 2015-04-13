package org.teiid.designer.transformation.ui.actions;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalViewProcedure;
import org.teiid.designer.relational.model.RelationalProcedure.PROCEDURE_TYPE;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.edit.RelationalDialogModel;
import org.teiid.designer.relational.ui.editor.EditRelationalObjectDialog;
import org.teiid.designer.relational.ui.textimport.RelationalTableLocationSelectionValidator;
import org.teiid.designer.transformation.model.RelationalViewModelFactory;
import org.teiid.designer.transformation.ui.Messages;
import org.teiid.designer.transformation.ui.editors.TransformationDialogModel;
import org.teiid.designer.transformation.ui.textimport.VirtualModelSelectorDialog;
import org.teiid.designer.transformation.ui.textimport.VirtualTablelLocationSelectionValidator;
import org.teiid.designer.transformation.ui.wizards.RelationalModelSelectorDialog;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;

/**
 * This class provides a mechanism to create either a User Defined Function or a Pushdown/Source Function
 * 
 * @author blafond
 *
 */
public class CreateFunctionAction extends Action {
    
	/**
	 * 
	 */
	public CreateFunctionAction() {
		super(Messages.createFunctionLabel);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor( UiConstants.Images.NEW_PROCEDURE_ICON));
	}
	
    /**
     * @param properties the initial Designer properties
     */
    public CreateFunctionAction( Properties properties ) {
        this();
    }

   public void run(String name, int numArgs) {

        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        
        SelectFunctionTypeDialog procedureTypeDialog = new SelectFunctionTypeDialog(shell);
        
        procedureTypeDialog.open();
        
        if (procedureTypeDialog.getReturnCode() == Window.OK) {
        	boolean isUDF = procedureTypeDialog.isUDF();
        
            if( isUDF ) {
            	// TODO: Need to Ask user for a target VIEW model
            	ModelResource mr = queryUserForViewModel();
            	
        		if( mr != null ) {
        	        
			        RelationalProcedure relationalProcedure = new RelationalViewProcedure(name);
			        relationalProcedure.setFunction(true);
			        
			        for (int i=0; i<numArgs; i++ ) {
			        	RelationalParameter param = new RelationalParameter("arg"+ (i+1)); //$NON-NLS-1$
			        	param.setDatatype("string"); //$NON-NLS-1$
			        	relationalProcedure.addParameter(param);
			        }
			        
			        RelationalParameter returnParam =  new RelationalParameter("return_param"); //$NON-NLS-1$
			        returnParam.setDatatype("string"); //$NON-NLS-1$
			        returnParam.setDirection(RelationalConstants.DIRECTION.RETURN);
			        relationalProcedure.addParameter(returnParam);

		            TransformationDialogModel dialogModel = new TransformationDialogModel(relationalProcedure, getCorrespondingResource(mr));
		            EditRelationalObjectDialog dialog = new EditRelationalObjectDialog(shell, dialogModel);
		
			        dialog.open();
			        
			        if (dialog.getReturnCode() == Window.OK) {
			        	createUDFInTxn(mr, (RelationalViewProcedure)relationalProcedure);
			        }
        		}
			} else {
				// TODO: Need to Ask user for a target SOURCE model
            	ModelResource mr = queryUserForModel();
            	
        		if( mr != null ) {
	            	RelationalProcedure relationalProcedure = new RelationalProcedure(name);
	            	relationalProcedure.setProcedureType(PROCEDURE_TYPE.SOURCE_FUNCTION);
			        
			        for (int i=0; i<numArgs; i++ ) {
			        	RelationalParameter param = new RelationalParameter("arg"+ (i+1)); //$NON-NLS-1$
			        	param.setDatatype("string"); //$NON-NLS-1$
			        	relationalProcedure.addParameter(param);
			        }
	            	
			        // Hand the table off to the generic edit dialog
		            RelationalDialogModel dialogModel = new RelationalDialogModel(relationalProcedure, getCorrespondingResource(mr));
		            EditRelationalObjectDialog dialog = new EditRelationalObjectDialog(shell, dialogModel);
			        
			        dialog.open();
			        
			        if (dialog.getReturnCode() == Window.OK) {
			        	createFunctionInTxn(mr, relationalProcedure);
			        }
        		}
            }
		}
		
	}

    private void createUDFInTxn( ModelResource modelResource, RelationalViewProcedure viewProcedure ) {

        boolean requiredStart = ModelerCore.startTxn(true, true, Messages.createRelationalViewProcedureTitle, this);
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalViewModelFactory factory = new RelationalViewModelFactory();
                
                RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
                relModel.addChild(viewProcedure);
                
                factory.build(modelResource, relModel, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                    Messages.createRelationalViewProcedureExceptionMessage,
                                    e.getMessage());
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, Messages.createRelationalViewProcedureExceptionMessage, e);
            UiConstants.Util.log(status);

            return;
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
    }
    
    private void createFunctionInTxn(ModelResource modelResource, RelationalProcedure procedure) {
        boolean requiredStart = ModelerCore.startTxn(true, true, "Create Source Function", this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            ModelEditor editor = ModelEditorManager.getModelEditorForFile((IFile)modelResource.getCorrespondingResource(), true);
            if (editor != null) {
                boolean isDirty = editor.isDirty();

                RelationalModelFactory factory = new RelationalModelFactory();
                
                RelationalModel relModel = new RelationalModel("dummy"); //$NON-NLS-1$
                relModel.addChild(procedure);
                
                factory.build(modelResource, relModel, new NullProgressMonitor());
    	        //factory.buildObject(table, modelResource, new NullProgressMonitor());

                if (!isDirty && editor.isDirty()) {
                    editor.doSave(new NullProgressMonitor());
                }
                succeeded = true;
            }
        } catch (Exception e) {
        	MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.createRelationalProcedureExceptionMessage, e.getMessage());
            IStatus status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, Messages.createRelationalProcedureExceptionMessage, e);
            UiConstants.Util.log(status);

            return;
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
    }
    

    private IFile getCorrespondingResource(ModelResource mr) {
    	IFile file = null;
    	
    	try {
			file = (IFile)mr.getCorrespondingResource();
		} catch (ModelWorkspaceException e) {
			UiConstants.Util.log(e);
		}
    	
    	return file;
    }
    
    
    class SelectFunctionTypeDialog extends TitleAreaDialog {
    	
    	boolean isUDF = false;
    	
    	public SelectFunctionTypeDialog(Shell parentShell) {
    		super(parentShell);
    	}

    	/**
    	 * @see org.eclipse.jface.window.Window#constrainShellSize()
    	 */
    	@Override
        protected void constrainShellSize() {
            super.constrainShellSize();

            final Shell shell = getShell();
            shell.setText("Choose Function Type");

            { // set size
                final Rectangle r = shell.getBounds();
                shell.setBounds(r.x, r.y, (int)(r.width * 0.67), r.height);
            }

            { // center on parent
                final Shell parentShell = (Shell)shell.getParent();
                final Rectangle parentBounds = parentShell.getBounds();
                final Point parentCenter = new Point(parentBounds.x + (parentBounds.width/2), parentBounds.y + parentBounds.height/2);

                final Rectangle r = shell.getBounds();
                final Point shellLocation = new Point(parentCenter.x - r.width/2, parentCenter.y - r.height/2);

                shell.setBounds(Math.max(0, shellLocation.x), Math.max(0, shellLocation.y), r.width, r.height);
            }
        }
    	
    	/**
    	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
    	 * @since 5.5.3
    	 */
		@Override
    	protected Control createDialogArea(Composite parent) {
    		Composite pnlOuter = (Composite) super.createDialogArea(parent);
    		
    		Composite panel = new Composite(pnlOuter, SWT.NONE);
    		GridLayout gridLayout = new GridLayout();
            gridLayout.marginLeft = 20;
            gridLayout.marginRight = 20;
    		panel.setLayout(gridLayout);
    		panel.setLayoutData(new GridData(GridData.FILL_BOTH));

    		// set title
    		setTitle(Messages.selectProcedureTypeDialogSubTitle);
    		
    		{ // source function
	            final Button sourceFunctionRB = new Button(panel, SWT.RADIO);
	            sourceFunctionRB.setSelection(true);
	            sourceFunctionRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            sourceFunctionRB.setText(Messages.sourceFunctionLabel);
	            sourceFunctionRB.addSelectionListener(new SelectionAdapter() {
	                /**            		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	isUDF = !sourceFunctionRB.getSelection();
	                }
	            });

	            Text descText = new Text(panel, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
                ((GridData)descText.getLayoutData()).horizontalIndent = 20;
                ((GridData)descText.getLayoutData()).heightHint = (3 * descText.getLineHeight());
    	    	descText.setText(Messages.createRelationalSourceFunctionDescription);
    		}
            
    		{ // user defined function
	            final Button userDefinedFunctionRB = new Button(panel, SWT.RADIO);
	            userDefinedFunctionRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            userDefinedFunctionRB.setText(Messages.userDefinedFunctionLabel);
	            userDefinedFunctionRB.addSelectionListener(new SelectionAdapter() {
	                /**            		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	isUDF = userDefinedFunctionRB.getSelection();
	                }
	            });
	    		
    	    	Text descText = new Text(panel, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
                ((GridData)descText.getLayoutData()).horizontalIndent = 20;
                ((GridData)descText.getLayoutData()).heightHint = (3 * descText.getLineHeight());
    	    	descText.setText(Messages.createRelationalViewUserDefinedFunctionDescription);
    		}
            return pnlOuter;
    	}
		
		public boolean isUDF() {
			return this.isUDF;
		}

    }
    
    private ModelResource queryUserForViewModel() {
            VirtualModelSelectorDialog mwdDialog = new VirtualModelSelectorDialog(
    		                UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell());
    		mwdDialog.setValidator(new VirtualTablelLocationSelectionValidator());
    		mwdDialog.setAllowMultiple(false);
    		mwdDialog.open();
    		
    		if (mwdDialog.getReturnCode() == Window.OK) {
    			Object[] oSelectedObjects = mwdDialog.getResult();
    			// add the selected location to this Relationship
                try {
    				if (oSelectedObjects.length == 1 && oSelectedObjects[0] instanceof IFile) {
    					return ModelUtil.getModelResource((IFile)(IFile)oSelectedObjects[0], false);
    				}
    			} catch (ModelWorkspaceException e) {
    				UiConstants.Util.log(e);
    			}
    		}
    		
    		return null;
    }
    
    private ModelResource queryUserForModel() {
    	RelationalModelSelectorDialog mwdDialog = new RelationalModelSelectorDialog(
		                UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell());
		mwdDialog.setValidator(new RelationalTableLocationSelectionValidator());
		mwdDialog.setAllowMultiple(false);
		mwdDialog.open();
		
		if (mwdDialog.getReturnCode() == Window.OK) {
			Object[] oSelectedObjects = mwdDialog.getResult();
			// add the selected location to this Relationship
            try {
				if (oSelectedObjects.length == 1 && oSelectedObjects[0] instanceof IFile) {
					return ModelUtil.getModelResource((IFile)(IFile)oSelectedObjects[0], false);
				}
			} catch (ModelWorkspaceException e) {
				UiConstants.Util.log(e);
			}
		}
		
		return null;
}
}
