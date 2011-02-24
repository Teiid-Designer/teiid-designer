/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.io.File;
import java.sql.Connection;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 *
 */
public final class ExecutionManager {
    public static final String TXN_AUTO_WRAP = "txnAutoWrap"; //$NON-NLS-1$

    private Connection adminConnection;

    /**
     * Obtain a string version of a URL suitable for the EmbeddedDriver.
     * 
     * @param vdbName the VDB name
     * @param version the VDB version
     * @param propsFile the path (including file name) of the DQP properties file
     * @return
     */
    public String buildConnectionURL( final String executionDir,
                                      final String vdbName,
                                      final String version,
                                      final Properties executionProps ) {
        final String txnAutoWrap = executionProps.getProperty(TXN_AUTO_WRAP);
        final File propsFile = new File(executionDir, "workspace.properties"); //$NON-NLS-1$

        final StringBuffer sb = new StringBuffer().append("jdbc:metamatrix:") //$NON-NLS-1$
        .append(vdbName).append('@').append("mmrofile:") //$NON-NLS-1$
        .append(CoreStringUtil.replaceAll(propsFile.getAbsolutePath(), "\\", "/")) //$NON-NLS-1$ //$NON-NLS-2$
        .append(";version=") //$NON-NLS-1$
        .append(version).append(";XMLFormat=Tree;"); //$NON-NLS-1$
        sb.append("EmbeddedContext").append("=").append("Designer;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        if (txnAutoWrap != null) {
            sb.append("txnAutoWrap").append("=").append(txnAutoWrap).append(";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        return sb.toString();
    }

    protected Connection getAdminConnection() {
        // TODO: FIX
        if (this.adminConnection == null) {
            final ClassLoader current = Thread.currentThread().getContextClassLoader();
            try {
                final File propertiesDir = DqpPlugin.getInstance().getRuntimePath().toFile();
                final String url = buildConnectionURL(propertiesDir.getAbsolutePath(), "admin", "1", new Properties()); //$NON-NLS-1$ //$NON-NLS-2$ 
                DqpPlugin.Util.log(IStatus.INFO, "starting workspace execution with url = \"" + url); //$NON-NLS-1$ 
                // EmbeddedDriver driver = new EmbeddedDriver();

                // Thread.currentThread().setContextClassLoader(driver.getClass().getClassLoader());

                final Properties props = new Properties();
                props.setProperty("user", "admin"); //$NON-NLS-1$ //$NON-NLS-2$
                props.setProperty("password", "teiid"); //$NON-NLS-1$ //$NON-NLS-2$
                // this.adminConnection = (com.metamatrix.jdbc.api.Connection)driver.connect(url, props);
            } finally {
                Thread.currentThread().setContextClassLoader(current);
            }
        }
        return this.adminConnection;
    }

}
