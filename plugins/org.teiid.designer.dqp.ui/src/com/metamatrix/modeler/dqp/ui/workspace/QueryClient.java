/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui.workspace;

import java.io.File;
import java.sql.Connection;
import java.util.Properties;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * A factory class to create connections to the embedded query from the Designer.
 */
public class QueryClient {
    static final String TXN_AUTO_WRAP = "txnAutoWrap"; //$NON-NLS-1$

    private Connection adminConnection;
    boolean showPlan = false;

    /**
     * Obtain a string version of a URL suitable for the EmbeddedDriver.
     * 
     * @param theVdbName the VDB name
     * @param theVersion the VDB version
     * @param propsFile the path (including file name) of the DQP properties file
     * @return
     */
    public String buildConnectionURL( final String executionDir,
                                      final String theVdbName,
                                      final String theVersion,
                                      final Properties executionProps ) {
        final String txnAutoWrap = executionProps.getProperty(TXN_AUTO_WRAP);

        final File propsFile = new File(executionDir, "workspace.properties"); //$NON-NLS-1$

        final StringBuffer sb = new StringBuffer().append("jdbc:metamatrix:") //$NON-NLS-1$
        .append(theVdbName).append('@').append("mmrofile:") //$NON-NLS-1$
        .append(CoreStringUtil.replaceAll(propsFile.getAbsolutePath(), "\\", "/")) //$NON-NLS-1$ //$NON-NLS-2$
        .append(";version=") //$NON-NLS-1$
        .append(theVersion).append(";XMLFormat=Tree;"); //$NON-NLS-1$

        if (SQLExplorerPlugin.getDefault() != null) {
            showPlan = false;
            UiUtil.runInSwtThread(new Runnable() {
                @Override
                public void run() {
                    showPlan = SQLExplorerPlugin.getDefault().shouldShowQueryPlan();
                }
            }, false);

            if (showPlan) {
                sb.append("sqlOptions").append("=").append("SHOWPLAN;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }

        sb.append("EmbeddedContext").append("=").append("Designer;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        //sb.append("metamatrix.security.password.PasswordKeyStore").append("=").append(DqpPlugin.getInstance().getDqpKey()); //$NON-NLS-1$ //$NON-NLS-2$

        if (txnAutoWrap != null) {
            sb.append("txnAutoWrap").append("=").append(txnAutoWrap).append(";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        return sb.toString();
    }

    //
    // static class DesignerLogger implements EmbeddedLogger {
    //
    // @Override
    // public void log( int logLevel,
    // long timestamp,
    // String componentName,
    // String threadName,
    // String message,
    // Throwable throwable ) {
    // switch (logLevel) {
    // case LogConfiguration.NONE:
    // break;
    // case LogConfiguration.CRITICAL:
    // case LogConfiguration.ERROR:
    // DqpUiConstants.UTIL.log(new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, message, throwable));
    // break;
    // case LogConfiguration.WARNING:
    // DqpUiConstants.UTIL.log(new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK, message, throwable));
    // break;
    // case LogConfiguration.INFO:
    // case LogConfiguration.DETAIL:
    // case LogConfiguration.TRACE:
    // DqpUiConstants.UTIL.log(new Status(IStatus.INFO, DqpUiConstants.PLUGIN_ID, IStatus.OK, message, throwable));
    // break;
    // }
    // }
    // }

    protected Connection getAdminConnection() {
        if (this.adminConnection == null) {
            final ClassLoader current = Thread.currentThread().getContextClassLoader();
            try {
                // File propertiesDir = DqpPath.getRuntimePath().toFile();
                //                String url = buildConnectionURL(propertiesDir.getAbsolutePath(), "admin", "1", new Properties()); //$NON-NLS-1$ //$NON-NLS-2$ 
                //                DqpPlugin.Util.log(IStatus.INFO, "starting workspace execution with url = \"" + url); //$NON-NLS-1$ 
                // EmbeddedDriver driver = new EmbeddedDriver();
                //
                // Thread.currentThread().setContextClassLoader(driver.getClass().getClassLoader());
                //
                // Properties props = new Properties();
                //                props.setProperty("user", "admin"); //$NON-NLS-1$ //$NON-NLS-2$
                //                props.setProperty("password", "teiid"); //$NON-NLS-1$ //$NON-NLS-2$
                // this.adminConnection = (com.metamatrix.jdbc.api.Connection)driver.connect(url, props);
                //
                // // wire the logging so that the messages are flown into designer
                // this.adminConnection.getAdminAPI().setLogListener(new DesignerLogger());
                // } catch (Exception e) {
                // throw new RuntimeException(e);
            } finally {
                Thread.currentThread().setContextClassLoader(current);
            }
        }
        return this.adminConnection;
    }
}
