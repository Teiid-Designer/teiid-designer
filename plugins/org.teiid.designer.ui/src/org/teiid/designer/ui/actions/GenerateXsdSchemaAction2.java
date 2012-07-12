/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.common.widget.ListMessageDialog;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceDialog;
import org.teiid.designer.ui.wizards.GenerateXsdWizard;


public class GenerateXsdSchemaAction2 extends SortableSelectionAction {
    static final IStatus STATUS_OK = new StatusInfo(UiConstants.PLUGIN_ID);
    static final IStatus STATUS_ERROR = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR,
                                                       "Invalid selection. Must be relational models and/or tables"); //$NON-NLS-1$
    //UiConstants.Util.getString("validationError")); //$NON-NLS-1$
    private static final String SELECTION_DIALOG_TITLE = UiConstants.Util.getString("GenerateXsdSchemaAction.selectionDialog.title"); //$NON-NLS-1$
    private static final String SELECTION_DIALOG_MESSAGE = UiConstants.Util.getString("GenerateXsdSchemaAction.selectionDialog.initialMessage"); //$NON-NLS-1$
    private static final String KEY_NO_SEL_DLG_MSG = "GenerateXsdSchemaAction.noSelMsg"; //$NON-NLS-1$
    private static final String NO_SEL_DLG_TITLE = UiConstants.Util.getString("GenerateXsdSchemaAction.noSelTitle"); //$NON-NLS-1$
    private static final String DEFAULT_EXPLORER_TITLE = UiConstants.Util.getString("GenerateXsdSchemaAction.defaultViewName"); //$NON-NLS-1$;

    private boolean canIgnoreSelection = false;

    public GenerateXsdSchemaAction2() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CREATE_WEB_SERVICE_ICON));
    }

    @Override
    public void run() {
        IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();

        // ----------------------------------------------------
        // Defect 22355: added preProcess() check to see if VDB is dirty or any Models are Dirty.
        // We need to pre-process this through product characteristics
        // ----------------------------------------------------
        if (!ProductCustomizerMgr.getInstance().getProductCharacteristics().preProcess(this, iww.getShell())) {
            notifyResult(false);
            return;
        }
        // ----------------------------------------------------

        ISelection selection = getSelection();

        // We can handle single selection, multiple selection AND NO selection.
        // 1) If a document or document root is NOT selected we
        // go ahead and process.
        // 2) If Document Model selected ONLY, we present dialog to select document or document root within model
        // 3) If NULL, NONE OF THE ABOVE are selected, we present a dialog to document or document root within model
        // from the whole workspace/VDB

        IStructuredSelection thisSelection = null;

        if (isValidSelection(selection)) {
            thisSelection = (IStructuredSelection)selection;
            
            if( ! checkDirtyRelationalModel(thisSelection, iww.getShell()) ) {
            	return;
            }
        } else if (canIgnoreSelection) {
            // Set to false in case there's an exception and it doesn't get set back.
            canIgnoreSelection = false;
            // Present user a selection dialog to get selection

            ILabelProvider labelProvider = new ModelExplorerLabelProvider();
            ITreeContentProvider contentProvider = new ModelExplorerContentProvider();

            ModelWorkspaceDialog wsDialog = new ModelWorkspaceDialog(
                                                                     UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                                     null, labelProvider, contentProvider);

            wsDialog.setValidator(new Validator());
            wsDialog.setTitle(SELECTION_DIALOG_TITLE);
            wsDialog.setMessage(SELECTION_DIALOG_MESSAGE);
            wsDialog.open();

            if (wsDialog.getReturnCode() == Window.OK) {
                Object[] oSelectedObjects = wsDialog.getResult();

                // add the selected location to this Relationship
                if (oSelectedObjects.length > 0) {
                    thisSelection = new StructuredSelection(oSelectedObjects);
                } else {
                    // ERROR !!!!!
                    String title = UiConstants.Util.getString("GenerateWebServiceModelAction.selectionError.title"); //$NON-NLS-1$
                    String dlgMsg = UiConstants.Util.getString("GenerateWebServiceModelAction.selectionError.emptySelection"); //$NON-NLS-1$
                    MessageDialog.openError(iww.getShell(), title, dlgMsg);
                    notifyResult(false);
                    return;
                }
                if (!isValidSelection(thisSelection)) {
                    // ERROR !!!!!
                    String title = UiConstants.Util.getString("GenerateWebServiceModelAction.selectionError.title"); //$NON-NLS-1$
                    String dlgMsg = UiConstants.Util.getString("GenerateWebServiceModelAction.selectionError.wrongType"); //$NON-NLS-1$
                    MessageDialog.openError(iww.getShell(), title, dlgMsg);
                    notifyResult(false);
                }
            } else {
                return;
            }

        } else {
            // selection empty, tell user to select something:
            IViewReference ivr = iww.getActivePage().findViewReference(ProductCustomizerMgr.getInstance().getProductCharacteristics().getPrimaryNavigationViewId());
            String viewTitle;
            if (ivr != null) {
                viewTitle = ivr.getTitle();

            } else {
                // don't have a view ref, use a default name:
                viewTitle = DEFAULT_EXPLORER_TITLE;
            } // endif

            // show the dialog:
            String dlgMsg = UiConstants.Util.getString(KEY_NO_SEL_DLG_MSG, viewTitle);
            MessageDialog.openError(iww.getShell(), NO_SEL_DLG_TITLE, dlgMsg);
            return;
        }

        final GenerateXsdWizard wizard = new GenerateXsdWizard();
        wizard.init(iww.getWorkbench(), thisSelection);
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        int rc = dialog.open();

        if (rc != Window.CANCEL) {
            final MultiStatus result = wizard.getResult();
            final int severity = result.getSeverity();
            if (severity == IStatus.ERROR) {
                final String errTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.errTitle"); //$NON-NLS-1$
                final String err = UiConstants.Util.getString("GenerateXsdSchemaAction.errFinish"); //$NON-NLS-1$
                ErrorDialog.openError(wizard.getShell(), errTitle, err, result);
            } else if (severity == IStatus.WARNING) {
                final String warnTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.warnTitle"); //$NON-NLS-1$
                final String warn = UiConstants.Util.getString("GenerateXsdSchemaAction.warnFinish"); //$NON-NLS-1$
                ErrorDialog.openError(wizard.getShell(), warnTitle, warn, result);
            } else {
                final String okTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.successTitle"); //$NON-NLS-1$
                final String ok = UiConstants.Util.getString("GenerateXsdSchemaAction.successFinish"); //$NON-NLS-1$

                List msgs = new ArrayList(result.getChildren().length);
                for (int i = 0; i < result.getChildren().length; i++) {
                    msgs.add(result.getChildren()[i].getMessage());
                }

                // Defect 20589 - Thread off this dialog, so it shows up AFTER auto-build and other jobs which are
                // causing more Progress monitors to appear (lots of flashing).
                final List messgs = msgs;
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        ListMessageDialog.openInformation(wizard.getShell(), okTitle, null, ok, messgs, null);
                        if (wizard.getWebServiceModel() != null) {
                            // Changed to use method that insures Object editor mode is on
                            ModelEditorManager.openInEditMode(wizard.getWebServiceModel(),
                                                              true,
                                                              UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
                        }

                    }
                });
            }
            notifyResult(severity < IStatus.ERROR);
        }

    }

    /**
     * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated
     * with all Tables and Procedures contained within the current selection.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        boolean isValid = true;
        if (SelectionUtilities.isEmptySelection(selection)) {
            isValid = false;
        }
        
        if( !SelectionUtilities.isAllEObjects(selection) &&
        	!SelectionUtilities.isAllIResourceObjects(selection)	) {
        	isValid = false;
        }
        
        if( SelectionUtilities.isAllIResourceObjects(selection) && 
        		!SelectionUtilities.isSingleSelection(selection) ) {
        	isValid = false;
        }

        if (isValid) {
            final Collection objs = SelectionUtilities.getSelectedObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext() && isValid) {
                final Object next = selections.next();
                if (next instanceof Table) {
                    isValid = true;
                } else if (next instanceof Procedure) {
                    isValid = true;
                } else if (next instanceof IFile) {
                    final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)next);
                    if (modelResource != null) {
                        try {
                            // defect 19183 - do not load models on selection:
                            isValid = ModelUtilities.isRelationalModel(modelResource) && hasTableOrProcedure(modelResource);
                        } catch (ModelWorkspaceException err) {
                            UiConstants.Util.log(err);
                            isValid = false;
                        } catch (ModelerCoreException err) {
                            UiConstants.Util.log(err);
                            isValid = false;
                        }
                    } else {
                        isValid = false;
                    }
                } else {
                    isValid = false;
                }

                // stop processing if no longer valid:
                if (!isValid) {
                    break;
                } // endif -- valid
            } // endwhile -- all selected
        } // endif -- is empty sel

        return isValid;
    }

    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
    }
    
    
    private boolean checkDirtyRelationalModel( ISelection selection, Shell shell ) {
    	if( SelectionUtilities.isSingleSelection(selection)) {
	        final Object obj = SelectionUtilities.getSelectedObject(selection);

           if (obj instanceof IFile) {
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)obj);
                if (modelResource != null &&
                		(ModelIdentifier.isRelationalSourceModel(modelResource) ||
                		 ModelIdentifier.isRelationalViewModel(modelResource) ) ) {
                	
                	ModelEditor openEditor = ModelEditorManager.getModelEditorForFile((IFile)obj, false);
                	if( openEditor != null && openEditor.isDirty() ) {
                		MessageDialog.openWarning(shell, UiConstants.Util.getString("GenerateXsdSchemaAction.unsavedChangesTitle"),  //$NON-NLS-1$
                				UiConstants.Util.getString("GenerateXsdSchemaAction.unsavedChangesMessage", modelResource.getItemName())); //$NON-NLS-1$
                		return false;
                	}
                }
            }
    	}
    	return true;
    }
    
    /*
     * A relational model may be Empty or have no tables or procedures. In this case the wizard can't create anything.
     */
    private boolean hasTableOrProcedure(ModelResource mr) throws ModelWorkspaceException, ModelerCoreException {
    	TableOrProcedureFinder visitor = new TableOrProcedureFinder();
    	final int mode = ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS;   // show only those objects visible to user
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor,mode);
        
        processor.walk(mr, ModelVisitorProcessor.DEPTH_INFINITE);
        
        return visitor.hasTableOrProcedure();
        
