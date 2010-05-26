/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.io.File;
import net.sourceforge.sqlexplorer.AliasModel;
import net.sourceforge.sqlexplorer.DriverModel;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.IdentifierFactory;
import net.sourceforge.sqlexplorer.LoggingProgress;
import net.sourceforge.sqlexplorer.RetrievingTableDataProgress;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.DBView;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.workspace.QueryClient;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * This component is used to execute the VDB that has a well defined VDB definition file.
 * 
 * @since 4.3
 */
public class VdbExecutor extends QueryClient implements DqpUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbExecutor.class);
    private static final String DQP_DRIVER_CLASS = "com.metamatrix.jdbc.EmbeddedDriver"; //$NON-NLS-1$
    private static final String DQP_DRIVER_NAME = "MetaBase Modeler DQP Driver"; //$NON-NLS-1$
    private static final String DQP_SAMPLE_URL = "jdbc:metamatrix:<vdbName>@<propertiesFile>;version=<vdbVersion>"; //$NON-NLS-1$

    /**
     * Status code returned from the execute method when a license problem exists.
     */
    public static final int LICENSE_PROBLEM_CODE = 30160037; // see com.metatmatrix.license.util.ErrorMessageKeys
    private static final IStatus SUCCESS = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, getString("executionSuccess"), null); //$NON-NLS-1$

    private static String getString( final String theId ) {
        return UTIL.getStringOrKey(I18N_PREFIX + theId);
    }

    private final Vdb vdb;
    private final VdbExecutionValidator validator;

    private SQLConnection sqlConnection;

    private long timestamp; // the modification date of the VDB

    /**
     * VdbExecutor Constructor
     * 
     * @param vdbFile
     * @since 4.3
     */
    public VdbExecutor( final Vdb vdb,
                        final VdbExecutionValidator validator ) {
        CoreArgCheck.isNotNull(vdb);
        CoreArgCheck.isNotNull(validator);
        this.vdb = vdb;
        this.validator = validator;
        // initialize the VDB
        init();
    }

    /**
     * Check if the VDB can be executed, this runs the VdbExecutionValidator on the given VDB.
     * 
     * @return The status for executing a VDB.
     * @since 4.3
     */
    public IStatus canExecute() {
        return validator.validateVdb(this.vdb);
    }

    /**
     * Check if the VDB and its models are complete with connector bindings defined, also check the VDB models have any validation
     * problems.
     * 
     * @param defn The VDB definition to validate the models against.
     * @return The status for executing a VDB.
     * @since 4.3
     */
    public IStatus checkVdbModelState() {
        return validator.validateVdbModels(this.vdb);
    }

    public void closeAllConnections() {
        SQLExplorerPlugin.getDefault().closeAllConnections();
    }

    private boolean deployVDB( final String vdbName,
                               final File vdbFile ) {

        // Admin admin = getAdminConnection().getAdminAPI();
        //
        // // remove the old one if there is one.
        // try { final VdbDefnHelper helper = DqpPlugin.getInstance().getVdbDefnHelper(this.vdb);
        // Collection<VDB> vdbs = admin.getVDBs(vdbName + AdminObject.WILDCARD);
        // for (VDB vdb : vdbs) {
        // admin.changeVDBStatus(vdb.getName(), vdb.getVDBVersion(), VDB.DELETED);
        // } // for
        // } catch (AdminException e) {
        //            UTIL.log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, "Failed to undeploy the VDB", null)); //$NON-NLS-1$                
        // }
        //
        // // now add the new one
        // try {
        // admin.addVDB(vdbName, new FileUtil(vdbFile).readBytes(), new AdminOptions(AdminOptions.OnConflict.OVERWRITE));
        // } catch (AdminException e) {
        //            UTIL.log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, "Failed to deploy the VDB = " + vdbName, null)); //$NON-NLS-1$                                
        // }
        // return true;
        return false;
    }

    public IStatus execute( final IProgressMonitor theMonitor ) {
        return execute(theMonitor, false);
    }

    /**
     * Execute the VDB this executor is managing.
     * 
     * @param theMonitor the progress monitor
     * @return the execution status
     * @since 4.3
     */
    public IStatus execute( final IProgressMonitor theMonitor,
                            final boolean initConnOnly ) {
        IStatus result = SUCCESS;

        final SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        final String vdbName = this.vdb.getName().toString();

        final String vdbVersion = "1"; //$NON-NLS-1$

        final File vdbFile = getVdbFile();

        final File executionDir = DqpPath.getRuntimePath().toFile();
        final String url = buildConnectionURL(executionDir.getAbsolutePath(), vdbName, vdbVersion, null);
        ISQLDriver driver = null;

        // ensure that the JDBC Driver for the DQP has been loaded
        try {
            driver = getDqpDriver();
        } catch (final Exception e) {
            UTIL.log(e);
            return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, getString("dqpDriverError"), e); //$NON-NLS-1$
        }

        final String aliasName = vdbFile.getAbsolutePath();

        CoreArgCheck.isNotNull(aliasName);

        ISQLAlias alias = getSqlAlias(aliasName);

        boolean closePrevious = false;

        if (alias == null) {
            // no alias found, need to create one;
            final IdentifierFactory factory = IdentifierFactory.getInstance();
            final AliasModel aliasModel = sqlPlugin.getAliasModel();
            alias = aliasModel.createAlias(factory.createIdentifier());

            try {
                alias.setName(aliasName);
                alias.setDriverIdentifier(driver.getIdentifier());
                alias.setUrl(url);
                alias.setUserName("username"); //$NON-NLS-1$
                alias.setConnectAtStartup(false);
                alias.setAutoLogon(false);
                alias.setPassword("password"); //$NON-NLS-1$
                aliasModel.addAlias(alias);
            } catch (final ValidationException excp) {
                result = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, getString("sqlAliasValidationError"), excp); //$NON-NLS-1$
                UTIL.log(result);
                return result;
            } catch (final DuplicateObjectException excp) {
                result = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, getString("sqlAliasDuplicateError"), excp); //$NON-NLS-1$
                UTIL.log(result);
                return result;
            }
        } else // check to see if the url has changed
        if (!url.equals(alias.getUrl())) {
            closePrevious = true;
            try {
                alias.setUrl(url);
            } catch (final ValidationException excp) {
                UTIL.log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK,
                                    UTIL.getString(I18N_PREFIX + "sqlAliasUrlError", url), excp)); //$NON-NLS-1$
            }
        }

        // If initializing connection only, we can skip this
        if (!initConnOnly) {
            // TODO: second, get the connection bindings and display them to the user

            // third, launch the JFaceDbc perspective
            final IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();

            // page should never be null but check just in case
            if (page == null) {
                result = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, getString("nullWorkbenchPageMsg"), null); //$NON-NLS-1$
                UTIL.log(result);
                return result;
            }

            try {
                final IWorkbenchPage jdbcPage = PlatformUI.getWorkbench().showPerspective(JDBC_CLIENT_PERSPECTIVE_ID,
                                                                                          page.getWorkbenchWindow());
                jdbcPage.showView(JDBC_CONNECTION_VIEW_ID);
            } catch (final Throwable e) {
                result = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, getString("perspectiveErrorMsg"), e); //$NON-NLS-1$
                UTIL.log(result);
                return result;
            }
        }

        // fourth, open a connection to the alias for this VDB

        final DriverModel driverModel = sqlPlugin.getDriverModel();
        final SQLDriverManager driverMgr = sqlPlugin.getSQLDriverManager();
        final ISQLDriver dv = driverModel.getDriver(alias.getDriverIdentifier());
        this.sqlConnection = null;
        boolean deployVDB = true;

        try {
            // use existing connection if possible
            SessionTreeNode sessionNode = findOpenSession(alias);

            if (sessionNode != null) {
                // if VDB has changed since the last time we executed close the existing connection and
                // establish a new one. if the VDB hasn't changed reuse the same connection.
                if (this.timestamp == getVdbFile().lastModified() && !closePrevious) {
                    // reuse existing connection
                    this.sqlConnection = sessionNode.getConnection();
                    deployVDB = false;
                } else {
                    // close existing and create new connection
                    this.timestamp = getVdbFile().lastModified();
                    sessionNode.close();
                    sessionNode = null;
                }
            } else closeAllConnections();

            // since the VDB changed now deploy the new one VDB file
            if (deployVDB) deployVDB(vdbName, vdbFile);

            // create new connection if old connection with right timestamp is not found
            if (this.sqlConnection == null) {
                final LoggingProgress lp = new LoggingProgress(driverMgr, dv, alias, alias.getUserName(), alias.getPassword());
                final ProgressMonitorDialog pg = new ProgressMonitorDialog(null);
                pg.run(true, true, lp);

                if (lp.isOk()) {
                    this.sqlConnection = lp.getConn();
                    this.sqlConnection.setAutoCommit(true);
                } else {
                    final Throwable e = lp.getException();
                    final Object param = (lp.getError() == null) ? e.getClass().getName() : lp.getError();
                    final String message = UTIL.getString(I18N_PREFIX + "connectionErrorMsg", param); //$NON-NLS-1$
                    UTIL.log(result);

                    return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, null);
                }

                // If init connection only, we can skip this
                if (!initConnOnly) {
                    // run query
                    final RetrievingTableDataProgress rtdp = new RetrievingTableDataProgress(this.sqlConnection, alias,
                                                                                             sqlPlugin.stm, alias.getPassword());
                    final ProgressMonitorDialog pg2 = new ProgressMonitorDialog(null);
                    pg2.run(true, true, rtdp);
                }
            } else // If init connection only, we can skip this
            if (!initConnOnly) {
                // just select the appropriate structure view
                final DBView dbView = (DBView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IConstants.Extensions.Views.SQL_DB_VIEW);

                if (dbView != null) dbView.setInput(sessionNode);
            }

        } catch (final Exception e) {
            result = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, getString("connectionErrorMsg"), e); //$NON-NLS-1$
            UTIL.log(result);
            return result;
        }

        return result;
    }

    /**
     * Obtains the connection for the specified alias. The {@link SessionTreeNode} contains information about the connection and
     * alias.
     * 
     * @param theAlias the alias whose connection is being requested
     * @return the connection or <code>null</code>
     */
    private SessionTreeNode findOpenSession( final ISQLAlias theAlias ) {
        return SQLExplorerPlugin.getDefault().stm.findOpenSessionTreeNode(theAlias);
    }

    private ISQLDriver getDqpDriver() throws Exception {
        final DriverModel driverModel = SQLExplorerPlugin.getDefault().getDriverModel();
        final Object[] drivers = driverModel.getElements();
        for (int i = 0; i < drivers.length; ++i) {
            final ISQLDriver driver = (ISQLDriver)drivers[i];
            if (driver.getDriverClassName().equals(DQP_DRIVER_CLASS)) // found it
            return driver;
        }
        // never found our driver, so create it.
        final ISQLDriver driver = driverModel.createDriver(IdentifierFactory.getInstance().createIdentifier());
        driver.setDriverClassName(DQP_DRIVER_CLASS);
        driver.setName(DQP_DRIVER_NAME);
        driver.setUrl(DQP_SAMPLE_URL);
        driverModel.addDriver(driver);
        return driver;
    }

    private ISQLAlias getSqlAlias( final String theName ) {
        // lookup the VDB name in the AliasModel and see if one has been created
        final SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        final AliasModel aliasModel = sqlPlugin.getAliasModel();
        final Object[] aliasArray = aliasModel.getElements();

        for (int i = 0; i < aliasArray.length; ++i) {
            final ISQLAlias aliasElement = (ISQLAlias)aliasArray[i];

            if (theName.equals(aliasElement.getName())) return aliasElement;
        }
        return null;

    }

    public SQLConnection getSqlConnection() {
        return sqlConnection;
    }

    private File getVdbFile() {
        return this.vdb.getName().toFile();
    }

    private void init() {
        try {

            this.timestamp = getVdbFile().lastModified();
        } catch (final Exception e) {
            UTIL.log(IStatus.ERROR, e, UTIL.getString(I18N_PREFIX + "error_vdb_context_loading", getVdbFile().getName())); //$NON-NLS-1$
        }
    }
}
