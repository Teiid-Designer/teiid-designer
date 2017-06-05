/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalModel;
import org.teiid.designer.relational.model.RelationalModelFactory;
import org.teiid.designer.relational.model.RelationalProcedure;
import org.teiid.designer.relational.model.RelationalProcedureResultSet;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.edit.RelationalDialogModel;
import org.teiid.designer.relational.ui.editor.EditRelationalObjectDialog;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.ui.actions.INewChildAction;
import org.teiid.designer.ui.actions.INewSiblingAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

/**
 *
 */
public class CreateRelationalProcedureAction extends Action implements INewChildAction, INewSiblingAction {
	private IFile selectedModel;
	 
	private Collection<String> datatypes;
	
	protected int currentProcedureType = RelationalProcedure.PROCEDURE_TYPE.FUNCTION.ordinal();
	
	Button procedureRB;
	Button sourceFunctionRB;
	Button nativeQueryProcedureRB;
	 
	/**
	 * 
	 */
	public CreateRelationalProcedureAction() {
		super(Messages.createRelationalProcedureActionText);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor( UiConstants.Images.NEW_PROCEDURE_ICON));
		
		IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
		Set<String> unsortedDatatypes = service.getAllDataTypeNames();
		datatypes = new ArrayList<String>();
		
		String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
		Arrays.sort(sortedStrings);
		for( String dType : sortedStrings ) {
			datatypes.add(dType);
		}
	}
	
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewChildAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean canCreateChild(EObject parent) {
    	return false;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewChildAction#canCreateChild(org.eclipse.core.resources.IFile)
     */
    @Override
	public boolean canCreateChild(IFile modelFile) {
    	return isApplicable(new StructuredSelection(modelFile));
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.actions.INewSiblingAction#canCreateChild(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean canCreateSibling(EObject parent) {
		// has to be a table, view, procedure or function
		if( !(parent instanceof Table || parent instanceof Procedure || parent instanceof Index) ) {
			return false;
		}
    	//Convert eObject selection to IFile
    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(parent);
    	if( mr != null ) {
    		IFile modelFile = null;
    		
    		try {
				modelFile = (IFile)mr.getCorrespondingResource();
			} catch (ModelWorkspaceException ex) {
				UiConstants.Util.log(ex);
			}
    		if( modelFile != null ) {
    			return isApplicable(new StructuredSelection(modelFile));
    		}
    	}
    	
    	return false;
    }
    
	/**
	 * @param selection the selected object
	 * @return if applicable to selection
	 */
	public boolean isApplicable(ISelection selection) {
		boolean result = false;
		if (!SelectionUtilities.isMultiSelection(selection)) {
			Object obj = SelectionUtilities.getSelectedObject(selection);
			if (obj instanceof IResource) {
				IResource iRes = (IResource) obj;
				if (ModelIdentifier.isRelationalSourceModel(iRes)) {
					this.selectedModel = (IFile) obj;
					result = true;
				}
			}
		}

		return result;
	}

	@Override
	public void run() {
		if( selectedModel != null ) {
	        ModelResource mr = ModelUtilities.getModelResource(selectedModel);
	        run(mr);
		}
	}
	
	public void run(ModelResource mr) {
        final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
        
        RelationalProcedure procedure = new RelationalProcedure();
        
        SelectProcedureTypeDialog procedureTypeDialog = new SelectProcedureTypeDialog(shell, procedure);
        
        procedureTypeDialog.open();
        
        if (procedureTypeDialog.getReturnCode() == Window.OK) {
	        if( procedure.isNativeQueryProcedure() ) {
	        	RelationalProcedureResultSet resultSet = new RelationalProcedureResultSet("ResultSet");
	        	RelationalColumn column = new RelationalColumn("output");
	        	column.setDatatype("object");
	        	resultSet.addColumn(column);
	        	procedure.setResultSet(resultSet);
	        }
	        
	        // Hand the table off to the generic edit dialog
            RelationalDialogModel dialogModel = new RelationalDialogModel(procedure, selectedModel);
            EditRelationalObjectDialog dialog = new EditRelationalObjectDialog(shell, dialogModel);
	        
	        dialog.open();
	        
	        if (dialog.getReturnCode() == Window.OK) {
	        	createProcedureInTxn(mr, procedure);
	        }
        }
	}

    private void createProcedureInTxn(ModelResource modelResource, RelationalProcedure procedure) {
        boolean requiredStart = ModelerCore.startTxn(true, true, Messages.createRelationalProcedureTitle, this);
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
    
    class SelectProcedureTypeDialog extends TitleAreaDialog {
    	RelationalProcedure relationalProcedure;
    	
    	public SelectProcedureTypeDialog(Shell parentShell, RelationalProcedure procedure) {
    		super(parentShell);
    		relationalProcedure = procedure;
    	}

        /**
         * @see org.eclipse.jface.window.Window#constrainShellSize()
         */
        @Override
        protected void constrainShellSize() {
            super.constrainShellSize();

            final Shell shell = getShell();
            shell.setText(Messages.selectProcedureTypeDialogTitle);

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
    		panel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));

    		// set title
    		setTitle(Messages.selectProcedureTypeDialogSubTitle);
    		
    		{ // simple procedure
    		    procedureRB = new Button(panel, SWT.RADIO);
	            procedureRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            procedureRB.setText(Messages.procedureLabel);
	            procedureRB.addSelectionListener(new SelectionAdapter() {
	                /**            		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	handleInfoChanged();
	                }
	            });
	            procedureRB.setSelection(!relationalProcedure.isFunction());
	    		
    	    	Text descText = new Text(panel, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
                ((GridData)descText.getLayoutData()).horizontalIndent = 20;
                ((GridData)descText.getLayoutData()).heightHint = (3 * descText.getLineHeight());
    	    	descText.setText(Messages.createRelationalProcedureDescription);
    		}
            
    		{ // source function
	            sourceFunctionRB = new Button(panel, SWT.RADIO);
	            sourceFunctionRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            sourceFunctionRB.setText(Messages.sourceFunctionLabel);
	            sourceFunctionRB.addSelectionListener(new SelectionAdapter() {
	                /**            		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	handleInfoChanged();
	                }
	            });

	            if (this.relationalProcedure.isSourceFunction()) {
	                sourceFunctionRB.setSelection(relationalProcedure.isSourceFunction());
	            }

	            Text descText = new Text(panel, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
                ((GridData)descText.getLayoutData()).horizontalIndent = 20;
                ((GridData)descText.getLayoutData()).heightHint = (3 * descText.getLineHeight());
    	    	descText.setText(Messages.createRelationalSourceFunctionDescription);
    		}
    		
    		{ // Native Query PRocedure
	            nativeQueryProcedureRB = new Button(panel, SWT.RADIO);
	            nativeQueryProcedureRB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	            nativeQueryProcedureRB.setText(Messages.nativeQueryProcedureLabel);
	            nativeQueryProcedureRB.addSelectionListener(new SelectionAdapter() {
	                /** nativeQueryProcedureRB          		
	                 * {@inheritDoc}
	                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	                 */
	                @Override
	                public void widgetSelected( SelectionEvent e ) {
	                	handleInfoChanged();
	                }
	            });

	            if (this.relationalProcedure.isNativeQueryProcedure()) {
	            	nativeQueryProcedureRB.setSelection(relationalProcedure.isNativeQueryProcedure());
	            }

	            Text descText = new Text(panel, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
    	    	descText.setBackground(parent.getBackground());
    	    	descText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    	    	descText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
                ((GridData)descText.getLayoutData()).horizontalIndent = 20;
                ((GridData)descText.getLayoutData()).heightHint = (3 * descText.getLineHeight());
    	    	descText.setText(Messages.createRelationalNativeQueryProcedureDescription);
    		}
            
            return pnlOuter;
    	}
    	
    	private void handleInfoChanged() {
    		if( procedureRB.getSelection() ) {
    			relationalProcedure.setProcedureType(RelationalProcedure.PROCEDURE_TYPE.PROCEDURE);
    		} else if( sourceFunctionRB.getSelection()) {
    			relationalProcedure.setProcedureType(RelationalProcedure.PROCEDURE_TYPE.SOURCE_FUNCTION);
    		} else {
    			relationalProcedure.setProcedureType(RelationalProcedure.PROCEDURE_TYPE.NATIVE_QUERY_PROCEDURE);
    		}
    	}

    }
}