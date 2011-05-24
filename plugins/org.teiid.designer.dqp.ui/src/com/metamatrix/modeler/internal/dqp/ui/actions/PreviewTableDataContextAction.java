/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.IProgressConstants;
import org.teiid.adminapi.Admin;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.ui.TeiidAdHocScriptRunnable;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.runtime.TeiidJdbcInfo;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.preview.jobs.TeiidPreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.WorkspacePreviewVdbJob;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.AccessPatternColumnsDialog;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.ParameterInputDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class PreviewTableDataContextAction extends SortableSelectionAction {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(PreviewTableDataContextAction.class);
	
    private static String getString( String key ) {
        return DqpUiConstants.UTIL.getString(THIS_CLASS + key);
    }

    static String getString( final String key, final Object param ) {
    	return DqpUiConstants.UTIL.getString(THIS_CLASS + key, param);
	}
    
    static String getString( final String key, final Object param, final Object param2 ) {
    	return DqpUiConstants.UTIL.getString(THIS_CLASS + key, param, param2);
	}
    
    /**
     * @since 5.0
     */
    public PreviewTableDataContextAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.PREVIEW_DATA_ICON));
        setWiredForSelection(true);
        setToolTipText(getString("tooltip")); //$NON-NLS-1$
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

    /**
     * This method was created to allow the transformation.ui plugin, and TransformationObjectEditorPage to get it's own instance
     * of this action so it can allow preview of the specific edited virtual table or procedure. This allows the original action
     * to remain workspace selection driven. Override abstract method
     * 
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#getClone()
     * @since 5.0
     */
    @Override
    public SortableSelectionAction getClone() {
        return new PreviewTableDataContextAction();
    }

    private ParameterInputDialog getInputDialog( List<EObject> params ) {
        ParameterInputDialog dialog = new ParameterInputDialog(getShell(), params);
        return dialog;
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }

    /**
     * @param profile
     * @return
     * @throws SQLException
     * @throws JdbcException
     */
    private IConnection getSqlConnection( IConnectionProfile profile ) throws SQLException, JdbcException {
        IConnectionProfileProvider provider = profile.getProvider();
        IConnectionFactoryProvider factory = provider.getConnectionFactory("java.sql.Connection"); //$NON-NLS-1$
        final String factoryId = factory.getId();

        return profile.createConnection(factoryId);
    }

    /**
     * Open the launch configuration dialog, passing in the current workbench selection.
     * 
     * @throws ModelWorkspaceException
     */
    private void internalRun() throws ModelWorkspaceException {
        String sql = null;
        List<String> paramValues = null;
        final Shell shell = getShell();
        
        boolean isXML = false;
        
    	final EObject selected = SelectionUtilities.getSelectedEObject(getSelection());

    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(selected);

    	IProject project = mr.getCorrespondingResource().getProject();

    	List accessPatternsColumns = null;
    	if (SqlAspectHelper.isTable(selected)) {
    		SqlTableAspect tableAspect = (SqlTableAspect)SqlAspectHelper.getSqlAspect(selected);
    		Collection accessPatterns = tableAspect.getAccessPatterns(selected);

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

    	if (selected instanceof Operation) {
    		List<EObject> inputElements = WebServiceUtil.getInputElements((Operation)selected, false);

    		if (!inputElements.isEmpty()) {
    			ParameterInputDialog dialog = getInputDialog(inputElements);
    			dialog.open();

    			if (dialog.getReturnCode() == Window.OK) {
    				paramValues = dialog.getParameterValues();
    				sql = WebServiceUtil.getSql((Operation)selected, paramValues);
    				paramValues = Collections.emptyList(); // no need to pass these to the executor
    			} else {
    				return;
    			}
    		} else {
    			paramValues = Collections.emptyList();
    			sql = WebServiceUtil.getSql((Operation)selected, paramValues);
    		}
    		isXML = true;
    	} else if (SqlAspectHelper.isProcedure(selected)) {
    		SqlProcedureAspect procAspect = (SqlProcedureAspect)SqlAspectHelper.getSqlAspect(selected);
    		List<EObject> params = procAspect.getParameters(selected);
    		if (params != null && !params.isEmpty()) {
    			ParameterInputDialog dialog = getInputDialog(params);
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
    		sql = ModelObjectUtilities.getSQL(selected, paramValuesAsArray, accessPatternsColumns);
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
    			TeiidJdbcInfo jdbcInfo = new TeiidJdbcInfo(vdbName, DqpPlugin.getInstance().getServerManager().getDefaultServer().getTeiidJdbcInfo());
    			
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
				
				if( isXML ) {
					runnable = new TeiidAdHocScriptRunnable(sqlConnection, sql, true, null, new NullProgressMonitor(), ID, config, null);
				} else {
					runnable = new SimpleSQLResultRunnable(sqlConnection, sql, true, null, new NullProgressMonitor(), ID, config);
				}
				BusyIndicator.showWhile(null, runnable);
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

	private String insertParameterValuesSQL(String sql, List<String> paramValues) {
		if( paramValues != null && !paramValues.isEmpty() ) {
			for (String value : paramValues) {
				// skip over null values as those don't have a ? to replace
				if( value != null ) {
					sql = sql.replaceFirst("\\?", "'" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
		return sql;
	}

    /**
     *
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
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
        // An object can be previewed if it is of a certain object type in a Source/Relational model
        // This is changed from previous releases because the requirement of having a Source binding prior to
        // enablement has changed. Now the binding check is moved to the run() method which performs the check
        // and queries the user for any additional info that's needed to execute the preview, including creating
        // a source binding if necessary.

        // see if preview enabled
        if (!DqpPlugin.getInstance().getServerManager().getPreviewManager().isPreviewEnabled()) return false;

        // first must have a Teiid to run preview on
        if (DqpPlugin.getInstance().getServerManager().getDefaultServer() == null) return false;

        // must have one and only one EObject selected
        EObject eObj = SelectionUtilities.getSelectedEObject(selection);
        if (eObj == null) return false;

        // eObj must be previewable
        return ModelObjectUtilities.isExecutable(eObj);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
    	// Check for unsaved changes
    	// Walk through open ModelEditors and check if "isDirty()"
    	
    	if( !ModelEditorManager.getDirtyResources().isEmpty() ) {
    		boolean doContinue = MessageDialog.openQuestion(getShell(), 
    				getString("unsavedModelsWarning.title"),  //$NON-NLS-1$
    				getString("unsavedModelsWarning.message")); //$NON-NLS-1$
        	if( !doContinue ) {
        		return;
        	}
    	}
    	
    	
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

        final EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
        
        IConnectionInfoHelper helper = new ConnectionInfoHelper();
        ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObj);
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
					tt = DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin().getTranslator(translatorName);
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
        
        final PreviewManager previewManager = DqpPlugin.getInstance().getServerManager().getPreviewManager();
        assert (previewManager != null) : "PreviewManager is null"; //$NON-NLS-1$

        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                try {
                    previewManager.previewSetup(eObj, monitor);
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
                internalRun();
            } catch (Exception e) {
                DqpUiConstants.UTIL.log(e);
                MessageDialog.openError(getShell(),
                                        null,
                                        getString("error_in_execution")); //$NON-NLS-1$
            }
        }
    }

    /**
     * Show the specified results in the results view.
     * 
     * @param theResults the results being displayed
     * @since 5.5.3
     */
    @SuppressWarnings( "unused" )
    private void showResults( final IResults theResults ) {
        // REPLACE
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
