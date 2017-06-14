/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v9;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.teiid.adminapi.AdminProcessingException;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.PropertyDefinition.RestartType;
import org.teiid.adminapi.impl.PropertyDefinitionMetadata;

public class BuildPropertyDefinitions extends ResultCallback{
	private ArrayList<PropertyDefinition> propDefinitions = new ArrayList<PropertyDefinition>();

	@Override
	public void onSuccess(ModelNode outcome, ModelNode result) throws AdminProcessingException {
		if (result.getType().equals(ModelType.LIST)) {
			buildPropertyDefinitions(result.asList());
		}
		else if (result.get("attributes").isDefined()) {
				buildPropertyDefinitions(result.get("attributes").asList());
		}
	}

	@Override
	public void onFailure(String msg) throws AdminProcessingException {
		throw new AdminProcessingException(msg);
	}

	public ArrayList<PropertyDefinition> getPropertyDefinitions() {
		return this.propDefinitions;
	}

	private void buildPropertyDefinitions(List<ModelNode> propsNodes) {
    	for (ModelNode node:propsNodes) {
    		PropertyDefinitionMetadata def = new PropertyDefinitionMetadata();
    		Set<String> keys = node.keys();

    		String name = keys.iterator().next();
    		if (AdminUtil.excludeProperty(name)) {
    			continue;
    		}
    		def.setName(name);
    		node = node.get(name);

    		if (node.hasDefined("display")) {
    			def.setDisplayName(node.get("display").asString());
    		}
    		else {
    			def.setDisplayName(name);
    		}

    		if (node.hasDefined("description")) {
    			def.setDescription(node.get("description").asString());
    		}

    		if (node.hasDefined("allowed")) {
    			List<ModelNode> allowed = node.get("allowed").asList();
    			ArrayList<String> list = new ArrayList<String>();
    			for(ModelNode m:allowed) {
    				list.add(m.asString());
    			}
    			def.setAllowedValues(list);
    		}

    		if (node.hasDefined("required")) {
    			def.setRequired(node.get("required").asBoolean());
    		}

    		if (node.hasDefined("owner")) {
                def.addProperty("owner", node.get("owner").asString());
            }   

    		if (node.hasDefined("read-only")) {
    			String access = node.get("read-only").asString();
    			def.setModifiable(!Boolean.parseBoolean(access));
    		}

    		if (node.hasDefined("access-type")) {
    			String access = node.get("access-type").asString();
    			if ("read-write".equals(access)) {
    				def.setModifiable(true);
    			}
    			else {
    				def.setModifiable(false);
    			}
    		}

    		if (node.hasDefined("advanced")) {
    		    String advanced = node.get("advanced").asString();
    		    def.setAdvanced(Boolean.parseBoolean(advanced));
    		}

    		if (node.hasDefined("masked")) {
    		    String masked = node.get("masked").asString();
    		    def.setMasked(Boolean.parseBoolean(masked));
    		}

    		if (node.hasDefined("category")) {
    		    def.setCategory(node.get("category").asString());
    		}

    		if (node.hasDefined("restart-required")) {
    			def.setRequiresRestart(RestartType.NONE);
    		}

    		String type = node.get("type").asString();
    		if (ModelType.STRING.name().equals(type)) {
    			def.setPropertyTypeClassName(String.class.getName());
    		}
    		else if (ModelType.INT.name().equals(type)) {
    			def.setPropertyTypeClassName(Integer.class.getName());
    		}
    		else if (ModelType.LONG.name().equals(type)) {
    			def.setPropertyTypeClassName(Long.class.getName());
    		}
    		else if (ModelType.BOOLEAN.name().equals(type)) {
    			def.setPropertyTypeClassName(Boolean.class.getName());
    		}
    		else if (ModelType.BIG_INTEGER.name().equals(type)) {
    			def.setPropertyTypeClassName(BigInteger.class.getName());
    		}
    		else if (ModelType.BIG_DECIMAL.name().equals(type)) {
    			def.setPropertyTypeClassName(BigDecimal.class.getName());
    		}

    		if (node.hasDefined("default")) {
        		if (ModelType.STRING.name().equals(type)) {
        			def.setDefaultValue(node.get("default").asString());
        		}
        		else if (ModelType.INT.name().equals(type)) {
        			def.setDefaultValue(node.get("default").asInt());
        		}
        		else if (ModelType.LONG.name().equals(type)) {
        			def.setDefaultValue(node.get("default").asLong());
        		}
        		else if (ModelType.BOOLEAN.name().equals(type)) {
        			def.setDefaultValue(node.get("default").asBoolean());
        		}
        		else if (ModelType.BIG_INTEGER.name().equals(type)) {
        			def.setDefaultValue(node.get("default").asBigInteger());
        		}
        		else if (ModelType.BIG_DECIMAL.name().equals(type)) {
        			def.setDefaultValue(node.get("default").asBigDecimal());
        		}
    		}
    		this.propDefinitions.add(def);
    	}
	}
}