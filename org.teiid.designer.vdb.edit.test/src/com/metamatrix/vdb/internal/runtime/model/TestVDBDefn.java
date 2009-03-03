/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.runtime.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.api.ProductType;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.vdb.runtime.BasicVDBDefn;


/**
 */
public class TestVDBDefn extends TestCase {

    private BasicConfigurationObjectEditor editor = new BasicConfigurationObjectEditor(false);

    public TestVDBDefn(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
    }
    
    public void testAddingRemovingConnectorTypes() throws Exception {
        String ctName = "ConnectorType";//$NON-NLS-1$
         
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$

        int x = 50;
        for (int i=0; i < x; i++) {
            ConnectorBindingType ct = creatConnectorType(ctName + i);
            vdbDefn.addConnectorType(ct);
        }
        
        validateConnectorTypeSize(vdbDefn, x);
        
        for (int i=0; i < x; i++) {
            vdbDefn.removeConnectorType(ctName + i);
        }  

        validateConnectorTypeSize(vdbDefn, 0);
        
    }
    
    public void testAddingRemovingConnectorBinding() throws Exception {
        String ctName = "ConnectorType";//$NON-NLS-1$
        String cbName = "ConnectorBinding1";//$NON-NLS-1$
        String cbRouting = "mmuid:1234565";//$NON-NLS-1$
        
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
    
        ConnectorBindingType ct = creatConnectorType(ctName);
        
        ConnectorBinding cb = createConnectorBinding(cbName, cbRouting, ct);  
        
        vdbDefn.addConnectorType(ct);
        vdbDefn.addConnectorBinding(cb);
        
        validateConnectorBindingSize(vdbDefn, 1);

        validateConnectorBinding(vdbDefn, cbName, cbRouting);
        
        validateConnectorTypeSize(vdbDefn, 1);
        
        validateConnectorType(vdbDefn, ctName);
        
        
        vdbDefn.removeConnectorBinding(cbName);
        
        validateConnectorBindingSize(vdbDefn, 0);
 
        validateConnectorTypeSize(vdbDefn, 0);          
       
    }   
        
        
    
    /**
     * This addes the models one at a time, and then adds the bindings
     * to the defn based on the model.  Validation ensures the model-to-binding
     * mapping association was done correctly when the binding was added. 
     * @throws Exception
     * @since 4.2
     */
    public void testAddingAndRemovingModels() throws Exception {
        String ctName = "ConnectorType";//$NON-NLS-1$
        String cbName = "ConnectorBinding1";//$NON-NLS-1$
        String cbRouting = "mmuid:1234565";//$NON-NLS-1$
        String modelName = "ModelName";//$NON-NLS-1$

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        
        int modelscnt=20;
        int bindingIt = 300;
        int bindingcnt = 0;
        
        // load the vdbdefn
        for (int m=0; m < modelscnt; m++ ) {
            BasicVDBModelDefn mi = new BasicVDBModelDefn(modelName + m);
            
            vdbDefn.addModelInfo(mi); 
            
            for (int b=0; b<bindingIt; b++) {
            
                String typeName = ctName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBindingType ct = creatConnectorType(typeName);
                String bindingName = cbName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBinding cb = createConnectorBinding(bindingName, cbRouting + "_" + m + "_" + b, ct); //$NON-NLS-1$ //$NON-NLS-2$
                
                vdbDefn.addConnectorType(ct);
                
                vdbDefn.addConnectorBinding(mi.getName(), cb);
                
                validateModelToBinding(vdbDefn, mi.getName(), cb.getName(), cb.getRoutingUUID());
                ++bindingcnt;
            }  

        }
            
        validateModelCount(vdbDefn, modelscnt);           
        validateConnectorTypeSize(vdbDefn, bindingcnt);   
        validateConnectorBindingSize(vdbDefn, bindingcnt);
        
            
         // remove
        Iterator modelNames = vdbDefn.getModelNames().iterator();
        while(modelNames.hasNext()) {
            String mn = (String) modelNames.next();            
            vdbDefn.removeModelInfo(mn);            
        }
       
        validateModelCount(vdbDefn, 0);           
        validateConnectorTypeSize(vdbDefn, 0);   
        validateConnectorBindingSize(vdbDefn, 0);
    }  
    
