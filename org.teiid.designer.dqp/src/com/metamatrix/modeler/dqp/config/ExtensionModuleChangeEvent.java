/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.config;

import java.util.Collection;
import java.util.Collections;

/**
 * An event used when extension module jars are added or deleted.
 * 
 * @since 5.5.3
 */
public class ExtensionModuleChangeEvent {

    // ===========================================================================================================================
    // Enums
    // ===========================================================================================================================

    /**
     * The type of the event.
     * 
     * @since 5.5.3
     */
    public enum Type {
        ADDED, DELETED, ADDED_FROM_CAF, ADDED_FOR_UDF, DELETED_FROM_UDF
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private Collection<Object> affectedObjects;

    private final Object source;

    private final Type type;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param source
     *            the event creator
     * @param type
     *            the event type
     * @since 5.5.3
     */
    public ExtensionModuleChangeEvent(Object source,
                                      Type type) {
        this.source = source;
        this.type = type;
    }

    /**
     * @param source
     *            the event creator
     * @param type
     *            the event type
     * @param affectedObjects
     *            the objects affected by the UDF configuration change
     * @since 5.5.3
     */
    public ExtensionModuleChangeEvent(Object source,
                                      Type type,
                                      Collection<Object> affectedObjects) {
        this.source = source;
        this.type = type;
        this.affectedObjects = affectedObjects;
    }

    /**
     * @param source
     *            the event creator
     * @param type
     *            the event type
     * @param affectedObject
     *            the object affected by the UDF configuration change
     * @since 5.5.3
     */
    public ExtensionModuleChangeEvent(Object source,
                                      Type type,
                                      Object affectedObject) {
        this(source, type, Collections.singletonList(affectedObject));
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @return the affected objects (never <code>null</code> but may be empty)
     * @since 5.5.3
     */
    public Collection<Object> getAffectedObjects() {
        return (this.affectedObjects == null ? Collections.EMPTY_LIST : this.affectedObjects);
    }

    /**
     * @return the event source
     * @since 5.5.3
     */
    public Object getSource() {
        return this.source;
    }

    /**
     * @return <code>true</code> if one or more extension modules have been added
     * @since 5.5.3
     */
    public boolean modulesAdded() {
        return (this.type == Type.ADDED);
    }
    
    /**
     * @return <code>true</code> if one or more extension modules have been added from CAF import
     * @since 5.5.3
     */
    public boolean modulesAddedFromCAF() {
        return (this.type == Type.ADDED_FROM_CAF);
    }

    /**
     * @return <code>true</code> if one or more extension modules have been deleted
     * @since 5.5.3
     */
    public boolean modulesDeleted() {
        return (this.type == Type.DELETED);
    }
    
    /**
     * @return <code>true</code> if one or more extension modules have been added for the UDF
     * @since 5.5.3
     */
    public boolean udfModulesAdded() {
        return (this.type == Type.ADDED_FOR_UDF);
    }
    
    /**
     * @return <code>true</code> if one or more extension modules have been deleted from the UDF
     * @since 5.5.3
     */
    public boolean udfModulesDeleted() {
        return (this.type == Type.DELETED_FROM_UDF);
    }
}
