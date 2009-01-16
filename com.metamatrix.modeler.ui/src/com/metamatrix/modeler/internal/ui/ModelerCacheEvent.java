/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
