/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.metamatrix.modeler.dqp.DqpPlugin;

public final class DqpPath {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    public static final String PLUGIN_XML = "plugin.xml"; //$NON-NLS-1$

    private static final String DQP_DIR = "dqp";//$NON-NLS-1$ 
    public static final String CONFIGURATION_DIR = "config"; //$NON-NLS-1$
    private static final String EXTENSIONS_DIR = "extensions"; //$NON-NLS-1$
    private static final String CONNECTORS_DIR = "connectors"; //$NON-NLS-1$
    private static final String LIB_DIR = "lib"; //$NON-NLS-1$
    private static final String LOG_DIR = "log"; //$NON-NLS-1$
    private static final String VDB_EXEC_DIR = "vdb-execution"; //$NON-NLS-1$
    private static final String WORKSPACE_DEFN_DIR = "workspaceConfig"; //$NON-NLS-1$
    
    private static final String TEIID_EMBEDDED_BUNDLE_ID = "teiid_embedded_query";  //$NON-NLS-1$

    // ===========================================================================================================================
    // Class Fields
    // ===========================================================================================================================

    private static IPath installPath;

    private static IPath installConfigPath;

    private static IPath installDqpPath;

    private static IPath runtimePath;

    private static IPath runtimeConfigPath;

    private static IPath runtimeConnectorsPath;

    private static IPath logPath;

    public static String testInstallDir;

    public static String testRuntimeDir;

    private static IPath vdbExecutionPath;

    private static IPath workspaceDefnPath;
    
    /*   (Designer Installation Path)/eclipse/plugins/teiid_embedded_query/ */
    private static IPath embeddedInstallPath;
    
    /*   (Designer Installation Path)/eclipse/plugins/teiid_embedded_query/lib */
    private static IPath installLibPath;
    
    /*   (Designer Installation Path)/eclipse/plugins/teiid_embedded_query/extensions */
    private static IPath installExtensionsPath;
    
    private static IPath embeddedExtensionsPath;
    
    private static IPath embeddedLibsPath;
    
