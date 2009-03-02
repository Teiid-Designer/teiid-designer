/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorArchive;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.config.xml.XMLConfigurationImportExportUtility;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.internal.config.ConfigFileManager;
import com.metamatrix.modeler.dqp.internal.config.ConfigurationManagerImpl;
import com.metamatrix.modeler.dqp.internal.config.ConnectionManager;

/**
 * @since 4.3
 */
public class DqpConfigurationManagerTestHelper {

    public final static String CONFIG_FILE_NAME = "configuration.xml"; //$NON-NLS-1$
    public final static String SYSTEM_VDB_FILE_NAME = "System.vdb"; //$NON-NLS-1$
    public final static String BINDING_CONFIG_FILE_NAME = "binding.xml"; //$NON-NLS-1$
    public final static String CONNECTOR_FILE_NAME = "config/connectortype.xml"; //$NON-NLS-1$
    public final static String CAF_FILE_NAME = "sample_connector_archive.caf"; //$NON-NLS-1$

    public final static String CONNECTION_BINDING_NAME = "BQT2 Oracle 10g Simple Cap"; //$NON-NLS-1$
    public final static String BASE_CONFIG_PATH = SmartTestSuite.getTestDataPath();
    public final static String CONFIG_WORKING_DIR = BASE_CONFIG_PATH + File.separator + "DqpConfiguration"; //$NON-NLS-1$

    public DqpConfigurationManagerTestHelper() {
    }

    public void tearDown() {
        File testDir = new File(CONFIG_WORKING_DIR);

        if (testDir.exists()) {
            File[] files = testDir.listFiles();

            for (int i = 0; i < files.length; ++i) {
                files[i].delete();
            }
        }
    }

    protected IPath getPath() {

        IPath path = new Path(BASE_CONFIG_PATH);

        return (IPath)path.clone();
    }

    protected IPath getConfigPath() {

        IPath path = getPath();

        return (IPath)path.clone();
    }

    protected ConfigFileManager getDefaultConfigFileManager() throws Exception {

        IPath filePath = getConfigPath();
        Assert.assertTrue(filePath.toFile().exists());

        return new ConfigFileManager(filePath);
    }

    protected ConnectionManager getDefaultConnectionManager() throws Exception {

        ConfigFileManager configFileManager = getDefaultConfigFileManager();
        Assert.assertNotNull(configFileManager);

        ConfigurationModelContainer cmc = configFileManager.loadConfig(CONFIG_FILE_NAME);
        Assert.assertNotNull(cmc);

        return new ConnectionManager(cmc, CONFIG_FILE_NAME);
    }

    protected ConnectionManager getBindingConnectionManager() throws Exception {

        ConfigFileManager configFileManager = getDefaultConfigFileManager();
        Assert.assertNotNull(configFileManager);

        ConfigurationModelContainer cmc = configFileManager.loadConfig(BINDING_CONFIG_FILE_NAME);
        Assert.assertNotNull(cmc);

        return new ConnectionManager(cmc, BINDING_CONFIG_FILE_NAME);
    }

    protected ConnectorBinding getConnectorBinding( String bindingName ) throws Exception {

        ConnectionManager connMgr = getBindingConnectionManager();
        Assert.assertNotNull(connMgr);

        ConnectorBinding cb = connMgr.getBinding(bindingName);
        Assert.assertNotNull(cb);
        Assert.assertEquals(CONNECTION_BINDING_NAME, cb.getFullName());

        return cb;
    }

    protected Map getConnectorBinding() throws Exception {

        ConnectionManager connMgr = getBindingConnectionManager();
        Assert.assertNotNull(connMgr);

        Collection bindings = connMgr.getBinding();
        Assert.assertNotNull(bindings);

        // add the new binding to the name map
        Map bindingNameToBindingMap = new HashMap();
        Iterator iter = bindings.iterator();

        while (iter.hasNext()) {
            ConnectorBinding binding = (ConnectorBinding)iter.next();
            bindingNameToBindingMap.put(binding.getName(), binding);
        }

        return bindingNameToBindingMap;
    }

    protected File getConnectorTypeFile() {

        IPath filePath = getConfigPath();
        IPath connectorPath = filePath.append(CONNECTOR_FILE_NAME);

        return connectorPath.toFile();
    }

    protected File getImportedFile() {

        IPath filePath = getConfigPath();
        IPath importedPath = filePath.append(BINDING_CONFIG_FILE_NAME);

        return importedPath.toFile();
    }

    protected File getCAFFile() {

        IPath filePath = getPath();
        IPath connectorPath = filePath.append(CAF_FILE_NAME);

        return connectorPath.toFile();
    }

    protected ConnectorArchive getConnectorArchive() throws Exception {
        XMLConfigurationImportExportUtility util = new XMLConfigurationImportExportUtility();
        FileInputStream stream = new FileInputStream(getCAFFile());
        ConnectorArchive archive = util.importConnectorArchive(stream, new BasicConfigurationObjectEditor());
        Assert.assertNotNull(archive);

        return archive;
    }

    protected ConfigurationManager getConfigurationManager() throws Exception {

        IPath path = getConfigPath();
        Assert.assertNotNull(path);

        ConfigurationManager connMgr = new ConfigurationManagerImpl(path);
        return connMgr;
    }

    protected String getNewpath() throws Exception {
        String path = getPath().toFile().getCanonicalPath().concat("/dqp").toString(); //$NON-NLS-1$
        if (path.indexOf("\\") != -1) { //$NON-NLS-1$
            path = path.replace('\\', IPath.SEPARATOR);
        }

        return path + "/lib/;" + //$NON-NLS-1$
               path + "/lib/metamatrix-dqp.jar;" + //$NON-NLS-1$
               path + "/lib/bcprov-jdk14-122.jar;" + //$NON-NLS-1$
               path + "/extensions/MJjdbc.jar"; //$NON-NLS-1$
    }

    /**
     * @return
     * @since 4.3
     */
    protected String getUdfExtensionClasspath() {
        String path = getPath().append("config").toString(); //$NON-NLS-1$
        if (path.indexOf("\\") != -1) { //$NON-NLS-1$
            path = path.replace('\\', IPath.SEPARATOR);
        }

        return path + "/jdbcconn.jar;" + //$NON-NLS-1$
               path + "/loopbackconn.jar;" + //$NON-NLS-1$
               path + "/sampleconn.jar"; //$NON-NLS-1$
    }
}
