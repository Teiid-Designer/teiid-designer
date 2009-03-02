/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce;

import java.util.Collection;
import java.util.Iterator;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

public class BindingGenerator {
	
	private static final String BINDING_TYPE_NAME = "Salesforce Connector"; //$NON-NLS-1$
	private static final String BINDING_USERNAME = "username"; //$NON-NLS-1$
	private static final String BINDING_PASSWORD = "password"; //$NON-NLS-1$
	private static final String BINDING_CONNECTION_URL = "URL"; //$NON-NLS-1$
	
	public static void createConnectorBinding(SalesforceImportWizardManager wizMan) {		
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
			ModelerDqpUtils.setPropertyValue(bind, BINDING_USERNAME, wizMan.getUsername());
			ModelerDqpUtils.setPropertyValue(bind, BINDING_PASSWORD, wizMan.getPassword());
			if(null != wizMan.getConnectionURL()) {
				ModelerDqpUtils.setPropertyValue(bind, BINDING_CONNECTION_URL, wizMan.getConnectionURL().toString());
			}
			configMan.addBinding(bind);
		} catch(Exception e) {
			// Don't throw this exception.  It will stop the importer from completing.  it is already logged.
		}
	}
	
	private static String generateUniqueName(String candidate) {
		String workingName = candidate;
		int num = 1;
		while(!ModelerDqpUtils.isUniqueBindingName(candidate)) {
			candidate = workingName + "_" + num++; //$NON-NLS-1$
		}
		return candidate;
	}
}
