/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v8;

import java.util.List;
import java.util.Properties;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;

public class ConnectionFactoryProperties  extends ResultCallback {
	private Properties dsProperties;
	private String deployedName;
	private String rarName;
	private String poolName;
	private AdminConnectionManager manager;

	ConnectionFactoryProperties(Properties props, String rarName, String deployedName, String poolName, AdminConnectionManager manager){
		this.dsProperties = props;
		this.rarName = rarName;
		this.deployedName = deployedName;
		this.poolName = poolName;
		this.manager = manager;
	}

	@Override
	public void onSuccess(ModelNode outcome, ModelNode result) throws AdminException {
		List<ModelNode> props = outcome.get("result").asList();
		for (ModelNode prop:props) {
			if (!prop.getType().equals(ModelType.PROPERTY)) {
				continue;
			}
			org.jboss.dmr.Property p = prop.asProperty();
			if (p.getName().equals("jndi-name")) {
				this.dsProperties.setProperty("jndi-name", p.getValue().asString());
			}
			if (!p.getValue().isDefined() || AdminUtil.excludeProperty(p.getName())) {
				continue;
			}
			if (p.getName().equals("archive")) {
				this.dsProperties.setProperty("driver-name", p.getValue().asString());
			}
			if (p.getName().equals("value")) {
				this.dsProperties.setProperty(this.poolName, p.getValue().asString());
			}
			else if (p.getName().equals("config-properties")) {
				List<ModelNode> configs = p.getValue().asList();
				for (ModelNode config:configs) {
					if (config.getType().equals(ModelType.PROPERTY)) {
						org.jboss.dmr.Property p1 = config.asProperty();
						//getConnectionFactoryProperties(rarName, dsProps, subsystem[0], subsystem[1], subsystem[2], subsystem[3], );
						manager.cliCall("read-resource",
								new String[] {"subsystem","resource-adapters",
										"resource-adapter",this.rarName,
										"connection-definitions",this.deployedName,
										"config-properties",p1.getName()}, null,
										new ConnectionFactoryProperties(this.dsProperties, this.rarName, this.deployedName, p1.getName(), manager));
					}
				}
			}
			else {
				this.dsProperties.setProperty(p.getName(), p.getValue().asString());
			}
		}
	}

	@Override
	public void onFailure(String msg) throws AdminProcessingException {
	    // nothing required
	}
}
