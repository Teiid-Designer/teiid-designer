/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;


/** 
 * @since 4.3
 */
public class TestModelConnectorBindingMapper extends TestCase {
    
    private static String PHYSICAL_MODEL_NAME1 = "PhysicalModel1"; //$NON-NLS-1$
    private static String PHYSICAL_MODEL_NAME2 = "PhysicalModel2"; //$NON-NLS-1$
    private static String PHYSICAL_MODEL_NAME3 = "PhysicalModel3"; //$NON-NLS-1$
    private static String VIRTUAL_MODEL_NAME1 = "VirtualModel1"; //$NON-NLS-1$
    
    private static String DRIVER_CLASS1 = "com.oracle.OracleDriver"; //$NON-NLS-1$
    private static String DRIVER_CLASS2 = "com.sybase.SybaseDriver"; //$NON-NLS-1$
    private static String URL1 = "jdbc:oracle:OracleUrl1"; //$NON-NLS-1$
    private static String URL2 = "jdbc:sybase:sybaseUrl1"; //$NON-NLS-1$
    private static String URL3 = "jdbc:oracle:OracleUrl2"; //$NON-NLS-1$
    private static String USER1 = "matoug"; //$NON-NLS-1$
    private static String USER2 = "tiger"; //$NON-NLS-1$    