    /**
     * DEF 19121 - When removeconnectorbinding(bindingname) is canned on BasicVDBDefn,  
     * any models that were referencing this binding were not removed. 
     * 
     * @throws Exception
     * @since 4.3
     */
    public void testAddingAndRemovingModels3() throws Exception {
        String ctName = "ConnectorType";//$NON-NLS-1$
        String cbName = "ConnectorBinding1";//$NON-NLS-1$
        String cbRouting = "mmuid:1234565";//$NON-NLS-1$
        String modelName = "ModelName";//$NON-NLS-1$

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        
        int modelscnt=1;
        int bindingIt = 1;
        int bindingcnt = 0;
        
        String bindingName = null; 
        ConnectorBinding cb = null;
        // load the vdbdefn
        for (int m=0; m < modelscnt; m++ ) {
            BasicVDBModelDefn mi = new BasicVDBModelDefn(modelName + m);
            
            vdbDefn.addModelInfo(mi); 
            
            for (int b=0; b<bindingIt; b++) {
            
                String typeName = ctName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBindingType ct = creatConnectorType(typeName);
                bindingName = cbName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                cb = createConnectorBinding(bindingName, cbRouting + "_" + m + "_" + b, ct); //$NON-NLS-1$ //$NON-NLS-2$
                
                vdbDefn.addConnectorType(ct);
                
                vdbDefn.addConnectorBinding(mi.getName(), cb);
                
                validateModelToBinding(vdbDefn, mi.getName(), cb.getName(), cb.getRoutingUUID());
                ++bindingcnt;
            }  

        }
            
        validateModelCount(vdbDefn, modelscnt);           
        validateConnectorTypeSize(vdbDefn, bindingcnt);   
        validateConnectorBindingSize(vdbDefn, bindingcnt);
        
        vdbDefn.removeConnectorBinding(bindingName);
        
        if (vdbDefn.isBindingInUse(cb)) {
            fail("CB " + bindingName + " is still in use");//$NON-NLS-1$ //$NON-NLS-2$
        }
            
        if (vdbDefn.getConnectorTypes().size() > 0) {
            fail("CB Types was not removed");//$NON-NLS-1$
        }

    }      

    /**
     * This adds the models and bindings seperate and then
     * validates to ensure the model-to-binding mapping
     * is correct 
     * @throws Exception
     * @since 4.2
     */
    public void testAddingAndRemovingModels2() throws Exception {
        String ctName = "ConnectorType";//$NON-NLS-1$
        String cbName = "ConnectorBinding1";//$NON-NLS-1$
        String cbRouting = "mmuid:1234565";//$NON-NLS-1$
        String modelName = "ModelName";//$NON-NLS-1$

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        
        int modelscnt=20;
        int bindingIt = 300;
        int bindingcnt = 0;
        // load the vdbdefn
        List models = new ArrayList(modelscnt);
        Map bindings = new HashMap(modelscnt * bindingIt);
        for (int m=0; m < modelscnt; m++ ) {
            BasicVDBModelDefn mi = new BasicVDBModelDefn(modelName + m);
            vdbDefn.addModelInfo(mi);
            models.add(mi);
            
            for (int b=0; b<bindingIt; b++) {
            
                String typeName = ctName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBindingType ct = creatConnectorType(typeName);
                String bindingName = cbName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBinding cb = createConnectorBinding(bindingName, cbRouting + "_" + m + "_" + b, ct); //$NON-NLS-1$ //$NON-NLS-2$
                
                vdbDefn.addConnectorType(ct);
                vdbDefn.addConnectorBinding(mi.getName(), cb);
                
                bindings.put(cb.getFullName(), cb);
//                mi.addConnectorBindingByName(cb.getFullName());
                ++bindingcnt;
            }  

        }
        
//        vdbDefn.setModelInfos(models);
//        vdbDefn.setConnectorBindings(bindings);
            
        validateModelCount(vdbDefn, modelscnt);           
        validateConnectorTypeSize(vdbDefn, bindingcnt);   
        validateConnectorBindingSize(vdbDefn, bindingcnt);
        
        Map mtob = vdbDefn.getModelToBindingMappings();
        for (Iterator it=mtob.keySet().iterator(); it.hasNext();) {
            String mname=(String) it.next();
            List routings=(List) mtob.get(mname);
            String routing = (String) routings.get(0);
            
            if (vdbDefn.getModel(mname) == null ) {
                fail("No model found for " + mname);//$NON-NLS-1$ 
            }
            ConnectorBinding cb = vdbDefn.getConnectorBindingByRouting(routing);
            if (cb == null) {
                fail("No binding found for routing " + routing);//$NON-NLS-1$ 
            }
            
            validateModelToBinding(vdbDefn, mname, cb.getName(), routing);
            
        }

         
    }  
    
