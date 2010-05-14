/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
