/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.namedobject.BaseID;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.internal.config.ConfigFileManager;
import com.metamatrix.modeler.dqp.internal.config.ConnectionManager;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

/**
 * TestDdlPlugin
 */
public class TestDqpConfigurationManager extends TestCase {


    private final static DqpConfigurationManagerTestHelper
                    dqpConfigurationManagerTestHelper = new DqpConfigurationManagerTestHelper();

    /**
     * Constructor for TestDdlPlugin.Util.
     * @param name
     */
    public TestDqpConfigurationManager(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        TestDqpConfigurationManager.dqpConfigurationManagerTestHelper.tearDown();
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {

        TestSuite suite = new TestSuite("com.metamatrix.modeler.dqp.TestDqpConfigurationManager"); //$NON-NLS-1$
        suite.addTestSuite(TestDqpConfigurationManager.class);

//        suite.addTest(new TestDqpConfigurationManager("testUdfExtensionClasspath"));//$NON-NLS-1$
//        suite.addTest(new TestDqpConfigurationManager("testLoadDqpPluginHelperConnectorTypeIds"));//$NON-NLS-1$

        // One-time setup and teardown
        return new TestSetup(suite) {

            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================

    private DqpConfigurationManagerTestHelper getHelper() {
        return dqpConfigurationManagerTestHelper;
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testLoadDqpPluginHelperConnectorTypeIds() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            Collection connectorTypeIdsCol = configMgr.getConnectorTypeIds();
            for(Iterator i = connectorTypeIdsCol.iterator(); i.hasNext();) {
                BaseID baseID = (BaseID)i.next();
                System.out.println(" Name : " + baseID.getFullName()); //$NON-NLS-1$
            }

            configMgr.addConnectorType( getHelper().getConnectorTypeFile() );

            connectorTypeIdsCol = configMgr.getConnectorTypeIds();
            for(Iterator i = connectorTypeIdsCol.iterator(); i.hasNext();) {
                BaseID baseID = (BaseID)i.next();
                System.out.println("After Name : " + baseID.getFullName()); //$NON-NLS-1$
            }

            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     *
     * Path test
     * @since 4.3
     */
    public void testPathExist() {

        IPath filePath = getHelper().getConfigPath();
        String path = filePath.toString();
        assertNotNull(path);
        assertTrue(filePath.toFile().exists());
    }

    public void testConfigFileExist() {

        IPath filePath = getHelper().getConfigPath();
        filePath = filePath.append(DqpConfigurationManagerTestHelper.CONFIG_FILE_NAME);
        assertTrue(filePath.toFile().exists());
    }


    /**
     *
     * ConfigFileManager Test
     * @since 4.3
     */
    public void testLoadConfig() {

        try {

            ConfigFileManager configFileManager = getHelper().getDefaultConfigFileManager();
            assertNotNull(configFileManager);

            ConnectionManager cm = getHelper().getDefaultConnectionManager();
            assertNotNull(cm);

            ConfigurationModelContainer cmc = cm.getCMContainerImpl();
            assertNotNull(cmc);

            //System.out.println("System Name : " + cmc.getSystemName()); //$NON-NLS-1$
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testLoadAndSaveConfig() {

        try {

            ConfigFileManager configFileManager = getHelper().getDefaultConfigFileManager();
            assertNotNull(configFileManager);

            ConnectionManager cm = getHelper().getDefaultConnectionManager();
            assertNotNull(cm);

            ConfigurationModelContainer cmc = cm.getCMContainerImpl();
            assertNotNull(cmc);

            //System.out.println("System Name : " + cmc.getSystemName()); //$NON-NLS-1$

            boolean isConfigFileSave = configFileManager.saveConfig(cmc, cm.getConfigurationName());
            assertTrue(isConfigFileSave);
        }catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     *
     * ConnectionManager Test
     * @since 4.3
     */
    public void testLoadConnectorBindingFromConfig() {

        try {

            ConnectionManager connMgr = getHelper().getDefaultConnectionManager();
            assertNotNull(connMgr);

            ConnectorBinding cb = connMgr.getBinding(DqpConfigurationManagerTestHelper.CONNECTION_BINDING_NAME);
            assertNotNull(cb);
            assertEquals(DqpConfigurationManagerTestHelper.CONNECTION_BINDING_NAME, cb.getFullName());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testLoadAndSaveConnectorBindingFromConfig() {

        try {

            ConnectionManager connMgr = getHelper().getDefaultConnectionManager();
            assertNotNull(connMgr);

            ConnectorBinding cb = getHelper().getConnectorBinding(DqpConfigurationManagerTestHelper.CONNECTION_BINDING_NAME);
            connMgr.addBinding(cb);

            ConfigFileManager configFileManager = getHelper().getDefaultConfigFileManager();
            assertNotNull(configFileManager);

            ConfigurationModelContainer cmc = connMgr.getCMContainerImpl();
            assertNotNull(cmc);

            boolean isConfigFileSave = configFileManager.saveConfig(cmc, connMgr.getConfigurationName());
            assertTrue(isConfigFileSave);
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testLoadAndSaveConnectorBindingFromConfigAsMap() {

        try {

            ConnectionManager connMgr = getHelper().getDefaultConnectionManager();
            assertNotNull(connMgr);

            Map cb = getHelper().getConnectorBinding();
            assertNotNull(cb);
            connMgr.addBinding(cb);

            ConfigFileManager configFileManager = getHelper().getDefaultConfigFileManager();
            assertNotNull(configFileManager);

            ConfigurationModelContainer cmc = connMgr.getCMContainerImpl();
            assertNotNull(cmc);

            boolean isConfigFileSave = configFileManager.saveConfig(cmc, connMgr.getConfigurationName());
            assertTrue(isConfigFileSave);
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }



    /**
     *
     * Configuration Manager Test
     * @since 4.3
     */
    public void testLoadConfigurationManager() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testLoadAndSaveConfigurationManager() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            boolean isConfigFileSave = configMgr.saveConfig();
            assertTrue(isConfigFileSave);
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testLoadBindingConfigurationManager() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            ConnectorBinding cb = configMgr.getBinding(DqpConfigurationManagerTestHelper.CONNECTION_BINDING_NAME);
            assertNotNull(cb);
            assertEquals(DqpConfigurationManagerTestHelper.CONNECTION_BINDING_NAME, cb.getFullName());

            boolean isConfigFileSave = configMgr.saveConfig();
            assertTrue(isConfigFileSave);
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testLoadBindingConfigurationManagerMap() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            Map cb = getHelper().getConnectorBinding();
            assertNotNull(cb);

            configMgr.addBinding(cb);
            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testConfigurationManagerLoadConfiguration() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            configMgr.loadConfiguration(DqpConfigurationManagerTestHelper.BINDING_CONFIG_FILE_NAME);
            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testConfigurationManagerLoadConfigurationNameList() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            Collection namelist = configMgr.getConfigurationsNameList();
            assertNotNull(namelist);

            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testConfigurationManagerLoadComponentType() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            ConnectorBinding binding = configMgr.getBinding(DqpConfigurationManagerTestHelper.CONNECTION_BINDING_NAME);
            assertNotNull(binding);
            assertEquals(DqpConfigurationManagerTestHelper.CONNECTION_BINDING_NAME, binding.getFullName());

            ComponentType componentType = configMgr.getComponentType(binding);
            assertNotNull(componentType);

            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testConfigurationManagerLoadAddAndSaveConnector() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            configMgr.addConnectorType( getHelper().getConnectorTypeFile() );

            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testConfigurationManagerLoadConnectorTypeIds() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            Collection connectorTypeIdsCol = configMgr.getConnectorTypeIds();
            for(Iterator i = connectorTypeIdsCol.iterator(); i.hasNext();) {
                BaseID baseID = (BaseID)i.next();
                System.out.println(" ConnectorTypeId Name : " + baseID.getFullName()); //$NON-NLS-1$
            }

            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testConfigurationManagerLoadAllBindings() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            Collection bindingsCol = configMgr.getConnectorBindings();
            for(Iterator i = bindingsCol.iterator(); i.hasNext();) {
                ConnectorBinding cb = (ConnectorBinding) i.next();
                System.out.println(" ConnectorBinding Name : " + cb.getFullName()); //$NON-NLS-1$
            }

            assertTrue(configMgr.saveConfig());
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Make sure connector bindings have been filtered
     */
    public void testGetConnectorBindings1() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection filteredBindings = configMgr.getConnectorBindings();
        assertFalse("No filtered connector bindings found in the configuration", filteredBindings.isEmpty()); //$NON-NLS-1$

        Iterator itr = filteredBindings.iterator();

        while (itr.hasNext()) {
            ConnectorBinding binding = (ConnectorBinding)itr.next();
            ComponentType type = configMgr.getComponentType(binding);

            assertNotNull(type);
            assertTrue("Type is not a ConnectorBindingType:" + type.getClass().getName(), //$NON-NLS-1$
                                    (type instanceof ConnectorBindingType));
            assertTrue("Binding type was not filtered:" + type.getFullName(), //$NON-NLS-1$
                                     ModelerDqpUtils.isValidConnectorType((ConnectorBindingType)type));
        }
    }

    /**
     * Make sure connector bindings have NOT been filtered
     */
    public void testGetConnectorBindings2() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection allBindings = configMgr.getConnectorBindings(false);
        assertFalse("No connector bindings found in the configuration", allBindings.isEmpty());  //$NON-NLS-1$

        Collection filteredBindings = configMgr.getConnectorBindings(true);
        assertFalse("No filtered connector bindings found in the configuration", filteredBindings.isEmpty());  //$NON-NLS-1$

        // size of filtered collection should not be greater than unfiltered collection
        assertTrue("Size of all bindings is not greater than the size of the filtered bindings", //$NON-NLS-1$
                                (allBindings.size() >= filteredBindings.size()));
    }

    /**
     * Make sure connector types have been filtered
     */
    public void testGetConnectorTypes1() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection filteredTypes = configMgr.getConnectorTypes();
        assertFalse("No filtered connector types found in the configuration", filteredTypes.isEmpty()); //$NON-NLS-1$

        Iterator itr = filteredTypes.iterator();

        while (itr.hasNext()) {
            ComponentType type = (ComponentType)itr.next();

            assertNotNull(type);
            assertTrue("Type is not a ConnectorBindingType:" + type.getClass().getName(), //$NON-NLS-1$
                                    (type instanceof ConnectorBindingType));
            assertTrue("Binding type was not filtered:" + type.getFullName(), //$NON-NLS-1$
                                     ModelerDqpUtils.isValidConnectorType((ConnectorBindingType)type));
        }
    }

    /**
     * Make sure connector types have NOT been filtered
     */
    public void testGetConnectorTypes2() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection allTypes = configMgr.getConnectorTypes(false);
        assertFalse("No connector types found in the configuration", allTypes.isEmpty());  //$NON-NLS-1$

        Collection filteredTypes = configMgr.getConnectorTypes(true);
        assertFalse("No filtered connector types found in the configuration", filteredTypes.isEmpty());  //$NON-NLS-1$

        // size of collections should not be the same
        assertTrue("Size of all bindings is not greater than the size of the filtered bindings", //$NON-NLS-1$
                                (allTypes.size() > filteredTypes.size()));
    }

    /**
     * Make sure size of filtered connector types is the same as the filtered type IDs.
     */
    public void testGetConnectorTypeIds1() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection filteredTypeIds = configMgr.getConnectorTypeIds();
        assertFalse("No filtered connector type IDs found in the configuration", filteredTypeIds.isEmpty());  //$NON-NLS-1$

        Collection filteredTypes = configMgr.getConnectorTypes();
        assertFalse("No filtered connector types found in the configuration", filteredTypes.isEmpty());  //$NON-NLS-1$

        // make sure collections are the same size
        assertEquals("Number of filtered connector binding types does not equal the number of filtered type IDs", //$NON-NLS-1$
                                  filteredTypes.size(),
                                  filteredTypeIds.size());
    }

    /**
     * Make sure both the filtered connector type ID collection and the filtered connector type collection represent the same objects.
     */
    public void testGetConnectorTypeIds2() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection filteredTypeIds = configMgr.getConnectorTypeIds();
        assertFalse("No filtered connector type IDs found in the configuration", filteredTypeIds.isEmpty());  //$NON-NLS-1$

        Collection filteredTypes = configMgr.getConnectorTypes();
        assertFalse("No filtered connector types found in the configuration", filteredTypes.isEmpty());  //$NON-NLS-1$

        // make sure the IDs of the filtered types collection are represented in the filtered IDs collection
        Iterator itr = filteredTypes.iterator();

        while (itr.hasNext()) {
            ComponentType type = (ComponentType)itr.next();
            Object id = type.getID();
            assertTrue("ID collection does not contain ID for " + type.getFullName(), //$NON-NLS-1$
                                    filteredTypeIds.contains(id));
        }

        // make sure the IDs in the filtered type IDs collection represent types in the filtered types collection
        itr = filteredTypeIds.iterator();
        Iterator itr2 = null;

        while (itr.hasNext()) {
            boolean foundIt = false;
            Object id = itr.next();
            itr2 = filteredTypes.iterator();

            while (itr2.hasNext()) {
                ComponentType type = (ComponentType)itr2.next();

                if (type.getID().equals(id)) {
                    foundIt = true;
                    break;
                }
            }

            assertTrue("Filtered type collection does not contain ID " + id, foundIt); //$NON-NLS-1$
        }
    }

    /**
     * Make sure size of unfiltered connector types is the same as the unfiltered type IDs.
     */
    public void testGetConnectorTypeIds3() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection allTypeIds = configMgr.getConnectorTypeIds(false);
        assertFalse("No unfiltered connector type IDs found in the configuration", allTypeIds.isEmpty());  //$NON-NLS-1$

        Collection allTypes = configMgr.getConnectorTypes(false);
        assertFalse("No unfiltered connector type IDs found in the configuration", allTypes.isEmpty());  //$NON-NLS-1$

        // make sure collections are the same size
        assertEquals("Number of unfiltered connector binding type IDs does not equal the number of unfiltered types", //$NON-NLS-1$
                                  allTypeIds.size(),
                                  allTypes.size());
    }

    /**
     * Make sure both the unfiltered connector type ID collection and the unfiltered connector type collection represent the same objects.
     */
    public void testConnectorTypeIds4() throws Exception {
        ConfigurationManager configMgr = getHelper().getConfigurationManager();
        assertNotNull(configMgr);

        Collection allTypeIds = configMgr.getConnectorTypeIds(false);
        assertFalse("No unfiltered connector type IDs found in the configuration", allTypeIds.isEmpty());  //$NON-NLS-1$

        Collection allTypes = configMgr.getConnectorTypes(false);
        assertFalse("No unfiltered connector types found in the configuration", allTypes.isEmpty());  //$NON-NLS-1$

        // make sure the IDs of the unfiltered types collection are represented in the unfiltered IDs collection
        Iterator itr = allTypes.iterator();

        while (itr.hasNext()) {
            ComponentType type = (ComponentType)itr.next();
            Object id = type.getID();
            assertTrue("Unfiltered type ID collection does not contain ID for " + type.getFullName(), //$NON-NLS-1$
                                    allTypeIds.contains(id));
        }

        // make sure the IDs in the unfiltered type IDs collection represent types in the unfiltered types collection
        itr = allTypeIds.iterator();
        Iterator itr2 = null;

        while (itr.hasNext()) {
            boolean foundIt = false;
            Object id = itr.next();
            itr2 = allTypes.iterator();

            while (itr2.hasNext()) {
                ComponentType type = (ComponentType)itr2.next();

                if (type.getID().equals(id)) {
                    foundIt = true;
                    break;
                }
            }

            assertTrue("Unfiltered type collection does not contain ID " + id, foundIt); //$NON-NLS-1$
        }
    }

    public void testConfigurationManagerLoadBindingByName() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            ConnectorBinding cb = configMgr.getBinding("BookListingSQL"); //$NON-NLS-1$
            assertNotNull(cb);
            System.out.println(" ConnectorBinding Name : " + cb.getFullName()); //$NON-NLS-1$
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testConfigurationManagerLoadBindingByConnectorTypeId() {

        try {

            String componentTypeIdName = "SQL Server JDBC Connector";   //$NON-NLS-1$

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            BaseID componentType = null;
            Collection connectorTypeIdsCol = configMgr.getConnectorTypeIds();
            for(Iterator i = connectorTypeIdsCol.iterator(); i.hasNext();) {
                BaseID baseID = (BaseID)i.next();

                if(baseID.getFullName().equalsIgnoreCase(componentTypeIdName)) {
                    System.out.println(" ConnectorTypeId  Name : " + baseID.getFullName()); //$NON-NLS-1$
                    componentType = baseID;
                    break;
                }
            }
            assertNotNull(componentType);

            Collection bindingsCol = configMgr.getBindingsForType(componentType);
            assertNotNull(bindingsCol);
            for(Iterator i = bindingsCol.iterator(); i.hasNext();) {
                ConnectorBinding cb = (ConnectorBinding) i.next();
                System.out.println(" ConnectorBinding Name : " + cb.getFullName()); //$NON-NLS-1$
            }

        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     *
     * Listener Test
     * @since 4.3
     */
    public void testDqpChangeListener() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            MockDqpChangeListener  java = new MockDqpChangeListener();
            MockDqpChangeListener World = new MockDqpChangeListener();

            configMgr.addChangeListener(java);
            configMgr.addChangeListener(World);

            // import so that notifications will be sent
            File fileToImport = getHelper().getImportedFile();
            assertNotNull(fileToImport);
            configMgr.importConfig(fileToImport);

            // check notification
            assertTrue(java.isChange());
			assertTrue(World.isChange());

            // remove one listener and check notifications
            configMgr.removeChangeListener(java);

            // import again so notifications will be sent
            configMgr.importConfig(fileToImport);

            assertFalse("Removed ChangeListener notified", java.isChange()); //$NON-NLS-1$
            assertTrue("ChangeListener not notified", World.isChange()); //$NON-NLS-1$
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public void testLoadAddAndSaveConnectorDqpChangeListener() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            MockDqpChangeListener  java = new MockDqpChangeListener();
            MockDqpChangeListener World = new MockDqpChangeListener();

            configMgr.addChangeListener(java);
            configMgr.addChangeListener(World);
            configMgr.addConnectorType( getHelper().getConnectorTypeFile() );

            assertTrue(java.isChange());
			assertTrue(World.isChange());

            configMgr.removeChangeListener(java);
            assertTrue(configMgr.saveConfig());

            //removed listener should be false
            assertFalse(java.isChange());

            //not removed but listener is not been changed, should be false
            assertFalse(World.isChange());

        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * testImportFile
     *
     * @since 4.3
     */
    public void testImportFile() {

        try {

            ConfigurationManager configMgr = getHelper().getConfigurationManager();
            assertNotNull(configMgr);

            File fileToImport = getHelper().getImportedFile();
            assertNotNull(fileToImport);
            if (!configMgr.importConfig(fileToImport)) {
                fail("Saving configuration <" + fileToImport.getAbsolutePath() + "> returned false"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }catch(Exception e) {
            fail(e.getMessage());
        }
    }

}
