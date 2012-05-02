/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.preview;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactoryProvider;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IConnectionProfileProvider;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
import org.eclipse.datatools.sqltools.result.ui.ResultsViewUIPlugin;
import org.eclipse.datatools.sqltools.routineeditor.launching.LaunchHelper;
import org.eclipse.datatools.sqltools.routineeditor.launching.RoutineLaunchConfigurationAttribute;
import org.eclipse.datatools.sqltools.sqleditor.result.SimpleSQLResultRunnable;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;
import org.teiid.adminapi.Admin;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.ui.TeiidAdHocScriptRunnable;
import org.teiid.datatools.views.ExecutionPlanView;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.runtime.ServerManager;
import org.teiid.designer.runtime.TeiidJdbcInfo;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.preview.jobs.TeiidPreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.WorkspacePreviewVdbJob;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.AccessPatternColumnsDialog;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.ParameterInputDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

public class PreviewDataWorker {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(PreviewDataWorker.class);
	
    static String getString( String key ) {
        return DqpUiConstants.UTIL.getString(THIS_CLASS + key);
    }

    static String getString( final String key, final Object param ) {
    	return DqpUiConstants.UTIL.getString(THIS_CLASS + key, param);
	}
    
    static String getString( final String key, final Object param, final Object param2 ) {
    	return DqpUiConstants.UTIL.getString(THIS_CLASS + key, param, param2);
	}
    
    /**
     * Valid selections include Relational Tables, Procedures.
     * 
     * @return is previeable or not
     */
    public boolean isPreviewableEObject( EObject eObject ) {
        // An object can be previewed if it is of a certain object type in a Source/Relational model
        // This is changed from previous releases because the requirement of having a Source binding prior to
        // enablement has changed. Now the binding check is moved to the run() method which performs the check
        // and queries the user for any additional info that's needed to execute the preview, including creating
        // a source binding if necessary.


        // eObj must be previewable
        return ModelObjectUtilities.isExecutable(eObject);

    }
    
    public Shell getShell() {
    	return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }
    
    public boolean isPreviewPossible() {
    	if( !ModelEditorManager.getDirtyResources().isEmpty() ) {
    		boolean doContinue = MessageDialog.openQuestion(getShell(), 
    				getString("unsavedModelsWarning.title"),  //$NON-NLS-1$
    				getString("unsavedModelsWarning.message")); //$NON-NLS-1$
        	if( !doContinue ) {
        		return false;
        	}
    	}

    	// make sure preview is enabled and that there is a Teiid server
    	if (!RuntimeAssistant.ensurePreviewEnabled(getShell())) {
    	    return false;
    	}
    	
    	return true;
    }
    
    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */

    public void run( final EObject eObject,
                     final boolean planOnly ) {

    	// if we get here we know preview is enabled and a server exists and can be connected to
    	
        if ((Job.getJobManager().find(WorkspacePreviewVdbJob.WORKSPACE_PREVIEW_FAMILY).length != 0) ||
                (Job.getJobManager().find(TeiidPreviewVdbJob.TEIID_PREVIEW_FAMILY).length != 0)) {
            PreviewUnavailableDialog dialog = new PreviewUnavailableDialog(getShell());
            dialog.open();
            
            if (dialog.shouldOpenProgressView()) {
                IWorkbenchPage page = UiUtil.getWorkbenchPage();
                
                if (page != null) {
                    try {
                        page.showView(IProgressConstants.PROGRESS_VIEW_ID);
                    } catch (PartInitException e) {
                        DqpUiConstants.UTIL.log(e);
                    }
                }
            }

            return;
        }
        
        IConnectionInfoHelper helper = new ConnectionInfoHelper();
        ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);
        if (mr != null && ModelIdentifier.isPhysicalModelType(mr) ) { 
        	if( !helper.hasConnectionInfo(mr)) {
	        	MessageDialog.openWarning(getShell(), 
	        			getString("noPreviewAvailableTitle"),  //$NON-NLS-1$
	        			getString("noProfileAvailableMissingConnectionInfoMessage", mr.getItemName())); //$NON-NLS-1$
	        	return;
	        }
        	
        	String translatorName = helper.getTranslatorName(mr);
        	if( translatorName != null ) {
        		TeiidTranslator tt = null; 
        		
        		try {
					tt = getServerManager().getDefaultServer().getAdmin().getTranslator(translatorName);
				} catch (Exception e) {
					DqpUiConstants.UTIL.log(e);
				}
        		
        		if( tt == null ) {
        			boolean result = MessageDialog.openQuestion(getShell(), 
    	        			getString("noMatchingTranslatorTitle"),  //$NON-NLS-1$
    	        			getString("noMatchingTeiidTranslatorMessage", translatorName, mr.getItemName())); //$NON-NLS-1$
        			if( !result ) {
        				return;
        			}
        		}
        		
        	}
        }
        
