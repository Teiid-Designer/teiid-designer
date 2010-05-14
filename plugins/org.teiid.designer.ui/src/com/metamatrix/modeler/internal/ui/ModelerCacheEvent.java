/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui;

import java.util.Collection;


/** 
 * @since 4.2
 */
public class ModelerCacheEvent {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static final int ADD = 0;
    
    public static final int REMOVE = 1;
    
    public static final int CHANGE = 2;
    
    public static final int CLEAR = 3;
    
    public static final ModelerCacheEvent CLEAR_CACHE_EVENT = new ModelerCacheEvent(CLEAR);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Object object;
    
    private int type;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private ModelerCacheEvent(int theType) {
        this.type = theType;
    }
    
    public ModelerCacheEvent(int theType,
                             Object theObject) {
        this(theType);
        this.object = theObject;
    }
    
    public ModelerCacheEvent(int theType,
                             Collection theObjects) {
        this(theType, (Object)theObjects);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isAdd() {
        return (this.type == ADD);
    }
    
    public boolean isChange() {
        return (this.type == CHANGE);
    }
    
    public boolean isClear() {
        return ((this == CLEAR_CACHE_EVENT) || (this.type == CLEAR));
    }
    
    public boolean isDelete() {
        return (this.type == REMOVE);
    }
    
    public Object[] toArray() {
        return ((this.object instanceof Collection) ? ((Collection)this.object).toArray()
                                                    : new Object[] {this.object});
    }
}
