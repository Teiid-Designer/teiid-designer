/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionFactoryProvider;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IConnectionProfileProvider;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
import org.eclipse.datatools.sqltools.core.profile.NoSuchProfileException;
import org.eclipse.datatools.sqltools.routineeditor.launching.LaunchHelper;
import org.eclipse.datatools.sqltools.routineeditor.launching.RoutineLaunchConfigurationAttribute;
import org.eclipse.datatools.sqltools.routineeditor.result.CallableSQLResultRunnable;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.ui.actions.SetConnectionProfileAction;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.preview.PreviewManager;

import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants.Preferences;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.AccessPatternColumnsDialog;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.ParameterInputDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 5.0
 */
public class PreviewTableDataContextAction extends SortableSelectionAction {

	private ConnectionInfoHelper connectionHelper;
    /**
     * @since 5.0
     */
    public PreviewTableDataContextAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.PREVIEW_DATA_ICON));
        setWiredForSelection(true);
        setToolTipText(DqpUiConstants.UTIL.getString("PreviewTableDataContextAction.tooltip")); //$NON-NLS-1$
        
        this.connectionHelper = new ConnectionInfoHelper();
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
        
        // first must have a Teiid to run preview on
        if (DqpPlugin.getInstance().getServerManager().getDefaultServer() == null) return false;
    	
        // must have one and only one EObject selected
        EObject eObj = SelectionUtilities.getSelectedEObject(selection);
        if (eObj == null) return false;

        // eObj must be previewable
        return ModelObjectUtilities.isExecutable(eObj);
    }

    private boolean connectionInfoExists( EObject obj ) {
        Set<IResource> depModels = new HashSet<IResource>();
        try {
        	depModels = ModelObjectUtilities.getDependentPhysicalModels(obj);
        } catch (ModelWorkspaceException e) {
            String msg = DqpUiConstants.UTIL.getString("PreviewTableDataContextAction.errorGettingDependentPhysicalSources", obj); //$NON-NLS-1$
            DqpUiConstants.UTIL.log(IStatus.ERROR, e, msg);
            return false;
        }
        
        for( IResource iResource : depModels ) {
        	ModelResource mr = ModelUtilities.getModelResourceForIFile((IFile)iResource, true);
        		
    		if( ! this.connectionHelper.hasConnectionInfo(mr) ) {
        		boolean doContinue = SetConnectionProfileAction.setConnectionProfile((IFile)iResource);
        		if( !doContinue ) {
        			// User canceled
        			return false;
        		}
    		}
        }
        
        return true;
    }
	
    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
      EObject eObj = SelectionUtilities.getSelectedEObject(getSelection());