//    	for( Object eObj : mr.getEObjects() ) {
//    		if( eObj instanceof Table || eObj instanceof Procedure ) {
//    			return true;
//    		}
//    	}
//    	
//    	return false;
    }

    /**
     * Validates the selection ensuring there is no .project file in the selected directory or in an ancestor directory.
     * 
     * @since 4.2
     */
    class Validator implements ISelectionStatusValidator {

        /**
         * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
         * @since 4.2
         */
        public IStatus validate( Object[] theSelection ) {
            IStructuredSelection selection = new StructuredSelection(theSelection);

            IStatus result = STATUS_OK;

            if (!isValidSelection(selection)) {
                result = STATUS_ERROR;
            }

            return result;
        }
    }

    /**
     * @param theCanIgnoreSelection The canIgnoreSelection to set.
     * @since 5.0
     */
    public void setCanIgnoreSelection( boolean theCanIgnoreSelection ) {
        this.canIgnoreSelection = theCanIgnoreSelection;
    }
    
    class TableOrProcedureFinder implements ModelVisitor {
    	
    	boolean hasTableOrProcedure = false;

		@Override
		public boolean visit(EObject object) throws ModelerCoreException {
			// Tables are contained by Catalogs, Schemas and Resources
	        if (object instanceof Table) {
	        	hasTableOrProcedure = true;
	            return false;
	        }
	        if (object instanceof Procedure) {
	        	hasTableOrProcedure = true;
	            return true;
	        }
	        if (object instanceof Catalog) {
	            // catalogs will contain tables
	            return true;
	        }
	        if (object instanceof Schema) {
	            // schemas will contain tables
	            return true;
	        }
	        return false;
		}

		@Override
		public boolean visit(Resource resource) throws ModelerCoreException {
			return true;
		}
		
		public boolean hasTableOrProcedure() {
			return hasTableOrProcedure;
		}
    	
    }
}
