/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui.workspace;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.admin.api.embedded.EmbeddedAdmin;
import com.metamatrix.admin.api.embedded.EmbeddedLogger;
import com.metamatrix.admin.api.exception.AdminException;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.jdbc.EmbeddedDriver;
import com.metamatrix.jdbc.api.Connection;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.vdb.edit.loader.VDBConstants;

/**
 * A factory class to create connections to the embedded query from the Designer.
 */
public class QueryClient {

    private Connection adminConnection;

    protected Connection getAdminConnection() throws SQLException {
        if (this.adminConnection == null) {
            ClassLoader current = Thread.currentThread().getContextClassLoader();
            try {
                File propertiesDir = DqpPath.getRuntimePath().toFile();
                String url = buildConnectionURL(propertiesDir.getAbsolutePath(), "admin", "1", new Properties()); //$NON-NLS-1$ //$NON-NLS-2$ 
                DqpPlugin.Util.log(IStatus.INFO, "starting workspace execution with url = \"" + url); //$NON-NLS-1$ 
                EmbeddedDriver driver = new EmbeddedDriver();

                Thread.currentThread().setContextClassLoader(driver.getClass().getClassLoader());

                this.adminConnection = (com.metamatrix.jdbc.api.Connection)driver.connect(url, null);

                // wire the logging so that the messages are flown into designer
                ((EmbeddedAdmin)this.adminConnection.getAdminAPI()).setLogListener(new DesignerLogger());
            } catch (AdminException e) {
                throw new RuntimeException(e);
            } finally {
                Thread.currentThread().setContextClassLoader(current);
            }
        }
        return this.adminConnection;
    }

    /**
     * Obtain a string version of a URL suitable for the EmbeddedDriver.
     * 
     * @param theVdbName the VDB name
     * @param theVersion the VDB version
     * @param propsFile the path (including file name) of the DQP properties file
     * @return
     */
    public String buildConnectionURL( String executionDir,
                                      String theVdbName,
                                      String theVersion,
                                      Properties executionProps ) {
        String txnAutoWrap = executionProps.getProperty(VDBConstants.VDBElementNames.ExecutionProperties.Properties.TXN_AUTO_WRAP);

        File propsFile = new File(executionDir, "workspace.properties"); //$NON-NLS-1$

        StringBuffer sb = new StringBuffer().append("jdbc:metamatrix:") //$NON-NLS-1$
        .append(theVdbName).append('@').append("mmrofile:") //$NON-NLS-1$
        .append(StringUtil.replaceAll(propsFile.getAbsolutePath(), "\\", "/")) //$NON-NLS-1$ //$NON-NLS-2$
        .append(";version=") //$NON-NLS-1$
        .append(theVersion).append(";XMLFormat=Tree;"); //$NON-NLS-1$

        if (SQLExplorerPlugin.getDefault() != null && SQLExplorerPlugin.getDefault().shouldShowQueryPlan()) {
            sb.append("sqlOptions").append("=").append("SHOWPLAN;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        sb.append("EmbeddedContext").append("=").append("Designer;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        //sb.append("metamatrix.security.password.PasswordKeyStore").append("=").append(DqpPlugin.getInstance().getDqpKey()); //$NON-NLS-1$ //$NON-NLS-2$

        if (txnAutoWrap != null) {
            sb.append("txnAutoWrap").append("=").append(txnAutoWrap).append(";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        return sb.toString();
    }

    static class DesignerLogger implements EmbeddedLogger {

        @Override
        public void log( int logLevel,
                         long timestamp,
                         String componentName,
                         String threadName,
                         String message,
                         Throwable throwable ) {
            switch (logLevel) {
                case NONE:
                    break;
                case CRITICAL:
                case ERROR:
                    DqpUiConstants.UTIL.log(new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, message, throwable));
                    break;
                case WARNING:
                    DqpUiConstants.UTIL.log(new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK, message, throwable));
                    break;
                case INFO:
                case DETAIL:
                case TRACE:
                    DqpUiConstants.UTIL.log(new Status(IStatus.INFO, DqpUiConstants.PLUGIN_ID, IStatus.OK, message, throwable));
                    break;
            }
        }
    }
}