    /**
     * Constructor for TestModelConnectorBindingMapper.
     * @param name
     */
    public TestModelConnectorBindingMapper(String name) {
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
        super.tearDown();
    }
    
    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelConnectorBindingMapper"); //$NON-NLS-1$
        suite.addTestSuite(TestModelConnectorBindingMapper.class);
//        suite.addTest(new TestModelConnectorBindingMapper("testPathExist"));//$NON-NLS-1$
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            	ModelConnectorBindingMapperImpl.HEADLESS = true;
            }
            @Override
            public void tearDown() {
            	ModelConnectorBindingMapperImpl.HEADLESS = false;
            }
        };
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    public ModelConnectorBindingMapper helpGetMapper(VdbEditingContext context) throws Exception {
        return new ModelConnectorBindingMapperImpl(context);
    }
    
    public VdbEditingContext getSampleEditingContext1() {
        FakeVdbEditingContext context = new FakeVdbEditingContext();
        VirtualDatabase database = context.getVirtualDatabase();
        ModelReference reference1 = helpGetmodelReference(PHYSICAL_MODEL_NAME1, ModelType.PHYSICAL_LITERAL, DRIVER_CLASS1, URL1, USER1);
        database.getModels().add(reference1);
        ModelReference reference2 = helpGetmodelReference(PHYSICAL_MODEL_NAME2, ModelType.PHYSICAL_LITERAL, DRIVER_CLASS2, URL2, USER2);
        database.getModels().add(reference2);
        ModelReference reference3 = helpGetmodelReference(PHYSICAL_MODEL_NAME3, ModelType.PHYSICAL_LITERAL, DRIVER_CLASS2, URL3, USER1);
        database.getModels().add(reference3);
        ModelReference reference4 = helpGetmodelReference(VIRTUAL_MODEL_NAME1, ModelType.VIRTUAL_LITERAL, DRIVER_CLASS2, URL3, USER2);
        database.getModels().add(reference4);
        return context;
    }

    public VdbEditingContext getSampleEditingContext2() {
        FakeVdbEditingContext context = new FakeVdbEditingContext();
        VirtualDatabase database = context.getVirtualDatabase();
        ModelReference reference1 = helpGetmodelReference(VIRTUAL_MODEL_NAME1, ModelType.VIRTUAL_LITERAL, DRIVER_CLASS2, URL3, USER2);
        database.getModels().add(reference1);
        return context;
    }

    public ModelReference helpGetmodelReference(String modelName, ModelType type, String driverclass, String url, String userName) {
        ManifestFactory factory = ManifestFactory.eINSTANCE;
        ModelReference reference = factory.createModelReference();
        reference.setModelType(type);
        reference.setName(modelName);
        
        ModelSourceProperty sourceProperty1 = factory.createModelSourceProperty();
        sourceProperty1.setName(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS);
        sourceProperty1.setValue(driverclass);
        
        ModelSourceProperty sourceProperty2 = factory.createModelSourceProperty();
        sourceProperty2.setName(JDBCConnectionPropertyNames.JDBC_IMPORT_URL);
        sourceProperty2.setValue(url);
        
        ModelSourceProperty sourceProperty3 = factory.createModelSourceProperty();
        sourceProperty3.setName(JDBCConnectionPropertyNames.JDBC_IMPORT_USERNAME);
        sourceProperty3.setValue(userName);
        
        ModelSource source = factory.createModelSource();
        source.getProperties().add(sourceProperty1);
        source.getProperties().add(sourceProperty2);
        source.getProperties().add(sourceProperty3);        

        reference.setModelSource(source);
        return reference;
    }
    
    public ModelReference helpGetmodelReference(VdbEditingContext context, String modelName) {
        Collection refs = context.getVirtualDatabase().getModels();
        for(final Iterator iter = refs.iterator(); iter.hasNext();) {
            ModelReference reference = (ModelReference) iter.next();
            if(reference.getName().equalsIgnoreCase(modelName)) {
                return reference;
            }
        }
        return null;
    }

    public VdbDefnHelper helpGetVdbDefnHelper() throws Exception {
        return new FakeVdbDefnHelper(null);
    }

    public ConfigurationManager getSampleConfigManager1() {
        FakeConfigurationManager manager = new FakeConfigurationManager();
        manager.addBinding(helpGetConnectorBinding(DRIVER_CLASS1, URL1, USER1));
        manager.addBinding(helpGetConnectorBinding(DRIVER_CLASS2, URL2, USER2));
        manager.addBindingType(helpGetConnectorBindingType(DRIVER_CLASS1));
        return manager;
    }

    public ConnectorBinding helpGetConnectorBinding(String driverclass, String url, String userName) {
        FakeConnectorBinding binding = new FakeConnectorBinding();
        binding.setProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS, driverclass);
        binding.setProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL, url);
        binding.setProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER, userName);
        return binding;
    }
    
    public ConnectorBindingType helpGetConnectorBindingType(String driverclass) {
        FakeConnectorBindingType bindingType = new FakeConnectorBindingType();
        bindingType.props.setProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS, driverclass);
        return bindingType;
    }    

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================
    
    public void testFindConnectorBindingMatch() throws Exception {
        VdbEditingContext context = getSampleEditingContext1();
        ConfigurationManager manager = getSampleConfigManager1();
        
        ModelConnectorBindingMapperImpl mapper = (ModelConnectorBindingMapperImpl) helpGetMapper(context);
        mapper.setManager(manager);
        
        Map map = mapper.findAllConnectorBindingMatches();
        assertEquals(2, map.size());
    }
    
    public void testFindNoConnectorBindingMatch() throws Exception {
        VdbEditingContext context = getSampleEditingContext2();
        ConfigurationManager manager = getSampleConfigManager1();
        
        ModelConnectorBindingMapperImpl mapper = (ModelConnectorBindingMapperImpl) helpGetMapper(context);
        mapper.setManager(manager);
        
        Map map = mapper.findAllConnectorBindingMatches();
        assertEquals(0, map.size());        
    }
    
    public void testFindConnectorBindingTypeMatch() throws Exception {
        VdbEditingContext context = getSampleEditingContext1();
        ConfigurationManager manager = getSampleConfigManager1();
        
        ModelConnectorBindingMapperImpl mapper = (ModelConnectorBindingMapperImpl) helpGetMapper(context);
        mapper.setManager(manager);
        
        ModelReference reference1 = helpGetmodelReference(context, PHYSICAL_MODEL_NAME1);
        Collection matches = mapper.findConnectorTypeMatches(reference1);
        assertEquals(1, matches.size());        
    }
    
    public void testFindNoConnectorBindingTypeMatch() throws Exception {
        VdbEditingContext context = getSampleEditingContext1();
        ConfigurationManager manager = getSampleConfigManager1();
        
        ModelConnectorBindingMapperImpl mapper = (ModelConnectorBindingMapperImpl) helpGetMapper(context);
        mapper.setManager(manager);
        
        ModelReference reference1 = helpGetmodelReference(context, VIRTUAL_MODEL_NAME1);        
        Collection matches = mapper.findConnectorTypeMatches(reference1);
        assertEquals(0, matches.size());        
    }
    
    public void testCreateConnectorBinding() throws Exception {
        VdbEditingContext context = getSampleEditingContext1();
        ConfigurationManager manager = getSampleConfigManager1();
        
        ModelConnectorBindingMapperImpl mapper = (ModelConnectorBindingMapperImpl) helpGetMapper(context);
        mapper.setManager(manager);
        
        ModelReference reference = helpGetmodelReference(context, PHYSICAL_MODEL_NAME1);
        ConnectorBindingType connectorBindingType = helpGetConnectorBindingType(DRIVER_CLASS1);
        ConnectorBinding binding = mapper.createConnectorBinding(reference, connectorBindingType, connectorBindingType.getFullName());
        assertNotNull(binding);
        Properties props = binding.getProperties();
        assertEquals(DRIVER_CLASS1, props.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS));
        assertEquals(URL1, props.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL));
        assertEquals(USER1, props.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER));
    }
}