        if(! validateResultDisplayProperties()) {
        	return;
        }
        
        final PreviewManager previewManager = getServerManager().getPreviewManager();
        assert (previewManager != null) : "PreviewManager is null"; //$NON-NLS-1$

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                try {
                    previewManager.previewSetup(eObject, monitor);
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
            }
        };


        // show dialog
        try {
            dialog.run(true, true, runnable);
        } catch (InterruptedException e) {
            // canceled by user
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException)e).getTargetException();
            }
            DqpUiConstants.UTIL.log(e);
            MessageDialog.openError(getShell(),
                                    null,
                                    getString("error_in_execution")); //$NON-NLS-1$
        }

        if (dialog.getReturnCode() == Window.OK) {
            // setup successful so run preview
            try {
                internalRun(eObject, planOnly);
            } catch (Exception e) {
                DqpUiConstants.UTIL.log(e);
                MessageDialog.openError(getShell(),
                                        null,
                                        getString("error_in_execution")); //$NON-NLS-1$
            }
        }
    }
    
    /**
     * Open the launch configuration dialog, passing in the current workbench selection.
     * 
     * @throws ModelWorkspaceException
     */
    private void internalRun( final EObject eObject,
                              final boolean planOnly ) throws ModelWorkspaceException {
        String sql = null;
        List<String> paramValues = null;
        final Shell shell = getShell();
        
        boolean isXML = false;
        
    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);

    	IProject project = mr.getCorrespondingResource().getProject();

    	List accessPatternsColumns = null;
    	if (SqlAspectHelper.isTable(eObject)) {
    		SqlTableAspect tableAspect = (SqlTableAspect)SqlAspectHelper.getSqlAspect(eObject);
    		Collection accessPatterns = tableAspect.getAccessPatterns(eObject);

    		if (accessPatterns != null && !accessPatterns.isEmpty()) {
    			// first need to type the collection since dialog requires typed collection
    			List<EObject> patterns = new ArrayList<EObject>(accessPatterns.size());

    			for (Object pattern : accessPatterns) {
    				patterns.add((EObject)pattern);
    			}

    			AccessPatternColumnsDialog dialog = new AccessPatternColumnsDialog(shell, patterns);

    			if (dialog.open() == Window.OK) {
    				accessPatternsColumns = dialog.getColumns();
    				paramValues = dialog.getColumnValues();
    			} else {
    				return;
    			}
    		} else {
    			paramValues = Collections.emptyList();
    		}
    	}

    	if (eObject instanceof Operation) {
    		List<EObject> inputElements = WebServiceUtil.getInputElements((Operation)eObject, false);

    		if (!inputElements.isEmpty()) {
    			ParameterInputDialog dialog = getInputDialog(inputElements);
    			dialog.open();

    			if (dialog.getReturnCode() == Window.OK) {
    				paramValues = dialog.getParameterValues();
    				sql = WebServiceUtil.getSql((Operation)eObject, paramValues);
    				paramValues = Collections.emptyList(); // no need to pass these to the executor
    			} else {
    				return;
    			}
    		} else {
    			paramValues = Collections.emptyList();
    			sql = WebServiceUtil.getSql((Operation)eObject, paramValues);
    		}
    		isXML = true;
    	} else if (SqlAspectHelper.isProcedure(eObject)) {
    		SqlProcedureAspect procAspect = (SqlProcedureAspect)SqlAspectHelper.getSqlAspect(eObject);
    		List<EObject> params = procAspect.getParameters(eObject);
            // create list - (only the IN and IN/OUT parameters)
            List<EObject> inParams = new ArrayList<EObject>();
            for (EObject param : params) {
                if (param instanceof ProcedureParameter) {
                    DirectionKind direction = ((ProcedureParameter)param).getDirection();
                    int directionKind = direction.getValue();
                    if (directionKind == DirectionKind.IN || directionKind == DirectionKind.INOUT) {
                        inParams.add(param);
                    }
                }
            }

            if (!inParams.isEmpty()) {
                ParameterInputDialog dialog = getInputDialog(inParams);
    			dialog.open();
    			if (dialog.getReturnCode() == Window.OK) {
    				paramValues = dialog.getParameterValues();
    			} else {
    				return;
    			}
    		} else {
    			paramValues = Collections.emptyList();
    		}
    	}

    	assert (paramValues != null);

    	final Object[] paramValuesAsArray = paramValues.toArray();

    	if (sql == null) {
    		sql = ModelObjectUtilities.getSQL(eObject, paramValuesAsArray, accessPatternsColumns);
    		sql = insertParameterValuesSQL(sql, paramValues);
    	}



    	if (sql != null) {
    		// use the Admin API to get the location of the client jar
    		String driverPath = Admin.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    		try {

    			// TODO: All of these values should be taken from the deployed
    			// pvdb. I have hardcoded them here so that I can test with
    			// a fully depolyed VDB, see the Teiid download for this VDB.
//    			String username = "admin"; //$NON-NLS-1$
//    			String password = "teiid"; //$NON-NLS-1$
    			String vdbName = PreviewManager.getPreviewProjectVdbName(project);
    			if (vdbName.endsWith(TeiidVdb.VDB_DOT_EXTENSION)) {
    				vdbName = vdbName.substring(0, vdbName.length() - 4);
    			}
//    			String currentDefaultServerURL = DqpPlugin.getInstance().getServerManager().getDefaultServer().getUrl();
    			TeiidJdbcInfo jdbcInfo = new TeiidJdbcInfo(vdbName, getServerManager().getDefaultServer().getTeiidJdbcInfo());
    			
//    			String connectionURL = jdbcInfo.getURL(); //"jdbc:teiid:" + vdbName + "@" + currentDefaultServerURL; //$NON-NLS-1$ //$NON-NLS-2$

    			// Note that this is a Transient profile, it is not visible in
    			// the
    			// UI and goes away when it is garbage collected.
    			IConnectionProfile profile = ConnectivityUtil.createTransientTeiidProfile(driverPath,
    					jdbcInfo.getUrl(),
    					jdbcInfo.getUsername(),
    					jdbcInfo.getPassword(),
    					vdbName);

    			final Connection sqlConnection;
    			IConnection connection = getSqlConnection(profile);
    			sqlConnection = (Connection) connection.getRawConnection();
    			if (null == sqlConnection || sqlConnection.isClosed()) {
    				final Throwable e = connection.getConnectException();
    				if(null != e) {
    					DqpUiConstants.UTIL.log(e);
    				} else {
    					DqpUiConstants.UTIL.log("Unspecified connection error"); //$NON-NLS-1$
    				}
  					MessageDialog.openError(getShell(),
							getString("error_getting_connection.title"), //$NON-NLS-1$
							getString("error_getting_connection.message")); //$NON-NLS-1$
  					return;
    			}

    			DatabaseIdentifier ID = new DatabaseIdentifier(profile.getName(), vdbName);
    			ILaunchConfigurationWorkingCopy config = creatLaunchConfig(sql, ID);

    			// This runnable executes the SQL and displays the results
				// in the DTP 'SQL Results' view.
				SimpleSQLResultRunnable runnable = null;

                if (planOnly) {
                    String planStr = getExecutionPlan(sqlConnection, sql);
                    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    IViewPart viewPart = null;
                    try {
                        if (window != null) {
                            viewPart = window.getActivePage().showView(ExecutionPlanView.VIEW_ID);
                            if (viewPart instanceof ExecutionPlanView) {
                                String labelStr = getString("planOnlyFetchFor.label") + " " + ModelerCore.getModelEditor().getName(eObject); //$NON-NLS-1$ //$NON-NLS-2$                               
                                ((ExecutionPlanView)viewPart).updateContents(labelStr, sql, planStr);
                            }
                        }
                    } catch (PartInitException e) {
                        DqpUiConstants.UTIL.log(e);
                        WidgetUtil.showError(e.getLocalizedMessage());
                    }
                } else if (isXML) {
                    String labelStr = getString("previewWithPlanFor.label") + " " + ModelerCore.getModelEditor().getName(eObject); //$NON-NLS-1$ //$NON-NLS-2$                               
                    runnable = new TeiidAdHocScriptRunnable(sqlConnection, labelStr, sql, true, null, new NullProgressMonitor(),
                                                            ID, config);
                    BusyIndicator.showWhile(null, runnable);
                } else {
                    String labelStr = getString("previewWithPlanFor.label") + " " + ModelerCore.getModelEditor().getName(eObject); //$NON-NLS-1$ //$NON-NLS-2$                               
                    runnable = new TeiidAdHocScriptRunnable(sqlConnection, labelStr, sql, true, null, new NullProgressMonitor(),
                                                            ID, config);
                    // runnable = new SimpleSQLResultRunnable(sqlConnection, sql, true, null, new NullProgressMonitor(), ID,
                    // config);
                    BusyIndicator.showWhile(null, runnable);
                }

				sqlConnection.close();
				ConnectivityUtil.deleteTransientTeiidProfile(profile);
    		} catch (CoreException e) {
    			DqpUiConstants.UTIL.log(IStatus.ERROR, e.getMessage());
    		} catch (SQLException e) {
    			DqpUiConstants.UTIL.log(IStatus.ERROR, e.getMessage());
    		}
    	} else {
    		DqpUiConstants.UTIL.log(new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK,
    				"failed to produce valid SQL to execute", null)); //$NON-NLS-1$
    	}
    }
    
    private String getExecutionPlan( Connection sqlConnection,
                                     String sql ) {
        String executionPlan = null;
        try {
            Statement stmt = sqlConnection.createStatement();
            stmt.execute("SET SHOWPLAN DEBUG"); //$NON-NLS-1$
            stmt.executeQuery(sql);
            ResultSet planRs = stmt.executeQuery("SHOW PLAN"); //$NON-NLS-1$
            planRs.next();
            executionPlan = planRs.getString("PLAN_XML"); //$NON-NLS-1$
        } catch (SQLException e) {
            // Error Generating the plan. Log the error and display dialog.
            DqpUiConstants.UTIL.log(e);
            MessageDialog.openError(getShell(), getString("errorGeneratingExecutionPlan.title"), //$NON-NLS-1$
                                    getString("errorGeneratingExecutionPlan.message")); //$NON-NLS-1$
        }
        return executionPlan;
    }

    private ParameterInputDialog getInputDialog( List<EObject> params ) {
        ParameterInputDialog dialog = new ParameterInputDialog(getShell(), params);
        return dialog;
    }
    
	private String insertParameterValuesSQL(String sql, List<String> paramValues) {
		if( paramValues != null && !paramValues.isEmpty() ) {
			for (String value : paramValues) {
                // If value is null, replace with the word null
                if (value == null) {
                    sql = sql.replaceFirst("\\?", "null"); //$NON-NLS-1$ //$NON-NLS-2$ 
                    // Escaped literal - no quotes
                } else if (value.trim().startsWith("{") && value.trim().endsWith("}")) { //$NON-NLS-1$ //$NON-NLS-2$ 
                    sql = sql.replaceFirst("\\?", value); //$NON-NLS-1$
                    // Non-null, replace with quoted value
                } else {
                    sql = sql.replaceFirst("\\?", "'" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
			}
		}
		return sql;
	}
	
    public ServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }
    
    /**
     * @param sql
     * @param ID
     * @return
     * @throws CoreException
     */
    private ILaunchConfigurationWorkingCopy creatLaunchConfig( String sql,
                                                               DatabaseIdentifier ID ) throws CoreException {
        ILaunchConfigurationWorkingCopy config = LaunchHelper.createExternalClientConfiguration(ID, "pvdb"); //$NON-NLS-1$
        config.setAttribute(RoutineLaunchConfigurationAttribute.ROUTINE_LAUNCH_SQL, sql);
        // ROUTINE_LAUNCH_TYPE 3 is ad-hoc SQL
        config.setAttribute(RoutineLaunchConfigurationAttribute.ROUTINE_LAUNCH_TYPE, 3);
        return config;
    }
    

    private IConnection getSqlConnection( IConnectionProfile profile ) {
        IConnectionProfileProvider provider = profile.getProvider();
        IConnectionFactoryProvider factory = provider.getConnectionFactory("java.sql.Connection"); //$NON-NLS-1$
        final String factoryId = factory.getId();

        return profile.createConnection(factoryId);
    }
    
	private boolean validateResultDisplayProperties() {
		
		IPreferenceStore store = ResultsViewUIPlugin.getDefault().getPreferenceStore();
		if(!store.getBoolean("org.eclipse.datatools.sqltools.result.ResultsFilterDialog.unknownProfile")){ //$NON-NLS-1$
			boolean isOK = MessageDialog.openQuestion(getShell(), 
					getString("propertyPromptTitle"),  //$NON-NLS-1$
					getString("propertyPrompt")); //$NON-NLS-1$
			
			if( isOK ) {
				store.setValue("org.eclipse.datatools.sqltools.result.ResultsFilterDialog.unknownProfile", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			
			return false;
		}
	    return true;
	}

    class PreviewUnavailableDialog extends MessageDialog {
        boolean openProgressView = false;

        public PreviewUnavailableDialog( Shell parent ) {
            super(parent, getString("previewUnavailableDialog.title"), null, //$NON-NLS-1$
                    getString("previewUnavailableDialog.message"), //$NON-NLS-1$
                    MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0);
            setShellStyle(getShellStyle() | SWT.RESIZE);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createCustomArea( Composite parent ) {
            Composite panel = new Composite(parent, SWT.NONE);
            panel.setLayout(new GridLayout(2, false));
            panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Button btnOpenProgressView = new Button(panel, SWT.CHECK);
            btnOpenProgressView.setText(getString("previewUnavailableDialog.btnShowProgressView.text")); //$NON-NLS-1$
            btnOpenProgressView.setToolTipText(getString("previewUnavailableDialog.btnShowProgressView.toolTip")); //$NON-NLS-1$
            btnOpenProgressView.addSelectionListener(new SelectionAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    setOpenProgressView(((Button)e.widget).getSelection());
                }
            });

            return panel;
        }

        void setOpenProgressView( boolean openProgressView ) {
            this.openProgressView = openProgressView;
        }

        public boolean shouldOpenProgressView() {
            return this.openProgressView;
        }
    }

}
