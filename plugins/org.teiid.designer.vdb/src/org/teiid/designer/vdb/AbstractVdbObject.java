/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import java.util.Map;
import java.util.Properties;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.core.designer.util.StringUtilities;

/**
 *
 */
public abstract class AbstractVdbObject implements StringConstants {

    /**
     * Name of vdb object
     */
    private String name = EMPTY_STRING;

    /**
     * Description of vdb object
     */
    private String description = EMPTY_STRING;

    /**
     * Properties of vdb object
     */
    private Properties properties = new Properties();

    /**
     * Changed flag of vdb object
     */
    private boolean changed;

    /**
     * 
     */
    public AbstractVdbObject() {
        super();
    }

    /**
     * @return properties
     */
    public Properties getProperties() {
    	return properties;
    }

    /**
     * @return the name
     */
    public String getName() {
    	return this.name;
    }

    /**
     * @param newName
     */
    public void setName(String newName) {
    	setChanged(this.name, newName);
    	this.name = newName;
    }

    /**
     * @param properties
     */
    public void setProperties(Properties properties) {
    	this.properties = properties;
    }

    /**
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
    	if( properties.get(key) != null ) {
    		String valueString = properties.getProperty(key);
    		if( StringUtilities.areDifferent(value, valueString)) {
    			this.properties.put(key,  value);
    			setChanged(true);
    		}
    	} else {
    		this.properties.put(key,  value);
    		setChanged(true);
    	}
    }

    /**
     * @param key
     * @return removed property
     */
    public String removeProperty(String key) {
        String property = this.properties.getProperty(key);
    	Object removed = this.properties.remove(key);
        setChanged(removed != null);
        return property;
    }

    /**
     * 
     * @param newDescription
     */
    public void setDescription(String newDescription) {
    	setChanged(this.description, newDescription);
    	this.description = newDescription;
    }

    /**
     * @return description
     */
    public String getDescription() {
    	return this.description;
    }

    /**
     * @return changed
     */
    public boolean isChanged() {
    	return changed;
    }

    /**
     * @param value
     */
    protected void setChanged(boolean value) {
    	changed = value;
    }

    /**
     * @param value1
     * @param value2
     */
    protected void setChanged(boolean value1, boolean value2) {
    	setChanged(value1 != value2);
    }

    /**
     * @param value1
     * @param value2
     */
    protected void setChanged(String value1, String value2) {
    	setChanged(StringUtilities.areDifferent(value1, value2));
    }

    /**
     * @param value1
     * @param value2
     */
    protected void setChanged(int value1, int value2) {
    	setChanged(value1 != value2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.properties == null) ? 0 : this.properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractVdbObject other = (AbstractVdbObject)obj;
        if (this.description == null) {
            if (other.description != null)
                return false;
        } else if (!this.description.equals(other.description))
            return false;
        if (this.name == null) {
            if (other.name != null)
                return false;
        } else if (!this.name.equals(other.name))
            return false;
        if (this.properties == null) {
            if (other.properties != null)
                return false;
        } else if (!this.properties.equals(other.properties))
            return false;
        return true;
    }

    protected void cloneVdbObject(AbstractVdbObject clone) {
        if (StringUtilities.areDifferent(name, clone.getName()))
            clone.setName(name);

        if (StringUtilities.areDifferent(description, clone.getDescription()))
            clone.setDescription(description);

        for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
            clone.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    /**
     * Clone this object
     */
    public abstract AbstractVdbObject clone();
}
