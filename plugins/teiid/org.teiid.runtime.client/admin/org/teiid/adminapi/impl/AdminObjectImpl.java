/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.teiid.adminapi.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.teiid.adminapi.AdminObject;
import org.teiid.core.util.CopyOnWriteLinkedHashMap;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;

public abstract class AdminObjectImpl implements AdminObject, Serializable {

	private static final long serialVersionUID = -6381303538713462682L;

	/**
	 * This ONLY exists to ensure that the serialisation framework
	 * has access to the anonymous class AdminObjectImpl$1.
	 *
	 * IT SHOULD NEVER BE USED FOR ANYTHING ELSE!!!
	 */
	@Removed("8.0.0")
	private transient ListOverMap<PropertyMetadata> sevenProperties = new ListOverMap<PropertyMetadata>(new KeyBuilder<PropertyMetadata>() {
        private static final long serialVersionUID = 3687928367250819142L;

        @Override
        public String getKey(PropertyMetadata entry) {
            return entry.getName();
        }
    });

	private String name;

	@Since("8.0.0")
	private String serverGroup;

	@Since("8.0.0")
	private String serverName;

	@Since("8.0.0")
	private String hostName;

	private Map<String, String> properties = new CopyOnWriteLinkedHashMap<String, String>();

	protected transient Map<Class<?>, Object> attachments = new CopyOnWriteLinkedHashMap<Class<?>, Object>();

	protected boolean isListOverMap(Object obj) {
        return obj instanceof ListOverMap;
    }

    protected boolean isLinkedHashMap(Object obj) {
        return obj instanceof LinkedHashMap;
    }

    protected boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    private Map<String, String> convertProperties(ListOverMap<PropertyMetadata> overMap) {
        Map<String, String> newMap = new CopyOnWriteLinkedHashMap<String, String>();
        for (Entry<String, PropertyMetadata> entry : overMap.getMap().entrySet()) {
            PropertyMetadata propertyMetadata = entry.getValue();
            newMap.put(propertyMetadata.getName(), propertyMetadata.getValue());
        }

        return Collections.synchronizedMap(newMap);
    }

    protected <K, V> CopyOnWriteLinkedHashMap<K, V> newCopyOnWriteLinkedHashMap(Map<K, V> oldMap) {

        /* Teiid Version 8.7 */
        if (oldMap instanceof CopyOnWriteLinkedHashMap) {
            return (CopyOnWriteLinkedHashMap<K, V>) oldMap;
        }

        CopyOnWriteLinkedHashMap<K, V> newMap = new CopyOnWriteLinkedHashMap<K, V>();
        newMap.putAll(oldMap);
        return newMap;
    }

    /*
     * Helper method for serialization to deal with differences between Teiid 7 and 8
     */
    @SuppressWarnings("nls")
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        GetField serFields = ois.readFields();
        name = (String) serFields.get("name", null);

        /* Version 8+ */
        serverGroup = (String) serFields.get("serverGroup", null);
        serverName = (String) serFields.get("serverName", null);
        hostName = (String) serFields.get("hostName", null);

        Object serProps = serFields.get("properties", null);
        /* Teiid Version 8+ */
        if (serProps instanceof Map) {
            properties = newCopyOnWriteLinkedHashMap((Map<String, String>) serProps);
            return;
        }

        /* Teiid Version 7 */
        if (serProps instanceof ListOverMap) {
            ListOverMap<PropertyMetadata> overMap = (ListOverMap<PropertyMetadata>)serProps;
            properties = convertProperties(overMap);
        }
    }

	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}	
	
	public String getServerGroup() {
		return this.serverGroup;
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	public String getHostName() {
		return this.hostName;
	}
	
	public void setServerGroup(String group) {
		this.serverGroup = group;
	}
	
	public void setServerName(String name) {
		this.serverName = name;
	}
	
	public void setHostName(String name) {
		this.hostName = name;
	}	

	@Override
	public Properties getProperties() {
		Properties props = new Properties();
		props.putAll(this.properties);
		return props;
	}
	
	public void setProperties(Properties props) {
		this.properties.clear();
		if (props != null && !props.isEmpty()) {
			for (String key:props.stringPropertyNames()) {
				addProperty(key, props.getProperty(key));
			}
		}
	}	
	
	public Map<String, String> getPropertiesMap() {
		return this.properties;
	}
	
	@Override
	public String getPropertyValue(String key) {
		return this.properties.get(key);
	}

	public void addProperty(String key, String value) {
		this.properties.put(key, value);
	}
	
   /**
    * Add attachment
    *
    * @param <T> the expected type
    * @param attachment the attachment
    * @param type the type
    * @return any previous attachment
    * @throws IllegalArgumentException for a null name, attachment or type
    * @throws UnsupportedOperationException when not supported by the implementation
    */	
	public <T> T addAttchment(Class<T> type, T attachment) {
		if (type == null)
      		throw new IllegalArgumentException("Null type"); //$NON-NLS-1$
  		Object result = this.attachments.put(type, attachment);
  		return type.cast(result);
	}	
			
   /**
    * Remove attachment
    * 
    * @param <T> the expected type
    * @return the attachment or null if not present
    * @param type the type
    * @throws IllegalArgumentException for a null name or type
    */	
	public <T> T removeAttachment(Class<T> type) {
		if (type == null)
			throw new IllegalArgumentException("Null type"); //$NON-NLS-1$
		Object result = this.attachments.remove(type);
		return type.cast(result);
	}
		
   /**
    * Get attachment
    * 
    * @param <T> the expected type
    * @param type the type
    * @return the attachment or null if not present
    * @throws IllegalArgumentException for a null name or type
    */
   public <T> T getAttachment(Class<T> type) {
	   if (type == null)
		   throw new IllegalArgumentException("Null type"); //$NON-NLS-1$
	   Object result = this.attachments.get(type);
	   return type.cast(result);      
   }	
   
	   	   
}
