/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.config;


/** 
 * @since 5.0
 */
public class ConfigurationChangeEvent implements ConfigurationConstants {
    
    private int eventType = UNKNOWN_EVENT;
    private int changedObjectType = UNKNOWN_CHANGED;
    private String objectName;
    private String[] objectNames;
    private boolean multipleUpdate = false;
    /** 
     * 
     * @since 5.0
     */
    public ConfigurationChangeEvent(int eventType, int changedObjectType, String objectName) {
        super();
        this.eventType = eventType;
        this.changedObjectType = changedObjectType;
        this.objectName = objectName;
    }
    
    public ConfigurationChangeEvent(int eventType, int changedObjectType, String[] objectNames) {
        this.eventType = eventType;
        this.changedObjectType = changedObjectType;
        this.objectNames = objectNames;
        this.multipleUpdate = true;
    }
        
    public String getObjectName() {
        return this.objectName;
    }
    
    public String[] getObjectNames() {
        return this.objectNames;
    }
    
    public int getEventType() {
        return this.eventType;
    }
    
    public int getChangedObjectType() {
        return this.changedObjectType;
    }
    
    public boolean isConnectorTypeEvent() {
        return this.changedObjectType == CONNECTOR_TYPE_CHANGE;
    }
    
    public boolean isConnectorBindingEvent() {
        return this.changedObjectType == CONNECTOR_BINDINGS_CHANGED;
    }
    
    public boolean isChanged() {
        return this.eventType == CHANGED_EVENT;
    }

    public boolean isAdded() {
        return this.eventType == ADDED_EVENT;
    }
    
    public boolean isRemoved() {
        return this.eventType == REMOVED_EVENT;
    }
    
    public boolean isReplaced() {
        return this.eventType == REPLACED_EVENT;
    }
    
    public boolean isMultipleUpdate() {
        return this.multipleUpdate;
    }
    
    @Override
    public String toString() {
        return "ConfigurationChangeEvent" + "\n   Event Type = " + eventType +  //$NON-NLS-1$ //$NON-NLS-2$
            "\n   Object Type = " + changedObjectType +  //$NON-NLS-1$
            "\n   Name = " + objectName; //$NON-NLS-1$
    }
}
