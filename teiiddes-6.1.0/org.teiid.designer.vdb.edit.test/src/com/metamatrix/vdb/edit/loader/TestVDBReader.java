/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.config.model.ConfigurationModelContainerAdapter;
import com.metamatrix.common.util.ByteArrayHelper;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.common.vdb.api.VDBStream;
import com.metamatrix.common.vdb.api.VDBStreamImpl;
import com.metamatrix.core.util.FileUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.vdb.VDBStatus;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.vdb.edit.VdbContext;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;
import com.metamatrix.vdb.runtime.BasicVDBDefn;


/**
 */
public class TestVDBReader extends TestCase {

 
    private BasicConfigurationObjectEditor editor = new BasicConfigurationObjectEditor(false);

    public TestVDBReader(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
    }

    // ----------------------------------------------------------------
    // Helper Methods 
    // ----------------------------------------------------------------

    private BasicVDBDefn createBasicVDBDefn(String vdbName, String modelName) {
    	// --------------------------------------------------------------------------
        // Create BasicVDBDefn, with modelInfo for one model and one connectorType
    	// --------------------------------------------------------------------------
        String ctName = "ConnectorType";//$NON-NLS-1$
        String cbName = "ConnectorBinding1";//$NON-NLS-1$
        String cbRouting = "mmuid:1234565";//$NON-NLS-1$

        BasicVDBDefn vdbDefn = new BasicVDBDefn(vdbName); 
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        
        int modelscnt=1;
        int bindingIt = 1;
        int bindingcnt = 0;
        
        String bindingName = null; 
        ConnectorBinding cb = null;
        // load the vdbdefn
        for (int m=0; m < modelscnt; m++ ) {
            BasicVDBModelDefn mi = new BasicVDBModelDefn(modelName);
            
            vdbDefn.addModelInfo(mi); 
            
            for (int b=0; b<bindingIt; b++) {
            
                String typeName = ctName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBindingType ct = creatConnectorType(typeName);
                bindingName = cbName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                cb = createConnectorBinding(bindingName, cbRouting + "_" + m + "_" + b, ct); //$NON-NLS-1$ //$NON-NLS-2$
                
                vdbDefn.addConnectorType(ct);
                
                vdbDefn.addConnectorBinding(mi.getName(), cb);
                
                ++bindingcnt;
            }  

        }
    	return vdbDefn;
    }
    
    private ConnectorBindingType creatConnectorType(String name) {
        ConnectorBindingType ct = (ConnectorBindingType)editor.createComponentType(ComponentType.CONNECTOR_COMPONENT_TYPE_CODE,
                                                                                   name,
                                                                                   ConnectorBindingType.CONNECTOR_TYPE_ID, // not really used since getting rid of Product
                                                                                   ConnectorBindingType.CONNECTOR_TYPE_ID,
                                                                                   true,
                                                                                   false); 
        return ct;
    }
    
    private ConnectorBinding createConnectorBinding(String name, String routingID, ConnectorBindingType ct) {
        ConnectorBinding cb =  editor.createConnectorComponent(Configuration.NEXT_STARTUP_ID, 
                                                               (ComponentTypeID) ct.getID(), 
                                                               name,
                                                               routingID);
        return cb;

    }
    
    // ----------------------------------------------------------------
    // Test Methods 
    // ----------------------------------------------------------------
    
    public void testImportingDTMSVDBDefn() throws Exception {

        String vdbFile = "DTCOracle8iVDB.DEF"; //$NON-NLS-1$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.DTMS_VDB);

        VDBDefn defn = VDBReader.loadVDBDefn(vdbFile, v.getVDBDefnLocation());

        validate(v, defn);

    }

    public void testImportingPartsVDBDefn() throws Exception {

        String vdbFile = "PartsSupplier.DEF"; //$NON-NLS-1$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);

        VDBDefn defn = VDBReader.loadVDBDefn(vdbFile, v.getVDBDefnLocation());

        validate(v, defn);

    }

