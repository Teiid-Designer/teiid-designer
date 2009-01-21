/******************************************************************************* 
 * Copyright (c) 2000, 2008 MetaMatrix, Inc. and Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 *******************************************************************************/ 

package com.metamatrix.modeler.dqp.internal.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.metamatrix.modeler.dqp.DqpPlugin;

public final class DqpPath {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    public static final String PLUGIN_XML = "plugin.xml"; //$NON-NLS-1$

    private static final String DQP_DIR = "dqp";//$NON-NLS-1$ 
    public static final String CONFIGURATION_DIR = "config"; //$NON-NLS-1$
    private static final String EXTENSIONS_DIR = "extensions"; //$NON-NLS-1$
    private static final String LOG_DIR = "log"; //$NON-NLS-1$
    private static final String VDB_EXEC_DIR = "vdb-execution"; //$NON-NLS-1$
    private static final String WORKSPACE_DEFN_DIR = "workspaceConfig"; //$NON-NLS-1$

    // ===========================================================================================================================
    // Class Fields
    // ===========================================================================================================================

    private static IPath installPath;

    private static IPath installConfigPath;

    private static IPath installDqpPath;

    private static IPath runtimePath;

    private static IPath runtimeConfigPath;

    private static IPath runtimeExtensionsPath;

    private static IPath logPath;

    public static String testInstallDir;

    public static String testRuntimeDir;

    private static IPath vdbExecutionPath;

    private static IPath workspaceDefnPath;

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
     * @return the <code>modeler.dqp</code> plugin install path or the test install path
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
     * @return the "dqp" directory under the <code>modeler.dqp</code> plugin's install path or the test install path
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
     * @return the configuration directory under the <code>modeler.dqp</code> plugin's install path or the test install path
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
     * @return the log directory under the <code>modeler.dqp</code> plugin's runtime path or the test runtime path
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
     * @return the <code>modeler.dqp</code> plugin's runtime workspace path or the test runtime path
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
     * @return the configuration directory under the <code>modeler.dqp</code> plugin's runtime workspace path or the test runtime
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
     * @return the extensions directory under the <code>modeler.dqp</code> plugin's runtime workspace path or the test runtime
     *         path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public static IPath getRuntimeExtensionsPath() {
        if (runtimeExtensionsPath == null) {
            runtimeExtensionsPath = getRuntimePath().append(EXTENSIONS_DIR);
            createDirectory(runtimeExtensionsPath);
        }

        return (IPath)runtimeExtensionsPath.clone();
    }

    /**
     * If the VDB execution directory does not exist it is created.
     * 
     * @return the VDB execution directory under the <code>modeler.dqp</code> plugin's runtime workspace path or the test runtime
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
     * @return the directory under the <code>modeler.dqp</code> plugin's runtime workspace path or the test runtime path where the
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
