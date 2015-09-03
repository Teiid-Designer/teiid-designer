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

package org.teiid.metadata;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;


/**
 * AbstractMetadataRecord
 */
public abstract class AbstractMetadataRecord implements Serializable {

    @Since(Version.TEIID_8_9)
    private static final Collection<AbstractMetadataRecord> EMPTY_INCOMING = Collections.emptyList();

	public interface Modifiable {
		long getLastModified();
	}
	
	public interface DataModifiable {
		long getLastDataModification();
	}
	
	private static final long serialVersionUID = 564092984812414058L;

	public final static char NAME_DELIM_CHAR = '.';
	
	private static AtomicLong UUID_SEQUENCE = new AtomicLong();
    
    private String uuid; //globally unique id
    private String name; //contextually unique name
    
    private String nameInSource;
	
	private volatile Map<String, String> properties;
	private String annotation;

	@Since(Version.TEIID_8_9)
	private transient Collection<AbstractMetadataRecord> incomingObjects;

	public static final String RELATIONAL_URI = "{http://www.teiid.org/ext/relational/2012}"; //$NON-NLS-1$
	
	public String getUUID() {
		if (uuid == null) {
			uuid = String.valueOf(UUID_SEQUENCE.getAndIncrement());
		}
		return uuid;
	}
	
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	
	public String getNameInSource() {
		return nameInSource;
	}
	
	public void setNameInSource(String nameInSource) {
		this.nameInSource = nameInSource;
	}
	
	/**
	 * Get the name in source or the name if
     * the name in source is not set.
     * @return
     */
	@Since(Version.TEIID_8_9)
    public String getSourceName() {
        if (this.nameInSource != null && this.nameInSource.length() > 0) {
            return this.nameInSource;
        }
        return getName();
    }

    /**
     * WARNING - The name returned by this method may be ambiguous and
     * is not SQL safe - it may need quoted/escaped
     */
	public String getFullName() {
        AbstractMetadataRecord parent = getParent();
        if (parent != null) {
        	String result = parent.getFullName() + NAME_DELIM_CHAR + getName();
        	return result;
        }
        return name;
	}

	public void getSQLString(StringBuilder sb) {
		AbstractMetadataRecord parent = getParent();
		if (parent != null) {
        	parent.getSQLString(sb);
        	sb.append(NAME_DELIM_CHAR);
        }
		sb.append('"').append(StringUtil.replace(name, "\"", "\"\"")).append('"'); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Get the full name as a SQL safe string
	 * @return
	 */
	public String getSQLString() {
		StringBuilder sb = new StringBuilder();
		getSQLString(sb);
		return sb.toString();
	}
	
	public AbstractMetadataRecord getParent() {
		return null;
	}
	
	public String getName() {
		return name;
	}	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCanonicalName() {
		return name.toUpperCase();
	}
	
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer(100);
        sb.append(getClass().getSimpleName());
        sb.append(" name="); //$NON-NLS-1$
        sb.append(getName());
        sb.append(", nameInSource="); //$NON-NLS-1$
        sb.append(getNameInSource());
        sb.append(", uuid="); //$NON-NLS-1$
        sb.append(getUUID());
        return sb.toString();
    }
    
    /**
     * Return the extension properties for this record - may be unmodifiable
     * if {@link #setProperties(LinkedHashMap)} or {@link #setProperty(String, String)}
     * has not been called.
     * @return
     */
    public Map<String, String> getProperties() {
    	if (properties == null) {
    		return Collections.emptyMap();
    	}
    	return properties;
	}
    
    public String getProperty(String key, boolean checkUnqualified) {
    	String value = getProperties().get(key);
    	if (value != null || !checkUnqualified) {
    		return value;
    	}
    	int index = key.indexOf('}');
    	if (index > 0 && index < key.length() &&  key.charAt(0) == '{') {
    		key = key.substring(index + 1, key.length());
    	}
    	return getProperties().get(key);
    }
    
    /**
     * The preferred setter for extension properties.
     * @param key
     * @param value, if null the property will be removed
     */
    public String setProperty(String key, String value) {
    	if (this.properties == null) {
    		synchronized (this) {
    			if (this.properties == null && value == null) {
    				return null;
    			}
    			this.properties = new ConcurrentSkipListMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    		}
		}
    	if (value == null) {
    		return this.properties.remove(key);
    	}
    	return this.properties.put(key, value);
    }
    
    public synchronized void setProperties(Map<String, String> properties) {
    	if (this.properties == null) {
    		this.properties = new ConcurrentSkipListMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    	} else {
    		this.properties.clear();
    	}
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

    public String getAnnotation() {
		return annotation;
	}
    
    public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

    /**
     * Compare two records for equality.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if(obj.getClass() != this.getClass()) {
            return false;
        }

        AbstractMetadataRecord other = (AbstractMetadataRecord)obj;
        if (getUUID() == other.getUUID()) {
            return true;
        } else if (getUUID() == null || other.getUUID() == null) {
            return false;
        } else {
            return getUUID().equals(other.getUUID());
        }
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	if (this.properties != null && !(this.properties instanceof ConcurrentSkipListMap<?, ?>)) {
    		this.properties = new ConcurrentSkipListMap<String, String>(this.properties);
    	}
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
    }

    @Since(Version.TEIID_8_9)
    public Collection<AbstractMetadataRecord> getIncomingObjects() {
        if (incomingObjects == null) {
            return EMPTY_INCOMING;
        }
        return incomingObjects;
    }

    @Since(Version.TEIID_8_9)
    public void setIncomingObjects(Collection<AbstractMetadataRecord> incomingObjects) {
        this.incomingObjects = incomingObjects;
    }

    @Since(Version.TEIID_8_9)
    public boolean isUUIDSet() {
        return this.uuid != null && this.uuid.length() > 0 && !Character.isDigit(this.uuid.charAt(0));
    }
}