    public void testSimpleAddRemove() {
        String ctName = "ConnectorType";//$NON-NLS-1$
        String cbName = "ConnectorBinding1";//$NON-NLS-1$
        String cbRouting = "mmuid:1234565";//$NON-NLS-1$
        String modelName = "ModelName";//$NON-NLS-1$

        
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        
        ConnectorBindingType ct = creatConnectorType(ctName);

        ConnectorBinding cb = createConnectorBinding(cbName, cbRouting, ct);
        
        vdbDefn.addConnectorType(ct);
        
        
        validateConnectorTypeSize(vdbDefn, 1);                  
        validateConnectorType(vdbDefn, ctName);
        
        BasicVDBModelDefn mi = new BasicVDBModelDefn(modelName); 
        vdbDefn.addModelInfo(mi); 
        vdbDefn.addConnectorBinding(mi.getName(), cb);
        
        validateModelCount(vdbDefn, 1);
        validateModelToBinding(vdbDefn, modelName, cbName, cbRouting);
        
        validateMtoBSize(vdbDefn, 1);
        
        validateConnectorBindingSize(vdbDefn, 1);

        
        vdbDefn.removeConnectorBinding(modelName, cbName);
        validateModelCount(vdbDefn, 1);
        validateConnectorBindingSize(vdbDefn, 0);
        validateMtoBSize(vdbDefn, 0);
        
        vdbDefn.removeModelInfo(modelName);
        validateModelCount(vdbDefn, 0);

    }
    
    /**
     * Exception:
     * java.lang.NullPointerException
     * at com.metamatrix.vdb.edit.loader.VDBWriter.writeVDBDefn(VDBWriter.java:143)
     * at com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper.saveDefn(VdbDefnHelper.java:454) 
     * 
     * is caused because the model had a ConnectorBinding UUID for which the binding
     * did not exist at the VDBDefn level.
     * @throws Exception
     * @since 4.2
     */
    public void testDef18613() throws Exception {
        String ctName = "ConnectorType";//$NON-NLS-1$
        String cbName = "ConnectorBinding1";//$NON-NLS-1$
        String cbRouting = "mmuid:1234565";//$NON-NLS-1$
        String modelName = "ModelName";//$NON-NLS-1$

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        
        int modelscnt=20;
        int bindingIt = 300;
        int bindingcnt = 0;
        // load the vdbdefn
        List models = new ArrayList(modelscnt);
        Map bindings = new HashMap(modelscnt * bindingIt);
        for (int m=0; m < modelscnt; m++ ) {
            BasicVDBModelDefn mi = new BasicVDBModelDefn(modelName + m);
            
            models.add(mi);
            
            for (int b=0; b<bindingIt; b++) {
            
                String typeName = ctName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBindingType ct = creatConnectorType(typeName);
                String bindingName = cbName + "_" + m + "_" + b; //$NON-NLS-1$ //$NON-NLS-2$
                ConnectorBinding cb = createConnectorBinding(bindingName, cbRouting + "_" + m + "_" + b, ct); //$NON-NLS-1$ //$NON-NLS-2$
                
                vdbDefn.addConnectorType(ct);
                vdbDefn.addConnectorBinding(cb);
                
                bindings.put(cb.getFullName(), cb);
                mi.addConnectorBindingByName(cb.getFullName());
                ++bindingcnt;
            }  

        }
        
        vdbDefn.setModelInfos(models);
        //vdbDefn.setConnectorBindings(bindings);
            
        validateModelCount(vdbDefn, modelscnt);           
        validateConnectorTypeSize(vdbDefn, bindingcnt);   
        validateConnectorBindingSize(vdbDefn, bindingcnt);
        
        Map mtob = vdbDefn.getModelToBindingMappings();
        for (Iterator it=mtob.keySet().iterator(); it.hasNext();) {
            String mname=(String) it.next();
            List routings=(List) mtob.get(mname);
            String routing = (String) routings.get(0);
            
            if (vdbDefn.getModel(mname) == null ) {
                fail("No model found for " + mname);//$NON-NLS-1$ 
            }
            ConnectorBinding cb = vdbDefn.getConnectorBindingByRouting(routing);
            if (cb == null) {
                fail("No binding found for routing " + routing);//$NON-NLS-1$ 
            }
            
            validateModelToBinding(vdbDefn, mname, cb.getName(), routing);
            
        }
        
        
    }
    
    
    /**
     * Tests BasicVDBDefn.removeUnmappedBindings() 
     * 
     * @since 4.3
     */
    public void testRemoveUnmappedBindings() {
        String ctName1 = "ConnectorType1";//$NON-NLS-1$
        String cbName1 = "ConnectorBinding1";//$NON-NLS-1$
        String cbUUID1 = "mmuid:111111";//$NON-NLS-1$
        String modelName1 = "ModelName1";//$NON-NLS-1$
        
        String ctName2 = "ConnectorType2";//$NON-NLS-1$
        String cbName2 = "ConnectorBinding2";//$NON-NLS-1$
        String cbUUID2 = "mmuid:222222";//$NON-NLS-1$
        
        
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        
        
        //add a model with a connector binding
        BasicVDBModelDefn modelDefn = new BasicVDBModelDefn(modelName1);
        vdbDefn.addModelInfo(modelDefn);             

        ConnectorBindingType ct1 = creatConnectorType(ctName1);
        ConnectorBinding cb1 = createConnectorBinding(cbName1, cbUUID1, ct1); 
        vdbDefn.addConnectorType(ct1);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb1);

        
        //add a connector binding with no model
        ConnectorBindingType ct2 = creatConnectorType(ctName2);
        ConnectorBinding cb2 = createConnectorBinding(cbName2, cbUUID2, ct2); 
        vdbDefn.addConnectorType(ct2);        
        vdbDefn.addConnectorBinding(cb2);