    public static String testEmbeddedInstallDir;

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * Ensures the specified directory exists on the file system.
     * 
     * @param dirPath the directory being checked
     */
    private static void createDirectory( IPath dirPath ) {
        File dir = new File(dirPath.toOSString());

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * @return the <code>designer.dqp</code> plugin install path or the test install path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getInstallPath() throws IOException {
        if (installPath == null) {
            if (testInstallDir == null) {
                URL url = FileLocator.find(DqpPlugin.getInstance().getBundle(), new Path(""), null); //$NON-NLS-1$
                url = FileLocator.toFileURL(url);
                installPath = new Path(url.getFile());
            } else {
                installPath = new Path(testInstallDir);
            }
        }

        return (IPath)installPath.clone();
    }

    /**
     * @return the "dqp" directory under the <code>designer.dqp</code> plugin's install path or the test install path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getInstallDqpPath() throws IOException {
        if (installDqpPath == null) {
            installDqpPath = getInstallPath().append(DQP_DIR);
        }

        return (IPath)installDqpPath.clone();
    }

    /**
     * @return the configuration directory under the <code>designer.dqp</code> plugin's install path or the test install path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getInstallConfigPath() throws IOException {
        if (installConfigPath == null) {
            installConfigPath = getInstallDqpPath().append(CONFIGURATION_DIR);
        }

        return (IPath)installConfigPath.clone();
    }

    /**
     * If the log directory does not exist it is created.
     * 
     * @return the log directory under the <code>designer.dqp</code> plugin's runtime path or the test runtime path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getLogPath() {
        if (logPath == null) {
            logPath = getRuntimePath().append(LOG_DIR);
            createDirectory(logPath);
        }

        return (IPath)logPath.clone();
    }

    /**
     * @return the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getRuntimePath() {
        if (runtimePath == null) {
            if (testRuntimeDir == null) {
                runtimePath = DqpPlugin.getInstance().getStateLocation();
            } else {
                runtimePath = new Path(testRuntimeDir);
            }
        }

        return (IPath)runtimePath.clone();
    }

    /**
     * If the configuration directory does not exist it is created.
     * 
     * @return the configuration directory under the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime
     *         path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getRuntimeConfigPath() {
        if (runtimeConfigPath == null) {
            runtimeConfigPath = getRuntimePath().append(CONFIGURATION_DIR);
            createDirectory(runtimeConfigPath);
        }

        return (IPath)runtimeConfigPath.clone();
    }

    /**
     * If the extensions directory does not exist it is created.
     * 
     * @return the extensions directory under the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime
     *         path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getRuntimeConnectorsPath() {
        if (runtimeConnectorsPath == null) {
            runtimeConnectorsPath = getRuntimePath().append(CONNECTORS_DIR);
            createDirectory(runtimeConnectorsPath);
        }

        return (IPath)runtimeConnectorsPath.clone();
    }

    /**
     * If the VDB execution directory does not exist it is created.
     * 
     * @return the VDB execution directory under the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime
     *         path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getVdbExecutionPath() {
        if (vdbExecutionPath == null) {
            vdbExecutionPath = getRuntimePath().append(VDB_EXEC_DIR);
            createDirectory(vdbExecutionPath);
        }

        return (IPath)vdbExecutionPath.clone();
    }

    /**
     * If the worspace DEFN directory does not exist it is created.
     * 
     * @return the directory under the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime path where the
     *         workspace binding information is kept
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getWorkspaceDefnPath() {
        if (workspaceDefnPath == null) {
            workspaceDefnPath = getRuntimePath().append(WORKSPACE_DEFN_DIR);
            createDirectory(workspaceDefnPath);
        }

        return (IPath)workspaceDefnPath.clone();
    }
    
    /**
     * @return the <code>teiid_embedded_query</code> plugin install path or the test install path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getEmbeddedInstallPath() throws IOException {
        if (embeddedInstallPath == null) {
            if (testEmbeddedInstallDir == null) {
                URL url = FileLocator.find(Platform.getBundle(TEIID_EMBEDDED_BUNDLE_ID), new Path(""), null); //$NON-NLS-1$
                url = FileLocator.toFileURL(url);
                embeddedInstallPath = new Path(url.getFile());
            } else {
            	embeddedInstallPath = new Path(testEmbeddedInstallDir);
            }
        }

        return (IPath)embeddedInstallPath.clone();
    }
    
    /**
     * @return the "lib" directory under the <code>teiid_embedded_query</code> plugin's install path or the test install path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getInstallLibPath() throws IOException {
        if (installLibPath == null) {
        	installLibPath = getEmbeddedInstallPath().append(LIB_DIR);
        }

        return (IPath)installLibPath.clone();
    }
    
    /**
     * @return the "extensions" directory under the <code>teiid_embedded_query</code> plugin's install path or the test install path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getInstallExtensionsPath() throws IOException {
        if (installExtensionsPath == null) {
        	installExtensionsPath = getEmbeddedInstallPath().append(EXTENSIONS_DIR);
        }

        return (IPath)installExtensionsPath.clone();
    }
    
    /**
     * If the Embedded Teiid extensions directory does not exist it is created.
     * 
     * @return the Embedded Teiid extensions directory under the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime
     *         path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getRuntimeExtensionsPath() {
        if (embeddedExtensionsPath == null) {
        	embeddedExtensionsPath = getRuntimePath().append(EXTENSIONS_DIR);
            createDirectory(embeddedExtensionsPath);
        }

        return (IPath)embeddedExtensionsPath.clone();
    }
    
    /**
     * If the Embedded Teiid extensions directory does not exist it is created.
     * 
     * @return the Embedded Teiid extensions directory under the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime
     *         path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getRuntimeLibsPath() {
        if (embeddedLibsPath == null) {
        	embeddedLibsPath = getRuntimePath().append(LIB_DIR);
            createDirectory(embeddedLibsPath);
        }

        return (IPath)embeddedLibsPath.clone();
    }

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Don't allow construction.
     * 
     * @since 6.0.0
     */
    private DqpPath() {
        // nothing to do
    }
}
