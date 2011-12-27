package org.teiid.designer.runtime.connection;

import java.util.Properties;

import com.metamatrix.core.util.CoreArgCheck;

public class TranslatorProfile {


	private String name;
	
	private String type;
	
	private Properties properties;
	
	public TranslatorProfile() {
		super();
		this.properties = new Properties();
	}

	public TranslatorProfile(String name, String type, Properties properties) {
		super();
		CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(type, "type"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(properties, "properties"); //$NON-NLS-1$
		
		this.name = name;
		this.type = type;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public void addProperty(String key, String value) {
		this.properties.put(key, value);
	}
	
	public void addProperties(Properties properties) {
		this.properties.putAll(properties);
	}
	
	public String getProperty(String key) {
		return (String)this.properties.get(key);
	}
}
