/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.IProgressConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.JdbcUtil;
import org.teiid.designer.jdbc.relational.CostAnalyzer;
import org.teiid.designer.jdbc.relational.CostAnalyzerFactory;
import org.teiid.designer.jdbc.relational.impl.TableStatistics;
import org.teiid.designer.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import org.teiid.designer.jdbc.ui.ModelerJdbcUiConstants;
import org.teiid.designer.jdbc.ui.ModelerJdbcUiPlugin;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.util.RelationalUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class JdbcCostAnalysisAction extends SortableSelectionAction {

    private IFile selectedModel;
    private List<Table> selectedTables;

    /**
     * Constructor
     */
    public JdbcCostAnalysisAction() {
        super();
        setImageDescriptor(ModelerJdbcUiPlugin.getDefault().getImageDescriptor(ModelerJdbcUiConstants.Images.COST_ANALYSIS));
    }

    /**
     * 
     */
    @Override
    public boolean isApplicable( final ISelection selection ) {
        return isValidSelection(selection);
    }

    /**
     * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated with
     * all Tables and Procedures contained within the current selection.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( final ISelection selection ) {
        boolean isValid = true;
        this.selectedModel=null;
        this.selectedTables=null;
        if (SelectionUtilities.isEmptySelection(selection)) isValid = false;

        if (isValid) {
        	try {
        		// Single Selection must be a Model or a Table
        		if (SelectionUtilities.isSingleSelection(selection)) {
        			Object obj = SelectionUtilities.getSelectedObject(selection);
        			ModelResource modelResource = null;
        			// Object must be a ModelResource or a Table
        			if (obj instanceof IFile && ModelUtilities.isModelFile((IFile)obj)) {
        				modelResource = ModelUtil.getModelResource((IFile)obj, false);
        			} else if (obj instanceof Table) {
        				modelResource = ModelUtil.getModel(obj);
        			} else {
        				isValid = false;
        			}
        			// Make sure the selection has a JdbcSource
        			if(isValid) {
        				if (ModelUtilities.hasJdbcSource(modelResource)) {
        					this.selectedModel = (IFile)modelResource.getResource();
        					if(obj instanceof Table) {
        						this.selectedTables = new ArrayList<Table>();
        						this.selectedTables.add((Table)obj);
        					}
        				} else {
        					isValid = false;
        				}
        			}
        		// Multi selection must be all Tables
        		} else {
        			ModelResource theModel = null;
        			List<Object> objs = SelectionUtilities.getSelectedObjects(selection);
        			List<Table> allTables = new ArrayList<Table>();
        			for(Object aObj : objs) {
        				if(aObj instanceof Table) {
        					ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((Table)aObj);
        					if (modelResource!=null) {
        						if(theModel==null) theModel = modelResource;
        						if (!modelResource.equals(theModel)) {
        							isValid=false;
        							break;
        						} else {
        							allTables.add((Table)aObj);
        						}
        					} else {
        						isValid=false;
        						break;
        					}
        				} else {
        					isValid = false;
        					break;
        				}
        			}
        			// Make sure theModel has a JdbcSource
        			if(isValid) {
        				if (ModelUtilities.hasJdbcSource(theModel)) {
        					this.selectedModel = (IFile)theModel.getResource();
        					this.selectedTables = allTables;
        				} else {
        					isValid = false;
        				}
        			}
        		}
        	} catch (ModelWorkspaceException e) {
        		isValid = false;
        	}

        }
        
        return isValid;
    }
    
    /**
     * We will compute column-level statistics (min-value, max-value, # of null values, # of distinct values) for all columns in
     * tables in the model IFF the model is physical relational with a Jdbc source. We must first prompt the user for the
     * password, as it is not stored with the Jdbc import settings.
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.3
     */
    @Override
    public void run() {
        if (isEnabled()) {
            final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
            try {
                ModelResource modelResource = ModelUtil.getModelResource(this.selectedModel, false);
                if (modelResource != null) {
                    // JdbcSource found - check if can do costing update
                    String allowsCostUpdate = ModelUtil.getModelAnnotationPropertyValue(modelResource, IConnectionInfoHelper.JDBCCONNECTION_NAMESPACE+IConnectionInfoHelper.JDBCCONNECTION_ALLOW_COSTUPDATE_KEY);
                    if(allowsCostUpdate!=null && !allowsCostUpdate.isEmpty() && !Boolean.getBoolean(allowsCostUpdate)) {
                        String title = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.costingNotAllowed.title"); //$NON-NLS-1$
                        String message = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.costingNotAllowed.msg"); //$NON-NLS-1$
                        MessageDialog.openInformation(shell, title, message);
                        return;
                    }
                	
                	boolean cancelled = openEditorIfNeeded(modelResource);
                	
                	if( cancelled ) {
                		return;
                	}
                	final Resource resource = modelResource.getEmfResource();
                	
                    executeInTransaction(resource, selectedTables, shell);
                }
            } catch (Exception e) {
                InternalModelerJdbcUiPluginConstants.Util.log(e);
                final String title = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.errorTitle"); //$NON-NLS-1$
                final String message = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.errorMessage"); //$NON-NLS-1$
                MessageDialog.openError(shell, title, message);
            }
        }
    }
    
    private void executeInTransaction(final Resource resource, final List<Table> tables, final Shell shell) {
        boolean requiredStart = ModelerCore.startTxn(true,true,"Update Cost Statistics",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	internalExecute(resource, tables, shell);
        	            
            succeeded = true;
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
         
    }
    
    private void internalExecute(final Resource resource, final List<Table> tables, final Shell shell) {
    	if (resource != null) {
            final JdbcSource source = JdbcUtil.findJdbcSource(resource);
            if (source != null) {
            	final List emfTables = getTables(tables,resource);
                final CostAnalyzerFactory analyzerFactory = CostAnalyzerFactory.getCostAnalyzerFactory();
                final Map tblStats = analyzerFactory.createTableInfos(emfTables);
                if (tblStats != null && tblStats.size() > 0) {
                    CostAnalysisDialog dialog = new CostAnalysisDialog(
                                                                       shell,
                                                                       InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.taskDescription"), //$NON-NLS-1$
                                                                       InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.passwordPrompt", new Object[] {source.getUrl(), source.getUsername()}), null, null); //$NON-NLS-1$
                    dialog.open();

                    final String password = dialog.getValue();
                    if (password != null) {
                        final Job job = new Job(
                                                InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.jobDescription")) { //$NON-NLS-1$
                            @Override
                            protected IStatus run( IProgressMonitor monitor ) {
                                try {
                                    monitor.beginTask(InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.taskDescription"), calculateNumberOfWorkIncrements(tblStats.values())); //$NON-NLS-1$

                                    CostAnalyzer costAnalyzer = analyzerFactory.getCostAnalyzer(source,password);
                                    // log output to standard out
                                    // costAnalyzer.setOutputStream(System.out);
                                    costAnalyzer.collectStatistics(tblStats, monitor);

                                    if (!monitor.isCanceled()) {
                                    	analyzerFactory.populateEmfColumnStatistics(emfTables, tblStats);
                                    }

                                    monitor.done();

                                    if (monitor.isCanceled()) {
                                        return Status.CANCEL_STATUS;
                                    }

                                    return new Status(
                                                      IStatus.OK,
                                                      ModelerJdbcUiConstants.PLUGIN_ID,
                                                      IStatus.OK,
                                                      InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.statusFinished", emfTables.size()), null); //$NON-NLS-1$
                                } catch (Exception e) {
                                    InternalModelerJdbcUiPluginConstants.Util.log(e);
                                    return new Status(
                                                      IStatus.ERROR,
                                                      ModelerJdbcUiConstants.PLUGIN_ID,
                                                      IStatus.ERROR,
                                                      InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.errorMessage"), e); //$NON-NLS-1$
                                } finally {
                                }
                            }
                        };

                        job.setSystem(false);
                        job.setUser(true);
                        job.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
                        // start as soon as possible
                        job.schedule();
                    }
                } else {
                    MessageDialog.openInformation(shell,
                                                  InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.taskDescription"), InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.noValidTablesMessage")); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }
    // calculate the number of work units for the progress monitoring
    // of this cost analysis task
    int calculateNumberOfWorkIncrements( Collection tblStats ) {
        // first, add twice the number of columns for two table operations
        // (table cardinality and column attribute population)
        int numWorkInc = tblStats.size() * 2;
        for (Iterator it = tblStats.iterator(); it.hasNext();) {
            TableStatistics tblStat = (TableStatistics)it.next();
            // add the number of columns from each table,
            // as each requires 1-2 database operations
            numWorkInc += tblStat.getColumnStats().size();
        }
        return numWorkInc;
    }
    
    /**
     * Get the tables for the costing updates.  If tables have been selected, they're used.  Otherwise all tables from the resource are used.
     * @param selectedTables the list of selected tables
     * @param resource the resource
     * @return the tables for costing update
     */
    private List<Table> getTables(List<Table> selectedTables, final Resource resource) {
    	List<Table> resultTables = null;
    	
    	if(selectedTables!=null && !selectedTables.isEmpty()) {
    		resultTables = selectedTables;
    	} else {
    		resultTables = RelationalUtil.findTables(resource);
    	}
    	
    	return resultTables;
    }

    class CostAnalysisDialog extends InputDialog {

        private static final char ECHO_CHAR = '*';

        public CostAnalysisDialog( Shell parentShell,
                                   String dialogTitle,
                                   String dialogMessage,
                                   String initialValue,
                                   IInputValidator validator ) {
            super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
        }

        @Override
        protected Control createDialogArea( Composite parent ) {
            Control control = super.createDialogArea(parent);
            getText().setEchoChar(ECHO_CHAR);
            return control;
        }
    }

    @Override
    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    /**
     * Should only be called if current object and model are not <code>null</code>.
     * 
     * @since 5.5.3
     */
    private boolean openEditorIfNeeded(ModelResource currentModel) {
    	boolean openEditorCancelled = false;
        // we only need to worry about the readonly status if the file is not currently open,
        // and its underlying IResource is not read only
        
    	if (currentModel!=null && !isEditorOpen(currentModel) && !currentModel.getResource().getResourceAttributes().isReadOnly()) {
            final IFile modelFile = (IFile)currentModel.getResource();
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();

            // may want to change these text strings eventually:
            if (MessageDialog.openQuestion(shell,
                                           ModelEditorManager.OPEN_EDITOR_TITLE,
                                           ModelEditorManager.OPEN_EDITOR_MESSAGE)) {
                // load and activate, not async (to prevent multiple dialogs from coming up):
                // Changed to use method that insures Object editor mode is on
                ModelEditorManager.openInEditMode(modelFile, true, UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);

            } else {
            	openEditorCancelled = true;
            }
        }
        
        return openEditorCancelled;
    }
    
    private boolean isEditorOpen(ModelResource currentModel) {
        if (currentModel != null) {
            IFile modelFile = (IFile)currentModel.getResource();
            return (ModelEditorManager.isOpen(modelFile));
        }

        return false;
    }
}
