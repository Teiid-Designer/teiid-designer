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

package com.metamatrix.modeler.internal.core.workspace;

import java.util.Map.Entry;

import com.metamatrix.modeler.core.workspace.ModelBuffer;
import com.metamatrix.modeler.internal.core.util.OverflowingLRUCache;

/**
 * An LRU cache of <code>ModelBuffers</code>.
 */
public class ModelBufferCache extends OverflowingLRUCache {
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new buffer cache of the given size.
     */
    public ModelBufferCache(int size) {
        super(size);
    }
        
    /**
     * Returns true if the buffer is successfully closed and
     * removed from the cache, otherwise false.
     *
     * <p>NOTE: this triggers an external removal of this buffer
     * by closing the buffer.
     */
    @Override
    protected boolean close(Entry entry) {
    	final Object value = entry.getValue();
        if ( value instanceof ModelBuffer ) {
            ModelBuffer buffer= (ModelBuffer) value;
            if (buffer.hasUnsavedChanges()) {
                return false;
            }
            buffer.close();
            return true;
        }
        return false;
    }
    
}