    public void testImportingPartsVDB_42Defn() throws Exception {

        String vdbFile = "PartsSupplier_42.DEF"; //$NON-NLS-1$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);

        VDBDefn defn = VDBReader.loadVDBDefn(vdbFile, v.getVDBDefnLocation());

        validate(v, defn);
        
        if (defn.getDescription() == null || defn.getDescription().length() == 0) {
            fail("Description for VDB " + vdbFile + " was not loaded.");//$NON-NLS-1$ //$NON-NLS-2$
        } 
              
        
        Map connectorTypes = defn.getConnectorTypes();
        if (connectorTypes == null || connectorTypes.size() == 0) {
            fail("VDB DEF file " + v.getVDBFileLocation() + " did not contain any connector types"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (connectorTypes.size() != 2) {
            fail("VDB DEF file " + v.getVDBFileLocation() + " should have contained 2 connector type but was " + connectorTypes.size()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Map connectorBindings = defn.getConnectorBindings();
        if (connectorBindings == null || connectorBindings.size() == 0) {
            fail("VDB DEF file " + defn.getName() + " did not contain any connector bindings"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (connectorBindings.size() != 2) {
            fail("VDB DEF file " + defn.getName() + " should have contained 2 connector bindings but was " + connectorBindings.size()); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    
    /**
     * This test is to verify the use of an existing binding in the configuration when the ConfigurationModelContainer
     * is passed to the VDBReader.loadVDBDefn method. 
     * @throws Exception
     * @since 4.3
     */
    
    public void testImportingPartsSupplierVDBUsingConfig_def18612() throws Exception {

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
               
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier_VDB_missingCB_DEF.vdb"});//$NON-NLS-1$

        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }

        VDBReader reader = new VDBReader();
        VdbContext context = reader.createVDBContextEditor("PartsSupplier_VDB_missingCB_DEF.vdb", vdbArchiveArray);//$NON-NLS-1$
        
        FileInputStream fis = null;
        ConfigurationModelContainer cmc = null;
        try {
            String configpath = FileUtils.buildDirectoryPath(new String[] {SmartTestSuite.getTestDataPath(), "vdbloader", "config.xml"});//$NON-NLS-1$ //$NON-NLS-2$ 
            fis = new FileInputStream(configpath);
            ConfigurationModelContainerAdapter adapter = new     ConfigurationModelContainerAdapter();
            cmc = adapter.readConfigurationModel(fis, Configuration.NEXT_STARTUP_ID);
        } finally {
            fis.close();
        }
        
        if (cmc.getComponentType("PartsSupplier JDBC Connector") == null) {//$NON-NLS-1$
            fail("Incorrect configuration was imported, did not find connector type");//$NON-NLS-1$
        }
        ConnectorBinding mcbin = cmc.getConfiguration().getConnectorBinding("Missing Connector");//$NON-NLS-1$
        if (mcbin == null) {
            fail("Incorrect configuration was imported, did not find connector binding");//$NON-NLS-1$
       }
        
//        ConnectorBinding ocbin = cmc.getConfiguration().getConnectorBinding("PartSupplier_Oracle Connector");//$NON-NLS-1$
//        if (ocbin == null) {
//            fail("Incorrect configuration was imported, did not find connector binding");//$NON-NLS-1$
//       }

        
        
//        String user = ocbin.getProperty("User");//$NON-NLS-1$
//        if (!user.equalsIgnoreCase("ConfigUser")) {//$NON-NLS-1$
//            fail("Incorrect configuration was imported, connector binding User property should have value of ConfigUser");//$NON-NLS-1$
//        }
        
            //ConfigUse
        VDBDefn defn = VDBReader.loadVDBDefn(context, false, cmc);
        
        if (defn.getConnectorType("PartsSupplier JDBC Connector") == null) {//$NON-NLS-1$
            fail("Did not import connector type from configuration");//$NON-NLS-1$
        }
        
        ConnectorBinding mcbDefn = defn.getConnectorBindingByName(mcbin.getFullName());
        if (mcbDefn == null ) {
            fail("The missing connector in the .DEF was not loaded from configuration");//$NON-NLS-1$
        }        

        ConnectorBinding ocbin = defn.getConnectorBindings().get("PartSupplier_Oracle Connector");//$NON-NLS-1$
        if (ocbin == null) {
            fail("Incorrect .VDB, the .DEF did not find connector binding");//$NON-NLS-1$
        }        
        
        ConnectorBinding cbDefn = defn.getConnectorBindingByName(ocbin.getFullName());
        if (cbDefn == null ) {
            fail("Did not use the binding from configuration");//$NON-NLS-1$
        }
        
        // the binding should have been loaded from the import def, not the configuration 
        
        String userDefn = cbDefn.getProperty("User");//$NON-NLS-1$
        if (!userDefn.equalsIgnoreCase("USER_OVERRIDE")) {//$NON-NLS-1$
            fail("The incorrect binding was picked up, the DEF binding was not used");//$NON-NLS-1$
        }
        
//        validate(v, defn);

    }    
    
    
    /**
     * DEF 19596 - Unable to deploy VDB to server that has connector bindings defined in modeler
     * The problem here is the .def is missing a connector type for one of the bindings.
     * @throws Exception
     * @since 4.3
     */
    
    public void testImportingMissingConnectorType_def19596() throws Exception {

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "uvgts.vdb"} ) ; //$NON-NLS-1$

        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        VDBDefn defn = VDBReader.loadVDBDefn(vdbArchiveArray, "VDBName"); //$NON-NLS-1$
        
       
        Map bindings = defn.getConnectorBindings();
        ConnectorBinding mcbDefn = (ConnectorBinding) bindings.get("Products ORA Connector"); //$NON-NLS-1$
        
        if (mcbDefn == null ) {
            fail("The missing connector Products ORA Connector in the .DEF");//$NON-NLS-1$
        }        


        if (defn.getConnectorType(mcbDefn.getComponentTypeID().getFullName()) != null) {
            fail("Found the connector type, which should have been missing");//$NON-NLS-1$
        }

    }    
    

    
    
    public void testImportingPartsVDB_42DefnSoftRef() throws Exception {

        String vdbFile = "PartsSupplier_42.DEF"; //$NON-NLS-1$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String archiveLoc = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier.VDB" });//$NON-NLS-1$
      
        VDBDefn defn = VDBReader.loadVDBDefn(vdbFile, v.getVDBDefnLocation(), new File(archiveLoc));

        VDBStream stream = defn.getVDBStream();
        if (! (stream instanceof VDBStreamImpl) ) {
            fail("VDB Archive SOftreference was not used.");//$NON-NLS-1$
            
        }
        validate(v, defn);
        
        if (defn.getDescription() == null || defn.getDescription().length() == 0) {
            fail("Description for VDB " + vdbFile + " was not loaded.");//$NON-NLS-1$ //$NON-NLS-2$
        } 
              
        
        Map connectorTypes = defn.getConnectorTypes();
        if (connectorTypes == null || connectorTypes.size() == 0) {
            fail("VDB DEF file " + v.getVDBFileLocation() + " did not contain any connector types"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (connectorTypes.size() != 2) {
            fail("VDB DEF file " + v.getVDBFileLocation() + " should have contained 2 connector type but was " + connectorTypes.size()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Map connectorBindings = defn.getConnectorBindings();
        if (connectorBindings == null || connectorBindings.size() == 0) {
            fail("VDB DEF file " + defn.getName() + " did not contain any connector bindings"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (connectorBindings.size() != 2) {
            fail("VDB DEF file " + defn.getName() + " should have contained 2 connector bindings but was " + connectorBindings.size()); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }    
    
    public void testImportingPartsPre42VDBDefnArray() throws Exception {

        String vdbFile = "PartsSupplier.DEF"; //$NON-NLS-1$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), vdbFile});
        
        FileUtil fu = new FileUtil(path);
        byte[] vdbDefnArray = fu.readBytes();
        
        if (vdbDefnArray == null || vdbDefnArray.length == 0) {
            fail("Unable to load " + path + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
        String vdbDefnString = new String(vdbDefnArray);
        
        path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier.vdb"});//$NON-NLS-1$

        fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
        VDBDefn defn = VDBReader.loadVDBDefn(vdbFile, vdbArchiveArray, vdbDefnString.toCharArray());

        validate(v, defn);

    } 
    
    public void testImportingParts42VDBDefnArray() throws Exception {

        String vdbFile = "PartsSupplier_42.DEF"; //$NON-NLS-1$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), vdbFile});
        
        FileUtil fu = new FileUtil(path);
        byte[] vdbDefnArray = fu.readBytes();
        
        if (vdbDefnArray == null || vdbDefnArray.length == 0) {
            fail("Unable to load " + path + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
        String vdbDefnString = new String(vdbDefnArray);
        
        path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier.vdb"});//$NON-NLS-1$

        fu = new FileUtil(path);
                          //v.getVDBFileLocation());
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
        VDBDefn defn = VDBReader.loadVDBDefn(vdbFile, vdbArchiveArray, vdbDefnString.toCharArray());

        validate(v, defn);

    }  
    
    public void testImportingVDBArchiveWithNoDef() throws Exception {

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
               
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier.vdb"} ) ; //$NON-NLS-1$

        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        VDBDefn defn = VDBReader.loadVDBDefn(vdbArchiveArray, "VDBName"); //$NON-NLS-1$
        

        validate(v, defn);

    }      
    
    
    public void testImportingPartsSupplierVDBArchive() throws Exception {

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
               
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier_VDB_with_DEF.vdb"});//$NON-NLS-1$

        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
        VDBDefn defn = VDBReader.loadVDBDefn(null, vdbArchiveArray, null); 

        validate(v, defn);
        
        ComponentType type = defn.getConnectorType("Oracle ANSI JDBC Connector");//$NON-NLS-1$
        if (type == null) {
            fail("PartsSupplier_VDB_with_DEF.vdb did not contain the expected connector type Oracle ANSI JDBC Connector");//$NON-NLS-1$
        }
        
        // DEF 19965 - Importing .VDB file with a new connector type sets the editable flag to false
        // this test is verifying the default setting of isModifiable() for each property is true, unless it is specified 
        // in the .DEF file
        ComponentTypeDefn td = type.getComponentTypeDefinition("User");//$NON-NLS-1$
        if (!td.getPropertyDefinition().isModifiable()) {
            fail("User property defn should have been modifiable");//$NON-NLS-1$
        }
        
    }    
    
    public void testImportingVDBArchiveWithIncompleteDefFile() throws Exception {

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
               
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "QT_DB2v72DS_1.vdb"});//$NON-NLS-1$

        // test using path
        boolean isValid = VDBReader.isValidVDBDefFileIncluded(path);
        if (isValid) {
            fail("VDB should not be valid, it contains an incomplete DEF File");//$NON-NLS-1$
        }
        
        // test using archive
        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
        isValid = VDBReader.isValidVDBDefFileIncluded(vdbArchiveArray);
        if (isValid) {
            fail("VDB should not be valid, it contains an incomplete DEF File");//$NON-NLS-1$
        }
    }  
    
    
    
    public void testImportingVDBArchiveWithcompleteDefFile() throws Exception {

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
               
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier_VDB_with_DEF.vdb"});//$NON-NLS-1$

        // test using path
        boolean isValid = VDBReader.isValidVDBDefFileIncluded(path);
        if (!isValid) {
            fail("VDB should be valid, it contains an complete DEF File");//$NON-NLS-1$
        }
        
        // test using archive
        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
        isValid = VDBReader.isValidVDBDefFileIncluded(vdbArchiveArray);
        if (!isValid) {
            fail("VDB should be valid, it contains an complete DEF File");//$NON-NLS-1$
        }
        
        
    }     
    
    public void testImportingBadVDBDefn() throws Exception {

        //String vdbFile = "BadVdb.vdb"; //$NON-NLS-1$
        String path = FileUtils.buildDirectoryPath(new String [] {SmartTestSuite.getTestDataPath(), "vdbloader", "BadVdb.vdb"} ); //$NON-NLS-1$ //$NON-NLS-2$ 

        byte[] archive = ByteArrayHelper.toByteArray(new File(path));
        

        VDBDefn defn = VDBReader.loadVDBDefn("BadVDB", archive, null);//$NON-NLS-1$

        if (defn.getStatus() != VDBStatus.INACTIVE) {
            fail("VDB should have been in an INCOMPLETE state");//$NON-NLS-1$
        }
    }    

    
    public void testImportingPartsVDB_Def19430() throws Exception {

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier_VDB_with_DEF.vdb"});//$NON-NLS-1$

        // test using path
        boolean isValid = VDBReader.isValidVDBDefFileIncluded(path);
        if (!isValid) {
            fail("VDB should be valid, it contains an incomplete DEF File");//$NON-NLS-1$
        }
        
        // test using archive
        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
        
       
        VDBDefn vdbDefn = new BasicVDBDefn("PartsSupplierVDB");//$NON-NLS-1$
        
        boolean transferBindingsFromDefn = false;	// Keep binding info from vdbArchive.
        vdbDefn = VDBReader.loadVDBDefn(vdbDefn, vdbArchiveArray, transferBindingsFromDefn);
        
        if (vdbDefn.getConnectorBindings() == null || vdbDefn.getConnectorBindings().isEmpty()) {
            fail("VDB did not get loaded with the connector bindings");//$NON-NLS-1$
        }
    } 
    
    /*
     * Test transfer Defn Bindings, when the defn binding doesnt match any model in the vdbDefn
     */
    public void testTransferDefnBindings1() throws Exception {
    	VDBDefn vdbDefn = createBasicVDBDefn("TestVDB", "TestModel"); //$NON-NLS-1$ //$NON-NLS-2$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier_VDB_with_DEF.vdb"});//$NON-NLS-1$

        // test using path
        boolean isValid = VDBReader.isValidVDBDefFileIncluded(path);
        if (!isValid) {
            fail("VDB should be valid, it contains an incomplete DEF File");//$NON-NLS-1$
        }
        
        // test using archive
        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
                
        boolean transferBindingsFromDefn = true;	// Keep binding info from vdbArchive.
        vdbDefn = VDBReader.loadVDBDefn(vdbDefn, vdbArchiveArray, transferBindingsFromDefn);
        System.out.println();
        
        // Check the vdbDefn name
        if(!vdbDefn.getName().equals("TestVDB")) { //$NON-NLS-1$ 
        	fail("The vdbDefn name is not set to the expected Name: TestVDB"); //$NON-NLS-1$
        }
        
        // Check the vdbDefn version number
        if(!vdbDefn.getVersion().equals("2")) { //$NON-NLS-1$
        	fail("The vdbDefn version is not set to the expected Version: 2"); //$NON-NLS-1$
        }
        
        // Check the vdbDefn for number of connector types - should be 3
        int nTypes = vdbDefn.getConnectorTypes().size();
        if(nTypes!=3) {
        	fail("The vdbDefn should contain 3 connectorTypes, but contained "+nTypes); //$NON-NLS-1$
        }
        
        // Check the vdbDefn for number of connector bindings - should be 0.
        // The Binding modelName from the vdbDefn doesnt match the baseDefn
        int nBindings = vdbDefn.getConnectorBindings().size();
        if(nBindings!=0) {
        	fail("The vdbDefn should contain no connector bindings, but contained "+nBindings); //$NON-NLS-1$
        }
    } 
    
    /*
     * Test transfer Defn Bindings, when the defn binding does match a model in the vdbDefn
     */
    public void testTransferDefnBindings2() throws Exception {
    	VDBDefn vdbDefn = createBasicVDBDefn("TestVDB", "PartSupplier_Oracle"); //$NON-NLS-1$ //$NON-NLS-2$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier_VDB_with_DEF.vdb"});//$NON-NLS-1$

        // test using path
        boolean isValid = VDBReader.isValidVDBDefFileIncluded(path);
        if (!isValid) {
            fail("VDB should be valid, it contains an incomplete DEF File");//$NON-NLS-1$
        }
        
        // test using archive
        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
                
        boolean transferBindingsFromDefn = true;	// Keep binding info from vdbArchive.
        vdbDefn = VDBReader.loadVDBDefn(vdbDefn, vdbArchiveArray, transferBindingsFromDefn);
        System.out.println();
        
        // Check the vdbDefn name
        if(!vdbDefn.getName().equals("TestVDB")) { //$NON-NLS-1$
        	fail("The vdbDefn name is not set to the expected Name: TestVDB"); //$NON-NLS-1$
        }
        
        // Check the vdbDefn version number
        if(!vdbDefn.getVersion().equals("2")) { //$NON-NLS-1$
        	fail("The vdbDefn version is not set to the expected Version: 2"); //$NON-NLS-1$
        }
        
        // Check the vdbDefn for number of connector types - should be 3
        int nTypes = vdbDefn.getConnectorTypes().size();
        if(nTypes!=3) {
        	fail("The vdbDefn should contain 3 connectorTypes, but contained "+nTypes); //$NON-NLS-1$
        }
        
        // Check the vdbDefn for number of connector bindings - should be 1.
        // The Binding modelName from the vdbDefn does match the baseDefn
        int nBindings = vdbDefn.getConnectorBindings().size();
        if(nBindings!=1) {
        	fail("The vdbDefn should contain 1 connector binding, but contained "+nBindings); //$NON-NLS-1$
        }
    } 
    
    public void testDontTransferDefnBindings() throws Exception {
    	VDBDefn vdbDefn = createBasicVDBDefn("TestVDB", "TestModel"); //$NON-NLS-1$ //$NON-NLS-2$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);
        
        String path = FileUtils.buildDirectoryPath(new String[] {v.getVDBDefnLocation(), "PartsSupplier_VDB_with_DEF.vdb"});//$NON-NLS-1$

        // test using path
        boolean isValid = VDBReader.isValidVDBDefFileIncluded(path);
        if (!isValid) {
            fail("VDB should be valid, it contains an incomplete DEF File");//$NON-NLS-1$
        }
        
        // test using archive
        FileUtil fu = new FileUtil(path);
        byte[] vdbArchiveArray = fu.readBytes();
        
        if (vdbArchiveArray == null || vdbArchiveArray.length == 0) {
            fail("Unable to load " + v.getVDBFileLocation() + " into byte array.");  //$NON-NLS-1$ //$NON-NLS-2$          
        }
                
        boolean transferBindingsFromDefn = false;	// Keep binding info from vdbArchive.
        vdbDefn = VDBReader.loadVDBDefn(vdbDefn, vdbArchiveArray, transferBindingsFromDefn);
        
        // Check the vdbDefn name
        if(!vdbDefn.getName().equals("TestVDB")) { //$NON-NLS-1$
        	fail("The vdbDefn name is not set to the expected Name: TestVDB"); //$NON-NLS-1$
        }
        
        // Check the vdbDefn version number
        if(!vdbDefn.getVersion().equals("2")) { //$NON-NLS-1$
        	fail("The vdbDefn version is not set to the expected Version: 2"); //$NON-NLS-1$
        }
        
        // Check the vdbDefn for number of connector types - should be 2
        int nTypes = vdbDefn.getConnectorTypes().size();
        if(nTypes!=2) {
        	fail("The vdbDefn should contain 2 connectorTypes, but contained "+nTypes); //$NON-NLS-1$
        }
        
        // Check the vdbDefn for number of connector bindings - should be 2
        int nBindings = vdbDefn.getConnectorBindings().size();
        if(nBindings!=2) {
        	fail("The vdbDefn should contain 2 connector bindings, but contained "+nBindings); //$NON-NLS-1$
        }
    } 

    public void testLoadingVDBDefnIntoInputStream() throws Exception {

        String vdbFile = "PartsSupplier_42.DEF"; //$NON-NLS-1$

        VDBValidation v = VDBValidation.getValidation(VDBValidation.PARTS_VDB);

        VDBDefn defn = VDBReader.loadVDBDefn(vdbFile, v.getVDBDefnLocation());

        validate(v, defn);
        
        if (defn.getDescription() == null || defn.getDescription().length() == 0) {
            fail("Description for VDB " + vdbFile + " was not loaded.");//$NON-NLS-1$ //$NON-NLS-2$
        } 
              
        
        InputStream is = VDBReader.createVDBDefnInputStream(defn, new Properties());
        
        byte[] vdb = ByteArrayHelper.toByteArray(is);
        
        if (vdb == null || vdb.length == 0) {
            fail("VDB was not read from inputstream to a byte array"); //$NON-NLS-1$
        }
        
    }    
    

    private void validate(VDBValidation v,
                          VDBDefn defn) throws Exception {
        if (defn.getModels() == null || defn.getModels().size() == 0) {
            fail("VDB DEF file " + v.getVDBFileLocation() + " did not contain any models"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (defn.getFileName() == null) {
            fail("VDB DEF filename is null for VDB " + v.getVDBName()); //$NON-NLS-1$
        }
        VDBStream stream = defn.getVDBStream();
        if (stream == null) {
            fail("VDB byte array containing the vdb content is null for VDB " + v.getVDBName()); //$NON-NLS-1$
        }
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);

    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestVDBReader"); //$NON-NLS-1$
        suite.addTestSuite(TestVDBReader.class);

        return new TestSetup(suite) { // junit.extensions package

            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
            	ModelerCore.testLoadModelContainer();
            }

            @Override
            public void tearDown() {
            }
        };
    }
}

