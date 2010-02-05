/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

import com.metamatrix.modeler.core.workspace.ModelResource;

public class BindingGenerator {

    //	private static final String BINDING_TYPE_NAME = "Salesforce Connector"; //$NON-NLS-1$
    //	private static final String BINDING_USERNAME = "username"; //$NON-NLS-1$
    //	private static final String BINDING_PASSWORD = "password"; //$NON-NLS-1$
    //	private static final String BINDING_CONNECTION_URL = "URL"; //$NON-NLS-1$

    public static void createConnectorBinding( SalesforceImportWizardManager wizMan,
                                               ModelResource modelResource ) {
        /*
        final String suffix = ".xmi"; //$NON-NLS-1$
        
        String modelName = wizMan.getTargetModelName();
        if(modelName.endsWith(suffix)) {
        	modelName = modelName.substring(0, modelName.length() - suffix.length());
        }
        
        ConfigurationManager configMan = DqpPlugin.getInstance().getConfigurationManager();
        Collection<ComponentType> types = configMan.getConnectorTypes();
        ComponentType type = null;
        for(Iterator<ComponentType> iter = types.iterator(); iter.hasNext();) {
        	ComponentType tp = iter.next();
        	if(tp.getName().equals(BINDING_TYPE_NAME)) { 
        		type = tp; 
        		break;
        	}
        }
        if(type == null) return;
        
        String bindingName = generateUniqueName(modelName);
        try {
        	com.metamatrix.common.config.api.ConnectorBinding bind = configMan.createConnectorBinding(type, bindingName, false);		
        	
        	try {
        	    ModelerDqpUtils.setPropertyValue(bind, BINDING_USERNAME, wizMan.getUsername());
        	} catch (Exception e) {
        	    // property was not set
        	}
        	
        	try {
        	    ModelerDqpUtils.setPropertyValue(bind, BINDING_PASSWORD, wizMan.getPassword());
        	} catch (Exception e) {
        	    // property was not set
        	}

        	if(null != wizMan.getConnectionURL()) {
        	    try {
        	        ModelerDqpUtils.setPropertyValue(bind, BINDING_CONNECTION_URL, wizMan.getConnectionURL().toString());
        	    } catch (Exception e) {
        	        // property was not set
        	    }
        	}
        	configMan.addBinding(bind);
        	DqpPlugin.getWorkspaceConfig().createSourceBinding(modelResource, bind);
        	
        } catch(Exception e) {
        	// Don't throw this exception.  It will stop the importer from completing.  it is already logged.
        }
         */
    }

    /*
    private static String generateUniqueName(String candidate) {
    	
    	String workingName = candidate;
    	
    	int num = 1;
    	while(!ModelerDqpUtils.isUniqueBindingName(candidate)) {
    		candidate = workingName + "_" + num++; //$NON-NLS-1$
    	}
    	return candidate;
    	 
    }
    */
}