        //check for expected counts
        validateModelCount(vdbDefn, 1);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);
        
        
        //remove unmapped bindings
        vdbDefn.removeUnmappedBindings();
        
        
        //check for expected counts
        validateModelCount(vdbDefn, 1);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 1);
        validateConnectorBinding(vdbDefn, cbName1, cbUUID1);
        
    }
    
    /**
     * This simple test renames the model and expects everthing else to remain the same
     * @see Defect 21354
     * @throws Exception
     * @since 4.2
     */
    public void testRenamingModels1() throws Exception {
        String ctName1 = "ConnectorType1";//$NON-NLS-1$
        String cbName1 = "ConnectorBinding1";//$NON-NLS-1$
        String cbUUID1 = "mmuid:111111";//$NON-NLS-1$
        String modelName = "ModelName1";//$NON-NLS-1$
        String newModelName = "NewModelName";//$NON-NLS-1$
        
        String ctName2 = "ConnectorType2";//$NON-NLS-1$
        String cbName2 = "ConnectorBinding2";//$NON-NLS-1$
        String cbUUID2 = "mmuid:222222";//$NON-NLS-1$
        
        
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        
        
        //add a model with a connector binding
        BasicVDBModelDefn modelDefn = new BasicVDBModelDefn(modelName);
        vdbDefn.addModelInfo(modelDefn);             

        ConnectorBindingType ct1 = creatConnectorType(ctName1);
        ConnectorBinding cb1 = createConnectorBinding(cbName1, cbUUID1, ct1); 
        vdbDefn.addConnectorType(ct1);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb1);

        
        //add a connector binding with no model
        ConnectorBindingType ct2 = creatConnectorType(ctName2);
        ConnectorBinding cb2 = createConnectorBinding(cbName2, cbUUID2, ct2); 
        vdbDefn.addConnectorType(ct2);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb2);
        

        //check for expected counts
        validateModelCount(vdbDefn, 1);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);

        vdbDefn.renameModelInfo(modelName, newModelName);
        
        //check for expected counts - the counts should be same 
        // and to check that nothing else was removed accidentally        
        validateModelCount(vdbDefn, 1);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);

        ModelInfo mi = vdbDefn.getModel(newModelName);
        if (mi == null) {
            fail("Newly renamed model was not found");//$NON-NLS-1$
        }        
        
    } 
    
    public void testRenamingModels2() throws Exception {
        String ctName1 = "ConnectorType1";//$NON-NLS-1$
        String cbName1 = "ConnectorBinding1";//$NON-NLS-1$
        String cbUUID1 = "mmuid:111111";//$NON-NLS-1$
        String modelName1 = "ModelName1";//$NON-NLS-1$
        String modelName2 = "ModelName2";//$NON-NLS-1$
        String newModelName = "NewModelName";//$NON-NLS-1$
       
        String ctName2 = "ConnectorType2";//$NON-NLS-1$
        String cbName2 = "ConnectorBinding2";//$NON-NLS-1$
        String cbUUID2 = "mmuid:222222";//$NON-NLS-1$
        
        
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        
        
        //add a model with a connector binding
        BasicVDBModelDefn modelDefn = new BasicVDBModelDefn(modelName1);
        vdbDefn.addModelInfo(modelDefn);             

        ConnectorBindingType ct1 = creatConnectorType(ctName1);
        ConnectorBinding cb1 = createConnectorBinding(cbName1, cbUUID1, ct1); 
        vdbDefn.addConnectorType(ct1);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb1);

        
        //add a connector binding with no model
        ConnectorBindingType ct2 = creatConnectorType(ctName2);
        ConnectorBinding cb2 = createConnectorBinding(cbName2, cbUUID2, ct2); 
        vdbDefn.addConnectorType(ct2);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb2);

        
        //---------------
        BasicVDBModelDefn modelDefn2 = new BasicVDBModelDefn(modelName2);
        vdbDefn.addModelInfo(modelDefn2);
        vdbDefn.addConnectorBinding(modelDefn2.getName(), cb2);
        

        //check for expected counts
        validateModelCount(vdbDefn, 2);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);
        
        
        vdbDefn.renameModelInfo(modelName1, newModelName);
        
        
        //check for expected counts
        validateModelCount(vdbDefn, 2);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);
        
        ModelInfo mi = vdbDefn.getModel(newModelName);
        if (mi == null) {
            fail("Newly renamed model was not found");//$NON-NLS-1$
        }
        if (mi.getConnectorBindingNames().size() != 2) {
            fail("Model should have had 2 bindings after the rename");//$NON-NLS-1$
        }
        
     
    }  
    
    
    /**
     * This simple test renames the connectorbinding and expects everthing else to remain the same
     * @see Defect 21354
     * @throws Exception
     * @since 4.2
     */
    public void testRenamingBinding1() throws Exception {
        String ctName1 = "ConnectorType1";//$NON-NLS-1$
        String cbName1 = "ConnectorBinding1";//$NON-NLS-1$
        String cbUUID1 = "mmuid:111111";//$NON-NLS-1$
        String modelName = "ModelName1";//$NON-NLS-1$
        
        String ctName2 = "ConnectorType2";//$NON-NLS-1$
        String cbName2 = "ConnectorBinding2";//$NON-NLS-1$
        String cbUUID2 = "mmuid:222222";//$NON-NLS-1$
        String newcbName2 = "NewConnectorBinding2";//$NON-NLS-1$
        
        
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        
        
        //add a model with a connector binding
        BasicVDBModelDefn modelDefn = new BasicVDBModelDefn(modelName);
        vdbDefn.addModelInfo(modelDefn);             

        ConnectorBindingType ct1 = creatConnectorType(ctName1);
        ConnectorBinding cb1 = createConnectorBinding(cbName1, cbUUID1, ct1); 
        vdbDefn.addConnectorType(ct1);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb1);

        
        //add a connector binding with no model
        ConnectorBindingType ct2 = creatConnectorType(ctName2);
        ConnectorBinding cb2 = createConnectorBinding(cbName2, cbUUID2, ct2); 
        vdbDefn.addConnectorType(ct2);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb2);
        

        //check for expected counts
        validateModelCount(vdbDefn, 1);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);

        vdbDefn.renameConnectorBinding(cbName2, newcbName2);
        
        //check for expected counts - the counts should be same 
        // and to check that nothing else was removed accidentally        
        validateModelCount(vdbDefn, 1);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);

        if (vdbDefn.getConnectorBindingByName(cbName2) != null) {
            fail("Old binding " + cbName2 + " was not renamed");//$NON-NLS-1$ //$NON-NLS-2$
        }

        if (vdbDefn.getConnectorBindingByName(newcbName2) == null) {
            fail("New binding " + newcbName2 + " was not found");//$NON-NLS-1$ //$NON-NLS-2$
        }
        
        
        ModelInfo mi = vdbDefn.getModel(modelDefn.getName());
        if (mi == null) {
            fail("Renamed binding caused model to go missing");//$NON-NLS-1$
        }        
        
        if (!mi.getConnectorBindingNames().contains(newcbName2)) {
            fail("Renamed binding was not found in the model");//$NON-NLS-1$
        }
        
    } 
    
 
    public void testRenamingBinding2() throws Exception {
        String ctName1 = "ConnectorType1";//$NON-NLS-1$
        String cbName1 = "ConnectorBinding1";//$NON-NLS-1$
        String cbUUID1 = "mmuid:111111";//$NON-NLS-1$
        String modelName1 = "ModelName1";//$NON-NLS-1$
        String modelName2 = "ModelName2";//$NON-NLS-1$
       
        String ctName2 = "ConnectorType2";//$NON-NLS-1$
        String cbName2 = "ConnectorBinding2";//$NON-NLS-1$
        String cbUUID2 = "mmuid:222222";//$NON-NLS-1$
        String newcbName2 = "NewConnectorBinding2";//$NON-NLS-1$
        
        
        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        
        
        //add a model with a connector binding
        BasicVDBModelDefn modelDefn = new BasicVDBModelDefn(modelName1);
        vdbDefn.addModelInfo(modelDefn);             

        ConnectorBindingType ct1 = creatConnectorType(ctName1);
        ConnectorBinding cb1 = createConnectorBinding(cbName1, cbUUID1, ct1); 
        vdbDefn.addConnectorType(ct1);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb1);

        
        //add a connector binding with no model
        ConnectorBindingType ct2 = creatConnectorType(ctName2);
        ConnectorBinding cb2 = createConnectorBinding(cbName2, cbUUID2, ct2); 
        vdbDefn.addConnectorType(ct2);        
        vdbDefn.addConnectorBinding(modelDefn.getName(), cb2);

        
        //---------------
        BasicVDBModelDefn modelDefn2 = new BasicVDBModelDefn(modelName2);
        vdbDefn.addModelInfo(modelDefn2);
        vdbDefn.addConnectorBinding(modelDefn2.getName(), cb2);
        

        //check for expected counts
        validateModelCount(vdbDefn, 2);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);
        
        
        vdbDefn.renameConnectorBinding(cbName2, newcbName2);
        
        
        //check for expected counts
        validateModelCount(vdbDefn, 2);           
        validateConnectorTypeSize(vdbDefn, 2);   
        validateConnectorBindingSize(vdbDefn, 2);
        
        if (vdbDefn.getConnectorBindingByName(cb2.getFullName()) != null) {
            fail("Old binding " + cbName2 + " was not renamed");//$NON-NLS-1$ //$NON-NLS-2$
        }

        if (vdbDefn.getConnectorBindingByName(newcbName2) == null) {
            fail("New binding " + newcbName2 + " was not found");//$NON-NLS-1$ //$NON-NLS-2$
        }
        
        
        ModelInfo mi = vdbDefn.getModel(modelDefn.getName());
        if (mi == null) {
            fail("Renamed binding caused model1 to go missing");//$NON-NLS-1$
        }        
        
        if (!mi.getConnectorBindingNames().contains(newcbName2)) {
            fail("Renamed binding was not found in the model1");//$NON-NLS-1$
        }
        
        mi = vdbDefn.getModel(modelDefn2.getName());
        if (mi == null) {
            fail("Renamed binding caused model2 to go missing");//$NON-NLS-1$
        }        
        
        if (!mi.getConnectorBindingNames().contains(newcbName2)) {
            fail("Renamed binding was not found in the model2");//$NON-NLS-1$
        }        
    }  
    
    
    
    
    private void validateConnectorTypeSize(VDBDefn vdbDefn, int expectedSize) {
        int cts = vdbDefn.getConnectorTypes().size();
        if (cts != expectedSize) {
            fail("Should have found " + expectedSize + " connector types, but it has " + cts);//$NON-NLS-1$ //$NON-NLS-2$
        }  
    }
    
    private void validateConnectorBindingSize(VDBDefn vdbDefn, int expectedSize) {
        int s = vdbDefn.getConnectorBindings().size();
        if (s != expectedSize) {
            fail("Should have found " + expectedSize + " connector bindings, but it has " + s);//$NON-NLS-1$ //$NON-NLS-2$
        } 
    }    
    
    private void validateConnectorType(VDBDefn vdbDefn, String ctName) {
        ConnectorBindingType findct = (ConnectorBindingType) vdbDefn.getConnectorType(ctName);
        
        if (findct == null) {
            fail("Did not find Connector type " + ctName + " that was added to the VDBDefn");//$NON-NLS-1$ //$NON-NLS-2$
        }
        if (!findct.getName().equals(ctName)) {
            fail("Did not find the corrector Connector type of " + ctName + " but found " + findct.getName());//$NON-NLS-1$ //$NON-NLS-2$
        }
        
    }
    
    private void validateConnectorBinding(VDBDefn vdbDefn, String cbName, String cbRouting) {
        ConnectorBinding findcb = vdbDefn.getConnectorBindingByRouting(cbRouting);
        if (findcb == null) {
            fail("Did not find ConnectorBinding " + cbName + " that was added to the VDBDefn");//$NON-NLS-1$ //$NON-NLS-2$
        }
        if (!findcb.getName().equals(cbName)) {
            fail("Did not find the corrector Connector Binding of " + cbName + " but found " + findcb.getName());//$NON-NLS-1$ //$NON-NLS-2$
        }

    }
    
    private void validateModelCount(VDBDefn vdbDefn, int cnt) {
        int s = vdbDefn.getModelNames().size();
        if (s != cnt) {
            fail("Should have had " + cnt + " model name(s), but found " + s);//$NON-NLS-1$ //$NON-NLS-2$
        }
        
        s = vdbDefn.getModels().size();
        if (s != cnt) {
            fail("Should have found " + cnt + " model(s) when getModels is called, but found " + s);//$NON-NLS-1$ //$NON-NLS-2$
        }

    }
    
    private void validateModel(VDBDefn vdbDefn, String modelName) {
        if (vdbDefn.getModel(modelName) == null) {
            fail("Model " + modelName + " was not found");//$NON-NLS-1$ //$NON-NLS-2$
        } 
       
    }
    
    private void validateModelToBinding(VDBDefn vdbDefn, String modelName, String cbName, String cbRouting) {
        validateModel(vdbDefn, modelName); 
        validateConnectorBinding(vdbDefn, cbName, cbRouting);
       
        Map mtob = vdbDefn.getModelToBindingMappings();
        if (!mtob.containsKey(modelName)) {
            fail("Model " + modelName + " was not found in the model-to-binding mapping");//$NON-NLS-1$ //$NON-NLS-2$
        }
        
        List routings = (List) mtob.get(modelName);
        if (!routings.contains(cbRouting)) {
            fail("Binding routing " + cbRouting + " was not mapped to model " + modelName);//$NON-NLS-1$ //$NON-NLS-2$
        }
        
    }
       
    
    private void validateMtoBSize(VDBDefn vdbDefn, int cnt) {
        if (vdbDefn.getModelToBindingMappings().size() != cnt) {
            fail("Should have found " + cnt + " in the model-to-binding mappings");//$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
 
    private ConnectorBindingType creatConnectorType(String name) {
        ConnectorBindingType ct = (ConnectorBindingType) editor.createComponentType(ComponentType.CONNECTOR_COMPONENT_TYPE_CODE, 
                                                                                    name, 
                                                                                    ConnectorBindingType.CONNECTOR_TYPE_ID, ProductType.PRODUCT_SUPER_TYPE_ID, true, false); 
        return ct;
    }
    
    private ConnectorBinding createConnectorBinding(String name, String routingID, ConnectorBindingType ct) {
        ConnectorBinding cb =  editor.createConnectorComponent(Configuration.NEXT_STARTUP_ID, 
                                                               (ComponentTypeID) ct.getID(), 
                                                               name,
                                                               routingID);
        return cb;

    }
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);

    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestVDBDefn.class);

        return suite;
    }
}

