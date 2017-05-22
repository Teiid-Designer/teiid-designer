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
import org.teiid.adminapi.AdminProcessingException;

public class DataSourceProperties extends ResultCallback {

	public DataSourceProperties() {
		// TODO Auto-generated constructor stub
	}
	private Properties dsProperties;
	DataSourceProperties(Properties props){
		this.dsProperties = props;
	}
	@Override
	public void onSuccess(ModelNode outcome, ModelNode result) throws AdminProcessingException {
		List<ModelNode> props = outcome.get("result").asList();
		for (ModelNode prop:props) {
			if (prop.getType().equals(ModelType.PROPERTY)) {
				org.jboss.dmr.Property p = prop.asProperty();
				ModelType type = p.getValue().getType();
				if (p.getValue().isDefined() && !type.equals(ModelType.LIST) && !type.equals(ModelType.OBJECT)) {
					if (p.getName().equals("driver-name")
							|| p.getName().equals("jndi-name")
							|| !AdminUtil.excludeProperty(p.getName())) {
						this.dsProperties.setProperty(p.getName(), p.getValue().asString());
					}
				}
			}
		}
	}
	@Override
	public void onFailure(String msg) throws AdminProcessingException {
	    // nothing required
	}
}