//    
//    if( connectionInfoExists(selected) ) {
//        internalRun();
//    }

        try {
            // TODO need a progress monitor
            Job job = DqpPlugin.getInstance().getServerManager().getPreviewManager().previewSetup(eObj, null);
            IProgressService progressService = PlatformUI.getWorkbench().getProgressService(); 
            progressService.showInDialog(getShell(), job);
            
            // setup successful so run preview
            internalRun();
        } catch (Exception e) {
            // TODO show error message dialog
        	MessageDialog.openError(getShell(), "Preview Data Error", e.getMessage()); //$NON-NLS-1$
        	System.out.print(e);
        	return;
        }
        
        
    }

    /**
     *
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
    }

    /**
     * Open the launch configuration dialog, passing in the current workbench selection.
     * @throws ModelWorkspaceException 
     */
    private void internalRun() throws ModelWorkspaceException {
        String sql = null;
        List<String> paramValues = null;
        final Shell shell = getShell();
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

        @SuppressWarnings("unused")
		final int rowLimit = getCurrentRowLimit();
        final Object[] paramValuesAsArray = paramValues.toArray();

        String displaySQL = sql;
        if (sql == null) {
            sql = ModelObjectUtilities.getSQL(selected, paramValuesAsArray, accessPatternsColumns);
            displaySQL = getDisplaySQL(sql, paramValues);
        }

        if (sql != null) {


			String driverPath = getClientJarPath();
			try {

				// TODO: All of these values should be taken from the deployed
				// pvdb. I have hardcoded them here so that I can test with
				// a fully depolyed VDB, see the Teiid download for this VDB.
				String username = PreviewManager.DEFAULT_USERNAME;
				String password = PreviewManager.DEFAULT_PASSWORD;
				String vdbName = PreviewManager.getPreviewProjectVdbName(project);
				if( vdbName.endsWith(TeiidVdb.VDB_DOT_EXTENSION)) {
					vdbName = vdbName.substring(0, vdbName.length()-4);
				}
				String currentDefaultServerURL = DqpPlugin.getInstance().getServerManager().getDefaultServer().getUrl();
				String connectionURL = "jdbc:teiid:" + vdbName + "@" + currentDefaultServerURL; //$NON-NLS-1$ //$NON-NLS-2$

				// Note that this is a Transient profile, it is not visible in
				// the
				// UI and goes away when it is garbage collected.
				IConnectionProfile profile = ConnectivityUtil
						.createTransientTeiidProfile(driverPath, connectionURL,
								username, password, vdbName);

        		final Connection sqlConnection = getSqlConnection(profile);

        		DatabaseIdentifier ID = new DatabaseIdentifier(profile
						.getName(), vdbName);
				ILaunchConfigurationWorkingCopy config = creatLaunchConfig(sql,
						ID);

        		try {
					// This runnable executes the SQL and displays the results
					// in the DTP 'SQL Results' view.
					CallableSQLResultRunnable runnable = new CallableSQLResultRunnable(
							sqlConnection, config, false, null, ID);
					final IWorkbenchWindow iww = DqpUiPlugin.getDefault()
							.getCurrentWorkbenchWindow();
					iww.getShell().getDisplay().asyncExec(runnable);

        		} catch (SQLException e) {
					// TODO: Handle this
					System.out.print(e);
				} catch (NoSuchProfileException e) {
					// TODO: Handle this
					System.out.print(e);
				}
			} catch (CoreException e) {
				// TODO: Handle this
				System.out.print(e);
			} catch (SQLException e) {
				// TODO: Handle this
				System.out.print(e);
			}

        	// TODO:  REPLACE DIALOG WITH NEW PREVIEW DATA LOGIC
			//MessageDialog.openInformation(shell, "Preview Data Action Results Pending",  //$NON-NLS-1$
			//		"Note that this feature is being re-architected and not yet available. " +  //$NON-NLS-1$
			//		"\n\nThank you for your patience." + "\n\n Eventually the following query will be executed for you:\n\n" + displaySQL);  //$NON-NLS-1$//$NON-NLS-2$
        	
//            class QueryExecutor implements IRunnableWithProgress {
//                private final QueryMetadataInterface qmi;
//                private final String sql;
//                private final String displaySql;
//
//                public QueryExecutor( EObject previewObject,
//                                      String sql,
//                                      String displaySql ) {
//                    this.qmi = WorkspaceExecutionUtil.getMetadata(previewObject);
//                    this.sql = sql;
//                    this.displaySql = displaySql;
//                }
//
//                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
//                    try {
//                        WorkspaceExecutor.getInstance().executeSQL(this.qmi,
//                                                                   this.sql,
//                                                                   paramValuesAsArray,
//                                                                   this.displaySql,
//                                                                   rowLimit,
//                                                                   monitor);
//                    } catch (SQLException e) {
//                        throw new InvocationTargetException(e);
//                    }
//                }
//            }
//
//            ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell) {
//                @Override
//                protected void cancelPressed() {
//                    super.cancelPressed();
//
//                    try {
//                        WorkspaceExecutor.getInstance().cancel();
//                    } catch (SQLException e) {
//                        DqpUiConstants.UTIL.log(e);
//                    }
//                }
//            };
//
//            QueryExecutor op = new QueryExecutor(selected, sql, displaySQL);
//
//            try {
//                dialog.run(true, true, op);
//
//                if (!dialog.getProgressMonitor().isCanceled()) {
//                    showResults(WorkspaceExecutor.getInstance().getResults());
//                }
//            } catch (InvocationTargetException e) {
//                if (!dialog.getProgressMonitor().isCanceled()) {
//                    String msg = e.getTargetException().getMessage();
//                    DqpUiConstants.UTIL.log(new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, e.getTargetException()));
//                    MessageDialog.openError(shell, DqpUiConstants.UTIL.getString("PreviewTableDataAction.error_in_execution"), msg); //$NON-NLS-1$
//                }
//            } catch (InterruptedException e) {
//                // dialog was canceled
//            } finally {
//                dialog.getProgressMonitor().done();
//            }
        } else {
            DqpUiConstants.UTIL.log(new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK,
                                            "failed to produce valid SQL to execute", null)); //$NON-NLS-1$
        }
    }

	/**
	 * @param sql
	 * @param ID
	 * @return
	 * @throws CoreException
	 */
	private ILaunchConfigurationWorkingCopy creatLaunchConfig(String sql,
			DatabaseIdentifier ID) throws CoreException {
		ILaunchConfigurationWorkingCopy config = LaunchHelper
				.createExternalClientConfiguration(ID, "pvdb"); //$NON-NLS-1$
		config.setAttribute(
				RoutineLaunchConfigurationAttribute.ROUTINE_LAUNCH_SQL, sql);
		// ROUTINE_LAUNCH_TYPE 3 is ad-hoc SQL
		config.setAttribute(
				RoutineLaunchConfigurationAttribute.ROUTINE_LAUNCH_TYPE, 3);
		return config;
	}

	/**
	 * @param profile
	 * @return
	 * @throws SQLException
	 * @throws JdbcException
	 */
	private Connection getSqlConnection(IConnectionProfile profile)
			throws SQLException, JdbcException {
		IConnectionProfileProvider provider = profile.getProvider();
		IConnectionFactoryProvider factory = provider
				.getConnectionFactory("java.sql.Connection"); //$NON-NLS-1$
		final String factoryId = factory.getId();

		final IConnection connection = profile.createConnection(factoryId);

		final Connection sqlConnection = (Connection) connection
				.getRawConnection();
		if (null == sqlConnection || sqlConnection.isClosed()) {
			final Throwable e = connection.getConnectException();
			throw new JdbcException(e == null
					? "Unspecified connection error" //$NON-NLS-1$
					: e.getMessage());
		}
		return sqlConnection;
	}

	/**
	 * @return
	 * @throws Error
	 */
	private String getClientJarPath() throws Error {
		// We could have a predefined driver created around the jars we
		// deliver in the dqp plugin, then we could use that driver to
		// assist in the creation of the Profile. That driver would be
		// visible to the users though, unlike the transient ConnProfiles.

		IPath jarPath;
		try {
			// TODO: has to be a better way to do this, this breaks with
			// every API change
			jarPath = DqpPath.getInstallLibPath().addTrailingSeparator()
					.append("teiid-7.1.0-SNAPSHOT-client.jar");
		} catch (IOException e) {
			throw new Error(e);
		}
		// String driverPath = jarPath.toOSString();
		String driverPath = "/home/blafond/TeiidDesigner/trunk/plugins/teiid_embedded_query/teiid-7.1.0-SNAPSHOT-client.jar";
		return driverPath;
	}

    private ParameterInputDialog getInputDialog( List<EObject> params ) {
        ParameterInputDialog dialog = new ParameterInputDialog(getShell(), params);
        return dialog;
    }

    private String getDisplaySQL( String sql,
                                  List<String> paramValues ) {
        if (paramValues != null && !paramValues.isEmpty()) {
            for (String value : paramValues) {
                // skip over null values as those don't have a ? to replace
                if (value != null) {
                    sql = sql.replaceFirst("\\?", "'" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }
        }
        return sql;
    }

    private int getCurrentRowLimit() {
        IPreferenceStore prefStore = DqpUiPlugin.getDefault().getPreferenceStore();
        int rowLimit = 10;
        // initialize
        rowLimit = prefStore.getInt(Preferences.ID_PREVIEW_ROW_LIMIT);

        if (rowLimit < 1) {
            rowLimit = prefStore.getDefaultInt(Preferences.ID_PREVIEW_ROW_LIMIT);
        }

        return rowLimit;
    }

    /**
     * Show the specified results in the results view.
     * 
     * @param theResults the results being displayed
     * @since 5.5.3
     */
    @SuppressWarnings("unused")
	private void showResults( final IResults theResults ) {
        // REPLACE
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

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
